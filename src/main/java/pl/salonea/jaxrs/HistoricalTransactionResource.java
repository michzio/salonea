package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.EmployeeFacade;
import pl.salonea.ejb.stateless.HistoricalTransactionFacade;
import pl.salonea.entities.*;
import pl.salonea.entities.idclass.TransactionId;
import pl.salonea.enums.CurrencyCode;
import pl.salonea.enums.TransactionCompletionStatus;
import pl.salonea.jaxrs.bean_params.*;
import pl.salonea.jaxrs.exceptions.*;
import pl.salonea.jaxrs.exceptions.BadRequestException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.HistoricalTransactionWrapper;

import javax.inject.Inject;
import javax.transaction.*;
import javax.transaction.NotSupportedException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by michzio on 15/05/2016.
 */
@Path("/historical-transactions")
public class HistoricalTransactionResource {

    private static final Logger logger = Logger.getLogger(HistoricalTransactionResource.class.getName());

    @Inject
    private UserTransaction utx;

    @Inject
    private HistoricalTransactionFacade historicalTransactionFacade;
    @Inject
    private EmployeeFacade employeeFacade;

    @Inject
    private ClientResource clientResource;

    /**
     *  Alternative methods to access Historical Transaction resource
     */
    @GET
    @Path("/{clientId: \\d+}+{transactionNumber: \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getHistoricalTransaction( @PathParam("clientId") Long clientId,
                                              @PathParam("transactionNumber") Integer transactionNumber,
                                              @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        return clientResource.getHistoricalTransactionResource().getHistoricalTransaction(clientId, transactionNumber, params);
    }

    @GET
    @Path("/{clientId: \\d+}+{transactionNumber: \\d+}/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getHistoricalTransactionEagerly( @PathParam("clientId") Long clientId,
                                                     @PathParam("transactionNumber") Integer transactionNumber,
                                                     @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        return clientResource.getHistoricalTransactionResource().getHistoricalTransactionEagerly(clientId, transactionNumber, params);
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createHistoricalTransaction(HistoricalTransaction historicalTransaction,
                                                @BeanParam GenericBeanParam params) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        return clientResource.getHistoricalTransactionResource().createHistoricalTransaction(historicalTransaction.getClient().getClientId(), historicalTransaction, params);
    }

    @PUT
    @Path("/{clientId: \\d+}+{transactionNumber: \\d+}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateHistoricalTransaction( @PathParam("clientId") Long clientId,
                                                 @PathParam("transactionNumber")  Integer transactionNumber,
                                                 HistoricalTransaction historicalTransaction,
                                                 @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        return clientResource.getHistoricalTransactionResource().updateHistoricalTransaction(clientId, transactionNumber, historicalTransaction, params);
    }

    @DELETE
    @Path("/{clientId: \\d+}+{transactionNumber: \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeHistoricalTransaction( @PathParam("clientId") Long clientId,
                                                 @PathParam("transactionNumber")  Integer transactionNumber,
                                                 @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException, InternalServerErrorException,
    /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

        return clientResource.getHistoricalTransactionResource().removeHistoricalTransaction(clientId, transactionNumber, params);
    }

    /**
     * Method returns all Historical Transaction entities.
     * They can be additionally filtered and paginated by @QueryParams
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getHistoricalTransactions(@BeanParam HistoricalTransactionBeanParam params) throws ForbiddenException,
    /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Historical Transactions by executing HistoricalTransactionResource.getHistoricalTransactions() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<HistoricalTransaction> historicalTransactions = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            utx.begin();

            // get historical transactions filtered by criteria provided in query params
            historicalTransactions = new ResourceList<>(
                    historicalTransactionFacade.findByMultipleCriteria(params.getClients(), params.getProviders(), params.getServices(),
                            params.getServicePoints(), params.getWorkStations(), params.getEmployees(), params.getProviderServices(),
                            params.getTransactionTimePeriod(), params.getBookedTimePeriod(), params.getTerms(), params.getPriceRange(),
                            params.getCurrencyCodes(), params.getPaymentMethods(), params.getPaid(), params.getCompletionStatuses(),
                            params.getClientRatingRange(), params.getClientComments(), params.getProviderRatingRange(), params.getProviderDementis(),
                            params.getOffset(), params.getLimit())
            );

            utx.commit();

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all historical transactions without filtering (eventually paginated)
            historicalTransactions = new ResourceList<>( historicalTransactionFacade.findAll(params.getOffset(), params.getLimit())  );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(historicalTransactions).build();
    }

    @GET
    @Path("/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getHistoricalTransactionsEagerly( @BeanParam HistoricalTransactionBeanParam params ) throws ForbiddenException,
    /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Historical Transactions eagerly by executing HistoricalTransactionResource.getHistoricalTransactionsEagerly() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<HistoricalTransactionWrapper> historicalTransactions = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            utx.begin();

            // get historical transactions eagerly filtered by criteria provided in query params
            historicalTransactions = new ResourceList<>(
                    HistoricalTransactionWrapper.wrap(
                            historicalTransactionFacade.findByMultipleCriteriaEagerly(params.getClients(), params.getProviders(), params.getServices(),
                                    params.getServicePoints(), params.getWorkStations(), params.getEmployees(), params.getProviderServices(),
                                    params.getTransactionTimePeriod(), params.getBookedTimePeriod(), params.getTerms(), params.getPriceRange(),
                                    params.getCurrencyCodes(), params.getPaymentMethods(), params.getPaid(), params.getCompletionStatuses(),
                                    params.getClientRatingRange(), params.getClientComments(), params.getProviderRatingRange(), params.getProviderDementis(),
                                    params.getOffset(), params.getLimit())
                    )
            );

            utx.commit();

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all historical transactions eagerly without filtering (eventually paginated)
            historicalTransactions = new ResourceList<>( HistoricalTransactionWrapper.wrap(historicalTransactionFacade.findAllEagerly(params.getOffset(), params.getLimit())) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(historicalTransactions).build();
    }

    /**
     * Additional methods returning a subset of resources based on given criteria
     * you can achieve similar results by applying @QueryParams to generic method
     * returning all resources in order to filter and limit them
     */

    /**
     * Method returns number of Historical Transaction entities in database
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countHistoricalTransactions( @BeanParam GenericBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning number of historical transactions by executing HistoricalTransactionResource.countHistoricalTransactions() method of REST API");

        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(historicalTransactionFacade.count()), 200, "number of historical transactions");
        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     *  Method returns subset of Historical Transaction entities for transaction time in given date range (startDate, endDate).
     */
    @GET
    @Path("/by-transaction-time")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getHistoricalTransactionsByTransactionTime( @BeanParam DateRangeBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning historical transactions for given transaction time (startDate, endDate) using " +
                "HistoricalTransactionResource.getHistoricalTransactionsByTransactionTime(transactionTime) method of REST API");

        RESTToolkit.validateDateRange(params); // i.e. startDate and endDate

        // find historical transactions for given date range (startDate, endDate) of transaction time
        ResourceList<HistoricalTransaction> historicalTransactions = new ResourceList<>(
                historicalTransactionFacade.findByTransactionTime(params.getStartDate(), params.getEndDate(), params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(historicalTransactions).build();
    }

    /**
     * Method returns subset of Historical Transaction entities for booked time in given date range (startDate, endDate).
     */
    @GET
    @Path("/by-booked-time")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getHistoricalTransactionsByBookedTime( @BeanParam DateRangeBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning historical transactions for given booked time (startDate, endDate) using " +
                "HistoricalTransactionResource.getHistoricalTransactionsByBookedTime(bookedTime) method of REST API");

        RESTToolkit.validateDateRange(params); // i.e. startDate and endDate

        // find historical transactions for given date range (startDate, endDate) of booked time
        ResourceList<HistoricalTransaction> historicalTransactions = new ResourceList<>(
                historicalTransactionFacade.findByBookedTime(params.getStartDate(), params.getEndDate(), params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(historicalTransactions).build();
    }

    /**
     * Method returns subset of Historical Transaction entities that have already been paid.
     */
    @GET
    @Path("/paid")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getHistoricalTransactionsPaid( @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning paid historical transactions using HistoricalTransactionResource.getHistoricalTransactionsPaid() method of REST API");

        // find paid historical transactions
        ResourceList<HistoricalTransaction> historicalTransactions = new ResourceList<>(
                historicalTransactionFacade.findOnlyPaid(params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(historicalTransactions).build();
    }

    /**
     * Method returns subset of Historical Transaction entities that haven't been paid yet.
     */
    @GET
    @Path("/unpaid")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getHistoricalTransactionsUnpaid( @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning unpaid historical transactions using HistoricalTransactionResource.getHistoricalTransactionsUnpaid() method of REST API");

        // find unpaid historical transactions
        ResourceList<HistoricalTransaction> historicalTransactions = new ResourceList<>(
                historicalTransactionFacade.findOnlyUnpaid(params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(historicalTransactions).build();
    }

    /**
     * Method returns subset of Historical Transaction entities for given price
     * range (and optionally currency code).
     * The price range (and optionally currency code) are passed through query params.
     */
    @GET
    @Path("/by-price")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getHistoricalTransactionsByPrice( @BeanParam PriceRangeBeanParam params,
                                                      @QueryParam("currency") CurrencyCode currencyCode ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning historical transactions for given price range (minPrice, maxPrice) and optionally currency code using " +
                "HistoricalTransactionResource.getHistoricalTransactionsByPrice(priceRange, currencyCode) method of REST API");

        RESTToolkit.validatePriceRange(params); // i.e. minPrice and maxPrice

        ResourceList<HistoricalTransaction> historicalTransactions = null;

        if(currencyCode != null) {
            // find historical transactions by given criteria (price, currency code)
            historicalTransactions = new ResourceList<>(
                    historicalTransactionFacade.findByPriceRangeAndCurrencyCode(params.getMinPrice(), params.getMaxPrice(),
                            currencyCode, params.getOffset(), params.getLimit())
            );
        } else {
            // find historical transactions by given criteria (price)
            historicalTransactions = new ResourceList<>(
                    historicalTransactionFacade.findByPriceRange(params.getMinPrice(), params.getMaxPrice(),
                            params.getOffset(), params.getLimit())
            );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(historicalTransactions).build();
    }

    /**
     * Method returns subset of Historical Transaction entities for given currency code.
     * The currency code is passed through path param.
     */
    @GET
    @Path("/by-currency/{currencyCode : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getHistoricalTransactionsByCurrency( @PathParam("currencyCode") CurrencyCode currencyCode,
                                                         @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning historical transactions for given currency code using " +
                "HistoricalTransactionResource.getHistoricalTransactionsByCurrency(currencyCode) method of REST API");

        // find historical transactions by currency code
        ResourceList<HistoricalTransaction> historicalTransactions = new ResourceList<>(
                historicalTransactionFacade.findByCurrencyCode(currencyCode, params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(historicalTransactions).build();
    }

    /**
     * Method returns subset of Historical Transaction entities for given completion status.
     * The completion status is passed through path param.
     */
    @GET
    @Path("/by-completion-status/{completionStatus : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getHistoricalTransactionsByCompletionStatus( @PathParam("completionStatus") TransactionCompletionStatus completionStatus,
                                                                 @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning historical transactions for given completion status using " +
                "HistoricalTransactionResource.getHistoricalTransactionsByCompletionStatus(completionStatus) method of REST API");

        // find historical transactions by completion status
        ResourceList<HistoricalTransaction> historicalTransactions = new ResourceList<>(
                historicalTransactionFacade.findByCompletionStatus(completionStatus, params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(historicalTransactions).build();
    }

    /**
     * Method returns subset of Historical Transaction entities for given client rating range (minRating, maxRating).
     * The client rating range is passed through query params.
     */
    @GET
    @Path("/by-client-rating")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getHistoricalTransactionsByClientRating( @BeanParam RatingBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning historical transactions for given client rating range (minRating, maxRating) using " +
                "HistoricalTransactionResource.getHistoricalTransactionsByClientRating(clientRatingRange) method of REST API");

        if(params.getExactRating() != null) {
            params.setMinRating(params.getExactRating());
            params.setMaxRating(params.getExactRating());
        }

        // check client rating params correctness
        if(params.getMinRating() == null || params.getMaxRating() == null)
            throw new BadRequestException("Min rating and max rating (optionally exact rating) cannot be null.");

        if(params.getMaxRating() < params.getMinRating())
            throw new BadRequestException("Max rating cannot be less than min rating.");

        // find historical transactions by client rating range
        ResourceList<HistoricalTransaction> historicalTransactions = new ResourceList<>(
                historicalTransactionFacade.findByClientRatingRange(params.getMinRating(), params.getMaxRating(),
                        params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(historicalTransactions).build();
    }

    /**
     * Method returns subset of Historical Transaction entities for given client comment.
     * The client comment is passed through path param.
     */
    @GET
    @Path("/by-client-comment/{clientComment : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getHistoricalTransactionsByClientComment( @PathParam("clientComment") String clientComment,
                                                              @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning historical transactions for given client comment using " +
                "HistoricalTransactionResource.getHistoricalTransactionsByClientComment(clientComment) method of REST API");

        // find historical transactions by client comment
        ResourceList<HistoricalTransaction> historicalTransactions = new ResourceList<>(
                historicalTransactionFacade.findByClientComment(clientComment, params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(historicalTransactions).build();
    }

    /**
     * Method returns subset of Historical Transaction entities for given provider rating range (minRating, maxRating).
     * The provider rating range is passed through query params.
     */
    @GET
    @Path("/by-provider-rating")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getHistoricalTransactionsByProviderRating( @BeanParam RatingBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning historical transactions for given provider rating range (minRating, maxRating) using " +
                "HistoricalTransactionResource.getHistoricalTransactionsByProviderRating(providerRatingRange) method of REST API");

        if(params.getExactRating() != null) {
            params.setMinRating(params.getExactRating());
            params.setMaxRating(params.getExactRating());
        }

        // check provider rating params correctness
        if(params.getMinRating() == null || params.getMaxRating() == null)
            throw new BadRequestException("Min rating and max rating (optionally exact rating) cannot be null.");

        if(params.getMaxRating() < params.getMinRating())
            throw new BadRequestException("Max rating cannot be less than min rating.");

        // find historical transactions by provider rating range
        ResourceList<HistoricalTransaction> historicalTransactions = new ResourceList<>(
                historicalTransactionFacade.findByProviderRatingRange(params.getMinRating(), params.getMaxRating(),
                        params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(historicalTransactions).build();
    }

    /**
     * Method returns subset of Historical Transaction entities for given provider dementi.
     * The provider dementi is passed through path param.
     */
    @GET
    @Path("/by-provider-dementi/{providerDementi : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getHistoricalTransactionsByProviderDementi( @PathParam("providerDementi") String providerDementi,
                                                                @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning historical transactions for given provider dementi using " +
                "HistoricalTransactionResource.getHistoricalTransactionsByProviderDementi(providerDementi) method of REST API");

        // find historical transactions by provider dementi
        ResourceList<HistoricalTransaction> historicalTransactions = new ResourceList<>(
                historicalTransactionFacade.findByProviderDementi(providerDementi, params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(historicalTransactions).build();
    }

    /**
     * related subresources (through relationships)
     */
    // TODO impl all related subresources

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList historicalTransactions, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(historicalTransactions, uriInfo, offset, limit);

        try {
            // count resources hypermedia link
            Method countMethod = HistoricalTransactionResource.class.getMethod("countHistoricalTransactions", GenericBeanParam.class);
            historicalTransactions.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(HistoricalTransactionResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            historicalTransactions.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(HistoricalTransactionResource.class).build()).rel("historical-transactions").build() );

            // get all resources eagerly hypermedia link
            Method historicalTransactionsEagerlyMethod = HistoricalTransactionResource.class.getMethod("getHistoricalTransactionsEagerly", HistoricalTransactionBeanParam.class);
            historicalTransactions.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(HistoricalTransactionResource.class).path(historicalTransactionsEagerlyMethod).build()).rel("historical-transactions-eagerly").build() );

            // get subset of resources hypermedia links

            // by-transaction-time

            // by-booked-time

            // paid

            // unpaid

            // by-price

            // by-currency

            // by-completion-status

            // by-client-rating

            // by-client-comment

            // by-provider-rating

            // by-provider-dementi

            // TODO

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for( Object object : historicalTransactions.getResources() ) {
            if (object instanceof HistoricalTransaction) {
                HistoricalTransactionResource.populateWithHATEOASLinks( (HistoricalTransaction) object, uriInfo);
            } else if (object instanceof HistoricalTransactionWrapper) {
                HistoricalTransactionResource.populateWithHATEOASLinks( (HistoricalTransactionWrapper) object, uriInfo );
            }
        }
    }

    /**
     * This method enables to populate each individual resource wrapper with hypermedia links
     */
    public static void populateWithHATEOASLinks(HistoricalTransactionWrapper historicalTransactionWrapper, UriInfo uriInfo) {

        HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactionWrapper.getHistoricalTransaction(), uriInfo);

        for(Employee employee : historicalTransactionWrapper.getEmployees())
            pl.salonea.jaxrs.EmployeeResource.populateWithHATEOASLinks(employee, uriInfo);
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(HistoricalTransaction historicalTransaction, UriInfo uriInfo) {

        try {
            // self link with pattern: http://localhost:port/app/rest/{resources}/{id1}+{id2}
            Method historicalTransactionMethod = HistoricalTransactionResource.class.getMethod("getHistoricalTransaction", Long.class, Integer.class, GenericBeanParam.class);
            historicalTransaction.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(HistoricalTransactionResource.class)
                    .path(historicalTransactionMethod)
                    .resolveTemplate("clientId", historicalTransaction.getClient().getClientId().toString())
                    .resolveTemplate("transactionNumber", historicalTransaction.getTransactionNumber().toString())
                    .build())
                    .rel("self").build());

            // TODO self alternative

            // collection link with pattern: http://localhost:port/app/rest/{resources}
            historicalTransaction.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(HistoricalTransactionResource.class)
                    .build())
                    .rel("historical-transactions").build() );

            // self eagerly link with pattern: http://localhost:port/app/rest/{resources}/{id1}+{id2}/eagerly
            Method historicalTransactionEagerlyMethod = HistoricalTransactionResource.class.getMethod("getHistoricalTransactionEagerly", Long.class, Integer.class, GenericBeanParam.class);
            historicalTransaction.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(HistoricalTransactionResource.class)
                    .path(historicalTransactionEagerlyMethod)
                    .resolveTemplate("clientId", historicalTransaction.getClient().getClientId().toString())
                    .resolveTemplate("transactionNumber", historicalTransaction.getTransactionNumber().toString())
                    .build())
                    .rel("historical-transaction-eagerly").build());

            // TODO self eagerly alternative

            // associated collections links with pattern: http://localhost:port/app/rest/{resources}/{id1}+{id2}/{relationship}

            /**
             * Employees that executed current HistoricalTransaction resource
             */
            // employees

            // employees eagerly

            // employees count

            // TODO

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
