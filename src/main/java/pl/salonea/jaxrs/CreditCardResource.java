package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.CreditCardFacade;
import pl.salonea.entities.CreditCard;
import pl.salonea.enums.CreditCardType;
import pl.salonea.jaxrs.bean_params.CreditCardBeanParam;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.bean_params.PaginationBeanParam;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.RESTDateTime;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.hateoas.Link;

import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static javax.ws.rs.core.Response.Status;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by michzio on 10/11/2015.
 */
@Path("/credit-cards")
public class CreditCardResource {

    private static final Logger logger = Logger.getLogger(CreditCardResource.class.getName());

    @Inject
    private CreditCardFacade creditCardFacade;

    /**
     * Method returns all Credit Card resources
     * They can be additionally filtered or paginated by @QueryParams
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCreditCards( @BeanParam CreditCardBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Credit Cards by executing CreditCardResource.getCreditCards() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<CreditCard> creditCards = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            // get credit cards filtered by criteria provided in query params
            creditCards = new ResourceList<>(
                    creditCardFacade.findByMultipleCriteria(params.getClients(), params.getCardTypes(), params.getCardNumber(), params.getCardHolder(),
                            params.getExpired(), params.getTheEarliestExpirationDate(), params.getTheLatestExpirationDate(), params.getOffset(), params.getLimit())
            );
        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all credit cards without filtering (eventually paginated)
            creditCards = new ResourceList<>( creditCardFacade.findAll(params.getOffset(), params.getLimit()) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        CreditCardResource.populateWithHATEOASLinks(creditCards, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(creditCards).build();
    }



    /**
     * This method enables to populate list of resources and each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList<CreditCard> creditCards, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(creditCards, uriInfo, offset, limit);

        try {
            // count resources hypermedia links
            Method countMethod = CreditCardResource.class.getMethod("countCreditCards", GenericBeanParam.class);
            creditCards.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(CreditCardResource.class)
                    .path(countMethod)
                    .build())
                    .rel("count").build());

            // get all resources hypermedia links
            creditCards.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(CreditCardResource.class)
                    .build())
                    .rel("credit-cards").build());

            // get subset of resources hypermedia links


        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for(CreditCard creditCard : creditCards.getResources()) {
            CreditCardResource.populateWithHATEOASLinks(creditCard, uriInfo);
        }
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(CreditCard creditCard, UriInfo uriInfo) {


        try {
            // self link with pattern: http://localhost:port/app/rest/clients/{clientId}/{credit-cards}/{cardNumber}/expiring/{expirationDate}
            Method clientCreditCardsMethod = ClientResource.class.getMethod("getCreditCardResource");
            Method creditCardMethod = ClientResource.CreditCardResource.class.getMethod("getCreditCard", Long.class, String.class, RESTDateTime.class, GenericBeanParam.class);
            creditCard.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(clientCreditCardsMethod)
                    .path(creditCardMethod)
                    .resolveTemplate("clientId", creditCard.getClient().getClientId().toString())
                    .resolveTemplate("cardNumber", creditCard.getCreditCardNumber())
                    .resolveTemplate("expirationDate", new RESTDateTime(creditCard.getExpirationDate()).toString())
                    .build())
                    .rel("self").build());

            // collection link with pattern: http://localhost:port/app/rest/{resources}
            creditCard.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(CreditCardResource.class)
                    .build())
                    .rel("credit-cards").build());

            // sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}
           creditCard.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(clientCreditCardsMethod)
                    .resolveTemplate("clientId", creditCard.getClient().getClientId().toString())
                    .build())
                    .rel("client-credit-cards").build());

            // sub-collection count link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/count
            Method countByClientMethod = ClientResource.CreditCardResource.class.getMethod("countCreditCardsByClient", Long.class, GenericBeanParam.class);
            creditCard.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(clientCreditCardsMethod)
                    .path(countByClientMethod)
                    .resolveTemplate("clientId", creditCard.getClient().getClientId().toString())
                    .build())
                    .rel("client-credit-cards-count").build());

            // typed sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/typed
            creditCard.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(clientCreditCardsMethod)
                    .path("typed")
                    .resolveTemplate("clientId", creditCard.getClient().getClientId().toString())
                    .build())
                    .rel("client-credit-cards-typed").build());

            // expired sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/expired
            Method expiredMethod = ClientResource.CreditCardResource.class.getMethod("getClientCreditCardsThatExpired", Long.class, PaginationBeanParam.class);
            creditCard.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(clientCreditCardsMethod)
                    .path(expiredMethod)
                    .resolveTemplate("clientId", creditCard.getClient().getClientId().toString())
                    .build())
                    .rel("client-credit-cards-expired").build());

            // not-expired sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/not-expired
            Method notExpiredMethod = ClientResource.CreditCardResource.class.getMethod("getClientCreditCardsThatNotExpired", Long.class, PaginationBeanParam.class);
            creditCard.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(clientCreditCardsMethod)
                    .path(notExpiredMethod)
                    .resolveTemplate("clientId", creditCard.getClient().getClientId().toString())
                    .build())
                    .rel("client-credit-cards-not-expired").build());

            // expiring-after sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/expiring-after
            creditCard.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(clientCreditCardsMethod)
                    .path("expiring-after")
                    .resolveTemplate("clientId", creditCard.getClient().getClientId().toString())
                    .build())
                    .rel("client-credit-cards-expiring-after").build());

            // expiring-before sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/expiring-before
            creditCard.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(clientCreditCardsMethod)
                    .path("expiring-before")
                    .resolveTemplate("clientId", creditCard.getClient().getClientId().toString())
                    .build())
                    .rel("client-credit-cards-expiring-before").build());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

}
