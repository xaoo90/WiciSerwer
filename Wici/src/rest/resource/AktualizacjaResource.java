package rest.resource;

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

import dao.AktualizacjaFacade;
import entity.Aktualizacja;

@Path("/aktualizacja")
public class AktualizacjaResource {
	
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("Wici");
	
	AktualizacjaFacade aktualizacjaFacade = new AktualizacjaFacade(emf);	
	
	@Context
    UriInfo uriInfo;
 
    @Context
    Request request;
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String respondAsReady() {
        return "AktualizacjaResource";
    }
    
    @GET
    @Path("ostatnia")
    @Produces(MediaType.APPLICATION_JSON)
    public Aktualizacja getOstatniaAktualizacja() {
    	Aktualizacja aktualizacja = aktualizacjaFacade.findAktualizacja(aktualizacjaFacade.maxId());
		System.out.println("a: " + aktualizacja.getIdAktualizacja()
				+ " " + aktualizacja.gtetDataDodania());
		JSONConfiguration.natural().build();
        return aktualizacja;
    }
    
    @GET
    @Path("ostatnia/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getOstatniaAktualizacja(@PathParam("id") int idAkt) {       
        return aktualizacjaFacade.isOstatniaAktualizacja(idAkt).toString();
    }

}
