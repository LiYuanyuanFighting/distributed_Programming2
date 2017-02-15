package it.polito.dp2.NFFG.sol3.test1.client3;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import it.polito.dp2.NFFG.NffgVerifier;
import it.polito.dp2.NFFG.lab3.ServiceException;
import it.polito.dp2.NFFG.lab3.UnknownNameException;
import it.polito.dp2.NFFG.lab3.test1.Caching;
import it.polito.dp2.NFFG.sol3.service2.EnabledCache;
import it.polito.dp2.NFFG.sol3.service2.Nffg;

public class NFFGClient3 implements it.polito.dp2.NFFG.lab3.test1.NFFGClient3 {

	private NffgVerifier monitor;
	private WebTarget target;
	
	public NFFGClient3() throws ServiceException {
	Client client = ClientBuilder.newClient();
	target = client.target(getBaseURI());
	if (target == null)
		throw new ServiceException();
	}
	
	private static URI getBaseURI(){
		String s = System.getProperty("it.polito.dp2.NFFG.lab3.URL");
		return UriBuilder.fromUri(s).build();
	}
	
	@Override
	public void setCaching(Caching c) throws ServiceException {
		EnabledCache ec = new EnabledCache();
		if (c.name().equals("ENABLED")) { //  use c.name()
			ec.setCache("enabled");
		}
		else
			ec.setCache("disabled");
		Response r = target.path("caching")
				.request(MediaType.APPLICATION_XML)
				.post(Entity.entity(ec, MediaType.APPLICATION_XML));
		System.out.println("*****************The response code is " + r.getStatus());
		if (r.getStatus() != 201)
			throw new ServiceException();
	}

	@Override
	public Caching getCaching() throws ServiceException {
		Response response = target.path("caching").request().accept(MediaType.TEXT_PLAIN).get();
		System.out.println("*****************The response code is " + response.getStatus());
		if (response.getStatus() != 200)
			throw new ServiceException();
		String s = response.readEntity(String.class);
		Caching c = null;
		if (s.equals("enabled"))
			return c.ENABLED;
			
		return c.DISABLED;
	}

}
