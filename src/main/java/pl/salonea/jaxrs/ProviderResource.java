package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.*;
import pl.salonea.entities.*;
import pl.salonea.enums.ProviderType;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.bean_params.PaginationBeanParam;
import pl.salonea.jaxrs.bean_params.ProviderBeanParam;
import pl.salonea.jaxrs.bean_params.ServicePointBeanParam;
import pl.salonea.jaxrs.exceptions.ExceptionHandler;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.exceptions.UnprocessableEntityException;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.ProviderWrapper;


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
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by michzio on 10/09/2015.
 */
@Path("/providers")
public class ProviderResource {

    private static final Logger logger = Logger.getLogger(ProviderResource.class.getName());

    @Inject
    private ProviderFacade providerFacade;

    @Inject
    private CorporationFacade corporationFacade;

    @Inject
    private IndustryFacade industryFacade;

    @Inject
    private PaymentMethodFacade paymentMethodFacade;

    @Inject
    private ServiceFacade serviceFacade;

    @Inject
    private ClientFacade clientFacade;

    @Inject
    private ServicePointFacade servicePointFacade;

    /**
     * Method returns all Provider resources
     * They can be additionally filtered or paginated by @QueryParams
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProviders( @BeanParam ProviderBeanParam params ) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning all Providers by executing ProviderResource.getProviders() method of REST API");

        // calculate number of filter query params
        Integer noOfParams = params.getUriInfo().getQueryParameters().size();
        if(params.getOffset() != null) noOfParams -= 1;
        if(params.getLimit() != null) noOfParams -= 1;

        ResourceList<Provider> providers = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            // get providers filtered by criteria provided in query params
            providers = new ResourceList<>(
                            providerFacade.findByMultipleCriteria(params.getCorporations(), params.getProviderTypes(),
                                    params.getIndustries(), params.getPaymentMethods(), params.getServices(), params.getRated(),
                                    params.getMinAvgRating(), params.getMaxAvgRating(), params.getRatingClients(), params.getProviderName(),
                                    params.getDescription(), params.getOffset(), params.getLimit())
            );

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all providers without filtering (eventually paginated)
            providers = new ResourceList<>(providerFacade.findAll(params.getOffset(), params.getLimit()));
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(providers).build();
    }

    @GET
    @Path("/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProvidersEagerly(@BeanParam ProviderBeanParam params) throws ForbiddenException, NotFoundException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning all Providers eagerly by executing ProviderResource.getProvidersEagerly() method of REST API");

        // calculate number of filter query params
        Integer noOfParams = params.getUriInfo().getQueryParameters().size();
        if(params.getOffset() != null) noOfParams -= 1;
        if(params.getLimit() != null) noOfParams -= 1;

        ResourceList<ProviderWrapper> providers = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            // get providers filtered by criteria provided in query params
            providers = new ResourceList<>(
                    ProviderWrapper.wrap(
                        providerFacade.findByMultipleCriteriaEagerly(params.getCorporations(), params.getProviderTypes(),
                                params.getIndustries(), params.getPaymentMethods(), params.getServices(), params.getRated(),
                                params.getMinAvgRating(), params.getMaxAvgRating(), params.getRatingClients(), params.getProviderName(),
                                params.getDescription(), params.getOffset(), params.getLimit())
                    )
            );

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all providers without filtering (eventually paginated)
            providers = new ResourceList<>( ProviderWrapper.wrap(providerFacade.findAllEagerly(params.getOffset(), params.getLimit())));
        }

        ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(providers).build();
    }

    /**
     * Method matches specific Provider resource by identifier and returns its instance.
     */
    @GET
    @Path("/{userId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProvider( @PathParam("userId") Long userId,
                                 @BeanParam GenericBeanParam params) throws NotFoundException, ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning given Provider by executing ProviderResource.getProvider(userId) method of REST API");

        Provider foundProvider = providerFacade.find(userId);
        if(foundProvider == null)
            throw new NotFoundException("Could not find provider for id " + userId + ".");

        // adding hypermedia links to provider resource
        ProviderResource.populateWithHATEOASLinks(foundProvider, params.getUriInfo());

        return Response.status(Status.OK).entity(foundProvider).build();
    }

    /**
     * Method matches specific Provider resource by identifier and returns its instance fetching it eagerly
     */
    @GET
    @Path("{userId : \\d+}/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProviderEagerly( @PathParam("userId") Long userId,
                                        @BeanParam GenericBeanParam params) throws NotFoundException, ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning given Provider eagerly by executing ProviderResource.getProviderEagerly(userId) method of REST API");

        Provider foundProvider = providerFacade.findByIdEagerly(userId);
        if(foundProvider == null)
            throw new NotFoundException("Could not find provider for id " + userId + ".");

        // wrapping Provider into ProviderWrapper in order to marshall eagerly fetched associated collection of entities
        ProviderWrapper wrappedProvider = new ProviderWrapper(foundProvider);

        // adding hypermedia links to wrapped provider resource
        ProviderResource.populateWithHATEOASLinks(wrappedProvider, params.getUriInfo());

        return Response.status(Status.OK).entity(wrappedProvider).build();
    }

    /**
     * Method that takes Provider as XML or JSON and creates its new instance in database
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createProvider(Provider provider,
                                   @BeanParam GenericBeanParam params) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "creating new Provider by executing ProviderResource.createProvider(provider) method of REST API");

        if(provider.getRegistrationDate() == null) {
            // if registration date of newly created provider hasn't been set by Client set it now to the current datetime value
            provider.setRegistrationDate(new Date());
        }

        Provider createdProvider = null;
        URI locationURI = null;

        try {
            // persist new resource in database
            createdProvider = providerFacade.create(provider);

            // populate created resource with hypermedia links
            ProviderResource.populateWithHATEOASLinks(createdProvider, params.getUriInfo());

            // construct link to newly created resource to return in HTTP Header
            String createdProviderId = String.valueOf(createdProvider.getUserId());
            locationURI = params.getUriInfo().getBaseUriBuilder().path(ProviderResource.class).path(createdProviderId).build();

        } catch(EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch(EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_CREATION_ERROR_MESSAGE);
        }

        return Response.created(locationURI).entity(createdProvider).build();
    }

    /**
     * Method that takes updated Provider as XML or JSON and its ID as path param.
     * It updates Provider in database for provided ID.
     */
    @PUT
    @Path("/{userId : \\d+}") // catch only numeric identifiers
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateProvider( @PathParam("userId") Long userId,
                                    Provider provider,
                                    @BeanParam GenericBeanParam params) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "updating existing Provider by executing ProviderResource.updateProvider(provider) method of REST API");

        // set resource ID passed in path param on updated resource object
        provider.setUserId(userId);

        // keep current collection attributes of resource (marked @XmlTransient)
        Provider currentProvider = providerFacade.findByIdEagerly(userId);
        if(currentProvider != null) {
            provider.setIndustries(currentProvider.getIndustries());
            provider.setAcceptedPaymentMethods(currentProvider.getAcceptedPaymentMethods());
            provider.setServicePoints(currentProvider.getServicePoints());
            provider.setSuppliedServiceOffers(currentProvider.getSuppliedServiceOffers());
            provider.setReceivedRatings(currentProvider.getReceivedRatings());
        }

        Provider updatedProvider = null;
        try {
            // reflect updated resource object in database
            updatedProvider = providerFacade.update(provider);
            // populate created resource with hypermedia links
            ProviderResource.populateWithHATEOASLinks(updatedProvider, params.getUriInfo());

        } catch(EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch(EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch(Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_UPDATE_ERROR_MESSAGE);
        }

        return Response.status(Status.OK).entity(updatedProvider).build();
    }

    /**
     * Method that removes Provider entity from database for given ID.
     * The ID is passed through path param.
     */
    @DELETE
    @Path("/{userId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeProvider( @PathParam("userId") Long userId,
                                    @HeaderParam("authToken") String authToken) throws ForbiddenException, NotFoundException {

        if(authToken == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "removing given Provider by executing ProviderResource.removeProvider(userId) method of REST API");

        // find Provider entity that should be deleted
        Provider toDeleteProvider = providerFacade.find(userId);
        // throw exception if entity hasn't been found
        if(toDeleteProvider == null)
            throw new NotFoundException("Could not find provider to delete for given id: " + userId + ".");

        // remove entity form database
        providerFacade.remove(toDeleteProvider);

        return  Response.status(Status.NO_CONTENT).build();
    }

    /**
     * Additional methods returning a subset of resources based on given criteria
     * you can also achieve similar results by applying @QueryParams to generic method
     * returning all resources in order to filter and limit them
     */

    /**
     * Method returns number of Provider entities in database
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countProviders( @HeaderParam("authToken") String authToken ) throws ForbiddenException {

        if(authToken == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning number of providers by executing ProviderResource.countProviders() method of REST API");

        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(providerFacade.count()), 200, "number of providers");
        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method returns subset of Provider entities for given corporation entity.
     * The corporation id is passed through path param.
     */
    @GET
    @Path("/corporation/{corporationId : \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProvidersByCorporation( @PathParam("corporationId") Long corporationId,
                                               @BeanParam PaginationBeanParam params) throws ForbiddenException, NotFoundException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning providers for given corporation using ProviderResource.getProvidersByCorporation(corporationId) method of REST API");

        // get corporation for which to look for providers
        Corporation corporation = corporationFacade.find(corporationId);
        if(corporation == null)
            throw new NotFoundException("Could not find corporation for which to look for providers.");

        // find providers by given criteria
        ResourceList<Provider> providers = new ResourceList<>( providerFacade.findByCorporation(corporation, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(providers).build();
    }

    /**
     * Method returns subset of Provider entities for given provider type.
     * The provider type is passed through path param as a string mapped to ProviderType.
     */
    @GET
    @Path("/typed/{type : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProvidersByType( @PathParam("type") ProviderType providerType,
                                        @BeanParam PaginationBeanParam params) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning providers for given provider type using ProviderResource.getProvidersByType(providerType) method of REST API");

        // find providers by given criteria
        ResourceList<Provider> providers = new ResourceList<>( providerFacade.findByType(providerType, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(providers).build();
    }

    /**
     *  Method returns subset of Provider entities for given industry entity.
     *  The industry id is passed through path param.
     */
    @GET
    @Path("/industry/{industryId : \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProvidersByIndustry( @PathParam("industryId") Long industryId,
                                            @BeanParam PaginationBeanParam params) throws ForbiddenException, NotFoundException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning providers for given industry using ProviderResource.getProvidersByIndustry(industryId) method of REST API");

        // get industry for which to look for providers
        Industry industry = industryFacade.find(industryId);
        if(industry == null)
            throw new NotFoundException("Could not find industry for which to look for providers.");

        // find providers by given criteria
        ResourceList<Provider> providers = new ResourceList<>( providerFacade.findByIndustry(industry, params.getOffset(), params.getLimit()));

        // result resources need to be populated with hypermedia links to enable resource discovery
        ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(providers).build();
    }

    /**
     * Method returns subset of Provider entities for given payment method entity.
     * The payment method id is passed through path param.
     */
    @GET
    @Path("/accepting-payment-method/{paymentMethodId : \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProvidersAcceptingPaymentMethod( @PathParam("paymentMethodId") Integer paymentMethodId,
                                                        @BeanParam PaginationBeanParam params) throws ForbiddenException, NotFoundException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning providers for given payment method using ProviderResource.getProvidersAcceptingPaymentMethod(paymentMethodId) method of REST API");

        // get payment method for which to look for providers
        PaymentMethod paymentMethod = paymentMethodFacade.find(paymentMethodId);
        if(paymentMethod == null)
            throw new NotFoundException("Could not find payment method for which to look for providers.");

        // find providers by given criteria
        ResourceList<Provider> providers = new ResourceList<>( providerFacade.findByPaymentMethod(paymentMethod, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(providers).build();
    }

    /**
     * Method returns subset of Provider entities for given Service entity.
     * The service id is passed through path param.
     */
    @GET
    @Path("/supplying-service/{serviceId : \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProvidersSupplyingService( @PathParam("serviceId") Integer serviceId,
                                                  @BeanParam PaginationBeanParam params) throws ForbiddenException, NotFoundException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning providers for given service using ProviderResource.getProvidersSupplyingService(serviceId) method of REST API");

        // get service for which to look for providers
        Service service = serviceFacade.find(serviceId);
        if(service == null)
            throw new NotFoundException("Could not find service for which to look for providers.");

        // find providers by given criteria
        ResourceList<Provider> providers = new ResourceList<>( providerFacade.findBySuppliedService(service, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(providers).build();
    }

    /**
     * Method returns subset of Provider entities that have been rated
     */
    @GET
    @Path("/rated")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProvidersRated( @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning providers that have been rated using ProviderResource.getProvidersRated() method of REST API");

        // find providers by given criteria
        ResourceList<Provider> providers = new ResourceList<>( providerFacade.findRated(params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(providers).build();
    }

    /**
     * Method returns subset of Provider entities that haven't been rated (unrated)
     */
    @GET
    @Path("/unrated")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProvidersUnrated( @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning providers that haven't been rated (unrated) using ProviderResource.getProvidersUnrated() method of REST API");

        // find providers by given criteria
        ResourceList<Provider> providers = new ResourceList<>( providerFacade.findUnrated(params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(providers).build();
    }

    /**
     * Method returns subset of Provider entities that have been rated above given value.
     * The min avg rating is passed through path param.
     */
    @GET
    @Path("/rated-above/{minAvgRating :  \\d+(\\.\\d+)?}") // catch only unsigned double numbers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProvidersOnAverageRatedAbove( @PathParam("minAvgRating") Double minAvgRating,
                                                     @BeanParam PaginationBeanParam params) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning providers on average rated above given minimal value using ProviderResource.getProvidersOnAverageRatedAbove(minAvgRating) method of REST API");

        // find providers by given criteria
        ResourceList<Provider> providers = new ResourceList<>( providerFacade.findOnAvgRatedAbove(minAvgRating, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(providers).build();
    }

    /**
     * Method returns subset of Provider entities that have been rated below given value.
     * The max avg rating is passed through path param.
     */
    @GET
    @Path("/rated-below/{maxAvgRating :  \\d+(\\.\\d+)?}") // catch only unsigned double numbers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProvidersOnAverageRatedBelow( @PathParam("maxAvgRating") Double maxAvgRating,
                                                     @BeanParam PaginationBeanParam params) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning providers on average rated below given maximal value using ProviderResource.getProvidersOnAverageRatedBelow(maxAvgRating) method of REST API");

        // find providers by given criteria
        ResourceList<Provider> providers = new ResourceList<>( providerFacade.findOnAvgRatedBelow(maxAvgRating, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(providers).build();
    }

    /**
     * Method returns subset of Provider entities that have been rated by given Client
     * The client id is passed through path param.
     */
    @GET
    @Path("/rated-by/{clientId : \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProvidersRatedByClient( @PathParam("clientId") Long clientId,
                                               @BeanParam PaginationBeanParam params) throws ForbiddenException, NotFoundException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning providers rated by given client using ProviderResource.getProvidersRatedByClient(clientId) method of REST API");

        // get client for which to look for providers
        Client client = clientFacade.find(clientId);
        if(client == null)
            throw new NotFoundException("Could not find client for which to look for providers.");

        // find providers by given criteria
        ResourceList<Provider> providers = new ResourceList<>( providerFacade.findRatedByClient(client, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(providers).build();
    }

    /**
     * related subresources (through relationships)
     */

    @Path("/{userId: \\d+}/industries")
    public IndustryResource getIndustryResource() {
        return new IndustryResource();
    }

    @Path("/{userId: \\d+}/payment-methods")
    public PaymentMethodResource getPaymentMethodResource() {
        return new PaymentMethodResource();
    }

    @Path("/{userId: \\d+}/service-points")
    public ServicePointResource getServicePointResource() {
        return new ServicePointResource();
    }

    @Path("/{userId: \\d+}/provider-services")
    public ProviderServiceResource getProviderServiceResource() {
        return new ProviderServiceResource();
    }

    @Path("/{userId: \\d+}/provider-ratings")
    public ProviderRatingResource getProviderRatingResource() {
        return new ProviderRatingResource();
    }

    // private helper methods e.g. to populate resources/resource lists with HATEOAS links

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList providers, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        if(offset != null && limit != null) {
            // self collection link
            providers.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", offset).queryParam("limit", limit).build()).rel("self").build() );
            // prev collection link
            Integer prevOffset = (offset - limit) < 0 ? 0 : offset - limit;
            Integer prevLimit = offset - prevOffset;
            if(prevLimit > 0)
                providers.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", prevOffset).queryParam("limit", prevLimit).build()).rel("prev").build() );
            else
                providers.getLinks().add( Link.fromUri("").rel("prev").build() );
            // next collection link
            providers.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", (offset+limit)).queryParam("limit", limit).build()).rel("next").build() );
        } else {
            providers.getLinks().add( Link.fromUri(uriInfo.getAbsolutePath()).rel("self").build() );
        }

        try {

            // count resources hypermedia link
            Method countMethod = ProviderResource.class.getMethod("countProviders", String.class);
            providers.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            providers.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderResource.class).build()).rel("providers").build() );

            // get all resources eagerly hypermedia link
            Method providersEagerlyMethod = ProviderResource.class.getMethod("getProvidersEagerly", ProviderBeanParam.class);
            providers.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderResource.class).path(providersEagerlyMethod).build()).rel("providers-eagerly").build() );

            // get subset of resources hypermedia links
            // corporation
            providers.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderResource.class).path("corporation").build()).rel("corporation").build() );

            // typed
            providers.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderResource.class).path("typed").build()).rel("typed").build() );

            // industry
            providers.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderResource.class).path("industry").build()).rel("industry").build() );

            // accepting-payment-method
            providers.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderResource.class).path("accepting-payment-method").build()).rel("accepting-payment-method").build() );

            // supplying-service
            providers.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderResource.class).path("supplying-service").build()).rel("supplying-service").build() );

            // rated
            Method ratedMethod = ProviderResource.class.getMethod("getProvidersRated", PaginationBeanParam.class);
            providers.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderResource.class).path(ratedMethod).build()).rel("rated").build() );

            // unrated
            Method unratedMethod = ProviderResource.class.getMethod("getProvidersUnrated", PaginationBeanParam.class);
            providers.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderResource.class).path(unratedMethod).build()).rel("unrated").build() );

            // rated-above
            providers.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderResource.class).path("rated-above").build()).rel("rated-above").build() );

            // rated-below
            providers.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderResource.class).path("rated-below").build()).rel("rated-below").build() );

            // rated-by
            providers.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderResource.class).path("rated-by").build()).rel("rated-by").build() );

        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        }

        for(Object object : providers.getResources()) {
            if(object instanceof Provider) {
                ProviderResource.populateWithHATEOASLinks((Provider) object, uriInfo);
            } else if(object instanceof ProviderWrapper) {
                ProviderResource.populateWithHATEOASLinks( (ProviderWrapper) object, uriInfo);
            }
        }
    }

    /**
     * This method enables to populate each individual resource wrapper with hypermedia links
     */
    public static void populateWithHATEOASLinks(ProviderWrapper providerWrapper, UriInfo uriInfo) {

        ProviderResource.populateWithHATEOASLinks(providerWrapper.getProvider(), uriInfo);

        for(Industry industry : providerWrapper.getIndustries() )
            pl.salonea.jaxrs.IndustryResource.populateWithHATEOASLinks(industry, uriInfo);

        for(PaymentMethod paymentMethod : providerWrapper.getAcceptedPaymentMethods())
            pl.salonea.jaxrs.PaymentMethodResource.populateWithHATEOASLinks(paymentMethod, uriInfo);

        for(ServicePoint servicePoint : providerWrapper.getServicePoints())
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoint, uriInfo);

        for(ProviderService providerService : providerWrapper.getSuppliedServiceOffers())
            ProviderServiceResource.populateWithHATEOASLinks(providerService, uriInfo);

        for(ProviderRating providerRating : providerWrapper.getReceivedRatings())
            ProviderRatingResource.populateWithHATEOASLinks(providerRating, uriInfo);
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(Provider provider, UriInfo uriInfo) {

        // self link with pattern: http://localhost:port/app/rest/{resources}/{id}
        provider.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                    .path(ProviderResource.class)
                                                    .path(provider.getUserId().toString())
                                                    .build())
                                    .rel("self").build() );

        // collection link with pattern: http://localhost:port/app/rest/{resources}
        provider.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                    .path(ProviderResource.class)
                                                    .build())
                                     .rel("providers").build());


        try {
            // self eagerly link with pattern http://localhost:port/app/rest/{resources}/{id}/eagerly
            Method providerEagerlyMethod = ProviderResource.class.getMethod("getProviderEagerly", Long.class, GenericBeanParam.class);
            provider.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                        .path(ProviderResource.class)
                                                        .path(providerEagerlyMethod)
                                                        .resolveTemplate("userId", provider.getUserId().toString())
                                                        .build())
                                        .rel("provider-eagerly").build());

            // associated collections links with pattern: http://localhost:port/app/rest/{resources}/{id}/{relationship}

            // industries relationship
            Method industriesMethod = ProviderResource.class.getMethod("getIndustryResource");
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                                                        .path(ProviderResource.class)
                                                        .path(industriesMethod)
                                                        .resolveTemplate("userId", provider.getUserId().toString())
                                                        .build())
                                .rel("industries").build());

            // payment-methods relationship
            Method paymentMethodsMethod = ProviderResource.class.getMethod("getPaymentMethodResource");
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                                                        .path(ProviderResource.class)
                                                        .path(paymentMethodsMethod)
                                                        .resolveTemplate("userId", provider.getUserId().toString())
                                                        .build())
                                .rel("payment-methods").build());

            // service-points relationship
            Method servicePointsMethod = ProviderResource.class.getMethod("getServicePointResource");
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                                                        .path(ProviderResource.class)
                                                        .path(servicePointsMethod)
                                                        .resolveTemplate("userId", provider.getUserId().toString())
                                                        .build())
                                .rel("service-points").build());

            // provider-services
            Method providerServicesMethod = ProviderResource.class.getMethod("getProviderServiceResource");
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                                                        .path(ProviderResource.class)
                                                        .path(providerServicesMethod)
                                                        .resolveTemplate("userId", provider.getUserId().toString())
                                                        .build())
                                .rel("provider-services").build());

            // provider-ratings
            Method providerRatingsMethod = ProviderResource.class.getMethod("getProviderRatingResource");
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                                                        .path(ProviderResource.class)
                                                        .path(providerRatingsMethod)
                                                        .resolveTemplate("userId", provider.getUserId().toString())
                                                        .build())
                                .rel("provider-ratings").build());

        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public class IndustryResource {

        /**
         * Method returns subset of Industry entities for given Provider.
         * The provider id is passed through path param.
         * They can be additionally paginated by @QueryParams
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderIndustries( @PathParam("userId") Long userId,
                                               @BeanParam PaginationBeanParam params) throws NotFoundException, ForbiddenException {

            if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning subset of Industry entities for given Provider using ProviderResource.IndustryResource.getProviderIndustries(userId) method of REST API");

            // find provider entity for which to get associated industries
            Provider provider = providerFacade.find(userId);
            if(provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            // get industries for given provider
            ResourceList<Industry> industries = new ResourceList<>( industryFacade.findByProvider(provider, params.getOffset(), params.getLimit()) );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.IndustryResource.populateWithHATEOASLinks(industries, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(industries).build();
        }
    }

    public class PaymentMethodResource {

        /**
         * Method returns subset of PaymentMethod entities for given Provider.
         * The provider id is passed through path param.
         * They can be additionally paginated by @QueryParams
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderPaymentMethods( @PathParam("userId") Long userId,
                                                   @BeanParam PaginationBeanParam params) throws NotFoundException, ForbiddenException {

            if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning subset of PaymentMethod entities for given Provider using ProviderResource.PaymentMethodResource.getProviderPaymentMethods(userId) method of REST API");

            // find provider entity for which to get associated payment methods
            Provider provider = providerFacade.find(userId);
            if(provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            // get payment methods for given provider
            ResourceList<PaymentMethod> paymentMethods = new ResourceList<>( paymentMethodFacade.findByProvider(provider, params.getOffset(), params.getLimit()) );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.PaymentMethodResource.populateWithHATEOASLinks(paymentMethods, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(paymentMethods).build();
        }

    }

    public class ServicePointResource {

        /**
         * Method returns subset of ServicePoint entities for given Provider
         * The provider id is passed through path param.
         * They can be additionally paginated by @QueryParams
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServicePoints( @PathParam("userId") Long userId,
                                                  @BeanParam ServicePointBeanParam params) throws NotFoundException, ForbiddenException {

            if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning subset of ServicePoint entities for given Provider using ProviderResource.ServicePointResource.getProviderServicePoints(userId) method of REST API");

            // find provider entity for which to get associated service points
            Provider provider = providerFacade.find(userId);
            if(provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            // calculate number of filter query params
            Integer noOfParams = params.getUriInfo().getQueryParameters().size();
            if(params.getOffset() != null) noOfParams -= 1;
            if(params.getLimit() != null) noOfParams -= 1;

            ResourceList<ServicePoint> servicePoints = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Provider> providers = new ArrayList<>();
                providers.add(provider);

                if(params.getAddress() != null) {
                    if(params.getCoordinatesSquare() != null || params.getCoordinatesCircle() != null)
                        throw new BadRequestException("Query params cannot include address params and coordinates square params or coordinates circle params at the same time.");
                    // only address params
                    servicePoints = new ResourceList<>(
                        servicePointFacade.findByMultipleCriteria(providers, params.getServices(), params.getEmployees(),
                                params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getAddress(), params.getOffset(), params.getLimit())
                    );

                } else if(params.getCoordinatesSquare() != null) {
                    if(params.getAddress() != null || params.getCoordinatesCircle() != null)
                        throw new BadRequestException("Query params cannot include coordinates square params and address params or coordinates circle params at the same time.");
                    // only coordinates square params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(providers, params.getServices(), params.getEmployees(),
                                    params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesSquare(), params.getOffset(), params.getLimit())
                    );

                } else if(params.getCoordinatesCircle() != null) {
                    if(params.getAddress() != null || params.getCoordinatesSquare() != null)
                        throw new BadRequestException("Query params cannot include coordinates circle params and address params or coordinates square params at the same time.");
                    // only coordinates circle params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(providers, params.getServices(), params.getEmployees(),
                                    params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesCircle(), params.getOffset(), params.getLimit())
                    );

                } else {
                    // no location params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(providers, params.getServices(), params.getEmployees(),
                                    params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getOffset(), params.getLimit())
                    );
                }

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                servicePoints = new ResourceList<>( servicePointFacade.findByProvider(provider, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(servicePoints).build();
        }

    }

}
