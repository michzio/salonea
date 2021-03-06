package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.*;
import pl.salonea.entities.*;
import pl.salonea.entities.Transaction;
import pl.salonea.jaxrs.bean_params.*;
import pl.salonea.jaxrs.exceptions.*;
import pl.salonea.jaxrs.exceptions.BadRequestException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.*;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.transaction.*;
import javax.transaction.NotSupportedException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
import java.net.URI;
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
    private UserTransaction utx;

    @Inject
    private ServiceFacade serviceFacade;
    @Inject
    private ProviderFacade providerFacade;
    @Inject
    private ServicePointFacade servicePointFacade;
    @Inject
    private EmployeeFacade employeeFacade;
    @Inject
    private WorkStationFacade workStationFacade;
    @Inject
    private ProviderServiceFacade providerServiceFacade;
    @Inject
    private EmployeeTermFacade employeeTermFacade;
    @Inject
    private TermFacade termFacade;
    @Inject
    private TransactionFacade transactionFacade;
    @Inject
    private HistoricalTransactionFacade historicalTransactionFacade;

    /**
     * Method returns all Service resources
     * They can be additionally filtered or paginated by @QueryParams.
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getServices( @BeanParam ServiceBeanParam params ) throws ForbiddenException, BadRequestException,
    /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Services by executing ServiceResource.getServices() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<Service> services = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            utx.begin();

            // get all services filtered by given query params

            if( RESTToolkit.isSet(params.getKeywords()) ) {
                if( RESTToolkit.isSet(params.getNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                    throw new BadRequestException("Query params cannot include keywords and service names or descriptions at the same time.");

                // find only by keywords
                services = new ResourceList<>(
                        serviceFacade.findByMultipleCriteria(params.getKeywords(), params.getServiceCategories(), params.getProviders(),
                                params.getEmployees(), params.getWorkStations(), params.getServicePoints(), params.getEmployeeTerms(),
                                params.getTerms(),params.getOffset(), params.getLimit())
                );
            } else {
                // find by service names and descriptions
                services = new ResourceList<>(
                        serviceFacade.findByMultipleCriteria(params.getNames(), params.getDescriptions(), params.getServiceCategories(),
                                params.getProviders(), params.getEmployees(), params.getWorkStations(), params.getServicePoints(),
                                params.getEmployeeTerms(), params.getTerms(), params.getOffset(), params.getLimit())
                );
            }

            utx.commit();

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all services without filtering (eventually paginated)
            services = new ResourceList<>( serviceFacade.findAll(params.getOffset(), params.getLimit()) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        ServiceResource.populateWithHATEOASLinks(services, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(services).build();
    }

    @GET
    @Path("/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getServicesEagerly( @BeanParam ServiceBeanParam params ) throws ForbiddenException, BadRequestException,
    /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Services eagerly by executing ServiceResource.getServicesEagerly() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<ServiceWrapper> services = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            utx.begin();

            // get all services eagerly filtered by given query params

            if( RESTToolkit.isSet(params.getKeywords()) ) {
                if (RESTToolkit.isSet(params.getNames()) || RESTToolkit.isSet(params.getDescriptions()))
                    throw new BadRequestException("Query params cannot include keywords and service names or descriptions at the same time.");

                // find only by keywords
                services = new ResourceList<>(
                        ServiceWrapper.wrap(
                                serviceFacade.findByMultipleCriteriaEagerly(params.getKeywords(), params.getServiceCategories(), params.getProviders(),
                                        params.getEmployees(), params.getWorkStations(), params.getServicePoints(), params.getEmployeeTerms(),
                                        params.getTerms(),params.getOffset(), params.getLimit())
                        )
                );
            } else {
                // find by service names and descriptions
                services = new ResourceList<>(
                        ServiceWrapper.wrap(
                                serviceFacade.findByMultipleCriteriaEagerly(params.getNames(), params.getDescriptions(), params.getServiceCategories(),
                                        params.getProviders(), params.getEmployees(), params.getWorkStations(), params.getServicePoints(),
                                        params.getEmployeeTerms(), params.getTerms(), params.getOffset(), params.getLimit())
                        )
                );
            }

            utx.commit();

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all services eagerly without filtering (eventually paginated)
            services = new ResourceList<>( ServiceWrapper.wrap(serviceFacade.findAllEagerly(params.getOffset(), params.getLimit())) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        ServiceResource.populateWithHATEOASLinks(services, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(services).build();
    }

    /**
     * Method matches specific Service resource by identifier and returns its instance.
     */
    @GET
    @Path("/{serviceId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getService( @PathParam("serviceId") Integer serviceId,
                                @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning given Service by executing ServiceResource.getService(serviceId) method of REST API");

        Service foundService = serviceFacade.find(serviceId);
        if(foundService == null)
            throw new NotFoundException("Could not find service for id " + serviceId + ".");

        // adding hypermedia links to service resource
        ServiceResource.populateWithHATEOASLinks(foundService, params.getUriInfo());

        return Response.status(Status.OK).entity(foundService).build();
    }

    /**
     * Method matches specific Service resource by identifier and returns its instance fetching it eagerly
     */
    @GET
    @Path("/{serviceId : \\d+}/eagerly") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getServiceEagerly( @PathParam("serviceId") Integer serviceId,
                                       @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning given Service eagerly by executing ServiceResource.getServiceEagerly(serviceId) method of REST API");

        Service foundService = serviceFacade.findByIdEagerly(serviceId);
        if(foundService == null)
            throw new NotFoundException("Could not find service for id " + serviceId + ".");

        // wrapping Service into ServiceWrapper in order to marshall eagerly fetched associated collections of entities
        ServiceWrapper wrappedService = new ServiceWrapper(foundService);

        // adding hypermedia links to wrapped service resource
        ServiceResource.populateWithHATEOASLinks(wrappedService, params.getUriInfo());

        return Response.status(Status.OK).entity(wrappedService).build();
    }

    /**
     * Method that takes Service as XML or JSON and creates its new instance in database
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createService( Service service,
                                   @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "creating new Service by executing ServiceResource.createService(service) method of REST API");

        Service createdService = null;
        URI locationURI = null;

        try {
            // persist new resource in database
            createdService = serviceFacade.create(service);

            // populate created resource with hypermedia links
            ServiceResource.populateWithHATEOASLinks(createdService, params.getUriInfo());

            // construct link to newly created resource to return in HTTP Header
            String createdServiceId = String.valueOf(createdService.getServiceId());
            locationURI = params.getUriInfo().getBaseUriBuilder().path(ServiceResource.class).path(createdServiceId).build();

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_CREATION_ERROR_MESSAGE);
        }

        return Response.created(locationURI).entity(createdService).build();
    }

    /**
     * Method that takes updated Service as XML or JSON and its ID as path param.
     * It updates Service in database for provided ID.
     */
    @PUT
    @Path("/{serviceId : \\d+}") // catch only numeric identifiers
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateService( @PathParam("serviceId") Integer serviceId,
                                   Service service,
                                   @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "updating existing Service by executing ServiceResource.updateService(serviceId, service) method of REST API");

        // set resource ID passed in path param on updated resource object
        service.setServiceId(serviceId);

        Service updatedService = null;
        try {
            // reflect updated resource object in database
            updatedService = serviceFacade.update(service, true);
            // populate created resource with hypermedia links
            ServiceResource.populateWithHATEOASLinks(updatedService, params.getUriInfo());

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_UPDATE_ERROR_MESSAGE);
        }

        return Response.status(Status.OK).entity(updatedService).build();
    }

    /**
     * Method that removes Service entity from database for given ID.
     * The ID is passed through path param.
     */
    @DELETE
    @Path("/{serviceId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeService( @PathParam("serviceId") Integer serviceId,
                                   @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing given Service by executing ServiceResource.removeService(serviceId) method of REST API");

        // find Service entity that should be deleted
        Service toDeleteService = serviceFacade.find(serviceId);
        // throw exception if entity hasn't been found
        if(toDeleteService == null)
            throw new NotFoundException("Could not find service to delete for given id: " + serviceId + ".");

        // remove entity from database
        serviceFacade.remove(toDeleteService);

        return Response.status(Status.NO_CONTENT).build();
    }

    /**
     * Additional methods returning a subset of resources based on given criteria.
     * You can also achieve similar results by applying @QueryParams to generic method
     * returning all resources in order to filter and limit them.
     */

    /**
     * Method returns number of Service entities in database
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countServices( @BeanParam GenericBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning number of services by executing ServiceResource.countServices() method of REST API");

        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(serviceFacade.count()), 200, "number of services");
        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method returns subset of Service entities for given service name.
     * The service name is passed through path param.
     */
    @GET
    @Path("/named/{serviceName : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getServicesByName( @PathParam("serviceName") String serviceName,
                                       @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning services for given service name using ServiceResource.getServicesByName(serviceName) method of REST API");

        // find services by given criteria
        ResourceList<Service> services = new ResourceList<>( serviceFacade.findByName(serviceName, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        ServiceResource.populateWithHATEOASLinks(services, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(services).build();
    }

    /**
     * Method returns subset of Service entities for given description.
     * The description is passed through path param.
     */
    @GET
    @Path("/described/{description : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getServicesByDescription( @PathParam("description") String description,
                                              @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning services for given description using ServiceResource.getServicesByDescription(description) method of REST API");

        // find services by given criteria
        ResourceList<Service> services = new ResourceList<>( serviceFacade.findByDescription(description, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        ServiceResource.populateWithHATEOASLinks(services, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(services).build();
    }

    /**
     * Method returns subset of Service entities for given keyword.
     * The keyword is passed through path param.
     */
    @GET
    @Path("/containing-keyword/{keyword : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getServicesByKeyword( @PathParam("keyword") String keyword,
                                          @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning services for given keyword using ServiceResource.getServicesByKeyword(keyword) method of REST API");

        // find services by given criteria
        ResourceList<Service> services = new ResourceList<>( serviceFacade.findByKeyword(keyword, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        ServiceResource.populateWithHATEOASLinks(services, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(services).build();
    }

    /**
     * related subresources (through relationships)
     */

    @Path("/{serviceId : \\d+}/providers")
    public ProviderResource getProviderResource() {
        return new ProviderResource();
    }
    @Path("/{serviceId : \\d+}/provider-services")
    public ProviderServiceResource getProviderServiceResource() {
        return new ProviderServiceResource();
    }
    @Path("/{serviceId : \\d+}/service-points")
    public ServicePointResource getServicePointResource() {
        return new ServicePointResource();
    }
    @Path("/{serviceId : \\d+}/work-stations")
    public WorkStationResource getWorkStationResource() {
        return new WorkStationResource();
    }
    @Path("/{serviceId : \\d+}/employees")
    public EmployeeResource getEmployeeResource() {
        return new EmployeeResource();
    }
    @Path("/{serviceId : \\d+}/employee-terms")
    public EmployeeTermResource getEmployeeTermResource() { return new EmployeeTermResource(); }
    @Path("/{serviceId : \\d+}/terms")
    public TermResource getTermResource() { return new TermResource(); }
    @Path("/{serviceId : \\d+}/transactions")
    public TransactionResource getTransactionResource() { return new TransactionResource(); }
    @Path("/{serviceId : \\d+}/historical-transactions")
    public HistoricalTransactionResource getHistoricalTransactionResource() { return new HistoricalTransactionResource(); }

    // helper methods e.g. to populate resources/resource lists with HATEOAS links

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList services, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(services, uriInfo, offset, limit);

        try {
            // count resources hypermedia link
            Method countMethod = ServiceResource.class.getMethod("countServices", GenericBeanParam.class);
            services.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ServiceResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            services.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ServiceResource.class).build()).rel("services").build() );

            // get all resources eagerly hypermedia link
            Method servicesEagerlyMethod = ServiceResource.class.getMethod("getServicesEagerly", ServiceBeanParam.class);
            services.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ServiceResource.class).path(servicesEagerlyMethod).build()).rel("services-eagerly").build() );

            // get subset of resources hypermedia links

            // named
            services.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path("named")
                    .build())
                    .rel("named").build() );

            // described
            services.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path("described")
                    .build())
                    .rel("described").build() );

            // containing-keyword
            services.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path("containing-keyword")
                    .build())
                    .rel("containing-keyword").build() );

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
            Method serviceEagerlyMethod = ServiceResource.class.getMethod("getServiceEagerly", Integer.class, GenericBeanParam.class);
            service.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                        .path(ServiceResource.class)
                                                        .path(serviceEagerlyMethod)
                                                        .resolveTemplate("serviceId", service.getServiceId().toString())
                                                        .build())
                                        .rel("service-eagerly").build() );

            // associated collections links with pattern: http://localhost:port/app/rest/{resources}/{id}/{relationship}

            /**
             * Providers providing current Service resource
             */

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

            // providers count
            Method countProvidersByServiceMethod = ServiceResource.ProviderResource.class.getMethod("countProvidersByService", Integer.class, GenericBeanParam.class);
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(providersMethod)
                    .path(countProvidersByServiceMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("providers-count").build());

            /**
             * Provider Services offered for current Service resource
             */

            // provider-services
            Method providerServicesMethod = ServiceResource.class.getMethod("getProviderServiceResource");
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(providerServicesMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("provider-services").build());

            // provider-services eagerly
            Method providerServicesEagerlyMethod = ServiceResource.ProviderServiceResource.class.getMethod("getServiceProviderServicesEagerly", Integer.class, ProviderServiceBeanParam.class);
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(providerServicesMethod)
                    .path(providerServicesEagerlyMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("provider-services-eagerly").build());

            // provider-services count
            Method countProviderServicesByServiceMethod = ServiceResource.ProviderServiceResource.class.getMethod("countProviderServicesByService", Integer.class, GenericBeanParam.class);
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(providerServicesMethod)
                    .path(countProviderServicesByServiceMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("provider-services-count").build());

            // provider-services described
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(providerServicesMethod)
                    .path("described")
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("provider-services-described").build());

            // provider-services priced
            Method providerServicesByPriceMethod = ServiceResource.ProviderServiceResource.class.getMethod("getServiceProviderServicesByPrice", Integer.class, PriceRangeBeanParam.class);
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(providerServicesMethod)
                    .path(providerServicesByPriceMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("provider-services-priced").build());

            // provider-services discounted-priced
            Method providerServicesByDiscountedPriceMethod = ServiceResource.ProviderServiceResource.class.getMethod("getServiceProviderServicesByDiscountedPrice", Integer.class, PriceRangeBeanParam.class);
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(providerServicesMethod)
                    .path(providerServicesByDiscountedPriceMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("provider-services-discounted-priced").build());

            // provider-services discounted
            Method providerServicesByDiscountMethod = ServiceResource.ProviderServiceResource.class.getMethod("getServiceProviderServicesByDiscount", Integer.class, DiscountRangeBeanParam.class);
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(providerServicesMethod)
                    .path(providerServicesByDiscountMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("provider-services-discounted").build());

            /**
             * Service Points where current Service resource is provided
             */

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

            /**
             * Work Stations where current Service resource is provided
             */

            // work-stations
            Method workStationsMethod = ServiceResource.class.getMethod("getWorkStationResource");
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(workStationsMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("work-stations").build());

            // work-stations eagerly
            Method workStationsEagerlyMethod = ServiceResource.WorkStationResource.class.getMethod("getServiceWorkStationsEagerly", Integer.class, WorkStationBeanParam.class);
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(workStationsMethod)
                    .path(workStationsEagerlyMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("work-stations-eagerly").build());

            // work-stations count
            Method countWorkStationsByServiceMethod = ServiceResource.WorkStationResource.class.getMethod("countWorkStationsByService", Integer.class, GenericBeanParam.class);
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(workStationsMethod)
                    .path(countWorkStationsByServiceMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("work-stations-count").build());

            // work-stations by-term
            Method workStationsByTermMethod = ServiceResource.WorkStationResource.class.getMethod("getServiceWorkStationsByTerm", Integer.class, DateRangeBeanParam.class);
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(workStationsMethod)
                    .path(workStationsByTermMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("work-stations-by-term").build());

            // work-stations by-term-strict
            Method workStationsByTermStrictMethod = ServiceResource.WorkStationResource.class.getMethod("getServiceWorkStationsByTermStrict", Integer.class, DateRangeBeanParam.class);
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(workStationsMethod)
                    .path(workStationsByTermStrictMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("work-stations-by-term-strict").build());

            /**
             * Employees executing current Service resource
             */
            // employees
            Method employeesMethod = ServiceResource.class.getMethod("getEmployeeResource");
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(employeesMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("employees").build());

            // employees eagerly
            Method employeesEagerlyMethod = ServiceResource.EmployeeResource.class.getMethod("getServiceEmployeesEagerly", Integer.class, EmployeeBeanParam.class);
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(employeesMethod)
                    .path(employeesEagerlyMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("employees-eagerly").build());

            // employees count
            Method countEmployeesByServiceMethod = ServiceResource.EmployeeResource.class.getMethod("countEmployeesByService", Integer.class, GenericBeanParam.class);
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(employeesMethod)
                    .path(countEmployeesByServiceMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("employees-count").build());

            /**
             * Employee Terms when current Service resource is executed
             */
            // employee-terms
            Method employeeTermsMethod = ServiceResource.class.getMethod("getEmployeeTermResource");
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(employeeTermsMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("employee-terms").build() );

            // employee-terms count
            Method countEmployeeTermsByServiceMethod = ServiceResource.EmployeeTermResource.class.getMethod("countEmployeeTermsByService", Integer.class, GenericBeanParam.class);
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(employeeTermsMethod)
                    .path(countEmployeeTermsByServiceMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("employee-terms-count").build() );

            /**
             * Terms when current Service resource is executed
             */

            // terms
            Method termsMethod = ServiceResource.class.getMethod("getTermResource");
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(termsMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("terms").build() );

            // terms eagerly
            Method termsEagerlyMethod = ServiceResource.TermResource.class.getMethod("getServiceTermsEagerly", Integer.class, TermBeanParam.class);
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(termsMethod)
                    .path(termsEagerlyMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("terms-eagerly").build() );

            // terms count
            Method countTermsByServiceMethod = ServiceResource.TermResource.class.getMethod("countTermsByService", Integer.class, GenericBeanParam.class);
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(termsMethod)
                    .path(countTermsByServiceMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("terms-count").build() );

            /**
             * Transactions associated with current Service resource
             */
            // transactions
            Method transactionsMethod = ServiceResource.class.getMethod("getTransactionResource");
            service.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(transactionsMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("transactions").build() );

            // transactions eagerly
            Method transactionsEagerlyMethod = ServiceResource.TransactionResource.class.getMethod("getServiceTransactionsEagerly", Integer.class, TransactionBeanParam.class);
            service.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(transactionsMethod)
                    .path(transactionsEagerlyMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("transactions-eagerly").build() );

            // transactions count
            Method countTransactionsByServiceMethod = ServiceResource.TransactionResource.class.getMethod("countTransactionsByService", Integer.class, GenericBeanParam.class);
            service.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(transactionsMethod)
                    .path(countTransactionsByServiceMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("transactions-count").build() );

            /**
             * Historical Transactions associated with current Service resource
             */
            // historical-transactions
            Method historicalTransactionsMethod = ServiceResource.class.getMethod("getHistoricalTransactionResource");
            service.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(historicalTransactionsMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("historical-transactions").build());

            // historical-transactions eagerly
            Method historicalTransactionsEagerlyMethod = ServiceResource.HistoricalTransactionResource.class.getMethod("getServiceHistoricalTransactionsEagerly", Integer.class, HistoricalTransactionBeanParam.class);
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(historicalTransactionsMethod)
                    .path(historicalTransactionsEagerlyMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("historical-transactions-eagerly").build());

            // historical-transactions count
            Method countHistoricalTransactionsByServiceMethod = ServiceResource.HistoricalTransactionResource.class.getMethod("countHistoricalTransactionsByService", Integer.class, GenericBeanParam.class);
            service.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServiceResource.class)
                    .path(historicalTransactionsMethod)
                    .path(countHistoricalTransactionsByServiceMethod)
                    .resolveTemplate("serviceId", service.getServiceId().toString())
                    .build())
                    .rel("historical-transactions-count").build());

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

        /**
         * Method counts Provider entities for given Service entity.
         * The service id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countProvidersByService( @PathParam("serviceId") Integer serviceId,
                                                 @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of providers for given service by executing " +
                    "ServiceResource.ProviderResource.countProvidersByService(serviceId) method of REST API");

            // find service entity for which to count providers
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(providerFacade.countByService(service)), 200,
                    "number of providers for service with id " + serviceId + ".");
            return Response.status(Status.OK).entity(responseEntity).build();
        }

    }

    public class ProviderServiceResource {

        public ProviderServiceResource() { }

        /**
         * Method returns subset of ProviderService entities for given Service
         * The service id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceProviderServices( @PathParam("serviceId") Integer serviceId,
                                                    @BeanParam ProviderServiceBeanParam params ) throws NotFoundException, ForbiddenException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Provider Service entities for given Service using " +
                    "ServiceResource.ProviderServiceResource.getServiceProviderServices(serviceId) method of REST API");

            // find service entity for which to get associated provider services
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            // calculate number of filter query params
            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ProviderService> providerServices = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Service> services = new ArrayList<>();
                services.add(service);

                utx.begin();

                // get provider services for given service filtered by given params
                providerServices = new ResourceList<>(
                        providerServiceFacade.findByMultipleCriteria(params.getProviders(), services, params.getServiceCategories(),
                                params.getDescriptions(), params.getMinPrice(), params.getMaxPrice(), params.getIncludeDiscounts(),
                                params.getMinDiscount(), params.getMaxDiscount(), params.getMinDuration(), params.getMaxDuration(),
                                params.getServicePoints(), params.getWorkStations(), params.getEmployees(), params.getEmployeeTerms(),
                                params.getTerms(), params.getOffset(), params.getLimit())
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get provider services for given service without filtering (eventually paginated)
                providerServices = new ResourceList<>(providerServiceFacade.findByService(service, params.getOffset(), params.getLimit()));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(providerServices, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providerServices).build();
        }

        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceProviderServicesEagerly( @PathParam("serviceId") Integer serviceId,
                                                           @BeanParam ProviderServiceBeanParam params ) throws NotFoundException, ForbiddenException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Provider Service entities for given Service eagerly using " +
                    "ServiceResource.ProviderServiceResource.getServiceProviderServicesEagerly(serviceId) method of REST API");

            // find service entity for which to get associated provider services
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            // calculate number of filter query params
            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ProviderServiceWrapper> providerServices = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Service> services = new ArrayList<>();
                services.add(service);

                utx.begin();

                // get provider services eagerly for given service filtered by given params
                providerServices = new ResourceList<>(
                        ProviderServiceWrapper.wrap(
                                providerServiceFacade.findByMultipleCriteriaEagerly(params.getProviders(), services, params.getServiceCategories(),
                                        params.getDescriptions(), params.getMinPrice(), params.getMaxPrice(), params.getIncludeDiscounts(),
                                        params.getMinDiscount(), params.getMaxDiscount(), params.getMinDuration(), params.getMaxDuration(),
                                        params.getServicePoints(), params.getWorkStations(), params.getEmployees(), params.getEmployeeTerms(),
                                        params.getTerms(), params.getOffset(), params.getLimit())
                        )
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get provider services eagerly for given service without filtering (eventually paginated)
                providerServices = new ResourceList<>( ProviderServiceWrapper.wrap(providerServiceFacade.findByServiceEagerly(service, params.getOffset(), params.getLimit())) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(providerServices, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providerServices).build();
        }

        /**
         * Additional methods returning subset of resources based on given criteria.
         * you can achieve similar results by applying @QueryParams to generic method
         * returning all resources in order to filter and limit them.
         */

        /**
         * Method that counts Provider Service entities for given Service resource
         * The service id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countProviderServicesByService( @PathParam("serviceId") Integer serviceId,
                                                        @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of provider services for given service by executing " +
                    "ServiceResource.ProviderServiceResource.countProviderServicesByService(serviceId) method of REST API");

            // find service entity for which to count provider services
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(providerServiceFacade.countByService(service)),
                    200, "number of provider services for service with id " + service.getServiceId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method returns subset of Provider Service entities for given service
         * described by given description.
         * The service id and description are passed through path params.
         */
        @GET
        @Path("/described/{description : \\S+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceProviderServicesByDescription( @PathParam("serviceId") Integer serviceId,
                                                                 @PathParam("description") String description,
                                                                 @BeanParam PaginationBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning provider services for given service and description using " +
                    "ServiceResource.ProviderServiceResource.getServiceProviderServicesByDescription(serviceId, description) method of REST API");

            // find service entity for which to get associated provider services
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            if(description == null)
                throw new BadRequestException("Description param cannot be null.");

            // find provider services by given criteria (service and description)
            ResourceList<ProviderService> providerServices = new ResourceList<>(
                    providerServiceFacade.findByServiceAndDescription(service, description, params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(providerServices, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providerServices).build();
        }

        /**
         * Method returns subset of Provider Service entities for given service
         * that have been priced between some min price and max price.
         * The service id is passed through path param and price limits are
         * passed through query params.
         */
        @GET
        @Path("/priced")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceProviderServicesByPrice( @PathParam("serviceId") Integer serviceId,
                                                           @BeanParam PriceRangeBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning provider services for given service and priced between given min and max price limits using " +
                    "ServiceResource.ProviderServiceResource.getServiceProviderServicesByPrice(serviceId, minPrice, maxPrice) method of REST API");

            // find service entity for which to get associated provider services
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            RESTToolkit.validatePriceRange(params);

            // find provider services by given criteria (service and price range)
            ResourceList<ProviderService> providerServices = new ResourceList<>(
                    providerServiceFacade.findByServiceAndPrice(service, params.getMinPrice(), params.getMaxPrice(),
                            params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(providerServices, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providerServices).build();
        }

        /**
         * Method returns subset of Provider Service entities for given service
         * that have been priced (incl. discounts) between some min discounted price
         * and max discounted price. The service id is passed through path param and
         * discounted price limits are passed through query params.
         */
        @GET
        @Path("/discounted-priced")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceProviderServicesByDiscountedPrice( @PathParam("serviceId") Integer serviceId,
                                                                     @BeanParam PriceRangeBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning provider services for given service and priced (incl. discounts) between given min and max discounted price limits using " +
                    "ServiceResource.ProviderServiceResource.getServiceProviderServicesByDiscountedPrice(serviceId, minDiscountedPrice, maxDiscountedPrice) method of REST API");

            // find service entity for which to get associated provider services
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            RESTToolkit.validatePriceRange(params);

            // find provider services by given criteria (service and discounted price range)
            ResourceList<ProviderService> providerServices = new ResourceList<>(
                    providerServiceFacade.findByServiceAndDiscountedPrice(service, params.getMinPrice(), params.getMaxPrice(),
                            params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(providerServices, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providerServices).build();
        }

        /**
         * Method returns subset of Provider Service entities for given service
         * that have been discounted between some min discount and max discount.
         * The service id is passed through path param and discount limits are
         * passed through query params.
         */
        @GET
        @Path("/discounted")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceProviderServicesByDiscount( @PathParam("serviceId") Integer serviceId,
                                                              @BeanParam DiscountRangeBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning provider services for given service and discounted between given min and max discount limits using " +
                    "ServiceResource.ProviderServiceResource.getServiceProviderServicesByDiscount(serviceId, minDiscount, maxDiscount) method of REST API");

            // find service entity for which to get associated provider services
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            RESTToolkit.validateDiscountRange(params);

            // find provider services for given criteria (service and discount range)
            ResourceList<ProviderService> providerServices = new ResourceList<>(
                    providerServiceFacade.findByServiceAndDiscount(service, params.getMinDiscount(), params.getMaxDiscount(),
                            params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(providerServices, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providerServices).build();
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
                                                 @BeanParam ServicePointBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

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

                utx.begin();

                if(params.getAddress() != null) {
                    if(params.getCoordinatesSquare() != null || params.getCoordinatesCircle() != null)
                        throw new BadRequestException("Query params cannot include address params and coordinates square params or coordinates circle params at the same time.");
                    // only address params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(params.getProviders(), services, params.getProviderServices(), params.getEmployees(),
                                    params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getAddress(), params.getTerms(),
                                    params.getOffset(), params.getLimit())
                    );

                } else if(params.getCoordinatesSquare() != null) {
                    if(params.getAddress() != null || params.getCoordinatesCircle() != null)
                        throw new BadRequestException("Query params cannot include coordinates square params and address params or coordinates circle params at the same time.");
                    // only coordinates square params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(params.getProviders(), services, params.getProviderServices(), params.getEmployees(),
                                    params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesSquare(), params.getTerms(),
                                    params.getOffset(), params.getLimit())
                    );

                } else if(params.getCoordinatesCircle() != null) {
                    if(params.getAddress() != null || params.getCoordinatesSquare() != null)
                        throw new BadRequestException("Query params cannot include coordinates circle params and address params or coordinates square params at the same time.");
                    // only coordinates circle params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(params.getProviders(), services, params.getProviderServices(), params.getEmployees(),
                                    params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesCircle(), params.getTerms(),
                                    params.getOffset(), params.getLimit())
                    );

                } else {
                    // no location params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(params.getProviders(), services, params.getProviderServices(), params.getEmployees(),
                                    params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getTerms(),
                                    params.getOffset(), params.getLimit())
                    );
                }

                utx.commit();

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
                                                        @BeanParam ServicePointBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

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

                utx.begin();

                if(params.getAddress() != null) {
                    if(params.getCoordinatesSquare() != null || params.getCoordinatesCircle() != null)
                        throw new BadRequestException("Query params cannot include address params and coordinates square params or coordinates circle params at the same time.");
                    // only address params
                    servicePoints = new ResourceList<>(
                            ServicePointWrapper.wrap(
                                    servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), services, params.getProviderServices(), params.getEmployees(),
                                            params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getAddress(), params.getTerms(),
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
                                            params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesSquare(), params.getTerms(),
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
                                            params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesCircle(), params.getTerms(),
                                            params.getOffset(), params.getLimit())
                            )
                    );

                } else {
                    // no location params
                    servicePoints = new ResourceList<>(
                            ServicePointWrapper.wrap(
                                    servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), services, params.getProviderServices(), params.getEmployees(),
                                            params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getTerms(),
                                            params.getOffset(), params.getLimit())
                            )
                    );
                }

                utx.commit();

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

    public class EmployeeResource {

        public EmployeeResource() { }

        /**
         * Method returns subset of Employee entities for given Service entity.
         * The service id is passed through path param.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceEmployees( @PathParam("serviceId") Integer serviceId,
                                             @BeanParam EmployeeBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {


            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employees for given service using ServiceResource.EmployeeResource.getServiceEmployees(serviceId) method of REST API");

            // find service entity for which to get associated employees
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<Employee> employees = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Service> services = new ArrayList<>();
                services.add(service);

                utx.begin();

                // get employees for given service filtered by given params
                employees = new ResourceList<>(
                        employeeFacade.findByMultipleCriteria(params.getDescriptions(), params.getJobPositions(), params.getSkills(),
                                params.getEducations(), services, params.getProviderServices(), params.getServicePoints(),
                                params.getWorkStations(), params.getPeriod(), params.getStrictTerm(), params.getTerms(), params.getRated(),
                                params.getMinAvgRating(), params.getMaxAvgRating(), params.getRatingClients(), params.getOffset(), params.getLimit())
                );

                utx.commit();
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get employees for given service without filtering (eventually paginated)
                employees = new ResourceList<>( employeeFacade.findByService(service, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeResource.populateWithHATEOASLinks(employees, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employees).build();
        }

        /**
         * Method returns subset of Employee entities for given Service fetching them eagerly.
         * The service id is passed through path param.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceEmployeesEagerly( @PathParam("serviceId") Integer serviceId,
                                                    @BeanParam EmployeeBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employees eagerly for given service using " +
                    "ServiceResource.EmployeeResource.getServiceEmployeesEagerly(serviceId) method of REST API");

            // find service entity for which to get associated employees
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<EmployeeWrapper> employees = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Service> services = new ArrayList<>();
                services.add(service);

                utx.begin();

                // get employees eagerly for given service filtered by given params
                employees = new ResourceList<>(
                        EmployeeWrapper.wrap(
                                employeeFacade.findByMultipleCriteriaEagerly(params.getDescriptions(), params.getJobPositions(), params.getSkills(),
                                        params.getEducations(), services, params.getProviderServices(), params.getServicePoints(),
                                        params.getWorkStations(), params.getPeriod(), params.getStrictTerm(), params.getTerms(), params.getRated(),
                                        params.getMinAvgRating(), params.getMaxAvgRating(), params.getRatingClients(), params.getOffset(), params.getLimit())
                        )
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get employees eagerly for given service without filtering (eventually paginated)
                employees = new ResourceList<>( EmployeeWrapper.wrap(employeeFacade.findByServiceEagerly(service, params.getOffset(), params.getLimit())) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeResource.populateWithHATEOASLinks(employees, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employees).build();
        }

        /**
         * Method counts Employee entities for given Service entity.
         * The service id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countEmployeesByService( @PathParam("serviceId") Integer serviceId,
                                                 @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of employees for given service by executing " +
                    "ServiceResource.EmployeeResource.countEmployeesByService(serviceId) method of REST API");

            // find service entity for which to count employees
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(employeeFacade.countByService(service)), 200,
                    "number of employees for service with id " + serviceId + ".");
            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    public class WorkStationResource {

        public WorkStationResource() {
        }

        /**
         * Method returns subset of WorkStation entities for given Service.
         * The service id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceWorkStations(@PathParam("serviceId") Integer serviceId,
                                               @BeanParam WorkStationBeanParam params) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Work Station entities for given Service using " +
                    "ServiceResource.WorkStationResource.getServiceWorkStations(serviceId) method of REST API");

            // find service entity for which to get associated work stations
            Service service = serviceFacade.find(serviceId);
            if (service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<WorkStation> workStations = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Service> services = new ArrayList<>();
                services.add(service);

                utx.begin();

                workStations = new ResourceList<>(
                        workStationFacade.findByMultipleCriteria(params.getServicePoints(), services, params.getProviderServices(),
                                params.getEmployees(), params.getWorkStationTypes(), params.getPeriod(), params.getStrictTerm(),
                                params.getTerms(), params.getOffset(), params.getLimit())
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                workStations = new ResourceList<>(workStationFacade.findByService(service, params.getOffset(), params.getLimit()));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.WorkStationResource.populateWithHATEOASLinks(workStations, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(workStations).build();
        }

        /**
         * Method returns subset of Work Station entities for given Service
         * fetching them eagerly. The service id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceWorkStationsEagerly(@PathParam("serviceId") Integer serviceId,
                                                      @BeanParam WorkStationBeanParam params) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Work Station entities eagerly for given Service using " +
                    "ServiceResource.WorkStationResource.getServiceWorkStationsEagerly(serviceId) method of REST API");

            // find service entity for which to get associated work stations
            Service service = serviceFacade.find(serviceId);
            if (service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<WorkStationWrapper> workStations = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Service> services = new ArrayList<>();
                services.add(service);

                utx.begin();

                workStations = new ResourceList<>(
                        WorkStationWrapper.wrap(
                                workStationFacade.findByMultipleCriteriaEagerly(params.getServicePoints(), services,
                                        params.getProviderServices(), params.getEmployees(), params.getWorkStationTypes(),
                                        params.getPeriod(), params.getStrictTerm(), params.getTerms(), params.getOffset(), params.getLimit())
                        )
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                workStations = new ResourceList<>(WorkStationWrapper.wrap(workStationFacade.findByServiceEagerly(service, params.getOffset(), params.getLimit())));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.WorkStationResource.populateWithHATEOASLinks(workStations, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(workStations).build();
        }

        /**
         * Method that counts Work Station entities for given Service resource.
         * The service id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countWorkStationsByService(@PathParam("serviceId") Integer serviceId,
                                                   @BeanParam GenericBeanParam params) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of work stations for given service by executing " +
                    "ServiceResource.WorkStationResource.countWorkStationsByService(serviceId) method of REST API");

            // find service entity for which to count work stations
            Service service = serviceFacade.find(serviceId);
            if (service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(workStationFacade.countByService(service)), 200,
                    "number of work stations for service with id " + service.getServiceId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method returns subset of Work Station entities for given Service entity and
         * Term when it is provided on them. The service id is passed through path param.
         * Term start and end dates are passed through query params.
         */
        @GET
        @Path("/by-term")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceWorkStationsByTerm(@PathParam("serviceId") Integer serviceId,
                                                     @BeanParam DateRangeBeanParam params) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning work stations for given service and term (startDate, endDate) using " +
                    "ServiceResource.WorkStationResource.getServiceWorkStationsByTerm(serviceId, term) method of REST API");

            RESTToolkit.validateDateRange(params); // i.e. startDate and endDate

            // find service entity for which to get associated work stations
            Service service = serviceFacade.find(serviceId);
            if (service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            // find work stations by given criteria (service, term)
            ResourceList<WorkStation> workStations = new ResourceList<>(
                    workStationFacade.findByServiceAndTerm(service, params.getStartDate(), params.getEndDate(),
                            params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.WorkStationResource.populateWithHATEOASLinks(workStations, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(workStations).build();
        }

        /**
         * Method returns subset of Work Station entities for given Service entity and
         * Term (strict) when it is provided on them. The service id is passed through path param.
         * Term (strict) start and end dates are passed through query params.
         */
        @GET
        @Path("/by-term-strict")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceWorkStationsByTermStrict(@PathParam("serviceId") Integer serviceId,
                                                           @BeanParam DateRangeBeanParam params) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning work stations for given service and term strict (startDate, endDate) using " +
                    "ServiceResource.WorkStationResource.getServiceWorkStationsByTermStrict(serviceId, termStrict) method of REST API");

            RESTToolkit.validateDateRange(params); // i.e. startDate and endDate

            // find service entity for which to get associated work stations
            Service service = serviceFacade.find(serviceId);
            if (service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            // find work stations by given criteria (service, term strict)
            ResourceList<WorkStation> workStations = new ResourceList<>(
                    workStationFacade.findByServiceAndTermStrict(service, params.getStartDate(), params.getEndDate(),
                            params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.WorkStationResource.populateWithHATEOASLinks(workStations, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(workStations).build();
        }
    }

    public class EmployeeTermResource {

        public EmployeeTermResource() { }

        /**
         * Method returns subset of Employee Term entities for given Service entity.
         * The service id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceEmployeeTerms( @PathParam("serviceId") Integer serviceId,
                                                 @BeanParam EmployeeTermBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employee terms for given service using " +
                    "ServiceResource.EmployeeTermResource.getServiceEmployeeTerms(serviceId) method of REST API");

            // find service entity for which to get associated employee terms
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<EmployeeTerm> employeeTerms = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Service> services = new ArrayList<>();
                services.add(service);

                // get employee terms for given service filtered by given query params

                utx.begin();

                employeeTerms = new ResourceList<>(
                        employeeTermFacade.findByMultipleCriteria(params.getServicePoints(), params.getWorkStations(), params.getEmployees(),
                                params.getTerms(), services, params.getProviderServices(), params.getPeriod(), params.getStrictTerm(),
                                params.getOffset(), params.getLimit())
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get employee terms for given service without filtering (eventually paginated)
                employeeTerms = new ResourceList<>( employeeTermFacade.findByService(service, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeTermResource.populateWithHATEOASLinks(employeeTerms, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employeeTerms).build();
        }

        /**
         * Method that counts Employee Term entities for given Service resource.
         * The service id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countEmployeeTermsByService( @PathParam("serviceId") Integer serviceId,
                                                     @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of employee terms for given service by executing " +
                    "ServiceResource.EmployeeTermResource.countEmployeeTermsByService(serviceId) method of REST API");

            // find service entity for which to count employee terms
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(employeeTermFacade.countByService(service)), 200,
                    "number of employee terms for service with id " + service.getServiceId() );
            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    public class TermResource {

        public TermResource() { }

        /**
         * Method returns subset of Term entities for given Service entity.
         * The service id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceTerms(@PathParam("serviceId") Integer serviceId,
                                        @BeanParam TermBeanParam params) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning terms for given service using " +
                    "ServiceResource.TermResource.getServiceTerms(serviceId) method of REST API");

            // find service entity for which to get associated terms
            Service service = serviceFacade.find(serviceId);
            if (service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<Term> terms = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Service> services = new ArrayList<>();
                services.add(service);

                // get terms for given service filtered by given query params

                utx.begin();

                terms = new ResourceList<>(
                        termFacade.findByMultipleCriteria(params.getServicePoints(), params.getWorkStations(), params.getEmployees(),
                                services, params.getProviderServices(), params.getPeriod(), params.getStrictTerm(),
                                params.getOffset(), params.getLimit())
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get terms for given service without filtering (eventually paginated)
                terms = new ResourceList<>(termFacade.findByService(service, params.getOffset(), params.getLimit()));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.TermResource.populateWithHATEOASLinks(terms, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(terms).build();
        }

        /**
         * Method returns subset of Term entities for given Service fetching them eagerly.
         * The service id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceTermsEagerly(@PathParam("serviceId") Integer serviceId,
                                               @BeanParam TermBeanParam params) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning terms eagerly for given service using " +
                    "ServiceResource.TermResource.getServiceTermsEagerly(serviceId) method of REST API");

            // find service entity for which to get associated terms
            Service service = serviceFacade.find(serviceId);
            if (service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<TermWrapper> terms = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Service> services = new ArrayList<>();
                services.add(service);

                // get terms eagerly for given service filtered by given query params

                utx.begin();

                terms = new ResourceList<>(
                        TermWrapper.wrap(
                                termFacade.findByMultipleCriteriaEagerly(params.getServicePoints(), params.getWorkStations(), params.getEmployees(),
                                        services, params.getProviderServices(), params.getPeriod(), params.getStrictTerm(),
                                        params.getOffset(), params.getLimit())
                        )
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get terms eagerly for given service without filtering (eventually paginated)
                terms = new ResourceList<>(TermWrapper.wrap(termFacade.findByServiceEagerly(service, params.getOffset(), params.getLimit())));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.TermResource.populateWithHATEOASLinks(terms, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(terms).build();
        }

        /**
         * Method that counts Term entities for given Service resource.
         * The service id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countTermsByService( @PathParam("serviceId") Integer serviceId,
                                             @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of terms for given service by executing " +
                    "ServiceResource.TermResource.countTermsByService(serviceId) method of REST API");

            // find service entity for which to count terms
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(termFacade.countByService(service)), 200,
                    "number of terms for service with id " + service.getServiceId() );
            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    public class TransactionResource {

        public TransactionResource() { }

        /**
         * Method returns subset of Transaction entities for given Service entity.
         * The service id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceTransactions( @PathParam("serviceId") Integer serviceId,
                                                @BeanParam TransactionBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning transactions for given service using " +
                    "ServiceResource.TransactionResource.getServiceTransactions(serviceId) method of REST API");

            // find service entity for which to get associated transactions
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<Transaction> transactions = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Service> services = new ArrayList<>();
                services.add(service);

                // get transactions for given service filtered by given query params

                utx.begin();

                transactions = new ResourceList<>(
                        transactionFacade.findByMultipleCriteria(params.getClients(), params.getProviders(), services, params.getServicePoints(),
                                params.getWorkStations(), params.getEmployees(), params.getProviderServices(), params.getTransactionTimePeriod(),
                                params.getBookedTimePeriod(), params.getTerms(), params.getPriceRange(), params.getCurrencyCodes(), params.getPaymentMethods(),
                                params.getPaid(), params.getOffset(), params.getLimit())
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get transactions for given service without filtering (eventually paginated)
                transactions = new ResourceList<>( transactionFacade.findByService(service, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.TransactionResource.populateWithHATEOASLinks(transactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(transactions).build();
        }

        /**
         * Method returns subset of Transaction entities for given Service fetching them eagerly.
         * The service id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceTransactionsEagerly( @PathParam("serviceId") Integer serviceId,
                                                       @BeanParam TransactionBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning transactions eagerly for given service using " +
                    "ServiceResource.TransactionResource.getServiceTransactionsEagerly(serviceId) method of REST API");

            // find service entity for which to get associated transactions
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<TransactionWrapper> transactions = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Service> services = new ArrayList<>();
                services.add(service);

                // get transactions eagerly for given service filtered by given query params

                utx.begin();

                transactions = new ResourceList<>(
                        TransactionWrapper.wrap(
                                transactionFacade.findByMultipleCriteriaEagerly(params.getClients(), params.getProviders(), services,
                                        params.getServicePoints(), params.getWorkStations(), params.getEmployees(), params.getProviderServices(),
                                        params.getTransactionTimePeriod(), params.getBookedTimePeriod(), params.getTerms(), params.getPriceRange(),
                                        params.getCurrencyCodes(), params.getPaymentMethods(), params.getPaid(), params.getOffset(), params.getLimit())
                        )
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get transactions eagerly for given service without filtering (eventually paginated)
                transactions = new ResourceList<>( TransactionWrapper.wrap(transactionFacade.findByServiceEagerly(service, params.getOffset(), params.getLimit())) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.TransactionResource.populateWithHATEOASLinks(transactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(transactions).build();
        }

        /**
         * Method that counts Transaction entities for given Service resource.
         * The service id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countTransactionsByService( @PathParam("serviceId") Integer serviceId,
                                                    @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of transactions for given service by executing " +
                    "ServiceResource.TransactionResource.countTransactionsByService(serviceId) method of REST API");

            // find service entity for which to count transactions
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(transactionFacade.countByService(service)), 200,
                    "number of transactions for service with id " + service.getServiceId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    public class HistoricalTransactionResource {

        public HistoricalTransactionResource() {}

        /**
         * Method returns subset of Historical Transaction entities for given Service entity.
         * The service id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceHistoricalTransactions( @PathParam("serviceId") Integer serviceId,
                                                          @BeanParam HistoricalTransactionBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning historical transactions for given service using " +
                    "ServiceResource.HistoricalTransactionResource.getServiceHistoricalTransactions(serviceId) method of REST API");

            // find service entity for which to get associated historical transactions
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<HistoricalTransaction> historicalTransactions = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Service> services = new ArrayList<>();
                services.add(service);

                // get historical transactions for given service filtered by given query params

                utx.begin();

                historicalTransactions = new ResourceList<>(
                        historicalTransactionFacade.findByMultipleCriteria(params.getClients(), params.getProviders(), services, params.getServicePoints(),
                                params.getWorkStations(), params.getEmployees(), params.getProviderServices(), params.getTransactionTimePeriod(),
                                params.getBookedTimePeriod(), params.getTerms(), params.getPriceRange(), params.getCurrencyCodes(), params.getPaymentMethods(),
                                params.getPaid(), params.getCompletionStatuses(), params.getClientRatingRange(), params.getClientComments(),
                                params.getProviderRatingRange(), params.getProviderDementis(), params.getOffset(), params.getLimit())
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get historical transactions for given service without filtering (eventually paginated)
                historicalTransactions = new ResourceList<>(historicalTransactionFacade.findByService(service, params.getOffset(), params.getLimit()));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(historicalTransactions).build();
        }

        /**
         * Method returns subset of Historical Transaction entities for given Service fetching them eagerly.
         * The service id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceHistoricalTransactionsEagerly( @PathParam("serviceId") Integer serviceId,
                                                                 @BeanParam HistoricalTransactionBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning historical transactions eagerly for given service using " +
                    "ServiceResource.HistoricalTransactionResource.getServiceHistoricalTransactionsEagerly(serviceId) method of REST API");

            // find service entity for which to get associated historical transactions
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<HistoricalTransactionWrapper> historicalTransactions = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Service> services = new ArrayList<>();
                services.add(service);

                // get historical transactions eagerly for given service filtered by given query params

                utx.begin();

                historicalTransactions = new ResourceList<>(
                        HistoricalTransactionWrapper.wrap(
                                historicalTransactionFacade.findByMultipleCriteriaEagerly(params.getClients(), params.getProviders(), services, params.getServicePoints(),
                                        params.getWorkStations(), params.getEmployees(), params.getProviderServices(), params.getTransactionTimePeriod(),
                                        params.getBookedTimePeriod(), params.getTerms(), params.getPriceRange(), params.getCurrencyCodes(), params.getPaymentMethods(),
                                        params.getPaid(), params.getCompletionStatuses(), params.getClientRatingRange(), params.getClientComments(),
                                        params.getProviderRatingRange(), params.getProviderDementis(), params.getOffset(), params.getLimit())
                        )
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get historical transactions eagerly for given service without filtering (eventually paginated)
                historicalTransactions = new ResourceList<>(HistoricalTransactionWrapper.wrap(historicalTransactionFacade.findByServiceEagerly(service, params.getOffset(), params.getLimit())));

            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(historicalTransactions).build();
        }

        /**
         * Method that counts Historical Transaction entities for given Service resource.
         * The service id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countHistoricalTransactionsByService( @PathParam("serviceId") Integer serviceId,
                                                              @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of historical transactions for given service by executing " +
                    "ServiceResource.HistoricalTransactionResource.countHistoricalTransactionsByService(serviceId) method of REST API");

            // find service entity for which to count historical transactions
            Service service = serviceFacade.find(serviceId);
            if(service == null)
                throw new NotFoundException("Could not find service for id " + serviceId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(historicalTransactionFacade.countByService(service)), 200,
                    "number of historical transactions for service with id " + service.getServiceId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }
}
