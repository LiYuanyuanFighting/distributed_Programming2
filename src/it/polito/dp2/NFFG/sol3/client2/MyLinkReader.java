package it.polito.dp2.NFFG.sol3.client2;

import java.util.List;

import it.polito.dp2.NFFG.NodeReader;
import it.polito.dp2.NFFG.sol3.service2.Link;
import it.polito.dp2.NFFG.sol3.service2.NetworkNode;
import it.polito.dp2.NFFG.sol3.service2.Nffg;

public class MyLinkReader implements it.polito.dp2.NFFG.LinkReader {

	private Link link;
	private Nffg nffg;
	
	public MyLinkReader(Link link, Nffg nffg) {
		this.link = link;
		this.nffg = nffg;
	}
	@Override
	public String getName() {
		if (link == null)
			return null;
		return link.getName();
	}

	@Override
	public NodeReader getDestinationNode() {
		if (link == null || nffg == null) 
			return null;
		String dstName = link.getDestinationNode();
		List<NetworkNode> nodes = nffg.getNetworkNodes().getNode();
		NetworkNode temp = null;
		for (NetworkNode node : nodes) {
			if (node.getName().equals(dstName)) {
				temp = node;
				break;
			}
		}
		if (temp == null)
			return null;
		return new MyNodeReader(temp, nffg);
	}

	@Override
	public NodeReader getSourceNode() {
		if (link == null || nffg == null) 
			return null;
		String srcName = link.getSourceNode();
		List<NetworkNode> nodes = nffg.getNetworkNodes().getNode();
		NetworkNode temp = null;
		for (NetworkNode node : nodes) {
			if (node.getName().equals(srcName)) {
				temp = node;
				break;
			}
		}
		if (temp == null)
			return null;
		return new MyNodeReader(temp, nffg);
	}
	
	
}
