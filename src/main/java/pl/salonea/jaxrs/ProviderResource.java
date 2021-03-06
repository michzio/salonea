package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.*;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.*;
import pl.salonea.entities.Transaction;
import pl.salonea.entities.idclass.ProviderServiceId;
import pl.salonea.entities.idclass.ServicePointId;
import pl.salonea.entities.idclass.WorkStationId;
import pl.salonea.enums.ClientType;
import pl.salonea.enums.ProviderType;
import pl.salonea.enums.WorkStationType;
import pl.salonea.jaxrs.bean_params.*;
import pl.salonea.jaxrs.exceptions.*;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.exceptions.BadRequestException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.RequestWrapper;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.*;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
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
    private UserTransaction utx;

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
    @Inject
    private WorkStationFacade workStationFacade;
    @Inject
    private ProviderServiceFacade providerServiceFacade;
    @Inject
    private EmployeeFacade employeeFacade;
    @Inject
    private ServiceCategoryFacade serviceCategoryFacade;
    @Inject
    private ProviderRatingFacade providerRatingFacade;
    @Inject
    private ServicePointPhotoFacade servicePointPhotoFacade;
    @Inject
    private VirtualTourFacade virtualTourFacade;
    @Inject
    private TransactionFacade transactionFacade;
    @Inject
    private HistoricalTransactionFacade historicalTransactionFacade;

    /**
     * Method returns all Provider resources
     * They can be additionally filtered or paginated by @QueryParams
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProviders(@BeanParam ProviderBeanParam params) throws ForbiddenException, NotFoundException {

        if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning all Providers by executing ProviderResource.getProviders() method of REST API");

        // calculate number of filter query params
        Integer noOfParams = params.getUriInfo().getQueryParameters().size();
        if (params.getOffset() != null) noOfParams -= 1;
        if (params.getLimit() != null) noOfParams -= 1;

        ResourceList<Provider> providers = null;

        if (noOfParams > 0) {
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

        if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning all Providers eagerly by executing ProviderResource.getProvidersEagerly() method of REST API");

        // calculate number of filter query params
        Integer noOfParams = params.getUriInfo().getQueryParameters().size();
        if (params.getOffset() != null) noOfParams -= 1;
        if (params.getLimit() != null) noOfParams -= 1;

        ResourceList<ProviderWrapper> providers = null;

        if (noOfParams > 0) {
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
            providers = new ResourceList<>(ProviderWrapper.wrap(providerFacade.findAllEagerly(params.getOffset(), params.getLimit())));
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(providers).build();
    }

    /**
     * Method matches specific Provider resource by identifier and returns its instance.
     */
    @GET
    @Path("/{userId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProvider(@PathParam("userId") Long userId,
                                @BeanParam GenericBeanParam params) throws NotFoundException, ForbiddenException {

        if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning given Provider by executing ProviderResource.getProvider(userId) method of REST API");

        Provider foundProvider = providerFacade.find(userId);
        if (foundProvider == null)
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
    public Response getProviderEagerly(@PathParam("userId") Long userId,
                                       @BeanParam GenericBeanParam params) throws NotFoundException, ForbiddenException {

        if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning given Provider eagerly by executing ProviderResource.getProviderEagerly(userId) method of REST API");

        Provider foundProvider = providerFacade.findByIdEagerly(userId);
        if (foundProvider == null)
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

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
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
    public Response updateProvider(@PathParam("userId") Long userId,
                                   Provider provider,
                                   @BeanParam GenericBeanParam params) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "updating existing Provider by executing ProviderResource.updateProvider(userId, provider) method of REST API");

        // set resource ID passed in path param on updated resource object
        provider.setUserId(userId);

        Provider updatedProvider = null;
        try {
            // reflect updated resource object in database
            updatedProvider = providerFacade.update(provider, true);
            // populate created resource with hypermedia links
            ProviderResource.populateWithHATEOASLinks(updatedProvider, params.getUriInfo());

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
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
    public Response removeProvider(@PathParam("userId") Long userId,
                                   @HeaderParam("authToken") String authToken) throws ForbiddenException, NotFoundException {

        if (authToken == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "removing given Provider by executing ProviderResource.removeProvider(userId) method of REST API");

        // find Provider entity that should be deleted
        Provider toDeleteProvider = providerFacade.find(userId);
        // throw exception if entity hasn't been found
        if (toDeleteProvider == null)
            throw new NotFoundException("Could not find provider to delete for given id: " + userId + ".");

        // remove entity from database
        providerFacade.remove(toDeleteProvider);

        return Response.status(Status.NO_CONTENT).build();
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
    public Response countProviders(@HeaderParam("authToken") String authToken) throws ForbiddenException {

        if(authToken == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning number of providers by executing ProviderResource.countProviders() method of REST API");

        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(providerFacade.count()), 200, "number of providers");
        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method returns subset of Provider entities for given provider type.
     * The provider type is passed through path param as a string mapped to ProviderType.
     */
    @GET
    @Path("/typed/{type : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProvidersByType(@PathParam("type") ProviderType providerType,
                                       @BeanParam PaginationBeanParam params) throws ForbiddenException {

        if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning providers for given provider type using ProviderResource.getProvidersByType(providerType) method of REST API");

        // find providers by given criteria
        ResourceList<Provider> providers = new ResourceList<>(providerFacade.findByType(providerType, params.getOffset(), params.getLimit()));

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
    public Response getProvidersRated(@BeanParam PaginationBeanParam params) throws ForbiddenException {

        if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning providers that have been rated using ProviderResource.getProvidersRated() method of REST API");

        // find providers by given criteria
        ResourceList<Provider> providers = new ResourceList<>(providerFacade.findRated(params.getOffset(), params.getLimit()));

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
    public Response getProvidersUnrated(@BeanParam PaginationBeanParam params) throws ForbiddenException {

        if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning providers that haven't been rated (unrated) using ProviderResource.getProvidersUnrated() method of REST API");

        // find providers by given criteria
        ResourceList<Provider> providers = new ResourceList<>(providerFacade.findUnrated(params.getOffset(), params.getLimit()));

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
    public Response getProvidersOnAverageRatedAbove(@PathParam("minAvgRating") Double minAvgRating,
                                                    @BeanParam PaginationBeanParam params) throws ForbiddenException {

        if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning providers on average rated above given minimal value using ProviderResource.getProvidersOnAverageRatedAbove(minAvgRating) method of REST API");

        // find providers by given criteria
        ResourceList<Provider> providers = new ResourceList<>(providerFacade.findOnAvgRatedAbove(minAvgRating, params.getOffset(), params.getLimit()));

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
    public Response getProvidersOnAverageRatedBelow(@PathParam("maxAvgRating") Double maxAvgRating,
                                                    @BeanParam PaginationBeanParam params) throws ForbiddenException {

        if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning providers on average rated below given maximal value using ProviderResource.getProvidersOnAverageRatedBelow(maxAvgRating) method of REST API");

        // find providers by given criteria
        ResourceList<Provider> providers = new ResourceList<>(providerFacade.findOnAvgRatedBelow(maxAvgRating, params.getOffset(), params.getLimit()));

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
    @Path("/{userId: \\d+}/rating-clients")
    public ClientResource getClientResource() { return new ClientResource(); }
    @Path("/{userId: \\d+}/service-point-photos")
    public ServicePointPhotoResource getServicePointPhotoResource()  {
        return new ServicePointPhotoResource();
    }
    @Path("/{userId: \\d+}/virtual-tours")
    public VirtualTourResource getVirtualTourResource() { return new VirtualTourResource(); }
    @Path("/{userId: \\d+}/services")
    public ServiceResource getServiceResource() { return new ServiceResource(); }
    @Path("/{userId: \\d+}/transactions")
    public TransactionResource getTransactionResource() { return new TransactionResource(); }
    @Path("/{userId: \\d+}/historical-transactions")
    public HistoricalTransactionResource getHistoricalTransactionResource() { return new HistoricalTransactionResource(); }

    // helper methods e.g. to populate resources/resource lists with HATEOAS links

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList providers, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(providers, uriInfo, offset, limit);

        try {

            // count resources hypermedia link
            Method countMethod = ProviderResource.class.getMethod("countProviders", String.class);
            providers.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderResource.class).path(countMethod).build()).rel("count").build());

            // get all resources hypermedia link
            providers.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderResource.class).build()).rel("providers").build());

            // get all resources eagerly hypermedia link
            Method providersEagerlyMethod = ProviderResource.class.getMethod("getProvidersEagerly", ProviderBeanParam.class);
            providers.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderResource.class).path(providersEagerlyMethod).build()).rel("providers-eagerly").build());

            // get subset of resources hypermedia links
            // typed
            providers.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderResource.class).path("typed").build()).rel("typed").build());

            // rated
            Method ratedMethod = ProviderResource.class.getMethod("getProvidersRated", PaginationBeanParam.class);
            providers.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderResource.class).path(ratedMethod).build()).rel("rated").build());

            // unrated
            Method unratedMethod = ProviderResource.class.getMethod("getProvidersUnrated", PaginationBeanParam.class);
            providers.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderResource.class).path(unratedMethod).build()).rel("unrated").build());

            // rated-above
            providers.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderResource.class).path("rated-above").build()).rel("rated-above").build());

            // rated-below
            providers.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderResource.class).path("rated-below").build()).rel("rated-below").build());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for (Object object : providers.getResources()) {
            if (object instanceof Provider) {
                ProviderResource.populateWithHATEOASLinks((Provider) object, uriInfo);
            } else if (object instanceof ProviderWrapper) {
                ProviderResource.populateWithHATEOASLinks((ProviderWrapper) object, uriInfo);
            }
        }
    }

    /**
     * This method enables to populate each individual resource wrapper with hypermedia links
     */
    public static void populateWithHATEOASLinks(ProviderWrapper providerWrapper, UriInfo uriInfo) {

        ProviderResource.populateWithHATEOASLinks(providerWrapper.getProvider(), uriInfo);

        for (Industry industry : providerWrapper.getIndustries())
            pl.salonea.jaxrs.IndustryResource.populateWithHATEOASLinks(industry, uriInfo);

        for (PaymentMethod paymentMethod : providerWrapper.getAcceptedPaymentMethods())
            pl.salonea.jaxrs.PaymentMethodResource.populateWithHATEOASLinks(paymentMethod, uriInfo);

        for (ServicePoint servicePoint : providerWrapper.getServicePoints())
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoint, uriInfo);

        for (ProviderService providerService : providerWrapper.getSuppliedServiceOffers())
            pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(providerService, uriInfo);

        for (ProviderRating providerRating : providerWrapper.getReceivedRatings())
            pl.salonea.jaxrs.ProviderRatingResource.populateWithHATEOASLinks(providerRating, uriInfo);
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(Provider provider, UriInfo uriInfo) {

        // self link with pattern: http://localhost:port/app/rest/{resources}/{id}
        provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                .path(ProviderResource.class)
                .path(provider.getUserId().toString())
                .build())
                .rel("self").build());

        // collection link with pattern: http://localhost:port/app/rest/{resources}
        provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                .path(ProviderResource.class)
                .build())
                .rel("providers").build());

        try {
            // self eagerly link with pattern http://localhost:port/app/rest/{resources}/{id}/eagerly
            Method providerEagerlyMethod = ProviderResource.class.getMethod("getProviderEagerly", Long.class, GenericBeanParam.class);
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerEagerlyMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("provider-eagerly").build());

            // associated collections links with pattern: http://localhost:port/app/rest/{resources}/{id}/{relationship}

            /**
             * Industries associated with current Provider resource
             */

            // industries relationship
            Method industriesMethod = ProviderResource.class.getMethod("getIndustryResource");
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(industriesMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("industries").build());

            // industries eagerly relationship
            Method industriesEagerlyMethod = ProviderResource.IndustryResource.class.getMethod("getProviderIndustriesEagerly", Long.class, IndustryBeanParam.class);
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(industriesMethod)
                    .path(industriesEagerlyMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("industries-eagerly").build());

            // industries count
            Method countIndustriesByProviderMethod = ProviderResource.IndustryResource.class.getMethod("countIndustriesByProvider", Long.class, GenericBeanParam.class);
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(industriesMethod)
                    .path(countIndustriesByProviderMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("industries-count").build());

            /**
             * Payment Methods associated with current Provider resource
             */

            // payment-methods relationship
            Method paymentMethodsMethod = ProviderResource.class.getMethod("getPaymentMethodResource");
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(paymentMethodsMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("payment-methods").build());

            // payment-methods eagerly relationship
            Method paymentMethodsEagerlyMethod = ProviderResource.PaymentMethodResource.class.getMethod("getProviderPaymentMethodsEagerly", Long.class, PaymentMethodBeanParam.class);
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(paymentMethodsMethod)
                    .path(paymentMethodsEagerlyMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("payment-methods-eagerly").build());

            // payment-methods count
            Method countPaymentMethodsByProviderMethod = ProviderResource.PaymentMethodResource.class.getMethod("countPaymentMethodsByProvider", Long.class, GenericBeanParam.class);
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(paymentMethodsMethod)
                    .path(countPaymentMethodsByProviderMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("payment-methods-count").build());

            /**
             * Service Points associated with current Provider resource
             */

            // service-points link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}
            Method servicePointsMethod = ProviderResource.class.getMethod("getServicePointResource");
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(servicePointsMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("service-points").build());

            // service-points eagerly link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/eagerly
            Method servicePointsEagerlyMethod = ProviderResource.ServicePointResource.class.getMethod("getProviderServicePointsEagerly", Long.class, ServicePointBeanParam.class);
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(servicePointsMethod)
                    .path(servicePointsEagerlyMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("service-points-eagerly").build());


            // service-points count link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/count
            Method countServicePointsByProviderMethod = ProviderResource.ServicePointResource.class.getMethod("countServicePointsByProvider", Long.class, GenericBeanParam.class);
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(servicePointsMethod)
                    .path(countServicePointsByProviderMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("service-points-count").build());

            // service-points address link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/address
            Method addressMethod = ProviderResource.ServicePointResource.class.getMethod("getProviderServicePointsByAddress", Long.class, AddressBeanParam.class);
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(servicePointsMethod)
                    .path(addressMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("service-points-address").build());

            // service-points coordinates square link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/coordinates-square
            Method coordinatesSquareMethod = ProviderResource.ServicePointResource.class.getMethod("getProviderServicePointsByCoordinatesSquare", Long.class, CoordinatesSquareBeanParam.class);
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(servicePointsMethod)
                    .path(coordinatesSquareMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("service-points-coordinates-square").build());

            // service-points coordinates circle link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/coordinates-circle
            Method coordinatesCircleMethod = ProviderResource.ServicePointResource.class.getMethod("getProviderServicePointsByCoordinatesCircle", Long.class, CoordinatesCircleBeanParam.class);
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(servicePointsMethod)
                    .path(coordinatesCircleMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("service-points-coordinates-circle").build());

            /**
             * Provider Services associated with current Provider resource
             */

            // provider-services link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}
            Method providerServicesMethod = ProviderResource.class.getMethod("getProviderServiceResource");
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerServicesMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("provider-services").build());

            // provider-services eagerly link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/eagerly
            Method providerServicesEagerlyMethod = ProviderResource.ProviderServiceResource.class.getMethod("getProviderServicesEagerly", Long.class, ProviderServiceBeanParam.class);
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerServicesMethod)
                    .path(providerServicesEagerlyMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("provider-services-eagerly").build());

            // provider-services count link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/count
            Method countProviderServicesByProviderMethod = ProviderResource.ProviderServiceResource.class.getMethod("countProviderServicesByProvider", Long.class, GenericBeanParam.class);
            provider.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerServicesMethod)
                    .path(countProviderServicesByProviderMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("provider-services-count").build());

            // provider-services categorized link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/categorized-in/{categoryId}
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerServicesMethod)
                    .path("categorized-in")
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("provider-services-categorized").build());

            // provider-services described link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/described-by/{description}
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerServicesMethod)
                    .path("described-by")
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("provider-services-described").build());

            // provider-services discounted link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/discounted-between?minDiscount={minDiscount}&maxDiscount={maxDiscount}
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerServicesMethod)
                    .path("discounted-between")
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("provider-services-discounted").build());

            // provider-services supplied-by-employee link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/supplied-by/{employeeId}
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerServicesMethod)
                    .path("supplied-by")
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("provider-services-supplied-by-employee").build());

            /**
             * Provider Ratings associated with current Provider resource
             */

            // provider-ratings
            Method providerRatingsMethod = ProviderResource.class.getMethod("getProviderRatingResource");
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerRatingsMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("provider-ratings").build());

            // provider-ratings count
            Method countProviderRatingsByProviderMethod = ProviderResource.ProviderRatingResource.class.getMethod("countProviderRatings", Long.class, GenericBeanParam.class);
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerRatingsMethod)
                    .path(countProviderRatingsByProviderMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("provider-ratings-count").build());

            // provider-ratings average-rating
            Method providerAverageRatingMethod = ProviderResource.ProviderRatingResource.class.getMethod("getAverageProviderRating", Long.class, GenericBeanParam.class);
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerRatingsMethod)
                    .path(providerAverageRatingMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("provider-ratings-average-rating").build());

            // provider-ratings rated
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerRatingsMethod)
                    .path("rated")
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("provider-ratings-rated").build());

            // provider-ratings rated-above
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerRatingsMethod)
                    .path("rated-above")
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("provider-ratings-rated-above").build());

            // provider-ratings rated-below
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerRatingsMethod)
                    .path("rated-below")
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("provider-ratings-rated-below").build());

            /**
             * Clients rating current Provider resource
             */

            // rating-clients
            Method ratingClientsMethod = ProviderResource.class.getMethod("getClientResource");
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(ratingClientsMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("rating-clients").build());

            // rating-clients eagerly
            Method ratingClientsEagerlyMethod = ProviderResource.ClientResource.class.getMethod("getProviderRatingClientsEagerly", Long.class, ClientBeanParam.class);
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(ratingClientsMethod)
                    .path(ratingClientsEagerlyMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("rating-clients-eagerly").build());

            // rating-clients count
            Method countClientsRatingProviderMethod = ProviderResource.ClientResource.class.getMethod("countClientsRatingProvider", Long.class, GenericBeanParam.class);
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(ratingClientsMethod)
                    .path(countClientsRatingProviderMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("rating-clients-count").build());

            /**
             * Service Point Photos related with current Provider resource
             */

            // service-point-photos
            Method servicePointPhotosMethod = ProviderResource.class.getMethod("getServicePointPhotoResource");
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(servicePointPhotosMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("service-point-photos").build());

            // service-point-photos eagerly
            Method servicePointPhotosEagerlyMethod = ProviderResource.ServicePointPhotoResource.class.getMethod("getProviderServicePointPhotosEagerly", Long.class, ServicePointPhotoBeanParam.class);
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(servicePointPhotosMethod)
                    .path(servicePointPhotosEagerlyMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("service-point-photos-eagerly").build());

            // service-point-photos count
            Method countServicePointPhotosByProviderMethod = ProviderResource.ServicePointPhotoResource.class.getMethod("countServicePointPhotosByProvider", Long.class, GenericBeanParam.class);
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(servicePointPhotosMethod)
                    .path(countServicePointPhotosByProviderMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("service-point-photos-count").build());

            /**
             * Virtual Tours related with current Provider resource
             */

            // virtual-tours
            Method virtualToursMethod = ProviderResource.class.getMethod("getVirtualTourResource");
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(virtualToursMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("virtual-tours").build());

            // virtual-tours eagerly
            Method virtualToursEagerlyMethod = ProviderResource.VirtualTourResource.class.getMethod("getProviderVirtualToursEagerly", Long.class, VirtualTourBeanParam.class);
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(virtualToursMethod)
                    .path(virtualToursEagerlyMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("virtual-tours-eagerly").build());

            // virtual-tours count
            Method countVirtualToursByProviderMethod = ProviderResource.VirtualTourResource.class.getMethod("countVirtualToursByProvider", Long.class, GenericBeanParam.class);
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(virtualToursMethod)
                    .path(countVirtualToursByProviderMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("virtual-tours-count").build());

            /**
             * Services provided by current Provider resource
             */

            // services
            Method servicesMethod = ProviderResource.class.getMethod("getServiceResource");
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(servicesMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("services").build());

            // services eagerly
            Method servicesEagerlyMethod = ProviderResource.ServiceResource.class.getMethod("getProviderServicesEagerly", Long.class, ServiceBeanParam.class);
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(servicesMethod)
                    .path(servicesEagerlyMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("services-eagerly").build());

            // services count
            Method countServicesByProviderMethod = ProviderResource.ServiceResource.class.getMethod("countServicesByProvider", Long.class, GenericBeanParam.class);
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(servicesMethod)
                    .path(countServicesByProviderMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("services-count").build());

            /**
             * Transactions associated with current Provider resource
             */
            // transactions
            Method transactionsMethod = ProviderResource.class.getMethod("getTransactionResource");
            provider.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(transactionsMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("transactions").build() );

            // transactions eagerly
            Method transactionsEagerlyMethod = ProviderResource.TransactionResource.class.getMethod("getProviderTransactionsEagerly", Long.class, TransactionBeanParam.class);
            provider.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(transactionsMethod)
                    .path(transactionsEagerlyMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("transactions-eagerly").build() );

            // transactions count
            Method countTransactionsByProviderMethod = ProviderResource.TransactionResource.class.getMethod("countTransactionsByProvider", Long.class, GenericBeanParam.class);
            provider.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(transactionsMethod)
                    .path(countTransactionsByProviderMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("transactions-count").build() );

            /**
             * Historical Transactions associated with current Provider resource
             */
            // historical-transactions
            Method historicalTransactionsMethod = ProviderResource.class.getMethod("getHistoricalTransactionResource");
            provider.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(historicalTransactionsMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("historical-transactions").build());

            // historical-transactions eagerly
            Method historicalTransactionsEagerlyMethod = ProviderResource.HistoricalTransactionResource.class.getMethod("getProviderHistoricalTransactionsEagerly", Long.class, HistoricalTransactionBeanParam.class);
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(historicalTransactionsMethod)
                    .path(historicalTransactionsEagerlyMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("historical-transactions-eagerly").build());

            // historical-transactions count
            Method countHistoricalTransactionsByProviderMethod = ProviderResource.HistoricalTransactionResource.class.getMethod("countHistoricalTransactionsByProvider", Long.class, GenericBeanParam.class);
            provider.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(historicalTransactionsMethod)
                    .path(countHistoricalTransactionsByProviderMethod)
                    .resolveTemplate("userId", provider.getUserId().toString())
                    .build())
                    .rel("historical-transactions-count").build());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public class IndustryResource {

        /**
         * Method returns subset of Industry entities for given Provider.
         * The provider id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderIndustries(@PathParam("userId") Long userId,
                                              @BeanParam IndustryBeanParam params) throws NotFoundException, ForbiddenException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning subset of Industry entities for given Provider using ProviderResource.IndustryResource.getProviderIndustries(userId) method of REST API");

            // find provider entity for which to get associated industries
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            // calculate number of filter query params
            Integer noOfParams = params.getUriInfo().getQueryParameters().size();
            if (params.getOffset() != null) noOfParams -= 1;
            if (params.getLimit() != null) noOfParams -= 1;

            ResourceList<Industry> industries = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Provider> providers = new ArrayList<>();
                providers.add(provider);

                // get industries for given provider filtered by given params
                industries = new ResourceList<>(
                        industryFacade.findByMultipleCriteria(providers, params.getName(), params.getDescription(),
                                params.getOffset(), params.getLimit())
                );

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get industries for given provider
                industries = new ResourceList<>(industryFacade.findByProvider(provider, params.getOffset(), params.getLimit()));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.IndustryResource.populateWithHATEOASLinks(industries, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(industries).build();
        }

        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderIndustriesEagerly(@PathParam("userId") Long userId,
                                                     @BeanParam IndustryBeanParam params) throws NotFoundException, ForbiddenException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning subset of Industry entities for given Provider eagerly using ProviderResource.IndustryResource.getProviderIndustriesEagerly(userId) method of REST API");

            // find provider entity for which to get associated industries
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            // calculate number of filter query params
            Integer noOfParams = params.getUriInfo().getQueryParameters().size();
            if (params.getOffset() != null) noOfParams -= 1;
            if (params.getLimit() != null) noOfParams -= 1;

            ResourceList<IndustryWrapper> industries = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Provider> providers = new ArrayList<>();
                providers.add(provider);

                // get industries for given provider eagerly filtered by given params
                industries = new ResourceList<>(
                        IndustryWrapper.wrap(
                                industryFacade.findByMultipleCriteriaEagerly(providers, params.getName(), params.getDescription(),
                                        params.getOffset(), params.getLimit())
                        )
                );
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get industries for given provider eagerly without filtering (eventually paginated)
                industries = new ResourceList<>(IndustryWrapper.wrap(industryFacade.findByProviderEagerly(provider, params.getOffset(), params.getLimit())));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.IndustryResource.populateWithHATEOASLinks(industries, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(industries).build();
        }

        /**
         * Method that counts Industry entities for given Provider.
         * The provider id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countIndustriesByProvider( @PathParam("userId") Long providerId,
                                                   @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of industries for given provider by executing " +
                    "ProviderResource.IndustryResource.countIndustriesByProvider(providerId) method of REST API");

            // find provider entity for which to count industries
            Provider provider = providerFacade.find(providerId);
            if(provider == null)
                throw new NotFoundException("Could not find provider for id " + providerId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(industryFacade.countByProvider(provider)),
                    200, "number of industries for provider with id " + provider.getUserId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    public class PaymentMethodResource {

        /**
         * Method returns subset of PaymentMethod entities for given Provider.
         * The provider id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderPaymentMethods(@PathParam("userId") Long userId,
                                                  @BeanParam PaymentMethodBeanParam params) throws NotFoundException, ForbiddenException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning subset of PaymentMethod entities for given Provider using ProviderResource.PaymentMethodResource.getProviderPaymentMethods(userId) method of REST API");

            // find provider entity for which to get associated payment methods
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            // calculate number of filter query params
            Integer noOfParams = params.getUriInfo().getQueryParameters().size();
            if (params.getOffset() != null) noOfParams -= 1;
            if (params.getLimit() != null) noOfParams -= 1;

            ResourceList<PaymentMethod> paymentMethods = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Provider> providers = new ArrayList<>();
                providers.add(provider);

                // get payment methods for given provider filtered by given params
                paymentMethods = new ResourceList<>(
                        paymentMethodFacade.findByMultipleCriteria(providers, params.getName(), params.getDescription(),
                                params.getInAdvance(), params.getOffset(), params.getLimit())

                );

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get payment methods for given provider
                paymentMethods = new ResourceList<>(paymentMethodFacade.findByProvider(provider, params.getOffset(), params.getLimit()));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.PaymentMethodResource.populateWithHATEOASLinks(paymentMethods, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(paymentMethods).build();
        }

        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderPaymentMethodsEagerly(@PathParam("userId") Long userId,
                                                         @BeanParam PaymentMethodBeanParam params) throws NotFoundException, ForbiddenException {
            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning subset of PaymentMethod entities for given Provider eagerly using ProviderResource.PaymentMethodResource.getProviderPaymentMethodsEagerly(userId) method of REST API");

            // find provider entity for which to get associated payment methods
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            // calculate number of filter query params
            Integer noOfParams = params.getUriInfo().getQueryParameters().size();
            if (params.getOffset() != null) noOfParams -= 1;
            if (params.getLimit() != null) noOfParams -= 1;

            ResourceList<PaymentMethodWrapper> paymentMethods = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Provider> providers = new ArrayList<>();
                providers.add(provider);

                // get payment methods for given provider eagerly filtered by given params
                paymentMethods = new ResourceList<>(
                        PaymentMethodWrapper.wrap(
                                paymentMethodFacade.findByMultipleCriteriaEagerly(providers, params.getName(), params.getDescription(), params.getInAdvance(),
                                        params.getOffset(), params.getLimit())
                        )
                );

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get payment methods for given provider eagerly without filtering (eventually paginated)
                paymentMethods = new ResourceList<>(PaymentMethodWrapper.wrap(paymentMethodFacade.findByProviderEagerly(provider, params.getOffset(), params.getLimit())));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.PaymentMethodResource.populateWithHATEOASLinks(paymentMethods, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(paymentMethods).build();
        }

        /**
         * Methods that counts Payment Method entities for given Provider.
         * The provider id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countPaymentMethodsByProvider( @PathParam("userId") Long providerId,
                                                       @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of payment methods for given provider by executing " +
                    "ProviderResource.PaymentMethodResource.countPaymentMethodsByProvider(providerId) method of REST API");

            // find provider entity for which to count payment methods
            Provider provider = providerFacade.find(providerId);
            if(provider == null)
                throw new NotFoundException("Could not find provider for id " + providerId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(paymentMethodFacade.countByProvider(provider)),
                    200, "number of payment methods for provider with id " + providerId);
            return Response.status(Status.OK).entity(responseEntity).build();
        }

    }

    public class ServicePointResource {

        public ServicePointResource() {
        }

        /**
         * Method returns subset of ServicePoint entities for given Provider
         * The provider id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServicePoints(@PathParam("userId") Long userId,
                                                 @BeanParam ServicePointBeanParam params) throws NotFoundException, ForbiddenException, BadRequestException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning subset of ServicePoint entities for given Provider using ProviderResource.ServicePointResource.getProviderServicePoints(userId) method of REST API");

            // find provider entity for which to get associated service points
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            // calculate number of filter query params
            Integer noOfParams = params.getUriInfo().getQueryParameters().size();
            if (params.getOffset() != null) noOfParams -= 1;
            if (params.getLimit() != null) noOfParams -= 1;

            ResourceList<ServicePoint> servicePoints = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Provider> providers = new ArrayList<>();
                providers.add(provider);

                utx.begin();

                if (params.getAddress() != null) {
                    if (params.getCoordinatesSquare() != null || params.getCoordinatesCircle() != null)
                        throw new BadRequestException("Query params cannot include address params and coordinates square params or coordinates circle params at the same time.");
                    // only address params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(providers, params.getServices(), params.getProviderServices(), params.getEmployees(),
                                    params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getAddress(), params.getTerms(), params.getOffset(), params.getLimit())
                    );

                } else if (params.getCoordinatesSquare() != null) {
                    if (params.getAddress() != null || params.getCoordinatesCircle() != null)
                        throw new BadRequestException("Query params cannot include coordinates square params and address params or coordinates circle params at the same time.");
                    // only coordinates square params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(providers, params.getServices(), params.getProviderServices(), params.getEmployees(),
                                    params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesSquare(), params.getTerms(), params.getOffset(), params.getLimit())
                    );

                } else if (params.getCoordinatesCircle() != null) {
                    if (params.getAddress() != null || params.getCoordinatesSquare() != null)
                        throw new BadRequestException("Query params cannot include coordinates circle params and address params or coordinates square params at the same time.");
                    // only coordinates circle params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(providers, params.getServices(), params.getProviderServices(), params.getEmployees(),
                                    params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesCircle(), params.getTerms(), params.getOffset(), params.getLimit())
                    );

                } else {
                    // no location params
                    servicePoints = new ResourceList<>(
                            servicePointFacade.findByMultipleCriteria(providers, params.getServices(), params.getProviderServices(), params.getEmployees(),
                                    params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getTerms(), params.getOffset(), params.getLimit())
                    );
                }

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                servicePoints = new ResourceList<>(servicePointFacade.findByProvider(provider, params.getOffset(), params.getLimit()));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(servicePoints).build();
        }

        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServicePointsEagerly(@PathParam("userId") Long userId,
                                                        @BeanParam ServicePointBeanParam params) throws NotFoundException, ForbiddenException, BadRequestException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning subset of ServicePoint entities for given Provider eagerly using ProviderResource.ServicePointResource.getProviderServicePointsEagerly(userId) method of REST API");

            // find provider entity for which to get associated service points
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            // calculate number of filter query params
            Integer noOfParams = params.getUriInfo().getQueryParameters().size();
            if (params.getOffset() != null) noOfParams -= 1;
            if (params.getLimit() != null) noOfParams -= 1;

            ResourceList<ServicePointWrapper> servicePoints = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Provider> providers = new ArrayList<>();
                providers.add(provider);

                utx.begin();

                if (params.getAddress() != null) {
                    if (params.getCoordinatesSquare() != null || params.getCoordinatesCircle() != null)
                        throw new BadRequestException("Query params cannot include address params and coordinates square params or coordinates circle params at the same time.");
                    // only address params
                    servicePoints = new ResourceList<>(
                            ServicePointWrapper.wrap(
                                    servicePointFacade.findByMultipleCriteriaEagerly(providers, params.getServices(), params.getProviderServices(), params.getEmployees(),
                                            params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getAddress(), params.getTerms(), params.getOffset(), params.getLimit())
                            )
                    );

                } else if (params.getCoordinatesSquare() != null) {
                    if (params.getAddress() != null || params.getCoordinatesCircle() != null)
                        throw new BadRequestException("Query params cannot include coordinates square params and address params or coordinates circle params at the same time.");
                    // only coordinates square params
                    servicePoints = new ResourceList<>(
                            ServicePointWrapper.wrap(
                                    servicePointFacade.findByMultipleCriteriaEagerly(providers, params.getServices(), params.getProviderServices(), params.getEmployees(),
                                            params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesSquare(), params.getTerms(), params.getOffset(), params.getLimit())
                            )
                    );

                } else if (params.getCoordinatesCircle() != null) {
                    if (params.getAddress() != null || params.getCoordinatesSquare() != null)
                        throw new BadRequestException("Query params cannot include coordinates circle params and address params or coordinates square params at the same time.");
                    // only coordinates circle params
                    servicePoints = new ResourceList<>(
                            ServicePointWrapper.wrap(
                                    servicePointFacade.findByMultipleCriteriaEagerly(providers, params.getServices(), params.getProviderServices(), params.getEmployees(),
                                            params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesCircle(), params.getTerms(), params.getOffset(), params.getLimit())
                            )
                    );

                } else {
                    // no location params
                    servicePoints = new ResourceList<>(
                            ServicePointWrapper.wrap(
                                    servicePointFacade.findByMultipleCriteriaEagerly(providers, params.getServices(), params.getProviderServices(), params.getEmployees(),
                                            params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getTerms(), params.getOffset(), params.getLimit())
                            )
                    );
                }

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                servicePoints = new ResourceList<>(ServicePointWrapper.wrap(servicePointFacade.findByProviderEagerly(provider, params.getOffset(), params.getLimit())));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(servicePoints).build();
        }

        /**
         * Method matches specific Service Point resource by composite identifier and returns its instance.
         */
        @GET
        @Path("/{servicePointNumber : \\d+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServicePoint(@PathParam("userId") Long userId,
                                        @PathParam("servicePointNumber") Integer servicePointNumber,
                                        @BeanParam GenericBeanParam params) throws NotFoundException, ForbiddenException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning given Service Point by executing ProviderResource.ServicePointResource.getServicePoint(userId, servicePointNumber) method of REST API");

            ServicePoint foundServicePoint = servicePointFacade.find(new ServicePointId(userId, servicePointNumber));
            if (foundServicePoint == null)
                throw new NotFoundException("Could not find service point for id (" + userId + "," + servicePointNumber + ").");

            // adding hypermedia links to service point resource
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(foundServicePoint, params.getUriInfo());

            return Response.status(Status.OK).entity(foundServicePoint).build();
        }

        /**
         * Method matches specific Service Point resource by composite identifier and returns its instance fetching it eagerly
         */
        @GET
        @Path("{servicePointNumber : \\d+}/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServicePointEagerly(@PathParam("userId") Long userId,
                                               @PathParam("servicePointNumber") Integer servicePointNumber,
                                               @BeanParam GenericBeanParam params) throws NotFoundException, ForbiddenException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning given Service Point eagerly by executing ProviderResource.ServicePointResource.getServicePointEagerly(userId, servicePointNumber) method of REST API");

            ServicePoint foundServicePoint = servicePointFacade.findByIdEagerly(new ServicePointId(userId, servicePointNumber));
            if (foundServicePoint == null)
                throw new NotFoundException("Could not find service point for id (" + userId + "," + servicePointNumber + ").");

            // wrapping ServicePoint into ServicePointWrapper in order to marshall eagerly fetched associated collection of entities
            ServicePointWrapper wrappedServicePoint = new ServicePointWrapper(foundServicePoint);

            // adding hypermedia links to wrapped service point resource
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(wrappedServicePoint, params.getUriInfo());

            return Response.status(Status.OK).entity(wrappedServicePoint).build();
        }

        /**
         * Method that takes Service Point as XML or JSON and creates its new instance in database
         */
        @POST
        @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response createServicePoint(@PathParam("userId") Long providerId,
                                           ServicePoint servicePoint,
                                           @BeanParam GenericBeanParam params) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "creating new ServicePoint by executing ProviderResource.ServicePointResource.createServicePoint(servicePoint) method of REST API");

            ServicePoint createdServicePoint = null;
            URI locationURI = null;

            try {
                // persist new resource in database
                createdServicePoint = servicePointFacade.createForProvider(providerId, servicePoint);

                // populate created resource with hypermedia links
                pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(createdServicePoint, params.getUriInfo());

                // construct link to newly created resource to return in HTTP Header
                String userId = String.valueOf(createdServicePoint.getProvider().getUserId());
                String servicePointNumber = String.valueOf(createdServicePoint.getServicePointNumber());

                Method servicePointsMethod = ProviderResource.class.getMethod("getServicePointResource");
                locationURI = params.getUriInfo().getBaseUriBuilder()
                        .path(ProviderResource.class)
                        .path(servicePointsMethod)
                        .path(servicePointNumber)
                        .resolveTemplate("userId", userId)
                        .build();

            } catch (EJBTransactionRolledbackException ex) {
                ExceptionHandler.handleEJBTransactionRolledbackException(ex);
            } catch (EJBException ex) {
                ExceptionHandler.handleEJBException(ex);
            } catch (NoSuchMethodException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                throw new InternalServerErrorException(ExceptionHandler.ENTITY_CREATION_ERROR_MESSAGE);
            }

            return Response.created(locationURI).entity(createdServicePoint).build();
        }

        /**
         * Method that takes updated Service Point as XML or JSON and its composite ID as path params.
         * It updates Service Point in database for provided composite ID.
         */
        @PUT
        @Path("/{servicePointNumber : \\d+}")
        @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response updateServicePoint(@PathParam("userId") Long userId,
                                           @PathParam("servicePointNumber") Integer servicePointNumber,
                                           ServicePoint servicePoint,
                                           @BeanParam GenericBeanParam params) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "updating existing Service Point by executing ProviderResource.ServicePointResource.updateServicePoint(servicePoint) method of REST API");

            // create composite ID based on path params
            ServicePointId servicePointId = new ServicePointId(userId, servicePointNumber);

            ServicePoint updatedServicePoint = null;
            try {
                // reflect updated resource object in database
                updatedServicePoint = servicePointFacade.update(servicePointId, servicePoint);
                // populate created resource with hypermedia links
                pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(updatedServicePoint, params.getUriInfo());

            } catch (EJBTransactionRolledbackException ex) {
                ExceptionHandler.handleEJBTransactionRolledbackException(ex);
            } catch (EJBException ex) {
                ExceptionHandler.handleEJBException(ex);
            } catch (Exception ex) {
                throw new InternalServerErrorException(ExceptionHandler.ENTITY_UPDATE_ERROR_MESSAGE);
            }

            return Response.status(Status.OK).entity(updatedServicePoint).build();
        }

        /**
         * Method that removes subset of Service Point entities from database for given Provider.
         * The provider id is passed through path param.
         */
        @DELETE
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response removeProviderServicePoints(@PathParam("userId") Long userId,
                                                    @BeanParam GenericBeanParam params) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "removing subset of Service Point entities for given Provider by executing ProviderResource.ServicePointResource.removeProviderServicePoints(userId) method of REST API");

            // find provider entity for which to remove service points
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            // remove service points for given provider from database
            Integer noOfDeleted = servicePointFacade.deleteByProvider(provider);

            // create response returning number of deleted entities
            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfDeleted), 200, "number of deleted service points for provider with id " + userId);

            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method that removes Service Point entity from database for given ID.
         * The service point composite id is passed through path param.
         */
        @DELETE
        @Path("/{servicePointNumber : \\d+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response removeServicePoint(@PathParam("userId") Long userId,
                                           @PathParam("servicePointNumber") Integer servicePointNumber,
                                           @BeanParam GenericBeanParam params) throws ForbiddenException, NotFoundException, InternalServerErrorException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "removing given Service Point by executing ProviderResource.ServicePointResource.removeServicePoint(userId, servicePointNumber) method of REST API");

            // remove entity from database
            Integer noOfDeleted = servicePointFacade.deleteById(new ServicePointId(userId, servicePointNumber));

            if (noOfDeleted == 0)
                throw new NotFoundException("Could not find service point to delete for id (" + userId + "," + servicePointNumber + ").");
            else if (noOfDeleted != 1)
                throw new InternalServerErrorException("Some error occurred while trying to delete service point with id (" + userId + "," + servicePointNumber + ").");

            return Response.status(Status.NO_CONTENT).build();
        }

        /**
         * Additional methods returning subset of resources based on given criteria
         * you can also achieve similar results by applying @QueryParams to generic method
         * returning all resources in order to filter and limit them
         */

        /**
         * Method that counts Service Point entities for given Provider resource
         * The provider id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countServicePointsByProvider(@PathParam("userId") Long userId,
                                                     @BeanParam GenericBeanParam params) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of service points for given provider by executing ProviderResource.ServicePointResource.countServicePointsByProvider(providerId) method of REST API");

            // find provider entity for which to count service points
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(servicePointFacade.countByProvider(provider)), 200, "number of service points for provider with id " + provider.getUserId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method returns subset of Service Point entities for given Provider entity and
         * Address related query params. The provider id is passed through path param.
         * Address params are passed through query params.
         */
        @GET
        @Path("/address")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServicePointsByAddress(@PathParam("userId") Long userId,
                                                          @BeanParam AddressBeanParam params) throws ForbiddenException, NotFoundException, BadRequestException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning service points for given provider and address related params using ProviderResource.ServicePointResource.getProviderServicePointsByAddress(userId, address) method of REST API");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);
            if(noOfParams < 1)
                throw new BadRequestException("There is no location related query param in request.");

            // find provider entity for which to get associated service points
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            // find service points by given criteria
            ResourceList<ServicePoint> servicePoints = new ResourceList<>(servicePointFacade.findByProviderAndAddress(provider,
                    params.getCity(), params.getState(), params.getCountry(), params.getStreet(), params.getZipCode(), params.getOffset(), params.getLimit()));

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(servicePoints).build();
        }

        /**
         * Method returns subset of Service Point entities for given Provider entity and
         * Coordinates Square related params. The provider id is passed through path param.
         * Coordinates Square params are passed through query params.
         */
        @GET
        @Path("/coordinates-square")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServicePointsByCoordinatesSquare(@PathParam("userId") Long userId,
                                                                    @BeanParam CoordinatesSquareBeanParam params) throws ForbiddenException, NotFoundException, BadRequestException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning service points for given provider and coordinates square related params using ProviderResource.ServicePointResource.getProviderServicePointsByCoordinatesSquare(userId, coordinatesSquare) method of REST API");

            // find provider entity for which to get associated service points
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            if (params.getMinLongitudeWGS84() == null || params.getMinLatitudeWGS84() == null ||
                    params.getMaxLongitudeWGS84() == null || params.getMaxLatitudeWGS84() == null)
                throw new BadRequestException("All coordinates square query params must be specified.");

            // find service points by given criteria
            ResourceList<ServicePoint> servicePoints = new ResourceList<>(servicePointFacade.findByProviderAndCoordinatesSquare(provider,
                    params.getMinLongitudeWGS84(), params.getMinLatitudeWGS84(), params.getMaxLongitudeWGS84(), params.getMaxLatitudeWGS84(), params.getOffset(), params.getLimit()));

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(servicePoints).build();
        }

        /**
         * Method returns subset of Service Point entities for given Provider entity and
         * Coordinates Circle related params. The provider id is passed through path param.
         * Coordinates Circle params are passed through query params.
         */
        @GET
        @Path("/coordinates-circle")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServicePointsByCoordinatesCircle(@PathParam("userId") Long userId,
                                                                    @BeanParam CoordinatesCircleBeanParam params) throws ForbiddenException, NotFoundException, BadRequestException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning service points for given provider and coordinates circle related params using ProviderResource.ServicePointResource.getProviderServicePointsByCoordinatesCircle(userId, coordinatesCircle) method of REST API");

            // find provider entity for which to get associated service points
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            if (params.getLongitudeWGS84() == null || params.getLatitudeWGS84() == null || params.getRadius() == null)
                throw new BadRequestException("All coordinates circle query params must be specified.");

            // find service points by given criteria
            ResourceList<ServicePoint> servicePoints = new ResourceList<>(servicePointFacade.findByProviderAndCoordinatesCircle(provider,
                    params.getLongitudeWGS84(), params.getLatitudeWGS84(), params.getRadius(), params.getOffset(), params.getLimit()));

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(servicePoints).build();
        }

        /**
         * related subresources (through relationships)
         */
        @Path("/{servicePointNumber : \\d+}/work-stations")
        public WorkStationResource getWorkStationResource() { return new WorkStationResource(); }

        public class WorkStationResource {

            public WorkStationResource() { }

            /**
             * Method returns subset of Work Station entities for given Service Point
             * The provider id and service point number are passed through path params.
             * They can be additionally filtered and paginated by @QueryParams.
             */
            @GET
            @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
            public Response getServicePointWorkStations( @PathParam("userId") Long providerId,
                                                         @PathParam("servicePointNumber") Integer servicePointNumber,
                                                         @BeanParam WorkStationBeanParam params ) throws ForbiddenException, NotFoundException,
            /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

                RESTToolkit.authorizeAccessToWebService(params);
                logger.log(Level.INFO, "returning subset of Work Station entities for given Service Point using " +
                        "ProviderResource.ServicePointResource.WorkStationResource.getServicePointWorkStations(providerId, servicePointNumber) method of REST API");

                utx.begin();

                // find service point entity for which to get associated work stations
                ServicePoint servicePoint = servicePointFacade.find( new ServicePointId(providerId, servicePointNumber) );
                if (servicePoint == null)
                    throw new NotFoundException("Could not find service point for id (" + providerId + "," + servicePointNumber + ").");

                Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

                ResourceList<WorkStation> workStations = null;

                if(noOfParams > 0) {
                    logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                    List<ServicePoint> servicePoints = new ArrayList<>();
                    servicePoints.add(servicePoint);

                    // get work stations for given service point filtered by given query params
                    workStations = new ResourceList<>(
                            workStationFacade.findByMultipleCriteria(servicePoints, params.getServices(), params.getProviderServices(),
                                    params.getEmployees(), params.getWorkStationTypes(), params.getPeriod(), params.getStrictTerm(),
                                    params.getTerms(), params.getOffset(), params.getLimit())
                    );
                } else {
                    logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                    // get work stations for given service point without filtering (eventually paginated)
                    workStations = new ResourceList<>( workStationFacade.findByServicePoint(servicePoint, params.getOffset(), params.getLimit()) );
                }

                utx.commit();

                // result resources need to be populated with hypermedia links to enable resource discovery
                pl.salonea.jaxrs.WorkStationResource.populateWithHATEOASLinks(workStations, params.getUriInfo(), params.getOffset(), params.getLimit());

                return Response.status(Status.OK).entity(workStations).build();
            }

            @GET
            @Path("/eagerly")
            @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
            public Response getServicePointWorkStationsEagerly( @PathParam("userId") Long providerId,
                                                                @PathParam("servicePointNumber") Integer servicePointNumber,
                                                                @BeanParam WorkStationBeanParam params ) throws ForbiddenException, NotFoundException,
            /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

                RESTToolkit.authorizeAccessToWebService(params);
                logger.log(Level.INFO, "returning subset of Work Station entities for given Service Point eagerly using " +
                        "ProviderResource.ServicePointResource.WorkStationResource.getServicePointWorkStationsEagerly(providerId, servicePointNumber) method of REST API");

                utx.begin();

                // find service point entity for which to get associated work stations
                ServicePoint servicePoint = servicePointFacade.find( new ServicePointId(providerId, servicePointNumber) );
                if (servicePoint == null)
                    throw new NotFoundException("Could not find service point for id (" + providerId + "," + servicePointNumber + ").");

                Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

                ResourceList<WorkStationWrapper> workStations = null;

                if (noOfParams > 0) {
                    logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                    List<ServicePoint> servicePoints = new ArrayList<>();
                    servicePoints.add(servicePoint);

                    // get work stations eagerly for given service point filtered by given query params
                    workStations = new ResourceList<>(
                            WorkStationWrapper.wrap(
                                    workStationFacade.findByMultipleCriteriaEagerly(servicePoints, params.getServices(), params.getProviderServices(),
                                            params.getEmployees(), params.getWorkStationTypes(), params.getPeriod(), params.getStrictTerm(),
                                            params.getTerms(), params.getOffset(), params.getLimit())
                            )
                    );
                } else {
                    logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                    // get work stations eagerly for given service point without filtering (eventually paginated)
                    workStations = new ResourceList<>( WorkStationWrapper.wrap(workStationFacade.findByServicePointEagerly(servicePoint, params.getOffset(), params.getLimit())) );
                }

                utx.commit();

                // result resources need to be populated with hypermedia links to enable resource discovery
                pl.salonea.jaxrs.WorkStationResource.populateWithHATEOASLinks(workStations, params.getUriInfo(), params.getOffset(), params.getLimit());

                return Response.status(Status.OK).entity(workStations).build();
            }

            /**
             * Method matches specific Work Station resource by composite identifier and returns its instance.
             */
            @GET
            @Path("/{workStationNumber : \\d+}")
            @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
            public Response getWorkStation( @PathParam("userId") Long providerId,
                                            @PathParam("servicePointNumber") Integer servicePointNumber,
                                            @PathParam("workStationNumber") Integer workStationNumber,
                                            @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

                RESTToolkit.authorizeAccessToWebService(params);
                logger.log(Level.INFO, "returning given Work Station by executing " +
                        "ProviderResource.ServicePointResource.WorkStationResource.getWorkStation(providerId, servicePointNumber, workStationNumber) method of REST API");

                WorkStation foundWorkStation = workStationFacade.find( new WorkStationId(providerId, servicePointNumber, workStationNumber) );
                if (foundWorkStation == null)
                    throw new NotFoundException("Could not find work station for id (" + providerId + "," + servicePointNumber + "," + workStationNumber + ").");

                // adding hypermedia links to work station resource
                pl.salonea.jaxrs.WorkStationResource.populateWithHATEOASLinks(foundWorkStation, params.getUriInfo());

                return Response.status(Status.OK).entity(foundWorkStation).build();
            }

            /**
             * Method that matches specific Work Station resource by composite identifier and returns its instance fetching it eagerly
             */
            @GET
            @Path("/{workStationNumber : \\d+}/eagerly")
            @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
            public Response getWorkStationEagerly( @PathParam("userId") Long providerId,
                                                   @PathParam("servicePointNumber") Integer servicePointNumber,
                                                   @PathParam("workStationNumber") Integer workStationNumber,
                                                   @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

                RESTToolkit.authorizeAccessToWebService(params);
                logger.log(Level.INFO, "returning given Work Station eagerly by executing " +
                        "ProviderResource.ServicePointResource.WorkStationResource.getWorkStationEagerly(providerId, servicePointNumber, workStationNumber) method of REST API");

                WorkStation foundWorkStation = workStationFacade.findByIdEagerly(new WorkStationId(providerId, servicePointNumber, workStationNumber));
                if (foundWorkStation == null)
                    throw new NotFoundException("Could not find work station for id (" + providerId + "," + servicePointNumber + "," + workStationNumber + ").");

                // wrapping WorkStation into WorkStationWrapper in order to marshall eagerly fetched associated collection of entities
                WorkStationWrapper wrappedWorkStation = new WorkStationWrapper(foundWorkStation);

                // adding hypermedia links to wrapped work station resource
                pl.salonea.jaxrs.WorkStationResource.populateWithHATEOASLinks(wrappedWorkStation, params.getUriInfo());

                return Response.status(Status.OK).entity(wrappedWorkStation).build();
            }

            /**
             * Method that takes Work Station as XML or JSON and creates its new instance in database
             */
            @POST
            @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
            @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
            public Response createWorkStation( @PathParam("userId") Long providerId,
                                               @PathParam("servicePointNumber") Integer servicePointNumber,
                                               WorkStation workStation,
                                               @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

                RESTToolkit.authorizeAccessToWebService(params);
                logger.log(Level.INFO, "creating new WorkStation by executing ProviderResource.ServicePointResource.WorkStationResource.createWorkStation(workStation) method of REST API");

                WorkStation createdWorkStation = null;
                URI locationURI = null;

                try {
                    // persist new resource in database
                    createdWorkStation = workStationFacade.createForServicePoint(new ServicePointId(providerId, servicePointNumber), workStation);

                    // populate created resource with hypermedia links
                    pl.salonea.jaxrs.WorkStationResource.populateWithHATEOASLinks(createdWorkStation, params.getUriInfo());

                    // construct link to newly created resource to return in HTTP Header
                    String userId = String.valueOf(createdWorkStation.getServicePoint().getProvider().getUserId());
                    String servicePointNo = String.valueOf(createdWorkStation.getServicePoint().getServicePointNumber());
                    String workStationNo = String.valueOf(createdWorkStation.getWorkStationNumber());

                    Method servicePointsMethod = ProviderResource.class.getMethod("getServicePointResource");
                    Method workStationsMethod = ProviderResource.ServicePointResource.class.getMethod("getWorkStationResource");
                    locationURI = params.getUriInfo().getBaseUriBuilder()
                            .path(ProviderResource.class)
                            .path(servicePointsMethod)
                            .path(workStationsMethod)
                            .path(workStationNo)
                            .resolveTemplate("userId", userId)
                            .resolveTemplate("servicePointNumber", servicePointNo)
                            .build();

                } catch (EJBTransactionRolledbackException ex) {
                    ExceptionHandler.handleEJBTransactionRolledbackException(ex);
                } catch (EJBException ex) {
                    ExceptionHandler.handleEJBException(ex);
                } catch (NoSuchMethodException ex) {
                    ex.printStackTrace();
                } catch (Exception ex) {
                    throw new InternalServerErrorException(ExceptionHandler.ENTITY_CREATION_ERROR_MESSAGE);
                }

                return Response.created(locationURI).entity(createdWorkStation).build();
            }

            /**
             * Method that takes updated Work Station as XML or JSON and its composite ID as path params.
             * It updates Work Station in database for provided composite ID.
             */
            @PUT
            @Path("/{workStationNumber : \\d+}")
            @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
            @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
            public Response updateWorkStation( @PathParam("userId") Long providerId,
                                               @PathParam("servicePointNumber") Integer servicePointNumber,
                                               @PathParam("workStationNumber") Integer workStationNumber,
                                               WorkStation workStation,
                                               @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

                RESTToolkit.authorizeAccessToWebService(params);
                logger.log(Level.INFO, "updating existing Work Station by executing ProviderResource.ServicePointResource.WorkStationResource.updateWorkStation(workStation) method of REST API");

                // create composite ID based on path params
                WorkStationId workStationId = new WorkStationId(providerId, servicePointNumber, workStationNumber);

                WorkStation updatedWorkStation = null;
                try {
                    // reflect updated resource object in database
                    updatedWorkStation = workStationFacade.update(workStationId, workStation);
                    // populate created resource with hypermedia links
                    pl.salonea.jaxrs.WorkStationResource.populateWithHATEOASLinks(updatedWorkStation, params.getUriInfo());

                } catch (EJBTransactionRolledbackException ex) {
                    ExceptionHandler.handleEJBTransactionRolledbackException(ex);
                } catch (EJBException ex) {
                    ExceptionHandler.handleEJBException(ex);
                } catch (Exception ex) {
                    throw new InternalServerErrorException(ExceptionHandler.ENTITY_UPDATE_ERROR_MESSAGE);
                }

                return Response.status(Status.OK).entity(updatedWorkStation).build();
            }

            /**
             * Method that removes subset of Work Station entities from database for given Service Point.
             * The provider id and service point number are passed through path param.
             */
            @DELETE
            @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
            public Response removeServicePointWorkStations( @PathParam("userId") Long providerId,
                                                            @PathParam("servicePointNumber") Integer servicePointNumber,
                                                            @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException,
            /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

                RESTToolkit.authorizeAccessToWebService(params);
                logger.log(Level.INFO, "removing subset of Work Station entities for given Service Point by executing " +
                        "ProviderResource.ServicePointResource.WorkStationResource.removeServicePointWorkStations(providerId, servicePointNumber) method of REST API");

                utx.begin();

                // find service point entity for which to remove work stations
                ServicePoint servicePoint = servicePointFacade.find( new ServicePointId(providerId, servicePointNumber) );
                if (servicePoint == null)
                    throw new NotFoundException("Could not find service point for id (" + providerId + "," + servicePointNumber + ").");

                // remove all specified entities from database
                Integer noOfDeleted = workStationFacade.deleteByServicePoint(servicePoint);

                utx.commit();

                // create response returning number of deleted entities
                ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfDeleted), 200,
                        "number of deleted work stations for service point with id (" + providerId + "," + servicePointNumber + ")");

                return Response.status(Status.OK).entity(responseEntity).build();
            }

            /**
             * Method that removes Work Station entity from database for given ID.
             * The work station composite id is passed through path param.
             */
            @DELETE
            @Path("/{workStationNumber : \\d+}")
            @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
            public Response removeWorkStation( @PathParam("userId") Long providerId,
                                               @PathParam("servicePointNumber") Integer servicePointNumber,
                                               @PathParam("workStationNumber") Integer workStationNumber,
                                               @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException, InternalServerErrorException {

                RESTToolkit.authorizeAccessToWebService(params);
                logger.log(Level.INFO, "removing given Work Station by executing ProviderResource.ServicePointResource.WorkStationResource.removeWorkStation(providerId, servicePointNumber, workStationNumber) method of REST API");

                // remove entity from database
                Integer noOfDeleted = workStationFacade.deleteById(new WorkStationId(providerId, servicePointNumber, workStationNumber));

                if (noOfDeleted == 0)
                    throw new NotFoundException("Could not find work station to delete for id (" + providerId + "," + servicePointNumber + "," + workStationNumber + ").");
                else if (noOfDeleted != 1)
                    throw new InternalServerErrorException("Some error occurred while trying to delete work station with id (" + providerId + "," + servicePointNumber + "," + workStationNumber + ").");

                return Response.status(Status.NO_CONTENT).build();
            }

            /**
             * Additional methods returning subset of resources based on given criteria
             * you can also achieve similar results by applying @QueryParams to generic method
             * returning all resources in order to filter and limit them.
             */

            /**
             * Method that counts Work Station entities for given Service Point resource.
             * The provider id and service point number are passed through path params.
             */
            @GET
            @Path("/count")
            @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
            public Response countWorkStationsByServicePoint( @PathParam("userId") Long providerId,
                                                             @PathParam("servicePointNumber") Integer servicePointNumber,
                                                             @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException,
            /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

                RESTToolkit.authorizeAccessToWebService(params);
                logger.log(Level.INFO, "returning number of work stations for given service point by executing " +
                        "ProviderResource.ServicePointResource.WorkStationResource.countWorkStationsByServicePoint(providerId, servicePointNumber) method of REST API");

                utx.begin();

                // find service point entity for which to count work stations
                ServicePoint servicePoint = servicePointFacade.find( new ServicePointId(providerId, servicePointNumber) );
                if (servicePoint == null)
                    throw new NotFoundException("Could not find service point for id (" + providerId + "," + servicePointNumber + ").");

                ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(workStationFacade.countByServicePoint(servicePoint)),
                        200, "number of work stations for service point with id (" + providerId + "," + servicePointNumber + ").");

                utx.commit();

                return Response.status(Status.OK).entity(responseEntity).build();
            }

            /**
             * Method returns subset of Work Station entities for given Service Point
             * entity and work station type. The provider id and service point number
             * are passed through path param. Work station type is also passed through
             * path param.
             */
            @GET
            @Path("/typed/{type : \\S+}")
            @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
            public Response getServicePointWorkStationsByType( @PathParam("userId") Long providerId,
                                                               @PathParam("servicePointNumber") Integer servicePointNumber,
                                                               @PathParam("type") WorkStationType type,
                                                               @BeanParam PaginationBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
            /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

                RESTToolkit.authorizeAccessToWebService(params);
                logger.log(Level.INFO, "returning work stations for given service point and work station type using " +
                        "ProviderResource.ServicePointResource.WorkStationResource.getServicePointWorkStationsByType(providerId, servicePointNumber, type) method of REST API");

                utx.begin();

                // find service point entity for which to get associated work stations
                ServicePoint servicePoint = servicePointFacade.find( new ServicePointId(providerId, servicePointNumber) );
                if (servicePoint == null)
                    throw new NotFoundException("Could not find service point for id (" + providerId + "," + servicePointNumber + ").");

                if(type == null)
                    throw new BadRequestException("Work station type param cannot be null.");

                // find work stations by given criteria (service point and work station type)
                ResourceList<WorkStation> workStations = new ResourceList<>(
                        workStationFacade.findByServicePointAndType(servicePoint, type, params.getOffset(), params.getLimit())
                );

                utx.commit();

                // result resources need to be populated with hypermedia links to enable resource discovery
                pl.salonea.jaxrs.WorkStationResource.populateWithHATEOASLinks(workStations, params.getUriInfo(), params.getOffset(), params.getLimit());

                return Response.status(Status.OK).entity(workStations).build();
            }

            /**
             * Method returns subset of Work Station entities for given Service Point and
             * Term's date range. The provider id and service point number are passed through
             * path params. Term start and end dates are passed through query params.
             */
            @GET
            @Path("/by-term")
            @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
            public Response getServicePointWorkStationsByTerm( @PathParam("userId") Long providerId,
                                                               @PathParam("servicePointNumber") Integer servicePointNumber,
                                                               @BeanParam DateRangeBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
            /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

                RESTToolkit.authorizeAccessToWebService(params);
                logger.log(Level.INFO, "returning work stations for given service point and term (startDate, endDate) using " +
                        "ProviderResource.ServicePointResource.WorkStationResource.getServicePointWorkStationsByTerm(providerId, servicePointNumber, term) method of REST API");

                RESTToolkit.validateDateRange(params); // i.e. startDate and endDate

                utx.begin();

                // find service point entity for which to get associated work stations
                ServicePoint servicePoint = servicePointFacade.find(new ServicePointId(providerId, servicePointNumber));
                if(servicePoint == null)
                    throw new NotFoundException("Could not find service point for id (" + providerId + "," + servicePointNumber + ").");

                // find work stations by given criteria (service point, term)
                ResourceList<WorkStation> workStations = new ResourceList<>(
                        workStationFacade.findByServicePointAndTerm(servicePoint, params.getStartDate(), params.getEndDate(),
                                params.getOffset(), params.getLimit())
                );

                utx.commit();

                // result resources need to be populated with hypermedia links to enable resource discovery
                pl.salonea.jaxrs.WorkStationResource.populateWithHATEOASLinks(workStations, params.getUriInfo(), params.getOffset(), params.getLimit());

                return Response.status(Status.OK).entity(workStations).build();
            }

            /**
             * Method returns subset of Work Station entities for given Service Point entity and
             * Term's date range (strict). The provider id and service point number are passed through
             * path params. Term (strict) start and end dates are passed through query params.
             */
            @GET
            @Path("/by-term-strict")
            @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
            public Response getServicePointWorkStationsByTermStrict( @PathParam("userId") Long providerId,
                                                                     @PathParam("servicePointNumber") Integer servicePointNumber,
                                                                     @BeanParam DateRangeBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
            /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

                RESTToolkit.authorizeAccessToWebService(params);
                logger.log(Level.INFO, "returning work stations for given service point and term strict (startDate, endDate) using " +
                        "ProviderResource.ServicePointResource.WorkStationResource.getServicePointWorkStationsByTermStrict(providerId, servicePointNumber, termStrict) method of REST API");

                RESTToolkit.validateDateRange(params); // i.e. startDate and endDate

                utx.begin();

                // find service point entity for which to get associated work stations
                ServicePoint servicePoint = servicePointFacade.find(new ServicePointId(providerId, servicePointNumber));
                if(servicePoint == null)
                    throw new NotFoundException("Could not find service point for id (" + providerId + "," + servicePointNumber + ").");

                // find work stations by given criteria (service point, term strict)
                ResourceList<WorkStation> workStations = new ResourceList<>(
                        workStationFacade.findByServicePointAndTermStrict(servicePoint, params.getStartDate(), params.getEndDate(),
                                params.getOffset(), params.getLimit())
                );

                utx.commit();

                // result resources need to be populated with hypermedia links to enable resource discovery
                pl.salonea.jaxrs.WorkStationResource.populateWithHATEOASLinks(workStations, params.getUriInfo(), params.getOffset(), params.getLimit());

                return Response.status(Status.OK).entity(workStations).build();
            }
        }
    }

    public class ProviderServiceResource {
        /**
         * Method returns subset of ProviderService entities for given Provider
         * The provider id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServices(@PathParam("userId") Long userId,
                                            @BeanParam ProviderServiceBeanParam params) throws NotFoundException, ForbiddenException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of ProviderService entities for given Provider using ProviderResource.ProviderServiceResource.getProviderServices(userId) method of REST API");

            // find provider entity for which to get associated provider services
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new pl.salonea.jaxrs.exceptions.NotFoundException("Could not find provider for id " + userId + ".");

            // calculate number of filter query params
            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ProviderService> providerServices = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Provider> providers = new ArrayList<>();
                providers.add(provider);

                utx.begin();

                // get provider services for given provider filtered by given params
                providerServices = new ResourceList<>(
                        providerServiceFacade.findByMultipleCriteria(providers, params.getServices(), params.getServiceCategories(),
                                params.getDescriptions(), params.getMinPrice(), params.getMaxPrice(), params.getIncludeDiscounts(),
                                params.getMinDiscount(), params.getMaxDiscount(), params.getMinDuration(), params.getMaxDuration(),
                                params.getServicePoints(), params.getWorkStations(), params.getEmployees(), params.getEmployeeTerms(),
                                params.getTerms(), params.getOffset(), params.getLimit())
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get provider services for given provider
                providerServices = new ResourceList<>(providerServiceFacade.findByProvider(provider, params.getOffset(), params.getLimit()));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(providerServices, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Response.Status.OK).entity(providerServices).build();
        }

        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServicesEagerly(@PathParam("userId") Long userId,
                                                   @BeanParam ProviderServiceBeanParam params) throws NotFoundException, ForbiddenException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of ProviderService entities for given Provider eagerly using ProviderResource.ProviderServiceResource.getProviderServicesEagerly(userId) method of REST API");

            // find provider entity for which to get associated provider services
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            // calculate number of filter query params
            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ProviderServiceWrapper> providerServices = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Provider> providers = new ArrayList<>();
                providers.add(provider);

                utx.begin();

                // get provider services for given provider eagerly filtered by given params
                providerServices = new ResourceList<>(
                        ProviderServiceWrapper.wrap(
                                providerServiceFacade.findByMultipleCriteriaEagerly(providers, params.getServices(), params.getServiceCategories(),
                                        params.getDescriptions(), params.getMinPrice(), params.getMaxPrice(), params.getIncludeDiscounts(),
                                        params.getMinDiscount(), params.getMaxDiscount(), params.getMinDuration(), params.getMaxDuration(),
                                        params.getServicePoints(), params.getWorkStations(), params.getEmployees(), params.getEmployeeTerms(),
                                        params.getTerms(), params.getOffset(), params.getLimit())
                        )
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get provider services for given provider eagerly without filtering (eventually paginated)
                providerServices = new ResourceList<>(ProviderServiceWrapper.wrap(providerServiceFacade.findByProviderEagerly(provider, params.getOffset(), params.getLimit())));

            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(providerServices, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providerServices).build();
        }

        /**
         * Method matches specific Provider Service resource by composite identifier and returns its instance.
         */
        @GET
        @Path("/{serviceId : \\d+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderService(@PathParam("userId") Long userId,
                                           @PathParam("serviceId") Integer serviceId,
                                           @BeanParam GenericBeanParam params) throws NotFoundException, ForbiddenException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning given Provider Service by executing ProviderResource.ProviderServiceResource.getProviderService(userId, serviceId) method of REST API");

            ProviderService foundProviderService = providerServiceFacade.find(new ProviderServiceId(userId, serviceId));
            if (foundProviderService == null)
                throw new NotFoundException("Could not find provider service for id (" + userId + "," + serviceId + ").");

            // adding hypermedia links to provider service resource
            pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(foundProviderService, params.getUriInfo());

            return Response.status(Status.OK).entity(foundProviderService).build();
        }

        /**
         * Method matches specific Provider Service resource by composite identifier and returns its instance fetching it eagerly
         */
        @GET
        @Path("/{serviceId : \\d+}/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServiceEagerly(@PathParam("userId") Long userId,
                                                  @PathParam("serviceId") Integer serviceId,
                                                  @BeanParam GenericBeanParam params) throws NotFoundException, ForbiddenException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning given Provider Service eagerly by executing ProviderResource.ProviderServiceResource.getProviderServiceEagerly(userId, serviceId) method of REST API");

            ProviderService foundProviderService = providerServiceFacade.findByIdEagerly(new ProviderServiceId(userId, serviceId));
            if (foundProviderService == null)
                throw new NotFoundException("Could not find provider service for id (" + userId + "," + serviceId + ").");

            // wrapping ProviderService into ProviderServiceWrapper in order to marshall eagerly fetched associated collection of entities
            ProviderServiceWrapper wrappedProviderService = new ProviderServiceWrapper(foundProviderService);

            // adding hypermedia links to wrapped provider service resource
            pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(wrappedProviderService, params.getUriInfo());

            return Response.status(Status.OK).entity(wrappedProviderService).build();
        }

        /**
         * Method that takes ProviderService as XML or JSON and creates its new instance in database
         */
        @POST
        @Path("/{serviceId : \\d+}")
        @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response createProviderService(@PathParam("userId") Long providerId,
                                              @PathParam("serviceId") Integer serviceId,
                                              ProviderService providerService,
                                              @BeanParam GenericBeanParam params) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "creating new ProviderService by executing ProviderResource.ProviderServiceResource.createProviderService(providerService) method of REST API");

            ProviderService createdProviderService = null;
            URI locationURI = null;

            try {
                // persist new resource in database
                createdProviderService = providerServiceFacade.createForProviderAndService(providerId, serviceId, providerService);

                // populate created resource with hypermedia links
                pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(createdProviderService, params.getUriInfo());

                // construct link to newly created resource to return in HTTP Header
                String uid = String.valueOf(createdProviderService.getProvider().getUserId());
                String sid = String.valueOf(createdProviderService.getService().getServiceId());

                Method providerServicesMethod = ProviderResource.class.getMethod("getProviderServiceResource");
                locationURI = params.getUriInfo().getBaseUriBuilder()
                        .path(ProviderResource.class)
                        .path(providerServicesMethod)
                        .path(sid)
                        .resolveTemplate("userId", uid)
                        .build();

            } catch (EJBTransactionRolledbackException ex) {
                ExceptionHandler.handleEJBTransactionRolledbackException(ex);
            } catch (EJBException ex) {
                ExceptionHandler.handleEJBException(ex);
            } catch (NoSuchMethodException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                throw new InternalServerErrorException(ExceptionHandler.ENTITY_CREATION_ERROR_MESSAGE);
            }

            return Response.created(locationURI).entity(createdProviderService).build();
        }

        /**
         * Method that takes updated Provider Service as XML or JSON and its composite ID as path param.
         * It updates Provider Service in database for provided composite ID.
         */
        @PUT
        @Path("/{serviceId : \\d+}")
        @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response updateProviderService(@PathParam("userId") Long userId,
                                              @PathParam("serviceId") Integer serviceId,
                                              ProviderService providerService,
                                              @BeanParam GenericBeanParam params) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "updating existing Provider Service by executing ProviderResource.ProviderServiceResource.updateProviderService(providerService) method of REST API");

            // create composite ID based on path params
            ProviderServiceId providerServiceId = new ProviderServiceId(userId, serviceId);

            ProviderService updatedProviderService = null;
            try {
                // reflect updated resource object in database
                updatedProviderService = providerServiceFacade.update(providerServiceId, providerService, true);
                // populate created resource with hypermedia links
                pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(updatedProviderService, params.getUriInfo());

            } catch (EJBTransactionRolledbackException ex) {
                ExceptionHandler.handleEJBTransactionRolledbackException(ex);
            } catch (EJBException ex) {
                ExceptionHandler.handleEJBException(ex);
            } catch (Exception ex) {
                throw new InternalServerErrorException(ExceptionHandler.ENTITY_UPDATE_ERROR_MESSAGE);
            }

            return Response.status(Status.OK).entity(updatedProviderService).build();
        }

        /**
         * Method that takes new discount value and update it on Provider Service entities
         * that match specified query param criteria: service category, employee.
         * New discount is passed through request body message and service category id
         * and employee id are passed through query params.
         */
        @PUT
        @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response updateProviderServicesDiscount(@PathParam("userId") Long userId,
                                                       RequestWrapper wrappedDiscount,
                                                       @QueryParam("serviceCategoryId") Integer serviceCategoryId,
                                                       @QueryParam("employeeId") Long employeeId,
                                                       @HeaderParam("authToken") String authToken) throws ForbiddenException, NotFoundException, BadRequestException {

            if (authToken == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "updating Provider Services matching given service category and/or employee by setting new discount value " +
                    "using ProviderResource.ProviderServiceResource.updateProviderServicesDiscount(userId, discount, serviceCategoryId, employeeId) method of REST API ");

            if (wrappedDiscount == null)
                throw new BadRequestException("New discount value should be send in request body as json or xml.");

            // find provider entity for which to update provider services
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            Short newDiscount = Short.valueOf(wrappedDiscount.getMessage());
            if (newDiscount < 0 || newDiscount > 100)
                throw new BadRequestException("New discount value should be in range [0,100].");

            Integer noOfUpdated = 0;
            if (serviceCategoryId != null && employeeId != null) {
                // update discount for provider, service category and employee

                ServiceCategory serviceCategory = serviceCategoryFacade.find(serviceCategoryId);
                if (serviceCategory == null)
                    throw new NotFoundException("Could not find service category for id " + serviceCategoryId + ".");

                Employee employee = employeeFacade.find(employeeId);
                if (employee == null)
                    throw new NotFoundException("Could not find employee for id " + employeeId + ".");

                noOfUpdated = providerServiceFacade.updateDiscountForProviderAndServiceCategoryAndEmployee(
                        provider, serviceCategory, employee, newDiscount);

            } else if (serviceCategoryId != null) {
                // update discount for provider and service category

                ServiceCategory serviceCategory = serviceCategoryFacade.find(serviceCategoryId);
                if (serviceCategory == null)
                    throw new NotFoundException("Could not find service category for id " + serviceCategoryId + ".");

                noOfUpdated = providerServiceFacade.updateDiscountForProviderAndServiceCategory(provider, serviceCategory, newDiscount);

            } else if (employeeId != null) {
                // update discount for provider and employee

                Employee employee = employeeFacade.find(employeeId);
                if (employee == null)
                    throw new NotFoundException("Could not find employee for id " + employee + ".");

                noOfUpdated = providerServiceFacade.updateDiscountForProviderAndEmployee(provider, employee, newDiscount);

            } else {
                // update discount for provider only

                noOfUpdated = providerServiceFacade.updateDiscountForProvider(provider, newDiscount);
            }

            // create response returning number of updated entities
            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfUpdated), 200, "number of updated provider services with new discount value " + newDiscount + "% for provider with id " + userId);

            return Response.status(Status.OK).entity(responseEntity).build();

        }

        /**
         * Method that removes subset of Provider Service entities from database for given Provider.
         * The provider id is passed through path param.
         */
        @DELETE
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response removeProviderServices(@PathParam("userId") Long userId,
                                               @QueryParam("employeeId") Long employeeId,
                                               @QueryParam("serviceCategoryId") Integer serviceCategoryId,
                                               @BeanParam GenericBeanParam params) throws ForbiddenException, NotFoundException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "removing subset of Provider Service entities for given Provider by executing ProviderResource.ProviderServiceResource.removeProviderServices(userId) method of REST API");

            // find provider entity for which to remove provider services
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            // remove all specified entities from database
            Integer noOfDeleted = 0;
            if (employeeId != null && serviceCategoryId != null) {

                Employee employee = employeeFacade.find(employeeId);
                if (employee == null)
                    throw new NotFoundException("Could not find employee for id " + employeeId + ".");

                ServiceCategory serviceCategory = serviceCategoryFacade.find(serviceCategoryId);
                if (serviceCategory == null)
                    throw new NotFoundException("Could not find service category for id " + serviceCategoryId + ".");

                noOfDeleted = providerServiceFacade.deleteForProviderAndServiceCategoryAndOnlyEmployee(provider, serviceCategory, employee);

            } else if (employeeId != null) {

                Employee employee = employeeFacade.find(employeeId);
                if (employee == null)
                    throw new NotFoundException("Could not find employee for id " + employeeId + ".");

                noOfDeleted = providerServiceFacade.deleteForProviderAndOnlyEmployee(provider, employee);

            } else if (serviceCategoryId != null) {

                ServiceCategory serviceCategory = serviceCategoryFacade.find(serviceCategoryId);
                if (serviceCategory == null)
                    throw new NotFoundException("Could not find service category for id " + serviceCategoryId + ".");

                noOfDeleted = providerServiceFacade.deleteForProviderAndServiceCategory(provider, serviceCategory);

            } else {
                noOfDeleted = providerServiceFacade.deleteForProvider(provider);
            }

            // create response returning number of deleted entities
            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfDeleted), 200, "number of deleted provider services for provider with id " + userId);

            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method that removes Provider Service entity from database for given ID.
         * The provider service composite id is passed through path params.
         */
        @DELETE
        @Path("/{serviceId : \\d+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response removeProviderService(@PathParam("userId") Long userId,
                                              @PathParam("serviceId") Integer serviceId,
                                              @BeanParam GenericBeanParam params) throws ForbiddenException, NotFoundException, InternalServerErrorException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "removing given Provider Service by executing ProviderResource.ProviderServiceResource.removeProviderService(userId, serviceId) method of REST API");

            // remove entity from database
            Integer noOfDeleted = providerServiceFacade.deleteById(new ProviderServiceId(userId, serviceId));

            if (noOfDeleted == 0)
                throw new NotFoundException("Could not find provider service to delete for id (" + userId + "," + serviceId + ").");
            else if (noOfDeleted != 1)
                throw new InternalServerErrorException("Some error occurred while trying to delete provider service with id (" + userId + "," + serviceId + ").");

            return Response.status(Status.NO_CONTENT).build();
        }

        /**
         * Additional methods returning subset of resources based on given criteria.
         * you can also achieve similar results by applying @QueryParams to generic method
         * returning all resources in order to filter and limit them.
         */

        /**
         * Method that counts Provider Service entities for given Provider resource
         * The provider id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countProviderServicesByProvider(@PathParam("userId") Long userId,
                                                        @BeanParam GenericBeanParam params) throws ForbiddenException, NotFoundException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning number of provider services for given provider by executing ProviderResource.ProviderServiceResource.countProviderServicesByProvider(userId) method of REST API");

            // find provider entity for which to count provider services
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(providerServiceFacade.countByProvider(provider)), 200,
                    "number of provider services for provider with id " + provider.getUserId());

            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method returns subset of Provider Service entities for given provider
         * categorized in given service category.
         * The provider id and service category id are passed in path params.
         */
        @GET
        @Path("/categorized-in/{serviceCategoryId : \\d+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServicesByCategory(@PathParam("userId") Long userId,
                                                      @PathParam("serviceCategoryId") Integer serviceCategoryId,
                                                      @BeanParam PaginationBeanParam params) throws ForbiddenException, NotFoundException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning provider services for given provider and service category using ProviderResource.ProviderServiceResource.getProviderServicesByCategory(userId, serviceCategoryId) method of REST API");

            // find provider entity for which to get associated provider services
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            // find service category entity for which to get associated provider services
            ServiceCategory serviceCategory = serviceCategoryFacade.find(serviceCategoryId);
            if (serviceCategory == null)
                throw new NotFoundException("Could not find service category for id " + serviceCategoryId + ".");

            // find provider services by given criteria (provider and service category)
            ResourceList<ProviderService> providerServices = new ResourceList<>(
                    providerServiceFacade.findByProviderAndServiceCategory(provider, serviceCategory, params.getOffset(), params.getLimit()));

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(providerServices, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providerServices).build();
        }

        /**
         * Method returns subset of Provider Service entities for given provider
         * described by given description.
         * The provider id and description are passed in path params.
         */
        @GET
        @Path("/described-by/{description}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServicesByDescription(@PathParam("userId") Long userId,
                                                         @PathParam("description") String description,
                                                         @BeanParam PaginationBeanParam params) throws ForbiddenException, NotFoundException, BadRequestException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning provider services for given provider and description using ProviderResource.ProviderServiceResource.getProviderServicesByDescription(userId, description) method of REST API");

            // find provider entity for which to get associated provider services
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            if (description == null)
                throw new BadRequestException("Description param cannot be null.");

            // find provider services by given criteria (provider and description)
            ResourceList<ProviderService> providerServices = new ResourceList<>(
                    providerServiceFacade.findByProviderAndDescription(provider, description, params.getOffset(), params.getLimit()));

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(providerServices, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providerServices).build();
        }

        /**
         * Method returns subset of Provider Service entities for given provider
         * that have been discounted between some min discount and max discount.
         * The provider id is passed through path param and discount limits are
         * passed through query params.
         */
        @GET
        @Path("/discounted-between")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServicesByDiscount(@PathParam("userId") Long userId,
                                                      @BeanParam DiscountRangeBeanParam params) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning provider services for given provider and discounted between given min and max discount limits" +
                    " using ProviderResource.ProviderServiceResource.getProviderServicesByDiscount(userId, minDiscount, maxDiscount) method of REST API");

            // find provider entity for which to get associated provider services
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            RESTToolkit.validateDiscountRange(params);

            // find provider services by given criteria (provider and discount max and min limits)
            ResourceList<ProviderService> providerServices = new ResourceList<>(
                    providerServiceFacade.findByProviderAndDiscount(provider, params.getMinDiscount(), params.getMaxDiscount(),
                            params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(providerServices, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providerServices).build();
        }

        /**
         * Method returns subset of Provider Service entities for given provider
         * supplied by given employee.
         * The provider id and employee id are passed in path params.
         */
        @GET
        @Path("/supplied-by/{employeeId : \\d+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServicesSuppliedByEmployee(@PathParam("userId") Long userId,
                                                              @PathParam("employeeId") Long employeeId,
                                                              @BeanParam PaginationBeanParam params) throws ForbiddenException, NotFoundException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning provider services for given provider and employee using ProviderResource.ProviderServiceResource.getProviderServicesSuppliedByEmployee(userId, employeeId) method of REST API");

            // find provider entity for which to get associated provider services
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            // find employee entity for which to get associated provider services
            Employee employee = employeeFacade.find(employeeId);
            if (employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            // find provider services by given criteria (provider and employee)
            ResourceList<ProviderService> providerServices = new ResourceList<>(
                    providerServiceFacade.findByProviderAndEmployee(provider, employee, params.getOffset(), params.getLimit()));

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(providerServices, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providerServices).build();
        }
    }

    public class ProviderRatingResource {
        /**
         * Method returns subset of Provider Rating entities for given Provider.
         * The provider id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderRatings(@PathParam("userId") Long userId,
                                           @BeanParam ProviderRatingBeanParam params) throws NotFoundException, ForbiddenException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning subset of Provider Rating entities for given Provider using ProviderResource.ProviderRatingResource.getProviderRatings(userId) method of REST API");

            // find provider entity for which to get associated provider ratings
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            // calculate number of filter query params
            Integer noOfParams = params.getUriInfo().getQueryParameters().size();
            if (params.getOffset() != null) noOfParams -= 1;
            if (params.getLimit() != null) noOfParams -= 1;

            ResourceList<ProviderRating> providerRatings = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Provider> providers = new ArrayList<>();
                providers.add(provider);

                // get provider ratings for given provider and filter params
                providerRatings = new ResourceList<>(
                        providerRatingFacade.findByMultipleCriteria(params.getClients(), providers, params.getMinRating(), params.getMaxRating(),
                                params.getExactRating(), params.getClientComment(), params.getProviderDementi(), params.getOffset(), params.getLimit())
                );

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get provider ratings for given provider
                providerRatings = new ResourceList<>(providerRatingFacade.findByProvider(provider, params.getOffset(), params.getLimit()));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderRatingResource.populateWithHATEOASLinks(providerRatings, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providerRatings).build();
        }

        /**
         * Method that removes subset of Provider Rating entities from database for given Provider.
         * The provider id is passed through path param.
         */
        @DELETE
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response removeProviderRatings( @PathParam("userId") Long userId,
                                               @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "removing subset of Provider Rating entities for given Provider by executing ProviderResource.ProviderRatingResource.removeProviderRatings(userId) method of REST API");

            // find provider entity for which to remove provider ratings
            Provider provider = providerFacade.find(userId);
            if(provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            // remove all specified entities from database
            Integer noOfDeleted = providerRatingFacade.deleteByProvider(provider);

            // create response returning number of deleted entities
            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfDeleted), 200, "number of deleted provider ratings for provider with id " + userId);

            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Additional methods returning subset of resources based on given criteria
         * You can also achieve similar results by applying @QueryParams to generic method
         * returning all resources in order to filter and limit them.
         */

        /**
         * Method that counts Provider Rating entities for given Provider resource
         * The provider id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countProviderRatings(@PathParam("userId") Long userId,
                                             @BeanParam GenericBeanParam params) throws ForbiddenException, NotFoundException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning number of provider ratings for given provider by executing ProviderResource.ProviderRatingResource.countProviderRatings(userId) method of REST API");

            // find provider entity for which to count provider ratings
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(providerRatingFacade.countProviderRatings(provider)), 200,
                    "number of provider ratings for provider with id " + provider.getUserId());

            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method that returns average rating for Provider entity with given provider id.
         */
        @GET
        @Path("/average-rating")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getAverageProviderRating( @PathParam("userId") Long userId,
                                                            @BeanParam GenericBeanParam params) throws ForbiddenException, NotFoundException {

            if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning average rating for given provider using ProviderResource.ProviderRatingResource.getAverageProviderRating(userId) method of REST API");

            // find provider entity for which to calculate average rating
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(providerRatingFacade.findProviderAvgRating(provider)), 200,
                    "average rating for provider with id " + provider.getUserId());

            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method returns subset of Provider Rating entities for given Provider
         * that have been granted given rating.
         * The provider id and rating are passed through path params.
         */
        @GET
        @Path("/rated/{rating : \\d+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderRatingsByRating(@PathParam("userId") Long userId,
                                                   @PathParam("rating") Short rating,
                                                   @BeanParam PaginationBeanParam params) throws ForbiddenException, NotFoundException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning provider ratings for given provider and rating using ProviderResource.ProviderRatingResource.getProviderRatingsByRating(userId, rating) method of REST API");

            // find provider entity for which to get associated provider ratings
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            // find provider ratings by given criteria (provider and rating)
            ResourceList<ProviderRating> providerRatings = new ResourceList<>(
                    providerRatingFacade.findForProviderByRating(provider, rating, params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderRatingResource.populateWithHATEOASLinks(providerRatings, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providerRatings).build();
        }

        /**
         * Method returns subset of Provider Rating entities for given Provider
         * rated above given minimal rating.
         * The provider id and minimal rating are passed through path params.
         */
        @GET
        @Path("/rated-above/{minRating : \\d+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderRatingsAboveMinimalRating(@PathParam("userId") Long userId,
                                                             @PathParam("minRating") Short minRating,
                                                             @BeanParam PaginationBeanParam params) throws ForbiddenException, NotFoundException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning provider ratings for given provider rated above given minimal rating using " +
                    "ProviderResource.ProviderRatingResource.getProviderRatingsAboveMinimalRating(userId, minRating) method of REST API");

            // find provider entity for which to get associated provider ratings
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            // find provider ratings by given criteria (provider and min rating)
            ResourceList<ProviderRating> providerRatings = new ResourceList<>(
                    providerRatingFacade.findForProviderAboveRating(provider, minRating, params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderRatingResource.populateWithHATEOASLinks(providerRatings, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providerRatings).build();
        }

        /**
         * Method returns subset of Provider Rating entities for given Provider
         * rated below given maximal rating.
         * The provider id and maximal rating are passed through path params.
         */
        @GET
        @Path("/rated-below/{maxRating : \\d+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderRatingsBelowMaximalRating(@PathParam("userId") Long userId,
                                                             @PathParam("maxRating") Short maxRating,
                                                             @BeanParam PaginationBeanParam params) throws ForbiddenException, NotFoundException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning provider ratings for given provider rated below given maximal rating using " +
                    "ProviderResource.ProviderRatingResource.getProviderRatingsBelowMaximalRating(userId, maxRating) method of REST API");

            // find provider entity for which to get associated provider ratings
            Provider provider = providerFacade.find(userId);
            if (provider == null)
                throw new NotFoundException("Could not find provider for id " + userId + ".");

            // find provider ratings by given criteria (provider and max rating)
            ResourceList<ProviderRating> providerRatings = new ResourceList<>(
                    providerRatingFacade.findForProviderBelowRating(provider, maxRating, params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderRatingResource.populateWithHATEOASLinks(providerRatings, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providerRatings).build();

        }

    }

    public class ClientResource {

        public ClientResource() { }

        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderRatingClients( @PathParam("userId") Long providerId,
                                                  @BeanParam ClientBeanParam params ) throws ForbiddenException, NotFoundException {

            if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning clients rating given provider using ProviderResource.ClientResource.getProviderRatingClients(providerId) method of REST API");

            // find provider entity for which to get rating it clients
            Provider provider = providerFacade.find(providerId);
            if(provider == null)
                throw new NotFoundException("Could not find provider for id " + providerId + ".");

            // calculate number of filter query params
            Integer noOfParams = params.getUriInfo().getQueryParameters().size();
            if(params.getOffset() != null) noOfParams -= 1;
            if(params.getLimit() != null) noOfParams -= 1;

            ResourceList<Client> clients = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Provider> ratedProviders = new ArrayList<>();
                ratedProviders.add(provider);

                Address location = new Address(params.getStreet(), params.getHouseNumber(), params.getFlatNumber(), params.getZipCode(), params.getCity(), params.getState(), params.getCountry());
                Address delivery = new Address(params.getDeliveryStreet(), params.getDeliveryHouseNumber(), params.getDeliveryFlatNumber(), params.getDeliveryZipCode(), params.getDeliveryCity(), params.getDeliveryState(), params.getDeliveryCountry());


                // get clients for given rated provider filtered by given params
                clients = new ResourceList<>(
                        clientFacade.findByMultipleCriteria(params.getFirstName(), params.getLastName(), params.getFirmName(), params.getName(),
                                params.getDescription(), new HashSet<>(params.getClientTypes()), params.getOldestBirthDate(), params.getYoungestBirthDate(),
                                params.getYoungestAge(), params.getOldestAge(), location, delivery, params.getGender(), ratedProviders,
                                params.getRatedEmployees(), params.getOffset(), params.getLimit())
                );

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get clients for given rated provider without filtering
                clients = new ResourceList<>( clientFacade.findRatingProvider(provider, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ClientResource.populateWithHATEOASLinks(clients, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(clients).build();

        }

        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderRatingClientsEagerly( @PathParam("userId") Long providerId,
                                                         @BeanParam ClientBeanParam params ) throws ForbiddenException, NotFoundException {

            if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning clients rating given provider eagerly using ProviderResource.ClientResource.getProviderRatingClientsEagerly(providerId) method of REST API");

            // find provider entity for which to get rating it clients
            Provider provider = providerFacade.find(providerId);
            if(provider == null)
                throw new NotFoundException("Could not find provider for id " + providerId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ClientWrapper> clients = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Provider> ratedProviders = new ArrayList<>();
                ratedProviders.add(provider);

                Address location = new Address(params.getStreet(), params.getHouseNumber(), params.getFlatNumber(), params.getZipCode(), params.getCity(), params.getState(), params.getCountry());
                Address delivery = new Address(params.getDeliveryStreet(), params.getDeliveryHouseNumber(), params.getDeliveryFlatNumber(), params.getDeliveryZipCode(), params.getDeliveryCity(), params.getDeliveryState(), params.getDeliveryCountry());

                // get clients eagerly for given rated provider filtered by given params
                clients = new ResourceList<>(
                        ClientWrapper.wrap(
                                clientFacade.findByMultipleCriteriaEagerly(params.getFirstName(), params.getLastName(), params.getFirmName(), params.getName(),
                                        params.getDescription(), new HashSet<>(params.getClientTypes()), params.getOldestBirthDate(), params.getYoungestBirthDate(),
                                        params.getYoungestAge(), params.getOldestAge(), location, delivery, params.getGender(), ratedProviders,
                                        params.getRatedEmployees(), params.getOffset(), params.getLimit())
                        )
                );

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get clients eagerly for given rated provider without filtering
                clients = new ResourceList<>(
                        ClientWrapper.wrap(clientFacade.findRatingProviderEagerly(provider, params.getOffset(), params.getLimit()))
                );

            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ClientResource.populateWithHATEOASLinks(clients, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(clients).build();
        }

        /**
         * Method that counts Client entities rating given Provider.
         * The provider id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countClientsRatingProvider( @PathParam("userId") Long providerId,
                                                    @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of clients for given rated by them provider using " +
                    "ProviderResource.ClientResource.countClientsRatingProvider(providerId) method of REST API");

            // find provider entity for which to count rating it clients
            Provider provider = providerFacade.find(providerId);
            if(provider == null)
                throw new NotFoundException("Could not find provider for id " + providerId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(clientFacade.countByRatedProvider(provider)),
                    200, "number of clients rating provider with id " + provider.getUserId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    public class ServicePointPhotoResource {

        public ServicePointPhotoResource() { }

        /**
         * Method returns subset of Service Point Photo entities for given Provider entity.
         * The provider id is passed through path param.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServicePointPhotos( @PathParam("userId") Long providerId,
                                                       @BeanParam ServicePointPhotoBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning service point photos for given provider using " +
                    "ProviderResource.ServicePointPhotoResource.getProviderServicePointPhotos(providerId) method of REST API");

            // find provider entity for which to get associated service point photos
            Provider provider = providerFacade.find(providerId);
            if(provider == null)
                throw new NotFoundException("Could not find provider for id " + providerId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ServicePointPhoto> photos = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Provider> providers = new ArrayList<>();
                providers.add(provider);

                // get service point photos for given provider filtered by given params

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getFileNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                        throw new BadRequestException("Query params cannot include keywords and fileNames or descriptions at the same time.");

                    if( RESTToolkit.isSet(params.getTagNames()) ) {
                        // find by keywords and tag names
                        photos = new ResourceList<>(
                                servicePointPhotoFacade.findByMultipleCriteria(params.getKeywords(), params.getTagNames(), params.getServicePoints(),
                                        providers, params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                        );
                    } else {
                        // find only by keywords
                        photos = new ResourceList<>(
                                servicePointPhotoFacade.findByMultipleCriteria(params.getKeywords(), params.getServicePoints(), providers,
                                        params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                        );
                    }
                } else {
                    // find by fileNames, descriptions or tagNames
                    photos = new ResourceList<>(
                            servicePointPhotoFacade.findByMultipleCriteria(params.getFileNames(), params.getDescriptions(), params.getTagNames(),
                                    params.getServicePoints(), providers, params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                    );
                }
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get service point photos for given provider without filtering
                photos = new ResourceList<>( servicePointPhotoFacade.findByProvider(provider, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointPhotoResource.populateWithHATEOASLinks(photos, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(photos).build();
        }

        /**
         * Method returns subset of Service Point Photo entities for given Provider fetching them eagerly
         * The provider id is passed through path param.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServicePointPhotosEagerly( @PathParam("userId") Long providerId,
                                                              @BeanParam ServicePointPhotoBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException  {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning service point photos eagerly for given provider using " +
                    "ProviderResource.ServicePointPhotoResource.getProviderServicePointPhotosEagerly(providerId) method of REST API");

            // find provider entity for which to get associated service point photos
            Provider provider = providerFacade.find(providerId);
            if(provider == null)
                throw new NotFoundException("Could not find provider for id " + providerId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ServicePointPhotoWrapper> photos = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Provider> providers = new ArrayList<>();
                providers.add(provider);

                // get service point photos eagerly for given provider filtered by given params

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getFileNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                        throw new BadRequestException("Query params cannot include keywords and fileNames or descriptions at the same time.");

                    if( RESTToolkit.isSet(params.getTagNames()) ) {
                        // find by keywords and tag names
                        photos = new ResourceList<>(
                                ServicePointPhotoWrapper.wrap(
                                        servicePointPhotoFacade.findByMultipleCriteriaEagerly(params.getKeywords(), params.getTagNames(), params.getServicePoints(),
                                                providers, params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                                )
                        );
                    } else {
                        // find only by keywords
                        photos = new ResourceList<>(
                                ServicePointPhotoWrapper.wrap(
                                        servicePointPhotoFacade.findByMultipleCriteriaEagerly(params.getKeywords(), params.getServicePoints(), providers,
                                                params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                                )
                        );
                    }
                } else {
                    // find by fileNames, descriptions or tagNames
                    photos = new ResourceList<>(
                            ServicePointPhotoWrapper.wrap(
                                    servicePointPhotoFacade.findByMultipleCriteriaEagerly(params.getFileNames(), params.getDescriptions(), params.getTagNames(),
                                            params.getServicePoints(), providers, params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                            )
                    );
                }
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get service point photos eagerly for given provider without filtering
                photos = new ResourceList<>( ServicePointPhotoWrapper.wrap(servicePointPhotoFacade.findByProviderEagerly(provider, params.getOffset(), params.getLimit())) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointPhotoResource.populateWithHATEOASLinks(photos, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(photos).build();
        }

        /**
         * Method that count Service Point Photo entities for given Provider resource.
         * The provider id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countServicePointPhotosByProvider( @PathParam("userId") Long providerId,
                                                           @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of service point photos for given provider by executing " +
                    "ProviderResource.ServicePointPhotoResource.countServicePointPhotosByProvider(providerId) method of REST API");

            // find provider entity for which to count service point photos
            Provider provider = providerFacade.find(providerId);
            if(provider == null)
                throw new NotFoundException("Could not find provider for id " + providerId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(servicePointPhotoFacade.countByProvider(provider)), 200, "number of service point photos for provider with id " + provider.getUserId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    public class VirtualTourResource {

        public VirtualTourResource() { }

        /**
         * Method returns subset of Virtual Tour entities for given Provider entity.
         * The provider id is passed through path param.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderVirtualTours( @PathParam("userId") Long providerId,
                                                 @BeanParam VirtualTourBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning virtual tours for given provider using " +
                    "ProviderResource.VirtualTourResource.getProviderVirtualTours(providerId) method of REST API");

            // find provider entity for which to get associated virtual tours
            Provider provider = providerFacade.find(providerId);
            if(provider == null)
                throw new NotFoundException("Could not find provider for id " + providerId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<VirtualTour> virtualTours = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Provider> providers = new ArrayList<>();
                providers.add(provider);

                // get virtual tours for given provider filtered by given params

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getFileNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                        throw new BadRequestException("Query params cannot include keywords and fileNames or descriptions at the same time.");

                    if( RESTToolkit.isSet(params.getTagNames()) ) {
                        // find by keywords and tag names
                        virtualTours = new ResourceList<>(
                                virtualTourFacade.findByMultipleCriteria(params.getKeywords(), params.getTagNames(), params.getServicePoints(),
                                        providers, params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                        );
                    } else {
                        // find only by keywords
                        virtualTours = new ResourceList<>(
                                virtualTourFacade.findByMultipleCriteria(params.getKeywords(), params.getServicePoints(), providers,
                                        params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                        );
                    }
                } else {
                    // find by fileNames, descriptions or tagNames
                    virtualTours = new ResourceList<>(
                            virtualTourFacade.findByMultipleCriteria(params.getFileNames(), params.getDescriptions(), params.getTagNames(),
                                    params.getServicePoints(), providers, params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                    );
                }
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get virtual tours for given provider without filtering (eventually paginated)
                virtualTours = new ResourceList<>( virtualTourFacade.findByProvider(provider, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.VirtualTourResource.populateWithHATEOASLinks(virtualTours, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(virtualTours).build();
        }

        /**
         * Method returns subset of Virtual Tour entities for given Provider fetching them eagerly.
         * The provider id is passed through path param.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderVirtualToursEagerly( @PathParam("userId") Long providerId,
                                                        @BeanParam VirtualTourBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning virtual tours eagerly for given provider using " +
                    "ProviderResource.VirtualTourResource.getProviderVirtualToursEagerly(providerId) method of REST API");

            // find provider entity for which to get associated virtual tours
            Provider provider = providerFacade.find(providerId);
            if(provider == null)
                throw new NotFoundException("Could not find provider for id " + providerId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<VirtualTourWrapper> virtualTours = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Provider> providers = new ArrayList<>();
                providers.add(provider);

                // get virtual tours eagerly for given provider filtered by given params

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getFileNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                        throw new BadRequestException("Query params cannot include keywords and fileNames or descriptions at the same time.");

                    if( RESTToolkit.isSet(params.getTagNames()) ) {
                        // find by keywords and tag names
                        virtualTours = new ResourceList<>(
                                VirtualTourWrapper.wrap(
                                        virtualTourFacade.findByMultipleCriteriaEagerly(params.getKeywords(), params.getTagNames(), params.getServicePoints(),
                                                providers, params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                                )
                        );
                    } else {
                        // find only by keywords
                        virtualTours = new ResourceList<>(
                                VirtualTourWrapper.wrap(
                                        virtualTourFacade.findByMultipleCriteriaEagerly(params.getKeywords(), params.getServicePoints(), providers,
                                                params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                                )
                        );
                    }
                } else {
                    // find by fileNames, descriptions or tagNames
                    virtualTours = new ResourceList<>(
                            VirtualTourWrapper.wrap(
                                    virtualTourFacade.findByMultipleCriteriaEagerly(params.getFileNames(), params.getDescriptions(), params.getTagNames(),
                                            params.getServicePoints(), providers, params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                            )
                    );
                }
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get virtual tours eagerly for given provider without filtering (eventually paginated)
                virtualTours = new ResourceList<>( VirtualTourWrapper.wrap(virtualTourFacade.findByProviderEagerly(provider, params.getOffset(), params.getLimit())) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.VirtualTourResource.populateWithHATEOASLinks(virtualTours, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(virtualTours).build();
        }

        /**
         * Method that count Virtual Tour entities for given Provider resource.
         * The provider id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countVirtualToursByProvider( @PathParam("userId") Long providerId,
                                                     @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of virtual tours for given provider by executing " +
                    "ProviderResource.VirtualTourResource.countVirtualToursByProvider(providerId) method of REST API");

            // find provider entity for which to count virtual tours
            Provider provider = providerFacade.find(providerId);
            if(provider == null)
                throw new NotFoundException("Could not find provider for id " + providerId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(virtualTourFacade.countByProvider(provider)), 200,
                    "number of virtual tours for provider with id " + provider.getUserId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    public class ServiceResource {

        public ServiceResource() { }

        /**
         * Method returns subset of Service entities for given Provider entity.
         * The provider id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServices( @PathParam("userId") Long providerId,
                                             @BeanParam ServiceBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning services for given provider using " +
                    "ProviderResource.ServiceResource.getProviderServices(providerId) method of REST API");

            // find provider entity for which to get associated services
            Provider provider = providerFacade.find(providerId);
            if(provider == null)
                throw new NotFoundException("Could not find provider for id " + providerId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<Service> services = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Provider> providers = new ArrayList<>();
                providers.add(provider);

                // get services for given provider filtered by given query params

                utx.begin();

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                        throw new BadRequestException("Query params cannot include keywords and names or descriptions at the same time.");

                    // find only by keywords
                    services = new ResourceList<>(
                           serviceFacade.findByMultipleCriteria(params.getKeywords(), params.getServiceCategories(), providers,
                                   params.getEmployees(), params.getWorkStations(), params.getServicePoints(), params.getEmployeeTerms(),
                                   params.getTerms(), params.getOffset(), params.getLimit())
                    );

                } else {
                    // find by names, descriptions
                    services = new ResourceList<>(
                            serviceFacade.findByMultipleCriteria(params.getNames(), params.getDescriptions(), params.getServiceCategories(),
                                    providers, params.getEmployees(), params.getWorkStations(), params.getServicePoints(), params.getEmployeeTerms(),
                                    params.getTerms(), params.getOffset(), params.getLimit())
                    );
                }

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get services for given provider without filtering (eventually paginated)
                services = new ResourceList<>( serviceFacade.findByProvider(provider, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServiceResource.populateWithHATEOASLinks(services, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(services).build();
        }


        /**
         * Method returns subset of Service entities for given Provider fetching them eagerly.
         * The provider id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderServicesEagerly( @PathParam("userId") Long providerId,
                                                    @BeanParam ServiceBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning services eagerly for given provider using " +
                    "ProviderResource.ServiceResource.getProviderServicesEagerly(providerId) method of REST API");

            // find provider entity for which to get associated services
            Provider provider = providerFacade.find(providerId);
            if(provider == null)
                throw new NotFoundException("Could not find provider for id " + providerId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ServiceWrapper> services = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Provider> providers = new ArrayList<>();
                providers.add(provider);

                // get services eagerly for given provider filtered by given query params

                utx.begin();

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                        throw new BadRequestException("Query params cannot include keywords and names or descriptions at the same time.");

                    // find only by keywords
                    services = new ResourceList<>(
                            ServiceWrapper.wrap(
                                    serviceFacade.findByMultipleCriteriaEagerly(params.getKeywords(), params.getServiceCategories(), providers,
                                            params.getEmployees(), params.getWorkStations(), params.getServicePoints(), params.getEmployeeTerms(),
                                            params.getTerms(), params.getOffset(), params.getLimit())
                            )
                    );

                } else {
                    // find by names, descriptions
                    services = new ResourceList<>(
                            ServiceWrapper.wrap(
                                    serviceFacade.findByMultipleCriteriaEagerly(params.getNames(), params.getDescriptions(), params.getServiceCategories(),
                                            providers, params.getEmployees(), params.getWorkStations(), params.getServicePoints(), params.getEmployeeTerms(),
                                            params.getTerms(), params.getOffset(), params.getLimit())
                            )
                    );
                }

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get services eagerly for given provider without filtering (eventually paginated)
                services = new ResourceList<>( ServiceWrapper.wrap(serviceFacade.findByProviderEagerly(provider, params.getOffset(), params.getLimit())) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServiceResource.populateWithHATEOASLinks(services, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(services).build();
        }

        /**
         * Method that counts Service entities for given Provider resource.
         * The provider id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countServicesByProvider( @PathParam("userId") Long providerId,
                                                 @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of services for given provider by executing " +
                    "ProviderResource.ServiceResource.countServicesByProvider(providerId) method of REST API");

            // find provider entity for which to count services
            Provider provider = providerFacade.find(providerId);
            if(provider == null)
                throw new NotFoundException("Could not find provider for id " + providerId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(serviceFacade.countByProvider(provider)), 200,
                    "number of services for provider with id " + provider.getUserId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    public class TransactionResource {

        public TransactionResource() { }

        /**
         * Method returns subset of Transaction entities for given Provider entity.
         * The provider id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderTransactions( @PathParam("userId") Long providerId,
                                                 @BeanParam TransactionBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning transactions for given provider using " +
                    "ProviderResource.TransactionResource.getProviderTransactions(providerId) method of REST API");

            // find provider entity for which to get associated transactions
            Provider provider = providerFacade.find(providerId);
            if(provider == null)
                throw new NotFoundException("Could not find provider for id " + providerId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<Transaction> transactions = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Provider> providers = new ArrayList<>();
                providers.add(provider);

                // get transactions for given provider filtered by given query params

                utx.begin();

                transactions = new ResourceList<>(
                        transactionFacade.findByMultipleCriteria(params.getClients(), providers, params.getServices(), params.getServicePoints(),
                                params.getWorkStations(), params.getEmployees(), params.getProviderServices(), params.getTransactionTimePeriod(),
                                params.getBookedTimePeriod(), params.getTerms(), params.getPriceRange(), params.getCurrencyCodes(), params.getPaymentMethods(),
                                params.getPaid(), params.getOffset(), params.getLimit())
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get transactions for given provider without filtering (eventually paginated)
                transactions = new ResourceList<>( transactionFacade.findByProvider(provider, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.TransactionResource.populateWithHATEOASLinks(transactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(transactions).build();
        }

        /**
         * Method returns subset of Transaction entities for given Provider fetching them eagerly.
         * The provider id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderTransactionsEagerly( @PathParam("userId") Long providerId,
                                                        @BeanParam TransactionBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning transactions eagerly for given provider using " +
                    "ProviderResource.TransactionResource.getProviderTransactionsEagerly(providerId) method of REST API");

            // find provider entity for which to get associated transactions
            Provider provider = providerFacade.find(providerId);
            if(provider == null)
                throw new NotFoundException("Could not find provider for id " + providerId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<TransactionWrapper> transactions = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Provider> providers = new ArrayList<>();
                providers.add(provider);

                // get transactions eagerly for given provider filtered by given query params

                utx.begin();

                transactions = new ResourceList<>(
                        TransactionWrapper.wrap(
                                transactionFacade.findByMultipleCriteriaEagerly(params.getClients(), providers, params.getServices(),
                                        params.getServicePoints(), params.getWorkStations(), params.getEmployees(), params.getProviderServices(),
                                        params.getTransactionTimePeriod(), params.getBookedTimePeriod(), params.getTerms(), params.getPriceRange(),
                                        params.getCurrencyCodes(), params.getPaymentMethods(), params.getPaid(), params.getOffset(), params.getLimit())
                        )
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get transactions eagerly for given provider without filtering (eventually paginated)
                transactions = new ResourceList<>( TransactionWrapper.wrap(transactionFacade.findByProviderEagerly(provider, params.getOffset(), params.getLimit())) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.TransactionResource.populateWithHATEOASLinks(transactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(transactions).build();
        }

        /**
         * Method that counts Transaction entities for given Provider resource.
         * The provider id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countTransactionsByProvider( @PathParam("userId") Long providerId,
                                                     @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of transactions for given provider by executing " +
                    "ProviderResource.TransactionResource.countTransactionsByProvider(providerId) method of REST API");

            // find provider entity for which to count transactions
            Provider provider = providerFacade.find(providerId);
            if(provider == null)
                throw new NotFoundException("Could not find provider for id " + providerId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(transactionFacade.countByProvider(provider)), 200,
                    "number of transactions for provider with id " + provider.getUserId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    public class HistoricalTransactionResource {

        public HistoricalTransactionResource() { }

        /**
         * Method returns subset of Historical Transaction entities for given Provider entity.
         * The provider id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderHistoricalTransactions( @PathParam("userId") Long providerId,
                                                           @BeanParam HistoricalTransactionBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning historical transactions for given provider using " +
                    "ProviderResource.HistoricalTransactionResource.getProviderHistoricalTransactions(providerId) method of REST API");

            // find provider entity for which to get associated historical transactions
            Provider provider = providerFacade.find(providerId);
            if(provider == null)
                throw new NotFoundException("Could not find provider for id " + providerId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<HistoricalTransaction> historicalTransactions = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Provider> providers = new ArrayList<>();
                providers.add(provider);

                // get historical transactions for given provider filtered by given query params

                utx.begin();

                historicalTransactions = new ResourceList<>(
                        historicalTransactionFacade.findByMultipleCriteria(params.getClients(), providers, params.getServices(), params.getServicePoints(),
                                params.getWorkStations(), params.getEmployees(), params.getProviderServices(), params.getTransactionTimePeriod(),
                                params.getBookedTimePeriod(), params.getTerms(), params.getPriceRange(), params.getCurrencyCodes(), params.getPaymentMethods(),
                                params.getPaid(), params.getCompletionStatuses(), params.getClientRatingRange(), params.getClientComments(),
                                params.getProviderRatingRange(), params.getProviderDementis(), params.getOffset(), params.getLimit())
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get historical transactions for given provider without filtering (eventually paginated)
                historicalTransactions = new ResourceList<>(historicalTransactionFacade.findByProvider(provider, params.getOffset(), params.getLimit()));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(historicalTransactions).build();
        }

        /**
         * Method returns subset of Historical Transaction entities for given Provider fetching them eagerly.
         * The provider id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getProviderHistoricalTransactionsEagerly( @PathParam("userId") Long providerId,
                                                                  @BeanParam HistoricalTransactionBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning historical transactions eagerly for given provider using " +
                    "ProviderResource.HistoricalTransactionResource.getProviderHistoricalTransactionsEagerly(providerId) method of REST API");

            // find provider entity for which to get associated historical transactions
            Provider provider = providerFacade.find(providerId);
            if(provider == null)
                throw new NotFoundException("Could not find provider for id " + providerId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<HistoricalTransactionWrapper> historicalTransactions = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Provider> providers = new ArrayList<>();
                providers.add(provider);

                // get historical transactions eagerly for given provider filtered by given query params

                utx.begin();

                historicalTransactions = new ResourceList<>(
                        HistoricalTransactionWrapper.wrap(
                                historicalTransactionFacade.findByMultipleCriteriaEagerly(params.getClients(), providers, params.getServices(), params.getServicePoints(),
                                        params.getWorkStations(), params.getEmployees(), params.getProviderServices(), params.getTransactionTimePeriod(),
                                        params.getBookedTimePeriod(), params.getTerms(), params.getPriceRange(), params.getCurrencyCodes(), params.getPaymentMethods(),
                                        params.getPaid(), params.getCompletionStatuses(), params.getClientRatingRange(), params.getClientComments(),
                                        params.getProviderRatingRange(), params.getProviderDementis(), params.getOffset(), params.getLimit())
                        )
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get historical transactions eagerly for given provider without filtering (eventually paginated)
                historicalTransactions = new ResourceList<>(HistoricalTransactionWrapper.wrap(historicalTransactionFacade.findByProviderEagerly(provider, params.getOffset(), params.getLimit())));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(historicalTransactions).build();
        }

        /**
         * Method that counts Historical Transaction entities for given Provider resource.
         * The provider id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countHistoricalTransactionsByProvider( @PathParam("userId") Long providerId,
                                                               @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of historical transactions for given provider by executing " +
                    "ProviderResource.HistoricalTransactionResource.countHistoricalTransactionsByProvider(providerId) method of REST API");

            // find provider entity for which to count historical transactions
            Provider provider = providerFacade.find(providerId);
            if(provider == null)
                throw new NotFoundException("Could not find provider for id " + providerId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(historicalTransactionFacade.countByProvider(provider)), 200,
                    "number of historical transactions for provider with id " + provider.getUserId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }

    }
}
