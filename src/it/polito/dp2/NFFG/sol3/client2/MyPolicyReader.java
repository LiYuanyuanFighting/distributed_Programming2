package it.polito.dp2.NFFG.sol3.client2;

import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.PolicyReader;
import it.polito.dp2.NFFG.VerificationResultReader;
import it.polito.dp2.NFFG.sol3.service2.Policy;
import it.polito.dp2.NFFG.sol3.service2.Nffg;

public class MyPolicyReader implements PolicyReader {

	private Nffg nffg;
	private Policy Policy;
	public MyPolicyReader(Policy Policy, Nffg nffg) {
		this.Policy = Policy;
		this.nffg = nffg;
	}
	@Override
	public String getName() {
		if (Policy == null) return null;
		return Policy.getName();
	}

	@Override
	public NffgReader getNffg() {
		if (nffg == null) return null;
		return new MyNffgReader(nffg);
	}

	@Override
	public VerificationResultReader getResult() {
		if (Policy == null || nffg == null) return null;
		if (Policy.getVerify() != null)
			return new MyVerificationResultReader(Policy, nffg);
		return null;
	}

	@Override
	public Boolean isPositive() {
		if (Policy == null) return null;
		if (Policy.getIsPositive().equals("True"))
			return true;
		return false;
	}

}
