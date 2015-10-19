package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.IndustryFacade;
import pl.salonea.ejb.stateless.ProviderFacade;
import pl.salonea.entities.Industry;
import pl.salonea.entities.Provider;
import pl.salonea.jaxrs.bean_params.ProviderBeanParam;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.IndustryWrapper;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by michzio on 12/09/2015.
 */
@Path("/industries")
public class IndustryResource {

    private static final Logger logger = Logger.getLogger(IndustryResource.class.getName());

    @Inject
    private IndustryFacade industryFacade;
    @Inject
    private ProviderFacade providerFacade;

    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countIndustries( @HeaderParam("authToken") String authToken ) throws ForbiddenException {

        if(authToken == null) throw new ForbiddenException("Unauthorized access to web service.");

        return null;
    }

    /**
     * related subresources (through relationships)
     */

    @Path("/{industryId : \\d+}/providers")
    public ProviderResource getProviderResource() {
        return new ProviderResource();
    }

    // helper methods e.g. to populate resources/resource lists with HATEOAS links

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList industries, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(industries, uriInfo, offset, limit);

        try {

            // count resources hypermedia link
            Method countMethod = IndustryResource.class.getMethod("countIndustries", String.class);
            industries.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(IndustryResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            industries.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(IndustryResource.class).build()).rel("industries").build() );

            // TODO

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for(Object object : industries.getResources()) {
            if(object instanceof Industry) {
                IndustryResource.populateWithHATEOASLinks((Industry) object, uriInfo);
            }  else if(object instanceof IndustryWrapper) {
                IndustryResource.populateWithHATEOASLinks( (IndustryWrapper) object, uriInfo);
            }
        }
    }

    /**
     * This method enables to populate each individual resource wrapper with hypermedia links
     */
    public static void populateWithHATEOASLinks(IndustryWrapper industryWrapper, UriInfo uriInfo) {

        IndustryResource.populateWithHATEOASLinks(industryWrapper.getIndustry(), uriInfo);

        for(Provider provider : industryWrapper.getProviders())
            pl.salonea.jaxrs.ProviderResource.populateWithHATEOASLinks(provider, uriInfo);
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(Industry industry, UriInfo uriInfo) {

        // self link with pattern: http://localhost:port/app/rest/{resources}/{id}
        industry.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                    .path(IndustryResource.class)
                                                    .path(industry.getIndustryId().toString())
                                                    .build())
                                     .rel("self").build() );

        // collection link with pattern: http://localhost:port/app/rest/{resources}
        industry.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                    .path(IndustryResource.class)
                                                    .build())
                                    .rel("industries").build());

    }

    public class ProviderResource {

        public ProviderResource() { }

        /**
         * Method returns subset of Provider entities for given Industry entity.
         * The industry id is passed through path param.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getIndustryProviders(@PathParam("industryId") Long industryId,
                                             @BeanParam ProviderBeanParam params) throws ForbiddenException, NotFoundException {

            if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning providers for given industry using IndustryResource.ProviderResource.getIndustryProviders(industryId) method of REST API");

            // find industry entity for which to get associated providers
            Industry industry = industryFacade.find(industryId);
            if (industry == null)
                throw new NotFoundException("Could not find industry for id " + industryId + ".");

            // calculate number of filter query params
            Integer noOfParams = params.getUriInfo().getQueryParameters().size();
            if(params.getOffset() != null) noOfParams -= 1;
            if(params.getLimit() != null) noOfParams -= 1;

            ResourceList<Provider> providers = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Industry> industries = new ArrayList<>();
                industries.add(industry);

                // get providers for given industry filtered by given params.
                providers = new ResourceList<>(
                        providerFacade.findByMultipleCriteria(params.getCorporations(), params.getProviderTypes(), industries, params.getPaymentMethods(),
                                params.getServices(), params.getRated(), params.getMinAvgRating(), params.getMaxAvgRating(),params.getRatingClients(),
                                params.getProviderName(), params.getDescription(), params.getOffset(), params.getLimit())
                );

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get providers for given industry without filtering
                providers = new ResourceList<>( providerFacade.findByIndustry(industry, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providers).build();
        }

    }
}
