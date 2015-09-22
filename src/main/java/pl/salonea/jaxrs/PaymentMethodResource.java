package pl.salonea.jaxrs;

import pl.salonea.entities.Industry;
import pl.salonea.entities.PaymentMethod;
import pl.salonea.entities.Provider;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.PaymentMethodWrapper;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.ws.Response;
import java.lang.reflect.Method;

/**
 * Created by michzio on 13/09/2015.
 */
@Path("/payment-methods")
public class PaymentMethodResource {

    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countPaymentMethods( @HeaderParam("authToken") String authToken ) throws ForbiddenException {

        if(authToken == null) throw new ForbiddenException("Unauthorized access to web service.");

        return null;
    }

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList paymentMethods, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        if(offset != null && limit != null) {
            // self collection link
            paymentMethods.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", offset).queryParam("limit", limit).build()).rel("self").build() );
            // prev collection link
            Integer prevOffset = (offset - limit) < 0 ? 0 : offset - limit;
            Integer prevLimit = offset - prevOffset;
            if(prevLimit > 0)
                paymentMethods.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", prevOffset).queryParam("limit", prevLimit).build()).rel("prev").build() );
            else
                paymentMethods.getLinks().add( Link.fromUri("").rel("prev").build() );
            // next collection link
            paymentMethods.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", (offset+limit)).queryParam("limit", limit).build()).rel("next").build() );
        } else {
            paymentMethods.getLinks().add( Link.fromUri(uriInfo.getAbsolutePath()).rel("self").build() );
        }

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
            }  else if(object instanceof PaymentMethodWrapper) {
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

}
