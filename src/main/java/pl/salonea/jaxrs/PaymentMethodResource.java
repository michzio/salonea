package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.PaymentMethodFacade;
import pl.salonea.ejb.stateless.ProviderFacade;
import pl.salonea.entities.PaymentMethod;
import pl.salonea.entities.Provider;
import pl.salonea.jaxrs.bean_params.ProviderBeanParam;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.PaymentMethodWrapper;

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
 * Created by michzio on 13/09/2015.
 */
@Path("/payment-methods")
public class PaymentMethodResource {

    private static final Logger logger = Logger.getLogger(PaymentMethodResource.class.getName());

    @Inject
    private PaymentMethodFacade paymentMethodFacade;
    @Inject
    private ProviderFacade providerFacade;

    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countPaymentMethods( @HeaderParam("authToken") String authToken ) throws ForbiddenException {

        if(authToken == null) throw new ForbiddenException("Unauthorized access to web service.");

        // TODO
        return null;
    }

    /**
     * related subresources (through relationships)
     */

    @Path("/{paymentMethodId : \\d+}/providers")
    public ProviderResource getProviderResource() {
        return new ProviderResource();
    }

    // helper methods e.g. to populate resources/resource lists with HATEOAS links

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList paymentMethods, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(paymentMethods, uriInfo, offset, limit);

        try {
            // count resources hypermedia link
            Method countMethod = PaymentMethodResource.class.getMethod("countPaymentMethods", String.class);
            paymentMethods.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(PaymentMethodResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            paymentMethods.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(PaymentMethodResource.class).build()).rel("payment-methods").build() );

            // TODO

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for(Object object : paymentMethods.getResources()) {
            if(object instanceof PaymentMethod) {
                PaymentMethodResource.populateWithHATEOASLinks((PaymentMethod) object, uriInfo);
            } else if(object instanceof PaymentMethodWrapper) {
                PaymentMethodResource.populateWithHATEOASLinks( (PaymentMethodWrapper) object, uriInfo);
            }
        }

    }

    /**
     * This method enables to populate each individual resource wrapper with hypermedia links
     */
    public static void populateWithHATEOASLinks(PaymentMethodWrapper paymentMethodWrapper, UriInfo uriInfo) {

        PaymentMethodResource.populateWithHATEOASLinks(paymentMethodWrapper.getPaymentMethod(), uriInfo);

        for(Provider provider : paymentMethodWrapper.getProviders())
            pl.salonea.jaxrs.ProviderResource.populateWithHATEOASLinks(provider, uriInfo);
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(PaymentMethod paymentMethod, UriInfo uriInfo) {

        // self link with pattern: http://localhost:port/app/rest/{resources}/{id}
        paymentMethod.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                            .path(PaymentMethodResource.class)
                                                            .path(paymentMethod.getId().toString())
                                                            .build())
                                            .rel("self").build() );

        // collection link with pattern: http://localhost:port/app/rest/{resources}
        paymentMethod.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                          .path(PaymentMethodResource.class)
                                                          .build())
                                          .rel("payment-methods").build() );
    }

    public class ProviderResource {

        public ProviderResource() { }

        /**
         * Method returns subset of Provider entities for given Payment Method entity.
         * The payment method id is passed through path param.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getPaymentMethodProviders( @PathParam("paymentMethodId") Integer paymentMethodId,
                                                   @BeanParam ProviderBeanParam params) throws ForbiddenException, NotFoundException {

            if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning providers for given payment method using PaymentMethodResource.ProviderResource.getPaymentMethodProviders(paymentMethodId) method of REST API");

            // find payment method entity for which to get associated providers
            PaymentMethod paymentMethod = paymentMethodFacade.find(paymentMethodId);
            if(paymentMethod == null)
                throw new NotFoundException("Could not find payment method for id " + paymentMethodId + ".");

            // calculate number of filter query params
            Integer noOfParams = params.getUriInfo().getQueryParameters().size();
            if(params.getOffset() != null) noOfParams -= 1;
            if(params.getLimit() != null) noOfParams -= 1;

            ResourceList<Provider> providers = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<PaymentMethod> paymentMethods = new ArrayList<>();
                paymentMethods.add(paymentMethod);

                // get providers for given payment method filtered by given params.
                providers = new ResourceList<>(
                        providerFacade.findByMultipleCriteria(params.getCorporations(), params.getProviderTypes(), params.getIndustries(), paymentMethods,
                                params.getServices(), params.getRated(), params.getMinAvgRating(), params.getMaxAvgRating(), params.getRatingClients(),
                                params.getProviderName(), params.getDescription(), params.getOffset(), params.getLimit())
                );
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get providers for given payment method without filtering
                providers = new ResourceList<>( providerFacade.findByPaymentMethod(paymentMethod, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providers).build();
        }
    }
}
