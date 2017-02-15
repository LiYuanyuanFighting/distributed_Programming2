package it.polito.dp2.NFFG.sol3.client2;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.PolicyReader;

public class MyNffgVerifier implements it.polito.dp2.NFFG.NffgVerifier {

	private Set<NffgReader> nffgs;
	private Set<PolicyReader> policies;
	
	public MyNffgVerifier(Set<NffgReader> nffgs, Set<PolicyReader> policies) {
		this.nffgs = nffgs;
		this.policies = policies;
	}
	@Override
	public NffgReader getNffg(String arg0) {
		if (nffgs.isEmpty())
			return null;
		for (NffgReader nffg : nffgs) {
			if (nffg.getName().equals(arg0))
				return nffg;
		}
		return null;
	}

	@Override
	public Set<NffgReader> getNffgs() {
		return nffgs;
	}

	@Override
	public Set<PolicyReader> getPolicies() {
		return policies;
	}

	@Override
	public Set<PolicyReader> getPolicies(String arg0) {
		Set<PolicyReader> policiesR = new HashSet<PolicyReader>();
		for (PolicyReader pr : policies) {
			if (pr.getNffg().getName().equals(arg0)) {
				System.out.println("Check " + pr.getNffg().getName() + " has policy " + pr.getName());
				policiesR.add(pr);
			}
		}
		return policiesR;
	}

	@Override
	public Set<PolicyReader> getPolicies(Calendar arg0) {
		Set<PolicyReader> set = new HashSet<PolicyReader>();
		for (PolicyReader pr : policies) {
			if (pr.getResult().getVerificationTime().equals(arg0))
				set.add(pr);
		}
		return set;
	}

}
