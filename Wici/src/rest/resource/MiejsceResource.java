package rest.resource;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.json.JSONConfiguration;

import dao.MiejsceFacade;
import entity.Miejsce;

@Path("/miejsce")
public class MiejsceResource {
	
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("Wici");
	
	MiejsceFacade miejsceFacade = new MiejsceFacade(emf);
		
	@Context
    UriInfo uriInfo;
 
    @Context
    Request request;
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String respondAsReady() {
        return "MiejsceResource";
    }
    
    @GET
    @Path("ostatnie/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Miejsce> getOstatnieMiejsca(@PathParam("id") int idAkt) {
    	List<Miejsce> miejsca = miejsceFacade.noweMiejsca(idAkt);
    	System.out.println(miejsca);
    	JSONConfiguration.natural().build();
    	return miejsca;
    }
}
