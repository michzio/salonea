package pl.salonea.jaxrs;

import pl.salonea.entities.Industry;
import pl.salonea.entities.PaymentMethod;
import pl.salonea.jaxrs.utils.hateoas.Link;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriInfo;

/**
 * Created by michzio on 13/09/2015.
 */
@Path("/payment-methods")
public class PaymentMethodResource {

    public static void populateWithHATEOASLinks(PaymentMethod paymentMethod, UriInfo uriInfo) {

        // self link with pattern: http://localhost:port/app/rest/{resources}/{id}
        paymentMethod.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                            .path(PaymentMethodResource.class)
                                                            .path(paymentMethod.getId().toString())
                                                            .build())
                                            .rel("self").build() );
    }
}
