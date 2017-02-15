package it.polito.dp2.NFFG.sol3.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.polito.dp2.NFFG.sol3.service2.Verify;


public class VerifyDB {
	private static Map<String, Verify> verifyMap = new ConcurrentHashMap<String, Verify>();
	
	public static Map<String, Verify> getMap() {
		return verifyMap;
	}
}
