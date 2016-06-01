package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.IndustryFacade;
import pl.salonea.ejb.stateless.ProviderFacade;
import pl.salonea.ejb.stateless.ServicePointFacade;
import pl.salonea.entities.Industry;
import pl.salonea.entities.Provider;
import pl.salonea.entities.ServicePoint;
import pl.salonea.jaxrs.bean_params.*;
import pl.salonea.jaxrs.exceptions.*;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.IndustryWrapper;
import pl.salonea.jaxrs.wrappers.ProviderWrapper;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.salonea.jaxrs.exceptions.BadRequestException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.wrappers.ServicePointWrapper;

/**
 * Created by michzio on 12/09/2015.
 */
@Path("/industries")
public class IndustryResource {

    private static final Logger logger = Logger.getLogger(IndustryResource.class.getName());

    @Inject
    private IndustryFacade industryFacade;
    @Inject
    private ProviderFacade providerFacade;
    @Inject
    private ServicePointFacade servicePointFacade;

    /**
     * Method returns all Industry resources
     * They can be additionally filtered or paginated by @QueryParams
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getIndustries( @BeanParam IndustryBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Industries by executing IndustryResource.getIndustries() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<Industry> industries = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            // get industries filtered by criteria provided in query params
            industries = new ResourceList<>(
                    industryFacade.findByMultipleCriteria(params.getProviders(), params.getName(), params.getDescription(), params.getOffset(), params.getLimit())
            );

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all industries without filtering (eventually paginated)
            industries = new ResourceList<>( industryFacade.findAll(params.getOffset(), params.getLimit()) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        IndustryResource.populateWithHATEOASLinks(industries, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(industries).build();
    }

    @GET
    @Path("/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getIndustriesEagerly( @BeanParam IndustryBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Industries eagerly by executing IndustryResource.getIndustriesEagerly() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<IndustryWrapper> industries = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            // get industries filtered by criteria provided in query params
            industries = new ResourceList<>(
                    IndustryWrapper.wrap(
                            industryFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getName(), params.getDescription(), params.getOffset(), params.getLimit())
                    )
            );
        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all industries without filtering (eventually paginated)
            industries = new ResourceList<>( IndustryWrapper.wrap(industryFacade.findAllEagerly(params.getOffset(), params.getLimit())) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        IndustryResource.populateWithHATEOASLinks(industries, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(industries).build();
    }

    /**
     * Method matches specific Industry resource by identifier and returns its instance
     */
    @GET
    @Path("/{industryId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getIndustry( @PathParam("industryId") Long industryId,
                                 @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning given Industry by executing IndustryResource.getIndustry(industryId) method of REST API");

        Industry foundIndustry = industryFacade.find(industryId);
        if(foundIndustry == null)
            throw new NotFoundException("Could not find industry for id " + industryId + ".");

        // adding hypermedia links to industry resource
       IndustryResource.populateWithHATEOASLinks(foundIndustry, params.getUriInfo());

        return Response.status(Status.OK).entity(foundIndustry).build();
    }

    /**
     * Method matches specific Industry resource by identifier and returns its instance fetching it eagerly
     */
    @GET
    @Path("/{industryId : \\d+}/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getIndustryEagerly( @PathParam("industryId") Long industryId,
                                        @BeanParam GenericBeanParam params  ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning given Industry eagerly by executing IndustryResource.getIndustryEagerly(industryId) method of REST API");

        Industry foundIndustry = industryFacade.findByIdEagerly(industryId);
        if(foundIndustry == null)
            throw new NotFoundException("Could not find industry for id " + industryId + ".");

        // wrapping Industry into IndustryWrapper in order to marshall eagerly fetched associated collection of entities
        IndustryWrapper wrappedIndustry = new IndustryWrapper(foundIndustry);

        // adding hypermedia links to wrapped industry resource
        IndustryResource.populateWithHATEOASLinks(wrappedIndustry, params.getUriInfo());

        return Response.status(Status.OK).entity(wrappedIndustry).build();
    }

    /**
     * Method that takes Industry as XML or JSON and creates its new instance in database
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createIndustry( Industry industry,
                                    @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "creating new Industry by executing IndustryResource.createIndustry(industry) method of REST API");

        Industry createdIndustry = null;
        URI locationURI = null;

        try {
            // persist new resource in database
            createdIndustry = industryFacade.create(industry);

            // populate created resource with hypermedia links
           IndustryResource.populateWithHATEOASLinks(createdIndustry, params.getUriInfo());

            // construct link to newly created resource to return in HTTP header
            String createdIndustryId = String.valueOf(createdIndustry.getIndustryId());
            locationURI = params.getUriInfo().getBaseUriBuilder().path(IndustryResource.class).path(createdIndustryId).build();

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_CREATION_ERROR_MESSAGE);
        }

        return Response.created(locationURI).entity(createdIndustry).build();
    }

    /**
     * Method that takes updated Industry as XML or JSON and its ID as path param.
     * It updates Industry in database for provided ID.
     */
    @PUT
    @Path("/{industryId : \\d+}") // catch only numeric identifiers
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateIndustry( @PathParam("industryId") Long industryId,
                                    Industry industry,
                                    @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "updating existing Industry by executing IndustryResource.updateIndustry(industryId, industry) method of REST API");

        // set resource ID passed in path param on updated resource object
        industry.setIndustryId(industryId);

        Industry updatedIndustry = null;
        try {
            // reflect updated resource object in database
            updatedIndustry = industryFacade.update(industry, true);
            // populate created resource with hypermedia links
            IndustryResource.populateWithHATEOASLinks(updatedIndustry, params.getUriInfo());

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_UPDATE_ERROR_MESSAGE);
        }

        return Response.status(Status.OK).entity(updatedIndustry).build();
    }

    /**
     * Method that removes Industry entity from database for given ID.
     * The ID is passed through path param.
     */
    @DELETE
    @Path("/{industryId : \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeIndustry( @PathParam("industryId") Long industryId,
                                    @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing given Industry by executing IndustryResource.removeIndustry(industryId) method of REST API");

        // find Industry entity that should be deleted
        Industry toDeleteIndustry = industryFacade.find(industryId);
        // throw exception if entity hasn't been found
        if(toDeleteIndustry == null)
            throw new NotFoundException("Could not find industry to delete for given id" + industryId + ".");

        // remove entity from database
        industryFacade.remove(toDeleteIndustry);

        return Response.status(Status.NO_CONTENT).build();
    }

    /**
     * Additional methods returning a subset of resources based on given criteria
     * You can also achieve similar results by applying @QueryParams to generic method
     * returning all resources in order to filter and limit them.
     */

    /**
     * Method returns number of Industry entities in database
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countIndustries( @BeanParam GenericBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning number of industries by executing IndustryResource.countIndustries() method of REST API");

        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(industryFacade.count()), 200, "number of industries");
        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method returns subset of Industry entities for given name.
     * The name is passed through path param.
     */
    @GET
    @Path("/named/{name : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getIndustriesByName( @PathParam("name") String name,
                                         @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning industries for given name using IndustryResource.getIndustriesByName(name) method of REST API");

        // find industries by given criteria
        ResourceList<Industry> industries = new ResourceList<>( industryFacade.findByName(name, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        IndustryResource.populateWithHATEOASLinks(industries, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(industries).build();
    }

    /**
     * Method returns subset of Industry entities for given description.
     * The description is passed through path param.
     */
    @GET
    @Path("/described/{description : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getIndustriesByDescription( @PathParam("description") String description,
                                                @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning industries for given description using IndustryResource.getIndustriesByDescription(description) method of REST API");

        // find industries by given criteria
        ResourceList<Industry> industries = new ResourceList<>( industryFacade.findByDescription(description, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        IndustryResource.populateWithHATEOASLinks(industries, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(industries).build();
    }

    /**
     * Method returns subset of Industry entities for given keyword.
     * The keyword is passed through path param.
     */
    @GET
    @Path("/containing-keyword/{keyword : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getIndustriesByKeyword( @PathParam("keyword") String keyword,
                                            @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning industries for given keyword using IndustryResource.getIndustriesByKeyword(keyword) method of REST API");

        // find industries by given criteria
        ResourceList<Industry> industries = new ResourceList<>( industryFacade.findByKeyword(keyword, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        IndustryResource.populateWithHATEOASLinks(industries, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(industries).build();
    }

    /**
     * related subresources (through relationships)
     */

    @Path("/{industryId : \\d+}/providers")
    public ProviderResource getProviderResource() {
        return new ProviderResource();
    }

    @Path("/{industryId : \\d+}/service-points")
    public ServicePointResource getServicePointResource() {
        return new ServicePointResource();
    }

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList industries, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(industries, uriInfo, offset, limit);

        try {

            // count resources hypermedia link
            Method countMethod = IndustryResource.class.getMethod("countIndustries", GenericBeanParam.class);
            industries.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(IndustryResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            industries.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(IndustryResource.class).build()).rel("industries").build() );

            // get all resources eagerly hypermedia link
            Method industriesEagerlyMethod = IndustryResource.class.getMethod("getIndustriesEagerly", IndustryBeanParam.class);
            industries.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(IndustryResource.class)
                    .path(industriesEagerlyMethod)
                    .build())
                    .rel("industries-eagerly").build());

            // get subset of resources hypermedia links
            // named
            industries.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(IndustryResource.class).path("named").build()).rel("named").build() );

            // described
            industries.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(IndustryResource.class).path("described").build()).rel("described").build() );

            // containing-keyword
            industries.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(IndustryResource.class).path("containing-keyword").build()).rel("containing-keyword").build() );

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
    public static void populateWithHATEOASLinks(IndustryWrapper industryWrapper, UriInfo uriInfo) {

        IndustryResource.populateWithHATEOASLinks(industryWrapper.getIndustry(), uriInfo);

        for(Provider provider : industryWrapper.getProviders())
            pl.salonea.jaxrs.ProviderResource.populateWithHATEOASLinks(provider, uriInfo);
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

        try {
            // self eagerly link with pattern http://localhost:port/app/rest/{resources}/{id}/eagerly
            Method industryEagerlyMethod = IndustryResource.class.getMethod("getIndustryEagerly", Long.class, GenericBeanParam.class);
            industry.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(IndustryResource.class)
                    .path(industryEagerlyMethod)
                    .resolveTemplate("industryId", industry.getIndustryId().toString())
                    .build())
                    .rel("industry-eagerly").build());

            // associated collections links with pattern: http://localhost:port/app/rest/{resources}/{id}/{relationship}

            /**
             * Providers associated with current Industry resource
             */

            // providers
            Method providersMethod = IndustryResource.class.getMethod("getProviderResource");
            industry.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(IndustryResource.class)
                    .path(providersMethod)
                    .resolveTemplate("industryId", industry.getIndustryId().toString())
                    .build())
                    .rel("providers").build());

            // providers-eagerly
            Method providersEagerlyMethod = IndustryResource.ProviderResource.class.getMethod("getIndustryProvidersEagerly", Long.class, ProviderBeanParam.class);
            industry.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(IndustryResource.class)
                    .path(providersMethod)
                    .path(providersEagerlyMethod)
                    .resolveTemplate("industryId", industry.getIndustryId().toString())
                    .build())
                    .rel("providers-eagerly").build());

            /**
             * Service Points associated with current Industry resource
             */

            // service-points relationship
            Method servicePointsMethod = IndustryResource.class.getMethod("getServicePointResource");
            industry.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(IndustryResource.class)
                    .path(servicePointsMethod)
                    .resolveTemplate("industryId", industry.getIndustryId().toString())
                    .build())
                    .rel("service-points").build());

            // service-points eagerly relationship
            Method servicePointsEagerlyMethod = IndustryResource.ServicePointResource.class.getMethod("getIndustryServicePointsEagerly", Long.class, ServicePointBeanParam.class);
            industry.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(IndustryResource.class)
                    .path(servicePointsMethod)
                    .path(servicePointsEagerlyMethod)
                    .resolveTemplate("industryId", industry.getIndustryId().toString())
                    .build())
                    .rel("service-points-eagerly").build());

            // service-points count link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/count
            Method countServicePointsByIndustryMethod = IndustryResource.ServicePointResource.class.getMethod("countServicePointsByIndustry", Long.class, GenericBeanParam.class);
            industry.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(IndustryResource.class)
                    .path(servicePointsMethod)
                    .path(countServicePointsByIndustryMethod)
                    .resolveTemplate("industryId", industry.getIndustryId().toString())
                    .build())
                    .rel("service-points-count").build());

            // service-points address link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/address
            Method addressMethod = IndustryResource.ServicePointResource.class.getMethod("getIndustryServicePointsByAddress", Long.class, AddressBeanParam.class);
            industry.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(IndustryResource.class)
                    .path(servicePointsMethod)
                    .path(addressMethod)
                    .resolveTemplate("industryId", industry.getIndustryId().toString())
                    .build())
                    .rel("service-points-address").build());

            // service-points coordinates-square link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/coordinates-square
            Method coordinatesSquareMethod = IndustryResource.ServicePointResource.class.getMethod("getIndustryServicePointsByCoordinatesSquare", Long.class, CoordinatesSquareBeanParam.class);
            industry.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(IndustryResource.class)
                    .path(servicePointsMethod)
                    .path(coordinatesSquareMethod)
                    .resolveTemplate("industryId", industry.getIndustryId().toString())
                    .build())
                    .rel("service-points-coordinates-square").build());

            // service-points coordinates-circle link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/coordinates-circle
            Method coordinatesCircleMethod = IndustryResource.ServicePointResource.class.getMethod("getIndustryServicePointsByCoordinatesCircle", Long.class, CoordinatesCircleBeanParam.class);
            industry.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(IndustryResource.class)
                    .path(servicePointsMethod)
                    .path(coordinatesCircleMethod)
                    .resolveTemplate("industryId", industry.getIndustryId().toString())
                    .build())
                    .rel("service-points-coordinates-circle").build());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    public class ProviderResource {

        public ProviderResource() { }

        /**
         * Method returns subset of Provider entities for given Industry entity.
         * The industry id is passed through path param.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getIndustryProviders(@PathParam("industryId") Long industryId,
                                             @BeanParam ProviderBeanParam params) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning providers for given industry using IndustryResource.ProviderResource.getIndustryProviders(industryId) method of REST API");

            // find industry entity for which to get associated providers
            Industry industry = industryFacade.find(industryId);
            if (industry == null)
                throw new NotFoundException("Could not find industry for id " + industryId + ".");

            // calculate number of filter query params
            Integer noOfParams = params.getUriInfo().getQueryParameters().size();
            if(params.getOffset() != null) noOfParams -= 1;
            if(params.getLimit() != null) noOfParams -= 1;

            ResourceList<Provider> providers = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Industry> industries = new ArrayList<>();
                industries.add(industry);

                // get providers for given industry filtered by given params.
                providers = new ResourceList<>(
                        providerFacade.findByMultipleCriteria(params.getCorporations(), params.getProviderTypes(), industries, params.getPaymentMethods(),
                                params.getServices(), params.getRated(), params.getMinAvgRating(), params.getMaxAvgRating(),params.getRatingClients(),
                                params.getProviderName(), params.getDescription(), params.getOffset(), params.getLimit())
                );

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get providers for given industry without filtering
                providers = new ResourceList<>( providerFacade.findByIndustry(industry, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providers).build();
        }

        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getIndustryProvidersEagerly( @PathParam("industryId") Long industryId,
                                                     @BeanParam ProviderBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Provider entities for given Industry eagerly using IndustryResource.ProviderResource.getIndustryProvidersEagerly(industryId) method of REST API");

            // find industry entity for which to get associated providers
            Industry industry = industryFacade.find(industryId);
            if(industry == null)
                throw new NotFoundException("Could not find industry for id " + industryId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ProviderWrapper> providers = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Industry> industries = new ArrayList<>();
                industries.add(industry);

                // get providers for given industry eagerly filtered by given params
                providers = new ResourceList<>(
                        ProviderWrapper.wrap(
                                providerFacade.findByMultipleCriteriaEagerly(params.getCorporations(), params.getProviderTypes(), industries, params.getPaymentMethods(),
                                        params.getServices(), params.getRated(), params.getMinAvgRating(), params.getMaxAvgRating(), params.getRatingClients(), params.getProviderName(),
                                        params.getDescription(), params.getOffset(), params.getLimit())
                        )
                );
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get providers for given industry eagerly without filtering (eventually paginated)
                providers = new ResourceList<>( ProviderWrapper.wrap(providerFacade.findByIndustryEagerly(industry, params.getOffset(), params.getLimit())) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providers).build();
        }
    }

    public class ServicePointResource {

        public ServicePointResource() {
        }

        /**
         * Method returns subset of Service Point entities for given Industry.
         * The industry id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getIndustryServicePoints(@PathParam("industryId") Long industryId,
                                                 @BeanParam ServicePointBeanParam params) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Service Point entities for given Industry using " +
                    "IndustryResource.ServicePointResource.getIndustryServicePoints(industryId) method of REST API");

            // find industry entity for which to get associated service points
            Industry industry = industryFacade.find(industryId);
            if (industry == null)
                throw new NotFoundException("Could not find industry for id " + industryId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ServicePoint> servicePoints = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Industry> industries = new ArrayList<>();
                industries.add(industry);

                if (params.getAddress() != null) {
                    if (params.getCoordinatesSquare() != null || params.getCoordinatesCircle() != null)
                        throw new BadRequestException("Query params cannot include address params and coordinates square params or coordinates circle params at the same time.");
                    // only address params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(params.getProviders(), params.getServices(), params.getProviderServices(), params.getEmployees(),
                                    params.getCorporations(), industries, params.getServiceCategories(), params.getAddress(), params.getTerms(),
                                    params.getOffset(), params.getLimit())
                    );
                } else if (params.getCoordinatesSquare() != null) {
                    if (params.getAddress() != null || params.getCoordinatesCircle() != null)
                        throw new BadRequestException("Query params cannot include coordinates square params and address params or coordinates circle params at the same time.");
                    // only coordinates square params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(params.getProviders(), params.getServices(), params.getProviderServices(), params.getEmployees(),
                                    params.getCorporations(), industries, params.getServiceCategories(), params.getCoordinatesSquare(), params.getTerms(),
                                    params.getOffset(), params.getLimit())
                    );
                } else if (params.getCoordinatesCircle() != null) {
                    if (params.getAddress() != null || params.getCoordinatesSquare() != null)
                        throw new BadRequestException("Query params cannot include coordinates circle params and address params or coordinates square params at the same time.");
                    // only coordinates circle params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(params.getProviders(), params.getServices(), params.getProviderServices(), params.getEmployees(),
                                    params.getCorporations(), industries, params.getServiceCategories(), params.getCoordinatesCircle(), params.getTerms(),
                                    params.getOffset(), params.getLimit())
                    );
                } else {
                    // no location params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(params.getProviders(), params.getServices(), params.getProviderServices(), params.getEmployees(),
                                    params.getCorporations(), industries, params.getServiceCategories(), params.getTerms(),
                                    params.getOffset(), params.getLimit())
                    );
                }
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                servicePoints = new ResourceList<>(servicePointFacade.findByIndustry(industry, params.getOffset(), params.getLimit()));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(servicePoints).build();
        }

        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getIndustryServicePointsEagerly(@PathParam("industryId") Long industryId,
                                                        @BeanParam ServicePointBeanParam params) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Service Point entities for given Industry eagerly using " +
                    "IndustryResource.ServicePointResource.getIndustryServicePointsEagerly(industryId) method of REST API");

            // find industry entity for which to get associated service points
            Industry industry = industryFacade.find(industryId);
            if (industry == null)
                throw new NotFoundException("Could not find industry for id " + industryId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ServicePointWrapper> servicePoints = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Industry> industries = new ArrayList<>();
                industries.add(industry);

                if (params.getAddress() != null) {
                    if (params.getCoordinatesSquare() != null || params.getCoordinatesCircle() != null)
                        throw new BadRequestException("Query params cannot include address params and coordinates square params or coordinates circle params at the same time.");
                    // only address params
                    servicePoints = new ResourceList<>(
                            ServicePointWrapper.wrap(
                                    servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getServices(), params.getProviderServices(), params.getEmployees(),
                                            params.getCorporations(), industries, params.getServiceCategories(), params.getAddress(), params.getTerms(),
                                            params.getOffset(), params.getLimit())
                            )
                    );
                } else if (params.getCoordinatesSquare() != null) {
                    if (params.getAddress() != null || params.getCoordinatesCircle() != null)
                        throw new BadRequestException("Query params cannot include coordinates square params and address params or coordinates circle params at the same time.");
                    // only coordinates square params
                    servicePoints = new ResourceList<>(
                            ServicePointWrapper.wrap(
                                    servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getServices(), params.getProviderServices(), params.getEmployees(),
                                            params.getCorporations(), industries, params.getServiceCategories(), params.getCoordinatesSquare(), params.getTerms(),
                                            params.getOffset(), params.getLimit())
                            )
                    );
                } else if (params.getCoordinatesCircle() != null) {
                    if (params.getAddress() != null || params.getCoordinatesSquare() != null)
                        throw new BadRequestException("Query params cannot include coordinates circle params and address params or coordinates square params at the same time.");
                    // only coordinates circle params
                    servicePoints = new ResourceList<>(
                            ServicePointWrapper.wrap(
                                    servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getServices(), params.getProviderServices(), params.getEmployees(),
                                            params.getCorporations(), industries, params.getServiceCategories(), params.getCoordinatesCircle(), params.getTerms(),
                                            params.getOffset(), params.getLimit())
                            )
                    );
                } else {
                    // no location params
                    servicePoints = new ResourceList<>(
                            ServicePointWrapper.wrap(
                                    servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getServices(), params.getProviderServices(), params.getEmployees(),
                                            params.getCorporations(), industries, params.getServiceCategories(), params.getTerms(),
                                            params.getOffset(), params.getLimit())
                            )
                    );
                }
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                servicePoints = new ResourceList<>(ServicePointWrapper.wrap(servicePointFacade.findByIndustryEagerly(industry, params.getOffset(), params.getLimit())));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(servicePoints).build();
        }

        /**
         * Additional methods returning subset of resources based on given criteria
         * you can also achieve similar results by applying @QueryParams to generic method
         * returning all resources in order to filter and limit them
         */

        /**
         * Method that counts Service Point entities for given Industry resource.
         * The industry id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countServicePointsByIndustry( @PathParam("industryId") Long industryId,
                                                      @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of service points for given industry by executing " +
                    "IndustryResource.ServicePointResource.countServicePointsByIndustry(industryId) method of REST API");

            // find industry entity for which to count service points
            Industry industry = industryFacade.find(industryId);
            if (industry == null)
                throw new NotFoundException("Could not find industry for id " + industryId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(servicePointFacade.countByIndustry(industry)), 200, "number of service points for industry with id " + industry.getIndustryId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method returns subset of Service Point entities for given Industry entity and
         * Address related query params. The industry id is passed through path param.
         * Address params are passed through query params.
         */
        @GET
        @Path("/address")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getIndustryServicePointsByAddress( @PathParam("industryId") Long industryId,
                                                           @BeanParam AddressBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning service points for given industry and address related params using " +
                    "IndustryResource.ServicePointResource.getIndustryServicePointsByAddress(industryId, address) method of REST API");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);
            if(noOfParams < 1)
                throw new BadRequestException("There is no address related query param in request.");

            // find industry entity for which to get associated service points
            Industry industry = industryFacade.find(industryId);
            if (industry == null)
                throw new NotFoundException("Could not find industry for id " + industryId + ".");

            // find service points by given criteria
            ResourceList<ServicePoint> servicePoints = new ResourceList<>(
                    servicePointFacade.findByIndustryAndAddress(industry, params.getCity(), params.getState(), params.getCountry(),
                            params.getStreet(), params.getZipCode(), params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(servicePoints).build();
        }

        /**
         * Method returns subset of Service Point entities for given Industry entity and
         * Coordinates Square related params. The industry id is passed through path param.
         * Coordinates Square params are passed through query params.
         */
        @GET
        @Path("/coordinates-square")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getIndustryServicePointsByCoordinatesSquare( @PathParam("industryId") Long industryId,
                                                                     @BeanParam CoordinatesSquareBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException  {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning service points for given industry and coordinates square params using " +
                    "IndustryResource.ServicePointResource.getIndustryServicePointsByCoordinatesSquare(industryId, coordinatesSquare) method of REST API");

            if(params.getMinLongitudeWGS84() == null || params.getMinLatitudeWGS84() == null ||
                    params.getMaxLongitudeWGS84() == null || params.getMaxLatitudeWGS84() == null)
                throw new BadRequestException("All coordinates square query params must be specified.");

            // find industry entity for which to get associated service points
            Industry industry = industryFacade.find(industryId);
            if (industry == null)
                throw new NotFoundException("Could not find industry for id " + industryId + ".");

            // find service points by given criteria
            ResourceList<ServicePoint> servicePoints = new ResourceList<>(
                    servicePointFacade.findByIndustryAndCoordinatesSquare(industry, params.getMinLongitudeWGS84(), params.getMinLatitudeWGS84(),
                            params.getMaxLongitudeWGS84(), params.getMaxLatitudeWGS84(), params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(servicePoints).build();
        }

        /**
         * Method returns subset of Service Point entities for given Industry entity and
         * Coordinates Circle related params. The industry id is passed through path param.
         * Coordinates Circle param are passed through query params.
         */
        @GET
        @Path("/coordinates-circle")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getIndustryServicePointsByCoordinatesCircle( @PathParam("industryId") Long industryId,
                                                                     @BeanParam CoordinatesCircleBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning service points for given industry and coordinates circle params using " +
                    "IndustryResource.ServicePointResource.getIndustryServicePointsByCoordinatesCircle(industryId, coordinatesCircle) method of REST API");

            if(params.getLongitudeWGS84() == null || params.getLatitudeWGS84() == null || params.getRadius() == null)
                throw new BadRequestException("All coordinates circle query params must be specified.");

            // find industry entity for which to get associated service points
            Industry industry = industryFacade.find(industryId);
            if (industry == null)
                throw new NotFoundException("Could not find industry for id " + industryId + ".");

            // find service points by given criteria
            ResourceList<ServicePoint> servicePoints = new ResourceList<>(
                    servicePointFacade.findByIndustryAndCoordinatesCircle(industry, params.getLongitudeWGS84(),
                            params.getLatitudeWGS84(), params.getRadius(), params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(servicePoints).build();
        }
    }
}
