package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.HistoricalTransactionFacade;
import pl.salonea.entities.Employee;
import pl.salonea.entities.HistoricalTransaction;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.bean_params.HistoricalTransactionBeanParam;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.HistoricalTransactionWrapper;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
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
