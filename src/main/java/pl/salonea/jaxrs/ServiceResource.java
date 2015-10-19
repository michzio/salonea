package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.ProviderFacade;
import pl.salonea.ejb.stateless.ServiceFacade;
import pl.salonea.entities.ProviderService;
import pl.salonea.entities.Service;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.ServiceWrapper;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * Created by michzio on 19/10/2015.
 */
@Path("/services")
public class ServiceResource {

    private static final Logger logger = Logger.getLogger(ServiceResource.class.getName());

    @Inject
    private ServiceFacade serviceFacade;
    @Inject
    private ProviderFacade providerFacade;

    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countServices( @HeaderParam("authToken") String authToken ) throws ForbiddenException {

        if(authToken == null) throw new ForbiddenException("Unauthorized access to web service.");

        // TODO
        return null;
    }

    /**
     * related subresources (through relationships)
     */

    @Path("/{serviceId : \\d+}/providers")
    public ProviderResource getProviderResource() {
        return new ProviderResource();
    }

    // helper methods e.g. to populate resources/resource lists with HATEOAS links

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList services, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(services, uriInfo, offset, limit);

        try {
            // count resources hypermedia link
            Method countMethod = ServiceResource.class.getMethod("countServices", String.class);
            services.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(ServiceResource.class).path(countMethod).build()).rel("count").build());

            // get all resources hypermedia link
            services.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(ServiceResource.class).build()).rel("services").build());

            // TODO

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for(Object object : services.getResources()) {
            if(object instanceof Service) {
                ServiceResource.populateWithHATEOASLinks( (Service) object, uriInfo);
            } else if(object instanceof ServiceWrapper) {
                ServiceResource.populateWithHATEOASLinks( (ServiceWrapper) object, uriInfo);
            }
        }
    }

    /**
     * This method enables to populate each individual resource wrapper with hypermedia links
     */
    public static void populateWithHATEOASLinks(ServiceWrapper serviceWrapper, UriInfo uriInfo) {

        ServiceResource.populateWithHATEOASLinks(serviceWrapper.getService(), uriInfo);

        for(ProviderService providerService : serviceWrapper.getProviderServices())
            pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(providerService, uriInfo);
    }

    public static void populateWithHATEOASLinks(Service service, UriInfo uriInfo) {
    }

}
