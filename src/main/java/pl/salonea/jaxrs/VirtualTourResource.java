package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.VirtualTourFacade;
import pl.salonea.entities.Tag;
import pl.salonea.entities.VirtualTour;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.VirtualTourWrapper;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * Created by michzio on 27/11/2015.
 */
@Path("/virtual-tours")
public class VirtualTourResource {

    private static final Logger logger = Logger.getLogger(VirtualTourResource.class.getName());

    @Inject
    private VirtualTourFacade virtualTourFacade;

    /**
     * Method returns all Virtual Tour resources
     * They can be additionally filtered or paginated by @QueryParams
     */

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList virtualTours, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(virtualTours, uriInfo, offset, limit);

        try {
            // count resources hypermedia link
            Method countMethod = VirtualTourResource.class.getMethod("countVirtualTours", GenericBeanParam.class);
            virtualTours.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(VirtualTourResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            virtualTours.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(VirtualTourResource.class).build()).rel("virtual-tours").build() );

            // get all resources eagerly hypermedia link

            // get subset of resources hypermedia links

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for (Object object : virtualTours.getResources()) {
            if(object instanceof VirtualTour) {
                VirtualTourResource.populateWithHATEOASLinks( (VirtualTour) object, uriInfo );
            } else if(object instanceof VirtualTourWrapper) {
                VirtualTourResource.populateWithHATEOASLinks( (VirtualTourWrapper) object, uriInfo );
            }
        }
    }

    /**
     * This method enables to populate each individual resource wrapper with hypermedia links
     */
    public static void populateWithHATEOASLinks(VirtualTourWrapper virtualTourWrapper, UriInfo uriInfo) {

        VirtualTourResource.populateWithHATEOASLinks(virtualTourWrapper.getVirtualTour(), uriInfo);

        for(Tag tag : virtualTourWrapper.getTags())
            pl.salonea.jaxrs.TagResource.populateWithHATEOASLinks(tag, uriInfo);
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(VirtualTour virtualTour, UriInfo uriInfo) {

        // self link with pattern: http://localhost:port/app/rest/{resources}/{id}
        virtualTour.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                        .path(VirtualTourResource.class)
                                                        .path(virtualTour.getTourId().toString())
                                                        .build())
                                        .rel("self").build() );

        // collection link with pattern: http://localhost:port/app/rest/{resources}
        virtualTour.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                         .path(VirtualTourResource.class)
                                                         .build())
                                        .rel("virtual-tours").build() );

        try {
            // self eagerly link with pattern http://localhost:port/app/rest/{resources}/{id}/eagerly
            Method virtualTourEagerlyMethod = VirtualTourResource.class.getMethod("getVirtualTourEagerly", Long.class, GenericBeanParam.class);
            virtualTour.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                        .path(VirtualTourResource.class)
                        .path(virtualTourEagerlyMethod)
                        .resolveTemplate("tourId", virtualTour.getTourId().toString())
                        .build())
                        .rel("virtual-tour-eagerly").build() );

            // associated collections links with pattern: http://localhost:port/app/rest/{resources}/{id}/{relationship}

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
