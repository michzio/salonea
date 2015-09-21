package pl.salonea.jaxrs;

import pl.salonea.entities.Industry;
import pl.salonea.entities.Provider;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.IndustryWrapper;

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
@Path("/industries")
public class IndustryResource {

    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countIndustries( @HeaderParam("authToken") String authToken ) throws ForbiddenException {

        if(authToken == null) throw new ForbiddenException("Unauthorized access to web service.");

        return null;
    }

    // private helper methods e.g. to populate resources/resource lists with HATEOAS links

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList industries, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        if(offset != null && limit != null) {
            // self collection link
            industries.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", offset).queryParam("limit", limit).build()).rel("self").build() );
            // prev collection link
            Integer prevOffset = (offset - limit) < 0 ? 0 : offset - limit;
            Integer prevLimit = offset - prevOffset;
            if(prevLimit > 0)
                industries.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", prevOffset).queryParam("limit", prevLimit).build()).rel("prev").build() );
            else
                industries.getLinks().add( Link.fromUri("").rel("prev").build() );
            // next collection link
            industries.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", (offset+limit)).queryParam("limit", limit).build()).rel("next").build() );
        } else {
            industries.getLinks().add( Link.fromUri(uriInfo.getAbsolutePath()).rel("self").build() );
        }

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
    public static void populateWithHATEOASLinks(IndustryWrapper industryWrapper, UriInfo uriinfo) {

        IndustryResource.populateWithHATEOASLinks(industryWrapper.getIndustry(), uriinfo);

        for(Provider provider : industryWrapper.getProviders())
            ProviderResource.populateWithHATEOASLinks(provider, uriinfo);
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
}
