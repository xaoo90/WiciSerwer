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

import dao.WydarzenieFacade;
import dao.ZdjecieFacade;
import entity.Zdjecie;

@Path("/zdjecie")
public class ZdjecieResource {
	
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("Wici");
	
	ZdjecieFacade zdjecieFacade = new ZdjecieFacade(emf);
	WydarzenieFacade wydarzenieFacade = new WydarzenieFacade(emf);
	

	
	@Context
    UriInfo uriInfo;
 
    @Context
    Request request;
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String respondAsReady() {
        return "ZdjecieResource";
    }
    
    @GET
    @Path("ostatnie/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Zdjecie> getOstatnieZdjecia(@PathParam("id") int idAkt) {
    	List<Zdjecie> zdjecia = zdjecieFacade.noweZdjecia(wydarzenieFacade.noweWydarzeniaId(idAkt));
    	System.out.println("zdjecia  " + zdjecia);
    	JSONConfiguration.natural().build();
    	return zdjecia;
    }


}
