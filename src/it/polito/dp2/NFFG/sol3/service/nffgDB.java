package it.polito.dp2.NFFG.sol3.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.polito.dp2.NFFG.sol3.service2.NetworkNode;
import it.polito.dp2.NFFG.sol3.service2.Nffg;

public class nffgDB {
	
	private static Map<String, Nffg> nffgMap = new ConcurrentHashMap<String, Nffg>();
	private static Map <String, Map<String, NetworkNode>> nffgNodes = new ConcurrentHashMap<String, Map<String, NetworkNode>>();
	
	public static Map<String, Nffg> getMap() {
		return nffgMap;
	}
	public static Map <String, Map<String, NetworkNode>> getNffgNodes() {
		return nffgNodes;
	}
}
