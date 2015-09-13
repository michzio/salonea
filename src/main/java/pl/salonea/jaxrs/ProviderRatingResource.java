package pl.salonea.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by michzio on 12/09/2015.
 */
public class ProviderRatingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String get() {
        return "dogs";
    }
}
