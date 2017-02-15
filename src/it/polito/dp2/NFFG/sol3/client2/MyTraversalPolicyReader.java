package it.polito.dp2.NFFG.sol3.client2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.polito.dp2.NFFG.FunctionalType;
import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.NodeReader;
import it.polito.dp2.NFFG.TraversalPolicyReader;
import it.polito.dp2.NFFG.VerificationResultReader;
import it.polito.dp2.NFFG.sol3.service2.Policy;
import it.polito.dp2.NFFG.sol3.service2.NetworkNode;
import it.polito.dp2.NFFG.sol3.service2.Nffg;
import it.polito.dp2.NFFG.sol3.service2.Nffgs;
import it.polito.dp2.NFFG.sol3.service2.Traversal;

public class MyTraversalPolicyReader implements TraversalPolicyReader {

	private Traversal traversal;
	private Nffgs nffgs;
	private Policy policy;
	
	public MyTraversalPolicyReader(Traversal traversal, Nffgs nffgs, Policy policy) {
		this.nffgs = nffgs;
		this.policy = policy;
		this.traversal = traversal;
	}
	@Override
	public NodeReader getDestinationNode() {
		if (nffgs == null) return null;
		String nffgName = policy.getNffgName();
		Nffg temp = null;
		for (Nffg nffg : nffgs.getNffg())  {
			if (nffg.getName().equals(nffgName)) {
				temp = nffg;
				break;
			}
		}
		if (temp == null) return null;
		try {
		List<NetworkNode> nodes = temp.getNetworkNodes().getNode();
		NodeReader nr = null;
		String dstName = traversal.getDSTnode().getDestinationNode();
		for (NetworkNode node : nodes) {
			if (node.getName().equals(dstName)) {
				nr = new MyNodeReader(node, temp);
				break;
			}
		}
		return nr;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public NodeReader getSourceNode() {
		if (nffgs == null) return null;
		String nffgName = policy.getNffgName();
		Nffg temp = null;
		for (Nffg nffg : nffgs.getNffg())  {
			if (nffg.getName().equals(nffgName)) {
				temp = nffg;
				break;
			}
		}
		if (temp == null) return null;
		try {
		List<NetworkNode> nodes = temp.getNetworkNodes().getNode();
		NodeReader nr = null;
		String dstName = traversal.getSRCnode().getSourceNode();
		for (NetworkNode node : nodes) {
			if (node.getName().equals(dstName)) {
				nr = new MyNodeReader(node, temp);
				break;
			}
		}
		return nr;
		} catch (Exception e) {
			return null;
		}
	}


	@Override
	public NffgReader getNffg() {
		if (policy == null || nffgs == null) return null;
		String nffgName = policy.getNffgName();
		for (Nffg nffg : nffgs.getNffg())  {
			if (nffg.getName().equals(nffgName))
				return new MyNffgReader(nffg);
		}
		return null;
	}

	@Override
	public VerificationResultReader getResult() {
		if (policy == null) return null;
		String nffgName = policy.getNffgName();
		Nffg temp = null;
		for (Nffg nffg : nffgs.getNffg())  {
			if (nffg.getName().equals(nffgName)) {
				temp = nffg;
				break;
			}
		}
		if (policy.getVerify() != null && temp != null)
			return new MyVerificationResultReader(policy, temp);
		return null;
	}

	@Override
	public Boolean isPositive() {
		if (policy == null) return null;
		if (policy.getIsPositive().equals("True"))
			return true;
		return false;
	}

	@Override
	public String getName() {
		if (policy == null) return null;
		return policy.getName();
	}

	@Override
	public Set<FunctionalType> getTraversedFuctionalTypes() {
		Set<FunctionalType> set = new HashSet<FunctionalType>();
		if (traversal == null)
			return set;
		for (it.polito.dp2.NFFG.sol3.service2.FunctionalType f : traversal.getTraversedFunc())
			set.add(FunctionalType.valueOf(f.value()));
		return set;
	}

}
