package it.polito.dp2.NFFG.sol3.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import it.polito.dp2.NFFG.sol3.service1.Labels;
import it.polito.dp2.NFFG.sol3.service1.Node;
import it.polito.dp2.NFFG.sol3.service1.Path;
import it.polito.dp2.NFFG.sol3.service1.Property;
import it.polito.dp2.NFFG.sol3.service1.Relationship;
import it.polito.dp2.NFFG.sol3.service2.*;

public class NffgService {

	private WebTarget target;
	Map <String, Policy> map = PolicyDB.getMap();
	Map <String, Nffg> nffgMap = nffgDB.getMap();
	Map <String, Map<String, NetworkNode>> nffgNodes = nffgDB.getNffgNodes();
	Map <String, Verify> verifyMap = VerifyDB.getMap();
	private static String cache = null;
	
	public NffgService() {
		Client client = ClientBuilder.newClient();
		target = client.target(getBaseURI());
	}
	private static URI getBaseURI() {
		System.setProperty("it.polito.dp2.NFFG.lab3.NEO4JURL", "http://localhost:8080/Neo4JXML/rest");
		String s = System.getProperty("it.polito.dp2.NFFG.lab3.NEO4JURL");
		return UriBuilder.fromUri(s).build();
	}
	// get a list of all the nffg objects
	public List<Nffg> getNffg() {
		if (nffgMap.isEmpty())
			return null;
		List<Nffg> nffgs = nffgMap.values().stream().collect(Collectors.toList());
		return nffgs;
	}
	
	public List<Node> getNffgNode() {
		// get from Neo4j
		List<Node> nodes = target.path("/resource/nodes")
						.request().accept(MediaType.APPLICATION_XML)
						.get(new GenericType<List<Node>>() {});
		List<Node> nffgs = nodes.stream()
				.filter(p->((it.polito.dp2.NFFG.sol3.service1.Node) p).getLabels().getValue().get(0).equals("NFFG")).collect(Collectors.toList());
		return nffgs;
	}
	public Nffg getSingleNode(String name) {
		return nffgMap.get(name);
	}
	public List<Policy> getPolicies() {
		if (map.isEmpty())
			return null;
		List<Policy> policies = map.values().stream().collect(Collectors.toList());
		return policies;
		// return new ArrayList<Policy>(map.values());
	}
	public Policy getSinglePolicy(String name) {
		return map.get(name);
	}
	public Policy getNffgSinglePolicy(String nffgName, String policyName) {
	    if (nffgMap.containsKey(nffgName)) {
	    	Policy policy = map.get(policyName);
	    	if (policy == null)
	    		return null;
	    	if (!policy.getNffgName().equals(nffgName))
	    		return null;
	    	return policy;
	    }
		return null;
	}
	public List<Policy> getNffgPolicy(String name) {
		if (nffgMap.containsKey(name)) {
			List<Policy> policies = map.values().stream().filter(p->p.getNffgName().equals(name)).collect(Collectors.toList());
			return policies;
		}
		return null;
	} 
	
	public Nffg createNffg(Nffg nffg) { // if use one attribute, will have problem when 2 clients try to post both and one failed
		if (nffg == null) return null;
		// put inside database
		try {
		if (nffgMap.containsKey(nffg.getName())) { //synchronized 
			Nffg temp = new Nffg();
			temp.setName("repeated");
			return temp;
		}
	    // return nffg;
		Node nffgNode = new Node();
		String nffgId = null;
		Map<String, NetworkNode> nodeMap = new HashMap<String, NetworkNode>();
		Property temp = new Property();
		temp.setName(nffg.getName());
		temp.setValue(nffg.getName());
		nffgNode.getProperty().add(temp);
		Labels labelNffg = new Labels();
		labelNffg.getValue().add("NFFG");
		nffgNode.setLabels(labelNffg);
		Node nffgResponse = target.path("resource")
				.path("node")
				.request(MediaType.APPLICATION_XML)
				.post(Entity.entity(nffgNode, MediaType.APPLICATION_XML), Node.class);
		nffgId = nffgResponse.getId();
		nffg.setId(nffgId);
		// set time
		Calendar dateTime = Calendar.getInstance(); 
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(dateTime.getTime());
		XMLGregorianCalendar time = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		nffg.setLastUpdateTime(time);
		// Create belonging relationship
		// 1st find and post all the nodes and record id
		if (nffg.getNetworkNodes() != null) {
	   List<NetworkNode> nodes = nffg.getNetworkNodes().getNode();
		List<String> ids = new LinkedList<String>();
		for (int i = 0; i < nodes.size(); i++) {
		// for (NetworkNode node : nodes) {
			NetworkNode node = nodes.get(i);
			Node n = new Node();
			Property property = new Property();
			property.setName("name");
			property.setValue(node.getName());
			List<Property> properties = new ArrayList<Property>();
			properties.add(property);
			n.getProperty().addAll(properties);
			Node responseNode = target.path("resource")
								.path("node")
								.request(MediaType.APPLICATION_XML)
								.post(Entity.entity(n, MediaType.APPLICATION_XML),Node.class);
			// update the node in nodes, after last one, update the nffg in the map
			// use the order of list, so change the code of for part
			nodes.get(i).setId(responseNode.getId());
			node.setId(responseNode.getId());
			nodeMap.put(node.getName(), node);
			ids.add(responseNode.getId());
		}
		nffgNodes.put(nffg.getName(), nodeMap);
		// 2nd post relationships
		for (String id : ids) {
			Relationship relationship = new Relationship();
			relationship.setSrcNode(nffgId);
			relationship.setDstNode(id);
			relationship.setType("belongs");
			 target.path("resource").path("node").path(nffgId).path("relationship")
									.request(MediaType.APPLICATION_XML)
									.post(Entity.entity(relationship, MediaType.APPLICATION_XML),Relationship.class); 
		} 
		// post link relationship
		if (nffg.getLinks() != null) {
		List<Link> links = nffg.getLinks().getEachLink();
		for (Link link : links) {
			String src = link.getSourceNode();
			String dst = link.getDestinationNode();
			String srcID = null;
			String dstID = null;
			//if (nodeMap.containsKey(src) && nodeMap.containsKey(dst)) {	
			 srcID = nodeMap.get(src).getId();
			 dstID = nodeMap.get(dst).getId();
			//} else {
				//throw new BadRequestException();
			//}
			Relationship relaLink = new Relationship();
			relaLink.setSrcNode(srcID);
			relaLink.setDstNode(dstID);
			relaLink.setType("Link");
			target.path("resource").path("node").path(srcID).path("relationship")
			.request(MediaType.APPLICATION_XML)
			.post(Entity.entity(relaLink, MediaType.APPLICATION_XML),Relationship.class); 
		}
		}
		}
		
		nffgMap.put(nffg.getName(), nffg);
		return nffg;
		//} catch (NullPointerException e) {
			//return null;
		} catch (Exception e) {
			// remove nffg
			e.printStackTrace();
			throw new javax.ws.rs.InternalServerErrorException();
			// return null;
		} 
	}
	public Policy createPolicy(String name, Policy ep) {
		if (name == null || ep == null) return null;
		if (!ep.getNffgName().equals(name))
			throw new ForbiddenException("nffg name is not matched!");
		Nffg nffg = nffgMap.get(name);
		if (nffg == null)
			return null;
		// check the existence of source node and destination node
		String srcName = null;
		String dstName = null;
		// if the type of the policy is traversal 
		if (ep.getTraversal() != null) {
			Traversal traversal = ep.getTraversal();
			srcName = traversal.getSRCnode().getSourceNode();
			dstName = traversal.getDSTnode().getDestinationNode();
		} else { 
			Reachability reachability = ep.getReachability();
			srcName = reachability.getSRCnode().getSourceNode();
			dstName = reachability.getDSTnode().getDestinationNode();
		}
		Map<String, NetworkNode> nodes = nffgNodes.get(nffg.getName());
		if ( !nodes.containsKey(srcName) || !nodes.containsKey(dstName) )
			throw new BadRequestException();
		map.put(ep.getName(), ep);
		// forget to put verify in verifyDB
		if ( getCacheStatus() != null ) {
		if (ep.getVerify() != null && getCacheStatus().equals("enabled"))
			verifyMap.put(ep.getName(), ep.getVerify());
		}
		return ep;
	}
	/*public String getCacheStatus() {
		Policy ep = map.get(name);
		if (ep == null)
			return null;
		return ep.getEnableCache();
	}*/
	public Verify getVerify(String name) throws DatatypeConfigurationException {
		System.out.println("Check verify " + name);
		if (name == null) {
			System.out.println("*************name is null**************");
			return null;
		}
		Policy ep = map.get(name);
		if (ep == null){
			System.out.println("*************policy is null**************");
			return null;
		}
		
		
		// To get the nffg
		String nffgName = ep.getNffgName();
		Nffg nffg = nffgMap.get(nffgName);
		if (nffg == null) {
			System.out.println("*************nffg is null**************");
			return null;
		}
		
		System.out.println("*************Check status of " + name +  "**************");
		if (getCacheStatus() != null) {
		if (getCacheStatus().equals("enabled") && verifyMap.containsKey(name)) {
			System.out.println("*************Enabled " + name +  "**************");
			Verify v = verifyMap.get(name);
			if (v.getVerifyTime().compare(nffg.getLastUpdateTime()) == DatatypeConstants.GREATER)
				return v;
		}
		}
		
		String isPositive = ep.getIsPositive();
		
		String srcName = null;
		String dstName = null;
		// if the type of the policy is traversal 
		if (ep.getTraversal() != null) {
			Traversal traversal = ep.getTraversal();
			srcName = traversal.getSRCnode().getSourceNode();
			dstName = traversal.getDSTnode().getDestinationNode();
		} else { 
			Reachability reachability = ep.getReachability();
			srcName = reachability.getSRCnode().getSourceNode();
			dstName = reachability.getDSTnode().getDestinationNode();
		}
		
		// get the corresponding 2 nodes from nffg
		// List<NetworkNode> nodes = nffg.getNetworkNodes().getNode();
		String sId = null, dId = null;
		Map<String, NetworkNode> nodes = nffgNodes.get(nffg.getName());
		sId = nodes.get(srcName).getId();
		dId = nodes.get(dstName).getId();
					
		// 2nd verify
		List<Path> paths;	
		try {
			paths = target.path("resource").path("node").path(sId).queryParam("dst", dId)
										.request().accept(MediaType.APPLICATION_XML)
										.get(new GenericType<List<Path>>() {});
		} catch (NotFoundException e) { // neo4j service can be stopped
			System.out.println("NotFoundException here!!!!!!!!!");
			throw new InternalServerErrorException();
		}
		
			System.out.println("Check verify " + name);
					boolean tempR = true;
					String result = null;
					String message = null;
					if (paths.isEmpty()) {
						tempR = false;
					}
					if ((isPositive.equals("True") && tempR) || (isPositive.equals("False") && !tempR)) {
						result = "true";
						message = "Policy verificarion result true";
					}
					else {
						result = "false";
						message = "Policy verificarion result not true";
					}
					// create Verify object if no existence before otw overrides
					Verify verify = null;
					if (ep.getVerify() == null) {
					verify = new Verify();
					} else {
						verify = ep.getVerify();
					}
					verify.setMessage(message);
					verify.setResult(result);
					Calendar dateTime = Calendar.getInstance(); 
					GregorianCalendar c = new GregorianCalendar();
					c.setTime(dateTime.getTime());
					XMLGregorianCalendar time = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
					verify.setVerifyTime(time);
					ep.setVerify(verify);
					map.put(name, ep);
					
					if (getCacheStatus() != null) {
					if (getCacheStatus().equals("enabled"))
						verifyMap.put(name, verify);
					}
					System.out.println("Check finish verify " + name);
		return verify;
		
	}
	public Policy modifyPolicy(Policy ep) {
		Policy policy = createPolicy(ep.getName(), ep);
		return policy;
	}
	public Policy remove(String policyName) {
		if (map.get(policyName) != null) {
			Policy ep = map.remove(policyName);
			verifyMap.clear();
			return ep;
		}
		return null;
	}
	public void removeAllPolicies() {
		if (!map.isEmpty()) {
			map.clear();
			verifyMap.clear(); // clear Verify map also
		}
	}
	public Object getSynchObject() {
		return map;
	}
	public String getCacheStatus() { // it is atomic variable, so no need of using synchronized
		return cache;
	}
	public String setCacheStatus(EnabledCache ec) {
		if (ec == null)
			return null;
		if (ec.getCache().equals("enabled")) {
			cache = "enabled";
			System.out.println("set cache into enable");
			return "enabled";
		}
		if (ec.getCache().equals("disabled")) {
			cache = "disabled";
			System.out.println("set cache into disable");
			return "disabled";
		}
		return null;
	}
}
