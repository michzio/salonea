package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.TagFacade;
import pl.salonea.entities.ServicePointPhoto;
import pl.salonea.entities.Tag;
import pl.salonea.entities.VirtualTour;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.TagWrapper;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * Created by michzio on 27/11/2015.
 */
@Path("/tags")
public class TagResource {

    private static final Logger logger = Logger.getLogger(TagResource.class.getName());

    @Inject
    private TagFacade tagFacade;

    /**
     * Method returns all Tag resources
     * They can be additionally filtered or paginated by @QueryParams
     */

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList tags, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(tags, uriInfo, offset, limit);

        try {
            // count resources hypermedia link
            Method countMethod = TagResource.class.getMethod("countTags", GenericBeanParam.class);
            tags.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(TagResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            tags.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(TagResource.class).build()).rel("tags").build() );

            // get all resources eagerly hypermedia link

            // get subset of resources hypermedia links

        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        }

        for( Object object : tags.getResources() ) {
            if (object instanceof Tag) {
                TagResource.populateWithHATEOASLinks( (Tag) object, uriInfo );
            } else if(object instanceof TagWrapper) {
                TagResource.populateWithHATEOASLinks( (TagWrapper) object, uriInfo );
            }
        }
    }

    /**
     * This method enables to populate each individual resource wrapper with hypermedia links
     */
    public static void populateWithHATEOASLinks(TagWrapper tagWrapper, UriInfo uriInfo) {

        TagResource.populateWithHATEOASLinks(tagWrapper.getTag(), uriInfo);

        for(ServicePointPhoto photo : tagWrapper.getPhotos())
            pl.salonea.jaxrs.ServicePointPhotoResource.populateWithHATEOASLinks(photo, uriInfo);

        for(VirtualTour virtualTour : tagWrapper.getVirtualTours())
            pl.salonea.jaxrs.VirtualTourResource.populateWithHATEOASLinks(virtualTour, uriInfo);
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(Tag tag, UriInfo uriInfo) {

        // self link with pattern: http://localhost:port/app/rest/{resources}/{id}

        // collection link with pattern: http://localhost:port/app/rest/{resources}

        try {
            // self eagerly link with pattern http://localhost:port/app/rest/{resources}/{id}/eagerly
            Method tagEagerlyMethod = TagResource.class.getMethod("getTagEagerly", Long.class, GenericBeanParam.class);
            tag.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TagResource.class)
                    .path(tagEagerlyMethod)
                    .resolveTemplate("tagId", tag.getTagId().toString())
                    .build())
                    .rel("tag-eagerly").build());

            // associated collections links with pattern: http://localhost:port/app/rest/{resources}/{id}/{relationship}

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}