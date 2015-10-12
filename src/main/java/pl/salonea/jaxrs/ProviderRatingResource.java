package pl.salonea.jaxrs;

import pl.salonea.entities.ProviderRating;
import pl.salonea.entities.ProviderService;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.hateoas.Link;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.ws.Response;
import java.lang.reflect.Method;

/**
 * Created by michzio on 12/09/2015.
 */
@Path("/provider-ratings")
public class ProviderRatingResource {

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
        if(offset != null && limit != null) {
            // self collection link
            providerRatings.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", offset).queryParam("limit", limit).build()).rel("self").build() );
            // prev collection link
            Integer prevOffset = (offset - limit) < 0 ? 0 : offset - limit;
            Integer prevLimit = offset - prevOffset;
            if(prevLimit > 0)
                providerRatings.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", prevOffset).queryParam("limit", prevLimit).build()).rel("prev").build() );
            else
                providerRatings.getLinks().add( Link.fromUri("").rel("prev").build() );
            // next collection link
            providerRatings.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", (offset+limit)).queryParam("limit", limit).build()).rel("next").build() );
        } else {
            providerRatings.getLinks().add( Link.fromUri(uriInfo.getAbsolutePath()).rel("self").build() );
        }

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
            Method clientProviderRatingsMethod = ClientResource.class.getMethod("getProviderRatingResource");
            providerRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(clientProviderRatingsMethod)
                    .path(providerRating.getProvider().getUserId().toString())
                    .resolveTemplate("clientId", providerRating.getClient().getClientId().toString())
                    .build())
                    .rel("self").build());

            // sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}
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
            Method countByClientMethod = ClientResource.ProviderRatingResource.class.getMethod("countProviderRatingsByClient", Long.class, GenericBeanParam.class);
            providerRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(clientProviderRatingsMethod)
                    .path(countByClientMethod)
                    .resolveTemplate("clientId", providerRating.getClient().getClientId().toString())
                    .build())
                    .rel("client-provider-ratings-count").build());

            Method countByProviderMethod = ProviderResource.ProviderRatingResource.class.getMethod("countProviderRatingsByProvider", Long.class, GenericBeanParam.class);
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
                    .path(ProviderResource.class)
                    .path(providerProviderRatingsMethod)
                    .path("rated")
                    .resolveTemplate("userId", providerRating.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-provider-ratings-rated").build());

            // rated-above sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/rated-above/{minRating}
            providerRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerProviderRatingsMethod)
                    .path("rated-above")
                    .resolveTemplate("userId", providerRating.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-provider-ratings-rated-above").build());

            // rated-below sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/rated-below/{maxRating}
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
