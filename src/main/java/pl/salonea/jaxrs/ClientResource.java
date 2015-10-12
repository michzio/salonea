package pl.salonea.jaxrs;


import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.xml.ws.Response;

/**
 * Created by michzio on 09/10/2015.
 */
@Path("/clients")
public class ClientResource {



    /**
     * related subresources (through relationships)
     */

    @Path("/{clientId: \\d+}/provider-ratings")
    public ProviderRatingResource getProviderRatingResource() {
        return new ProviderRatingResource();
    }

    public class ProviderRatingResource {

        public ProviderRatingResource() { }

        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countProviderRatingsByClient( @PathParam("clientId") Long clientId,
                                                      @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException  {

            if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");


            return null;

        }
    }

}
