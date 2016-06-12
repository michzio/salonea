package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.EmployeeFacade;
import pl.salonea.ejb.stateless.TransactionFacade;
import pl.salonea.entities.Employee;
import pl.salonea.entities.Transaction;
import pl.salonea.entities.idclass.TransactionId;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.bean_params.TransactionBeanParam;
import pl.salonea.jaxrs.exceptions.*;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
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
    public Response getTransaction( @PathParam("clientId") Long clientId,
                                    @PathParam("transactionNumber") Integer transactionNumber,
                                    @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        return clientResource.getTransactionResource().getTransaction(clientId, transactionNumber, params);
    }

    @GET
    @Path("/{clientId: \\d+}+{transactionNumber: \\d+}/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTransactionEagerly( @PathParam("clientId") Long clientId,
                                           @PathParam("transactionNumber") Integer transactionNumber,
                                           @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

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
    public Response updateTransaction( @PathParam("clientId") Long clientId,
                                       @PathParam("transactionNumber")  Integer transactionNumber,
                                       Transaction transaction,
                                       @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        return clientResource.getTransactionResource().updateTransaction(clientId, transactionNumber, transaction, params);
    }

    @DELETE
    @Path("/{clientId: \\d+}+{transactionNumber: \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeTransaction( @PathParam("clientId") Long clientId,
                                       @PathParam("transactionNumber")  Integer transactionNumber,
                                       @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException, InternalServerErrorException,
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

        if(noOfParams > 0) {
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
            transactions = new ResourceList<>( transactionFacade.findAll(params.getOffset(), params.getLimit()) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        TransactionResource.populateWithHATEOASLinks(transactions, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(transactions).build();
    }

    @GET
    @Path("/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTransactionsEagerly( @BeanParam TransactionBeanParam params ) throws ForbiddenException,
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

    // TODO additional methods, COUNT method

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

            // TODO

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
            // self link with pattern: http://localhost:port/app/rest/{resources}/{id1}+{id2}
            Method transactionMethod = TransactionResource.class.getMethod("getTransaction", Long.class, Integer.class, GenericBeanParam.class);
            transaction.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .path(transactionMethod)
                    .resolveTemplate("clientId", transaction.getClient().getClientId().toString())
                    .resolveTemplate("transactionNumber", transaction.getTransactionNumber().toString())
                    .build())
                    .rel("self").build());

            // collection link with pattern: http://localhost:port/app/rest/{resources}
            transaction.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .build())
                    .rel("transactions").build() );

            // self eagerly link with pattern: http://localhost:port/app/rest/{resources}/{id1}+{id2}/eagerly
            Method transactionEagerlyMethod = TransactionResource.class.getMethod("getTransactionEagerly", Long.class, Integer.class, GenericBeanParam.class);
            transaction.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .path(transactionEagerlyMethod)
                    .resolveTemplate("clientId", transaction.getClient().getClientId().toString())
                    .resolveTemplate("transactionNumber", transaction.getTransactionNumber().toString())
                    .build())
                    .rel("transaction-eagerly").build());

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
