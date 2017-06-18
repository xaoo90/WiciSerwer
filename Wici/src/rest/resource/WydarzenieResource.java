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
import entity.Wydarzenie;

@Path("/wydarzenie")
public class WydarzenieResource {

	EntityManagerFactory emf = Persistence.createEntityManagerFactory("Wici");

	WydarzenieFacade wydarzenieFacade = new WydarzenieFacade(emf);

	@Context
	UriInfo uriInfo;

	@Context
	Request request;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String respondAsReady() {
		return "WydarzenieResource";
	}

	@GET
	@Path("ostatnie/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Wydarzenie> getOstatnieWydarzenia(@PathParam("id") int idAkt) {
		List<Wydarzenie> wydarzenia = wydarzenieFacade.noweWydarzenia(idAkt);
		System.out.println(wydarzenia);

    	JSONConfiguration.natural().build();
		return wydarzenia;
		
	}

}
