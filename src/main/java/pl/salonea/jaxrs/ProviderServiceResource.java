package pl.salonea.jaxrs;

import pl.salonea.entities.ProviderService;
import pl.salonea.entities.ServicePoint;
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
public class ProviderServiceResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String get() {
        return "dogs";
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(ProviderService providerService, UriInfo uriInfo) {

        try {
            // self link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/{sub-id}
            Method providerServicesMethod = ProviderResource.class.getMethod("getProviderServiceResource");
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerServicesMethod)
                    .path(providerService.getService().getServiceId().toString())
                    .resolveTemplate("userId", providerService.getProvider().getUserId().toString())
                    .build())
                    .rel("self").build());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }
}
