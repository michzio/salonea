package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.ProviderRatingFacade;
import pl.salonea.entities.ProviderRating;
import pl.salonea.entities.idclass.ProviderRatingId;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.bean_params.ProviderRatingBeanParam;
import pl.salonea.jaxrs.exceptions.ExceptionHandler;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.UnprocessableEntityException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.exceptions.NotFoundException;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.ws.rs.core.Response.Status;

/**
 * Created by michzio on 12/09/2015.
 */
@Path("/provider-ratings")
public class ProviderRatingResource {

    private static final Logger logger = Logger.getLogger(ProviderRatingResource.class.getName());

    @Inject
    private ProviderRatingFacade providerRatingFacade;

    /**
     * Method returns all Provider Rating resources.
     * They can be additionally filtered or paginated by @QueryParams
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProviderRatings( @BeanParam ProviderRatingBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Provider Ratings by executing ProviderRatingResource.getProviderRatings() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<ProviderRating> providerRatings = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            // get provider ratings filtered by criteria provided in query params
            providerRatings = new ResourceList<>(
                    providerRatingFacade.findByMultipleCriteria(params.getClients(), params.getProviders(), params.getMinRating(), params.getMaxRating(),
                            params.getExactRating(), params.getClientComment(), params.getProviderDementi(), params.getOffset(), params.getLimit())
            );

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all provider ratings without filtering (eventually paginated)
            providerRatings = new ResourceList<>(providerRatingFacade.findAll(params.getOffset(), params.getLimit()));
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        ProviderRatingResource.populateWithHATEOASLinks(providerRatings, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(providerRatings).build();
    }

    /**
     * Method matches specific Provider Rating resource by identifier and returns its instance.
     * Provider Rating composite identifier has pattern: providerId+clientId.
     */
    @GET
    @Path("/{providerId : \\d+}+{clientId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProviderRating( @PathParam("providerId") Long providerId,
                                       @PathParam("clientId") Long clientId,
                                       @BeanParam GenericBeanParam params ) throws NotFoundException, ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning given Provider Rating for providerId: " + providerId + " and clientId: " + clientId +
                " by executing ProviderRatingResource.getProviderRating(providerId, clientId) method of REST API");

        ProviderRating foundProviderRating = providerRatingFacade.find(new ProviderRatingId(providerId, clientId));
        if(foundProviderRating == null)
            throw new NotFoundException("Could not find provider rating for id (" + providerId + "," + clientId + ").");

        // adding hypermedia links to provider rating resource
        ProviderRatingResource.populateWithHATEOASLinks(foundProviderRating, params.getUriInfo());

        return Response.status(Status.OK).entity(foundProviderRating).build();
    }

    /**
     * Method that takes Provider Rating as XML or JSON and creates its new instance in database
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createProviderRating( ProviderRating providerRating,
                                          @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "creating new Provider Rating by executing ProviderRatingResource.createProviderRating(providerRating) method of REST API");

        ProviderRating createdProviderRating = null;
        URI locationURI = null;

        try {
            // persist new resource in database
            Long providerId = providerRating.getProvider().getUserId();
            Long clientId = providerRating.getClient().getClientId();
            createdProviderRating = providerRatingFacade.createForProviderAndClient(providerId, clientId, providerRating);

            // populate created resource with hypermedia links
            ProviderRatingResource.populateWithHATEOASLinks(createdProviderRating, params.getUriInfo());

            // construct link to newly created resource to return in HTTP Header
            Method providerRatingMethod = ProviderRatingResource.class.getMethod("getProviderRating", Long.class, Long.class, GenericBeanParam.class);
            locationURI = params.getUriInfo().getBaseUriBuilder()
                    .path(ProviderRatingResource.class)
                    .path(providerRatingMethod)
                    .resolveTemplate("providerId", String.valueOf(createdProviderRating.getProvider().getUserId()))
                    .resolveTemplate("clientId", String.valueOf(createdProviderRating.getClient().getClientId()))
                    .build();

        } catch(EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch(EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch(Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_CREATION_ERROR_MESSAGE);
        }

        return Response.created(locationURI).entity(createdProviderRating).build();
    }

    /**
     * Method that takes updated Provider Rating as XML or JSON and its composite ID as path param.
     * It updates Provider Rating in database for provided composite ID.
     */
    @PUT
    @Path("/{providerId : \\d+}+{clientId : \\d+}") // catch only numeric identifiers
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateProviderRating( @PathParam("providerId") Long providerId,
                                          @PathParam("clientId") Long clientId,
                                          ProviderRating providerRating,
                                          @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "updating existing Provider Rating by executing ProviderRatingResource.updateProviderRating(providerId, clientId, providerRating) method of REST API");

        // create composite ID based on path params
        ProviderRatingId providerRatingId = new ProviderRatingId(providerId, clientId);

        ProviderRating updatedProviderRating = null;
        try {
            // reflect updated resource object in database
            updatedProviderRating = providerRatingFacade.update(providerRatingId, providerRating);
            // populate created resource with hypermedia links
            ProviderRatingResource.populateWithHATEOASLinks(updatedProviderRating, params.getUriInfo());

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_UPDATE_ERROR_MESSAGE);
        }

        return Response.status(Status.OK).entity(updatedProviderRating).build();
    }

    /**
     * Method that removes Provider Rating entity from database for given ID.
     * The provider rating composite id is passed through path params.
     */
    @DELETE
    @Path("/{providerId : \\d+}+{clientId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeProviderRating( @PathParam("providerId") Long providerId,
                                          @PathParam("clientId") Long clientId,
                                          @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing given Provider Rating by executing ProviderRatingResource.removeProviderRating(providerId, clientId) method of REST API");

        // remove entity from database
        Integer noOfDeleted = providerRatingFacade.deleteById(new ProviderRatingId(providerId, clientId));

        if(noOfDeleted == 0)
            throw new NotFoundException("Could not find provider rating to delete for id (" + providerId + "," + clientId + ").");
        else if(noOfDeleted != 1)
            throw new InternalServerErrorException("Some error occurred while trying to delete provider rating with id (" + providerId + "," + clientId + ").");

        return Response.status(Status.NO_CONTENT).build();
    }

    /**
     * Additional methods returning subset of resources based on given criteria
     * you can also achieve similar results by applying @QueryParams to generic method
     * returning all resources in order to filter and limit them
     */

    /**
     * Method returns number of Provider Rating entities in database
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countProviderRatings( @BeanParam GenericBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning number of provider ratings by executing ProviderRatingResource.countProviderRatings() method of REST API");

        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(providerRatingFacade.count()), 200, "number of provider ratings");
        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * This method enables to populate list of resources and each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList<ProviderRating> providerRatings, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
       ResourceList.generateNavigationLinks(providerRatings, uriInfo, offset, limit);

        try {
            // count resources hypermedia link
            Method countMethod = ProviderRatingResource.class.getMethod("countProviderRatings", String.class);
            providerRatings.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderRatingResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            providerRatings.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderRatingResource.class).build()).rel("provider-ratings").build() );

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for(ProviderRating providerRating : providerRatings.getResources())
            ProviderRatingResource.populateWithHATEOASLinks(providerRating, uriInfo);

    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(ProviderRating providerRating, UriInfo uriInfo) {

        try {
            // self link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/{sub-id}
            Method providerRatingMethod = ProviderRatingResource.class.getMethod("getProviderRating", Long.class, Long.class, GenericBeanParam.class);
            providerRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderRatingResource.class)
                    .path(providerRatingMethod)
                    .resolveTemplate("providerId", providerRating.getProvider().getUserId().toString())
                    .resolveTemplate("clientId", providerRating.getClient().getClientId().toString())
                    .build())
                    .rel("self").build());

            // sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}
            Method clientProviderRatingsMethod = ClientResource.class.getMethod("getProviderRatingResource");
            providerRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(clientProviderRatingsMethod)
                    .resolveTemplate("clientId", providerRating.getClient().getClientId().toString())
                    .build())
                    .rel("client-provider-ratings").build());

            Method providerProviderRatingsMethod = ProviderResource.class.getMethod("getProviderRatingResource");
            providerRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerProviderRatingsMethod)
                    .resolveTemplate("userId", providerRating.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-provider-ratings").build());

            // sub-collection count link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/count
            Method countByClientMethod = ClientResource.ProviderRatingResource.class.getMethod("countClientProviderRatings", Long.class, GenericBeanParam.class);
            providerRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(clientProviderRatingsMethod)
                    .path(countByClientMethod)
                    .resolveTemplate("clientId", providerRating.getClient().getClientId().toString())
                    .build())
                    .rel("client-provider-ratings-count").build());

            Method countByProviderMethod = ProviderResource.ProviderRatingResource.class.getMethod("countProviderRatings", Long.class, GenericBeanParam.class);
            providerRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerProviderRatingsMethod)
                    .path(countByProviderMethod)
                    .resolveTemplate("userId", providerRating.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-provider-ratings-count").build());

            // provider average rating link with pattern: http://localhost:port/app/{resources}/{id}/{subresources}/average-rating
            Method providerAverageRatingMethod = ProviderResource.ProviderRatingResource.class.getMethod("getAverageProviderRating", Long.class, GenericBeanParam.class);
            providerRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerProviderRatingsMethod)
                    .path(providerAverageRatingMethod)
                    .resolveTemplate("userId", providerRating.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-average-rating").build());

            // rated sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/rated/{rating}
            providerRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(clientProviderRatingsMethod)
                    .path("rated")
                    .resolveTemplate("clientId", providerRating.getClient().getClientId().toString())
                    .build())
                    .rel("client-provider-ratings-rated").build());

            providerRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerProviderRatingsMethod)
                    .path("rated")
                    .resolveTemplate("userId", providerRating.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-provider-ratings-rated").build());

            // rated-above sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/rated-above/{minRating}
            providerRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(clientProviderRatingsMethod)
                    .path("rated-above")
                    .resolveTemplate("clientId", providerRating.getClient().getClientId().toString())
                    .build())
                    .rel("client-provider-ratings-rated-above").build());

            providerRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerProviderRatingsMethod)
                    .path("rated-above")
                    .resolveTemplate("userId", providerRating.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-provider-ratings-rated-above").build());

            // rated-below sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/rated-below/{maxRating}
            providerRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(clientProviderRatingsMethod)
                    .path("rated-below")
                    .resolveTemplate("clientId", providerRating.getClient().getClientId().toString())
                    .build())
                    .rel("client-provider-ratings-rated-below").build());

            providerRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerProviderRatingsMethod)
                    .path("rated-below")
                    .resolveTemplate("userId", providerRating.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-provider-ratings-rated-below").build());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }
}
