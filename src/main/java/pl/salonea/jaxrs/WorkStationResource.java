package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.EmployeeFacade;
import pl.salonea.ejb.stateless.WorkStationFacade;
import pl.salonea.entities.Employee;
import pl.salonea.entities.ProviderService;

import pl.salonea.entities.WorkStation;
import pl.salonea.entities.idclass.WorkStationId;
import pl.salonea.jaxrs.bean_params.DateBetweenBeanParam;
import pl.salonea.jaxrs.bean_params.EmployeeBeanParam;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.bean_params.WorkStationBeanParam;
import pl.salonea.jaxrs.exceptions.BadRequestException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.EmployeeWrapper;
import pl.salonea.jaxrs.wrappers.WorkStationWrapper;

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

    /**
     * Alternative methods to access Work Station resource
     */

    // TODO

    /**
     * Method returns all Work Station entities.
     * They can be additionally filtered and paginated by @QueryParams.
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getWorkStations( @BeanParam WorkStationBeanParam params ) throws ForbiddenException {

        // TODO
        return null;
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

        return null;
    }

    // TODO other methods

    /**
     * related subresources (through relationships)
     */
    @Path("/{providerId: \\d+}+{servicePointNumber: \\d+}+{workStationNumber: \\d+}/employees")
    public EmployeeResource getEmployeeResource() { return new EmployeeResource(); }

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
            Method workStationsEagerlyMethod = null;
            // TODO

            // get subset of resources hypermedia links


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

      // TODO
      //  for(TermEmployeeWorkOn employeeTerm : workStationWrapper.getTermsEmployeesWorkOn())
      //      pl.salonea.jaxrs.EmployeeTermResource.populateWithHATEOASLinks(employeeTerm, uriInfo);

        for(ProviderService providerService : workStationWrapper.getProvidedServices())
            pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(providerService, uriInfo);
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(WorkStation workStation, UriInfo uriInfo) {

        try {

            // self link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/{sub-id}/{sub-subresources}/{sub-sub-id}

            // collection link with pattern: http://localhost:port/app/rest/{resources}
            workStation.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(WorkStationResource.class)
                    .build())
                    .rel("work-stations").build());

            // self eagerly link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/{sub-id}/{sub-subresources}/{sub-sub-id}/eagerly

            // associated collections links with pattern: http://localhost:port/app/rest/{resources}/{id}/{relationship}

            /**
             * Provider Services associated with current Work Station resource
             */

            /**
             * Employee Terms (TermEmployeeWorkOn entity) associated with current Work Station resource
             */

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

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        // TODO
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

}
