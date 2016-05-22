package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.EducationFacade;
import pl.salonea.ejb.stateless.EmployeeFacade;
import pl.salonea.entities.Education;
import pl.salonea.entities.Employee;
import pl.salonea.jaxrs.bean_params.EducationBeanParam;
import pl.salonea.jaxrs.bean_params.EmployeeBeanParam;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.bean_params.PaginationBeanParam;
import pl.salonea.jaxrs.exceptions.*;
import pl.salonea.jaxrs.exceptions.BadRequestException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.EducationWrapper;
import pl.salonea.jaxrs.wrappers.EmployeeWrapper;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.transaction.*;
import javax.transaction.NotSupportedException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

/**
 * Created by michzio on 14/01/2016.
 */
@Path("/educations")
public class EducationResource {

    private static final Logger logger = Logger.getLogger(EducationResource.class.getName());

    @Inject
    private UserTransaction utx;

    @Inject
    private EducationFacade educationFacade;
    @Inject
    private EmployeeFacade employeeFacade;

    /**
     * Method returns all Education resources.
     * They can be additionally filtered or paginated by @QueryParams
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getEducations( @BeanParam EducationBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Educations by executing EducationResource.getEducations() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<Education> educations = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            // get all educations filtered by given query params

            if( RESTToolkit.isSet(params.getKeywords()) ) {
                if( RESTToolkit.isSet(params.getDegrees()) || RESTToolkit.isSet(params.getFaculties()) || RESTToolkit.isSet(params.getSchools()) )
                    throw new BadRequestException("Query params cannot include keywords and degrees, faculties or schools at the same time.");

                // find only by keywords
                educations = new ResourceList<>(
                        educationFacade.findByMultipleCriteria(params.getKeywords(), params.getEmployees(), params.getOffset(), params.getLimit())
                );
            } else {
                // find by degrees, faculties, schools
                educations = new ResourceList<>(
                        educationFacade.findByMultipleCriteria(params.getDegrees(), params.getFaculties(), params.getSchools(), params.getEmployees(), params.getOffset(), params.getLimit())
                );
            }
        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all educations without filtering (eventually paginated)
            educations = new ResourceList<>( educationFacade.findAll(params.getOffset(), params.getLimit()) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        EducationResource.populateWithHATEOASLinks(educations, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(educations).build();
    }

    @GET
    @Path("/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getEducationsEagerly( @BeanParam EducationBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Educations eagerly by executing EducationResource.getEducationsEagerly() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<EducationWrapper> educations = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            // get all educations eagerly filtered by query params

            if( RESTToolkit.isSet(params.getKeywords()) ) {
                if( RESTToolkit.isSet(params.getDegrees()) || RESTToolkit.isSet(params.getFaculties()) || RESTToolkit.isSet(params.getSchools()) )
                    throw new BadRequestException("Query params cannot include keywords and degrees, faculties or schools at the same time.");

                // find only by keywords
                educations = new ResourceList<>(
                        EducationWrapper.wrap(
                                educationFacade.findByMultipleCriteriaEagerly(params.getKeywords(), params.getEmployees(), params.getOffset(), params.getLimit())
                        )
                );
            } else {
                // find by degrees, faculties, schools
                educations = new ResourceList<>(
                        EducationWrapper.wrap(
                                educationFacade.findByMultipleCriteriaEagerly(params.getDegrees(), params.getFaculties(), params.getSchools(), params.getEmployees(), params.getOffset(), params.getLimit())
                        )
                );
            }
        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all educations eagerly without filtering (eventually paginated)
            educations = new ResourceList<>( EducationWrapper.wrap(educationFacade.findAllEagerly(params.getOffset(), params.getLimit())) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        EducationResource.populateWithHATEOASLinks(educations, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(educations).build();
    }

    /**
     * Method matches specific Education resource by identifier and returns its instance
     */
    @GET
    @Path("/{educationId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getEducation( @PathParam("educationId") Long educationId,
                                  @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning given Education by executing EducationResource.getEducation(educationId) method of REST API");

        Education foundEducation = educationFacade.find(educationId);
        if(foundEducation == null)
            throw new NotFoundException("Could not find education for id " + educationId + ".");

        // adding hypermedia links to education resource
        EducationResource.populateWithHATEOASLinks(foundEducation, params.getUriInfo());

        return Response.status(Status.OK).entity(foundEducation).build();
    }

    /**
     * Method matches specific Education resource by identifier and returns its instance fetching it eagerly
     */
    @GET
    @Path("/{educationId : \\d+}/eagerly") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getEducationEagerly( @PathParam("educationId") Long educationId,
                                         @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning given Education eagerly by executing EducationResource.getEducationEagerly(educationId) method of REST API");

        Education foundEducation = educationFacade.findByIdEagerly(educationId);
        if(foundEducation == null)
            throw new NotFoundException("Could not find education for id " + educationId + ".");

        // wrapping Education into EducationWrapper in order to marshal eagerly fetched associated collection of entities
        EducationWrapper wrappedEducation = new EducationWrapper(foundEducation);

        // adding hypermedia links to wrapped education resource
        EducationResource.populateWithHATEOASLinks(wrappedEducation, params.getUriInfo());

        return Response.status(Status.OK).entity(wrappedEducation).build();
    }

    /**
     * Method that takes Education as XML or JSON and creates its new instance in database
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createEducation( Education education,
                                     @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "creating new Education by executing EducationResource.createEducation(education) method of REST API");

        Education createdEducation = null;
        URI locationURI = null;

        try {
            // persist new resource in database
            createdEducation = educationFacade.create(education);

            // populate created resource with hypermedia links
            EducationResource.populateWithHATEOASLinks(createdEducation, params.getUriInfo());

            // construct link to newly created resource to return in HTTP Header
            String createdEducationId = String.valueOf(createdEducation.getEducationId());
            locationURI = params.getUriInfo().getBaseUriBuilder().path(EducationResource.class).path(createdEducationId).build();

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_CREATION_ERROR_MESSAGE);
        }

        return Response.created(locationURI).entity(createdEducation).build();
    }

    /**
     * Method that takes updated Education as XML or JSON and its ID as path param.
     * It updates Education in database for provided ID.
     */
    @PUT
    @Path("/{educationId : \\d+}") // catch only numeric identifiers
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateEducation( @PathParam("educationId") Long educationId,
                                     Education education,
                                     @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "updating existing Education by executing EducationResource.updateEducation(educationId, education) method of REST API");

        // set resource ID passed in path param on updated resource object
        education.setEducationId(educationId);

        Education updatedEducation = null;
        try {
            // reflect updated resource object in database
            updatedEducation = educationFacade.update(education, true);
            // populate created resource with hypermedia links
            EducationResource.populateWithHATEOASLinks(updatedEducation, params.getUriInfo());

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_UPDATE_ERROR_MESSAGE);
        }

        return Response.status(Status.OK).entity(updatedEducation).build();
    }

    /**
     * Method that removes Education entity from database for given ID
     * The ID is passed through path param.
     */
    @DELETE
    @Path("/{educationId : \\d+}")  // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeEducation( @PathParam("educationId") Long educationId,
                                     @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing given Education by executing EducationResource.removeEducation(educationId) method of REST API");

        // find Education entity that should be deleted
        Education toDeleteEducation = educationFacade.find(educationId);
        // throw exception if entity hasn't been found
        if(toDeleteEducation == null)
            throw new NotFoundException("Could not find education to delete for given id: " + educationId + ".");

        // remove entity from database
        educationFacade.remove(toDeleteEducation);

        return Response.status(Status.NO_CONTENT).build();
    }

    /**
     * Additional methods returning a subset of resources based on given criteria.
     * You can also achieve similar results by applying @QueryParams to generic method
     * returning all resources in order to filter and limit them.
     */

    /**
     * Method returns number of Education entities in database
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countEducations( @BeanParam GenericBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning number of educations by executing EducationResource.countEducations() method of REST API");

        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(educationFacade.count()), 200, "number of educations");
        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method returns subset of Education entities for given degree.
     * The degree is passed through path param.
     */
    @GET
    @Path("/with-degree/{degree : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getEducationsByDegree( @PathParam("degree") String degree,
                                           @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning educations for given degree using EducationResource.getEducationsByDegree(degree) method of REST API");

        // find educations by given criteria
        ResourceList<Education> educations = new ResourceList<>( educationFacade.findByDegree(degree, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        EducationResource.populateWithHATEOASLinks(educations, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(educations).build();
    }

    /**
     * Method returns subset of Education entities for given faculty.
     * The faculty is passed through path param.
     */
    @GET
    @Path("/at-faculty/{faculty : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getEducationsByFaculty( @PathParam("faculty") String faculty,
                                            @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning educations for given faculty using EducationResource.getEducationsByFaculty(faculty) method of REST API");

        // find educations by given criteria
        ResourceList<Education> educations = new ResourceList<>( educationFacade.findByFaculty(faculty, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        EducationResource.populateWithHATEOASLinks(educations, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(educations).build();
    }

    /**
     * Method returns subset of Education entities for given school.
     * The school is passed through path param.
     */
    @GET
    @Path("/at-school/{school : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getEducationsBySchool( @PathParam("school") String school,
                                           @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning educations for given school using EducationResource.getEducationsBySchool(school) method of REST API");

        // find educations by given criteria
        ResourceList<Education> educations = new ResourceList<>( educationFacade.findBySchool(school, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        EducationResource.populateWithHATEOASLinks(educations, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(educations).build();
    }

    /**
     * Method returns subset of Education entities for given keyword.
     * The keyword is passed through path param.
     */
    @GET
    @Path("/containing-keyword/{keyword : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getEducationsByKeyword( @PathParam("keyword") String keyword,
                                            @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning educations for given keyword using EducationResource.getEducationsByKeyword(keyword) method of REST API");

        // find educations by given criteria
        ResourceList<Education> educations = new ResourceList<>( educationFacade.findByKeyword(keyword, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        EducationResource.populateWithHATEOASLinks(educations, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(educations).build();
    }

    /**
     * related subresources (through relationships)
     */

    @Path("/{educationId : \\d+}/employees")
    public EmployeeResource getEmployeeResource() {
        return new EmployeeResource();
    }

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList educations, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(educations, uriInfo, offset, limit);

        try {
            // count resources hypermedia link
            Method countMethod = EducationResource.class.getMethod("countEducations", GenericBeanParam.class);
            educations.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(EducationResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            educations.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(EducationResource.class).build()).rel("educations").build() );

            // get all resources eagerly hypermedia link
            Method educationsEagerlyMethod = EducationResource.class.getMethod("getEducationsEagerly", EducationBeanParam.class);
            educations.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                        .path(EducationResource.class)
                        .path(educationsEagerlyMethod)
                        .build()).rel("educations-eagerly").build() );

            // get subset of resources hypermedia links

            // with-degree
            educations.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EducationResource.class)
                    .path("with-degree")
                    .build())
                    .rel("with-degree").build() );

            // at-faculty
            educations.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EducationResource.class)
                    .path("at-faculty")
                    .build())
                    .rel("at-faculty").build() );

            // at-school
            educations.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EducationResource.class)
                    .path("at-school")
                    .build())
                    .rel("at-school").build() );

            // containing-keyword
            educations.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EducationResource.class)
                    .path("containing-keyword")
                    .build())
                    .rel("containing-keyword").build() );

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for(Object object : educations.getResources()) {
            if(object instanceof Education) {
                EducationResource.populateWithHATEOASLinks( (Education) object, uriInfo);
            } else if(object instanceof EducationWrapper) {
                EducationResource.populateWithHATEOASLinks( (EducationWrapper) object, uriInfo);
            }
        }
    }

    /**
     * This method enables to populate each individual resource wrapper with hypermedia links
     */
    public static void populateWithHATEOASLinks(EducationWrapper educationWrapper, UriInfo uriInfo) {

        EducationResource.populateWithHATEOASLinks(educationWrapper.getEducation(), uriInfo);

        for(Employee employee : educationWrapper.getEducatedEmployees())
            pl.salonea.jaxrs.EmployeeResource.populateWithHATEOASLinks(employee, uriInfo);
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(Education education, UriInfo uriInfo) {

        // self link with pattern: http://localhost:port/app/rest/{resources}/{id}
        education.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                        .path(EducationResource.class)
                                                        .path(education.getEducationId().toString())
                                                        .build())
                                        .rel("self").build() );

        // collection link with pattern: http://localhost:port/app/rest/{resources}
        education.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                        .path(EducationResource.class)
                                                        .build())
                                        .rel("educations").build());

        try {
            // self eagerly link with pattern: http://localhost:port/app/rest/{resources}/{id}/eagerly
            Method educationEagerlyMethod = EducationResource.class.getMethod("getEducationEagerly", Long.class, GenericBeanParam.class);
            education.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                        .path(EducationResource.class)
                        .path(educationEagerlyMethod)
                        .resolveTemplate("educationId", education.getEducationId().toString())
                        .build())
                        .rel("education-eagerly").build() );

            // associated collections links with pattern: http://localhost:port/app/rest/{resources}/{id}/{relationship}

            /**
             * Employees associated with current Education resource
             */

            // employees link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}
            Method employeesMethod = EducationResource.class.getMethod("getEmployeeResource");
            education.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EducationResource.class)
                    .path(employeesMethod)
                    .resolveTemplate("educationId", education.getEducationId().toString())
                    .build())
                    .rel("employees").build() );

            // employees eagerly link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/eagerly
            Method employeesEagerlyMethod = EducationResource.EmployeeResource.class.getMethod("getEducatedEmployeesEagerly", Long.class, EmployeeBeanParam.class);
            education.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EducationResource.class)
                    .path(employeesMethod)
                    .path(employeesEagerlyMethod)
                    .resolveTemplate("educationId", education.getEducationId().toString())
                    .build())
                    .rel("employees-eagerly").build());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public class EmployeeResource {

        public EmployeeResource() { }

        /**
         * Method returns subset of Employee entities for given Education entity.
         * The education id is passed through path param.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEducatedEmployees( @PathParam("educationId") Long educationId,
                                              @BeanParam EmployeeBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employees for given education using EducationResource.EmployeeResource.getEducatedEmployees(educationId) method of REST API");

            // find education entity for which to get associated employees
            Education education = educationFacade.find(educationId);
            if(education == null)
                throw new NotFoundException("Could not find education for id " + educationId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<Employee> employees = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Education> educations = new ArrayList<>();
                educations.add(education);

                utx.begin();

                // get employees for given education filtered by given params
                employees = new ResourceList<>(
                        employeeFacade.findByMultipleCriteria(params.getDescriptions(), params.getJobPositions(), params.getSkills(),
                                educations, params.getServices(), params.getProviderServices(), params.getServicePoints(),
                                params.getWorkStations(), params.getPeriod(), params.getStrictTerm(),  params.getTerms(), params.getRated(),
                                params.getMinAvgRating(), params.getMaxAvgRating(), params.getRatingClients(), params.getOffset(), params.getLimit())
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get employees for given education without filtering (eventually paginated)
                employees = new ResourceList<>( employeeFacade.findByEducation(education, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeResource.populateWithHATEOASLinks(employees, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employees).build();
        }

        /**
         * Method returns subset of Employee entities for given Education fetching them eagerly.
         * The education id is passed through path param.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEducatedEmployeesEagerly( @PathParam("educationId") Long educationId,
                                                     @BeanParam EmployeeBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employees eagerly for given education using " +
                    "EducationResource.EmployeeResource.getEducatedEmployeesEagerly(educationId) method of REST API" );

            // find education entity for which to get associated employees
            Education education = educationFacade.find(educationId);
            if(education == null)
                throw new NotFoundException("Could not find education for id " + educationId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<EmployeeWrapper> employees = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Education> educations = new ArrayList<>();
                educations.add(education);

                utx.begin();

                // get employees eagerly for given education filtered by given params
                employees = new ResourceList<>(
                        EmployeeWrapper.wrap(
                                employeeFacade.findByMultipleCriteriaEagerly(params.getDescriptions(), params.getJobPositions(), params.getSkills(),
                                        educations, params.getServices(), params.getProviderServices(), params.getServicePoints(),
                                        params.getWorkStations(), params.getPeriod(), params.getStrictTerm(),  params.getTerms(), params.getRated(),
                                        params.getMinAvgRating(), params.getMaxAvgRating(), params.getRatingClients(), params.getOffset(), params.getLimit())
                        )
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get employees eagerly for given education without filtering (eventually paginated)
                employees = new ResourceList<>( EmployeeWrapper.wrap(employeeFacade.findByEducationEagerly(education, params.getOffset(), params.getLimit())) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeResource.populateWithHATEOASLinks(employees, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employees).build();
        }

    }

}