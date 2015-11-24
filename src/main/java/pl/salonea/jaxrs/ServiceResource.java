package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.ProviderFacade;
import pl.salonea.ejb.stateless.ServiceFacade;
import pl.salonea.ejb.stateless.ServicePointFacade;
import pl.salonea.entities.Provider;
import pl.salonea.entities.ProviderService;
import pl.salonea.entities.Service;
import pl.salonea.entities.ServicePoint;
import pl.salonea.jaxrs.bean_params.*;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.ProviderWrapper;
import pl.salonea.jaxrs.wrappers.ServicePointWrapper;
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
import pl.salonea.jaxrs.exceptions.BadRequestException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;

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
    @Inject
    private ServicePointFacade servicePointFacade;

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

    @Path("/{serviceId : \\d+}/service-points")
    public ServicePointResource getServicePointResource() {
        return new ServicePointResource();
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

        try {
            // self eagerly link with pattern: http://localhost:port/app/rest/{resources}/{id}/eagerly
            // TODO

            // associated collections links with pattern: http://localhost:port/app/rest/{resources}/{id}/{relationship}
            // TODO

            // providers relationship
            Method providersMethod = ServiceResource.class.getMethod("getProviderResource");
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(providersMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("providers").build());

            // providers eagerly relationship
            Method providersEagerlyMethod = ServiceResource.ProviderResource.class.getMethod("getServiceProvidersEagerly", Integer.class, ProviderBeanParam.class);
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(providersMethod)
                    .path(providersEagerlyMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("providers-eagerly").build());

            // service-points relationship
            Method servicePointsMethod = ServiceResource.class.getMethod("getServicePointResource");
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(servicePointsMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("service-points").build());

            // service-points eagerly relationship
            Method servicePointsEagerlyMethod = ServiceResource.ServicePointResource.class.getMethod("getServiceServicePointsEagerly", Integer.class, ServicePointBeanParam.class);
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(servicePointsMethod)
                    .path(servicePointsEagerlyMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("service-points-eagerly").build());

            // service-points count link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/count
            Method countServicePointsByServiceMethod = ServiceResource.ServicePointResource.class.getMethod("countServicePointsByService", Integer.class, GenericBeanParam.class);
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(servicePointsMethod)
                    .path(countServicePointsByServiceMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("service-points-count").build());

            // service-points address link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/address
            Method addressMethod = ServiceResource.ServicePointResource.class.getMethod("getServiceServicePointsByAddress", Integer.class, AddressBeanParam.class);
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(servicePointsMethod)
                    .path(addressMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("service-points-address").build());

            // service-points coordinates square link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/coordinates-square
            Method coordinatesSquareMethod = ServiceResource.ServicePointResource.class.getMethod("getServiceServicePointsByCoordinatesSquare", Integer.class, CoordinatesSquareBeanParam.class);
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(servicePointsMethod)
                    .path(coordinatesSquareMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("service-points-coordinates-square").build());

            // service-points coordinates circle link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/coordinates-circle
            Method coordinatesCircleMethod = ServiceResource.ServicePointResource.class.getMethod("getServiceServicePointsByCoordinatesCircle", Integer.class, CoordinatesCircleBeanParam.class);
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(servicePointsMethod)
                    .path(coordinatesCircleMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("service-points-coordinates-circle").build());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
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

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

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

                // get providers for given service without filtering (eventually paginated)
                providers = new ResourceList<>( providerFacade.findBySuppliedService(service, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providers).build();
        }

        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceProvidersEagerly( @PathParam("serviceId") Integer serviceId,
                                                    @BeanParam ProviderBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Provider entities for given Service eagerly using ServiceResource.ProviderResource.getServiceProvidersEagerly(serviceId) method of REST API");

            // find service entity for which to get associated providers
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ProviderWrapper> providers = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Service> services = new ArrayList<>();
                services.add(service);

                // get providers for given service eagerly filtered by given params.
                providers = new ResourceList<>(
                        ProviderWrapper.wrap(
                                providerFacade.findByMultipleCriteriaEagerly(params.getCorporations(), params.getProviderTypes(), params.getIndustries(), params.getPaymentMethods(),
                                        services, params.getRated(), params.getMinAvgRating(), params.getMaxAvgRating(), params.getRatingClients(),
                                        params.getProviderName(), params.getDescription(), params.getOffset(), params.getLimit())
                        )
                );
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get providers for given service eagerly without filtering (eventually paginated)
                providers = new ResourceList<>( ProviderWrapper.wrap(providerFacade.findBySuppliedServiceEagerly(service, params.getOffset(), params.getLimit())) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providers).build();

        }

    }

    public class ServicePointResource {

        public ServicePointResource() { }

        /**
         * Method returns subset of ServicePoint entities for given Service.
         * The service id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceServicePoints( @PathParam("serviceId") Integer serviceId,
                                                 @BeanParam ServicePointBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException  {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Service Point entities for given Service using " +
                    "ServiceResource.ServicePointResource.getServiceServicePoints(serviceId) method of REST API");

            // find service entity for which to get associated service points
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ServicePoint> servicePoints = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Service> services = new ArrayList<>();
                services.add(service);

                if(params.getAddress() != null) {
                    if(params.getCoordinatesSquare() != null || params.getCoordinatesCircle() != null)
                        throw new BadRequestException("Query params cannot include address params and coordinates square params or coordinates circle params at the same time.");
                    // only address params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(params.getProviders(), services, params.getProviderServices(), params.getEmployees(),
                                    params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getAddress(),
                                    params.getOffset(), params.getLimit())
                    );

                } else if(params.getCoordinatesSquare() != null) {
                    if(params.getAddress() != null || params.getCoordinatesCircle() != null)
                        throw new BadRequestException("Query params cannot include coordinates square params and address params or coordinates circle params at the same time.");
                    // only coordinates square params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(params.getProviders(), services, params.getProviderServices(), params.getEmployees(),
                                    params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesSquare(),
                                    params.getOffset(), params.getLimit())
                    );

                } else if(params.getCoordinatesCircle() != null) {
                    if(params.getAddress() != null || params.getCoordinatesSquare() != null)
                        throw new BadRequestException("Query params cannot include coordinates circle params and address params or coordinates square params at the same time.");
                    // only coordinates circle params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(params.getProviders(), services, params.getProviderServices(), params.getEmployees(),
                                    params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesCircle(),
                                    params.getOffset(), params.getLimit())
                    );

                } else {
                    // no location params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(params.getProviders(), services, params.getProviderServices(), params.getEmployees(),
                                    params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getOffset(), params.getLimit())
                    );
                }

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                servicePoints = new ResourceList<>( servicePointFacade.findByService(service, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(servicePoints).build();
        }

        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceServicePointsEagerly( @PathParam("serviceId") Integer serviceId,
                                                        @BeanParam ServicePointBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Service Point entities for given Service eagerly using " +
                    "ServiceResource.ServicePointResource.getServiceServicePointsEagerly(serviceId) method of REST API");

            // find service entity for which to get associated service points
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ServicePointWrapper> servicePoints = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Service> services = new ArrayList<>();
                services.add(service);

                if(params.getAddress() != null) {
                    if(params.getCoordinatesSquare() != null || params.getCoordinatesCircle() != null)
                        throw new BadRequestException("Query params cannot include address params and coordinates square params or coordinates circle params at the same time.");
                    // only address params
                    servicePoints = new ResourceList<>(
                            ServicePointWrapper.wrap(
                                    servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), services, params.getProviderServices(), params.getEmployees(),
                                            params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getAddress(),
                                            params.getOffset(), params.getLimit())
                            )
                    );
                } else if(params.getCoordinatesSquare() != null) {
                    if(params.getAddress() != null || params.getCoordinatesCircle() != null)
                        throw new BadRequestException("Query params cannot include coordinates square params and address params or coordinates circle params at the same time.");
                    // only coordinates square params
                    servicePoints = new ResourceList<>(
                            ServicePointWrapper.wrap(
                                    servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), services, params.getProviderServices(), params.getEmployees(),
                                            params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesSquare(),
                                            params.getOffset(), params.getLimit())
                            )
                    );

                } else if(params.getCoordinatesCircle() != null) {
                    if (params.getAddress() != null || params.getCoordinatesSquare() != null)
                        throw new BadRequestException("Query params cannot include coordinates circle params and address params or coordinates square params at the same time.");
                    // only coordinates circle params
                    servicePoints = new ResourceList<>(
                            ServicePointWrapper.wrap(
                                    servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), services, params.getProviderServices(), params.getEmployees(),
                                            params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesCircle(),
                                            params.getOffset(), params.getLimit())
                            )
                    );

                } else {
                    // no location params
                    servicePoints = new ResourceList<>(
                            ServicePointWrapper.wrap(
                                    servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), services, params.getProviderServices(), params.getEmployees(),
                                            params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getOffset(), params.getLimit())
                            )
                    );
                }

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                servicePoints = new ResourceList<>( ServicePointWrapper.wrap(servicePointFacade.findByServiceEagerly(service, params.getOffset(), params.getLimit())) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(servicePoints).build();
        }

        /**
         * Additional methods returning subset of resources based on given criteria
         * you can also achieve similar results by applying @QueryParams to generic method
         * returning all resources in order to filter and limit them
         */

        /**
         * Method that counts Service Point entities for given Service resource.
         * The service id is passed through path params.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countServicePointsByService( @PathParam("serviceId") Integer serviceId,
                                                     @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {


            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of service points for given service by executing " +
                    "ServiceResource.ServicePointResource.countServicePointsByService(serviceId) method of REST API");

            // find service entity for which to count service points
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(servicePointFacade.countByService(service)), 200, "number of service points for service with id " + service.getServiceId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method returns subset of Service Point entities for given Service entity and
         * Address related query params. The service id is passed through path param.
         * Address params are passed through query params.
         */
        @GET
        @Path("/address")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceServicePointsByAddress( @PathParam("serviceId") Integer serviceId,
                                                          @BeanParam AddressBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning service points for given service and address related params using " +
                    "ServiceResource.ServicePointResource.getServiceServicePointsByAddress(serviceId, address) method of REST API");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);
            if(noOfParams < 1)
                throw new BadRequestException("There is no address related query param in request.");

            // find service entity for which to get associated service points
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            // find service points by given criteria
            ResourceList<ServicePoint> servicePoints = new ResourceList<>(
                    servicePointFacade.findByServiceAndAddress(service, params.getCity(), params.getState(), params.getCountry(),
                            params.getStreet(), params.getZipCode(), params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(servicePoints).build();
        }

        /**
         * Method returns subset of Service Point entities for given Service entity and
         * Coordinates Square related params. The service id is passed through path param.
         * Coordinates Square params are passed through query params.
         */
        @GET
        @Path("/coordinates-square")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceServicePointsByCoordinatesSquare( @PathParam("serviceId") Integer serviceId,
                                                                    @BeanParam CoordinatesSquareBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning service points for given service and coordinates square params using " +
                    "ServiceResource.ServicePointResource.getServiceServicePointsByCoordinatesSquare(serviceId, coordinatesSquare) method of REST API");

            if(params.getMinLongitudeWGS84() == null || params.getMinLatitudeWGS84() == null ||
                    params.getMaxLongitudeWGS84() == null || params.getMaxLatitudeWGS84() == null)
                throw new BadRequestException("All coordinates square query params must be specified.");

            // find service entity for which to get associated service points
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            // find service points by given criteria
            ResourceList<ServicePoint> servicePoints = new ResourceList<>(
                    servicePointFacade.findByServiceAndCoordinatesSquare(service, params.getMinLongitudeWGS84(), params.getMinLatitudeWGS84(),
                            params.getMaxLongitudeWGS84(), params.getMaxLatitudeWGS84(), params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(servicePoints).build();
        }

        /**
         * Method returns subset of Service Point entities for given Service entity and
         * Coordinates Circle related params. The service id is passed through path param.
         * Coordinates Circle params are passed through query params.
         */
        @GET
        @Path("/coordinates-circle")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceServicePointsByCoordinatesCircle( @PathParam("serviceId") Integer serviceId,
                                                                    @BeanParam CoordinatesCircleBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning service points for given service and coordinates circle params using " +
                    "ServiceResource.ServicePointResource.getServiceServicePointsByCoordinatesCircle(serviceId, coordinatesCircle) method of REST API");

            if (params.getLongitudeWGS84() == null || params.getLatitudeWGS84() == null || params.getRadius() == null)
                throw new BadRequestException("All coordinates circle query params must be specified.");

            // find service entity for which to get associated service points
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            // find service points by given criteria
            ResourceList<ServicePoint> servicePoints = new ResourceList<>(
                    servicePointFacade.findByServiceAndCoordinatesCircle(service, params.getLongitudeWGS84(),
                            params.getLatitudeWGS84(), params.getRadius(), params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(servicePoints).build();
        }

    }

}
