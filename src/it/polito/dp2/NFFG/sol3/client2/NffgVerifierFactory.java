package it.polito.dp2.NFFG.sol3.client2;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.NffgVerifier;
import it.polito.dp2.NFFG.NffgVerifierException;
import it.polito.dp2.NFFG.PolicyReader;
import it.polito.dp2.NFFG.sol3.service2.Policy;
import it.polito.dp2.NFFG.sol3.service2.Reachability;
import it.polito.dp2.NFFG.sol3.service2.Traversal;
import it.polito.dp2.NFFG.sol3.service2.Nffg;
import it.polito.dp2.NFFG.sol3.service2.Nffgs;

public class NffgVerifierFactory extends it.polito.dp2.NFFG.NffgVerifierFactory {

	private WebTarget target;
	
	@Override
	public NffgVerifier newNffgVerifier() throws NffgVerifierException {
		Set<NffgReader> nffgReaders = new HashSet<NffgReader>();
		Set<PolicyReader> policyReaders = new HashSet<PolicyReader>();
		MyNffgVerifier nv;
		try {
			Client client = ClientBuilder.newClient();
			target = client.target(getBaseURI());
			
			List<Nffg> nffgs = getNffg();
			List<Policy> policies = getPolicies();
			Map<String, Nffg> nffgMap = new HashMap<String, Nffg>();
			
			for (Nffg nffg : nffgs) {
				System.out.println("check nffg name:" + nffg.getName());
				MyNffgReader nr = new MyNffgReader(nffg);
				nffgMap.put(nffg.getName(), nffg);
				/*for (Policy policy : nffg.getPolicy()) {
					System.out.println("Check " + nr.getName() + "add policy " + policy.getName());
					policyReaders.add(new MyPolicyReader(policy, nffg));
				}*/
				nffgReaders.add(nr);
			}
			System.out.println("nffg size is " + nffgReaders.size());
			Nffgs temp1 = new Nffgs();
			temp1.getNffg().addAll(nffgs);
			
			if (!policies.isEmpty()) {
				for (Policy policy : policies) {
					if (policy.getReachability() != null) {
						Reachability r= policy.getReachability();
						policyReaders.add(new MyReachabilityPolicyReader(r, temp1, policy));
					} else {
						if (policy.getTraversal() != null) {
							Traversal t = policy.getTraversal();
							policyReaders.add(new MyTraversalPolicyReader(t, temp1, policy));
						} else {
								throw new NffgVerifierException();
						}
					}
			}
			}
			System.out.println("policy size is " + policyReaders.size());
			nv = new MyNffgVerifier(nffgReaders, policyReaders);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NffgVerifierException();
		}
		return nv;
	}

	private static URI getBaseURI() {
		String s = System.getProperty("it.polito.dp2.NFFG.lab3.URL");
		return UriBuilder.fromUri(s).build();
	}
	
	private List<Nffg> getNffg() {
		List<Nffg> nffgs = target.path("Nffgs")
				.request().accept(MediaType.APPLICATION_XML)
				.get(new GenericType<List<Nffg>>() {});
		List<Nffg> temp = new LinkedList<Nffg>();
		if (nffgs != null) {
		for (Nffg nffg : nffgs) {
			if (nffg != null) {
				temp.add(nffg);
			}
		} }
		return temp;
	}
	
	private List<Policy> getPolicies() {
		List<Policy> policies = new LinkedList<Policy>();
		policies =	target.path("Policies")
				.request().accept(MediaType.APPLICATION_XML)
				.get(new GenericType<List<Policy>>() {});
		List<Policy> temp = new LinkedList<Policy>();
		if (policies != null) {
		for (Policy policy:policies) {
		  if (policy != null) {
			  temp.add(policy);
		  }
		}
		}
		System.out.println("policy size is !!  " + temp.size());
		return temp;
	}
}
