package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.*;
import pl.salonea.entities.*;

import pl.salonea.entities.idclass.WorkStationId;
import pl.salonea.enums.WorkStationType;
import pl.salonea.jaxrs.bean_params.*;
import pl.salonea.jaxrs.exceptions.BadRequestException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.exceptions.UnprocessableEntityException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.*;

import javax.inject.Inject;
import javax.transaction.*;
import javax.transaction.NotSupportedException;
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
 * Created by michzio on 24/01/2016.
 */
@Path("/work-stations")
public class WorkStationResource {

    private static final Logger logger = Logger.getLogger(WorkStationResource.class.getName());

    @Inject
    private UserTransaction utx;

    @Inject
    private WorkStationFacade workStationFacade;
    @Inject
    private EmployeeFacade employeeFacade;
    @Inject
    private ServiceFacade serviceFacade;
    @Inject
    private ProviderServiceFacade providerServiceFacade;
    @Inject
    private EmployeeTermFacade employeeTermFacade;
    @Inject
    private TermFacade termFacade;

    @Inject
    private ProviderResource providerResource;

    /**
     * Alternative methods to access Work Station resource
     */
    @GET
    @Path("/{providerId: \\d+}+{servicePointNumber: \\d+}+{workStationNumber: \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getWorkStation( @PathParam("providerId") Long providerId,
                                    @PathParam("servicePointNumber") Integer servicePointNumber,
                                    @PathParam("workStationNumber") Integer workStationNumber,
                                    @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        return providerResource.getServicePointResource().getWorkStationResource().getWorkStation(providerId, servicePointNumber, workStationNumber, params);
    }

    @GET
    @Path("/{providerId: \\d+}+{servicePointNumber: \\d+}+{workStationNumber: \\d+}/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getWorkStationEagerly( @PathParam("providerId") Long providerId,
                                           @PathParam("servicePointNumber") Integer servicePointNumber,
                                           @PathParam("workStationNumber") Integer workStationNumber,
                                           @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        return providerResource.getServicePointResource().getWorkStationResource().getWorkStationEagerly(providerId, servicePointNumber, workStationNumber, params);
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createWorkStation( WorkStation workStation,
                                       @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        return providerResource.getServicePointResource()
                .getWorkStationResource().createWorkStation(workStation.getServicePoint().getProvider().getUserId(),
                        workStation.getServicePoint().getServicePointNumber(),
                        workStation, params);
    }

    @PUT
    @Path("/{providerId: \\d+}+{servicePointNumber: \\d+}+{workStationNumber: \\d+}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateWorkStation( @PathParam("providerId") Long providerId,
                                       @PathParam("servicePointNumber") Integer servicePointNumber,
                                       @PathParam("workStationNumber") Integer workStationNumber,
                                       WorkStation workStation,
                                       @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        return providerResource.getServicePointResource()
                .getWorkStationResource().updateWorkStation(providerId, servicePointNumber, workStationNumber, workStation, params);
    }

    @DELETE
    @Path("/{providerId: \\d+}+{servicePointNumber: \\d+}+{workStationNumber: \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeWorkStation( @PathParam("providerId") Long providerId,
                                       @PathParam("servicePointNumber") Integer servicePointNumber,
                                       @PathParam("workStationNumber") Integer workStationNumber,
                                       @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException, InternalServerErrorException {

        return providerResource.getServicePointResource()
                .getWorkStationResource().removeWorkStation(providerId, servicePointNumber, workStationNumber, params);
    }

    /**
     * Method returns all Work Station entities.
     * They can be additionally filtered and paginated by @QueryParams.
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getWorkStations( @BeanParam WorkStationBeanParam params ) throws ForbiddenException,
    /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Work Stations by executing WorkStationResource.getWorkStations() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<WorkStation> workStations = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            utx.begin();

            // get all work stations filtered by given query params
            workStations = new ResourceList<>(
                    workStationFacade.findByMultipleCriteria(params.getServicePoints(), params.getServices(), params.getProviderServices(),
                            params.getEmployees(), params.getWorkStationTypes(), params.getPeriod(), params.getStrictTerm(),
                            params.getOffset(), params.getLimit())
            );

            utx.commit();

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all work stations without filtering (eventually paginated)
            workStations = new ResourceList<>( workStationFacade.findAll(params.getOffset(), params.getLimit()) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        WorkStationResource.populateWithHATEOASLinks(workStations, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(workStations).build();
    }

    @GET
    @Path("/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getWorkStationsEagerly( @BeanParam WorkStationBeanParam params ) throws ForbiddenException,
    /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Work Stations eagerly by executing WorkStationResource.getWorkStationsEagerly() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<WorkStationWrapper> workStations = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            utx.begin();

            // get all work stations eagerly filtered by given query params
            workStations = new ResourceList<>(
                    WorkStationWrapper.wrap(
                            workStationFacade.findByMultipleCriteriaEagerly(params.getServicePoints(), params.getServices(), params.getProviderServices(),
                                    params.getEmployees(), params.getWorkStationTypes(), params.getPeriod(), params.getStrictTerm(),
                                    params.getOffset(), params.getLimit())
                    )
            );

            utx.commit();

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all work stations eagerly without filtering (eventually paginated)
            workStations = new ResourceList<>( WorkStationWrapper.wrap(workStationFacade.findAllEagerly(params.getOffset(), params.getLimit())) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        WorkStationResource.populateWithHATEOASLinks(workStations, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(workStations).build();
    }


    /**
     * Additional methods returning a subset of resources based on given criteria
     * you can achieve similar results by applying @QueryParams to generic method
     * returning all resources in order to filter and limit them.
     */

    /**
     * Method returns number of Work Station entities in database
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countWorkStations( @BeanParam GenericBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning number of work stations by executing WorkStationResource.countWorkStations() method of REST API");

        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(workStationFacade.count()), 200, "number of work stations");
        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method returns subset of Work Station entities for given type.
     * The work station type is passed through path param.
     */
    @GET
    @Path("/typed/{type: \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getWorkStationsByType( @PathParam("type") WorkStationType type,
                                           @BeanParam PaginationBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning work stations for given work station type using " +
                "WorkStationResource.getWorkStationsByType(type) method of REST API");

        if(type == null)
            throw new BadRequestException("Work station type param cannot be null.");

        // find work stations by given criteria (work station type)
        ResourceList<WorkStation> workStations = new ResourceList<>(
                workStationFacade.findByType(type, params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        WorkStationResource.populateWithHATEOASLinks(workStations, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(workStations).build();
    }

    /**
     *  Method returns subset of Work Station entities for given Term's data range.
     *  Term start and end dates are passed through query params.
     */
    @GET
    @Path("/by-term")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getWorkStationsByTerm( @BeanParam DateBetweenBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning work stations for given term (startDate, endDate) using " +
                "WorkStationResource.getWorkStationsByTerm(term) method of REST API");

        RESTToolkit.validateDateRange(params); // i.e. startDate and endDate

        // find work stations by given criteria (term)
        ResourceList<WorkStation> workStations = new ResourceList<>(
                workStationFacade.findByTerm(params.getStartDate(), params.getEndDate(), params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        WorkStationResource.populateWithHATEOASLinks(workStations, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(workStations).build();
    }

    /**
     *  Method returns subset of Work Station entities for given Term's data range (strict).
     *  Term (strict) start and end dates are passed through query params.
     */
    @GET
    @Path("/by-term-strict")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getWorkStationsByTermStrict( @BeanParam DateBetweenBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning work stations for given term strict (startDate, endDate) using " +
                "WorkStationResource.getWorkStationsByTermStrict(termStrict) method of REST API");

        RESTToolkit.validateDateRange(params); // i.e. startDate and endDate

        // find work stations by given criteria (term strict)
        ResourceList<WorkStation> workStations = new ResourceList<>(
                workStationFacade.findByTermStrict(params.getStartDate(), params.getEndDate(), params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        WorkStationResource.populateWithHATEOASLinks(workStations, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(workStations).build();
    }

    /**
     * related subresources (through relationships)
     */
    @Path("/{providerId: \\d+}+{servicePointNumber: \\d+}+{workStationNumber: \\d+}/employees")
    public EmployeeResource getEmployeeResource() { return new EmployeeResource(); }

    @Path("/{providerId: \\d+}+{servicePointNumber: \\d+}+{workStationNumber: \\d+}/services")
    public ServiceResource getServiceResource() { return new ServiceResource(); }

    @Path("/{providerId: \\d+}+{servicePointNumber: \\d+}+{workStationNumber: \\d+}/provider-services")
    public ProviderServiceResource getProviderServiceResource() { return new ProviderServiceResource(); }

    @Path("/{providerId: \\d+}+{servicePointNumber: \\d+}+{workStationNumber: \\d+}/employee-terms")
    public EmployeeTermResource getEmployeeTermResource() { return new EmployeeTermResource(); }

    @Path("/{providerId: \\d+}+{servicePointNumber: \\d+}+{workStationNumber: \\d+}/terms")
    public TermResource getTermResource() { return new TermResource(); }

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList workStations, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(workStations, uriInfo, offset, limit);

        try {
            // count resources hypermedia link
            Method countMethod = WorkStationResource.class.getMethod("countWorkStations", GenericBeanParam.class);
            workStations.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(WorkStationResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            workStations.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(WorkStationResource.class).build()).rel("work-stations").build() );

            // get all resources eagerly hypermedia link
            Method workStationsEagerlyMethod = WorkStationResource.class.getMethod("getWorkStationsEagerly", WorkStationBeanParam.class);
            workStations.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(WorkStationResource.class)
                    .path(workStationsEagerlyMethod)
                    .build())
                    .rel("work-stations-eagerly").build() );

            // get subset of resources hypermedia links

            // typed
            workStations.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(WorkStationResource.class)
                    .path("typed")
                    .build())
                    .rel("typed").build() );

            // by-term
            Method workStationsByTermMethod = WorkStationResource.class.getMethod("getWorkStationsByTerm", DateBetweenBeanParam.class);
            workStations.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(WorkStationResource.class)
                    .path(workStationsByTermMethod)
                    .build())
                    .rel("by-term").build() );

            // by-term-strict
            Method workStationsByTermStrictMethod = WorkStationResource.class.getMethod("getWorkStationsByTermStrict", DateBetweenBeanParam.class);
            workStations.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(WorkStationResource.class)
                    .path(workStationsByTermStrictMethod)
                    .build())
                    .rel("by-term-strict").build() );

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for(Object object : workStations.getResources()) {
            if(object instanceof WorkStation) {
                WorkStationResource.populateWithHATEOASLinks( (WorkStation) object, uriInfo);
            } else if(object instanceof WorkStationWrapper) {
                WorkStationResource.populateWithHATEOASLinks( (WorkStationWrapper) object, uriInfo);
            }
        }
    }

    /**
     * This method enables to populate each individual resource wrapper with hypermedia links
     */
    public static void populateWithHATEOASLinks(WorkStationWrapper workStationWrapper, UriInfo uriInfo) {

        WorkStationResource.populateWithHATEOASLinks(workStationWrapper.getWorkStation(), uriInfo);

      for(EmployeeTerm employeeTerm : workStationWrapper.getTermsEmployeesWorkOn())
          pl.salonea.jaxrs.EmployeeTermResource.populateWithHATEOASLinks(employeeTerm, uriInfo);

      for(ProviderService providerService : workStationWrapper.getProvidedServices())
          pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(providerService, uriInfo);
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(WorkStation workStation, UriInfo uriInfo) {

        try {

            // self link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/{sub-id}/{sub-subresources}/{sub-sub-id}
            Method servicePointsMethod = ProviderResource.class.getMethod("getServicePointResource");
            Method workStationsMethod = ProviderResource.ServicePointResource.class.getMethod("getWorkStationResource");
            workStation.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(servicePointsMethod)
                    .path(workStationsMethod)
                    .path(workStation.getWorkStationNumber().toString())
                    .resolveTemplate("userId", workStation.getServicePoint().getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", workStation.getServicePoint().getServicePointNumber().toString())
                    .build())
                    .rel("self").build());

            // self alternative link with pattern: http://localhost:port/app/rest/{resources}/{id}+{sub-id}+{sub-sub-id}
            Method workStationMethod = WorkStationResource.class.getMethod("getWorkStation", Long.class, Integer.class, Integer.class, GenericBeanParam.class);
            workStation.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(WorkStationResource.class)
                    .path(workStationMethod)
                    .resolveTemplate("providerId", workStation.getServicePoint().getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", workStation.getServicePoint().getServicePointNumber().toString())
                    .resolveTemplate("workStationNumber", workStation.getWorkStationNumber().toString())
                    .build())
                    .rel("self (alternative)").build());

            // collection link with pattern: http://localhost:port/app/rest/{resources}
            workStation.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(WorkStationResource.class)
                    .build())
                    .rel("work-stations").build());

            // self eagerly link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/{sub-id}/{sub-subresources}/{sub-sub-id}/eagerly
            Method workStationEagerlyMethod = ProviderResource.ServicePointResource.WorkStationResource.class.getMethod("getWorkStationEagerly", Long.class, Integer.class, Integer.class, GenericBeanParam.class);
            workStation.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(servicePointsMethod)
                    .path(workStationsMethod)
                    .path(workStationEagerlyMethod)
                    .resolveTemplate("userId", workStation.getServicePoint().getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", workStation.getServicePoint().getServicePointNumber().toString())
                    .resolveTemplate("workStationNumber", workStation.getWorkStationNumber().toString())
                    .build())
                    .rel("work-station-eagerly").build());

            // self eagerly alternative link with pattern: http://localhost:port/app/rest/{resources}/{id}+{sub-id}+{sub-sub-id}/eagerly
            Method workStationEagerlyAlternativeMethod = WorkStationResource.class.getMethod("getWorkStationEagerly", Long.class, Integer.class, Integer.class, GenericBeanParam.class);
            workStation.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(WorkStationResource.class)
                    .path(workStationEagerlyAlternativeMethod)
                    .resolveTemplate("providerId", workStation.getServicePoint().getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", workStation.getServicePoint().getServicePointNumber().toString())
                    .resolveTemplate("workStationNumber", workStation.getWorkStationNumber().toString())
                    .build())
                    .rel("work-station-eagerly (alternative)").build());

            // associated collections links with pattern: http://localhost:port/app/rest/{resources}/{id}/{relationship}

            /**
             * Provider Services associated with current Work Station resource
             */

            // provider-services
            Method providerServicesMethod = WorkStationResource.class.getMethod("getProviderServiceResource");
            workStation.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                        .path(WorkStationResource.class)
                        .path(providerServicesMethod)
                        .resolveTemplate("providerId", workStation.getServicePoint().getProvider().getUserId().toString())
                        .resolveTemplate("servicePointNumber", workStation.getServicePoint().getServicePointNumber().toString())
                        .resolveTemplate("workStationNumber", workStation.getWorkStationNumber().toString())
                        .build())
                        .rel("provider-services").build());

            // provider-services eagerly
            Method providerServicesEagerlyMethod = WorkStationResource.ProviderServiceResource.class.getMethod("getWorkStationProviderServicesEagerly", Long.class, Integer.class, Integer.class, ProviderServiceBeanParam.class);
            workStation.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                        .path(WorkStationResource.class)
                        .path(providerServicesMethod)
                        .path(providerServicesEagerlyMethod)
                        .resolveTemplate("providerId", workStation.getServicePoint().getProvider().getUserId().toString())
                        .resolveTemplate("servicePointNumber", workStation.getServicePoint().getServicePointNumber().toString())
                        .resolveTemplate("workStationNumber", workStation.getWorkStationNumber().toString())
                        .build())
                        .rel("provider-services-eagerly").build());

            // provider-services count
            Method countProviderServicesByWorkStationMethod = WorkStationResource.ProviderServiceResource.class.getMethod("countProviderServicesByWorkStation", Long.class, Integer.class, Integer.class, GenericBeanParam.class);
            workStation.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                        .path(WorkStationResource.class)
                        .path(providerServicesMethod)
                        .path(countProviderServicesByWorkStationMethod)
                        .resolveTemplate("providerId", workStation.getServicePoint().getProvider().getUserId().toString())
                        .resolveTemplate("servicePointNumber", workStation.getServicePoint().getServicePointNumber().toString())
                        .resolveTemplate("workStationNumber", workStation.getWorkStationNumber().toString())
                        .build())
                        .rel("provider-services-count").build());

            /**
             * Employee Terms (EmployeeTerm entity) associated with current Work Station resource
             */

            // employee-terms
            Method employeeTermsMethod = WorkStationResource.class.getMethod("getEmployeeTermResource");
            workStation.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(WorkStationResource.class)
                    .path(employeeTermsMethod)
                    .resolveTemplate("providerId", workStation.getServicePoint().getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", workStation.getServicePoint().getServicePointNumber().toString())
                    .resolveTemplate("workStationNumber", workStation.getWorkStationNumber().toString())
                    .build())
                    .rel("employee-terms").build() );

            // employee-terms count
            Method countEmployeeTermsByWorkStationMethod = WorkStationResource.EmployeeTermResource.class.getMethod("countEmployeeTermsByWorkStation", Long.class, Integer.class, Integer.class, GenericBeanParam.class);
            workStation.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(WorkStationResource.class)
                    .path(employeeTermsMethod)
                    .path(countEmployeeTermsByWorkStationMethod)
                    .resolveTemplate("providerId", workStation.getServicePoint().getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", workStation.getServicePoint().getServicePointNumber().toString())
                    .resolveTemplate("workStationNumber", workStation.getWorkStationNumber().toString())
                    .build())
                    .rel("employee-terms-count").build() );

            /**
             * Terms of executing any provider services on current Work Station resource
             */

            // terms
            Method termsMethod = WorkStationResource.class.getMethod("getTermResource");
            workStation.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(WorkStationResource.class)
                    .path(termsMethod)
                    .resolveTemplate("providerId", workStation.getServicePoint().getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", workStation.getServicePoint().getServicePointNumber().toString())
                    .resolveTemplate("workStationNumber", workStation.getWorkStationNumber().toString())
                    .build())
                    .rel("terms").build() );

            // terms eagerly
            Method termsEagerlyMethod = WorkStationResource.TermResource.class.getMethod("getWorkStationTermsEagerly", Long.class, Integer.class, Integer.class, TermBeanParam.class);
            workStation.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(WorkStationResource.class)
                    .path(termsMethod)
                    .path(termsEagerlyMethod)
                    .resolveTemplate("providerId", workStation.getServicePoint().getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", workStation.getServicePoint().getServicePointNumber().toString())
                    .resolveTemplate("workStationNumber", workStation.getWorkStationNumber().toString())
                    .build())
                    .rel("terms-eagerly").build() );

            // terms count
            Method countTermsByWorkStationMethod = WorkStationResource.TermResource.class.getMethod("countTermsByWorkStation", Long.class, Integer.class, Integer.class, GenericBeanParam.class);
            workStation.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(WorkStationResource.class)
                    .path(termsMethod)
                    .path(countTermsByWorkStationMethod)
                    .resolveTemplate("providerId", workStation.getServicePoint().getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", workStation.getServicePoint().getServicePointNumber().toString())
                    .resolveTemplate("workStationNumber", workStation.getWorkStationNumber().toString())
                    .build())
                    .rel("terms-count").build() );

            /**
             * Employees working on current Work Station resource
             */

            // employees
            Method employeesMethod = WorkStationResource.class.getMethod("getEmployeeResource");
            workStation.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(WorkStationResource.class)
                    .path(employeesMethod)
                    .resolveTemplate("providerId", workStation.getServicePoint().getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", workStation.getServicePoint().getServicePointNumber().toString())
                    .resolveTemplate("workStationNumber", workStation.getWorkStationNumber().toString())
                    .build())
                    .rel("employees").build());

            // employees eagerly
            Method employeesEagerlyMethod = WorkStationResource.EmployeeResource.class.getMethod("getWorkStationEmployeesEagerly", Long.class, Integer.class, Integer.class, EmployeeBeanParam.class);
            workStation.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(WorkStationResource.class)
                    .path(employeesMethod)
                    .path(employeesEagerlyMethod)
                    .resolveTemplate("providerId", workStation.getServicePoint().getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", workStation.getServicePoint().getServicePointNumber().toString())
                    .resolveTemplate("workStationNumber", workStation.getWorkStationNumber().toString())
                    .build())
                    .rel("employees-eagerly").build());

            // employees count
            Method countEmployeesByWorkStationMethod = WorkStationResource.EmployeeResource.class.getMethod("countEmployeesByWorkStation", Long.class, Integer.class, Integer.class, GenericBeanParam.class);
            workStation.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(WorkStationResource.class)
                    .path(employeesMethod)
                    .path(countEmployeesByWorkStationMethod)
                    .resolveTemplate("providerId", workStation.getServicePoint().getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", workStation.getServicePoint().getServicePointNumber().toString())
                    .resolveTemplate("workStationNumber", workStation.getWorkStationNumber().toString())
                    .build())
                    .rel("employees-count").build());

            // employees by-term
            Method employeesByTermMethod = WorkStationResource.EmployeeResource.class.getMethod("getWorkStationEmployeesByTerm", Long.class, Integer.class, Integer.class, DateBetweenBeanParam.class);
            workStation.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(WorkStationResource.class)
                    .path(employeesMethod)
                    .path(employeesByTermMethod)
                    .resolveTemplate("providerId", workStation.getServicePoint().getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", workStation.getServicePoint().getServicePointNumber().toString())
                    .resolveTemplate("workStationNumber", workStation.getWorkStationNumber().toString())
                    .build())
                    .rel("employees-by-term").build());

            // employees by-term-strict
            Method employeesByTermStrictMethod = WorkStationResource.EmployeeResource.class.getMethod("getWorkStationEmployeesByTermStrict", Long.class, Integer.class, Integer.class, DateBetweenBeanParam.class);;
            workStation.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(WorkStationResource.class)
                    .path(employeesMethod)
                    .path(employeesByTermStrictMethod)
                    .resolveTemplate("providerId", workStation.getServicePoint().getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", workStation.getServicePoint().getServicePointNumber().toString())
                    .resolveTemplate("workStationNumber", workStation.getWorkStationNumber().toString())
                    .build())
                    .rel("employees-by-term-strict").build());

            /**
             * Services executed on current Work Station resource
             */

            // services
            Method servicesMethod = WorkStationResource.class.getMethod("getServiceResource");
            workStation.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(WorkStationResource.class)
                    .path(servicesMethod)
                    .resolveTemplate("providerId", workStation.getServicePoint().getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", workStation.getServicePoint().getServicePointNumber().toString())
                    .resolveTemplate("workStationNumber", workStation.getWorkStationNumber().toString())
                    .build())
                    .rel("services").build());

            // services eagerly
            Method servicesEagerlyMethod = WorkStationResource.ServiceResource.class.getMethod("getWorkStationServicesEagerly", Long.class, Integer.class, Integer.class, ServiceBeanParam.class);
            workStation.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(WorkStationResource.class)
                    .path(servicesMethod)
                    .path(servicesEagerlyMethod)
                    .resolveTemplate("providerId", workStation.getServicePoint().getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", workStation.getServicePoint().getServicePointNumber().toString())
                    .resolveTemplate("workStationNumber", workStation.getWorkStationNumber().toString())
                    .build())
                    .rel("services-eagerly").build());

            // services count
            Method countServicesByWorkStationMethod = WorkStationResource.ServiceResource.class.getMethod("countServicesByWorkStation", Long.class, Integer.class, Integer.class, GenericBeanParam.class);
            workStation.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(WorkStationResource.class)
                    .path(servicesMethod)
                    .path(countServicesByWorkStationMethod)
                    .resolveTemplate("providerId", workStation.getServicePoint().getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", workStation.getServicePoint().getServicePointNumber().toString())
                    .resolveTemplate("workStationNumber", workStation.getWorkStationNumber().toString())
                    .build())
                    .rel("services-count").build());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public class ProviderServiceResource {

        public ProviderServiceResource() { }

        /**
         * Method returns subset of Provider Service entities for given Work Station.
         * The composite work station id is passed through path params i.e.
         * provider id, service point number and work station number.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getWorkStationProviderServices( @PathParam("providerId") Long providerId,
                                                        @PathParam("servicePointNumber") Integer servicePointNumber,
                                                        @PathParam("workStationNumber") Integer workStationNumber,
                                                        @BeanParam ProviderServiceBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Provider Service entities for given Work Station using " +
                    "WorkStationResource.ProviderServiceResource.getWorkStationProviderServices(providerId, servicePointNumber, workStationNumber) method of REST API");

            utx.begin();

            // find work station entity for which to get associated provider services
            WorkStation workStation = workStationFacade.find( new WorkStationId(providerId, servicePointNumber, workStationNumber) );
            if(workStation == null)
                throw new NotFoundException("Could not find work station for id (" + providerId + "," + servicePointNumber + "," + workStationNumber + ").");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ProviderService> providerServices = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<WorkStation> workStations = new ArrayList<>();
                workStations.add(workStation);

                // get provider services for given work station filtered by given params
                providerServices = new ResourceList<>(
                        providerServiceFacade.findByMultipleCriteria(params.getProviders(), params.getServices(), params.getServiceCategories(),
                                params.getDescriptions(), params.getMinPrice(), params.getMaxPrice(), params.getIncludeDiscounts(),
                                params.getMinDiscount(), params.getMaxDiscount(), params.getMinDuration(), params.getMaxDuration(),
                                params.getServicePoints(), workStations, params.getEmployees(), params.getEmployeeTerms(), params.getTerms(),
                                params.getOffset(), params.getLimit())
                );
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get provider services for given work station without filtering (eventually paginated)
                providerServices = new ResourceList<>( providerServiceFacade.findByWorkStation(workStation, params.getOffset(), params.getLimit()) );
            }

            utx.commit();

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(providerServices, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providerServices).build();
        }

        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getWorkStationProviderServicesEagerly( @PathParam("providerId") Long providerId,
                                                               @PathParam("servicePointNumber") Integer servicePointNumber,
                                                               @PathParam("workStationNumber") Integer workStationNumber,
                                                               @BeanParam ProviderServiceBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Provider Service entities for given Work Station eagerly using " +
                    "WorkStationResource.ProviderServiceResource.getWorkStationProviderServicesEagerly(providerId, servicePointNumber, workStationNumber) method of REST API");

            utx.begin();

            // find work station entity for which to get associated provider services
            WorkStation workStation = workStationFacade.find( new WorkStationId(providerId, servicePointNumber, workStationNumber) );
            if(workStation == null)
                throw new NotFoundException("Could not find work station for id (" + providerId + "," + servicePointNumber + "," + workStationNumber + ").");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ProviderServiceWrapper> providerServices = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<WorkStation> workStations = new ArrayList<>();
                workStations.add(workStation);

                // get provider services eagerly for given work station filtered by given query params
                providerServices = new ResourceList<>(
                        ProviderServiceWrapper.wrap(
                                providerServiceFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getServices(),
                                        params.getServiceCategories(), params.getDescriptions(), params.getMinPrice(), params.getMaxPrice(),
                                        params.getIncludeDiscounts(), params.getMinDiscount(), params.getMaxDiscount(), params.getMinDuration(),
                                        params.getMaxDuration(), params.getServicePoints(), workStations, params.getEmployees(),
                                        params.getEmployeeTerms(), params.getTerms(), params.getOffset(), params.getLimit())
                        )
                );
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get provider services eagerly for given work station without filtering (eventually paginated)
                providerServices = new ResourceList<>( ProviderServiceWrapper.wrap(providerServiceFacade.findByWorkStationEagerly(workStation, params.getOffset(), params.getLimit())) );
            }

            utx.commit();

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(providerServices, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providerServices).build();
        }

        /**
         * Method that counts Provider Service entities for given Work Station resource.
         * The composite work station id is passed through path params i.e.
         * provider id, service point number, work station number.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countProviderServicesByWorkStation( @PathParam("providerId") Long providerId,
                                                            @PathParam("servicePointNumber") Integer servicePointNumber,
                                                            @PathParam("workStationNumber") Integer workStationNumber,
                                                            @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of provider services for given work station by executing " +
                    "WorkStationResource.ProviderServiceResource.countProviderServicesByWorkStation(providerId, servicePointNumber, workStationNumber) method of REST API");

            utx.begin();

            // find work station entity for which to count provider services
            WorkStation workStation = workStationFacade.find( new WorkStationId(providerId, servicePointNumber, workStationNumber) );
            if(workStation == null)
                throw new NotFoundException("Could not find work station for id (" + providerId + "," + servicePointNumber + "," + workStationNumber + ").");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(providerServiceFacade.countByWorkStation(workStation)),
                    200, "number of provider services for work station with id (" + providerId + "," + servicePointNumber + "," + workStationNumber + ").");

            utx.commit();

            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    public class EmployeeResource {

        public EmployeeResource() { }

        /**
         * Method returns subset of Employee entities for given Work Station entity.
         * The provider id, service point number and work station number are passed through path params.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getWorkStationEmployees( @PathParam("providerId") Long providerId,
                                                 @PathParam("servicePointNumber") Integer servicePointNumber,
                                                 @PathParam("workStationNumber") Integer workStationNumber,
                                                 @BeanParam EmployeeBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employees for given work station using " +
                    "WorkStationResource.EmployeeResource.getWorkStationEmployees(providerId, servicePointNumber, workStationNumber) method of REST API");

            utx.begin();

            // find work station entity for which to get associated employees
            WorkStation workStation = workStationFacade.find( new WorkStationId(providerId, servicePointNumber, workStationNumber) );
            if(workStation == null)
                throw new NotFoundException("Could not find work station for id (" + providerId + "," + servicePointNumber + "," + workStationNumber + ").");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<Employee> employees = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<WorkStation> workStations = new ArrayList<>();
                workStations.add(workStation);

                // get employees for given work station filtered by given params
                employees = new ResourceList<>(
                        employeeFacade.findByMultipleCriteria(params.getDescriptions(), params.getJobPositions(), params.getSkills(),
                                params.getEducations(), params.getServices(), params.getProviderServices(), params.getServicePoints(),
                                workStations, params.getPeriod(), params.getStrictTerm(), params.getRated(), params.getMinAvgRating(),
                                params.getMaxAvgRating(), params.getRatingClients(), params.getOffset(), params.getLimit())
                );
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get employees for given work station without filtering (eventually paginated)
                employees = new ResourceList<>( employeeFacade.findByWorkStation(workStation, params.getOffset(), params.getLimit()) );
            }

            utx.commit();

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeResource.populateWithHATEOASLinks(employees, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employees).build();
        }

        /**
         * Method returns subset of Employee entities for given Work Station fetching them eagerly.
         * The provider id, service point number and work station number are passed through path params.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getWorkStationEmployeesEagerly( @PathParam("providerId") Long providerId,
                                                        @PathParam("servicePointNumber") Integer servicePointNumber,
                                                        @PathParam("workStationNumber") Integer workStationNumber,
                                                        @BeanParam EmployeeBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employees eagerly for given work station using " +
                    "WorkStationResource.EmployeeResource.getWorkStationEmployeesEagerly(providerId, servicePointNumber, workStationNumber) method of REST API");

            utx.begin();

            // find work station entity for which to get associated employees
            WorkStation workStation = workStationFacade.find( new WorkStationId(providerId, servicePointNumber, workStationNumber) );
            if(workStation == null)
                throw new NotFoundException("Could not find work station for id (" + providerId + "," + servicePointNumber + "," + workStationNumber + ").");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<EmployeeWrapper> employees = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<WorkStation> workStations = new ArrayList<>();
                workStations.add(workStation);

                // get employees eagerly for given work station filtered by given params
                employees = new ResourceList<>(
                        EmployeeWrapper.wrap(
                                employeeFacade.findByMultipleCriteriaEagerly(params.getDescriptions(), params.getJobPositions(), params.getSkills(),
                                        params.getEducations(), params.getServices(), params.getProviderServices(), params.getServicePoints(),
                                        workStations, params.getPeriod(), params.getStrictTerm(), params.getRated(), params.getMinAvgRating(),
                                        params.getMaxAvgRating(), params.getRatingClients(), params.getOffset(), params.getLimit())
                        )
                );
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get employees eagerly for given work station without filtering (eventually paginated)
                employees = new ResourceList<>( EmployeeWrapper.wrap(employeeFacade.findByWorkStationEagerly(workStation, params.getOffset(), params.getLimit())) );
            }

            utx.commit();

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeResource.populateWithHATEOASLinks(employees, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employees).build();
        }

        /**
         * Method counts Employee entities for given Work Station entity.
         * The provider id, service point number and work station number are passed through path params.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countEmployeesByWorkStation( @PathParam("providerId") Long providerId,
                                                     @PathParam("servicePointNumber") Integer servicePointNumber,
                                                     @PathParam("workStationNumber") Integer workStationNumber,
                                                     @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of employees for given work station by executing " +
                    "WorkStationResource.EmployeeResource.countEmployeesByWorkStation(providerId, servicePointNumber, workStationNumber) method of REST API");

            utx.begin();

            // find work station entity for which to count employees
            WorkStation workStation = workStationFacade.find( new WorkStationId(providerId, servicePointNumber, workStationNumber) );
            if(workStation == null)
                throw new NotFoundException("Could not find work station for id (" + providerId + "," + servicePointNumber + "," + workStationNumber + ").");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(employeeFacade.countByWorkStation(workStation)), 200,
                    "number of employees for work station with id (" + providerId + "," + servicePointNumber + "," + workStationNumber + ").");

            utx.commit();

            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method returns subset of Employee entities for given Work Station entity and
         * Term they work on it. The provider id, service point number and work station number
         * are passed through path params. Term start and end dates are passed through query params.
         */
        @GET
        @Path("/by-term")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getWorkStationEmployeesByTerm( @PathParam("providerId") Long providerId,
                                                       @PathParam("servicePointNumber") Integer servicePointNumber,
                                                       @PathParam("workStationNumber") Integer workStationNumber,
                                                       @BeanParam DateBetweenBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employees for given work station and term (startDate, endDate) using " +
                    "WorkStationResource.EmployeeResource.getWorkStationEmployeesByTerm(providerId, servicePointNumber, workStationNumber, term) method of REST API");

            RESTToolkit.validateDateRange(params); // i.e. startDate and endDate

            utx.begin();

            // find work station entity for which to get associated employees
            WorkStation workStation = workStationFacade.find( new WorkStationId(providerId, servicePointNumber, workStationNumber) );
            if(workStation == null)
                throw new NotFoundException("Could not find work station for id (" + providerId + "," + servicePointNumber + "," + workStationNumber + ").");

            // find employees by given criteria (work station, term)
            ResourceList<Employee> employees =  new ResourceList<>(
                    employeeFacade.findByWorkStationAndTerm(workStation, params.getStartDate(), params.getEndDate(),
                            params.getOffset(), params.getLimit())
            );

            utx.commit();

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeResource.populateWithHATEOASLinks(employees, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employees).build();
        }

        /**
         * Method returns subset of Employee entities for given Work Station entity and
         * Term (strict) they work on it. The provider id, service point number and work station number
         * are passed through path params. Term (strict) start and end dates are passed through query params.
         */
        @GET
        @Path("/by-term-strict")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getWorkStationEmployeesByTermStrict( @PathParam("providerId") Long providerId,
                                                             @PathParam("servicePointNumber") Integer servicePointNumber,
                                                             @PathParam("workStationNumber") Integer workStationNumber,
                                                             @BeanParam DateBetweenBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employees for given work station and term strict (startDate, endDate) using " +
                    "WorkStationResource.EmployeeResource.getWorkStationEmployeesByTermStrict(providerId, servicePointNumber, workStationNumber, termStrict) method of REST API");

            RESTToolkit.validateDateRange(params); // i.e. startDate and endDate

            utx.begin();

            // find work station entity for which to get associated employees
            WorkStation workStation = workStationFacade.find( new WorkStationId(providerId, servicePointNumber, workStationNumber) );
            if(workStation == null)
                throw new NotFoundException("Could not find work station for id (" + providerId + "," + servicePointNumber + "," + workStationNumber + ").");

            // find employees by given criteria (work station, term strict)
            ResourceList<Employee> employees =  new ResourceList<>(
                    employeeFacade.findByWorkStationAndTermStrict(workStation, params.getStartDate(), params.getEndDate(),
                            params.getOffset(), params.getLimit())
            );

            utx.commit();

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeResource.populateWithHATEOASLinks(employees, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employees).build();
        }
    }

    public class ServiceResource {

        public ServiceResource() { }

        /**
         * Method returns subset of Service entities for given Work Station entity.
         * The provider id, service point number and work station number are passed
         * through path params. They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getWorkStationServices( @PathParam("providerId") Long providerId,
                                                @PathParam("servicePointNumber") Integer servicePointNumber,
                                                @PathParam("workStationNumber") Integer workStationNumber,
                                                @BeanParam ServiceBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning services for given work station using " +
                    "WorkStationResource.ServiceResource.getWorkStationServices(providerId, servicePointNumber, workStationNumber) method of REST API");

            utx.begin();

            // find work station entity for which to get associated services
            WorkStation workStation = workStationFacade.find(new WorkStationId(providerId, servicePointNumber, workStationNumber));
            if(workStation == null)
                throw new NotFoundException("Could not find work station for id (" + providerId + "," + servicePointNumber + "," + workStationNumber + ").");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<Service> services = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<WorkStation> workStations = new ArrayList<>();
                workStations.add(workStation);

                // get services for given work station filtered by given query params

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                        throw new BadRequestException("Query params cannot include keywords and names or descriptions at the same time.");

                    // find only by keywords
                    services = new ResourceList<>(
                            serviceFacade.findByMultipleCriteria(params.getKeywords(), params.getServiceCategories(), params.getProviders(),
                                    params.getEmployees(), workStations, params.getServicePoints(), params.getEmployeeTerms(), params.getTerms(), params.getOffset(), params.getLimit())
                    );
                } else {
                    // find by names, descriptions
                    services = new ResourceList<>(
                            serviceFacade.findByMultipleCriteria(params.getNames(), params.getDescriptions(), params.getServiceCategories(),
                                    params.getProviders(), params.getEmployees(), workStations, params.getServicePoints(), params.getEmployeeTerms(),
                                    params.getTerms(), params.getOffset(), params.getLimit())
                    );
                }
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get services for given work station without filtering (eventually paginated)
                services = new ResourceList<>( serviceFacade.findByWorkStation(workStation, params.getOffset(), params.getLimit()) );
            }

            utx.commit();

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServiceResource.populateWithHATEOASLinks(services, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(services).build();
        }

        /**
         * Method returns subset of Service entities for given Work Station fetching them eagerly.
         * The provider id, service point number and work station number are passed through path params.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getWorkStationServicesEagerly( @PathParam("providerId") Long providerId,
                                                       @PathParam("servicePointNumber") Integer servicePointNumber,
                                                       @PathParam("workStationNumber") Integer workStationNumber,
                                                       @BeanParam ServiceBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning services eagerly for given work station using " +
                    "WorkStationResource.ServiceResource.getWorkStationServicesEagerly(providerId, servicePointNumber, workStationNumber) method of REST API");

            utx.begin();

            // find work station entity for which to get associated services
            WorkStation workStation = workStationFacade.find(new WorkStationId(providerId, servicePointNumber, workStationNumber));
            if(workStation == null)
                throw new NotFoundException("Could not find work station for id (" + providerId + "," + servicePointNumber + "," + workStationNumber + ").");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ServiceWrapper> services = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<WorkStation> workStations = new ArrayList<>();
                workStations.add(workStation);

                // get services eagerly for given work station filtered by given query params

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                        throw new BadRequestException("Query params cannot include keywords and names or descriptions at the same time.");

                    // find only by keywords
                    services = new ResourceList<>(
                            ServiceWrapper.wrap(
                                    serviceFacade.findByMultipleCriteriaEagerly(params.getKeywords(), params.getServiceCategories(),
                                            params.getProviders(), params.getEmployees(), workStations, params.getServicePoints(),
                                            params.getEmployeeTerms(), params.getTerms(), params.getOffset(), params.getLimit())
                            )
                    );
                } else {
                    // find by names, descriptions
                    services = new ResourceList<>(
                            ServiceWrapper.wrap(
                                    serviceFacade.findByMultipleCriteriaEagerly(params.getNames(), params.getDescriptions(),
                                            params.getServiceCategories(), params.getProviders(), params.getEmployees(),
                                            workStations, params.getServicePoints(), params.getEmployeeTerms(), params.getTerms(),
                                            params.getOffset(), params.getLimit())
                            )
                    );
                }
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get services eagerly for given work station without filtering (eventually paginated)
                services = new ResourceList<>( ServiceWrapper.wrap(serviceFacade.findByWorkStationEagerly(workStation, params.getOffset(), params.getLimit())) );
            }

            utx.commit();

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServiceResource.populateWithHATEOASLinks(services, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(services).build();
        }

        /**
         * Method that counts Service entities for given Work Station.
         * The provider id, service point number and work station number
         * are passed through path params.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countServicesByWorkStation( @PathParam("providerId") Long providerId,
                                                    @PathParam("servicePointNumber") Integer servicePointNumber,
                                                    @PathParam("workStationNumber") Integer workStationNumber,
                                                    @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of services for given work station by executing " +
                    "WorkStationResource.ServiceResource.countServicesByWorkStation(providerId, servicePointNumber, workStationNumber) method REST API");

            utx.begin();

            // find work station entity for which to count services
            WorkStation workStation = workStationFacade.find( new WorkStationId(providerId, servicePointNumber, workStationNumber) );
            if(workStation == null)
                throw new NotFoundException("Could not find work station for id (" + providerId + "," + servicePointNumber + "," + workStationNumber + ").");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(serviceFacade.countByWorkStation(workStation)), 200,
                    "number of services for work station with id (" + providerId + "," + servicePointNumber + "," + workStationNumber + ").");

            utx.commit();

            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    public class EmployeeTermResource {

        public EmployeeTermResource() { }

        /**
         * Method returns subset of Employee Term entities for given Work Station entity.
         * The provider id, service point number and work station number are passed through path params.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getWorkStationEmployeeTerms( @PathParam("providerId") Long providerId,
                                                     @PathParam("servicePointNumber") Integer servicePointNumber,
                                                     @PathParam("workStationNumber") Integer workStationNumber,
                                                     @BeanParam EmployeeTermBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employee terms for given work station using " +
                    "WorkStationResource.EmployeeTermResource.getWorkStationEmployeeTerms(providerId, servicePointNumber, workStationNumber) method of REST API");

            utx.begin();

            // find work station entity for which to get associated employee terms
            WorkStation workStation = workStationFacade.find( new WorkStationId(providerId, servicePointNumber, workStationNumber) );
            if(workStation == null)
                throw new NotFoundException("Could not find work station for id (" + providerId + "," + servicePointNumber + "," + workStationNumber + ").");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<EmployeeTerm> employeeTerms = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<WorkStation> workStations = new ArrayList<>();
                workStations.add(workStation);

                // get employee terms for given work station filtered by given query params
                employeeTerms = new ResourceList<>(
                        employeeTermFacade.findByMultipleCriteria(params.getServicePoints(), workStations, params.getEmployees(),
                                params.getTerms(), params.getServices(), params.getProviderServices(), params.getPeriod(), params.getStrictTerm(),
                                params.getOffset(), params.getLimit())
                );

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get employee terms for given work station without filtering (eventually paginated)
                employeeTerms = new ResourceList<>( employeeTermFacade.findByWorkStation(workStation, params.getOffset(), params.getLimit()) );
            }

            utx.commit();

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeTermResource.populateWithHATEOASLinks(employeeTerms, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employeeTerms).build();
        }

        /**
         * Method that counts Employee Term entities for given Work Station resource.
         * The provider id, service point number and work station number are passed through path params.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countEmployeeTermsByWorkStation( @PathParam("providerId") Long providerId,
                                                         @PathParam("servicePointNumber") Integer servicePointNumber,
                                                         @PathParam("workStationNumber") Integer workStationNumber,
                                                         @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of employee terms for given work station by executing " +
                    "WorkStationResource.EmployeeTermResource.countEmployeeTermsByWorkStation(providerId, servicePointNumber, workStationNumber) method of REST API");

            utx.begin();

            // find work station entity for which to count employee terms
            WorkStation workStation = workStationFacade.find( new WorkStationId(providerId, servicePointNumber, workStationNumber) );
            if(workStation == null)
                throw new NotFoundException("Could not find work station for id (" + providerId + "," + servicePointNumber + "," + workStationNumber + ").");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(employeeTermFacade.countByWorkStation(workStation)), 200,
                    "number of employee terms for work station with id (" + workStation.getServicePoint().getProvider().getUserId() + ","
                            + workStation.getServicePoint().getServicePointNumber() + "," + workStation.getWorkStationNumber() + ")");

            utx.commit();

            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    public class TermResource {

        public TermResource() { }

        /**
         * Method returns subset of Term entities for given Work Station entity.
         * The provider id, service point number and work station number are passed through path params.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getWorkStationTerms( @PathParam("providerId") Long providerId,
                                             @PathParam("servicePointNumber") Integer servicePointNumber,
                                             @PathParam("workStationNumber") Integer workStationNumber,
                                             @BeanParam TermBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning terms for given work station using " +
                    "WorkStationResource.TermResource.getWorkStationTerms(providerId, servicePointNumber, workStationNumber) method of REST API");

            utx.begin();

            // find work station entity for which to get associated terms
            WorkStation workStation = workStationFacade.find(new WorkStationId(providerId, servicePointNumber, workStationNumber));
            if (workStation == null)
                throw new NotFoundException("Could not find work station for id (" + providerId + "," + servicePointNumber + "," + workStationNumber + ").");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<Term> terms = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<WorkStation> workStations = new ArrayList<>();
                workStations.add(workStation);

                // get terms for given work station filtered by given query params
                terms = new ResourceList<>(
                        termFacade.findByMultipleCriteria(params.getServicePoints(), workStations, params.getEmployees(),
                                params.getServices(), params.getProviderServices(), params.getPeriod(), params.getStrictTerm(),
                                params.getOffset(), params.getLimit())
                );
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get terms for given work station without filtering (eventually paginated)
                terms = new ResourceList<>( termFacade.findByWorkStation(workStation, params.getOffset(), params.getLimit()) );
            }

            utx.commit();

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.TermResource.populateWithHATEOASLinks(terms, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(terms).build();
        }

        /**
         * Method returns subset of Term entities for given Work Station fetching them eagerly.
         * The provider id, service point number and work station number are passed through path params.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getWorkStationTermsEagerly( @PathParam("providerId") Long providerId,
                                                    @PathParam("servicePointNumber") Integer servicePointNumber,
                                                    @PathParam("workStationNumber") Integer workStationNumber,
                                                    @BeanParam TermBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning terms eagerly for given work station using " +
                    "WorkStationResource.TermResource.getWorkStationTermsEagerly(providerId, servicePointNumber, workStationNumber) method of REST API");

            utx.begin();

            // find work station entity for which to get associated terms
            WorkStation workStation = workStationFacade.find(new WorkStationId(providerId, servicePointNumber, workStationNumber));
            if (workStation == null)
                throw new NotFoundException("Could not find work station for id (" + providerId + "," + servicePointNumber + "," + workStationNumber + ").");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<TermWrapper> terms = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<WorkStation> workStations = new ArrayList<>();
                workStations.add(workStation);

                // get terms eagerly for given work station filtered by given query params
                terms = new ResourceList<>(
                        TermWrapper.wrap(
                                termFacade.findByMultipleCriteriaEagerly(params.getServicePoints(), workStations, params.getEmployees(),
                                        params.getServices(), params.getProviderServices(), params.getPeriod(), params.getStrictTerm(),
                                        params.getOffset(), params.getLimit())
                        )
                );

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get terms eagerly for given work station without filtering (eventually paginated)
                terms = new ResourceList<>( TermWrapper.wrap(termFacade.findByWorkStationEagerly(workStation, params.getOffset(), params.getLimit())) );
            }

            utx.commit();

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.TermResource.populateWithHATEOASLinks(terms, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(terms).build();
        }

        /**
         * Method that counts Term entities for given Work Station resource.
         * The provider id, service point number and work station number are passed through path params.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countTermsByWorkStation( @PathParam("providerId") Long providerId,
                                                 @PathParam("servicePointNumber") Integer servicePointNumber,
                                                 @PathParam("workStationNumber") Integer workStationNumber,
                                                 @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of terms for given work station by executing " +
                    "WorkStationResource.TermResource.countTermsByWorkStation(providerId, servicePointNumber, workStationNumber) method of REST API");

            utx.begin();

            // find work station entity for which to count terms
            WorkStation workStation = workStationFacade.find( new WorkStationId(providerId, servicePointNumber, workStationNumber) );
            if(workStation == null)
                throw new NotFoundException("Could not find work station for id (" + providerId + "," + servicePointNumber + "," + workStationNumber + ").");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(termFacade.countByWorkStation(workStation)), 200,
                    "number of terms for work station with id (" + workStation.getServicePoint().getProvider().getUserId() + ","
                            + workStation.getServicePoint().getServicePointNumber() + "," + workStation.getWorkStationNumber() + ")");

            utx.commit();

            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }
}