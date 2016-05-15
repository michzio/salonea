package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.TransactionFacade;
import pl.salonea.entities.Employee;
import pl.salonea.entities.Transaction;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.bean_params.TransactionBeanParam;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.hateoas.Link;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
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

    // TODO get Transactions CRUD methods and other

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
