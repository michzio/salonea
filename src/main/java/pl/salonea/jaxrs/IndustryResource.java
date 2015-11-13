package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.IndustryFacade;
import pl.salonea.ejb.stateless.ProviderFacade;
import pl.salonea.entities.Industry;
import pl.salonea.entities.Provider;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.bean_params.IndustryBeanParam;
import pl.salonea.jaxrs.bean_params.ProviderBeanParam;
import pl.salonea.jaxrs.exceptions.ExceptionHandler;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.exceptions.UnprocessableEntityException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.IndustryWrapper;

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
            throw new NotFoundException("Could not find industry id for id " + industryId + ".");

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

    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countIndustries( @HeaderParam("authToken") String authToken ) throws ForbiddenException {

        if(authToken == null) throw new ForbiddenException("Unauthorized access to web service.");

        return null;
    }

    /**
     * related subresources (through relationships)
     */

    @Path("/{industryId : \\d+}/providers")
    public ProviderResource getProviderResource() {
        return new ProviderResource();
    }

    // helper methods e.g. to populate resources/resource lists with HATEOAS links

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList industries, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(industries, uriInfo, offset, limit);

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

            // associated collections links with with pattern: http://localhost:port/app/rest/{resources}/{id}/{relationship}

            // providers
            Method providersMethod = IndustryResource.class.getMethod("getProviderResource");
            industry.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(IndustryResource.class)
                    .path(providersMethod)
                    .resolveTemplate("industryId", industry.getIndustryId().toString())
                    .build())
                    .rel("providers").build());

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

            if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
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

    }
}
