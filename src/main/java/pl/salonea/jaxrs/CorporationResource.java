package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.CorporationFacade;
import pl.salonea.ejb.stateless.ProviderFacade;
import pl.salonea.entities.Corporation;
import pl.salonea.entities.Provider;
import pl.salonea.jaxrs.bean_params.ProviderBeanParam;
import pl.salonea.jaxrs.utils.ResourceList;

import javax.inject.Inject;
import javax.ws.rs.*;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.CorporationWrapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by michzio on 17/10/2015.
 */
@Path("/corporations")
public class CorporationResource {

    private static final Logger logger = Logger.getLogger(CorporationResource.class.getName());

    @Inject
    private CorporationFacade corporationFacade;
    @Inject
    private ProviderFacade providerFacade;

    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countCorporations( @HeaderParam("authToken") String authToken ) throws ForbiddenException {

        if(authToken == null) throw new ForbiddenException("Unauthorized access to web service.");

        return null;
    }

    /**
     * related subresources (through relationships)
     */

    @Path("/{corporationId : \\d+}/providers")
    public ProviderResource getProviderResource() {
        return new ProviderResource();
    }

    // helper methods e.g. to populate resources/resource lists with HATEOAS links

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList corporations, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(corporations, uriInfo, offset, limit);

        try {

            // count resources hypermedia link
            Method countMethod = CorporationResource.class.getMethod("countCorporations", String.class);
            corporations.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(CorporationResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            corporations.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(CorporationResource.class).build()).rel("corporations").build() );

            // TODO

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for(Object object : corporations.getResources()) {
            if(object instanceof Corporation) {
                CorporationResource.populateWithHATEOASLinks( (Corporation) object, uriInfo);
            } else if(object instanceof CorporationWrapper) {
                CorporationResource.populateWithHATEOASLinks( (CorporationWrapper) object, uriInfo);
            }
        }
    }

    /**
     * This method enables to populate each individual resource wrapper with hypermedia links
     */
    public static void populateWithHATEOASLinks(CorporationWrapper corporationWrapper, UriInfo uriInfo) {

        CorporationResource.populateWithHATEOASLinks(corporationWrapper.getCorporation(), uriInfo);

        for(Provider provider : corporationWrapper.getProviders())
            pl.salonea.jaxrs.ProviderResource.populateWithHATEOASLinks(provider, uriInfo);
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(Corporation corporation, UriInfo uriInfo) {

        // self link with pattern: http://localhost:port/app/rest/{resources}/{id}
        corporation.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                        .path(CorporationResource.class)
                                                        .path(corporation.getCorporationId().toString())
                                                        .build())
                                        .rel("self").build());

        // collection link with pattern: http://localhost:port/app/rest/{resources}
        corporation.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                        .path(CorporationResource.class)
                                                        .build())
                                        .rel("corporations").build());

    }

    public class ProviderResource {

        public ProviderResource() { }

        /**
         * Method returns subset of Provider entities for given Corporation entity.
         * The corporation id is passed through path param.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getCorporationProviders(@PathParam("corporationId") Long corporationId,
                                                @BeanParam ProviderBeanParam params) throws ForbiddenException, NotFoundException {

            if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning providers for given corporation using CorporationResource.ProviderResource.getCorporationProviders(corporationId) method of REST API");

            // find corporation entity for which to get associated providers
            Corporation corporation = corporationFacade.find(corporationId);
            if (corporation == null)
                throw new NotFoundException("Could not find corporation for id " + corporationId + ".");

            // calculate number of filter query params
            Integer noOfParams = params.getUriInfo().getQueryParameters().size();
            if(params.getOffset() != null) noOfParams -= 1;
            if(params.getLimit() != null) noOfParams -= 1;

            ResourceList<Provider> providers = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Corporation> corporations = new ArrayList<>();
                corporations.add(corporation);

                // get providers for given corporation filtered by given params
                providers = new ResourceList<>(
                        providerFacade.findByMultipleCriteria(corporations, params.getProviderTypes(), params.getIndustries(), params.getPaymentMethods(),
                                params.getServices(), params.getRated(), params.getMinAvgRating(), params.getMaxAvgRating(),params.getRatingClients(),
                                params.getProviderName(), params.getDescription(), params.getOffset(), params.getLimit())
                );
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get providers for given corporation without filtering
                providers = new ResourceList<>(providerFacade.findByCorporation(corporation, params.getOffset(), params.getLimit()));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Response.Status.OK).entity(providers).build();
        }

    }

}
