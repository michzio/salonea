package pl.salonea.jaxrs;

import pl.salonea.entities.Industry;
import pl.salonea.jaxrs.utils.hateoas.Link;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriInfo;

/**
 * Created by michzio on 12/09/2015.
 */
@Path("/industries")
public class IndustryResource {



    public static void populateWithHATEOASLinks(Industry industry, UriInfo uriInfo) {

        // self link with pattern: http://localhost:port/app/rest/{resources}/{id}
        industry.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                    .path(IndustryResource.class)
                                                    .path(industry.getIndustryId().toString())
                                                    .build())
                                     .rel("self").build() );
    }
}
