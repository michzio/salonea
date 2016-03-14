package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.EmployeeTermFacade;
import pl.salonea.entities.EmployeeTerm;
import pl.salonea.entities.idclass.EmployeeTermId;
import pl.salonea.jaxrs.bean_params.EmployeeTermBeanParam;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.exceptions.ExceptionHandler;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.exceptions.UnprocessableEntityException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;

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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by michzio on 26/01/2016.
 */
@Path("/employee-terms")
public class EmployeeTermResource {

    private static final Logger logger = Logger.getLogger(EmployeeTermResource.class.getName());

    @Inject
    private UserTransaction utx;

    @Inject
    private EmployeeTermFacade employeeTermFacade;

    /**
     * Method returns all Employee Term entities.
     * They can be additionally filtered and paginated by @QueryParams.
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getEmployeeTerms( @BeanParam EmployeeTermBeanParam params ) throws ForbiddenException,
    /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Employee Terms by executing EmployeeTermResource.getEmployeeTerms() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<EmployeeTerm> employeeTerms = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            utx.begin();

            // get employee terms filtered by criteria provided in query params
            employeeTerms = new ResourceList<>(
                    employeeTermFacade.findByMultipleCriteria(params.getServicePoints(), params.getWorkStations(), params.getEmployees(),
                            params.getTerms(), params.getServices(), params.getProviderServices(), params.getPeriod(), params.getStrictTerm(),
                            params.getOffset(), params.getLimit())
            );

            utx.commit();

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all employee terms without filtering (eventually paginated)
            employeeTerms = new ResourceList<>( employeeTermFacade.findAll(params.getOffset(), params.getLimit())  );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        EmployeeTermResource.populateWithHATEOASLinks(employeeTerms, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(employeeTerms).build();
    }


    /**
     * Method matches specific Employee Term resource by composite identifier and returns its instance.
     */
    @GET
    @Path("/{employeeId: \\d+}+{termId: \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getEmployeeTerm(  @PathParam("employeeId") Long employeeId,
                                      @PathParam("termId") Long termId,
                                      @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning given Employee Term by executing EmployeeTermResource.getEmployeeTerm(employeeId, termId) method of REST API");

        EmployeeTerm foundEmployeeTerm = employeeTermFacade.find(new EmployeeTermId(termId, employeeId));
        if(foundEmployeeTerm == null)
            throw new NotFoundException("Could not find employee term for id (" + employeeId + "," + termId + ").");

        // adding hypermedia links to employee term resource
        EmployeeTermResource.populateWithHATEOASLinks(foundEmployeeTerm, params.getUriInfo());

        return Response.status(Status.OK).entity(foundEmployeeTerm).build();
    }

    /**
     * Method that takes EmployeeTerm as XML or JSON and creates its new instance in database
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createEmployeeTerm( EmployeeTerm employeeTerm,
                                        @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        final Long employeeId = employeeTerm.getEmployee().getUserId();
        final Long termId = employeeTerm.getTerm().getTermId();

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "creating new EmployeeTerm by executing EmployeeTermResource.createEmployeeTerm(employeeTerm) method of REST API");

        EmployeeTerm createdEmployeeTerm = null;
        URI locationURI = null;

        try {
            // persist new resource in database
            createdEmployeeTerm = employeeTermFacade.createForEmployeeAndTerm(employeeId, termId, employeeTerm);

            // populate created resource with hypermedia links
            EmployeeTermResource.populateWithHATEOASLinks(createdEmployeeTerm, params.getUriInfo());

            // construct link to newly created resource to return in HTTP Header
            String uid = String.valueOf(createdEmployeeTerm.getEmployee().getUserId());
            String tid = String.valueOf(createdEmployeeTerm.getTerm().getTermId());

            Method employeeTermMethod = EmployeeTermResource.class.getMethod("getEmployeeTerm", Long.class, Long.class, GenericBeanParam.class);
            locationURI = params.getUriInfo().getBaseUriBuilder()
                    .path(EmployeeTermResource.class)
                    .path(employeeTermMethod)
                    .resolveTemplate("employeeId", uid)
                    .resolveTemplate("termId", tid)
                    .build();

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }  catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_CREATION_ERROR_MESSAGE);
        }

        return Response.created(locationURI).entity(createdEmployeeTerm).build();
    }

    /**
     * Method that takes updated Employee Term as XML or JSON and its composite ID as path param.
     * It updates Employee Term in database for provided composite ID.
     */
    @PUT
    @Path("/{employeeId: \\d+}+{termId: \\d+}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateEmployeeTerm( @PathParam("employeeId") Long employeeId,
                                        @PathParam("termId") Long termId,
                                        EmployeeTerm employeeTerm,
                                        @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "updating existing Employee Term by executing EmployeeTermResource.updateEmployeeTerm(employeeTerm) method of REST API");

        // create composite ID based on path params
        EmployeeTermId employeeTermId = new EmployeeTermId(termId, employeeId);

        EmployeeTerm updatedEmployeeTerm = null;
        try {
            // reflect updated resource object in database
            updatedEmployeeTerm = employeeTermFacade.update(employeeTermId, employeeTerm);
            // populate created resource with hypermedia links
            EmployeeTermResource.populateWithHATEOASLinks(updatedEmployeeTerm, params.getUriInfo());

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_UPDATE_ERROR_MESSAGE);
        }

        return Response.status(Status.OK).entity(updatedEmployeeTerm).build();
    }

    /**
     * Method that removes Employee Term entity from database for given ID.
     * The employee term composite id is passed through path params.
     */
    @DELETE
    @Path("/{employeeId: \\d+}+{termId: \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeEmployeeTerm( @PathParam("employeeId") Long employeeId,
                                        @PathParam("termId") Long termId,
                                        @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing given Employee Term by executing EmployeeTermResource.removeEmployeeTerm(employeeId, termId) method of REST API");

        // remove entity from database
        Integer noOfDeleted = employeeTermFacade.deleteById(new EmployeeTermId(termId, employeeId));

        if (noOfDeleted == 0)
            throw new NotFoundException("Could not find employee term to delete for id (" + employeeId + "," + termId + ").");
        else if (noOfDeleted != 1)
            throw new InternalServerErrorException("Some error occurred while trying to delete employee term with id (" + employeeId + "," + termId + ").");

        return Response.status(Status.NO_CONTENT).build();
    }

    /**
     * Additional methods returning subset of resources based on given criteria.
     * you can also achieve similar results by applying @QueryParams to generic method
     * returning all resources in order to filter and limit them.
     */

    /**
     * Method returns number of Employee Term entities in database.
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countEmployeeTerms( @BeanParam GenericBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning number of employee terms by executing EmployeeTermResource.countEmployeeTerms() method of REST API");

        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(employeeTermFacade.count()), 200, "number of employee terms");
        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList<EmployeeTerm> employeeTerms, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(employeeTerms, uriInfo, offset, limit);

        try {
            // count resources hypermedia link
            Method countMethod = EmployeeTermResource.class.getMethod("countEmployeeTerms", GenericBeanParam.class);
            employeeTerms.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(EmployeeTermResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            employeeTerms.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(EmployeeTermResource.class).build()).rel("employee-terms").build() );

            // get subset of resources hypermedia links

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for(EmployeeTerm employeeTerm : employeeTerms.getResources())
            EmployeeTermResource.populateWithHATEOASLinks(employeeTerm, uriInfo);
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(EmployeeTerm employeeTerm, UriInfo uriInfo) {

        try {
            // self link with pattern: http://localhost:port/app/rest/{resources}/{id1}+{id2}
            Method employeeTermMethod = EmployeeTermResource.class.getMethod("getEmployeeTerm", Long.class, Long.class, GenericBeanParam.class);
            employeeTerm.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeTermResource.class)
                    .path(employeeTermMethod)
                    .resolveTemplate("employeeId", employeeTerm.getEmployee().getUserId().toString())
                    .resolveTemplate("termId", employeeTerm.getTerm().getTermId().toString())
                    .build())
                    .rel("self").build());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
