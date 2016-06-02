package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.EmployeeFacade;
import pl.salonea.ejb.stateless.HistoricalTransactionFacade;
import pl.salonea.entities.Employee;
import pl.salonea.entities.HistoricalTransaction;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.bean_params.HistoricalTransactionBeanParam;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.HistoricalTransactionWrapper;

import javax.inject.Inject;
import javax.transaction.*;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
    public Response getHistoricalTransactionsEagerly( HistoricalTransactionBeanParam params ) throws ForbiddenException,
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

    // TODO get HistoricalTransactions CRUD methods and other

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
