package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.CorporationFacade;
import pl.salonea.ejb.stateless.ProviderFacade;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.Corporation;
import pl.salonea.entities.Provider;
import pl.salonea.jaxrs.bean_params.CorporationBeanParam;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.bean_params.ProviderBeanParam;
import pl.salonea.jaxrs.exceptions.ExceptionHandler;
import pl.salonea.jaxrs.exceptions.UnprocessableEntityException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.ws.rs.*;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.CorporationWrapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response.Status;

/**
 * Created by michzio on 17/10/2015.
 */
@Path("/corporations")
public class CorporationResource {

    private static final Logger logger = Logger.getLogger(CorporationResource.class.getName());

    @Inject
    private CorporationFacade corporationFacade;
    @Inject
    private ProviderFacade providerFacade;

    /**
     * Method returns all Corporation resources
     * They can be additionally filtered or paginated by @QueryParams
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCorporations( @BeanParam CorporationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Corporations by executing CorporationResource.getCorporations() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<Corporation> corporations = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            Address corporationAddress = new Address(params.getStreet(), params.getHouseNumber(), params.getFlatNumber(),
                    params.getZipCode(), params.getCity(), params.getState(), params.getCountry());

            // get corporations filtered by criteria provided in query params
            corporations = new ResourceList<>(
                    corporationFacade.findByMultipleCriteria( params.getProviders(), params.getName(), params.getDescription(), params.getHistory(),
                            params.getStartOpeningDate(), params.getEndOpeningDate(), corporationAddress, params.getOffset(), params.getLimit() )
            );

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all corporations without filtering (eventually paginated)
            corporations = new ResourceList<>( corporationFacade.findAll(params.getOffset(), params.getLimit()) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        CorporationResource.populateWithHATEOASLinks(corporations, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(corporations).build();
    }

    @GET
    @Path("/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCorporationsEagerly( @BeanParam CorporationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Corporations eagerly by executing CorporationResource.getCorporationsEagerly() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<CorporationWrapper> corporations = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            Address corporationAddress = new Address(params.getStreet(), params.getHouseNumber(), params.getFlatNumber(),
                    params.getZipCode(), params.getCity(), params.getState(), params.getCountry());

            // get corporations filtered by criteria provided in query params
            corporations = new ResourceList<>(
                    CorporationWrapper.wrap(
                            corporationFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getName(), params.getDescription(), params.getHistory(),
                                    params.getStartOpeningDate(), params.getEndOpeningDate(), corporationAddress, params.getOffset(), params.getLimit())
                    )
            );

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all corporations without filtering (eventually paginated)
            corporations = new ResourceList<>( CorporationWrapper.wrap(corporationFacade.findAllEagerly(params.getOffset(), params.getLimit())) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        CorporationResource.populateWithHATEOASLinks(corporations, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(corporations).build();
    }

    /**
     * Method matches specific Corporation resource by identifier and returns its instance
     */
    @GET
    @Path("/{corporationId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCorporation( @PathParam("corporationId") Long corporationId,
                                    @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning given Corporation by executing CorporationResource.getCorporation(corporationId) method of REST API");

        Corporation foundCorporation = corporationFacade.find(corporationId);
        if(foundCorporation == null)
            throw new NotFoundException("Could not find corporation for id " + corporationId + ".");

        // adding hypermedia links to corporation resource
        CorporationResource.populateWithHATEOASLinks(foundCorporation, params.getUriInfo());

        return Response.status(Status.OK).entity(foundCorporation).build();
    }

    /**
     * Method matches specific Corporation resource by identifier and returns its instance fetching it eagerly
     */
    @GET
    @Path("/{corporationId : \\d+}/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCorporationEagerly( @PathParam("corporationId") Long corporationId,
                                           @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning given Corporation eagerly by executing CorporationResource.getCorporationEagerly(corporationId) method of REST API");

        Corporation foundCorporation = corporationFacade.findByIdEagerly(corporationId);
        if(foundCorporation == null)
            throw new NotFoundException("Could not find corporation for id " + corporationId + ".");

        // wrapping Corporation into CorporationWrapper in order to marshall eagerly fetched associated collection of entities
        CorporationWrapper wrappedCorporation = new CorporationWrapper(foundCorporation);

        // adding hypermedia links to wrapped corporation resource
        CorporationResource.populateWithHATEOASLinks(wrappedCorporation, params.getUriInfo());

        return Response.status(Status.OK).entity(wrappedCorporation).build();
    }

    /**
     * Method that takes Corporation as XML or JSON and creates its new instance in database
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createCorporation( Corporation corporation,
                                       @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "creating new Corporation by executing CorporationResource.createCorporation(corporation) method of REST API");

        Corporation createdCorporation = null;
        URI locationURI = null;

        try {
            // persist new resource in database
            createdCorporation = corporationFacade.create(corporation);

            // populate created resource with hypermedia links
            CorporationResource.populateWithHATEOASLinks(createdCorporation, params.getUriInfo());

            // construct link to newly created resource to return in HTTP header
            String createdCorporationId = String.valueOf(createdCorporation.getCorporationId());
            locationURI = params.getUriInfo().getBaseUriBuilder().path(CorporationResource.class).path(createdCorporationId).build();

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_CREATION_ERROR_MESSAGE);
        }

        return Response.created(locationURI).entity(createdCorporation).build();
    }

    /**
     * Method that takes updated Corporation as XML or JSON and its ID as path param.
     * It updates Corporation in database for provided ID.
     */
    @PUT
    @Path("/{corporationId : \\d+}") // catch only numeric identifiers
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateCorporation( @PathParam("corporationId") Long corporationId,
                                       Corporation corporation,
                                       @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "updating existing Corporation by executing CorporationResource.updateCorporation(corporationId, corporation) method of REST API");

        // set resource ID passed in path param on updated resource object
        corporation.setCorporationId(corporationId);

        Corporation updatedCorporation = null;
        try {
            // reflect updated resource object in database
            updatedCorporation = corporationFacade.update(corporation, true);
            // populate created resource with hypermedia links
            CorporationResource.populateWithHATEOASLinks(updatedCorporation, params.getUriInfo());

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_UPDATE_ERROR_MESSAGE);
        }

        return Response.status(Status.OK).entity(updatedCorporation).build();
    }

    /**
     * Method that removes Corporation entity from database for given ID.
     * The ID is passed through path param.
     */
    @DELETE
    @Path("/{corporationId : \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeCorporation( @PathParam("corporationId") Long corporationId,
                                       @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing given Corporation by executing CorporationResource.removeCorporation(corporationId) method of REST API");

        // find Corporation entity that should be deleted
        Corporation toDeleteCorporation = corporationFacade.find(corporationId);
        // throw exception if entity hasn't been found
        if(toDeleteCorporation == null)
            throw new NotFoundException("Could not find corporation to delete for given id " + corporationId + ".");

        // remove entity from database
        corporationFacade.remove(toDeleteCorporation);

        return Response.status(Status.NO_CONTENT).build();
    }

    /**
     *
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countCorporations( @HeaderParam("authToken") String authToken ) throws ForbiddenException {

        if(authToken == null) throw new ForbiddenException("Unauthorized access to web service.");

        return null;
    }

    /**
     * related subresources (through relationships)
     */

    @Path("/{corporationId : \\d+}/providers")
    public ProviderResource getProviderResource() {
        return new ProviderResource();
    }

    // helper methods e.g. to populate resources/resource lists with HATEOAS links

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList corporations, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(corporations, uriInfo, offset, limit);

        try {

            // count resources hypermedia link
            Method countMethod = CorporationResource.class.getMethod("countCorporations", String.class);
            corporations.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(CorporationResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            corporations.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(CorporationResource.class).build()).rel("corporations").build() );

            // get all resources eagerly hypermedia link
            Method corporationsEagerlyMethod = CorporationResource.class.getMethod("getCorporationsEagerly", CorporationBeanParam.class);
            corporations.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(CorporationResource.class)
                    .path(corporationsEagerlyMethod)
                    .build())
                    .rel("corporations-eagerly").build());

            // get subset of resources hypermedia links

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for(Object object : corporations.getResources()) {
            if(object instanceof Corporation) {
                CorporationResource.populateWithHATEOASLinks( (Corporation) object, uriInfo);
            } else if(object instanceof CorporationWrapper) {
                CorporationResource.populateWithHATEOASLinks( (CorporationWrapper) object, uriInfo);
            }
        }
    }

    /**
     * This method enables to populate each individual resource wrapper with hypermedia links
     */
    public static void populateWithHATEOASLinks(CorporationWrapper corporationWrapper, UriInfo uriInfo) {

        CorporationResource.populateWithHATEOASLinks(corporationWrapper.getCorporation(), uriInfo);

        for(Provider provider : corporationWrapper.getProviders())
            pl.salonea.jaxrs.ProviderResource.populateWithHATEOASLinks(provider, uriInfo);
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(Corporation corporation, UriInfo uriInfo) {

        // self link with pattern: http://localhost:port/app/rest/{resources}/{id}
        corporation.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                        .path(CorporationResource.class)
                                                        .path(corporation.getCorporationId().toString())
                                                        .build())
                                        .rel("self").build());

        // collection link with pattern: http://localhost:port/app/rest/{resources}
        corporation.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                        .path(CorporationResource.class)
                                                        .build())
                                        .rel("corporations").build());

        try {
            // self eagerly link with pattern http://localhost:port/app/rest/{resource}/{id}/eagerly
            Method corporationEagerlyMethod = CorporationResource.class.getMethod("getCorporationEagerly", Long.class, GenericBeanParam.class);
            corporation.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                        .path(CorporationResource.class)
                        .path(corporationEagerlyMethod)
                        .resolveTemplate("corporationId", corporation.getCorporationId().toString())
                        .build())
                        .rel("corporation-eagerly").build());

            // associated collections links with pattern: http://localhost:port/app/rest/{resources}/{id}/{relationship}

            // providers
            Method providersMethod = CorporationResource.class.getMethod("getProviderResource");
            corporation.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                        .path(CorporationResource.class)
                        .path(providersMethod)
                        .resolveTemplate("corporationId",corporation.getCorporationId().toString())
                        .build())
                        .rel("providers").build());

            // providers-eagerly TODO

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    public class ProviderResource {

        public ProviderResource() { }

        /**
         * Method returns subset of Provider entities for given Corporation entity.
         * The corporation id is passed through path param.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getCorporationProviders(@PathParam("corporationId") Long corporationId,
                                                @BeanParam ProviderBeanParam params) throws ForbiddenException, NotFoundException {

            if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning providers for given corporation using CorporationResource.ProviderResource.getCorporationProviders(corporationId) method of REST API");

            // find corporation entity for which to get associated providers
            Corporation corporation = corporationFacade.find(corporationId);
            if (corporation == null)
                throw new NotFoundException("Could not find corporation for id " + corporationId + ".");

            // calculate number of filter query params
            Integer noOfParams = params.getUriInfo().getQueryParameters().size();
            if(params.getOffset() != null) noOfParams -= 1;
            if(params.getLimit() != null) noOfParams -= 1;

            ResourceList<Provider> providers = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Corporation> corporations = new ArrayList<>();
                corporations.add(corporation);

                // get providers for given corporation filtered by given params
                providers = new ResourceList<>(
                        providerFacade.findByMultipleCriteria(corporations, params.getProviderTypes(), params.getIndustries(), params.getPaymentMethods(),
                                params.getServices(), params.getRated(), params.getMinAvgRating(), params.getMaxAvgRating(),params.getRatingClients(),
                                params.getProviderName(), params.getDescription(), params.getOffset(), params.getLimit())
                );
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get providers for given corporation without filtering
                providers = new ResourceList<>(providerFacade.findByCorporation(corporation, params.getOffset(), params.getLimit()));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Response.Status.OK).entity(providers).build();
        }

        // TODO eagerly all

    }

}
