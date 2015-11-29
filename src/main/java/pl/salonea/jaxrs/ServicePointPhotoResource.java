package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.ServicePointPhotoFacade;
import pl.salonea.entities.ServicePoint;
import pl.salonea.entities.ServicePointPhoto;
import pl.salonea.entities.Tag;
import pl.salonea.entities.idclass.ServicePointId;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.bean_params.PaginationBeanParam;
import pl.salonea.jaxrs.bean_params.ServicePointPhotoBeanParam;
import pl.salonea.jaxrs.exceptions.*;
import pl.salonea.jaxrs.exceptions.BadRequestException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.ServicePointPhotoWrapper;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
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

            if( RESTToolkit.isSet(params.getKeywords()) ) {
                if( RESTToolkit.isSet(params.getFileNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                    throw new BadRequestException("Query params cannot include keywords and fileNames or descriptions at the same time.");

                if( RESTToolkit.isSet(params.getTagNames()) ) {
                    // find by keywords and tag names
                    photos = new ResourceList<>(
                            servicePointPhotoFacade.findByMultipleCriteria(params.getKeywords(), params.getTagNames(), params.getServicePoints(),
                                    params.getProviders(), params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                    );
                } else {
                    // find only by keywords
                    photos = new ResourceList<>(
                            servicePointPhotoFacade.findByMultipleCriteria(params.getKeywords(), params.getServicePoints(), params.getProviders(),
                                    params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                    );
                }
            } else {
                // find by fileNames, descriptions or tagNames
                photos = new ResourceList<>(
                        servicePointPhotoFacade.findByMultipleCriteria(params.getFileNames(), params.getDescriptions(), params.getTagNames(),
                                params.getServicePoints(), params.getProviders(), params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
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

    @GET
    @Path("/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPhotosEagerly( @BeanParam ServicePointPhotoBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Service Point Photos eagerly by executing ServicePointPhotoResource.getPhotosEagerly() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<ServicePointPhotoWrapper> photos = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            if( RESTToolkit.isSet(params.getKeywords()) ) {
                if ( RESTToolkit.isSet(params.getFileNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                    throw new BadRequestException("Query params cannot include keywords and fileNames or descriptions at the same time.");

                if( RESTToolkit.isSet(params.getTagNames()) ) {
                    // find by keywords and tag names
                    photos = new ResourceList<>(
                            ServicePointPhotoWrapper.wrap(
                                    servicePointPhotoFacade.findByMultipleCriteriaEagerly(params.getKeywords(), params.getTagNames(), params.getServicePoints(),
                                            params.getProviders(), params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                            )
                    );
                } else {
                    // find only by keywords
                    photos = new ResourceList<>(
                            ServicePointPhotoWrapper.wrap(
                                    servicePointPhotoFacade.findByMultipleCriteriaEagerly(params.getKeywords(), params.getServicePoints(), params.getProviders(),
                                            params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                            )
                    );
                }
            } else {
                // find by fileNames, descriptions or tagNames
                photos = new ResourceList<>(
                        ServicePointPhotoWrapper.wrap(
                                servicePointPhotoFacade.findByMultipleCriteriaEagerly(params.getFileNames(), params.getDescriptions(), params.getTagNames(),
                                        params.getServicePoints(), params.getProviders(), params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                        )
                );
            }
        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all photos eagerly without filtering (eventually paginated)
            photos = new ResourceList<>( ServicePointPhotoWrapper.wrap(servicePointPhotoFacade.findAllEagerly(params.getOffset(), params.getLimit())) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        ServicePointPhotoResource.populateWithHATEOASLinks(photos, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(photos).build();
    }

    /**
     * Method matches specific Service Point Photo resource by identifier and returns its instance
     */
    @GET
    @Path("/{photoId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPhoto( @PathParam("photoId") Long photoId,
                              @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning given Service Point Photo by executing ServicePointPhotoResource.getPhoto(photoId) method of REST API");

        ServicePointPhoto foundPhoto = servicePointPhotoFacade.find(photoId);
        if(foundPhoto == null)
            throw new NotFoundException("Could not find photo for id " + photoId + ".");

        // adding hypermedia link to photo resource
        ServicePointPhotoResource.populateWithHATEOASLinks(foundPhoto, params.getUriInfo());

        return Response.status(Status.OK).entity(foundPhoto).build();
    }

    /**
     * Method matches specific Service Point Photo resource by identifier and returns its instance fetching it eagerly
     */
    @GET
    @Path("/{photoId : \\d+}/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPhotoEagerly( @PathParam("photoId") Long photoId,
                                     @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning given Service Point Photo eagerly by executing ServicePointPhotoResource.getPhotoEagerly(photoId) method of REST API");

        ServicePointPhoto foundPhoto = servicePointPhotoFacade.findByIdEagerly(photoId);
        if(foundPhoto == null)
            throw new NotFoundException("Could not find photo for id " + photoId + ".");

        // wrapping ServicePointPhoto into ServicePointPhotoWrapper in order to marshal eagerly fetched associated collection of entities
        ServicePointPhotoWrapper wrappedPhoto = new ServicePointPhotoWrapper(foundPhoto);

        // adding hypermedia links to wrapped photo resource
        ServicePointPhotoResource.populateWithHATEOASLinks(wrappedPhoto, params.getUriInfo());

        return Response.status(Status.OK).entity(wrappedPhoto).build();
    }

    /**
     * Method that takes Service Point Photo as XML or JSON and creates its new instance in database
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createPhoto( ServicePointPhoto photo,
                                 @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "creating new Service Point Photo by executing ServicePointPhotoResource.createPhoto(photo) method of REST API");

        ServicePointPhoto createdPhoto = null;
        URI locationURI = null;

        ServicePoint servicePoint = photo.getServicePoint();
        if(servicePoint == null)
            throw new UnprocessableEntityException("Photo cannot be inserted without Service Point set on it.");

        Long providerId = servicePoint.getProvider().getUserId();
        Integer servicePointNumber = servicePoint.getServicePointNumber();

        try {
            // persist new resource in database
            createdPhoto = servicePointPhotoFacade.createForServicePoint(new ServicePointId(providerId, servicePointNumber), photo);

            // populate created resource with hypermedia links
            ServicePointPhotoResource.populateWithHATEOASLinks(createdPhoto, params.getUriInfo());

            // construct link to newly created resource to return in HTTP header
            String createdPhotoId = String.valueOf(createdPhoto.getPhotoId());
            locationURI = params.getUriInfo().getBaseUriBuilder().path(ServicePointPhotoResource.class).path(createdPhotoId).build();

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_CREATION_ERROR_MESSAGE);
        }

        return Response.created(locationURI).entity(createdPhoto).build();
    }

    /**
     * Method that takes updated Service Point Photo as XML or JSON and its ID as path param.
     * It updates Service Point Photo in database for provided ID.
     */
    @PUT
    @Path("/{photoId : \\d+}") // catch only numeric identifiers
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updatePhoto( @PathParam("photoId") Long photoId,
                                 ServicePointPhoto photo,
                                 @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "updating existing Service Point Photo by executing ServicePointPhotoResource.updatePhoto(photoId, photo) method of REST API");

        // set resource ID passed in path param on updated resource object
        photo.setPhotoId(photoId);

        ServicePoint servicePoint = photo.getServicePoint();
        if(servicePoint == null)
            throw new UnprocessableEntityException("Photo cannot be updated without Service Point set on it.");

        Long providerId = servicePoint.getProvider().getUserId();
        Integer servicePointNumber = servicePoint.getServicePointNumber();

        ServicePointPhoto updatedPhoto = null;
        try {
            // reflect updated resource object in database
            updatedPhoto = servicePointPhotoFacade.updateWithServicePoint(new ServicePointId(providerId, servicePointNumber), photo);
            // populate created resource with hypermedia links
            ServicePointPhotoResource.populateWithHATEOASLinks(updatedPhoto, params.getUriInfo());

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_UPDATE_ERROR_MESSAGE);
        }

        return Response.status(Status.OK).entity(updatedPhoto).build();
    }

    /**
     * Method that removes Service Point Photo entity from database for given ID.
     * The ID is passed through path param.
     */
    @DELETE
    @Path("/{photoId : \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removePhoto( @PathParam("photoId") Long photoId,
                                 @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing given Service Point Photo by executing ServicePointPhotoResource.removePhoto(photoId) method of REST API");

        // remove entity from database
        Integer noOfDeleted = servicePointPhotoFacade.deleteById(photoId);

        if(noOfDeleted == 0)
            throw new NotFoundException("Could not find photo to delete for id " + photoId + ".");
        else if(noOfDeleted != 1)
            throw new InternalServerErrorException("Some error occurred while trying to delete photo with id " + photoId + ".");

        return Response.status(Status.NO_CONTENT).build();
    }

    /**
     * Additional methods returning a subset of resources based on given criteria
     * You can also achieve similar results by applying @QueryParams to generic method
     * returning all resources in order to filter and limit them
     */

    /**
     * Method returns number of Service Point Photo entities in database
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countPhotos( @BeanParam GenericBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning number of (service point) photos by executing ServicePointPhotoResource.countPhotos() method of REST API");

        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(servicePointPhotoFacade.count()), 200, "number of photos");
        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method returns subset of Service Point Photo entities for given file name.
     * The file name is passed through path param.
     */
    @GET
    @Path("/file-named/{fileName : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPhotosByFileName( @PathParam("fileName") String fileName,
                                         @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning service point photos for given file name using ServicePointPhotoResource.getPhotosByFileName(fileName) method of REST API");

        // find service point photos by given criteria
        ResourceList<ServicePointPhoto> photos = new ResourceList<>( servicePointPhotoFacade.findByFileName(fileName, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        ServicePointPhotoResource.populateWithHATEOASLinks(photos, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(photos).build();
    }

    /**
     * Method returns subset of Service Point Photo entities for given description.
     * The description is passed through path param.
     */
    @GET
    @Path("/described/{description : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPhotosByDescription( @PathParam("description") String description,
                                            @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning service point photos for given description using ServicePointPhotoResource.getPhotosByDescription(description) method of REST API");

        // find service point photos by given criteria
        ResourceList<ServicePointPhoto> photos = new ResourceList<>( servicePointPhotoFacade.findByDescription(description, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        ServicePointPhotoResource.populateWithHATEOASLinks(photos, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(photos).build();
    }

    /**
     * Method returns subset of Service Point Photo entities for given keyword.
     * The keyword is passed through path param.
     */
    @GET
    @Path("/containing-keyword/{keyword : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPhotosByKeyword( @PathParam("keyword") String keyword,
                                        @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning service point photos for given keyword using ServicePointPhotoResource.getPhotosByKeyword(keyword) method of REST API");

        // find service point photos by given criteria
        ResourceList<ServicePointPhoto> photos = new ResourceList<>( servicePointPhotoFacade.findByKeywordIncludingTags(keyword, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        ServicePointPhotoResource.populateWithHATEOASLinks(photos, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(photos).build();
    }

    /**
     * Method returns subset of Service Point Photo entities for given tag name.
     * The tag name is passed through path param.
     */
    @GET
    @Path("/tagged/{tagName : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPhotosByTagName( @PathParam("tagName") String tagName,
                                        @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning service point photos for given tag name using ServicePointPhotoResource.getPhotosByTagName(tagName) method of REST API");

        // find service point photos by given criteria
        ResourceList<ServicePointPhoto> photos = new ResourceList<>( servicePointPhotoFacade.findByTagName(tagName, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        ServicePointPhotoResource.populateWithHATEOASLinks(photos, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(photos).build();
    }

    /**
     * Method returns subset of Service Point Photo entities for all given tag names.
     * The tag names are passed through query params.
     */
    @GET
    @Path("/tagged")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPhotosByAllTagNames( @QueryParam("tagName") List<String> tagNames,
                                            @BeanParam PaginationBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning service point photos for all given tag names using ServicePointPhotoResource.getPhotosByAllTagNames(tagName) method of REST API");

        if(tagNames.size() < 1)
            throw new BadRequestException("There must be specified at least one tag name.");

        // find service point photos by given criteria
        ResourceList<ServicePointPhoto> photos = new ResourceList<>( servicePointPhotoFacade.findByAllTagNames(tagNames, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        ServicePointPhotoResource.populateWithHATEOASLinks(photos, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(photos).build();
    }

    /**
     * Method returns subset of Service Point Photo entities for any given tag names.
     * The tag names are passed through query params.
     */
    @GET
    @Path("/tagged-any")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPhotosByAnyTagNames( @QueryParam("tagName") List<String> tagNames,
                                            @BeanParam PaginationBeanParam params )  throws ForbiddenException, BadRequestException  {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning service point photos for any given tag names using ServicePointPhotoResource.getPhotosByAnyTagNames(tagName) method of REST API");

        if(tagNames.size() < 1)
            throw new BadRequestException("There must be specified at least one tag name.");

        // find service point photos by given criteria
        ResourceList<ServicePointPhoto> photos = new ResourceList<>( servicePointPhotoFacade.findByAnyTagNames(tagNames, params.getOffset(), params.getLimit()) );

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
            Method photosEagerlyMethod = ServicePointPhotoResource.class.getMethod("getPhotosEagerly", ServicePointPhotoBeanParam.class);
            photos.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServicePointPhotoResource.class)
                    .path(photosEagerlyMethod)
                    .build()).rel("photos-eagerly").build() );

            // get subset of resources hypermedia links

            // file-named
            photos.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServicePointPhotoResource.class)
                    .path("file-named")
                    .build())
                    .rel("file-named").build() );

            // described
            photos.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServicePointPhotoResource.class)
                    .path("described")
                    .build())
                    .rel("described").build() );

            // containing-keyword
            photos.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServicePointPhotoResource.class)
                    .path("containing-keyword")
                    .build())
                    .rel("containing-keyword").build() );

            // tagged
            photos.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServicePointPhotoResource.class)
                    .path("tagged")
                    .build())
                    .rel("tagged").build() );

            // tagged-any
            photos.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServicePointPhotoResource.class)
                    .path("tagged-any")
                    .build())
                    .rel("tagged-any").build() );

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
