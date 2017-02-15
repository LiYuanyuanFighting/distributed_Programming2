package it.polito.dp2.NFFG.sol3.client2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.polito.dp2.NFFG.FunctionalType;
import it.polito.dp2.NFFG.LinkReader;
import it.polito.dp2.NFFG.sol3.service2.Link;
import it.polito.dp2.NFFG.sol3.service2.NetworkNode;
import it.polito.dp2.NFFG.sol3.service2.Nffg;

public class MyNodeReader implements it.polito.dp2.NFFG.NodeReader {

	private Nffg nffg;
	private NetworkNode node;
	public MyNodeReader(NetworkNode node, Nffg nffg) {
		this.node = node;
		this.nffg = nffg;
	}
	@Override
	public String getName() {
		if (node == null)
			return null;
		return node.getName();
	}

	@Override
	public FunctionalType getFuncType() {
		if (node == null)
			return null;
		if (node.getFunctionalType() == null)
			return null;
		// FunctionalType ft = FunctionalType.valueOf(node.getFunctionalType().value());
		return FunctionalType.valueOf(node.getFunctionalType().value());
	}

	@Override
	public Set<LinkReader> getLinks() {
		Set<LinkReader> set = new HashSet<LinkReader>();
		if (nffg.getLinks() == null)
			return set;
		List<Link> links = nffg.getLinks().getEachLink();
		for (Link link : links) {
			if (link.getSourceNode().equals(node.getName()))
				set.add(new MyLinkReader(link, nffg));
		}
		return set;
	}

}
