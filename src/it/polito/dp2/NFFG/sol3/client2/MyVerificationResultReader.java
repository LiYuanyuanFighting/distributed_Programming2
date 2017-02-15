package it.polito.dp2.NFFG.sol3.client2;

import java.util.Calendar;

import javax.xml.datatype.XMLGregorianCalendar;

import it.polito.dp2.NFFG.PolicyReader;
import it.polito.dp2.NFFG.VerificationResultReader;
import it.polito.dp2.NFFG.sol3.service2.Policy;
import it.polito.dp2.NFFG.sol3.service2.Nffg;

public class MyVerificationResultReader implements VerificationResultReader {

	private Policy policy;
	private Nffg nffg;
	public MyVerificationResultReader (Policy policy, Nffg nffg) {
		this.nffg = nffg;
		this.policy = policy;
	}
	@Override
	public PolicyReader getPolicy() {
		if (policy == null || nffg == null) return null;
		System.out.println("Check policy name " + policy.getName());
		return new MyPolicyReader(policy, nffg);
	}

	@Override
	public Boolean getVerificationResult() {
		if (policy == null || nffg == null || policy.getVerify() == null || policy.getVerify().getResult() == null) 
			return null;
		System.out.println("Check policy " + policy.getName() + " verification result " + policy.getVerify().getResult());
		if (policy.getVerify().getResult().equals("true"))
			return true;
		else
			return false;
	}

	@Override
	public String getVerificationResultMsg() {
		if (policy == null || policy.getVerify() == null ) return null;
		System.out.println("Check result message " + policy.getVerify().getMessage());
		return policy.getVerify().getMessage();
	}

	@Override
	public Calendar getVerificationTime() {
		if (policy == null) return null;
		if (policy.getVerify() == null)
			return null;
		System.out.println("Check verify time " + policy.getVerify().getVerifyTime());
		XMLGregorianCalendar c = policy.getVerify().getVerifyTime();
		if (c == null)
			return null;
		return c.toGregorianCalendar();
	}

}
