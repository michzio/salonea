package pl.salonea.jaxrs;

import pl.salonea.entities.ServicePoint;
import pl.salonea.jaxrs.utils.ResourceList;
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

@Path("/service-points")
public class ServicePointResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String get() {
        return "dogs";
    }

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList<ServicePoint> servicePoints, UriInfo uriInfo, Integer offset, Integer limit) {


    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(ServicePoint servicePoint, UriInfo uriInfo) {


        try {
            // self link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/{sub-id}
            Method servicePointsMethod = ProviderResource.class.getMethod("getServicePointResource");
            servicePoint.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(servicePointsMethod)
                    .path(servicePoint.getServicePointNumber().toString())
                    .resolveTemplate("userId", servicePoint.getProvider().getUserId().toString())
                    .build())
                    .rel("self").build());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }
}
