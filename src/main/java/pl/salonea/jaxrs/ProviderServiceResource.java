package pl.salonea.jaxrs;


import pl.salonea.ejb.stateless.ProviderServiceFacade;
import pl.salonea.ejb.stateless.ServicePointFacade;
import pl.salonea.entities.Provider;
import pl.salonea.entities.ProviderService;
import pl.salonea.entities.ServicePoint;
import pl.salonea.entities.idclass.ProviderServiceId;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.bean_params.PaginationBeanParam;
import pl.salonea.jaxrs.bean_params.ProviderServiceBeanParam;
import pl.salonea.jaxrs.bean_params.ServicePointBeanParam;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.ProviderServiceWrapper;

import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.*;
import javax.transaction.NotSupportedException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.BadRequestException;
import javax.ws.rs.core.Response.Status;

/**
 * Created by michzio on 12/09/2015.
 */
@Path("/provider-services")
public class ProviderServiceResource {

    private static final Logger logger = Logger.getLogger(ProviderServiceResource.class.getName());

    @Inject
    private UserTransaction utx;

    @Inject
    private ProviderServiceFacade providerServiceFacade;
    @Inject
    private ServicePointFacade servicePointFacade;

    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countProviderServices(  @HeaderParam("authToken") String authToken ) throws ForbiddenException {

        if(authToken == null) throw new ForbiddenException("Unauthorized access to web service.");

        return null;
    }

    @Path("/{providerId : \\d+}+{serviceId : \\d+}/service-points")
    public ServicePointResource getServicePointResource() {
        return new ServicePointResource();
    }

    /**
     * This method enables to populate list of resources and each individual resource on list
     * with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList providerServices, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(providerServices, uriInfo, offset, limit);

        try {
            // count resources hypermedia link
            Method countMethod = ProviderServiceResource.class.getMethod("countProviderServices", String.class);
            providerServices.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderServiceResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            providerServices.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderServiceResource.class).build()).rel("provider-services").build() );

            // TODO

        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        }

        for(Object object : providerServices.getResources()) {
            if(object instanceof ProviderService) {
                ProviderServiceResource.populateWithHATEOASLinks( (ProviderService) object, uriInfo);
            } else if(object instanceof ProviderServiceWrapper) {
                ProviderServiceResource.populateWithHATEOASLinks( (ProviderServiceWrapper) object, uriInfo);
            }
        }

    }

    /**
     * This method enables to populate each individual resource wrapper with hypermedia links
     */
    public static void populateWithHATEOASLinks(ProviderServiceWrapper providerServiceWrapper, UriInfo uriInfo) {

        ProviderServiceResource.populateWithHATEOASLinks(providerServiceWrapper.getProviderService(), uriInfo);

        // TODO
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

            // self eagerly link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/{sub-id}/eagerly
            Method providerServiceEagerlyMethod = ProviderResource.ProviderServiceResource.class.getMethod("getProviderServiceEagerly", Long.class, Integer.class, GenericBeanParam.class);
            providerService.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerServicesMethod)
                    .path(providerServiceEagerlyMethod)
                    .resolveTemplate("userId", providerService.getProvider().getUserId().toString())
                    .resolveTemplate("serviceId", providerService.getService().getServiceId().toString())
                    .build())
                    .rel("provider-service-eagerly").build());

            // collection link with pattern: http://localhost:port/app/rest/{resources}
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderServiceResource.class)
                    .build())
                    .rel("provider-services").build());

            /**
             * Provider related sub-collections
             */
            // sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerServicesMethod)
                    .resolveTemplate("userId", providerService.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-provider-services").build());

            // sub-collection eagerly link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}
            Method providerServicesEagerlyMethod = ProviderResource.ProviderServiceResource.class.getMethod("getProviderServicesEagerly", Long.class, ProviderServiceBeanParam.class);
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerServicesMethod)
                    .path(providerServicesEagerlyMethod)
                    .resolveTemplate("userId", providerService.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-provider-services-eagerly").build());

            // sub-collection count link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/count
            Method countByProviderMethod = ProviderResource.ProviderServiceResource.class.getMethod("countProviderServicesByProvider", Long.class, GenericBeanParam.class);
            providerService.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerServicesMethod)
                    .path(countByProviderMethod)
                    .resolveTemplate("userId", providerService.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-provider-services-count").build());

            // categorized sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/categorized-in/{categoryId}
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerServicesMethod)
                    .path("categorized-in")
                    .resolveTemplate("userId", providerService.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-provider-services-categorized").build());

            // described sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/described-by/{description}
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerServicesMethod)
                    .path("described-by")
                    .resolveTemplate("userId", providerService.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-provider-services-described").build());

            // discounted sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/discounted-between?minDiscount={minDiscount}&maxDiscount={maxDiscount}
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerServicesMethod)
                    .path("discounted-between")
                    .resolveTemplate("userId", providerService.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-provider-services-discounted").build());

            // supplied-by-employee sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/supplied-by/{employeeId}
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerServicesMethod)
                    .path("supplied-by")
                    .resolveTemplate("userId", providerService.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-provider-services-supplied-by-employee").build());

            /**
             * Service Points associated with current Provider Service resource
             */

            // service-points relationship
            Method servicePointsMethod = ProviderServiceResource.class.getMethod("getServicePointResource");
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderServiceResource.class)
                    .path(servicePointsMethod)
                    .resolveTemplate("providerId", providerService.getProvider().getUserId().toString())
                    .resolveTemplate("serviceId", providerService.getService().getServiceId().toString())
                    .build())
                    .rel("service-points").build());


        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    public class ServicePointResource {

        public ServicePointResource() { }

        /**
         * Method returns subset of ServicePoint entities for given ProviderService.
         * The provider service composite id (provider id, service id) is passed through path params.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServiceServicePoints( @PathParam("providerId") Long providerId,
                                                         @PathParam("serviceId") Integer serviceId,
                                                         @BeanParam ServicePointBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
                /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Service Point entities for given Provider Service using " +
                    "ProviderServiceResource.ServicePointResource.getProviderServiceServicePoints(providerId, serviceId) method of REST API");

            utx.begin();

            // find provider service entity for which to get associated service points
            ProviderService providerService = providerServiceFacade.find(new ProviderServiceId(providerId, serviceId));
            if(providerService == null)
                throw new NotFoundException("Could not find provider service for id (" + providerId + "," + serviceId + ").");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ServicePoint> servicePoints = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<ProviderService> providerServices = new ArrayList<>();
                providerServices.add(providerService);

                if(params.getAddress() != null) {
                    if(params.getCoordinatesSquare() != null || params.getCoordinatesCircle() != null)
                        throw new BadRequestException("Query params cannot include address params and coordinates square params or coordinates circle params at the same time.");
                    // only address params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(params.getProviders(), params.getServices(), providerServices, params.getEmployees(),
                                    params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getAddress(),
                                    params.getOffset(), params.getLimit())
                    );
                } else if(params.getCoordinatesSquare() != null) {
                    if(params.getAddress() != null || params.getCoordinatesCircle() != null)
                        throw new BadRequestException("Query params cannot include coordinates square params and address params or coordinates circle params at the same time.");
                    // only coordinates square params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(params.getProviders(), params.getServices(), providerServices, params.getEmployees(),
                                    params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesSquare(),
                                    params.getOffset(), params.getLimit())
                    );
                } else if(params.getCoordinatesCircle() != null) {
                    if(params.getAddress() != null || params.getCoordinatesSquare() != null)
                        throw new BadRequestException("Query params cannot include coordinates circle params and address params or coordinates square params at the same time.");
                    // only coordinates circle params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(params.getProviders(), params.getServices(), providerServices, params.getEmployees(),
                                    params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesCircle(),
                                    params.getOffset(), params.getLimit())
                    );
                } else {
                    // no location params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(params.getProviders(), params.getServices(), providerServices, params.getEmployees(),
                                    params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getOffset(), params.getLimit())
                    );
                }

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                servicePoints = new ResourceList<>( servicePointFacade.findByProviderService(providerService, params.getOffset(), params.getLimit()) );
            }

            utx.commit();

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(servicePoints).build();
        }
    }

}
