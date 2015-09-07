package pl.salonea.jaxrs;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Created by michzio on 01/09/2015.
 */

@Path("/hello-world-resource")
public class RESTHelloWorldResource {

    @GET
    @Path("/{pathParam}")
    public Response getResponse( @PathParam("pathParam") String pathParam,
                                 @DefaultValue("Nothing") @QueryParam("queryParameter") String queryParam) {

        String response = "Hello from: " + pathParam + " : " + queryParam;
        return Response.status(200).entity(response).build();
    }
}
