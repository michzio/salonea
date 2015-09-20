package pl.salonea.jaxrs;

import pl.salonea.entities.ProviderRating;
import pl.salonea.entities.ProviderService;
import pl.salonea.jaxrs.utils.hateoas.Link;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;

/**
 * Created by michzio on 12/09/2015.
 */
@Path("/")
public class ProviderRatingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String get() {
        return "dogs";
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(ProviderRating providerRating, UriInfo uriInfo) {


        try {
            // self link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/{sub-id}
            Method providerRatingsMethod = ProviderResource.class.getMethod("getProviderRatingResource");
            providerRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerRatingsMethod)
                    .path(providerRating.getClient().getClientId().toString())
                    .resolveTemplate("userId", providerRating.getProvider().getUserId().toString())
                    .build())
                    .rel("self").build());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }
}
