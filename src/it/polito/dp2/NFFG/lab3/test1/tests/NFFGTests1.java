package it.polito.dp2.NFFG.lab3.test1.tests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.lab3.NFFGClient;
import it.polito.dp2.NFFG.lab3.NFFGClientException;
import it.polito.dp2.NFFG.lab3.NFFGClientFactory;
import it.polito.dp2.NFFG.lab3.ServiceException;
import it.polito.dp2.NFFG.lab3.UnknownNameException;
import it.polito.dp2.NFFG.lab3.test1.Caching;
import it.polito.dp2.NFFG.lab3.test1.NFFGClient3;
import it.polito.dp2.NFFG.lab3.test1.NFFGClient3Factory;

import java.net.URI;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;


public class NFFGTests1 {
	
	private static NffgVerifier referenceNffgVerifier;	// reference data generator
	private static NffgVerifier testNffgVerifier;		// data generator under test
	private static NFFGClient testNFFGClient;			// NFFGClient under test (client1)
	private static NFFGClient3 testNFFGClient3;			// NFFGClient3 under test (client3)
	private static long testcase;
	private static URI serviceURI; 
	private static NffgReader referenceNFFG;
	private static ReachabilityPolicyReader referencePolicy1, referencePolicy2;
	private static boolean isNeo4JXMLRunning;
	private final static String ENABLED_STRING = "enabled";
	private final static String DISABLED_STRING = "disabled";
	private final static String serviceURL = "http://localhost:8080/NffgService/rest/";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		serviceURI = UriBuilder.fromUri(serviceURL).build();
		// Create reference data generator
		System.setProperty("it.polito.dp2.NFFG.NffgVerifierFactory", "it.polito.dp2.NFFG.Random.NffgVerifierFactoryImpl");
		referenceNffgVerifier = NffgVerifierFactory.newInstance().newNffgVerifier();

		// set referenceNFFG
		if(referenceNffgVerifier.getNffgs().size()!=0){
			TreeSet<NffgReader> rts = new TreeSet<NffgReader>(new NamedEntityReaderComparator());
			rts.addAll(referenceNffgVerifier.getNffgs());
			Iterator<NffgReader> iter = rts.iterator();
			boolean found=false;
			// look for nffg with at least two policies
			while(iter.hasNext() && !found) {
				referenceNFFG = iter.next();
				Set<PolicyReader> policies = referenceNffgVerifier.getPolicies(referenceNFFG.getName());
				if (policies.size()>1) {
					TreeSet<PolicyReader> pts = new TreeSet<PolicyReader>(new NamedEntityReaderComparator());
					pts.addAll(policies);
					Iterator<PolicyReader> pIter = pts.iterator();
					referencePolicy1 = (ReachabilityPolicyReader) pIter.next();
					referencePolicy2 = (ReachabilityPolicyReader) pIter.next();
					found=true;
				}
			}
			assertEquals("Tests cannot run. Please choose another seed.",found,true);
		}
		
		// read testcase property
		Long testcaseObj = Long.getLong("it.polito.dp2.NFFG.Random.testcase");
		if (testcaseObj == null)
			testcase = 0;
		else
			testcase = testcaseObj.longValue();
		
		// read isNeo4JXMLRunning property
		isNeo4JXMLRunning = Boolean.valueOf(System.getProperty("it.polito.dp2.NFFG.lab3.test1.isNeo4JXMLRunning"));

	}

	@Before
	public void setUp() throws Exception {
        assertNotNull("Internal tester error during test setup: null nffgverifier reference", referenceNffgVerifier);
        assertNotNull("Internal tester error during test setup: null reference NFFG", referenceNFFG);
        assertNotNull("Internal tester error during test setup: null reference serviceURI", serviceURI);
	}

	@Test
	public final void testCachingBehavior() {
		System.out.println("DEBUG: starting testCachingBehavior");
		try {		
				// 0. create Client3 and Client1
				createClient3();
				createClient();
				
				// 1. Enable caching
				testNFFGClient3.setCaching(Caching.ENABLED);
				
				// 2. Compute reachability of reference policies
				testNFFGClient.testReachabilityPolicy(referencePolicy1.getName());
				testNFFGClient.testReachabilityPolicy(referencePolicy2.getName());

				// 3. Disable caching		
				testNFFGClient3.setCaching(Caching.DISABLED);
				
				// 4. Compute reachability of reference policies
				testPolicyWithoutCaching(referencePolicy1);
				testPolicyWithoutCaching(referencePolicy2);
		} catch (UnknownNameException 
				| ServiceException e) {
			fail("Unexpected exception thrown: "+e.getClass().getName());
		}		

	}
	
	private void createClient() {
		// Create client under test
		try {
			testNFFGClient = NFFGClientFactory.newInstance().newNFFGClient();
		} catch (FactoryConfigurationError | NFFGClientException e) {
			fail("The creation of an instance of NFFGClient failed");
		}
		
		assertNotNull("The implementation under test generated a null NFFGClient", testNFFGClient);
	}
	
	
	private void createClient3() {
		// Create client under test
		try {
			testNFFGClient3 = NFFGClient3Factory.newInstance().newNFFGClient3();
		} catch (FactoryConfigurationError | NFFGClientException e) {
			fail("The creation of an instance of NFFGClient3 failed");
		}
		
		assertNotNull("The implementation under test generated a null NFFGClient3", testNFFGClient3);
	}

	private void testPolicyWithoutCaching(ReachabilityPolicyReader policy) throws UnknownNameException {
		try {
			testNFFGClient.testReachabilityPolicy(policy.getName());
			if (!isNeo4JXMLRunning)
				fail("Operation that was expected to fail did not fail");
		}
		catch (ServiceException se) {
			// This exception is expected if Neo4JXML is not up
			if (isNeo4JXMLRunning)
				fail("Unexpected exception thrown...: "+se.getClass().getName());
		}
	}

	@Test
	public final void testGetCachingTextPlain() {
		System.out.println("DEBUG: starting testGetCachingTextPlain");
		try {		
				// 0. create Client3
				createClient3();
				
				// 1. Enable caching
				testNFFGClient3.setCaching(Caching.ENABLED);
				
				// 2. Get caching as text/plain and compare returned value with expected one (enabled)
				assertEquals("Wrong caching value", ENABLED_STRING, getCachingAsTextPlain());					

				// 3. Disable caching		
				testNFFGClient3.setCaching(Caching.DISABLED);
				
				// 4. Get caching as text/plain and compare returned value with expected one (disabled)
				assertEquals("Wrong caching value", DISABLED_STRING, getCachingAsTextPlain());					
				
		} catch (ServiceException e) {
			fail("Unexpected exception thrown: "+e.getClass().getName());
		}		

	}
	
	@Test
	public final void testGetCachingXML() {
		System.out.println("DEBUG: starting testGetCachingXML");
		try {		
				// 0. create Client3
				createClient3();
				
				// 1. Enable caching
				testNFFGClient3.setCaching(Caching.ENABLED);
				
				// 2. Get caching as XML and store returned value
				String returned1 = getCachingAsXML();					

				// 3. Disable caching		
				testNFFGClient3.setCaching(Caching.DISABLED);
				
				// 4. Get caching as XML and store returned value
				String returned2 = getCachingAsXML();					

				// 5. Enable caching
				testNFFGClient3.setCaching(Caching.ENABLED);
				
				// 6. Get caching as XML and store returned value
				String returned3 = getCachingAsXML();	
				
				// 7. Compare returned values with expected ones
				assertEquals("Non-consistent XML value returned by GET",returned1,returned3);
				assertTrue("Non-consistent XML value returned by GET",!returned1.equals(returned2));

		} catch (ServiceException e) {
			fail("Unexpected exception thrown: "+e.getClass().getName());
		}		

	}

	private String getCachingAsXML() {
		Client c = ClientBuilder.newClient();
		String result = c.target(serviceURI).path("/caching").request()
				   .accept(MediaType.APPLICATION_XML,MediaType.TEXT_XML)
				   .get(String.class);
		
		return result;
	}

	private String getCachingAsTextPlain() {
		Client c = ClientBuilder.newClient();
		String result = c.target(serviceURI).path("/caching").request()
				   .accept(MediaType.TEXT_PLAIN)
				   .get(String.class);
		return result;
	}

}

class NamedEntityReaderComparator implements Comparator<NamedEntityReader> {
    public int compare(NamedEntityReader f0, NamedEntityReader f1) {
    	return f0.getName().compareTo(f1.getName());
    }
}
