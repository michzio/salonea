package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.VirtualTourFacade;
import pl.salonea.entities.ServicePoint;
import pl.salonea.entities.Tag;
import pl.salonea.entities.VirtualTour;
import pl.salonea.entities.idclass.ServicePointId;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.bean_params.PaginationBeanParam;
import pl.salonea.jaxrs.bean_params.VirtualTourBeanParam;
import pl.salonea.jaxrs.exceptions.*;
import pl.salonea.jaxrs.exceptions.BadRequestException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.VirtualTourWrapper;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.transaction.*;
import javax.transaction.NotSupportedException;
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
 * Created by michzio on 27/11/2015.
 */
@Path("/virtual-tours")
public class VirtualTourResource {

    private static final Logger logger = Logger.getLogger(VirtualTourResource.class.getName());

    @Inject
    private UserTransaction utx;

    @Inject
    private VirtualTourFacade virtualTourFacade;

    /**
     * Method returns all Virtual Tour resources
     * They can be additionally filtered or paginated by @QueryParams
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getVirtualTours( @BeanParam VirtualTourBeanParam params ) throws ForbiddenException, BadRequestException,
    /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Virtual Tours by executing VirtualTourResource.getVirtualTours() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<VirtualTour> virtualTours = null;

        utx.begin();

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            // get all virtualTours filtered by given query params

            if( RESTToolkit.isSet(params.getKeywords()) ) {
                if( RESTToolkit.isSet(params.getFileNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                    throw new BadRequestException("Query params cannot include keywords and fileNames or descriptions at the same time.");

                if( RESTToolkit.isSet(params.getTagNames()) ) {
                    // find by keywords and tag names
                    virtualTours = new ResourceList<>(
                            virtualTourFacade.findByMultipleCriteria(params.getKeywords(), params.getTagNames(), params.getServicePoints(),
                                    params.getProviders(), params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                    );
                } else {
                    // find only by keywords
                    virtualTours = new ResourceList<>(
                            virtualTourFacade.findByMultipleCriteria(params.getKeywords(), params.getServicePoints(), params.getProviders(),
                                    params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                    );
                }
            } else {
                // find by fileNames, descriptions or tagNames
                virtualTours = new ResourceList<>(
                        virtualTourFacade.findByMultipleCriteria(params.getFileNames(), params.getDescriptions(), params.getTagNames(),
                                params.getServicePoints(), params.getProviders(), params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                );
            }
        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all virtualTours without filtering (eventually paginated)
            virtualTours = new ResourceList<>( virtualTourFacade.findAll(params.getOffset(), params.getLimit()) );

        }

        utx.commit();

        // result resources need to be populated with hypermedia links to enable resource discovery
        VirtualTourResource.populateWithHATEOASLinks(virtualTours, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(virtualTours).build();
    }

    @GET
    @Path("/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getVirtualToursEagerly( @BeanParam VirtualTourBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Virtual Tours eagerly by executing VirtualTourResource.getVirtualToursEagerly() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<VirtualTourWrapper> virtualTours = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            // get all virtualTours eagerly filtered by given query params

            if( RESTToolkit.isSet(params.getKeywords()) ) {
                if ( RESTToolkit.isSet(params.getFileNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                    throw new BadRequestException("Query params cannot include keywords and fileNames or descriptions at the same time.");

                if( RESTToolkit.isSet(params.getTagNames()) ) {
                    // find by keywords and tag names
                    virtualTours = new ResourceList<>(
                            VirtualTourWrapper.wrap(
                                    virtualTourFacade.findByMultipleCriteriaEagerly(params.getKeywords(), params.getTagNames(), params.getServicePoints(),
                                            params.getProviders(), params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                            )
                    );
                } else {
                    // find only by keywords
                    virtualTours = new ResourceList<>(
                            VirtualTourWrapper.wrap(
                                    virtualTourFacade.findByMultipleCriteriaEagerly(params.getKeywords(), params.getServicePoints(), params.getProviders(),
                                            params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                            )
                    );
                }
            } else {
                // find by fileNames, descriptions or tagNames
                virtualTours = new ResourceList<>(
                        VirtualTourWrapper.wrap(
                                virtualTourFacade.findByMultipleCriteriaEagerly(params.getFileNames(), params.getDescriptions(), params.getTagNames(),
                                        params.getServicePoints(), params.getProviders(), params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                        )
                );
            }
        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all virtualTours without filtering (eventually paginated)
            virtualTours = new ResourceList<>( VirtualTourWrapper.wrap(virtualTourFacade.findAllEagerly(params.getOffset(), params.getLimit())) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        VirtualTourResource.populateWithHATEOASLinks(virtualTours, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(virtualTours).build();
    }

    /**
     * Method matches specific Virtual Tour resource by identifier and returns its instance
     */
    @GET
    @Path("/{tourId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getVirtualTour( @PathParam("tourId") Long tourId,
                                    @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning given Virtual Tour by executing VirtualTourResource.getVirtualTour(tourId) method of REST API");

        VirtualTour foundVirtualTour = virtualTourFacade.find(tourId);
        if(foundVirtualTour == null)
            throw new NotFoundException("Could not find virtual tour for id " + tourId + ".");

        // adding hypermedia links to virtual tour resource
        VirtualTourResource.populateWithHATEOASLinks(foundVirtualTour, params.getUriInfo());

        return Response.status(Status.OK).entity(foundVirtualTour).build();
    }

    /**
     * Method matches specific Virtual Tour resource by identifier and returns its instance fetching it eagerly
     */
    @GET
    @Path("/{tourId : \\d+}/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getVirtualTourEagerly( @PathParam("tourId") Long tourId,
                                           @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning given Virtual Tour eagerly by executing VirtualTourResource.getVirtualTourEagerly(tourId) method of REST API");

        VirtualTour foundVirtualTour = virtualTourFacade.findByIdEagerly(tourId);
        if(foundVirtualTour == null)
            throw new NotFoundException("Could not find virtual tour for id " + tourId + ".");

        // wrapping VirtualTour into VirtualTourWrapper in order to marshal eagerly fetched associated collection of entities
        VirtualTourWrapper wrappedVirtualTour = new VirtualTourWrapper(foundVirtualTour);

        // adding hypermedia links to wrapped virtual tour resource
        VirtualTourResource.populateWithHATEOASLinks(wrappedVirtualTour, params.getUriInfo());

        return Response.status(Status.OK).entity(wrappedVirtualTour).build();
    }

    /**
     * Method that takes Virtual Tour as XML or JSON and creates its new instance in database
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createVirtualTour( VirtualTour virtualTour,
                                       @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "creating new Virtual Tour by executing VirtualTourResource.createVirtualTour(virtualTour) method of REST API");

        VirtualTour createdVirtualTour = null;
        URI locationURI = null;

        ServicePoint servicePoint = virtualTour.getServicePoint();
        if(servicePoint == null)
            throw new UnprocessableEntityException("Virtual Tour cannot be inserted without Virtual Tour set on it.");

        Long providerId = servicePoint.getProvider().getUserId();
        Integer servicePointNumber = servicePoint.getServicePointNumber();

        try {
            // persist new resource in database
            createdVirtualTour = virtualTourFacade.createForServicePoint(new ServicePointId(providerId, servicePointNumber), virtualTour);

            // populated created resource with hypermedia links
            VirtualTourResource.populateWithHATEOASLinks(createdVirtualTour, params.getUriInfo());

            // construct link to newly created resource to return in HTTP header
            String createdVirtualTourId = String.valueOf(createdVirtualTour.getTourId());
            locationURI = params.getUriInfo().getBaseUriBuilder().path(VirtualTourResource.class).path(createdVirtualTourId).build();

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_CREATION_ERROR_MESSAGE);
        }

        return Response.created(locationURI).entity(createdVirtualTour).build();
    }

    /**
     * Method that takes updated Virtual Tour as XML or JSON and its ID as path param.
     * It updates Virtual Tour in database for provided ID.
     */
    @PUT
    @Path("/{tourId : \\d+}") // catch only numeric identifiers
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateVirtualTour( @PathParam("tourId") Long tourId,
                                       VirtualTour virtualTour,
                                       @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "updating existing Virtual Tour by executing VirtualTourResource.updateVirtualTour(tourId, virtualTour) method of REST API");

        // set resource ID passed in path param on updated resource object
        virtualTour.setTourId(tourId);

        ServicePoint servicePoint = virtualTour.getServicePoint();
        if(servicePoint == null)
            throw new UnprocessableEntityException("Virtual Tour cannot be updated without Service Point set on it.");

        Long providerId = servicePoint.getProvider().getUserId();
        Integer servicePointNumber = servicePoint.getServicePointNumber();

        VirtualTour updatedVirtualTour = null;
        try {
            // reflect updated resource object in database
            updatedVirtualTour = virtualTourFacade.updateWithServicePoint(new ServicePointId(providerId, servicePointNumber), virtualTour);
            // populate created resource with hypermedia links
            VirtualTourResource.populateWithHATEOASLinks(updatedVirtualTour, params.getUriInfo());

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_UPDATE_ERROR_MESSAGE);
        }

        return Response.status(Status.OK).entity(updatedVirtualTour).build();
    }

    /**
     * Method that removes Virtual Tour entity from database for given ID.
     * The ID is passed through path param.
     */
    @DELETE
    @Path("/{tourId : \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeVirtualTour( @PathParam("tourId") Long tourId,
                                       @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing given Virtual Tour by executing VirtualTourResource.removeVirtualTour(tourId) method of REST API");

        // remove entity from database
        Integer noOfDeleted = virtualTourFacade.deleteById(tourId);

        if(noOfDeleted == 0)
            throw new NotFoundException("Could not find virtual tour to delete for id " + tourId + ".");
        else if(noOfDeleted != 1)
            throw new InternalServerErrorException("Some error occurred while trying to delete virtual tour with id " + tourId + ".");

        return Response.status(Status.NO_CONTENT).build();
    }

    /**
     * Additional methods returning a subset of resources based on given criteria
     * You can also achieve similar results by applying @QueryParams to generic method
     * returning all resources in order to filter and limit them
     */

    /**
     * Method returns number of Virtual Tour entities in database
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countVirtualTours( @BeanParam GenericBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning number of virtual tours by executing VirtualTourResource.countVirtualTours() method of REST API");

        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(virtualTourFacade.count()), 200, "number of virtual tours");
        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method returns subset of Virtual Tour entities for given file name
     * The file name is passed through path param.
     */
    @GET
    @Path("/file-named/{fileName : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getVirtualToursByFileName( @PathParam("fileName") String fileName,
                                               @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning virtual tours for given file name using VirtualTourResource.getVirtualToursByFileName(fileName) method of REST API");

        // find virtual tours by given criteria
        ResourceList<VirtualTour> virtualTours = new ResourceList<>( virtualTourFacade.findByFileName(fileName, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        VirtualTourResource.populateWithHATEOASLinks(virtualTours, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(virtualTours).build();
    }

    /**
     * Method returns subset of Virtual Tours entities for given description.
     * The description is passed through path param.
     */
    @GET
    @Path("/described/{description : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getVirtualToursByDescription( @PathParam("description") String description,
                                                  @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning virtual tours for given description using VirtualTourResource.getVirtualToursByDescription(description) method of REST API");

        // find virtual tours by given criteria
        ResourceList<VirtualTour> virtualTours = new ResourceList<>( virtualTourFacade.findByDescription(description, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        VirtualTourResource.populateWithHATEOASLinks(virtualTours, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(virtualTours).build();
    }

    /**
     * Method returns subset of Virtual Tour entities for given keyword.
     * The keyword is passed through path param.
     */
    @GET
    @Path("/containing-keyword/{keyword : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getVirtualToursByKeyword( @PathParam("keyword") String keyword,
                                              @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning virtual tours for given keyword using VirtualTourResource.getVirtualToursByKeyword(keyword) method of REST API");

        // find virtual tours by given criteria
        ResourceList<VirtualTour> virtualTours = new ResourceList<>( virtualTourFacade.findByKeywordIncludingTags(keyword, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        VirtualTourResource.populateWithHATEOASLinks(virtualTours, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(virtualTours).build();
    }

    /**
     * Method returns subset of Virtual Tour entities for given tag name.
     * The tag name is passed through path param.
     */
    @GET
    @Path("/tagged/{tagName : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getVirtualToursByTagName( @PathParam("tagName") String tagName,
                                              @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning virtual tours for given tag name using VirtualTourResource.getVirtualToursByTagName(tagName) method of REST API");

        // find virtual tours by given criteria
        ResourceList<VirtualTour> virtualTours = new ResourceList<>( virtualTourFacade.findByTagName(tagName, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        VirtualTourResource.populateWithHATEOASLinks(virtualTours, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(virtualTours).build();
    }

    /**
     * Method returns subset of Virtual Tour entities for all given tag names.
     * The tag names are passed through query params.
     */
    @GET
    @Path("/tagged")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getVirtualToursByAllTagNames( @QueryParam("tagName") List<String> tagNames,
                                                  @BeanParam PaginationBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning virtual tours for all given tag names using VirtualTourResource.getVirtualToursByAllTagNames(tagNames) method of REST API");

        if(tagNames.size() < 1)
            throw new BadRequestException("There must be specified at least one tag name.");

        // find virtual tours by given criteria
        ResourceList<VirtualTour> virtualTours = new ResourceList<>( virtualTourFacade.findByAllTagNames(tagNames, params.getOffset(), params.getLimit()) );

        // result resource need to be populated with hypermedia links to enable resource discovery
        VirtualTourResource.populateWithHATEOASLinks(virtualTours, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(virtualTours).build();
    }

    /**
     * Method returns subset of Virtual Tour entities for any given tag names.
     * The tag names are passed through query params.
     */
    @GET
    @Path("/tagged-any")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getVirtualToursByAnyTagNames( @QueryParam("tagName") List<String> tagNames,
                                                  @BeanParam PaginationBeanParam params ) throws ForbiddenException, BadRequestException  {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning virtual tours for any given tag names using VirtualTourResource.getVirtualToursByAnyTagNames(tagNames) method of REST API");

        if(tagNames.size() < 1)
            throw new BadRequestException("There must be specified at least one tag name.");

        // find virtual tours by given criteria
        ResourceList<VirtualTour> virtualTours = new ResourceList<>( virtualTourFacade.findByAnyTagNames(tagNames, params.getOffset(), params.getLimit()) );

        // result resource need to be populated with hypermedia links to enable resource discovery
        VirtualTourResource.populateWithHATEOASLinks(virtualTours, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(virtualTours).build();
    }

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
            Method virtualToursEagerlyMethod = VirtualTourResource.class.getMethod("getVirtualToursEagerly", VirtualTourBeanParam.class);
            virtualTours.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(VirtualTourResource.class)
                    .path(virtualToursEagerlyMethod)
                    .build()).rel("virtual-tours-eagerly").build() );

            // get subset of resources hypermedia links

            // file-named
            virtualTours.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(VirtualTourResource.class)
                    .path("file-named")
                    .build())
                    .rel("file-named").build() );

            // described
            virtualTours.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(VirtualTourResource.class)
                    .path("described")
                    .build())
                    .rel("described").build() );

            // containing-keyword
            virtualTours.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(VirtualTourResource.class)
                    .path("containing-keyword")
                    .build())
                    .rel("containing-keyword").build() );

            // tagged
            virtualTours.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(VirtualTourResource.class)
                    .path("tagged")
                    .build())
                    .rel("tagged").build() );

            // tagged-any
            virtualTours.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(VirtualTourResource.class)
                    .path("tagged-any")
                    .build())
                    .rel("tagged-any").build() );

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
