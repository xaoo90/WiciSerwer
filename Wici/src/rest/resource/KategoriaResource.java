package rest.resource;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jettison.json.JSONArray;

import com.sun.jersey.api.json.JSONConfiguration;

import dao.KategoriaFacade;
import entity.Kategoria;


@Path("/kategoria")
public class KategoriaResource {
	
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("Wici");
	
	KategoriaFacade kategoriaFacade = new KategoriaFacade(emf);	
	
    @Context
    UriInfo uriInfo;
 
    @Context
    Request request;
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String respondAsReady() {
        return "KategoriaResource";
    }
    
    @GET
    @Path("ostatnie/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Kategoria> getOstatnieKategorie(@PathParam("id") int idAkt) {
    	
    	List<Kategoria> kategorie = kategoriaFacade.noweKategorie(idAkt);
    	System.out.println(kategorie);
    	JSONConfiguration.natural().build();
    	return kategorie;
    }

}
