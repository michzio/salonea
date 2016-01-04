package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.ServicePointPhotoFacade;
import pl.salonea.ejb.stateless.TagFacade;
import pl.salonea.ejb.stateless.VirtualTourFacade;
import pl.salonea.entities.ServicePointPhoto;
import pl.salonea.entities.Tag;
import pl.salonea.entities.VirtualTour;
import pl.salonea.jaxrs.bean_params.*;
import pl.salonea.jaxrs.exceptions.*;
import pl.salonea.jaxrs.exceptions.BadRequestException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.ServicePointPhotoWrapper;
import pl.salonea.jaxrs.wrappers.TagWrapper;
import pl.salonea.jaxrs.wrappers.VirtualTourWrapper;

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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by michzio on 27/11/2015.
 */
@Path("/tags")
public class TagResource {

    private static final Logger logger = Logger.getLogger(TagResource.class.getName());

    @Inject
    private TagFacade tagFacade;
    @Inject
    private ServicePointPhotoFacade servicePointPhotoFacade;
    @Inject
    private VirtualTourFacade virtualTourFacade;

    /**
     * Method returns all Tag resources
     * They can be additionally filtered or paginated by @QueryParams
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTags( @BeanParam TagBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Tags by executing TagResource.getTags() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<Tag> tags = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            // get tags filtered by given query params
            tags = new ResourceList<>(
                    tagFacade.findByMultipleCriteria(params.getTagNames(), params.getServicePointPhotos(), params.getVirtualTours(),
                            params.getOffset(), params.getLimit())
            );

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get tags without filtering (eventually paginated)
            tags = new ResourceList<>( tagFacade.findAll(params.getOffset(), params.getLimit()) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        TagResource.populateWithHATEOASLinks(tags, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(tags).build();
    }

    @GET
    @Path("/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTagsEagerly( @BeanParam TagBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Tags eagerly by executing TagResource.getTagsEagerly() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<TagWrapper> tags = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            // get tags eagerly filtered by given query params
            tags = new ResourceList<>(
                    TagWrapper.wrap(
                            tagFacade.findByMultipleCriteriaEagerly(params.getTagNames(), params.getServicePointPhotos(), params.getVirtualTours(),
                                    params.getOffset(), params.getLimit())
                    )
            );

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get tags eagerly without filtering (eventually paginated)
            tags = new ResourceList<>( TagWrapper.wrap(tagFacade.findAllEagerly(params.getOffset(), params.getLimit())) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        TagResource.populateWithHATEOASLinks(tags, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(tags).build();
    }

    /**
     * Method matches specific Tag resource by identifier and returns its instance
     */
    @GET
    @Path("/{tagId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTag( @PathParam("tagId") Long tagId,
                            @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning given Tag by executing TagResource.getTag(tagId) method of REST API");

        Tag foundTag = tagFacade.find(tagId);
        if(foundTag == null)
            throw new NotFoundException("Could not find tag for id " + tagId + ".");

        // adding hypermedia links to tag resource
        TagResource.populateWithHATEOASLinks(foundTag, params.getUriInfo());

        return Response.status(Status.OK).entity(foundTag).build();
    }

    /**
     * Method matches specific Tag resource by identifier and returns its instance fetching it eagerly
     */
    @GET
    @Path("/{tagId : \\d+}/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTagEagerly( @PathParam("tagId") Long tagId,
                                   @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning given Tag eagerly by executing TagResource.getTagEagerly(tagId) method of REST API");

        Tag foundTag = tagFacade.findByIdEagerly(tagId);
        if(foundTag == null)
            throw new NotFoundException("Could not find tag for id " + tagId + ".");

        // wrapping Tag into TagWrapper in order to marshall eagerly fetched associated collections of entities
        TagWrapper wrappedTag = new TagWrapper(foundTag);

        // adding hypermedia links to wrapped tag resource
        TagResource.populateWithHATEOASLinks(wrappedTag, params.getUriInfo());

        return Response.status(Status.OK).entity(wrappedTag).build();
    }

    /**
     * Method that takes Tag as XML or JSON and creates its new instance in database
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createTag( Tag tag,
                               @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "creating new Tag by executing TagResource.createTag(tag) method of REST API");

        Tag createdTag = null;
        URI locationURI = null;

        try {
            // persist new resource in database
            createdTag = tagFacade.create(tag);

            // populate created resource with hypermedia links
            TagResource.populateWithHATEOASLinks(createdTag, params.getUriInfo());

            // construct link to newly created resource to return in HTTP header
            String createdTagId = String.valueOf(createdTag.getTagId());
            locationURI = params.getUriInfo().getBaseUriBuilder().path(TagResource.class).path(createdTagId).build();

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_CREATION_ERROR_MESSAGE);
        }

        return Response.created(locationURI).entity(createdTag).build();
    }

    /**
     * Method that takes updated Tag as XML or JSON and its ID as path param.
     * It updates Tag in database for provided ID.
     */
    @PUT
    @Path("/{tagId : \\d+}") // catch only numeric identifiers
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateTag( @PathParam("tagId") Long tagId,
                               Tag tag,
                               @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "updating existing Tag by executing TagResource.updateTag(tagId, tag) method of REST API");

        // set resource ID passed in path param on updated resource object
        tag.setTagId(tagId);

        Tag updatedTag = null;
        try {
            // reflect updated resource object in database
            updatedTag = tagFacade.update(tag, true);
            // populate created resource with hypermedia links
            TagResource.populateWithHATEOASLinks(updatedTag, params.getUriInfo());

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_UPDATE_ERROR_MESSAGE);
        }

        return Response.status(Status.OK).entity(updatedTag).build();
    }

    /**
     * Method that removes Tag entity from database for given ID.
     * The ID is passed through path param.
     */
    @DELETE
    @Path("/{tagId : \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeTag( @PathParam("tagId") Long tagId,
                               @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing given Tag by executing TagResource.removeTag(tagId) method of REST API");

        // find Tag entity that should be deleted
        Tag toDeleteTag = tagFacade.find(tagId);
        // throw exception if entity hasn't been found
        if(toDeleteTag == null)
            throw new NotFoundException("Could not find tag to delete for given id " + tagId + ".");

        // remove entity from database
        tagFacade.remove(toDeleteTag);

        return Response.status(Status.NO_CONTENT).build();
    }

    /**
     * Additional methods returning a subset of resources based on given criteria.
     * You can also achieve similar results by applying @QueryParams to generic method
     * returning all resources in order to filter and limit them.
     */

    /**
     * Method returns number of Tag entities in database
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countTags( @BeanParam GenericBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning number of tags by executing TagResource.countTags() method of REST API");

        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(tagFacade.count()), 200, "number of tags");
        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method returns subset of Tag entities for given tag name.
     * The tag name is passed through path param.
     */
    @GET
    @Path("/named/{tagName : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTagsByTagName( @PathParam("tagName") String tagName,
                                      @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning tags for given tag name using TagResource.getTagsByTagName(tagName) method of REST API");

        // find tags by given criteria
        ResourceList<Tag> tags = new ResourceList<>( tagFacade.findByTagName(tagName, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        TagResource.populateWithHATEOASLinks(tags, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(tags).build();
    }

    /**
     * related subresources (through relationships)
     */

    @Path("/{tagId: \\d+}/service-point-photos")
    public ServicePointPhotoResource getServicePointPhotoResource() {
        return new ServicePointPhotoResource();
    }

    @Path("/{tagId: \\d+}/virtual-tours")
    public VirtualTourResource getVirtualTourResource() { return new VirtualTourResource(); }

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
            Method tagsEagerlyMethod = TagResource.class.getMethod("getTagsEagerly", TagBeanParam.class);
            tags.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(TagResource.class).path(tagsEagerlyMethod).build()).rel("tags-eagerly").build() );

            // get subset of resources hypermedia links
            // named
            tags.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(TagResource.class).path("named").build()).rel("named").build() );

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
        tag.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                .path(TagResource.class)
                .path(tag.getTagId().toString())
                .build())
                .rel("self").build() );

        // collection link with pattern: http://localhost:port/app/rest/{resources}
        tag.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                .path(TagResource.class)
                .build())
                .rel("tags").build() );

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

            // service-point-photos
            Method servicePointPhotosMethod = TagResource.class.getMethod("getServicePointPhotoResource");
            tag.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TagResource.class)
                    .path(servicePointPhotosMethod)
                    .resolveTemplate("tagId", tag.getTagId().toString())
                    .build())
                    .rel("service-point-photos").build());

            // service-point-photos eagerly
            Method servicePointPhotosEagerlyMethod = TagResource.ServicePointPhotoResource.class.getMethod("getTagServicePointPhotosEagerly", Long.class, ServicePointPhotoBeanParam.class);
            tag.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TagResource.class)
                    .path(servicePointPhotosMethod)
                    .path(servicePointPhotosEagerlyMethod)
                    .resolveTemplate("tagId", tag.getTagId().toString())
                    .build())
                    .rel("service-point-photos-eagerly").build());

            // service-point-photos count
            Method countServicePointPhotosByTagMethod = TagResource.ServicePointPhotoResource.class.getMethod("countServicePointPhotosByTag", Long.class, GenericBeanParam.class);
            tag.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TagResource.class)
                    .path(servicePointPhotosMethod)
                    .path(countServicePointPhotosByTagMethod)
                    .resolveTemplate("tagId", tag.getTagId().toString())
                    .build())
                    .rel("service-point-photos-count").build());

            // virtual-tours
            Method virtualToursMethod = TagResource.class.getMethod("getVirtualTourResource");
            tag.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TagResource.class)
                    .path(virtualToursMethod)
                    .resolveTemplate("tagId", tag.getTagId().toString())
                    .build())
                    .rel("virtual-tours").build());

            // virtual-tours eagerly
            Method virtualToursEagerlyMethod = TagResource.VirtualTourResource.class.getMethod("getTagVirtualToursEagerly", Long.class, VirtualTourBeanParam.class);
            tag.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TagResource.class)
                    .path(virtualToursMethod)
                    .path(virtualToursEagerlyMethod)
                    .resolveTemplate("tagId", tag.getTagId().toString())
                    .build())
                    .rel("virtual-tours-eagerly").build());

            // virtual-tours count
            Method countVirtualToursByTagMethod = TagResource.VirtualTourResource.class.getMethod("countVirtualToursByTag", Long.class, GenericBeanParam.class);
            tag.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TagResource.class)
                    .path(virtualToursMethod)
                    .path(countVirtualToursByTagMethod)
                    .resolveTemplate("tagId", tag.getTagId().toString())
                    .build())
                    .rel("virtual-tours-count").build());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public class ServicePointPhotoResource {

        public ServicePointPhotoResource() { }

        /**
         * Method returns subset of Service Point Photo entities for given Tag entity.
         * The tag id is passed through path param.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getTagServicePointPhotos( @PathParam("tagId") Long tagId,
                                                  @BeanParam ServicePointPhotoBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning service point photos for given tag using " +
                    "TagResource.ServicePointPhotoResource.getTagServicePointPhotos(tagId) method of REST API");

            // find tag entity for which to get associated service point photos
            Tag tag = tagFacade.find(tagId);
            if(tag == null)
                throw new NotFoundException("Could not find tag for id " + tagId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ServicePointPhoto> photos = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Tag> tags = new ArrayList<>();
                tags.add(tag);

                // get service point photos for given tag filtered by given params

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getFileNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                        throw new BadRequestException("Query params cannot include keywords and fileNames or descriptions at the same time.");

                    if( RESTToolkit.isSet(params.getTagNames()) ) {
                        // find by keywords and tag names
                        photos = new ResourceList<>(
                                servicePointPhotoFacade.findByMultipleCriteria(params.getKeywords(), params.getTagNames(), params.getServicePoints(),
                                        params.getProviders(), params.getCorporations(), tags, params.getOffset(), params.getLimit())
                        );
                    } else {
                        // find only by keywords
                        photos = new ResourceList<>(
                                servicePointPhotoFacade.findByMultipleCriteria(params.getKeywords(), params.getServicePoints(), params.getProviders(),
                                        params.getCorporations(), tags, params.getOffset(), params.getLimit())
                        );
                    }
                } else {
                    // find by fileNames, descriptions or tagNames
                    photos = new ResourceList<>(
                            servicePointPhotoFacade.findByMultipleCriteria(params.getFileNames(), params.getDescriptions(), params.getTagNames(),
                                    params.getServicePoints(), params.getProviders(), params.getCorporations(), tags, params.getOffset(), params.getLimit())
                    );
                }
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get service point photos for given tag without filtering
                photos = new ResourceList<>(servicePointPhotoFacade.findByTag(tag, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointPhotoResource.populateWithHATEOASLinks(photos, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(photos).build();
        }

        /**
         * Method returns subset of Service Point Photo entities for given Tag fetching them eagerly
         * The tag id is passed through path param.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getTagServicePointPhotosEagerly( @PathParam("tagId") Long tagId,
                                                         @BeanParam ServicePointPhotoBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning service point photos eagerly for given tag using " +
                    "TagResource.ServicePointPhotoResource.getTagServicePointPhotosEagerly(tagId) method of REST API");

            // find tag entity for which to get associated service point photos
            Tag tag = tagFacade.find(tagId);
            if(tag == null)
                throw new NotFoundException("Could not find tag for id " + tagId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ServicePointPhotoWrapper> photos = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Tag> tags = new ArrayList<>();
                tags.add(tag);

                // get service point photos eagerly for given tag filtered by given params

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getFileNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                        throw new BadRequestException("Query params cannot include keywords and fileNames or descriptions at the same time.");

                    if( RESTToolkit.isSet(params.getTagNames()) ) {
                        // find by keywords and tag names
                        photos = new ResourceList<>(
                                ServicePointPhotoWrapper.wrap(
                                        servicePointPhotoFacade.findByMultipleCriteriaEagerly(params.getKeywords(), params.getTagNames(), params.getServicePoints(),
                                                params.getProviders(), params.getCorporations(), tags, params.getOffset(), params.getLimit())
                                )
                        );
                    } else {
                        // find only by keywords
                        photos = new ResourceList<>(
                                ServicePointPhotoWrapper.wrap(
                                        servicePointPhotoFacade.findByMultipleCriteriaEagerly(params.getKeywords(), params.getServicePoints(), params.getProviders(),
                                                params.getCorporations(), tags, params.getOffset(), params.getLimit())
                                )
                        );
                    }
                } else {
                    // find by fileNames, descriptions or tagNames
                    photos = new ResourceList<>(
                            ServicePointPhotoWrapper.wrap(
                                    servicePointPhotoFacade.findByMultipleCriteriaEagerly(params.getFileNames(), params.getDescriptions(), params.getTagNames(),
                                            params.getServicePoints(), params.getProviders(), params.getCorporations(), tags, params.getOffset(), params.getLimit())
                            )
                    );
                }
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get service point photos eagerly for given tag without filtering
                photos = new ResourceList<>( ServicePointPhotoWrapper.wrap(servicePointPhotoFacade.findByTagEagerly(tag, params.getOffset(), params.getLimit())) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointPhotoResource.populateWithHATEOASLinks(photos, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(photos).build();
        }

        /**
         * Method that count Service Point Photo entities for given Tag resource.
         * The tag id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countServicePointPhotosByTag( @PathParam("tagId") Long tagId,
                                                      @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of service point photos for given tag by executing " +
                    "TagResource.ServicePointPhotoResource.countServicePointPhotosByTag(tagId) method of REST API");

            // find tag entity for which to count service point photos
            Tag tag = tagFacade.find(tagId);
            if(tag == null)
                throw new NotFoundException("Could not find tag for id " + tagId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(servicePointPhotoFacade.countByTag(tag)), 200, "number of service point photos for tag with id " + tag.getTagId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    public class VirtualTourResource {

        public VirtualTourResource() { }

        /**
         * Method returns subset of Virtual Tour entities for given Tag entity
         * The tag id is passed through path param.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getTagVirtualTours( @PathParam("tagId") Long tagId,
                                            @BeanParam VirtualTourBeanParam params ) {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning virtual tours for given tag using " +
                    "TagResource.VirtualTourResource.getTagVirtualTours(tagId) method of REST API");

            // find tag entity for which to get associated virtual tours
            Tag tag = tagFacade.find(tagId);
            if(tag == null)
                throw new NotFoundException("Could not find tag for id " + tagId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<VirtualTour> virtualTours = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Tag> tags = new ArrayList<>();
                tags.add(tag);

                // get virtual tours for given tag filtered by given params

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getFileNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                        throw new BadRequestException("Query params cannot include keywords and fileNames or descriptions at the same time.");

                    if( RESTToolkit.isSet(params.getTagNames()) ) {
                        // find by keywords and tag names
                        virtualTours = new ResourceList<>(
                                virtualTourFacade.findByMultipleCriteria(params.getKeywords(), params.getTagNames(), params.getServicePoints(),
                                        params.getProviders(), params.getCorporations(), tags, params.getOffset(), params.getLimit())
                        );
                    } else {
                        // find only by keywords
                        virtualTours = new ResourceList<>(
                                virtualTourFacade.findByMultipleCriteria(params.getKeywords(), params.getServicePoints(), params.getProviders(),
                                        params.getCorporations(), tags, params.getOffset(), params.getLimit())
                        );
                    }
                } else {
                    // find by fileNames, descriptions or tagNames
                    virtualTours = new ResourceList<>(
                            virtualTourFacade.findByMultipleCriteria(params.getFileNames(), params.getDescriptions(), params.getTagNames(),
                                    params.getServicePoints(), params.getProviders(), params.getCorporations(), tags, params.getOffset(), params.getLimit())
                    );
                }
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get virtual tours for given tag without filtering
                virtualTours = new ResourceList<>( virtualTourFacade.findByTag(tag, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.VirtualTourResource.populateWithHATEOASLinks(virtualTours, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(virtualTours).build();
        }

        /**
         * Method returns subset of Virtual Tour entities for given Tag fetching them eagerly
         * The tag id is passed through path param.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getTagVirtualToursEagerly( @PathParam("tagId") Long tagId,
                                                   @BeanParam VirtualTourBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning virtual tours eagerly for given tag using " +
                    "TagResource.VirtualTourResource.getTagVirtualToursEagerly(tagId) method of REST API");

            // find tag entity for which to get associated virtual tours
            Tag tag = tagFacade.find(tagId);
            if(tag == null)
                throw new NotFoundException("Could not find tag for id " + tagId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<VirtualTourWrapper> virtualTours = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Tag> tags = new ArrayList<>();
                tags.add(tag);

                // get virtual tours eagerly for given tag filtered by given params

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getFileNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                        throw new BadRequestException("Query params cannot include keywords and fileNames or descriptions at the same time.");

                    if( RESTToolkit.isSet(params.getTagNames()) ) {
                        // find by keywords and tag names
                        virtualTours = new ResourceList<>(
                                VirtualTourWrapper.wrap(
                                        virtualTourFacade.findByMultipleCriteriaEagerly(params.getKeywords(), params.getTagNames(), params.getServicePoints(),
                                                params.getProviders(), params.getCorporations(), tags, params.getOffset(), params.getLimit())
                                )
                        );
                    } else {
                        // find only by keywords
                        virtualTours = new ResourceList<>(
                                VirtualTourWrapper.wrap(
                                        virtualTourFacade.findByMultipleCriteriaEagerly(params.getKeywords(), params.getServicePoints(), params.getProviders(),
                                                params.getCorporations(), tags, params.getOffset(), params.getLimit())
                                )
                        );
                    }
                } else {
                    // find by fileNames, descriptions or tagNames
                    virtualTours = new ResourceList<>(
                            VirtualTourWrapper.wrap(
                                    virtualTourFacade.findByMultipleCriteriaEagerly(params.getFileNames(), params.getDescriptions(), params.getTagNames(),
                                            params.getServicePoints(), params.getProviders(), params.getCorporations(), tags, params.getOffset(), params.getLimit())
                            )
                    );
                }
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get virtual tours eagerly for given tag without filtering (eventually paginated)
                virtualTours = new ResourceList<>( VirtualTourWrapper.wrap(virtualTourFacade.findByTagEagerly(tag, params.getOffset(), params.getLimit())) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.VirtualTourResource.populateWithHATEOASLinks(virtualTours, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(virtualTours).build();
        }

        /**
         * Method that count Virtual Tour entities for given Tag resource.
         * The tag id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countVirtualToursByTag( @PathParam("tagId") Long tagId,
                                                @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of virtual tours for given tag by executing " +
                    "TagResource.VirtualTourResource.countVirtualToursByTag(tagId) method of REST API");

            // find tag entity for which to count virtual tours
            Tag tag = tagFacade.find(tagId);
            if (tag == null)
                throw new NotFoundException("Could not find tag for id " + tagId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(virtualTourFacade.countByTag(tag)), 200,
                    "number of virtual tours for tag with id " + tag.getTagId());

            return Response.status(Status.OK).entity(responseEntity).build();
        }

    }
}