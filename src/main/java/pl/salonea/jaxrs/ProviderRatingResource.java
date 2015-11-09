package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.ProviderRatingFacade;
import pl.salonea.entities.ProviderRating;
import pl.salonea.entities.idclass.ProviderRatingId;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.exceptions.NotFoundException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
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

    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countProviderRatings( @HeaderParam("authToken") String authToken ) throws ForbiddenException {

        if(authToken == null) throw new ForbiddenException("Unauthorized access to web services.");
    // TODO
        return null;
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
