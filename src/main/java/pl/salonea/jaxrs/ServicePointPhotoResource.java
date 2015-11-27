package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.ServicePointPhotoFacade;
import pl.salonea.entities.ServicePointPhoto;
import pl.salonea.entities.Tag;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.bean_params.ServicePointPhotoBeanParam;
import pl.salonea.jaxrs.exceptions.BadRequestException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.ServicePointPhotoWrapper;

import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by michzio on 26/11/2015.
 */
@Path("service-point-photos")
public class ServicePointPhotoResource {

    private static final Logger logger = Logger.getLogger(ServicePointPhotoResource.class.getName());

    @Inject
    private ServicePointPhotoFacade servicePointPhotoFacade;

    /**
     * Method returns all Service Point Photo resources
     * They can be additionally filtered or paginated by @QueryParams
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPhotos( @BeanParam ServicePointPhotoBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Service Point Photos by executing ServicePointPhotoResource.getPhotos() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<ServicePointPhoto> photos = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            if(params.getKeywords() != null) {
                if(params.getFileNames() != null || params.getDescriptions() != null)
                    throw new BadRequestException("Query params cannot include keywords and fileNames or descriptions at the same time.");

                if(params.getTagNames() != null) {
                    // find by keywords and tag names
                    photos = new ResourceList<>(
                            servicePointPhotoFacade.findByMultipleCriteria(params.getKeywords(), params.getTagNames(), params.getServicePoints(),
                                    params.getProviders(), params.getCorporations(), params.getOffset(), params.getLimit())
                    );
                } else {
                    // find only by keywords
                    photos = new ResourceList<>(
                            servicePointPhotoFacade.findByMultipleCriteria(params.getKeywords(),params.getServicePoints(), params.getProviders(),
                                    params.getCorporations(), params.getOffset(), params.getLimit())
                    );
                }
            } else {
                // find by firstNames, descriptions or tagNames
                photos = new ResourceList<>(
                        servicePointPhotoFacade.findByMultipleCriteria(params.getFileNames(), params.getDescriptions(), params.getTagNames(),
                                params.getServicePoints(), params.getProviders(), params.getCorporations(), params.getOffset(), params.getLimit())
                );
            }

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all photos without filtering (eventually paginated)
            photos = new ResourceList<>( servicePointPhotoFacade.findAll(params.getOffset(), params.getLimit()) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        ServicePointPhotoResource.populateWithHATEOASLinks(photos, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(photos).build();
    }

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList photos, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(photos, uriInfo, offset, limit);

        try {
            // count resources hypermedia link
            Method countMethod = ServicePointPhotoResource.class.getMethod("countPhotos", GenericBeanParam.class);
            photos.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ServicePointPhotoResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            photos.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ServicePointPhotoResource.class).build()).rel("photos").build() );

            // get all resources eagerly hypermedia link

            // get subset of resources hypermedia links


        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for (Object object : photos.getResources()) {
            if(object instanceof ServicePointPhoto) {
                ServicePointPhotoResource.populateWithHATEOASLinks( (ServicePointPhoto) object, uriInfo );
            } else if(object instanceof ServicePointPhotoWrapper) {
                ServicePointPhotoResource.populateWithHATEOASLinks( (ServicePointPhotoWrapper) object, uriInfo );
            }
        }
    }

    /**
     * This method enables to populate each individual resource wrapper with hypermedia links
     */
    public static void populateWithHATEOASLinks(ServicePointPhotoWrapper photoWrapper, UriInfo uriInfo) {

        ServicePointPhotoResource.populateWithHATEOASLinks(photoWrapper.getPhoto(), uriInfo);

        for(Tag tag : photoWrapper.getTags())
            pl.salonea.jaxrs.TagResource.populateWithHATEOASLinks(tag, uriInfo);
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(ServicePointPhoto photo, UriInfo uriInfo) {

        // self link with pattern: http://localhost:port/app/rest/{resources}/{id}
        photo.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                  .path(ServicePointPhotoResource.class)
                                                  .path(photo.getPhotoId().toString())
                                                  .build())
                                    .rel("self").build());

        // collection link with pattern: http://localhost:port/app/rest/{resources}
        photo.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                  .path(ServicePointPhotoResource.class)
                                                  .build())
                                  .rel("photos").build());

        try {
            // self eagerly link with pattern http://localhost:port/app/rest/{resources}/{id}/eagerly
            Method photoEagerlyMethod = ServicePointPhotoResource.class.getMethod("getPhotoEagerly", Long.class, GenericBeanParam.class);
            photo.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServicePointPhotoResource.class)
                    .path(photoEagerlyMethod)
                    .resolveTemplate("photoId", photo.getPhotoId().toString())
                    .build())
                    .rel("photo-eagerly").build() );

            // associated collections links with pattern: http://localhost:port/app/rest/{resources}/{id}/{relationship}

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
