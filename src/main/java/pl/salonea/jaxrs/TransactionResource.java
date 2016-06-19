package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.EmployeeFacade;
import pl.salonea.ejb.stateless.TransactionFacade;
import pl.salonea.entities.Employee;
import pl.salonea.entities.Transaction;
import pl.salonea.entities.idclass.TransactionId;
import pl.salonea.enums.CurrencyCode;
import pl.salonea.jaxrs.bean_params.*;
import pl.salonea.jaxrs.exceptions.*;
import pl.salonea.jaxrs.exceptions.BadRequestException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.TransactionWrapper;

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
@Path("/transactions")
public class TransactionResource {

    private static final Logger logger = Logger.getLogger(TransactionResource.class.getName());

    @Inject
    private UserTransaction utx;

    @Inject
    private TransactionFacade transactionFacade;
    @Inject
    private EmployeeFacade employeeFacade;

    @Inject
    private ClientResource clientResource;

    /**
     * Alternative methods to access Transaction resource
     */
    @GET
    @Path("/{clientId: \\d+}+{transactionNumber: \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTransaction(@PathParam("clientId") Long clientId,
                                   @PathParam("transactionNumber") Integer transactionNumber,
                                   @BeanParam GenericBeanParam params) throws ForbiddenException, NotFoundException {

        return clientResource.getTransactionResource().getTransaction(clientId, transactionNumber, params);
    }

    @GET
    @Path("/{clientId: \\d+}+{transactionNumber: \\d+}/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTransactionEagerly(@PathParam("clientId") Long clientId,
                                          @PathParam("transactionNumber") Integer transactionNumber,
                                          @BeanParam GenericBeanParam params) throws ForbiddenException, NotFoundException {

        return clientResource.getTransactionResource().getTransactionEagerly(clientId, transactionNumber, params);
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createTransaction(Transaction transaction,
                                      @BeanParam GenericBeanParam params) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        return clientResource.getTransactionResource().createTransaction(transaction.getClient().getClientId(), transaction, params);
    }

    @PUT
    @Path("/{clientId: \\d+}+{transactionNumber: \\d+}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateTransaction(@PathParam("clientId") Long clientId,
                                      @PathParam("transactionNumber") Integer transactionNumber,
                                      Transaction transaction,
                                      @BeanParam GenericBeanParam params) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        return clientResource.getTransactionResource().updateTransaction(clientId, transactionNumber, transaction, params);
    }

    @DELETE
    @Path("/{clientId: \\d+}+{transactionNumber: \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeTransaction(@PathParam("clientId") Long clientId,
                                      @PathParam("transactionNumber") Integer transactionNumber,
                                      @BeanParam GenericBeanParam params) throws ForbiddenException, NotFoundException, InternalServerErrorException,
    /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

        return clientResource.getTransactionResource().removeTransaction(clientId, transactionNumber, params);
    }

    /**
     * Method returns all Transaction entities.
     * They can be additionally filtered and paginated by @QueryParams
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTransactions(@BeanParam TransactionBeanParam params) throws ForbiddenException,
    /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Transactions by executing TransactionResource.getTransactions() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<Transaction> transactions = null;

        if (noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            utx.begin();

            // get transactions filtered by criteria provided in query params
            transactions = new ResourceList<>(
                    transactionFacade.findByMultipleCriteria(params.getClients(), params.getProviders(), params.getServices(),
                            params.getServicePoints(), params.getWorkStations(), params.getEmployees(), params.getProviderServices(),
                            params.getTransactionTimePeriod(), params.getBookedTimePeriod(), params.getTerms(), params.getPriceRange(),
                            params.getCurrencyCodes(), params.getPaymentMethods(), params.getPaid(), params.getOffset(), params.getLimit())
            );

            utx.commit();

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all transactions without filtering (eventually paginated)
            transactions = new ResourceList<>(transactionFacade.findAll(params.getOffset(), params.getLimit()));
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        TransactionResource.populateWithHATEOASLinks(transactions, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(transactions).build();
    }

    @GET
    @Path("/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTransactionsEagerly(@BeanParam TransactionBeanParam params) throws ForbiddenException,
    /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Transactions eagerly by executing TransactionResource.getTransactionsEagerly() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<TransactionWrapper> transactions = null;

        if (noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            utx.begin();

            // get transactions eagerly filtered by criteria provided in query params
            transactions = new ResourceList<>(
                    TransactionWrapper.wrap(
                            transactionFacade.findByMultipleCriteriaEagerly(params.getClients(), params.getProviders(), params.getServices(),
                                    params.getServicePoints(), params.getWorkStations(), params.getEmployees(), params.getProviderServices(),
                                    params.getTransactionTimePeriod(), params.getBookedTimePeriod(), params.getTerms(), params.getPriceRange(),
                                    params.getCurrencyCodes(), params.getPaymentMethods(), params.getPaid(), params.getOffset(), params.getLimit())
                    )
            );

            utx.commit();

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all transactions eagerly without filtering (eventually paginated)
            transactions = new ResourceList<>(TransactionWrapper.wrap(transactionFacade.findAllEagerly(params.getOffset(), params.getLimit())));
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        TransactionResource.populateWithHATEOASLinks(transactions, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(transactions).build();
    }

    /**
     * Additional methods returning a subset of resources based on given criteria
     * you can achieve similar results by applying @QueryParams to generic method
     * returning all resources in order to filter and limit them
     */

    /**
     * Method returns number of Transaction entities in database
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countTransactions(@BeanParam GenericBeanParam params) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning number of transactions by executing TransactionResource.countTransactions() method of REST API");

        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(transactionFacade.count()), 200, "number of transactions");
        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method returns subset of Transaction entities for transaction time in given date range (startDate, endDate).
     */
    @GET
    @Path("/by-transaction-time")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTransactionsByTransactionTime(@BeanParam DateRangeBeanParam params) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning transactions for given transaction time (startDate, endDate) using " +
                "TransactionResource.getTransactionsByTransactionTime(transactionTime) method of REST API");

        RESTToolkit.validateDateRange(params); // i.e. startDate and endDate

        // find transactions for given date range (startDate, endDate) of transaction time
        ResourceList<Transaction> transactions = new ResourceList<>(
                transactionFacade.findByTransactionTime(params.getStartDate(), params.getEndDate(), params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        TransactionResource.populateWithHATEOASLinks(transactions, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(transactions).build();
    }

    /**
     * Method returns subset of Transaction entities for booked time in given date range (startDate, endDate).
     */
    @GET
    @Path("/by-booked-time")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTransactionsByBookedTime( @BeanParam DateRangeBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning transactions for given booked time (startDate, endDate) using " +
                "TransactionResource.getTransactionsByBookedTime(bookedTime) method of REST API");

        RESTToolkit.validateDateRange(params); // i.e. startDate and endDate

        // find transactions for given date range (startDate, endDate) of booked time
        ResourceList<Transaction> transactions = new ResourceList<>(
                transactionFacade.findByBookedTime(params.getStartDate(), params.getEndDate(), params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        TransactionResource.populateWithHATEOASLinks(transactions, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(transactions).build();
    }

    /**
     * Method returns subset of Transaction entities that have already been paid.
     */
    @GET
    @Path("/paid")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTransactionsPaid( @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning paid transactions using TransactionResource.getTransactionsPaid() method of REST API");

        // find paid transactions
        ResourceList<Transaction> transactions = new ResourceList<>(
                transactionFacade.findOnlyPaid(params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        TransactionResource.populateWithHATEOASLinks(transactions, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(transactions).build();
    }

    /**
     * Method returns subset of Transaction entities that haven't been paid yet.
     */
    @GET
    @Path("/unpaid")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTransactionsUnpaid( @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning unpaid transactions using TransactionResource.getTransactionsUnpaid() method of REST API");

        // find unpaid transactions
        ResourceList<Transaction> transactions = new ResourceList<>(
                transactionFacade.findOnlyUnpaid(params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        TransactionResource.populateWithHATEOASLinks(transactions, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(transactions).build();
    }

    /**
     * Method returns subset of Transaction entities for given price
     * range (and optionally currency code).
     * The price range (and optionally currency code) are passed through query params.
     */
    @GET
    @Path("/by-price")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTransactionsByPrice( @BeanParam PriceRangeBeanParam params,
                                            @QueryParam("currency") CurrencyCode currencyCode ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning transactions for given price range (minPrice, maxPrice) and optionally currency code using " +
                "TransactionResource.getTransactionsByPrice(priceRange, currencyCode) method of REST API");

        RESTToolkit.validatePriceRange(params); // i.e. minPrice and maxPrice

        ResourceList<Transaction> transactions = null;

        if(currencyCode != null) {
            // find transactions by given criteria (price, currency code)
            transactions = new ResourceList<>(
                    transactionFacade.findByPriceRangeAndCurrencyCode(params.getMinPrice(), params.getMaxPrice(),
                            currencyCode, params.getOffset(), params.getLimit())
            );
        } else {
            // find transactions by given criteria (price)
            transactions = new ResourceList<>(
                    transactionFacade.findByPriceRange(params.getMinPrice(), params.getMaxPrice(),
                            params.getOffset(), params.getLimit())
            );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        TransactionResource.populateWithHATEOASLinks(transactions, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(transactions).build();
    }

    /**
     * Method returns subset of Transaction entities for given currency code.
     * The currency code is passed through path param.
     */
    @GET
    @Path("/by-currency/{currencyCode : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTransactionsByCurrency( @PathParam("currencyCode") CurrencyCode currencyCode,
                                               @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning transactions for given currency code using " +
                "TransactionResource.getTransactionsByCurrency(currencyCode) method of REST API");

        // find transactions by currency code
        ResourceList<Transaction> transactions = new ResourceList<>(
                transactionFacade.findByCurrencyCode(currencyCode, params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        TransactionResource.populateWithHATEOASLinks(transactions, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(transactions).build();
    }

    /**
     * related subresources (through relationships)
     */
    // TODO impl all related subresources

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList transactions, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(transactions, uriInfo, offset, limit);

        try {
            // count resources hypermedia link
            Method countMethod = TransactionResource.class.getMethod("countTransactions", GenericBeanParam.class);
            transactions.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(TransactionResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            transactions.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(TransactionResource.class).build()).rel("transactions").build() );

            // get all resources eagerly hypermedia link
            Method transactionsEagerlyMethod = TransactionResource.class.getMethod("getTransactionsEagerly", TransactionBeanParam.class);
            transactions.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(TransactionResource.class).path(transactionsEagerlyMethod).build()).rel("transactions-eagerly").build() );

            // get subset of resources hypermedia links
            // by-transaction-time
            Method byTransactionTimeMethod = TransactionResource.class.getMethod("getTransactionsByTransactionTime", DateRangeBeanParam.class);
            transactions.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(TransactionResource.class).path(byTransactionTimeMethod).build()).rel("by-transaction-time").build() );

            // by-booked-time
            Method byBookedTimeMethod = TransactionResource.class.getMethod("getTransactionsByBookedTime", DateRangeBeanParam.class);
            transactions.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(TransactionResource.class).path(byBookedTimeMethod).build()).rel("by-booked-time").build() );

            // paid
            Method paidMethod = TransactionResource.class.getMethod("getTransactionsPaid", PaginationBeanParam.class);
            transactions.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(TransactionResource.class).path(paidMethod).build()).rel("paid").build() );

            // unpaid
            Method unpaidMethod = TransactionResource.class.getMethod("getTransactionsUnpaid", PaginationBeanParam.class);
            transactions.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(TransactionResource.class).path(unpaidMethod).build()).rel("unpaid").build() );

            // by-price
            Method byPriceMethod = TransactionResource.class.getMethod("getTransactionsByPrice", PriceRangeBeanParam.class, CurrencyCode.class);
            transactions.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(TransactionResource.class).path(byPriceMethod).build()).rel("by-price").build() );

            // by-currency
            transactions.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(TransactionResource.class).path("by-currency").build()).rel("by-currency").build() );

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for( Object object : transactions.getResources() ) {
            if (object instanceof Transaction) {
                TransactionResource.populateWithHATEOASLinks( (Transaction) object, uriInfo );
            } else if (object instanceof TransactionWrapper) {
                TransactionResource.populateWithHATEOASLinks( (TransactionWrapper) object, uriInfo );
            }
        }
    }

    /**
     * This method enables to populate each individual resource wrapper with hypermedia links
     */
    public static void populateWithHATEOASLinks(TransactionWrapper transactionWrapper, UriInfo uriInfo) {

        TransactionResource.populateWithHATEOASLinks(transactionWrapper.getTransaction(), uriInfo);

        for(Employee employee : transactionWrapper.getEmployees())
            pl.salonea.jaxrs.EmployeeResource.populateWithHATEOASLinks(employee, uriInfo);
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(Transaction transaction, UriInfo uriInfo) {

        try {

            // self link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/{sub-id}
            Method transactionsMethod = ClientResource.class.getMethod("getTransactionResource");
            transaction.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(transactionsMethod)
                    .path(transaction.getTransactionNumber().toString())
                    .resolveTemplate("clientId", transaction.getClient().getClientId().toString())
                    .build())
                    .rel("self").build());

            // self alternative link with pattern: http://localhost:port/app/rest/{resources}/{id}+{sub-id}
            Method transactionMethod = TransactionResource.class.getMethod("getTransaction", Long.class, Integer.class, GenericBeanParam.class);
            transaction.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .path(transactionMethod)
                    .resolveTemplate("clientId", transaction.getClient().getClientId().toString())
                    .resolveTemplate("transactionNumber", transaction.getTransactionNumber().toString())
                    .build())
                    .rel("self (alternative)").build());

            // collection link with pattern: http://localhost:port/app/rest/{resources}
            transaction.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .build())
                    .rel("transactions").build() );

            // self eagerly link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/{sub-id}/eagerly
            Method transactionEagerlyMethod = ClientResource.TransactionResource.class.getMethod("getTransactionEagerly", Long.class, Integer.class, GenericBeanParam.class);
            transaction.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(transactionsMethod)
                    .path(transactionEagerlyMethod)
                    .resolveTemplate("clientId", transaction.getClient().getClientId().toString())
                    .resolveTemplate("transactionNumber", transaction.getTransactionNumber().toString())
                    .build())
                    .rel("transaction-eagerly").build());

            // self eagerly alternative link with pattern: http://localhost:port/app/rest/{resources}/{id}+{sub-id}/eagerly
            Method transactionEagerlyAlternativeMethod = TransactionResource.class.getMethod("getTransactionEagerly", Long.class, Integer.class, GenericBeanParam.class);
            transaction.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .path(transactionEagerlyAlternativeMethod)
                    .resolveTemplate("clientId", transaction.getClient().getClientId().toString())
                    .resolveTemplate("transactionNumber", transaction.getTransactionNumber().toString())
                    .build())
                    .rel("transaction-eagerly (alternative)").build());

            // associated collections links with pattern: http://localhost:port/app/rest/{resources}/{id1}+{id2}/{relationship}

            /**
             * Employees executing current Transaction resource
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
