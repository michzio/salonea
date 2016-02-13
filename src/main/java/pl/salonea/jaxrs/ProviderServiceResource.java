package pl.salonea.jaxrs;


import pl.salonea.ejb.stateless.EmployeeFacade;
import pl.salonea.ejb.stateless.ProviderServiceFacade;
import pl.salonea.ejb.stateless.ServicePointFacade;
import pl.salonea.entities.Employee;
import pl.salonea.entities.ProviderService;
import pl.salonea.entities.ServicePoint;
import pl.salonea.entities.WorkStation;
import pl.salonea.entities.idclass.ProviderServiceId;
import pl.salonea.jaxrs.bean_params.*;
import pl.salonea.jaxrs.exceptions.UnprocessableEntityException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.EmployeeWrapper;
import pl.salonea.jaxrs.wrappers.ProviderServiceWrapper;

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
import pl.salonea.jaxrs.wrappers.ServicePointWrapper;

import javax.ws.rs.core.Response.Status;

/**
 * Created by michzio on 12/09/2015.
 */
@Path("/provider-services")
public class ProviderServiceResource {

    private static final Logger logger = Logger.getLogger(ProviderServiceResource.class.getName());

    @Inject
    private UserTransaction utx;

    /* DAO objects */
    @Inject
    private ProviderServiceFacade providerServiceFacade;
    @Inject
    private ServicePointFacade servicePointFacade;
    @Inject
    private EmployeeFacade employeeFacade;

    /* Resource objects */
    @Inject
    private ProviderResource providerResource;

    /**
     * Alternative methods to access Provider Service resource
     */
    @GET
    @Path("/{providerId: \\d+}+{serviceId: \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProviderService( @PathParam("providerId") Long providerId,
                                        @PathParam("serviceId") Integer serviceId,
                                        @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        return providerResource.getProviderServiceResource().getProviderService(providerId, serviceId, params);
    }

    @GET
    @Path("/{providerId: \\d+}+{serviceId: \\d+}/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProviderServiceEagerly( @PathParam("providerId") Long providerId,
                                               @PathParam("serviceId") Integer serviceId,
                                               @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        return providerResource.getProviderServiceResource().getProviderServiceEagerly(providerId, serviceId, params);
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createProviderService( ProviderService providerService,
                                           @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        return providerResource.getProviderServiceResource().createProviderService(providerService.getProvider().getUserId(),
                providerService.getService().getServiceId(), providerService, params);
    }

    @PUT
    @Path("/{providerId: \\d+}+{serviceId: \\d+}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateProviderService( @PathParam("providerId") Long providerId,
                                           @PathParam("serviceId") Integer serviceId,
                                           ProviderService providerService,
                                           @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        return providerResource.getProviderServiceResource().updateProviderService(providerId, serviceId, providerService, params);
    }

    @DELETE
    @Path("/{providerId: \\d+}+{serviceId: \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeProviderService( @PathParam("providerId") Long providerId,
                                           @PathParam("serviceId") Integer serviceId,
                                           @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException, InternalServerErrorException {

        return providerResource.getProviderServiceResource().removeProviderService(providerId, serviceId, params);
    }

    /**
     * Method returns all Provider Service entities.
     * They can be additionally filtered and paginated by @QueryParams.
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProviderServices( @BeanParam ProviderServiceBeanParam params ) throws ForbiddenException,
    /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Provider Services by executing ProviderServiceResource.getProviderServices() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<ProviderService> providerServices = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            utx.begin();

            // get provider services filtered by criteria provided in query params
            providerServices = new ResourceList<>(
                    providerServiceFacade.findByMultipleCriteria(params.getProviders(), params.getServices(), params.getServiceCategories(),
                            params.getDescriptions(), params.getMinPrice(), params.getMaxPrice(), params.getIncludeDiscounts(),
                            params.getMinDiscount(), params.getMaxDiscount(), params.getServicePoints(), params.getWorkStations(),
                            params.getEmployees(), params.getOffset(), params.getLimit())
            );

            utx.commit();

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all provider services without filtering (eventually paginated)
            providerServices = new ResourceList<>( providerServiceFacade.findAll(params.getOffset(), params.getLimit()) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        ProviderServiceResource.populateWithHATEOASLinks(providerServices, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(providerServices).build();
    }

    @GET
    @Path("/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProviderServicesEagerly( @BeanParam ProviderServiceBeanParam params ) throws ForbiddenException,
    /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Provider Services eagerly by executing ProviderServiceResource.getProviderServicesEagerly() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<ProviderServiceWrapper> providerServices = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            utx.begin();

            // get provider services eagerly filtered by criteria provided in query params
            providerServices = new ResourceList<>(
                    ProviderServiceWrapper.wrap(
                        providerServiceFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getServices(), params.getServiceCategories(),
                                params.getDescriptions(), params.getMinPrice(), params.getMaxPrice(), params.getIncludeDiscounts(),
                                params.getMinDiscount(), params.getMaxDiscount(), params.getServicePoints(), params.getWorkStations(),
                                params.getEmployees(), params.getOffset(), params.getLimit())
                    )
            );

            utx.commit();

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all provider services eagerly without filtering (eventually paginated)
            providerServices = new ResourceList<>( ProviderServiceWrapper.wrap(providerServiceFacade.findAllEagerly(params.getOffset(), params.getLimit())) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        ProviderServiceResource.populateWithHATEOASLinks(providerServices, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(providerServices).build();
    }

    /**
     * Additional methods returning a subset of resources based on given criteria.
     * You can achieve similar results by applying @QueryParams to generic method
     * returning all resources in order to filter and limit them.
     */

    /**
     * Method returns number of Provider Service entities in database.
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countProviderServices(  @BeanParam GenericBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning number of provider services by executing ProviderServiceResource.countProviderServices() method of REST API");

        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(providerServiceFacade.count()), 200, "number of provider services");
        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method returns subset of Provider Service entities for given description.
     * The description is passed through path param.
     */
    @GET
    @Path("/described/{description: \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProviderServicesByDescription( @PathParam("description") String description,
                                                      @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning provider services for given description using ProviderServiceResource.getProviderServicesByDescription(description) method of REST API");

        // find provider services by given criteria (description)
        ResourceList<ProviderService> providerServices = new ResourceList<>( providerServiceFacade.findByDescription(description, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        ProviderServiceResource.populateWithHATEOASLinks(providerServices, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(providerServices).build();
    }

    /**
     * related subresources (through relationships)
     */

    @Path("/{providerId : \\d+}+{serviceId : \\d+}/service-points")
    public ServicePointResource getServicePointResource() {
        return new ServicePointResource();
    }

    @Path("/{providerId : \\d+}+{serviceId : \\d+}/work-stations")
    public WorkStationResource getWorkStationResource() { return new WorkStationResource(); }

    @Path("/{providerId : \\d+}+{serviceId : \\d+}/employees")
    public EmployeeResource getEmployeeResource() { return new EmployeeResource(); }

    /**
     * This method enables to populate list of resources and each individual resource on list
     * with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList providerServices, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(providerServices, uriInfo, offset, limit);

        try {
            // count resources hypermedia link
            Method countMethod = ProviderServiceResource.class.getMethod("countProviderServices", GenericBeanParam.class);
            providerServices.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderServiceResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            providerServices.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderServiceResource.class).build()).rel("provider-services").build() );

            // get all resources eagerly hypermedia link
            Method providerServicesEagerlyMethod = ProviderServiceResource.class.getMethod("getProviderServicesEagerly", ProviderServiceBeanParam.class);
            providerServices.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                            .path(ProviderServiceResource.class)
                            .path(providerServicesEagerlyMethod)
                            .build())
                            .rel("provider-services-eagerly").build());

            // get subset of resources hypermedia links
            // described
            Method providerServicesByDescriptionMethod = ProviderServiceResource.class.getMethod("getProviderServicesByDescription", String.class, PaginationBeanParam.class);
            providerServices.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderServiceResource.class).path(providerServicesByDescriptionMethod).build()).rel("described").build() );

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

        for(Employee employee : providerServiceWrapper.getEmployees())
            pl.salonea.jaxrs.EmployeeResource.populateWithHATEOASLinks(employee, uriInfo);

        for(WorkStation workStation : providerServiceWrapper.getWorkStations())
            pl.salonea.jaxrs.WorkStationResource.populateWithHATEOASLinks(workStation, uriInfo);
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
             * Service Points where is executed current Provider Service resource
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

            // service-points eagerly relationship
            Method servicePointsEagerlyMethod = ProviderServiceResource.ServicePointResource.class.getMethod("getProviderServiceServicePointsEagerly", Long.class, Integer.class, ServicePointBeanParam.class);
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderServiceResource.class)
                    .path(servicePointsMethod)
                    .path(servicePointsEagerlyMethod)
                    .resolveTemplate("providerId", providerService.getProvider().getUserId().toString())
                    .resolveTemplate("serviceId", providerService.getService().getServiceId().toString())
                    .build())
                    .rel("service-points-eagerly").build());

            // service-points count link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/count
            Method countServicePointsByProviderServiceMethod = ProviderServiceResource.ServicePointResource.class.getMethod("countServicePointsByProviderService", Long.class, Integer.class, GenericBeanParam.class);
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderServiceResource.class)
                    .path(servicePointsMethod)
                    .path(countServicePointsByProviderServiceMethod)
                    .resolveTemplate("providerId", providerService.getProvider().getUserId().toString())
                    .resolveTemplate("serviceId", providerService.getService().getServiceId().toString())
                    .build())
                    .rel("service-points-count").build());

            // service-points address link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/address
            Method addressMethod = ProviderServiceResource.ServicePointResource.class.getMethod("getProviderServiceServicePointsByAddress", Long.class, Integer.class, AddressBeanParam.class);
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderServiceResource.class)
                    .path(servicePointsMethod)
                    .path(addressMethod)
                    .resolveTemplate("providerId", providerService.getProvider().getUserId().toString())
                    .resolveTemplate("serviceId", providerService.getService().getServiceId().toString())
                    .build())
                    .rel("service-points-address").build());

            // service-points coordinates-square link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/coordinates-square
            Method coordinatesSquareMethod = ProviderServiceResource.ServicePointResource.class.getMethod("getProviderServiceServicePointsByCoordinatesSquare", Long.class, Integer.class, CoordinatesSquareBeanParam.class);
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderServiceResource.class)
                    .path(servicePointsMethod)
                    .path(coordinatesSquareMethod)
                    .resolveTemplate("providerId", providerService.getProvider().getUserId().toString())
                    .resolveTemplate("serviceId", providerService.getService().getServiceId().toString())
                    .build())
                    .rel("service-points-coordinates-square").build());

            // service-points coordinates-circle link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/coordinates-circle
            Method coordinatesCircleMethod = ProviderServiceResource.ServicePointResource.class.getMethod("getProviderServiceServicePointsByCoordinatesCircle", Long.class, Integer.class, CoordinatesCircleBeanParam.class);
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderServiceResource.class)
                    .path(servicePointsMethod)
                    .path(coordinatesCircleMethod)
                    .resolveTemplate("providerId", providerService.getProvider().getUserId().toString())
                    .resolveTemplate("serviceId", providerService.getService().getServiceId().toString())
                    .build())
                    .rel("service-points-coordinates-circle").build());

            /**
             * Work Stations on which is executed current Provider Service resource
             */
            // work-stations
            Method workStationsMethod = ProviderServiceResource.class.getMethod("getWorkStationResource");
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                            .path(ProviderServiceResource.class)
                            .path(workStationsMethod)
                            .resolveTemplate("providerId", providerService.getProvider().getUserId().toString())
                            .resolveTemplate("serviceId", providerService.getService().getServiceId().toString())
                            .build())
                            .rel("work-stations").build());

            // work-stations eagerly
            Method workStationsEagerlyMethod = ProviderServiceResource.WorkStationResource.class.getMethod("getProviderServiceWorkStationsEagerly", Long.class, Integer.class, WorkStationBeanParam.class);
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                            .path(ProviderServiceResource.class)
                            .path(workStationsMethod)
                            .path(workStationsEagerlyMethod)
                            .resolveTemplate("providerId", providerService.getProvider().getUserId().toString())
                            .resolveTemplate("serviceId", providerService.getService().getServiceId().toString())
                            .build())
                            .rel("work-stations-eagerly").build());

            // work-stations count
            Method countWorkStationsByProviderServiceMethod = ProviderServiceResource.WorkStationResource.class.getMethod("countWorkStationsByProviderService", Long.class, Integer.class, GenericBeanParam.class);
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                            .path(ProviderServiceResource.class)
                            .path(workStationsMethod)
                            .path(countWorkStationsByProviderServiceMethod)
                            .resolveTemplate("providerId", providerService.getProvider().getUserId().toString())
                            .resolveTemplate("serviceId", providerService.getService().getServiceId().toString())
                            .build())
                            .rel("work-stations-count").build());

            // work-stations by-term
            Method workStationsByTermMethod = ProviderServiceResource.WorkStationResource.class.getMethod("getProviderServiceWorkStationsByTerm", Long.class, Integer.class, DateBetweenBeanParam.class);
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                            .path(ProviderServiceResource.class)
                            .path(workStationsMethod)
                            .path(workStationsByTermMethod)
                            .resolveTemplate("providerId", providerService.getProvider().getUserId().toString())
                            .resolveTemplate("serviceId", providerService.getService().getServiceId().toString())
                            .build())
                            .rel("work-stations-by-term").build());

            // work-stations by-term-strict
            Method workStationsByTermStrictMethod = ProviderServiceResource.WorkStationResource.class.getMethod("getProviderServiceWorkStationsByTermStrict", Long.class, Integer.class, DateBetweenBeanParam.class);
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderServiceResource.class)
                    .path(workStationsMethod)
                    .path(workStationsByTermStrictMethod)
                    .resolveTemplate("providerId", providerService.getProvider().getUserId().toString())
                    .resolveTemplate("serviceId", providerService.getService().getServiceId().toString())
                    .build())
                    .rel("work-stations-by-term-strict").build());

            /**
             * Employees executing current Provider Service resource
             */
            // employees
            Method employeesMethod = ProviderServiceResource.class.getMethod("getEmployeeResource");
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                            .path(ProviderServiceResource.class)
                            .path(employeesMethod)
                            .resolveTemplate("providerId", providerService.getProvider().getUserId().toString())
                            .resolveTemplate("serviceId", providerService.getService().getServiceId().toString())
                            .build())
                            .rel("employees").build());

            // employees eagerly
            Method employeesEagerlyMethod = ProviderServiceResource.EmployeeResource.class.getMethod("getProviderServiceEmployeesEagerly", Long.class, Integer.class, EmployeeBeanParam.class);
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                            .path(ProviderServiceResource.class)
                            .path(employeesMethod)
                            .path(employeesEagerlyMethod)
                            .resolveTemplate("providerId", providerService.getProvider().getUserId().toString())
                            .resolveTemplate("serviceId", providerService.getService().getServiceId().toString())
                            .build())
                            .rel("employees-eagerly").build());

            // employees count
            Method countEmployeesByProviderServiceMethod = ProviderServiceResource.EmployeeResource.class.getMethod("countEmployeesByProviderService", Long.class, Integer.class, GenericBeanParam.class);
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderServiceResource.class)
                    .path(employeesMethod)
                    .path(countEmployeesByProviderServiceMethod)
                    .resolveTemplate("providerId", providerService.getProvider().getUserId().toString())
                    .resolveTemplate("serviceId", providerService.getService().getServiceId().toString())
                    .build())
                    .rel("employees-count").build());

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

        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServiceServicePointsEagerly( @PathParam("providerId") Long providerId,
                                                                @PathParam("serviceId") Integer serviceId,
                                                                @BeanParam ServicePointBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
            /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Service Point entities for given Provider Service eagerly using " +
                    "ProviderServiceResource.ServicePointResource.getProviderServiceServicePointsEagerly(providerId, serviceId) method of REST API");

            utx.begin();

            // find provider service entity for which to get associated service points
            ProviderService providerService = providerServiceFacade.find(new ProviderServiceId(providerId, serviceId));
            if(providerService == null)
                throw new NotFoundException("Could not find provider service for id (" + providerId + "," + serviceId + ").");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ServicePointWrapper> servicePoints = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<ProviderService> providerServices = new ArrayList<>();
                providerServices.add(providerService);

                if(params.getAddress() != null) {
                    if(params.getCoordinatesSquare() != null || params.getCoordinatesCircle() != null)
                        throw new BadRequestException("Query params cannot include address params and coordinates square params or coordinates circle params at the same time.");
                    // only address params
                    servicePoints = new ResourceList<>(
                            ServicePointWrapper.wrap(
                                    servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getServices(), providerServices, params.getEmployees(),
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
                                    servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getServices(), providerServices, params.getEmployees(),
                                            params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesSquare(),
                                            params.getOffset(), params.getLimit())
                            )
                    );
                } else if(params.getCoordinatesCircle() != null) {
                    if(params.getAddress() != null || params.getCoordinatesSquare() != null)
                        throw new BadRequestException("Query params cannot include coordinates circle params and address params or coordinates square params at the same time.");
                    // only coordinates circle params
                    servicePoints = new ResourceList<>(
                            ServicePointWrapper.wrap(
                                    servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getServices(), providerServices, params.getEmployees(),
                                            params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesCircle(),
                                            params.getOffset(), params.getLimit())
                            )
                    );
                } else {
                    // no location params
                    servicePoints = new ResourceList<>(
                            ServicePointWrapper.wrap(
                                    servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getServices(), providerServices, params.getEmployees(),
                                            params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getOffset(), params.getLimit())
                            )
                    );
                }
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                servicePoints = new ResourceList<>( ServicePointWrapper.wrap(servicePointFacade.findByProviderServiceEagerly(providerService, params.getOffset(), params.getLimit())) );
            }

            utx.commit();

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
         * Method that counts Service Point entities for given Provider Service resource.
         * The provider service composite id is passed through path params.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countServicePointsByProviderService( @PathParam("providerId") Long providerId,
                                                             @PathParam("serviceId") Integer serviceId,
                                                             @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of service points for given provider service by executing " +
                    "ProviderServiceResource.ServicePointResource.countServicePointsByProviderService(providerId, serviceId) method of REST API");

            utx.begin();

            // find provider service entity for which to count service points
            ProviderService providerService = providerServiceFacade.find(new ProviderServiceId(providerId, serviceId));
            if(providerService == null)
                throw new NotFoundException("Could not find provider service for id (" + providerId + "," + serviceId + ").");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(servicePointFacade.countByProviderService(providerService)), 200,
                    "number of service points for provider service with id (" + providerService.getProvider().getUserId() + "," + providerService.getService().getServiceId() + ").");
            utx.commit();

            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method returns subset of Service Point entities for given Provider Service entity and
         * Address related query params. The provider service composite id is passed through path param.
         * Address params are passed through query params.
         */
        @GET
        @Path("/address")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServiceServicePointsByAddress( @PathParam("providerId") Long providerId,
                                                                  @PathParam("serviceId") Integer serviceId,
                                                                  @BeanParam AddressBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning service points for given provider service and address related params using " +
                    "ProviderServiceResource.ServicePointResource.getProviderServiceServicePointsByAddress(providerId, serviceId, address) method of REST API");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);
            if(noOfParams < 1)
                throw new BadRequestException("There is no address related query param in request.");

            utx.begin();

            // find provider service entity for which to get associated service points
            ProviderService providerService = providerServiceFacade.find(new ProviderServiceId(providerId, serviceId));
            if(providerService == null)
                throw new NotFoundException("Could not find provider service for id (" + providerId + "," + serviceId + ").");

            // find service points by given criteria
            ResourceList<ServicePoint> servicePoints = new ResourceList<>(
                    servicePointFacade.findByProviderServiceAndAddress(providerService, params.getCity(), params.getState(), params.getCountry(),
                            params.getStreet(), params.getZipCode(), params.getOffset(), params.getLimit())
            );

            utx.commit();

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(servicePoints).build();
        }

        /**
         * Method returns subset of Service Point entities for given Provider Service entity and
         * Coordinates Square related params. The provider service composite id is passed through path param.
         * Coordinates Square params are passed through query params.
         */
        @GET
        @Path("/coordinates-square")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServiceServicePointsByCoordinatesSquare( @PathParam("providerId") Long providerId,
                                                                            @PathParam("serviceId") Integer serviceId,
                                                                            @BeanParam CoordinatesSquareBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning service points for given provider service and coordinates square params using " +
                    "ProviderServiceResource.ServicePointResource.getProviderServiceServicePointsByCoordinatesSquare(providerId, serviceId, coordinatesSquare) method of REST API");

            if(params.getMinLongitudeWGS84() == null || params.getMinLatitudeWGS84() == null ||
                    params.getMaxLongitudeWGS84() == null || params.getMaxLatitudeWGS84() == null)
                throw new BadRequestException("All coordinates square query params must be specified.");

            utx.begin();

            // find provider service entity for which to get associated service points
            ProviderService providerService = providerServiceFacade.find(new ProviderServiceId(providerId, serviceId));
            if(providerService == null)
                throw new NotFoundException("Could not find provider service for id (" + providerId + "," + serviceId + ").");

            // find service points by given criteria
            ResourceList<ServicePoint> servicePoints = new ResourceList<>(
                    servicePointFacade.findByProviderServiceAndCoordinatesSquare(providerService, params.getMinLongitudeWGS84(), params.getMinLatitudeWGS84(),
                            params.getMaxLongitudeWGS84(), params.getMaxLatitudeWGS84(), params.getOffset(), params.getLimit())
            );

            utx.commit();

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(servicePoints).build();
        }

        /**
         * Method returns subset of Service Point entities for given Provider Service entity and
         * Coordinates Circle related params. The provider service composite id is passed through path param.
         * Coordinates Circle params are passed through query params.
         */
        @GET
        @Path("/coordinates-circle")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServiceServicePointsByCoordinatesCircle( @PathParam("providerId") Long providerId,
                                                                            @PathParam("serviceId") Integer serviceId,
                                                                            @BeanParam CoordinatesCircleBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning service points for given provider service and coordinates circle params using " +
                    "ProviderServiceResource.ServicePointResource.getProviderServiceServicePointsByCoordinatesCircle(providerId, serviceId, coordinatesCircle) method of REST API");

            if (params.getLongitudeWGS84() == null || params.getLatitudeWGS84() == null || params.getRadius() == null)
                throw new BadRequestException("All coordinates circle query params must be specified.");

            utx.begin();

            // find provider service entity for which to get associated service points
            ProviderService providerService = providerServiceFacade.find(new ProviderServiceId(providerId, serviceId));
            if(providerService == null)
                throw new NotFoundException("Could not find provider service for id (" + providerId + "," + serviceId + ").");

            // find service points by given criteria
            ResourceList<ServicePoint> servicePoints = new ResourceList<>(
                    servicePointFacade.findByProviderServiceAndCoordinatesCircle(providerService, params.getLongitudeWGS84(),
                            params.getLatitudeWGS84(), params.getRadius(), params.getOffset(), params.getLimit())
            );

            utx.commit();

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(servicePoints).build();
        }
    }

    public class WorkStationResource {

        public WorkStationResource() { }


        /**
         * Method returns subset of Work Station entities for given Provider Service.
         * The provider id and service id are passed through path params.
         * They can be additionally filtered and paginated by query params.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServiceWorkStations( @PathParam("providerId") Long providerId,
                                                        @PathParam("serviceId") Integer serviceId,
                                                        @BeanParam WorkStationBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);

            // TODO
            return null;
        }

        /**
         * Method returns subset of Work Station entities for given Provider Service
         * fetching them eagerly. The provider id and service id are passed through
         * path params. They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServiceWorkStationsEagerly( @PathParam("providerId") Long providerId,
                                                               @PathParam("serviceId") Integer serviceId,
                                                               @BeanParam WorkStationBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);

            // TODO
            return null;
        }

        /**
         * Method that counts Work Station entities for given Provider Service resource.
         * The provider id and service id are passed through path params.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countWorkStationsByProviderService( @PathParam("providerId") Long providerId,
                                                            @PathParam("serviceId") Integer serviceId,
                                                            @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            // TODO
            return null;
        }

        /**
         * Method returns subset of Work Station entities for given Provider Service entity
         * and Term when it is provided on them. The provider id and service id are passed
         * through path params. Term start and end dates are passed through query params.
         */
        @GET
        @Path("/by-term")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServiceWorkStationsByTerm( @PathParam("providerId") Long providerId,
                                                              @PathParam("serviceId") Integer serviceId,
                                                              @BeanParam DateBetweenBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);

            // TODO
            return null;
        }

        /**
         * Method returns subset of Work Station entities for given Provider Service entity
         * and Term (strict) when it is provided on them. The provider id and service id
         * are passed through path params. Term (strict) start and end dates are passed through query params.
         */
        @GET
        @Path("/by-term-strict")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServiceWorkStationsByTermStrict( @PathParam("providerId") Long providerId,
                                                                    @PathParam("serviceId") Integer serviceId,
                                                                    @BeanParam DateBetweenBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);

            // TODO
            return null;
        }

    }

    public class EmployeeResource {

        public EmployeeResource() { }

        /**
         * Method returns subset of Employee entities for given Provider Service entity.
         * The provider id and service id are passed through path params.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServiceEmployees( @PathParam("providerId") Long providerId,
                                                     @PathParam("serviceId") Integer serviceId,
                                                     @BeanParam EmployeeBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employees for given provider service using " +
                    "ProviderServiceResource.EmployeeResource.getProviderServiceEmployees(providerId, serviceId) method of REST API");

            utx.begin();

            // find provider service entity for which to get associated employees
            ProviderService providerService = providerServiceFacade.find(new ProviderServiceId(providerId, serviceId));
            if(providerService == null)
                throw new NotFoundException("Could not find provider service for id (" + providerId + "," + serviceId + ").");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<Employee> employees = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<ProviderService> providerServices = new ArrayList<>();
                providerServices.add(providerService);

                // get employees for given provider service filtered by given params
                employees = new ResourceList<>(
                        employeeFacade.findByMultipleCriteria(params.getDescriptions(), params.getJobPositions(), params.getSkills(),
                                params.getEducations(), params.getServices(), providerServices, params.getServicePoints(),
                                params.getWorkStations(), params.getPeriod(), params.getStrictTerm(), params.getRated(),
                                params.getMinAvgRating(), params.getMaxAvgRating(), params.getRatingClients(), params.getOffset(), params.getLimit())
                );
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get employees for given provider service without filtering (eventually paginated)
                employees = new ResourceList<>( employeeFacade.findByProviderService(providerService, params.getOffset(), params.getLimit()) );
            }

            utx.commit();

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeResource.populateWithHATEOASLinks(employees, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employees).build();
        }

        /**
         * Method returns subset of Employee entities for given Provider Service fetching them eagerly.
         * The provider id and service id are passed through path params.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServiceEmployeesEagerly( @PathParam("providerId") Long providerId,
                                                            @PathParam("serviceId") Integer serviceId,
                                                            @BeanParam EmployeeBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employees eagerly for given provider service using " +
                    "ProviderServiceResource.EmployeeResource.getProviderServiceEmployeesEagerly(providerId, serviceId) method of REST API");

            utx.begin();

            // find provider service entity for which to get associated employees
            ProviderService providerService = providerServiceFacade.find(new ProviderServiceId(providerId, serviceId));
            if(providerService == null)
                throw new NotFoundException("Could not find provider service for id (" + providerId + "," + serviceId + ").");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<EmployeeWrapper> employees = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<ProviderService> providerServices = new ArrayList<>();
                providerServices.add(providerService);

                // get employees eagerly for given provider service filtered by given params
                employees = new ResourceList<>(
                        EmployeeWrapper.wrap(
                                employeeFacade.findByMultipleCriteriaEagerly(params.getDescriptions(), params.getJobPositions(),
                                        params.getSkills(), params.getEducations(), params.getServices(), providerServices,
                                        params.getServicePoints(), params.getWorkStations(), params.getPeriod(), params.getStrictTerm(),
                                        params.getRated(), params.getMinAvgRating(), params.getMaxAvgRating(), params.getRatingClients(),
                                        params.getOffset(), params.getLimit())
                        )
                );
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get employees eagerly for given provider service without filtering (eventually paginated)
                employees = new ResourceList<>( EmployeeWrapper.wrap(employeeFacade.findByProviderServiceEagerly(providerService, params.getOffset(), params.getLimit())) );
            }

            utx.commit();

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeResource.populateWithHATEOASLinks(employees, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employees).build();
        }

        /**
         * Method counts Employee entities for given Provider Service entity.
         * The provider id and service id are passed through path params.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countEmployeesByProviderService( @PathParam("providerId") Long providerId,
                                                         @PathParam("serviceId") Integer serviceId,
                                                         @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of employees for given provider service by executing " +
                    "ProviderServiceResource.EmployeeResource.countEmployeesByProviderService(providerId, serviceId) method of REST API");

            utx.begin();

            // find provider service entity for which to count employees
            ProviderService providerService = providerServiceFacade.find( new ProviderServiceId(providerId, serviceId) );
            if(providerService == null)
                throw new NotFoundException("Could not find provider service for id (" + providerId + "," + serviceId + ").");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(employeeFacade.countByProviderService(providerService)),
                    200, "number of employees for provider service with id (" + providerId + "," + serviceId + ").");

            utx.commit();

            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

}
