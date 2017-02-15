package it.polito.dp2.NFFG.sol3.client2;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.datatype.XMLGregorianCalendar;

import it.polito.dp2.NFFG.NodeReader;
import it.polito.dp2.NFFG.sol3.service2.*;

public class MyNffgReader implements it.polito.dp2.NFFG.NffgReader {

	private Nffg nffg;
	public MyNffgReader (Nffg nffg) {
		this.nffg = nffg;
	}
	@Override
	public String getName() {
		if (nffg == null)
			return null;
		return nffg.getName();
	}

	@Override
	public NodeReader getNode(String arg0) {
		if (nffg == null)
			return null;
		List<NetworkNode> nodes= nffg.getNetworkNodes().getNode();
		NetworkNode temp = null;
		for (NetworkNode node: nodes) {
			if (node.getName().equals(arg0)) {
				temp = node;
				break;
			}
		}
		if (temp == null)
			return null;
		NodeReader nr = new MyNodeReader(temp, nffg);
		return nr;
	}

	@Override
	public Set<NodeReader> getNodes() {
		Set<NodeReader> nodeReaders = new HashSet<NodeReader>();
		if (nffg == null)
			return nodeReaders;
		if (nffg.getNetworkNodes() == null)
			return nodeReaders;
		List<NetworkNode> nodes = nffg.getNetworkNodes().getNode();
		for (NetworkNode node : nodes) {
			nodeReaders.add(new MyNodeReader(node, nffg));
		}
		return nodeReaders;
	}

	@Override
	public Calendar getUpdateTime() {
		if (nffg == null) return null;
		XMLGregorianCalendar c = nffg.getLastUpdateTime();
		if (c == null)
			return null;
		return c.toGregorianCalendar();
	}

}
