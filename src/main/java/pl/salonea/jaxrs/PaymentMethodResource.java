package pl.salonea.jaxrs;

import pl.salonea.entities.Industry;
import pl.salonea.entities.PaymentMethod;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.hateoas.Link;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriInfo;

/**
 * Created by michzio on 13/09/2015.
 */
@Path("/payment-methods")
public class PaymentMethodResource {



    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList<PaymentMethod> paymentMethods, UriInfo uriInfo, Integer offset, Integer limit) {

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
    }

}
