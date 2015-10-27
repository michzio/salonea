package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.ProviderFacade;
import pl.salonea.ejb.stateless.ServiceFacade;
import pl.salonea.entities.Provider;
import pl.salonea.entities.ProviderService;
import pl.salonea.entities.Service;
import pl.salonea.jaxrs.bean_params.ProviderBeanParam;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.ServiceWrapper;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
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

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(Service service, UriInfo uriInfo) {

        // self link with pattern: http://localhost:port/app/rest/{resources}/{id}
        service.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                    .path(ServiceResource.class)
                                                    .path(service.getServiceId().toString())
                                                    .build())
                                    .rel("self").build() );

        // collection link with pattern: http://localhost:port/app/rest/{resources}
        service.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                    .path(ServiceResource.class)
                                                    .build())
                                    .rel("services").build() );
    }

    public class ProviderResource {

        public ProviderResource() { }

        /**
         * Method returns subset of Provider entities for given Service entity.
         * The service id is passed through path param.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceProviders(@PathParam("serviceId") Integer serviceId,
                                            @BeanParam ProviderBeanParam params) throws ForbiddenException, NotFoundException {

            if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning providers for given service using ServiceResource.ProviderResource.getServiceProviders(serviceId) method of REST API");

            // find service entity for which to get associated providers
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            // calculate number of filter query params
            Integer noOfParams = params.getUriInfo().getQueryParameters().size();
            if(params.getOffset() != null) noOfParams -= 1;
            if(params.getLimit() != null) noOfParams -= 1;

            ResourceList<Provider> providers = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Service> services = new ArrayList<>();
                services.add(service);

                // get providers for given service filtered by given params.
                providers = new ResourceList<>(
                        providerFacade.findByMultipleCriteria(params.getCorporations(), params.getProviderTypes(), params.getIndustries(), params.getPaymentMethods(),
                                services, params.getRated(), params.getMinAvgRating(), params.getMaxAvgRating(),params.getRatingClients(),
                                params.getProviderName(), params.getDescription(), params.getOffset(), params.getLimit())
                );

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get providers for given service without filtering
                providers = new ResourceList<>( providerFacade.findBySuppliedService(service, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providers).build();
        }

    }

}
