package it.polito.dp2.NFFG.sol3.service;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.datatype.DatatypeConfigurationException;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import it.polito.dp2.NFFG.sol3.service2.EnabledCache;
import it.polito.dp2.NFFG.sol3.service2.Nffg;
import it.polito.dp2.NFFG.sol3.service2.Policies;
import it.polito.dp2.NFFG.sol3.service2.Policy;
import it.polito.dp2.NFFG.sol3.service2.Verify;

// import it.polito.dp2.NFFG.sol3.service.*;
@Path("")
@Api(value = "", description = "a collection os nffg services")
public class NffgResource {
	
	// create an instance of the object that can execute operations
	NffgService service = new NffgService();
	
	@GET
	@Path("/caching")
	@ApiOperation( value = "get the caching object", notes = "" )
	@ApiResponses( value = {
			@ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 500, message = "Internal Server Error")})
	@Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML}) // should contain 2 types
	public String getCachingStatus() {
		String r = service.getCacheStatus();
		return r;
	}
	
	@POST
	@Path("/caching")
	@ApiOperation( value = "post the caching", notes = "" )
	@ApiResponses( value = {
			@ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 500, message = "Internal Server Error")})
	@Produces({MediaType.APPLICATION_XML})
	public Response setCachingStatus(EnabledCache cache, @Context UriInfo uriInfo) {
		if (cache == null)
			throw new BadRequestException();
		String r = service.setCacheStatus(cache);
		if (r == null)
			throw new BadRequestException();
		UriBuilder builder = uriInfo.getAbsolutePathBuilder();
		URI u = builder.build();
		return Response.created(u).entity(cache).build();
	}
	
	@GET
	@Path("/Nffgs")
	@ApiOperation( value = "get the Nffg objects", notes = "a collection of Nffg objects" )
	@ApiResponses( value = {
			@ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 500, message = "Internal Server Error")})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public List<Nffg> getNffgXML() {
		
		List<Nffg> list = service.getNffg();
		return list;
	}
	
	@GET
	//  the URI matching pattern is a concatenation of the classes @Path expression and that of the methods: /NffgService/name
	@Path("/Nffgs/{name}") // a template parameter
	@ApiOperation( value = "get a single Nffg object", notes = "json and xml formats" )
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 500, message = "Internal Server Error")})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Nffg getSingleNffg(@PathParam("name") String name) {
		
		Nffg nffg = service.getSingleNode(name);
	    if (nffg == null)
			throw new NotFoundException();
		return nffg;
	}
	
	/* Policy */
	
	@GET
	@Path("/Policies")
	@ApiOperation( value = "get policy objects", notes = "a collection of policy objects" )
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 500, message = "Internal Server Error")})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public List<Policy> getPolicies() {
		
		List<Policy> list = service.getPolicies();
		return list;
	}
	
	@GET
	@Path("/Policies/{name}")
	@ApiOperation( value = "get a single policy object", notes = "json and xml formats" )
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 500, message = "Internal Server Error")})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Policy getSinglePolicy(@PathParam("name") String name) {
		
		Policy ep = service.getSinglePolicy(name);
	    if (ep == null)
			throw new NotFoundException();
		return ep;
	}
	
	// get policies belonging to a specified Nffg given Nffg id
	@GET
	@Path("/Nffgs/{name}/nffgPolicies/")
	@ApiOperation( value = "get the policy objects belonging to a given Nffg Id", notes = "json and xml formats")
	@ApiResponses( value = {
			@ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 500, message = "Internal Server Error")})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public List<Policy> getNffgPolicy(@PathParam("name") String name) {
		
		List<Policy> list = service.getNffgPolicy(name);
		if (list == null)
			throw new NotFoundException();
		if (list.size() == 0)
			return null;
		return list;
	}
	
	// get single Policy belonging to a specified Nffg given Nffg id, policy id
		@GET 
		@Path("/Nffgs/{nffgName}/nffgPolicies/{policyName}")
		@ApiOperation( value = "get single policy object belonging to a given Nffg Id, policy id", notes = "json and xml formats")
		@ApiResponses( value = {
				@ApiResponse(code = 200, message = "OK"),
				@ApiResponse(code = 404, message = "Not Found"),
				@ApiResponse(code = 500, message = "Internal Server Error")})
		@Produces("application/xml")
		public Policy getNffgSinglePolicy(@PathParam(value = "nffgName") String nffgName, @PathParam(value = "policyName") String policyName) {
			
			Policy ep = service.getNffgSinglePolicy(nffgName, policyName);
		    if (ep == null)
				throw new NotFoundException();
			return ep;
		}
		
		// execute Verify
		@POST 
		@Path("/Policies")
		@ApiOperation( value = "execute the verification of one policy", notes = "json and xml formats" )
		@ApiResponses(value = {
				@ApiResponse(code = 200, message = "OK"),
				@ApiResponse(code = 404, message = "Not Found"),
				@ApiResponse(code = 500, message = "Internal Server Error")})
		@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
		public Verify getVerify(String name) {
			
			Verify verify = new Verify();
			try {
				synchronized (service.getSynchObject()) {
				verify = service.getVerify(name);
				}
			} catch (DatatypeConfigurationException e) {
				e.printStackTrace();
				throw new InternalServerErrorException();
			}   
			if (verify == null) {
				System.out.println("verify is null for policy " + name);
				throw new NotFoundException();
			}
			return verify;
		}
		
		/*******************Post for Nffg*********************/
		@POST
		@Path("/Nffgs")
		@ApiOperation( value = "create a new nffg object", notes = "json and xml formats" )
		@ApiResponses(value = {
				@ApiResponse(code = 201, message = "Created"),
				@ApiResponse(code = 403, message = "Forbidden"),
				@ApiResponse(code = 400, message = "Bad Request"),
				@ApiResponse(code = 500, message = "Internal Server Error")})
		@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})// specify the returning MIME format
	    @Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON}) // specify the accepted MIME format
		public Response postNffg(Nffg nffg, @Context UriInfo uriInfo) {
			//try {
			Nffg nffgResponse = new Nffg();
			synchronized (service.getSynchObject()) {
			nffgResponse = service.createNffg(nffg);
			}
			if (nffgResponse == null) //|| !nffgResponse.exists())
				throw new BadRequestException();
			if (nffgResponse.getName().equals("repeated"))
				throw new ForbiddenException("Forbidden because creation failed");
			// try {
				UriBuilder builder = uriInfo.getAbsolutePathBuilder();
				URI u = builder.path(nffgResponse.getName()).build();
				return Response.created(u).entity(nffgResponse).build();
			//} catch(ClassCastException e) {
				//throw new BadRequestException();
			//}
			
			/* } catch (Exception e) {
				 throw new javax.ws.rs.InternalServerErrorException();
			 } */
		}
		
		@POST
		@Path("/Nffgs/{name}/nffgPolicies")
		@ApiOperation( value = "create a new Policy object", notes = "json and xml formats" )
		@ApiResponses(value = {
				@ApiResponse(code = 201, message = "Created"),
				@ApiResponse(code = 404, message = "Not Found Exception"),
				@ApiResponse(code = 400, message = "Bad Request Exception"),
				@ApiResponse(code = 500, message = "Internal Server Error")})
		@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
		@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
		public Response postPolicy(Policy ep, @PathParam("name") String name, @Context UriInfo uriInfo) {
			synchronized (service.getSynchObject()) {
			Policy created = service.createPolicy(name, ep); 
			if (created != null) {
				UriBuilder builder = uriInfo.getAbsolutePathBuilder();
				URI u = builder.path(created.getName()).build();
				return Response.created(u).entity(created).build();
			} else
				throw new NotFoundException(); 
			}
		}
		
		/* put */
		
		@PUT
		@Path("/Policies")
		@ApiOperation( value = "update whole policies", notes = "json and xml formats" )
		@ApiResponses(value = {
				@ApiResponse(code = 200, message = "OK"),
				@ApiResponse(code = 400, message = "Bad Request"),
				@ApiResponse(code = 404, message = "Not found"),
				@ApiResponse(code = 500, message = "Internal Server Error")})
		@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
		@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
		public Response putPolicy(Policies policies) {
			if (policies == null)
				throw new BadRequestException();
			List<Policy> policyList = policies.getPolicy();
			if (policyList.isEmpty())
				throw new BadRequestException();
			List<Policy> newPolicyList = new LinkedList<Policy>();
			for (Policy ep : policyList) {
				synchronized (service.getSynchObject()) {
				Policy e = service.getSinglePolicy(ep.getName()); 
				if (e == null)
					throw new NotFoundException();
				Policy newPolicy = service.modifyPolicy(e);
				newPolicyList.add(newPolicy);
				}
			}
			return Response.ok(newPolicyList).build();
		}
		
		/* delete  */
		@DELETE
		@Path("/Policies/{policyName}")
		@ApiOperation( value = "remove a policy object", notes = "json and xml formats" )
		@ApiResponses(value = {
				@ApiResponse(code = 200, message = "OK"),
				@ApiResponse(code = 404, message = "Not Found"),
				@ApiResponse(code = 500, message = "Internal Server Error")})
		@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
		public Policy deleteSinglePolicy(@PathParam("policyName") String policyName) {
			Policy deleted = new Policy();
			synchronized (service.getSynchObject()) {
			deleted = service.remove(policyName); 
			}
			if (deleted != null) {
				return deleted;
			} else
				throw new NotFoundException();
		}
		
		// add delete all and synchronize
		@DELETE
		@Path("/Policies")
		@ApiOperation( value = "remove all policies", notes = "json and xml formats" )
		@ApiResponses(value = {
				@ApiResponse(code = 204, message = "No content"),
				@ApiResponse(code = 500, message = "Internal Server Error")})
		@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
		public Response deleteAllPolicies() {
			synchronized (service.getSynchObject()) {
			service.removeAllPolicies();
			}
			return Response.noContent().build();
		}
}
