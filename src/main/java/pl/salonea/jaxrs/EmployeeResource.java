package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.*;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.*;
import pl.salonea.entities.Transaction;
import pl.salonea.jaxrs.bean_params.*;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.transaction.*;
import javax.transaction.NotSupportedException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import static javax.ws.rs.core.Response.Status;

import pl.salonea.jaxrs.exceptions.*;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.exceptions.BadRequestException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.*;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by michzio on 14/10/2015.
 */
@Path("/employees")
public class EmployeeResource {

    private static final Logger logger = Logger.getLogger(EmployeeResource.class.getName());

    @Inject
    private UserTransaction utx;

    @Inject
    private EmployeeFacade employeeFacade;
    @Inject
    private EducationFacade educationFacade;
    @Inject
    private SkillFacade skillFacade;
    @Inject
    private ClientFacade clientFacade;
    @Inject
    private EmployeeRatingFacade employeeRatingFacade;
    @Inject
    private ServicePointFacade servicePointFacade;
    @Inject
    private WorkStationFacade workStationFacade;
    @Inject
    private ServiceFacade serviceFacade;
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
     * Method returns all Employee resources
     * They can be additionally filtered or paginated by @QueryParams
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getEmployees( @BeanParam EmployeeBeanParam params ) throws ForbiddenException,
    /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Employees by executing EmployeeResource.getEmployees() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<Employee> employees = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            utx.begin();

            // get employees filtered by criteria provided in query params
            employees = new ResourceList<>(
                    employeeFacade.findByMultipleCriteria(params.getDescriptions(), params.getJobPositions(), params.getSkills(),
                            params.getEducations(), params.getServices(), params.getProviderServices(), params.getServicePoints(),
                            params.getWorkStations(), params.getPeriod(), params.getStrictTerm(), params.getTerms(), params.getRated(),
                            params.getMinAvgRating(), params.getMaxAvgRating(), params.getRatingClients(), params.getOffset(), params.getLimit())
            );

            utx.commit();

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all employees without filtering (eventually paginated)
            employees = new ResourceList<>( employeeFacade.findAll(params.getOffset(), params.getLimit()) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        EmployeeResource.populateWithHATEOASLinks(employees, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(employees).build();
    }

    @GET
    @Path("/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getEmployeesEagerly( @BeanParam EmployeeBeanParam params ) throws ForbiddenException,
    /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Employees eagerly by executing EmployeeResource.getEmployeesEagerly() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<EmployeeWrapper> employees = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            utx.begin();

            // get employees eagerly filtered by criteria provided in query params
            employees = new ResourceList<>(
                    EmployeeWrapper.wrap(
                            employeeFacade.findByMultipleCriteriaEagerly(params.getDescriptions(), params.getJobPositions(), params.getSkills(),
                                    params.getEducations(), params.getServices(), params.getProviderServices(), params.getServicePoints(),
                                    params.getWorkStations(), params.getPeriod(), params.getStrictTerm(), params.getTerms(),params.getRated(),
                                    params.getMinAvgRating(), params.getMaxAvgRating(), params.getRatingClients(), params.getOffset(), params.getLimit())
                    )
            );

            utx.commit();

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all employees eagerly without filtering (eventually paginated)
            employees = new ResourceList<>( EmployeeWrapper.wrap(employeeFacade.findAllEagerly(params.getOffset(), params.getLimit())) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        EmployeeResource.populateWithHATEOASLinks(employees, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(employees).build();
    }

    /**
     * Method matches specific Employee resource by identifier and returns its instance.
     */
    @GET
    @Path("/{userId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getEmployee( @PathParam("userId") Long userId,
                                 @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning given Employee by executing EmployeeResource.getEmployee(userId) method of REST API");

        Employee foundEmployee = employeeFacade.find(userId);
        if (foundEmployee == null)
            throw new NotFoundException("Could not find employee for id " + userId + ".");

        // adding hypermedia links to employee resource
        EmployeeResource.populateWithHATEOASLinks(foundEmployee, params.getUriInfo());

        return Response.status(Status.OK).entity(foundEmployee).build();
    }

    /**
     * Method matches specific Employee resource by identifier and returns its instance fetching it eagerly
     */
    @GET
    @Path("/{userId : \\d+}/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getEmployeeEagerly( @PathParam("userId") Long userId,
                                        @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning given Employee eagerly by executing EmployeeResource.getEmployeeEagerly(userId) method of REST API");

        Employee foundEmployee = employeeFacade.findByIdEagerly(userId);
        if (foundEmployee == null)
            throw new NotFoundException("Could not find employee for id " + userId + ".");

        // wrapping Employee into EmployeeWrapper in order to marshall eagerly fetched associated collections of entities
        EmployeeWrapper wrappedEmployee = new EmployeeWrapper(foundEmployee);

        // adding hypermedia links to wrapped employee resource
        EmployeeResource.populateWithHATEOASLinks(wrappedEmployee, params.getUriInfo());

        return Response.status(Status.OK).entity(wrappedEmployee).build();
    }

    /**
     * Method that takes Employee as XML or JSON and creates its new instance in database
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createEmployee( Employee employee,
                                    @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "creating new Employee by executing EmployeeResource.createEmployee(employee) method of REST API");

        if(employee.getRegistrationDate() == null) {
            // if registration date of newly created employee hasn't been set by client set it now to the current datetime value
            employee.setRegistrationDate(new Date());
        }

        Employee createdEmployee = null;
        URI locationURI = null;

        try {
            // persist new resource in database
            createdEmployee = employeeFacade.create(employee);

            // populate created resource with hypermedia links
            EmployeeResource.populateWithHATEOASLinks(createdEmployee, params.getUriInfo());

            // construct link to newly created resource to return in HTTP Header
            String createdEmployeeId = String.valueOf(createdEmployee.getUserId());
            locationURI = params.getUriInfo().getBaseUriBuilder().path(EmployeeResource.class).path(createdEmployeeId).build();

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_CREATION_ERROR_MESSAGE);
        }

        return Response.created(locationURI).entity(createdEmployee).build();
    }

    /**
     * Method that takes updated Employee as XML or JSON and its ID as path param.
     * It updates Employee in database for provided ID.
     */
    @PUT
    @Path("/{userId : \\d+}") // catch only numeric identifiers
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateEmployee( @PathParam("userId") Long userId,
                                    Employee employee,
                                    @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "updating existing Employee by executing EmployeeResource.updateEmployee(userId, employee) method of REST API");

        // set resource ID passed in path param on updated resource object
        employee.setUserId(userId);

        Employee updatedEmployee = null;
        try {
            // reflect updated resource object in database
            updatedEmployee = employeeFacade.update(employee, true);
            // populate created resource with hypermedia links
            EmployeeResource.populateWithHATEOASLinks(updatedEmployee, params.getUriInfo());

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_UPDATE_ERROR_MESSAGE);
        }

        return Response.status(Status.OK).entity(updatedEmployee).build();
    }

    /**
     * Method that removes Employee entity from database for given ID.
     * The ID is passed through path param.
     */
    @DELETE
    @Path("/{userId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeEmployee( @PathParam("userId") Long userId,
                                    @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing given Employee by executing EmployeeResource.removeEmployee(userId) method of REST API");

        // find Employee entity that should be deleted
        Employee toDeleteEmployee = employeeFacade.find(userId);
        // throw exception if entity hasn't been found
        if(toDeleteEmployee == null)
            throw new NotFoundException("Could not find employee to delete for given id: " + userId + ".");

        // remove entity from database
        employeeFacade.remove(toDeleteEmployee);

        return Response.status(Status.NO_CONTENT).build();
    }

    /**
     * Additional methods returning a subset of resources based on given criteria
     * You can also achieve similar results by applying @QueryParams to generic method
     * returning all resources in order to filter and limit them.
     */

    /**
     * Method returns number of Employee entities in database
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countEmployees( @BeanParam GenericBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning number of employees by executing EmployeeResource.countEmployees() method of REST API");

        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(employeeFacade.count()), 200, "number of employees");
        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method returns subset of Employee entities for given description.
     * The description is passed through path param.
     */
    @GET
    @Path("/described/{description : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getEmployeesByDescription( @PathParam("description") String description,
                                               @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning employees for given description using EmployeeResource.getEmployeesByDescription(description) method of REST API");

        // find employees by given criteria
        ResourceList<Employee> employees = new ResourceList<>( employeeFacade.findByDescription(description, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        EmployeeResource.populateWithHATEOASLinks(employees, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(employees).build();
    }

    /**
     * Method returns subset of Employee entities for given job position.
     * The job position is passed through path param.
     */
    @GET
    @Path("/holding-position/{jobPosition : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getEmployeesByJobPosition( @PathParam("jobPosition") String jobPosition,
                                               @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning employees for given job position using EmployeeResource.getEmployeesByJobPosition(jobPosition) method of REST API");

        // find employees by given criteria
        ResourceList<Employee> employees = new ResourceList<>( employeeFacade.findByJobPosition(jobPosition, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        EmployeeResource.populateWithHATEOASLinks(employees, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(employees).build();
    }

    /**
     * related subresources (through relationships)
     */

    @Path("/{userId: \\d+}/educations")
    public EducationResource getEducationResource() { return new EducationResource(); }
    @Path("/{userId: \\d+}/skills")
    public SkillResource getSkillResource() { return new SkillResource(); }
    @Path("/{userId: \\d+}/rating-clients")
    public ClientResource getClientResource() { return new ClientResource(); }
    @Path("/{userId: \\d+}/employee-ratings")
    public EmployeeRatingResource getEmployeeRatingResource() {
        return new EmployeeRatingResource();
    }
    @Path("/{userId: \\d+}/service-points")
    public ServicePointResource getServicePointResource() {
        return new ServicePointResource();
    }
    @Path("/{userId: \\d+}/work-stations")
    public WorkStationResource getWorkStationResource() {
        return new WorkStationResource();
    }
    @Path("/{userId: \\d+}/services")
    public ServiceResource getServiceResource() {
        return new ServiceResource();
    }
    @Path("/{userId: \\d+}/provider-services")
    public ProviderServiceResource getProviderServiceResource() {
        return new ProviderServiceResource();
    }
    @Path("/{userId: \\d+}/employee-terms")
    public EmployeeTermResource getEmployeeTermResource() { return new EmployeeTermResource(); }
    @Path("/{userId: \\d+}/terms")
    public TermResource getTermResource() { return new TermResource(); }
    @Path("/{userId: \\d+}/transactions")
    public TransactionResource getTransactionResource() { return new TransactionResource(); }
    @Path("/{userId: \\d+}/historical-transactions")
    public HistoricalTransactionResource getHistoricalTransactionResource() { return new HistoricalTransactionResource(); }

    // helper methods e.g. to populate resources/resource lists with HATEOAS links

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList employees, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(employees, uriInfo, offset, limit);

        try {
            // count resources hypermedia link
            Method countMethod = EmployeeResource.class.getMethod("countEmployees", GenericBeanParam.class);
            employees.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(EmployeeResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            employees.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(EmployeeResource.class).build()).rel("employees").build() );

            // get all resources eagerly hypermedia link
            Method employeesEagerlyMethod = EmployeeResource.class.getMethod("getEmployeesEagerly", EmployeeBeanParam.class);
            employees.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(EmployeeResource.class).path(employeesEagerlyMethod).build()).rel("employees-eagerly").build() );

            // get subset of resources hypermedia links

            // described
            employees.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path("described")
                    .build())
                    .rel("described").build() );

            // holding-position
            employees.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path("holding-position")
                    .build())
                    .rel("holding-position").build() );

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for(Object object : employees.getResources()) {
            if(object instanceof Employee) {
                EmployeeResource.populateWithHATEOASLinks((Employee) object, uriInfo);
            } else if (object instanceof EmployeeWrapper) {
                EmployeeResource.populateWithHATEOASLinks((EmployeeWrapper) object, uriInfo);
            }

        }
    }

    /**
     * This method enables to populate each individual resource wrapper with hypermedia links
     */
    public static void populateWithHATEOASLinks(EmployeeWrapper employeeWrapper, UriInfo uriInfo) {

        EmployeeResource.populateWithHATEOASLinks(employeeWrapper.getEmployee(), uriInfo);

       for(Education education : employeeWrapper.getEducations())
            pl.salonea.jaxrs.EducationResource.populateWithHATEOASLinks(education, uriInfo);

       for(Skill skill : employeeWrapper.getSkills())
           pl.salonea.jaxrs.SkillResource.populateWithHATEOASLinks(skill, uriInfo);

       for(ProviderService providerService : employeeWrapper.getSuppliedServices())
           pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(providerService, uriInfo);

       for(EmployeeTerm employeeTerm : employeeWrapper.getTermsOnWorkStation())
           pl.salonea.jaxrs.EmployeeTermResource.populateWithHATEOASLinks(employeeTerm, uriInfo);

       for(EmployeeRating employeeRating : employeeWrapper.getReceivedRatings())
           pl.salonea.jaxrs.EmployeeRatingResource.populateWithHATEOASLinks(employeeRating, uriInfo);

    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(Employee employee, UriInfo uriInfo) {

        // self link with pattern: http://localhost:port/app/rest/{resources}/{id}
        employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                .path(EmployeeResource.class)
                .path(employee.getUserId().toString())
                .build())
                .rel("self").build());

        // collection link with pattern: http://localhost:port/app/rest/{resources}
        employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                .path(EmployeeResource.class)
                .build())
                .rel("employees").build());

        try {
            // self eagerly link with pattern http://localhost:port/app/rest/{resources}/{id}/eagerly
            Method employeeEagerlyMethod = EmployeeResource.class.getMethod("getEmployeeEagerly", Long.class, GenericBeanParam.class);
            employee.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                        .path(EmployeeResource.class)
                        .path(employeeEagerlyMethod)
                        .resolveTemplate("userId", employee.getUserId().toString())
                        .build())
                        .rel("employee-eagerly").build() );

            // associated collections links with pattern: http://localhost:port/app/rest/{resources}/{id}/{relationship}

            /**
             * Educations associated with current Employee resource
             */

            // educations link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}
            Method educationsMethod = EmployeeResource.class.getMethod("getEducationResource");
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(educationsMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("educations").build());

            // educations eagerly link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/eagerly
            Method educationsEagerlyMethod = EmployeeResource.EducationResource.class.getMethod("getEmployeeEducationsEagerly", Long.class, EducationBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(educationsMethod)
                    .path(educationsEagerlyMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("educations-eagerly").build());

            // educations count link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/count
            Method countEmployeeEducationsMethod = EmployeeResource.EducationResource.class.getMethod("countEmployeeEducations", Long.class, GenericBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(educationsMethod)
                    .path(countEmployeeEducationsMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("educations-count").build());

            // educations containing-keyword link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/containing-keyword
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(educationsMethod)
                    .path("containing-keyword")
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("educations-containing-keyword").build());

            /**
             * Skills associated with current Employee resource
             */

            // skills link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}
            Method skillsMethod = EmployeeResource.class.getMethod("getSkillResource");
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(skillsMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("skills").build());

            // skills eagerly link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/eagerly
            Method skillsEagerlyMethod = EmployeeResource.SkillResource.class.getMethod("getEmployeeSkillsEagerly", Long.class, SkillBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(skillsMethod)
                    .path(skillsEagerlyMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("skills-eagerly").build());

            // skills count link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/count
            Method countEmployeeSkillsMethod = EmployeeResource.SkillResource.class.getMethod("countEmployeeSkills", Long.class, GenericBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(skillsMethod)
                    .path(countEmployeeSkillsMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("skills-count").build());

            // skills containing-keyword link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/containing-keyword
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(skillsMethod)
                    .path("containing-keyword")
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("skills-containing-keyword").build());

            /**
             * Employee Ratings associated with current Employee resource
             */

            // employee-ratings
            Method employeeRatingsMethod = EmployeeResource.class.getMethod("getEmployeeRatingResource");
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(employeeRatingsMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("employee-ratings").build());

            // employee-ratings count link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/count
            Method countEmployeeRatingsByEmployeeMethod = EmployeeResource.EmployeeRatingResource.class.getMethod("countEmployeeRatings", Long.class, GenericBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(employeeRatingsMethod)
                    .path(countEmployeeRatingsByEmployeeMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("employee-ratings-count").build());

            // employee-ratings average rating link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/average-rating
            Method employeeAverageRatingMethod = EmployeeResource.EmployeeRatingResource.class.getMethod("getAverageEmployeeRating", Long.class, GenericBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(employeeRatingsMethod)
                    .path(employeeAverageRatingMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("employee-ratings-average-rating").build());

            // employee-ratings rated link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/rated
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(employeeRatingsMethod)
                    .path("rated")
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("employee-ratings-rated").build());

            // employee-ratings rated-above link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/rated-above
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(employeeRatingsMethod)
                    .path("rated-above")
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("employee-ratings-rated-above").build());

            // employee-ratings rated-below link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/rated-below
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(employeeRatingsMethod)
                    .path("rated-below")
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("employee-ratings-rated-below").build());

            /**
             * Clients rating current Employee resource
             */

            // rating-clients
            Method ratingClientsMethod = EmployeeResource.class.getMethod("getClientResource");
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(ratingClientsMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("rating-clients").build());


            // rating-clients eagerly
            Method ratingClientsEagerlyMethod = ClientResource.class.getMethod("getEmployeeRatingClientsEagerly", Long.class, ClientBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(ratingClientsMethod)
                    .path(ratingClientsEagerlyMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("rating-clients-eagerly").build());

            /**
             * Service Points associated with current Employee resource
             */

            // service-points relationship
            Method servicePointsMethod = EmployeeResource.class.getMethod("getServicePointResource");
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(servicePointsMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("service-points").build());

            // service-points eagerly relationship
            Method servicePointsEagerlyMethod = EmployeeResource.ServicePointResource.class.getMethod("getEmployeeServicePointsEagerly", Long.class, ServicePointBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(servicePointsMethod)
                    .path(servicePointsEagerlyMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("service-points-eagerly").build());

            // service-points count link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/count
            Method countServicePointsByEmployeeMethod = EmployeeResource.ServicePointResource.class.getMethod("countServicePointsByEmployee", Long.class, GenericBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(servicePointsMethod)
                    .path(countServicePointsByEmployeeMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("service-points-count").build());

            // service-points address link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/address
            Method addressMethod = EmployeeResource.ServicePointResource.class.getMethod("getEmployeeServicePointsByAddress", Long.class, AddressBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(servicePointsMethod)
                    .path(addressMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("service-points-address").build());

            // service-points coordinates-square link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/coordinates-square
            Method coordinatesSquareMethod = EmployeeResource.ServicePointResource.class.getMethod("getEmployeeServicePointsByCoordinatesSquare", Long.class, CoordinatesSquareBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(servicePointsMethod)
                    .path(coordinatesSquareMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("service-points-coordinates-square").build());

            // service-points coordinates-circle link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/coordinates-circle
            Method coordinatesCircleMethod = EmployeeResource.ServicePointResource.class.getMethod("getEmployeeServicePointsByCoordinatesCircle", Long.class, CoordinatesCircleBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(servicePointsMethod)
                    .path(coordinatesCircleMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("service-points-coordinates-circle").build());

            /**
             * Work Stations associated with current Employee resource
             */

            // work-stations
            Method workStationsMethod = EmployeeResource.class.getMethod("getWorkStationResource");
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(workStationsMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("work-stations").build());

            // work-stations eagerly
            Method workStationsEagerlyMethod = EmployeeResource.WorkStationResource.class.getMethod("getEmployeeWorkStationsEagerly", Long.class, WorkStationBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(workStationsMethod)
                    .path(workStationsEagerlyMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("work-stations-eagerly").build());

            // work-stations count
            Method countWorkStationsByEmployeeMethod = EmployeeResource.WorkStationResource.class.getMethod("countWorkStationsByEmployee", Long.class, GenericBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(workStationsMethod)
                    .path(countWorkStationsByEmployeeMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("work-stations-count").build());

            // work-stations by-term
            Method workStationsByTermMethod = EmployeeResource.WorkStationResource.class.getMethod("getEmployeeWorkStationsByTerm", Long.class, DateRangeBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(workStationsMethod)
                    .path(workStationsByTermMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("work-stations-by-term").build());

            // work-stations by-term-strict
            Method workStationsByTermStrictMethod = EmployeeResource.WorkStationResource.class.getMethod("getEmployeeWorkStationsByTermStrict", Long.class, DateRangeBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(workStationsMethod)
                    .path(workStationsByTermStrictMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("work-stations-by-term-strict").build());

            /**
             * Services being executed by current Employee resource
             */

            // services link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}
            Method servicesMethod = EmployeeResource.class.getMethod("getServiceResource");
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(servicesMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("services").build());

            // services eagerly link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/eagerly
            Method servicesEagerlyMethod = EmployeeResource.ServiceResource.class.getMethod("getEmployeeServicesEagerly", Long.class, ServiceBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(servicesMethod)
                    .path(servicesEagerlyMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("services-eagerly").build());

            // services count link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/count
            Method countServicesByEmployeeMethod = EmployeeResource.ServiceResource.class.getMethod("countServicesByEmployee", Long.class, GenericBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(servicesMethod)
                    .path(countServicesByEmployeeMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("services-count").build());

            /**
             * Provider Services being executed by current Employee resource
             */

            // provider-services link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}
            Method providerServicesMethod = EmployeeResource.class.getMethod("getProviderServiceResource");
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(providerServicesMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("provider-services").build());

            // provider-services eagerly link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/eagerly
            Method providerServicesEagerlyMethod = EmployeeResource.ProviderServiceResource.class.getMethod("getEmployeeProviderServicesEagerly", Long.class, ProviderServiceBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(providerServicesMethod)
                    .path(providerServicesEagerlyMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("provider-services-eagerly").build());

            // provider-services count link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/count
            Method countProviderServicesByEmployeeMethod = EmployeeResource.ProviderServiceResource.class.getMethod("countProviderServicesByEmployee", Long.class, GenericBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(providerServicesMethod)
                    .path(countProviderServicesByEmployeeMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("provider-services-count").build());

            /**
             * Employee Terms defined for current Employee resource
             */

            // employee-terms
            Method employeeTermsMethod = EmployeeResource.class.getMethod("getEmployeeTermResource");
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(employeeTermsMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("employee-terms").build());

            // employee-terms count
            Method countEmployeeTermsByEmployeeMethod = EmployeeResource.EmployeeTermResource.class.getMethod("countEmployeeTermsByEmployee", Long.class, GenericBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(employeeTermsMethod)
                    .path(countEmployeeTermsByEmployeeMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("employee-terms-count").build());

            /**
             * Terms associated with current Employee resource
             */

            // terms
            Method termsMethod = EmployeeResource.class.getMethod("getTermResource");
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(termsMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("terms").build());

            // terms eagerly
            Method termsEagerlyMethod = EmployeeResource.TermResource.class.getMethod("getEmployeeTermsEagerly", Long.class, TermBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(termsMethod)
                    .path(termsEagerlyMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("terms-eagerly").build());

            // terms count
            Method countTermsByEmployeeMethod = EmployeeResource.TermResource.class.getMethod("countTermsByEmployee", Long.class, GenericBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(termsMethod)
                    .path(countTermsByEmployeeMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("terms-count").build());

            /**
             * Transactions associated with current Employee resource
             */
            // transactions
            Method transactionsMethod = EmployeeResource.class.getMethod("getTransactionResource");
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(transactionsMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("transactions").build());

            // transactions eagerly
            Method transactionsEagerlyMethod = EmployeeResource.TransactionResource.class.getMethod("getEmployeeTransactionsEagerly", Long.class, TransactionBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(transactionsMethod)
                    .path(transactionsEagerlyMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("transactions-eagerly").build());

            // transactions count
            Method countTransactionsByEmployeeMethod = EmployeeResource.TransactionResource.class.getMethod("countTransactionsByEmployee", Long.class, GenericBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(transactionsMethod)
                    .path(countTransactionsByEmployeeMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("transactions-count").build() );

            /**
             * Historical Transactions associated with current Employee resource
             */
            // historical-transactions
            Method historicalTransactionsMethod = EmployeeResource.class.getMethod("getHistoricalTransactionResource");
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(historicalTransactionsMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("historical-transactions").build());

            // historical-transactions eagerly
            Method historicalTransactionsEagerlyMethod = EmployeeResource.HistoricalTransactionResource.class.getMethod("getEmployeeHistoricalTransactionsEagerly", Long.class, HistoricalTransactionBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(historicalTransactionsMethod)
                    .path(historicalTransactionsEagerlyMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("historical-transactions-eagerly").build());

            // historical-transactions count
            Method countHistoricalTransactionsByEmployeeMethod = EmployeeResource.HistoricalTransactionResource.class.getMethod("countHistoricalTransactionsByEmployee", Long.class, GenericBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(historicalTransactionsMethod)
                    .path(countHistoricalTransactionsByEmployeeMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("historical-transactions-count").build());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public class EducationResource {

        public EducationResource() { }

        /**
         * Method returns subset of Education entities for given Employee entity.
         * The employee id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeEducations( @PathParam("userId") Long employeeId,
                                               @BeanParam EducationBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning educations for given employee using EmployeeResource.EducationResource.getEmployeeEducations(employeeId) method of REST API");

            // find employee entity for which to get associated educations
            Employee employee = employeeFacade.find(employeeId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<Education> educations = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Employee> employees = new ArrayList<>();
                employees.add(employee);

                // get educations for given employee filtered by given query params

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getDegrees()) || RESTToolkit.isSet(params.getFaculties()) || RESTToolkit.isSet(params.getSchools()) )
                        throw new BadRequestException("Query params cannot include keywords and degrees, faculties or schools at the same time.");

                    // find only by keywords
                    educations = new ResourceList<>(
                            educationFacade.findByMultipleCriteria(params.getKeywords(), employees, params.getOffset(), params.getLimit())
                    );
                } else {
                    // find by degrees, faculties, schools
                    educations = new ResourceList<>(
                            educationFacade.findByMultipleCriteria(params.getDegrees(), params.getFaculties(), params.getSchools(), employees, params.getOffset(), params.getLimit())
                    );
                }
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get educations for given employee without filtering (eventually paginated)
                educations = new ResourceList<>( educationFacade.findByEmployee(employee, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EducationResource.populateWithHATEOASLinks(educations, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(educations).build();
        }

        /**
         * Method returns subset of Education entities for given Employee fetching them eagerly.
         * The employee id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeEducationsEagerly( @PathParam("userId") Long employeeId,
                                                      @BeanParam EducationBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning educations eagerly for given employee using " +
                    "EmployeeResource.EducationResource.getEmployeeEducationsEagerly(employeeId) method of REST API");

            // find employee entity for which to get associated educations
            Employee employee = employeeFacade.find(employeeId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<EducationWrapper> educations = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Employee> employees = new ArrayList<>();
                employees.add(employee);

                // get educations eagerly for given employee filtered by given params

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getDegrees()) || RESTToolkit.isSet(params.getFaculties()) || RESTToolkit.isSet(params.getSchools()) )
                        throw new BadRequestException("Query params cannot include keywords and degrees, faculties or schools at the same time.");

                    // find only by keywords
                    educations = new ResourceList<>(
                            EducationWrapper.wrap(
                                    educationFacade.findByMultipleCriteriaEagerly(params.getKeywords(), employees, params.getOffset(), params.getLimit())
                            )
                    );
                } else {
                    // find by degrees, faculties, schools
                    educations = new ResourceList<>(
                            EducationWrapper.wrap(
                                    educationFacade.findByMultipleCriteriaEagerly(params.getDegrees(), params.getFaculties(), params.getSchools(), employees, params.getOffset(), params.getLimit())
                            )
                    );
                }
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get educations eagerly for given employee without filtering (eventually paginated)
                educations = new ResourceList<>( EducationWrapper.wrap(educationFacade.findByEmployeeEagerly(employee, params.getOffset(), params.getLimit())) );

            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EducationResource.populateWithHATEOASLinks(educations, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(educations).build();
        }

        /**
         * Method that counts Education entities for given Employee resource.
         * The employee id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countEmployeeEducations( @PathParam("userId") Long employeeId,
                                                 @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of educations for given employee by executing " +
                    "EmployeeResource.EducationResource.countEmployeeEducations(employeeId) method of REST API");

            // find employee entity for which to count educations
            Employee employee = employeeFacade.find(employeeId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(educationFacade.countByEmployee(employee)), 200,
                    "number of educations for employee with id " + employee.getUserId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method returns subset of Education entities for given Employee and keyword.
         * The employee id and keyword are passed through path params.
         */
        @GET
        @Path("/containing-keyword/{keyword : \\S+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeEducationsByKeyword( @PathParam("userId") Long employeeId,
                                                        @PathParam("keyword") String keyword,
                                                        @BeanParam PaginationBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning educations for given employee and keyword using " +
                    "EmployeeResource.EducationResource.getEmployeeEducationsByKeyword(employeeId, keyword) method of REST API");

            // find employee entity for which to get associated educations
            Employee employee = employeeFacade.find(employeeId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            // find educations by given criteria (employee and keyword)
            ResourceList<Education> educations = new ResourceList<>(
                    educationFacade.findByEmployeeAndKeyword(employee, keyword, params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EducationResource.populateWithHATEOASLinks(educations, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(educations).build();
        }
    }

    public class SkillResource {

        public SkillResource() { }

        /**
         * Method returns subset of Skill entities for given Employee entity.
         * The employee id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeSkills( @PathParam("userId") Long employeeId,
                                           @BeanParam SkillBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning skills for given employee using EmployeeResource.SkillResource.getEmployeeSkills(employeeId) method of REST API");

            // find employee entity for which to get associated skills
            Employee employee = employeeFacade.find(employeeId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<Skill> skills = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Employee> employees = new ArrayList<>();
                employees.add(employee);

                // get skills for given employee filtered by given query params

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getSkillNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                        throw new BadRequestException("Query params cannot include keywords and skillNames or descriptions at the same time.");

                    // find only by keywords
                    skills = new ResourceList<>(
                            skillFacade.findByMultipleCriteria(params.getKeywords(), employees, params.getOffset(), params.getLimit())
                    );

                } else {
                    // find by skillNames, descriptions
                    skills = new ResourceList<>(
                            skillFacade.findByMultipleCriteria(params.getSkillNames(), params.getDescriptions(), employees, params.getOffset(), params.getLimit())
                    );
                }
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get skills for given employee without filtering (eventually paginated)
                skills = new ResourceList<>( skillFacade.findByEmployee(employee, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.SkillResource.populateWithHATEOASLinks(skills, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(skills).build();
        }

        /**
         * Method returns subset of Skill entities for given Employee fetching them eagerly.
         * The employee id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeSkillsEagerly( @PathParam("userId") Long employeeId,
                                                  @BeanParam SkillBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning skills eagerly for given employee using " +
                    "EmployeeResource.SkillResource.getEmployeeSkillsEagerly(employeeId) method of REST API");

            // find employee entity for which to get associated skills
            Employee employee = employeeFacade.find(employeeId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<SkillWrapper> skills = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Employee> employees = new ArrayList<>();
                employees.add(employee);

                // get skills eagerly for given employee filtered by given params

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getSkillNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                        throw new BadRequestException("Query params cannot include keywords and skillNames or descriptions at the same time.");

                    // find only by keywords
                    skills = new ResourceList<>(
                            SkillWrapper.wrap(
                                    skillFacade.findByMultipleCriteriaEagerly(params.getKeywords(), employees, params.getOffset(), params.getLimit())
                            )
                    );

                } else {
                    // find by skillNames, descriptions
                    skills = new ResourceList<>(
                            SkillWrapper.wrap(
                                    skillFacade.findByMultipleCriteriaEagerly(params.getSkillNames(), params.getDescriptions(), employees, params.getOffset(), params.getLimit())
                            )
                    );
                }
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get skills eagerly for given employee without filtering (eventually paginated)
                skills = new ResourceList<>( SkillWrapper.wrap(skillFacade.findByEmployeeEagerly(employee, params.getOffset(), params.getLimit())) );

            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.SkillResource.populateWithHATEOASLinks(skills, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(skills).build();
        }

        /**
         * Method that counts Skill entities for given Employee resource.
         * The employee id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countEmployeeSkills( @PathParam("userId") Long employeeId,
                                             @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of skills for given employee by executing " +
                    "EmployeeResource.SkillResource.countEmployeeSkills(employeeId) method of REST API");

            // find employee entity for which to count skills
            Employee employee = employeeFacade.find(employeeId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(skillFacade.countByEmployee(employee)), 200,
                    "number of skills for employee with id " + employee.getUserId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method returns subset of Skill entities for given Employee and keyword.
         * The employee id and keyword are passed through path params.
         */
        @GET
        @Path("/containing-keyword/{keyword : \\S+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeSkillsByKeyword( @PathParam("userId") Long employeeId,
                                                    @PathParam("keyword") String keyword,
                                                    @BeanParam PaginationBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning skills for given employee and keyword using " +
                    "EmployeeResource.SkillResource.getEmployeeSkillsByKeyword(employeeId, keyword) method of REST API");

            // find employee entity for which to get associated skills
            Employee employee = employeeFacade.find(employeeId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            // find skills by given criteria (employee and keyword)
            ResourceList<Skill> skills = new ResourceList<>(
                    skillFacade.findByEmployeeAndKeyword(employee, keyword, params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.SkillResource.populateWithHATEOASLinks(skills, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(skills).build();
        }

    }

    public class ClientResource {

        public ClientResource() {
        }

        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeRatingClients(@PathParam("userId") Long employeeId,
                                                 @BeanParam ClientBeanParam params) throws ForbiddenException, NotFoundException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning clients rating given employee using EmployeeResource.ClientResource.getEmployeeRatingClients(employeeId) method of REST API");

            // find employee entity for which to get rating it clients
            Employee employee = employeeFacade.find(employeeId);
            if (employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            // calculate number of filter query params
            Integer noOfParams = params.getUriInfo().getQueryParameters().size();
            if (params.getOffset() != null) noOfParams -= 1;
            if (params.getLimit() != null) noOfParams -= 1;

            ResourceList<Client> clients = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Employee> ratedEmployees = new ArrayList<>();
                ratedEmployees.add(employee);

                Address location = new Address(params.getStreet(), params.getHouseNumber(), params.getFlatNumber(), params.getZipCode(), params.getCity(), params.getState(), params.getCountry());
                Address delivery = new Address(params.getDeliveryStreet(), params.getDeliveryHouseNumber(), params.getDeliveryFlatNumber(), params.getDeliveryZipCode(), params.getDeliveryCity(), params.getDeliveryState(), params.getDeliveryCountry());

                // get clients for given rated employee filtered by given params
                clients = new ResourceList<>(
                        clientFacade.findByMultipleCriteria(params.getFirstName(), params.getLastName(), params.getFirmName(), params.getName(),
                                params.getDescription(), new HashSet<>(params.getClientTypes()), params.getOldestBirthDate(), params.getYoungestBirthDate(),
                                params.getYoungestAge(), params.getOldestAge(), location, delivery, params.getGender(), params.getRatedProviders(),
                                ratedEmployees, params.getOffset(), params.getLimit())
                );

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get clients for given rated employee without filtering
                clients = new ResourceList<>(clientFacade.findRatingEmployee(employee, params.getOffset(), params.getLimit()));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ClientResource.populateWithHATEOASLinks(clients, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Response.Status.OK).entity(clients).build();
        }

        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeRatingClientsEagerly(@PathParam("userId") Long employeeId,
                                                        @BeanParam ClientBeanParam params) throws ForbiddenException, NotFoundException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning clients rating given employee eagerly using EmployeeResource.ClientResource.getEmployeeRatingClientsEagerly(employeeId) method of REST API");

            // find employee entity for which to get rating it clients
            Employee employee = employeeFacade.find(employeeId);
            if (employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            // calculate number of filter query params
            Integer noOfParams = params.getUriInfo().getQueryParameters().size();
            if (params.getOffset() != null) noOfParams -= 1;
            if (params.getLimit() != null) noOfParams -= 1;

            ResourceList<ClientWrapper> clients = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Employee> ratedEmployees = new ArrayList<>();
                ratedEmployees.add(employee);

                Address location = new Address(params.getStreet(), params.getHouseNumber(), params.getFlatNumber(), params.getZipCode(), params.getCity(), params.getState(), params.getCountry());
                Address delivery = new Address(params.getDeliveryStreet(), params.getDeliveryHouseNumber(), params.getDeliveryFlatNumber(), params.getDeliveryZipCode(), params.getDeliveryCity(), params.getDeliveryState(), params.getDeliveryCountry());

                // get clients eagerly for given rated employee filtered by given params
                clients = new ResourceList<>(
                        ClientWrapper.wrap(
                                clientFacade.findByMultipleCriteriaEagerly(params.getFirstName(), params.getLastName(), params.getFirmName(), params.getName(),
                                        params.getDescription(), new HashSet<>(params.getClientTypes()), params.getOldestBirthDate(), params.getYoungestBirthDate(),
                                        params.getYoungestAge(), params.getOldestAge(), location, delivery, params.getGender(), params.getRatedProviders(),
                                        ratedEmployees, params.getOffset(), params.getLimit())
                        )
                );

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get clients eagerly for given rated employee without filtering
                clients = new ResourceList<>(
                        ClientWrapper.wrap(clientFacade.findRatingEmployeeEagerly(employee, params.getOffset(), params.getLimit()))
                );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ClientResource.populateWithHATEOASLinks(clients, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Response.Status.OK).entity(clients).build();
        }
    }

    public class EmployeeRatingResource {

        public EmployeeRatingResource() { }

        /**
         * Method returns subset of Employee Rating entities for given Employee.
         * The employee id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeRatings(@PathParam("userId") Long userId,
                                           @BeanParam EmployeeRatingBeanParam params) throws NotFoundException, ForbiddenException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Employee Rating entities for given Employee using EmployeeResource.EmployeeRatingResource.getEmployeeRatings(userId) method of REST API");

            // find employee entity for which to get associated employee ratings
            Employee employee = employeeFacade.find(userId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + userId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<EmployeeRating> employeeRatings = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Employee> employees = new ArrayList<>();
                employees.add(employee);

                // get employee ratings for given employee and filter params
                employeeRatings = new ResourceList<>(
                        employeeRatingFacade.findByMultipleCriteria(params.getClients(), employees, params.getMinRating(), params.getMaxRating(),
                                params.getExactRating(), params.getClientComment(), params.getEmployeeDementi(), params.getOffset(), params.getLimit())
                );

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get employee ratings for given employee without filtering
                employeeRatings = new ResourceList<>( employeeRatingFacade.findByEmployee(employee, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeRatingResource.populateWithHATEOASLinks(employeeRatings, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employeeRatings).build();
        }

        /**
         * Method that removes subset of Employee Rating entities from database for given Employee.
         * The employee id is passed through path params.
         */
        @DELETE
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response removeEmployeeRatings( @PathParam("userId") Long userId,
                                               @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "removing subset of Employee Rating entities for given Employee by executing EmployeeResource.EmployeeRatingResource.removeEmployeeRatings(userId) method of REST API");

            // find employee entity for which to remove employee ratings
            Employee employee = employeeFacade.find(userId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + userId + ".");

            // remove all specified entities from database
            Integer noOfDeleted = employeeRatingFacade.deleteByEmployee(employee);

            // create response returning number of deleted entities
            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfDeleted), 200, "number of deleted employee ratings for employee with id " + userId);

            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Additional methods returning subset of resources based on given criteria
         * You can achieve similar results by applying @QueryParams to generic method
         * returning all resources in order to filter and limit them.
         */

        /**
         * Method that counts Employee Rating entities for given Employee resource
         * The employee id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countEmployeeRatings( @PathParam("userId") Long userId,
                                              @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of employee ratings for given employee by executing EmployeeResource.EmployeeRatingResource.countEmployeeRatings(userId) method of REST API");

            // find employee entity for which to count employee ratings
            Employee employee = employeeFacade.find(userId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + userId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(employeeRatingFacade.countEmployeeRatings(employee)), 200,
                    "number of employee ratings for employee with id " + employee.getUserId());

            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method that returns average rating for Employee entity with given employee id.
         */
        @GET
        @Path("/average-rating")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getAverageEmployeeRating( @PathParam("userId") Long userId,
                                                  @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning average rating for given employee using EmployeeResource.EmployeeRatingResource.getAverageEmployeeRating(userId) method of REST API");

            // find employee entity for which to calculate average rating
            Employee employee = employeeFacade.find(userId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + userId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(employeeRatingFacade.findEmployeeAvgRating(employee)), 200,
                    "average rating for employee with id " + employee.getUserId());

            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method returns subset of Employee Rating entities for given Employee
         * that have been granted given rating.
         * The employee id and rating are passed through path params.
         */
        @GET
        @Path("/rated/{rating : \\d+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeRatingsByRating( @PathParam("userId") Long userId,
                                                    @PathParam("rating") Short rating,
                                                    @BeanParam PaginationBeanParam params ) throws ForbiddenException, NotFoundException  {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employee ratings for given employee and rating using EmployeeResource.EmployeeRatingResource.getEmployeeRatingsByRating(userId, rating) method of REST API");

            // find employee entity for which to get associated employee ratings
            Employee employee = employeeFacade.find(userId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + userId + ".");

            // find employee ratings by given criteria (employee and rating)
            ResourceList<EmployeeRating> employeeRatings = new ResourceList<>(
                    employeeRatingFacade.findForEmployeeByRating(employee, rating, params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeRatingResource.populateWithHATEOASLinks(employeeRatings, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employeeRatings).build();
        }

        /**
         * Method returns subset of Employee Rating entities for given Employee
         * rated above given minimal rating.
         * The employee id and minimal rating are passed through path params.
         */
        @GET
        @Path("/rated-above/{minRating : \\d+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeRatingsAboveMinimalRating( @PathParam("userId") Long userId,
                                                              @PathParam("minRating") Short minRating,
                                                              @BeanParam PaginationBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employee ratings for given employee rated above given minimal rating using " +
                    "EmployeeResource.EmployeeRatingResource.getEmployeeRatingsAboveMinimalRating(userId, minRating) method of REST API");

            // find employee entity for which to get associated employee ratings
            Employee employee = employeeFacade.find(userId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + userId + ".");

            // find employee ratings by given criteria (employee and min rating)
            ResourceList<EmployeeRating> employeeRatings = new ResourceList<>(
                    employeeRatingFacade.findForEmployeeAboveRating(employee, minRating, params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeRatingResource.populateWithHATEOASLinks(employeeRatings, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employeeRatings).build();
        }

        /**
         * Method returns subset of Employee Rating entities for given Employee
         * rated below given maximal rating.
         * The employee id and maximal rating are passed through path params.
         */
        @GET
        @Path("/rated-below/{maxRating : \\d+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeRatingsBelowMaximalRating( @PathParam("userId") Long userId,
                                                              @PathParam("maxRating") Short maxRating,
                                                              @BeanParam PaginationBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employee ratings for given employee rated below given maximal rating using " +
                    "EmployeeResource.EmployeeRatingResource.getEmployeeRatingsBelowMaximalRating(userId, maxRating) method of REST API");

            // find employee entity for which to get associated employee ratings
            Employee employee = employeeFacade.find(userId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + userId + ".");

            // find employee ratings by given criteria (employee and max rating)
            ResourceList<EmployeeRating> employeeRatings = new ResourceList<>(
                    employeeRatingFacade.findForEmployeeBelowRating(employee, maxRating, params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeRatingResource.populateWithHATEOASLinks(employeeRatings, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employeeRatings).build();
        }

    }

    public class ServicePointResource {

        public ServicePointResource() { }

        /**
         * Method returns subset of Service Point entities for given Employee
         * The employee id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeServicePoints( @PathParam("userId") Long userId,
                                                  @BeanParam ServicePointBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Service Point entities for given Employee using " +
                    "EmployeeResource.ServicePointResource.getEmployeeServicePoints(employeeId) method of REST API");

            // find employee entity for which to get associated service points
            Employee employee = employeeFacade.find(userId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + userId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ServicePoint> servicePoints = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Employee> employees = new ArrayList<>();
                employees.add(employee);

                utx.begin();

                if(params.getAddress() != null) {
                    if(params.getCoordinatesSquare() != null || params.getCoordinatesCircle() != null)
                        throw new BadRequestException("Query params cannot include address params and coordinates square or coordinates circle params at the same time.");
                    // only address params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(params.getProviders(), params.getServices(), params.getProviderServices(), employees,
                                    params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getAddress(), params.getTerms(),
                                    params.getOffset(), params.getLimit())
                    );

                } else if(params.getCoordinatesSquare() != null) {
                    if(params.getAddress() != null || params.getCoordinatesCircle() != null)
                        throw new BadRequestException("Query params cannot include coordinates square params and address params or coordinates circle params at the same time.");
                    // only coordinates square params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(params.getProviders(), params.getServices(), params.getProviderServices(), employees,
                                    params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesSquare(), params.getTerms(),
                                    params.getOffset(), params.getLimit())
                    );

                } else if(params.getCoordinatesCircle() != null) {
                    if(params.getAddress() != null || params.getCoordinatesSquare() != null)
                        throw  new BadRequestException("Query params cannot include coordinates circle params and address params or coordinates square params at the same time.");
                    // only coordinates circle params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(params.getProviders(), params.getServices(), params.getProviderServices(), employees,
                                    params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesCircle(), params.getTerms(),
                                    params.getOffset(), params.getLimit())
                    );

                } else {
                    // no location params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(params.getProviders(), params.getServices(), params.getProviderServices(), employees,
                                    params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getTerms(),
                                    params.getOffset(), params.getLimit())
                    );
                }

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                servicePoints = new ResourceList<>( servicePointFacade.findByEmployee(employee, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(servicePoints).build();
        }

        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeServicePointsEagerly( @PathParam("userId") Long userId,
                                                         @BeanParam ServicePointBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Service Point entities for given Employee eagerly using " +
                    "EmployeeResource.ServicePointResource.getEmployeeServicePointsEagerly(employeeId) method of REST API");

            // find employee entity for which to get associated service points
            Employee employee = employeeFacade.find(userId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + userId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ServicePointWrapper> servicePoints = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Employee> employees = new ArrayList<>();
                employees.add(employee);

                utx.begin();

                if(params.getAddress() != null) {
                    if(params.getCoordinatesSquare() != null || params.getCoordinatesCircle() != null)
                        throw new BadRequestException("Query params cannot include address params and coordinates square params or coordinates circle params at the same time.");
                    // only address params
                    servicePoints = new ResourceList<>(
                            ServicePointWrapper.wrap(
                                    servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getServices(), params.getProviderServices(), employees,
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
                                    servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getServices(), params.getProviderServices(), employees,
                                            params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesSquare(), params.getTerms(),
                                            params.getOffset(), params.getLimit())
                            )
                    );
                } else if(params.getCoordinatesCircle() != null) {
                    if(params.getAddress() != null || params.getCoordinatesSquare() != null)
                        throw new BadRequestException("Query params cannot include coordinates circle params and address params or coordinates square params at the same time.");
                    // only coordinates circle params
                    servicePoints = new ResourceList<>(
                            ServicePointWrapper.wrap(
                                    servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getServices(), params.getProviderServices(), employees,
                                            params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesCircle(), params.getTerms(),
                                            params.getOffset(), params.getLimit())
                            )
                    );
                } else {
                    // no location params
                    servicePoints = new ResourceList<>(
                            ServicePointWrapper.wrap(
                                    servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getServices(), params.getProviderServices(), employees,
                                            params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getTerms(),
                                            params.getOffset(), params.getLimit())
                            )
                    );
                }

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                servicePoints = new ResourceList<>( ServicePointWrapper.wrap(servicePointFacade.findByEmployeeEagerly(employee, params.getOffset(), params.getLimit())) );
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
         * Method that counts Service Point entities for given Employee resource.
         * The employee id is passed through path params.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countServicePointsByEmployee( @PathParam("userId") Long userId,
                                                      @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of service points for given employee by executing " +
                    "EmployeeResource.ServicePointResource.countServicePointsByEmployee(employeeId) method of REST API");

            // find employee entity for which to count service points
            Employee employee = employeeFacade.find(userId);
            if (employee == null)
                throw new NotFoundException("Could not find employee for id " + userId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(servicePointFacade.countByEmployee(employee)), 200, "number of service points for employee with id " + employee.getUserId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method returns subset of Service Point entities for given Employee entity and
         * Address related query params. The employee id is passed through path param.
         * Address params are passed through query params.
         */
        @GET
        @Path("/address")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeServicePointsByAddress( @PathParam("userId") Long userId,
                                                           @BeanParam AddressBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning service points for given employee and address related params using " +
                    "EmployeeResource.ServicePointResource.getEmployeeServicePointsByAddress(employeeId, address) method of REST API");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);
            if(noOfParams < 1)
                throw new BadRequestException("There is no address related query param in request.");

            // find employee entity for which to get associated service points
            Employee employee = employeeFacade.find(userId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + userId + ".");

            // find service points by given criteria
            ResourceList<ServicePoint> servicePoints = new ResourceList<>(
                    servicePointFacade.findByEmployeeAndAddress(employee, params.getCity(), params.getState(), params.getCountry(),
                            params.getStreet(), params.getZipCode(), params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(servicePoints).build();
        }

        /**
         * Method returns subset of Service Point entities for given Employee entity and
         * Coordinates Square related params. The employee id is passed through path param.
         * Coordinates Square params are passed through query params.
         */
        @GET
        @Path("/coordinates-square")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeServicePointsByCoordinatesSquare( @PathParam("userId") Long userId,
                                                                     @BeanParam CoordinatesSquareBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning service points for given employee and coordinates square params using " +
                    "EmployeeResource.ServicePointResource.getEmployeeServicePointsByCoordinatesSquare(employeeId, coordinatesSquare) method of REST API");

            if(params.getMinLongitudeWGS84() == null || params.getMinLatitudeWGS84() == null ||
                    params.getMaxLongitudeWGS84() == null || params.getMaxLatitudeWGS84() == null)
                throw new BadRequestException("All coordinates square query params must be specified.");

            // find employee entity for which to get associated service points
            Employee employee = employeeFacade.find(userId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + userId + ".");

            // find service points by given criteria
            ResourceList<ServicePoint> servicePoints = new ResourceList<>(
                    servicePointFacade.findByEmployeeAndCoordinatesSquare(employee, params.getMinLongitudeWGS84(), params.getMinLatitudeWGS84(),
                            params.getMaxLongitudeWGS84(), params.getMaxLatitudeWGS84(), params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(servicePoints).build();
        }

        /**
         * Method returns subset of Service Point entities for given Employee and
         * Coordinates Circle related params. The employee id is passed through path param.
         * Coordinates Circle params are passed through query params.
         */
        @GET
        @Path("/coordinates-circle")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeServicePointsByCoordinatesCircle( @PathParam("userId") Long userId,
                                                                     @BeanParam CoordinatesCircleBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning service points for given employee and coordinates circle params using " +
                    "EmployeeResource.ServicePointResource.getEmployeeServicePointsByCoordinatesCircle(employeeId, coordinatesCircle) method of REST API");

            if (params.getLongitudeWGS84() == null || params.getLatitudeWGS84() == null || params.getRadius() == null)
                throw new BadRequestException("All coordinates circle query params must be specified.");

            // find employee entity for which to get associated service points
            Employee employee = employeeFacade.find(userId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + userId + ".");

            // find service points by given criteria
            ResourceList<ServicePoint> servicePoints = new ResourceList<>(
                    servicePointFacade.findByEmployeeAndCoordinatesCircle(employee, params.getLongitudeWGS84(),
                            params.getLatitudeWGS84(), params.getRadius(), params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(servicePoints).build();
        }
    }

    public class WorkStationResource {

        public WorkStationResource() { }

        /**
         * Method returns subset of Work Station entities for given Employee.
         * The employee id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeWorkStations( @PathParam("userId") Long userId,
                                                 @BeanParam WorkStationBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Work Station entities for given Employee using " +
                    "EmployeeResource.WorkStationResource.getEmployeeWorkStations(employeeId) method of REST API");

            // find employee entity for which to get associated work stations
            Employee employee = employeeFacade.find(userId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + userId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<WorkStation> workStations = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Employee> employees = new ArrayList<>();
                employees.add(employee);

                utx.begin();

                workStations = new ResourceList<>(
                        workStationFacade.findByMultipleCriteria(params.getServicePoints(), params.getServices(), params.getProviderServices(),
                                employees, params.getWorkStationTypes(), params.getPeriod(), params.getStrictTerm(), params.getTerms(), params.getOffset(), params.getLimit() )
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                workStations = new ResourceList<>( workStationFacade.findByEmployee(employee, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.WorkStationResource.populateWithHATEOASLinks(workStations, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(workStations).build();
        }

        /**
         * Method returns subset of Work Station entities for given Employee
         * fetching them eagerly. The employee id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeWorkStationsEagerly( @PathParam("userId") Long userId,
                                                        @BeanParam WorkStationBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Work Station entities eagerly for given Employee using " +
                    "EmployeeResource.WorkStationResource.getEmployeeWorkStationsEagerly(employeeId) method of REST API");

            // find employee entity for which to get associated work stations
            Employee employee = employeeFacade.find(userId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + userId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<WorkStationWrapper> workStations = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Employee> employees = new ArrayList<>();
                employees.add(employee);

                utx.begin();

                workStations = new ResourceList<>(
                        WorkStationWrapper.wrap(
                                workStationFacade.findByMultipleCriteriaEagerly(params.getServicePoints(), params.getServices(),
                                        params.getProviderServices(), employees, params.getWorkStationTypes(), params.getPeriod(),
                                        params.getStrictTerm(), params.getTerms(), params.getOffset(), params.getLimit())
                        )
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                workStations = new ResourceList<>( WorkStationWrapper.wrap(workStationFacade.findByEmployeeEagerly(employee, params.getOffset(), params.getLimit())) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.WorkStationResource.populateWithHATEOASLinks(workStations, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(workStations).build();
        }

        /**
         * Method that counts Work Station entities for given Employee resource.
         * The employee id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countWorkStationsByEmployee( @PathParam("userId") Long userId,
                                                     @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of work stations for given employee by executing " +
                    "EmployeeResource.WorkStationResource.countWorkStationsByEmployee(employeeId) method of REST API");

            // find employee entity for which to count work stations
            Employee employee = employeeFacade.find(userId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + userId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(workStationFacade.countByEmployee(employee)), 200,
                    "number of work stations for employee with id " + employee.getUserId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method returns subset of Work Station entities for given Employee entity and
         * Term he works on them. The employee id is passed through path param.
         * Term start and end dates are passed through query params.
         */
        @GET
        @Path("/by-term")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeWorkStationsByTerm( @PathParam("userId") Long userId,
                                                       @BeanParam DateRangeBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning work stations for given employee and term (startDate, endDate) using " +
                    "EmployeeResource.WorkStationResource.getEmployeeWorkStationsByTerm(employeeId, term) method of REST API");

            RESTToolkit.validateDateRange(params); // i.e. startDate and endDate

            // find employee entity for which to get associated work stations
            Employee employee = employeeFacade.find(userId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + userId + ".");

            // find work stations by given criteria (employee, term)
            ResourceList<WorkStation> workStations = new ResourceList<>(
                    workStationFacade.findByEmployeeAndTerm(employee, params.getStartDate(), params.getEndDate(),
                            params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.WorkStationResource.populateWithHATEOASLinks(workStations, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(workStations).build();
        }

        /**
         * Method returns subset of Work Station entities for given Employee entity and
         * Term (strict) he works on them. The employee id is passed through path param.
         * Term (strict) start and end dates are passed through query params.
         */
        @GET
        @Path("/by-term-strict")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeWorkStationsByTermStrict( @PathParam("userId") Long userId,
                                                             @BeanParam DateRangeBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning work stations for given employee and term strict (startDate, endDate) using " +
                    "EmployeeResource.WorkStationResource.getEmployeeWorkStationsByTermStrict(employeeId, termStrict) method of REST API");

            RESTToolkit.validateDateRange(params); // i.e. startDate and endDate

            // find employee entity for which to get associated work stations
            Employee employee = employeeFacade.find(userId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + userId + ".");

            // find work stations by given criteria (employee, term strict)
            ResourceList<WorkStation> workStations = new ResourceList<>(
                    workStationFacade.findByEmployeeAndTermStrict(employee, params.getStartDate(), params.getEndDate(),
                            params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.WorkStationResource.populateWithHATEOASLinks(workStations, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(workStations).build();
        }
    }

    public class ServiceResource {

        public ServiceResource() { }

        /**
         * Method returns subset of Service entities for given Employee entity.
         * The employee id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeServices( @PathParam("userId") Long employeeId,
                                             @BeanParam ServiceBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
         /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning services for given employee using " +
                    "EmployeeResource.ServiceResource.getEmployeeServices(employeeId) method of REST API");

            // find employee entity for which to get associated services
            Employee employee = employeeFacade.find(employeeId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<Service> services = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Employee> employees = new ArrayList<>();
                employees.add(employee);

                // get services for given employee filtered by given query params

                utx.begin();

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                        throw new BadRequestException("Query params cannot include keywords and names or descriptions at the same time.");

                    // find only by keywords
                    services = new ResourceList<>(
                            serviceFacade.findByMultipleCriteria(params.getKeywords(), params.getServiceCategories(), params.getProviders(),
                                    employees, params.getWorkStations(), params.getServicePoints(), params.getEmployeeTerms(),
                                    params.getTerms(), params.getOffset(), params.getLimit())
                    );
                } else {
                    // find by names, descriptions
                    services = new ResourceList<>(
                            serviceFacade.findByMultipleCriteria(params.getNames(), params.getDescriptions(), params.getServiceCategories(),
                                    params.getProviders(), employees, params.getWorkStations(), params.getServicePoints(), params.getEmployeeTerms(),
                                    params.getTerms(), params.getOffset(), params.getLimit())
                    );
                }

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get services for given employee without filtering (eventually paginated)
                services = new ResourceList<>( serviceFacade.findByEmployee(employee, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServiceResource.populateWithHATEOASLinks(services, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(services).build();
        }

        /**
         * Method returns subset of Service entities for given Employee fetching them eagerly.
         * The employee id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response  getEmployeeServicesEagerly( @PathParam("userId") Long employeeId,
                                                     @BeanParam ServiceBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning services eagerly for given employee using " +
                    "EmployeeResource.ServiceResource.getEmployeeServicesEagerly(employeeId) method of REST API");

            // find employee entity for which to get associated services
            Employee employee = employeeFacade.find(employeeId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ServiceWrapper> services = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Employee> employees = new ArrayList<>();
                employees.add(employee);

                // get services eagerly for given employee filtered by given params

                utx.begin();

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                        throw new BadRequestException("Query params cannot include keywords and names or descriptions at the same time.");

                    // find only by keywords
                    services = new ResourceList<>(
                            ServiceWrapper.wrap(
                                    serviceFacade.findByMultipleCriteriaEagerly(params.getKeywords(), params.getServiceCategories(), params.getProviders(),
                                            employees, params.getWorkStations(), params.getServicePoints(), params.getEmployeeTerms(), params.getTerms(),
                                            params.getOffset(), params.getLimit())
                            )
                    );
                } else {
                    // find by names, descriptions
                    services = new ResourceList<>(
                            ServiceWrapper.wrap(
                                    serviceFacade.findByMultipleCriteriaEagerly(params.getNames(), params.getDescriptions(), params.getServiceCategories(),
                                            params.getProviders(), employees, params.getWorkStations(), params.getServicePoints(), params.getEmployeeTerms(),
                                            params.getTerms(), params.getOffset(), params.getLimit())
                            )
                    );
                }

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get services eagerly for given employee without filtering (eventually paginated)
                services = new ResourceList<>( ServiceWrapper.wrap(serviceFacade.findByEmployeeEagerly(employee, params.getOffset(), params.getLimit())) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServiceResource.populateWithHATEOASLinks(services, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(services).build();
        }

        /**
         * Method that counts Service entities for given Employee resource.
         * The employee id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countServicesByEmployee( @PathParam("userId") Long employeeId,
                                                 @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of services for given employee by executing " +
                    "EmployeeResource.ServiceResource.countServicesByEmployee(employeeId) method of REST API");

            // find employee entity for which to count services
            Employee employee = employeeFacade.find(employeeId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(serviceFacade.countByEmployee(employee)), 200,
                    "number of services for employee with id " + employee.getUserId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    public class ProviderServiceResource {

        public ProviderServiceResource() { }

        /**
         * Method returns subset of Provider Service entities for given Employee entity.
         * The employee id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeProviderServices( @PathParam("userId") Long employeeId,
                                                     @BeanParam ProviderServiceBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning provider services for given employee using " +
                    "EmployeeResource.ProviderServiceResource.getEmployeeProviderServices(employeeId) method of REST API");

            // find employee entity for which to get associated provider services
            Employee employee = employeeFacade.find(employeeId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ProviderService> providerServices = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Employee> employees = new ArrayList<>();
                employees.add(employee);

                // get provider services for given employee filtered by given query params

                utx.begin();

                providerServices = new ResourceList<>(
                        providerServiceFacade.findByMultipleCriteria(params.getProviders(), params.getServices(), params.getServiceCategories(),
                                params.getDescriptions(), params.getMinPrice(), params.getMaxPrice(), params.getIncludeDiscounts(),
                                params.getMinDiscount(), params.getMaxDiscount(), params.getMinDuration(), params.getMaxDuration(),
                                params.getServicePoints(), params.getWorkStations(), employees, params.getEmployeeTerms(), params.getTerms(),
                                params.getOffset(), params.getLimit())
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get provider services for given employee without filtering (eventually paginated)
                providerServices = new ResourceList<>( providerServiceFacade.findByEmployee(employee, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(providerServices, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providerServices).build();
        }

        /**
         * Method returns subset of Provider Service entities for given Employee fetching them eagerly
         * The employee id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeProviderServicesEagerly( @PathParam("userId") Long employeeId,
                                                            @BeanParam ProviderServiceBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning provider services eagerly for given employee using " +
                    "EmployeeResource.ProviderServiceResource.getEmployeeProviderServicesEagerly(employeeId) method of REST API");

            // find employee entity for which to get associated provider services
            Employee employee = employeeFacade.find(employeeId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ProviderServiceWrapper> providerServices = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Employee> employees = new ArrayList<>();
                employees.add(employee);

                // get provider services eagerly for given employee filtered by given params

                utx.begin();

                providerServices = new ResourceList<>(
                        ProviderServiceWrapper.wrap(
                                providerServiceFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getServices(), params.getServiceCategories(),
                                        params.getDescriptions(), params.getMinPrice(), params.getMaxPrice(), params.getIncludeDiscounts(),
                                        params.getMinDiscount(), params.getMaxDiscount(), params.getMinDuration(), params.getMaxDuration(),
                                        params.getServicePoints(), params.getWorkStations(), employees, params.getEmployeeTerms(), params.getTerms(),
                                        params.getOffset(), params.getLimit())
                        )
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get provider services eagerly for given employee without filtering (eventually paginated)
                providerServices = new ResourceList<>( ProviderServiceWrapper.wrap(
                        providerServiceFacade.findByEmployeeEagerly(employee, params.getOffset(), params.getLimit())
                ) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(providerServices, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providerServices).build();
        }

        /**
         * Method that counts Provider Service entities for given Employee resource.
         * The employee id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countProviderServicesByEmployee( @PathParam("userId") Long employeeId,
                                                         @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of provider services for given employee by executing " +
                    "EmployeeResource.ProviderServiceResource.countProviderServicesByEmployee(employeeId) method of REST API");

            // find employee entity for which to count provider services
            Employee employee = employeeFacade.find(employeeId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(providerServiceFacade.countByEmployee(employee)), 200,
                    "number of provider services for employee with id " + employee.getUserId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    public class EmployeeTermResource {

        public EmployeeTermResource() { }

        /**
         * Method returns subset of Employee Term entities for given Employee entity.
         * The employee id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeEmployeeTerms( @PathParam("userId") Long employeeId,
                                                  @BeanParam EmployeeTermBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employee terms for given employee using " +
                    "EmployeeResource.EmployeeTermResource.getEmployeeEmployeeTerms(employeeId) method of REST API");

            // find employee entity for which to get associated employee terms
            Employee employee = employeeFacade.find(employeeId);
            if (employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<EmployeeTerm> employeeTerms = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Employee> employees = new ArrayList<>();
                employees.add(employee);

                // get employee terms for given employee filtered by given query params

                utx.begin();

                employeeTerms = new ResourceList<>(
                        employeeTermFacade.findByMultipleCriteria(params.getServicePoints(), params.getWorkStations(), employees,
                                params.getTerms(), params.getServices(), params.getProviderServices(), params.getPeriod(), params.getStrictTerm(),
                                params.getOffset(), params.getLimit())
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get employee terms for given employee without filtering (eventually paginated)
                employeeTerms = new ResourceList<>( employeeTermFacade.findByEmployee(employee, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeTermResource.populateWithHATEOASLinks(employeeTerms, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employeeTerms).build();
        }

        /**
         * Method that counts Employee Term entities for given Employee resource.
         * The employee id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countEmployeeTermsByEmployee( @PathParam("userId") Long employeeId,
                                                      @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of employee terms for given employee by executing " +
                    "EmployeeResource.EmployeeTermResource.countEmployeeTermsByEmployee(employeeId) method of REST API");

            // find employee entity for which to count employee terms
            Employee employee = employeeFacade.find(employeeId);
            if (employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(employeeTermFacade.countByEmployee(employee)), 200,
                    "number of employee terms for employee with id " + employee.getUserId() );
            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    public class TermResource {

        public TermResource() { }

        /**
         * Method returns subset of Term entities for given Employee entity.
         * The employee id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeTerms( @PathParam("userId") Long employeeId,
                                          @BeanParam TermBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning terms for given employee using " +
                    "EmployeeResource.TermResource.getEmployeeTerms(employeeId) method of REST API");

            // find employee entity for which to get associated terms
            Employee employee = employeeFacade.find(employeeId);
            if (employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<Term> terms = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Employee> employees = new ArrayList<>();
                employees.add(employee);

                // get terms for given employee filtered by given query params

                utx.begin();

                terms = new ResourceList<>(
                        termFacade.findByMultipleCriteria(params.getServicePoints(), params.getWorkStations(), employees,
                                params.getServices(), params.getProviderServices(), params.getPeriod(), params.getStrictTerm(),
                                params.getOffset(), params.getLimit())
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get terms for given employee without filtering (eventually paginated)
                terms = new ResourceList<>( termFacade.findByEmployee(employee, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.TermResource.populateWithHATEOASLinks(terms, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(terms).build();
        }

        /**
         * Method returns subset of Term entities for given Employee fetching them eagerly.
         * The employee id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeTermsEagerly( @PathParam("userId") Long employeeId,
                                                 @BeanParam TermBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning terms eagerly for given employee using " +
                    "EmployeeResource.TermResource.getEmployeeTermsEagerly(employeeId) method of REST API");

            // find employee entity for which to get associated terms
            Employee employee = employeeFacade.find(employeeId);
            if (employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<TermWrapper> terms = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Employee> employees = new ArrayList<>();
                employees.add(employee);

                // get terms eagerly for given employee filtered by given query params

                utx.begin();

                terms = new ResourceList<>(
                        TermWrapper.wrap(
                                termFacade.findByMultipleCriteriaEagerly(params.getServicePoints(), params.getWorkStations(), employees,
                                        params.getServices(), params.getProviderServices(), params.getPeriod(), params.getStrictTerm(),
                                        params.getOffset(), params.getLimit())
                        )
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get terms eagerly for given employee without filtering (eventually paginated)
                terms = new ResourceList<>( TermWrapper.wrap(termFacade.findByEmployeeEagerly(employee, params.getOffset(), params.getLimit())) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.TermResource.populateWithHATEOASLinks(terms, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(terms).build();
        }

        /**
         * Method that counts Term entities for given Employee resource.
         * The employee id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countTermsByEmployee( @PathParam("userId") Long employeeId,
                                              @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of terms for given employee by executing " +
                    "EmployeeResource.TermResource.countTermsByEmployee(employeeId) method of REST API");

            // find employee entity for which to count terms
            Employee employee = employeeFacade.find(employeeId);
            if (employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(termFacade.countByEmployee(employee)), 200,
                    "number of terms for employee with id " + employee.getUserId() );
            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    public class TransactionResource {

        public TransactionResource() { }

        /**
         * Method returns subset of Transaction entities for given Employee entity.
         * The employee id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeTransactions( @PathParam("userId") Long employeeId,
                                                 @BeanParam TransactionBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning transactions for given employee using " +
                    "EmployeeResource.TransactionResource.getEmployeeTransactions(employeeId) method of REST API");

            // find employee entity for which to get associated transactions
            Employee employee = employeeFacade.find(employeeId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<Transaction> transactions = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Employee> employees = new ArrayList<>();
                employees.add(employee);

                // get transactions for given employee filtered by given query params

                utx.begin();

                transactions = new ResourceList<>(
                        transactionFacade.findByMultipleCriteria(params.getClients(), params.getProviders(), params.getServices(), params.getServicePoints(),
                                params.getWorkStations(), employees, params.getProviderServices(), params.getTransactionTimePeriod(),
                                params.getBookedTimePeriod(), params.getTerms(), params.getPriceRange(), params.getCurrencyCodes(), params.getPaymentMethods(),
                                params.getPaid(), params.getOffset(), params.getLimit())
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get transactions for given employee without filtering (eventually paginated)
                transactions = new ResourceList<>( transactionFacade.findByEmployee(employee, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.TransactionResource.populateWithHATEOASLinks(transactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(transactions).build();
        }

        /**
         * Method returns subset of Transaction entities for given Employee fetching them eagerly.
         * The employee id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeTransactionsEagerly( @PathParam("userId") Long employeeId,
                                                        @BeanParam TransactionBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning transactions eagerly for given employee using " +
                    "EmployeeResource.TransactionResource.getEmployeeTransactionsEagerly(employeeId) method of REST API");

            // find employee entity for which to get associated transactions
            Employee employee = employeeFacade.find(employeeId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<TransactionWrapper> transactions = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Employee> employees = new ArrayList<>();
                employees.add(employee);

                // get transactions eagerly for given employee filtered by given query params

                utx.begin();

                transactions = new ResourceList<>(
                        TransactionWrapper.wrap(
                                transactionFacade.findByMultipleCriteriaEagerly(params.getClients(), params.getProviders(), params.getServices(),
                                        params.getServicePoints(), params.getWorkStations(), employees, params.getProviderServices(),
                                        params.getTransactionTimePeriod(), params.getBookedTimePeriod(), params.getTerms(), params.getPriceRange(),
                                        params.getCurrencyCodes(), params.getPaymentMethods(), params.getPaid(), params.getOffset(), params.getLimit())
                        )
                );

                utx.commit();

            }  else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get transactions eagerly for given employee without filtering (eventually paginated)
                transactions = new ResourceList<>( TransactionWrapper.wrap(transactionFacade.findByEmployeeEagerly(employee, params.getOffset(), params.getLimit())) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.TransactionResource.populateWithHATEOASLinks(transactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(transactions).build();
        }

        /**
         * Method that counts Transaction entities for given Employee resource.
         * The employee id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countTransactionsByEmployee( @PathParam("userId") Long employeeId,
                                                     @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of transactions for given employee by executing " +
                    "EmployeeResource.TransactionResource.countTransactionsByEmployee(employeeId) method of REST API");

            // find employee entity for which to count transactions
            Employee employee = employeeFacade.find(employeeId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(transactionFacade.countByEmployee(employee)), 200,
                    "number of transactions for employee with id " + employee.getUserId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    public class HistoricalTransactionResource {

        public HistoricalTransactionResource() { }

        /**
         * Method returns subset of Historical Transaction entities for given Employee entity.
         * The employee id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeHistoricalTransactions( @PathParam("userId") Long employeeId,
                                                           @BeanParam HistoricalTransactionBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning historical transactions for given employee using " +
                    "EmployeeResource.HistoricalTransactionResource.getEmployeeHistoricalTransactions(employeeId) method of REST API");

            // find employee entity for which to get associated historical transactions
            Employee employee = employeeFacade.find(employeeId);
            if (employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<HistoricalTransaction> historicalTransactions = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Employee> employees = new ArrayList<>();
                employees.add(employee);

                // get historical transactions for given employee filtered by given query params

                utx.begin();

                historicalTransactions = new ResourceList<>(
                        historicalTransactionFacade.findByMultipleCriteria(params.getClients(), params.getProviders(), params.getServices(), params.getServicePoints(),
                                params.getWorkStations(), employees, params.getProviderServices(), params.getTransactionTimePeriod(),
                                params.getBookedTimePeriod(), params.getTerms(), params.getPriceRange(), params.getCurrencyCodes(), params.getPaymentMethods(),
                                params.getPaid(), params.getCompletionStatuses(), params.getClientRatingRange(), params.getClientComments(),
                                params.getProviderRatingRange(), params.getProviderDementis(), params.getOffset(), params.getLimit())
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get historical transactions for given employee without filtering (eventually paginated)
                historicalTransactions = new ResourceList<>(historicalTransactionFacade.findByEmployee(employee, params.getOffset(), params.getLimit()));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(historicalTransactions).build();
        }

        /**
         * Method returns subset of Historical Transaction entities for given Employee fetching them eagerly.
         * The employee id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeHistoricalTransactionsEagerly( @PathParam("userId") Long employeeId,
                                                                  @BeanParam HistoricalTransactionBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning historical transactions eagerly for given employee using " +
                    "EmployeeResource.HistoricalTransactionResource.getEmployeeHistoricalTransactionsEagerly(employeeId) method of REST API");

            // find employee entity for which to get associated historical transactions
            Employee employee = employeeFacade.find(employeeId);
            if (employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<HistoricalTransactionWrapper> historicalTransactions = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Employee> employees = new ArrayList<>();
                employees.add(employee);

                // get historical transactions eagerly for given employee filtered by given query params

                utx.begin();

                historicalTransactions = new ResourceList<>(
                        HistoricalTransactionWrapper.wrap(
                                historicalTransactionFacade.findByMultipleCriteriaEagerly(params.getClients(), params.getProviders(), params.getServices(), params.getServicePoints(),
                                        params.getWorkStations(), employees, params.getProviderServices(), params.getTransactionTimePeriod(),
                                        params.getBookedTimePeriod(), params.getTerms(), params.getPriceRange(), params.getCurrencyCodes(), params.getPaymentMethods(),
                                        params.getPaid(), params.getCompletionStatuses(), params.getClientRatingRange(), params.getClientComments(),
                                        params.getProviderRatingRange(), params.getProviderDementis(), params.getOffset(), params.getLimit())
                        )
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get historical transactions eagerly for given employee without filtering (eventually paginated)
                historicalTransactions = new ResourceList<>(HistoricalTransactionWrapper.wrap(historicalTransactionFacade.findByEmployeeEagerly(employee, params.getOffset(), params.getLimit())));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(historicalTransactions).build();
        }

        /**
         * Method that counts Historical Transaction entities for given Employee resource.
         * The employee id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countHistoricalTransactionsByEmployee( @PathParam("userId") Long employeeId,
                                                               @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of historical transactions for given employee by executing " +
                    "EmployeeResource.HistoricalTransactionResource.countHistoricalTransactionsByEmployee(employeeId) method of REST API");

            // find employee entity for which to count historical transactions
            Employee employee = employeeFacade.find(employeeId);
            if (employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(historicalTransactionFacade.countByEmployee(employee)), 200,
                    "number of historical transactions for employee with id " + employee.getUserId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }
}
