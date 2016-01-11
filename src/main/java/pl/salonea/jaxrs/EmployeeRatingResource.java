package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.EmployeeRatingFacade;
import pl.salonea.entities.EmployeeRating;
import pl.salonea.entities.idclass.EmployeeRatingId;
import pl.salonea.jaxrs.bean_params.EmployeeRatingBeanParam;
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
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
/**
 * Created by michzio on 07/11/2015.
 */
@Path("/employee-ratings")
public class EmployeeRatingResource {

    private static final Logger logger = Logger.getLogger(EmployeeRatingResource.class.getName());

    @Inject
    private EmployeeRatingFacade employeeRatingFacade;

    /**
     * Method returns all Employee Rating resources.
     * They can be additionally filtered or paginated by @QueryParams
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getEmployeeRatings( @BeanParam EmployeeRatingBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Employee Ratings by executing EmployeeRatingResource.getEmployeeRatings() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<EmployeeRating> employeeRatings = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            // get employee ratings filtered by criteria provided in query params
            employeeRatings = new ResourceList<>(
                    employeeRatingFacade.findByMultipleCriteria(params.getClients(), params.getEmployees(), params.getMinRating(), params.getMaxRating(),
                            params.getExactRating(), params.getClientComment(), params.getEmployeeDementi(), params.getOffset(), params.getLimit())
            );

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all employee ratings without filtering (eventually paginated)
            employeeRatings = new ResourceList<>(employeeRatingFacade.findAll(params.getOffset(), params.getLimit()));
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        EmployeeRatingResource.populateWithHATEOASLinks(employeeRatings, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(employeeRatings).build();
    }

    /**
     * Method matches specific Employee Rating resource by identifier and returns its instance.
     * Employee Rating composite identifier has pattern: employeeId+clientId.
     */
    @GET
    @Path("/{employeeId : \\d+}+{clientId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getEmployeeRating( @PathParam("employeeId") Long employeeId,
                                       @PathParam("clientId") Long clientId,
                                       @BeanParam GenericBeanParam params ) throws NotFoundException, ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning given Employee Rating for employeeId: " + employeeId + " and clientId: " + clientId +
                " by executing EmployeeRatingResource.getEmployeeRating(employeeId, clientId) method of REST API");

        EmployeeRating foundEmployeeRating = employeeRatingFacade.find(new EmployeeRatingId(employeeId, clientId));
        if(foundEmployeeRating == null)
            throw new NotFoundException("Could not find employee rating for id (" + employeeId + "," + clientId + ").");

        // adding hypermedia links to employee rating resource
        EmployeeRatingResource.populateWithHATEOASLinks(foundEmployeeRating, params.getUriInfo());

        return Response.status(Status.OK).entity(foundEmployeeRating).build();
    }

    /**
     * Method that takes Employee Rating as XML or JSON and creates its new instance in database
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createEmployeeRating( EmployeeRating employeeRating,
                                          @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "creating new Employee Rating by executing EmployeeRatingResource.createEmployeeRating(employeeRating) method of REST API");

        EmployeeRating createdEmployeeRating = null;
        URI locationURI = null;

        try {
            // persist new resource in database
            Long employeeId = employeeRating.getEmployee().getUserId();
            Long clientId = employeeRating.getClient().getClientId();
            createdEmployeeRating = employeeRatingFacade.createForEmployeeAndClient(employeeId, clientId, employeeRating);

            // populate created resource with hypermedia links
            EmployeeRatingResource.populateWithHATEOASLinks(createdEmployeeRating, params.getUriInfo());

            // construct link to newly created resource to return in HTTP Header
            Method employeeRatingMethod = EmployeeRatingResource.class.getMethod("getEmployeeRating", Long.class, Long.class, GenericBeanParam.class);
            locationURI = params.getUriInfo().getBaseUriBuilder()
                    .path(EmployeeRatingResource.class)
                    .path(employeeRatingMethod)
                    .resolveTemplate("employeeId", String.valueOf(createdEmployeeRating.getEmployee().getUserId()) )
                    .resolveTemplate("clientId", String.valueOf(createdEmployeeRating.getClient().getClientId()) )
                    .build();

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_CREATION_ERROR_MESSAGE);
        }

        return Response.created(locationURI).entity(createdEmployeeRating).build();
    }

    /**
     * Method that takes updated Employee Rating as XML or JSON and its composite ID as path param.
     * It updates Employee Rating in database for provided composite ID.
     */
    @PUT
    @Path("/{employeeId : \\d+}+{clientId : \\d+}") // catch only numeric identifiers
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateEmployeeRating( @PathParam("employeeId") Long employeeId,
                                          @PathParam("clientId") Long clientId,
                                          EmployeeRating employeeRating,
                                          @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "updating existing Employee Rating by executing EmployeeRatingResource.updateEmployeeRating(employeeId, clientId, employeeRating) method of REST API");

        // create composite ID based on path params
        EmployeeRatingId employeeRatingId = new EmployeeRatingId(employeeId, clientId);

        EmployeeRating updatedEmployeeRating = null;
        try {
            // reflect updated resource object in database
            updatedEmployeeRating = employeeRatingFacade.update(employeeRatingId, employeeRating);
            // populate created resource with hypermedia links
            EmployeeRatingResource.populateWithHATEOASLinks(updatedEmployeeRating, params.getUriInfo());

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_UPDATE_ERROR_MESSAGE);
        }

        return Response.status(Status.OK).entity(updatedEmployeeRating).build();
    }

    /**
     * Method that removes Employee Rating entity from database for given ID.
     * The employee rating composite id is passed through path params.
     */
    @DELETE
    @Path("/{employeeId : \\d+}+{clientId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeEmployeeRating( @PathParam("employeeId") Long employeeId,
                                          @PathParam("clientId") Long clientId,
                                          @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing given Employee Rating by executing EmployeeRatingResource.removeEmployeeRating(employeeId, clientId) method of REST API");

        // remove entity from database
        Integer noOfDeleted = employeeRatingFacade.deleteById(new EmployeeRatingId(employeeId, clientId));

        if(noOfDeleted == 0)
            throw new NotFoundException("Could not find employee rating to delete for id (" + employeeId + "," + clientId + ").");
        else if(noOfDeleted != 1)
            throw new InternalServerErrorException("Some error occurred while trying to delete employee rating with id (" + employeeId + "," + clientId + ").");

        return Response.status(Status.NO_CONTENT).build();
    }

    /**
     * Additional methods returning subset of resources based on given criteria
     * you can also achieve similar results by applying @QueryParams to generic method
     * returning all resources in order to filter and limit them
     */

    /**
     * Method returns number of Employee Rating entities in database
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countEmployeeRatings( @BeanParam GenericBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning number of employee ratings by executing EmployeeRatingResource.countEmployeeRatings() method of REST API");

        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(employeeRatingFacade.count()), 200, "number of employee ratings");
        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * This method enable to populate list of resources and each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList<EmployeeRating> employeeRatings, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(employeeRatings, uriInfo, offset, limit);

        try {
            // count resources hypermedia link
            Method countMethod = EmployeeRatingResource.class.getMethod("countEmployeeRatings", GenericBeanParam.class);
            employeeRatings.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(EmployeeRatingResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            employeeRatings.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(EmployeeRatingResource.class).build()).rel("employee-ratings").build() );

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for(EmployeeRating employeeRating : employeeRatings.getResources())
            EmployeeRatingResource.populateWithHATEOASLinks(employeeRating, uriInfo);
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(EmployeeRating employeeRating, UriInfo uriInfo) {

        try {
            // self link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/{sub-id}
            Method employeeRatingMethod = EmployeeRatingResource.class.getMethod("getEmployeeRating", Long.class, Long.class, GenericBeanParam.class);
            employeeRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeRatingResource.class)
                    .path(employeeRatingMethod)
                    .resolveTemplate("employeeId", employeeRating.getEmployee().getUserId().toString())
                    .resolveTemplate("clientId", employeeRating.getClient().getClientId().toString())
                    .build())
                    .rel("self").build());

            // collection link with pattern: http://localhost:port/app/rest/{resources}
            employeeRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeRatingResource.class)
                    .build())
                    .rel("employee-ratings").build());

            // sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}
            Method clientEmployeeRatingsMethod = ClientResource.class.getMethod("getEmployeeRatingResource");
            employeeRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(clientEmployeeRatingsMethod)
                    .resolveTemplate("clientId", employeeRating.getClient().getClientId().toString())
                    .build())
                    .rel("client-employee-ratings").build());

            Method employeeEmployeeRatingsMethod = EmployeeResource.class.getMethod("getEmployeeRatingResource");
            employeeRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(employeeEmployeeRatingsMethod)
                    .resolveTemplate("userId", employeeRating.getEmployee().getUserId().toString())
                    .build())
                    .rel("employee-employee-ratings").build());

            // sub-collection count link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/count
            Method countByClientMethod = ClientResource.EmployeeRatingResource.class.getMethod("countClientEmployeeRatings", Long.class, GenericBeanParam.class);
            employeeRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(clientEmployeeRatingsMethod)
                    .path(countByClientMethod)
                    .resolveTemplate("clientId", employeeRating.getClient().getClientId().toString())
                    .build())
                    .rel("client-employee-ratings-count").build());

            Method countByEmployeeMethod = EmployeeResource.EmployeeRatingResource.class.getMethod("countEmployeeRatings", Long.class, GenericBeanParam.class);
            employeeRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(employeeEmployeeRatingsMethod)
                    .path(countByEmployeeMethod)
                    .resolveTemplate("userId", employeeRating.getEmployee().getUserId().toString())
                    .build())
                    .rel("employee-employee-ratings-count").build());

            // employee average rating link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/average-rating
            Method employeeAverageRatingMethod = EmployeeResource.EmployeeRatingResource.class.getMethod("getAverageEmployeeRating", Long.class, GenericBeanParam.class);
            employeeRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(employeeEmployeeRatingsMethod)
                    .path(employeeAverageRatingMethod)
                    .resolveTemplate("userId", employeeRating.getEmployee().getUserId().toString())
                    .build())
                    .rel("employee-average-rating").build());

            // rated sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/rated/{rating}
            employeeRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(clientEmployeeRatingsMethod)
                    .path("rated")
                    .resolveTemplate("clientId", employeeRating.getClient().getClientId().toString())
                    .build())
                    .rel("client-employee-ratings-rated").build());

            employeeRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(employeeEmployeeRatingsMethod)
                    .path("rated")
                    .resolveTemplate("userId", employeeRating.getEmployee().getUserId().toString())
                    .build())
                    .rel("employee-employee-ratings-rated").build());

            // rated-above sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/rated-above/{minRating}
            employeeRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(clientEmployeeRatingsMethod)
                    .path("rated-above")
                    .resolveTemplate("clientId", employeeRating.getClient().getClientId().toString())
                    .build())
                    .rel("client-employee-ratings-rated-above").build());

            employeeRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(employeeEmployeeRatingsMethod)
                    .path("rated-above")
                    .resolveTemplate("userId", employeeRating.getEmployee().getUserId().toString())
                    .build())
                    .rel("employee-employee-ratings-rated-above").build());

            // rated-below sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/rated-below/{maxRating}
            employeeRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(clientEmployeeRatingsMethod)
                    .path("rated-below")
                    .resolveTemplate("clientId", employeeRating.getClient().getClientId().toString())
                    .build())
                    .rel("client-employee-ratings-rated-below").build());

            employeeRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(employeeEmployeeRatingsMethod)
                    .path("rated-below")
                    .resolveTemplate("userId", employeeRating.getEmployee().getUserId().toString())
                    .build())
                    .rel("employee-employee-ratings-rated-below").build());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
