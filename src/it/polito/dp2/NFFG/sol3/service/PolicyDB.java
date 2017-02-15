package it.polito.dp2.NFFG.sol3.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.polito.dp2.NFFG.sol3.service2.Policy;

public class PolicyDB {

	// this is a database class containing a static Map of Policy objects
	private static Map<String, Policy> map = new ConcurrentHashMap<String, Policy>();
	
	public static Map<String, Policy> getMap() {
		return map;
	}
	
	public static void setMap(Map<String, Policy> map) {
		PolicyDB.map = map;
	}
}
