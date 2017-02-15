package it.polito.dp2.NFFG.sol3.client1;

import java.net.URI;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import it.polito.dp2.NFFG.FactoryConfigurationError;
import it.polito.dp2.NFFG.LinkReader;
import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.NffgVerifier;
import it.polito.dp2.NFFG.NffgVerifierException;
import it.polito.dp2.NFFG.NffgVerifierFactory;
import it.polito.dp2.NFFG.NodeReader;
import it.polito.dp2.NFFG.PolicyReader;
import it.polito.dp2.NFFG.ReachabilityPolicyReader;
import it.polito.dp2.NFFG.TraversalPolicyReader;
import it.polito.dp2.NFFG.VerificationResultReader;
import it.polito.dp2.NFFG.lab3.AlreadyLoadedException;
import it.polito.dp2.NFFG.lab3.ServiceException;
import it.polito.dp2.NFFG.lab3.UnknownNameException;
import it.polito.dp2.NFFG.sol3.service2.Policy;
import it.polito.dp2.NFFG.sol3.service2.NetworkNode;
import it.polito.dp2.NFFG.sol3.service2.Nffg;
import it.polito.dp2.NFFG.sol3.client2.MyLinkReader;
import it.polito.dp2.NFFG.sol3.service1.Node;
import it.polito.dp2.NFFG.sol3.service2.Reachability;
import it.polito.dp2.NFFG.sol3.service2.Traversal;
import it.polito.dp2.NFFG.sol3.service2.Reachability.DSTnode;
import it.polito.dp2.NFFG.sol3.service2.Reachability.SRCnode;
import it.polito.dp2.NFFG.sol3.service2.Verify;
import it.polito.dp2.NFFG.sol3.service2.FunctionalType;
import it.polito.dp2.NFFG.sol3.service2.Link;;

public class NFFGClient implements it.polito.dp2.NFFG.lab3.NFFGClient {

	private NffgVerifier monitor;
	private WebTarget target;
	
	public NFFGClient() throws UnknownNameException, ServiceException {
		Client client = ClientBuilder.newClient();
		target = client.target(getBaseURI());
		if (target == null)
			throw new ServiceException();
		try {
			monitor = NffgVerifierFactory.newInstance().newNffgVerifier();
		} catch (NffgVerifierException e) {
			e.printStackTrace();
			throw new ServiceException();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
			throw new ServiceException();
		}
		if (monitor == null)
			throw new ServiceException();
		
	}
	@Override
	public void loadNFFG(String name) throws UnknownNameException, AlreadyLoadedException, ServiceException {
		try {
		NffgReader nr = monitor.getNffg(name);
		System.out.println("check" + name);
		if (nr == null) {
			System.out.println("**************" + name + "is null!**************");
			throw new UnknownNameException();
		}
		if (getNFFG(name))
			throw new AlreadyLoadedException();
		postNFFG(nr);
		} catch(Exception e) {
			e.printStackTrace();
			throw new ServiceException();
		}
	}

	private boolean getNFFG(String name) {
		Response response = null;
		response = target.path("Nffgs").path(name).request().accept(MediaType.APPLICATION_XML).get();
		if (response.getStatus() == 200)
			return true;
		return false;
	}
	
	private Nffg postNFFG(NffgReader nr) throws ServiceException, AlreadyLoadedException {
		//getNffgs();
		//System.out.println("Get all Nffgs");
		if (nr == null)
			throw new ServiceException();
		try {
		Nffg nffg = new Nffg();
		nffg.setName(nr.getName());
		Set<NodeReader> set = nr.getNodes();
		Nffg.NetworkNodes nffgNodes = new Nffg.NetworkNodes();
		Nffg.Links links = new Nffg.Links();
		Set<LinkReader> linkSet = new HashSet<LinkReader>();
		for (NodeReader node : set) {
			NetworkNode temp = new NetworkNode();
			temp.setName(node.getName());
			// nodes.add(temp);
			FunctionalType ft = FunctionalType.valueOf(node.getFuncType().value());
			temp.setFunctionalType(ft);
			// links
			for (LinkReader lr : node.getLinks())
				linkSet.add(lr);
			nffgNodes.getNode().add(temp);
			temp = null; //?
		}
		for (LinkReader lr : linkSet) { 
			Link link = new Link();
			link.setName(lr.getName());
			link.setSourceNode(lr.getSourceNode().getName());
			link.setDestinationNode(lr.getDestinationNode().getName());
			links.getEachLink().add(link);
			
		}
		nffg.setLinks(links);
		System.out.println("To post nffg " + nr.getName());
		nffg.setNetworkNodes(nffgNodes);
		Nffg nodeResponse = target.path("Nffgs")
									.request(MediaType.APPLICATION_XML)
									.post(Entity.entity(nffg, MediaType.APPLICATION_XML), Nffg.class);
		return nodeResponse; } catch (BadRequestException e) {
			e.printStackTrace();
			System.out.println("Detailed message: " + e.getMessage());
			throw new ServiceException();
		} catch (ForbiddenException e) {
			e.printStackTrace();
			throw new AlreadyLoadedException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException();
		}
	}
	
	private static URI getBaseURI() throws UnknownNameException {
		String s = System.getProperty("it.polito.dp2.NFFG.lab3.URL");
		if (s == null)
			throw new UnknownNameException();
		return UriBuilder.fromUri(s).build();
	}

	@Override
	public void loadAll() throws AlreadyLoadedException, ServiceException {
		Set<NffgReader> nffgs = monitor.getNffgs();
		for (NffgReader nr : nffgs) {
			postNFFG(nr);
		}
		Set<PolicyReader> policies = monitor.getPolicies();
		for (PolicyReader pr : policies) {
			postPolicy(pr);
		}
	}
	
	private Policy postPolicyBasic(Policy ep, String name) throws ServiceException {
		try {
		Policy policyResponse = target.path("Nffgs").path(name).path("nffgPolicies")
				.request(MediaType.APPLICATION_XML)
				.post(Entity.entity(ep, MediaType.APPLICATION_XML), Policy.class);
		return policyResponse;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException();
		}
	}
	private Policy postPolicy(PolicyReader pr) throws ServiceException {
		// 1st get the corresponding nffg id
		if (pr.getNffg() == null || pr == null)
			throw new ServiceException();
		String nffgName = pr.getNffg().getName();
		// String id = getNffgId(nffgName);
		// 2nd post pr
		Policy ep = new Policy();
		if (pr.isPositive())
			ep.setIsPositive("True");
		else
			ep.setIsPositive("False");
		if (pr instanceof TraversalPolicyReader) {
			TraversalPolicyReader tp = (TraversalPolicyReader)pr;
			Traversal tr = new Traversal();
			try {
			Traversal.SRCnode ts = new Traversal.SRCnode();
			ts.setSourceNode(tp.getSourceNode().getName());
			tr.setSRCnode(ts);
			Traversal.DSTnode td = new Traversal.DSTnode();
			td.setDestinationNode(tp.getDestinationNode().getName());
			tr.setDSTnode(td);
			Set<it.polito.dp2.NFFG.FunctionalType> ft = tp.getTraversedFuctionalTypes();
			Set<FunctionalType> ft1 = new HashSet<FunctionalType>();
			for (it.polito.dp2.NFFG.FunctionalType f : ft) {
				FunctionalType f1 = FunctionalType.valueOf(f.value());
				ft1.add(f1);
			}
			tr.getTraversedFunc().addAll(ft1);
			ep.setTraversal(tr);
			} catch (Exception e) {
				throw new ServiceException();
			}
		} else {
			if (pr instanceof ReachabilityPolicyReader) {
			ReachabilityPolicyReader rp = (ReachabilityPolicyReader)pr;
			Reachability r = new Reachability();
			try {
			Reachability.SRCnode rs = new Reachability.SRCnode();
			rs.setSourceNode(rp.getSourceNode().getName());
			r.setSRCnode(rs);
			Reachability.DSTnode rd = new Reachability.DSTnode();
			rd.setDestinationNode(rp.getDestinationNode().getName());
			r.setDSTnode(rd);
			ep.setReachability(r);
			} catch (Exception e) {
				throw new ServiceException();
			}
			}
			else {
				throw new ServiceException();
			}
		}
		ep.setName(pr.getName());
		ep.setNffgName(pr.getNffg().getName());
		VerificationResultReader verify = pr.getResult();
		
		/* check whether there is verification */
		if (verify != null) {
			System.out.println("Test policy " + ep.getName() + " " + verify.getVerificationResult());
		Verify ve = new Verify();
		if (verify.getVerificationResultMsg() != null)
			ve.setMessage(verify.getVerificationResultMsg());
		if (verify.getVerificationResult() != null) {
			if (verify.getVerificationResult())
				ve.setResult("true");
			else 
				ve.setResult("false"); 
		}
		if ( verify.getVerificationTime() != null ) {
	       Calendar dateTime = verify.getVerificationTime(); 
	       GregorianCalendar c = new GregorianCalendar();
	       c.setTime(dateTime.getTime());
	       XMLGregorianCalendar time;
	       try {
	    	   time = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
	       } catch (DatatypeConfigurationException e) {
	    	   throw new ServiceException();
	       }
	       ve.setVerifyTime(time);
		}
		ep.setVerify(ve);
		System.out.println("Test policy " + ep.getName() + " " + ep.getVerify().getResult());
		} else {
			System.out.println("Policy " + ep.getName() + " has no verification");
		}
		Policy policyResponse = postPolicyBasic(ep, nffgName);
		return  policyResponse;
	}
	
	@Override
	public void loadReachabilityPolicy(String name, String nffgName, boolean isPositive, String srcNodeName,
			String dstNodeName) throws UnknownNameException, ServiceException {
		if (nffgName == null || srcNodeName == null || dstNodeName == null)
			throw new UnknownNameException();
		Policy ep = new Policy();
		ep.setName(name);
		if (!getNFFG(nffgName))
			throw new UnknownNameException();
		ep.setNffgName(nffgName);
		if (isPositive)
			ep.setIsPositive("True");
		else
			ep.setIsPositive("False");
		NodeReader src = monitor.getNffg(nffgName).getNode(srcNodeName);
		NodeReader dst = monitor.getNffg(nffgName).getNode(dstNodeName);
		if (src == null || dst == null)
			throw new UnknownNameException();
		Reachability reach = new Reachability();
		DSTnode dstNode = new DSTnode();
		dstNode.setDestinationNode(dstNodeName);
		SRCnode srcNode = new SRCnode();
		srcNode.setSourceNode(srcNodeName);
		reach.setDSTnode(dstNode);
		reach.setSRCnode(srcNode);
		ep.setReachability(reach);
		// post
		// String id = getNffgId(nffgName);
		postPolicyBasic(ep, nffgName);
	}

	@Override
	public void unloadReachabilityPolicy(String name) throws UnknownNameException, ServiceException {
		try {
		System.out.println("Start unloading policy " + name);
		target.path("Policies").path(name)
									.request()
									.delete();
		} catch (NotFoundException e) {
			System.out.println(name + "doesn't exist!");
			throw new UnknownNameException();
		} catch (InternalServerErrorException e) {
			throw new ServiceException();
		}
	}

	@Override
	public boolean testReachabilityPolicy(String name) throws UnknownNameException, ServiceException {
		try {
		System.out.println("Start testing reachability of " + name);		
		Verify verify = target.path("Policies")
				.request(MediaType.APPLICATION_XML)
				.post(Entity.entity(name, MediaType.APPLICATION_XML), Verify.class);
		System.out.println("Test policy " + name + " result is " + verify.getResult());
		if (verify.getResult().equals("true"))
			return true;
		return false; 
		} catch(NotFoundException e) {
			System.out.println("I found NotFoundException");
			throw new UnknownNameException();
		}  catch (Exception e) {
			e.printStackTrace();
			System.out.println("I found exception here!");
			throw new ServiceException();
		}

	}
}
