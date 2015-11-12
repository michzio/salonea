package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.CreditCardFacade;
import pl.salonea.entities.CreditCard;
import pl.salonea.enums.CreditCardType;
import pl.salonea.jaxrs.bean_params.CreditCardBeanParam;
import pl.salonea.jaxrs.bean_params.DateBetweenBeanParam;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.bean_params.PaginationBeanParam;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.RESTDateTime;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;

import javax.inject.Inject;
import javax.ws.rs.*;
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
     * Additional methods returning a subset of resources based on given criteria
     * you can also achieve similar results by applying @QueryParams to generic method
     * returning all resources in order to filter and limit them
     */

    /**
     * Method returns number of Credit Card entities in database
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countCreditCards( @BeanParam GenericBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning number of credit cards by executing CreditCardResource.countCreditCards() method of REST API");

        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(creditCardFacade.count()), 200, "number of credit cards");

        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method returns subset of Credit Card entities for given card type.
     * The card type is passed through path param.
     */
    @GET
    @Path("/typed/{cardType : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCreditCardsByCardType( @PathParam("cardType") CreditCardType cardType,
                                              @BeanParam PaginationBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning credit cards for given card type using CreditCardResource.getCreditCardsByCardType(cardType) method of REST API");

        if(cardType == null)
            throw new BadRequestException("Card type param cannot be null.");

        // find credit cards by given criteria (card type)
        ResourceList<CreditCard> creditCards = new ResourceList<>(
                creditCardFacade.findByType(cardType, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(creditCards, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(creditCards).build();
    }

    /**
     * Method returns subset of Credit Card entities that have already expired.
     */
    @GET
    @Path("/expired")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCreditCardsThatExpired( @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning credit cards that have already expired using " +
                "CreditCardResource.getCreditCardsThatExpired() method of REST API");

        // find credit cards by given criteria (expired)
        ResourceList<CreditCard> creditCards = new ResourceList<>(
                creditCardFacade.findExpired(params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(creditCards, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(creditCards).build();
    }

    /**
     * Method returns subset of Credit Card entities that haven't expired yet.
     */
    @GET
    @Path("/not-expired")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCreditCardsThatNotExpired( @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning credit cards that haven't expired yet using " +
                "CreditCardResource.getCreditCardsThatNotExpired() method of REST API");

        // find credit cards by given criteria (not expired)
        ResourceList<CreditCard> creditCards = new ResourceList<>(
                creditCardFacade.findNotExpired(params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(creditCards, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(creditCards).build();
    }

    /**
     * Method returns subset of Credit Card entities expiring after given date.
     * The expiration date is passed through path param.
     */
    @GET
    @Path("/expiring-after/{expirationDate : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCreditCardsExpiringAfter( @PathParam("expirationDate") RESTDateTime expirationDate,
                                                 @BeanParam PaginationBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning credit cards expiring after given date using CreditCardResource.getCreditCardsExpiringAfter(expirationDate) method of REST API");

        if(expirationDate == null)
            throw new BadRequestException("Expiration date param cannot be null.");

        // find credit cards by given criteria (expiration date)
        ResourceList<CreditCard> creditCards = new ResourceList<>(
                creditCardFacade.findExpirationDateAfter(expirationDate, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(creditCards, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(creditCards).build();
    }

    /**
     * Method returns subset of Credit Card entities expiring before given date.
     * The expiration date is passed through path param.
     */
    @GET
    @Path("/expiring-before/{expirationDate : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCreditCardsExpiringBefore( @PathParam("expirationDate") RESTDateTime expirationDate,
                                                  @BeanParam PaginationBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning credit cards expiring before given date using CreditCardResource.getCreditCardsExpiringBefore(expirationDate) method of REST API");

        if(expirationDate == null)
            throw new BadRequestException("Expiration date param cannot be null.");

        // find credit cards by given criteria (expiration date)
        ResourceList<CreditCard> creditCards = new ResourceList<>(
                creditCardFacade.findExpirationDateBefore(expirationDate, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(creditCards, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(creditCards).build();
    }

    /**
     * Method returns subset of Credit Card entities expiring between given dates.
     * The earliest date (start date) and the latest date (end date) are passed through query params.
     */
    @GET
    @Path("/expiring-between")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCreditCardsExpiringBetween( @BeanParam DateBetweenBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning credit cards expiring between given start and end date using " +
                "CreditCardResource.getCreditCardsExpiringBetween(dates) method of REST API" );

        // check correctness of query params
        if(params.getStartDate() == null || params.getEndDate() == null)
            throw new BadRequestException("Start date or end date query param not specified for request.");

        if(params.getStartDate().after(params.getEndDate()))
            throw new BadRequestException("Start date is after end date.");

        // find credit cards by given criteria (start and end expiration dates)
        ResourceList<CreditCard> creditCards = new ResourceList<>(
                creditCardFacade.findExpirationDateBetween(params.getStartDate(), params.getEndDate(), params.getOffset(), params.getLimit()));

        // result resources need to be populated with hypermedia links to enable resource discovery
       populateWithHATEOASLinks(creditCards, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(creditCards).build();
    }

    /**
     * Additional methods removing subset of resources by given criteria
     */

    /**
     * Method removes subset of Credit Card entities from database expiring after given date.
     * The expiration date is passed through path param.
     */
    @DELETE
    @Path("/expiring-after/{expirationDate : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeCreditCardsExpiringAfter( @PathParam("expirationDate") RESTDateTime expirationDate,
                                                    @BeanParam GenericBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing subset of Credit Card entities expiring after given date by executing " +
                "CreditCardResource.removeCreditCardsExpiringAfter(expirationDate) method of REST API");

        if(expirationDate == null)
            throw new BadRequestException("Expiration date param cannot be null.");

        // remove specified entities from database
        Integer noOfDeleted = creditCardFacade.deleteWithExpirationDateAfter(expirationDate);

        // create response returning number of deleted entities
        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfDeleted), 200, "number of deleted credit cards expiring after " + expirationDate);

        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method removes subset of Credit Card entities from database expiring before given date.
     * The expiration date is passed through path param.
     */
    @DELETE
    @Path("/expiring-before/{expirationDate : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeCreditCardsExpiringBefore( @PathParam("expirationDate") RESTDateTime expirationDate,
                                                     @BeanParam GenericBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing subset of Credit Card entities expiring before given date by executing " +
                "CreditCardResource.removeCreditCardsExpiringBefore(expirationDate) method of REST API");

        if(expirationDate == null)
            throw new BadRequestException("Expiration date param cannot be null.");

        // remove specified entities from database
        Integer noOfDeleted = creditCardFacade.deleteWithExpirationDateBefore(expirationDate);

        // create response returning number of deleted entities
        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfDeleted), 200, "number of deleted credit cards expiring before " + expirationDate);

        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method removes subset of Credit Card entities from database
     * expiring between given start and end dates.
     * The start date and end date are passed through query params.
     */
    @DELETE
    @Path("/expiring-between")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeCreditCardsExpiringBetween( @BeanParam DateBetweenBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing subset of Credit Card entities expiring between given start and end dates " +
                "by executing CreditCardResource.removeCreditCardsExpiringBetween(dates) method of REST API");

        // check correctness of query params
        if(params.getStartDate() == null || params.getEndDate() == null)
            throw new BadRequestException("Start date or end date query param not specified for request.");

        if(params.getStartDate().after(params.getEndDate()))
            throw new BadRequestException("Start date is after end date.");

        // remove specified entities from database
        Integer noOfDeleted = creditCardFacade.deleteWithExpirationDateBetween(params.getStartDate(), params.getEndDate());

        // create response returning number of deleted entities
        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfDeleted), 200, "number of deleted credit cards expiring after " + params.getStartDate() + " and before " + params.getEndDate());

        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method removes subset of Credit Card entities from database that have already expired.
     */
    @DELETE
    @Path("/expired")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeCreditCardsThatExpired( @BeanParam GenericBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing subset of Credit Card entities that have already expired by executing " +
                "CreditCardResource.removeCreditCardsThatExpired() method of REST API");

        // remove specified entities from database
        Integer noOfDeleted = creditCardFacade.deleteExpired();

        // create response returning number of deleted entities
        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfDeleted), 200, "number of deleted credit cards that have already expired");

        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method removes subset of Credit Card entities from database for given card type.
     * The card type is passed through path param.
     */
    @DELETE
    @Path("/typed/{cardType : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeCreditCardsByCardType( @PathParam("cardType") CreditCardType cardType,
                                                 @BeanParam GenericBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing subset of Credit Card entities for given card type by executing " +
                "CreditCardResource.removeCreditCardsByCardType(cardType) method of REST API");

        if(cardType == null)
            throw new BadRequestException("Card type param cannot be null.");

        // remove specified entities from database
        Integer noOfDeleted = creditCardFacade.deleteWithType(cardType);

        // create response returning number of deleted entities
        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfDeleted), 200, "number of deleted credit cards for card type " + cardType);

        return Response.status(Status.OK).entity(responseEntity).build();
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

            // typed
            creditCards.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(CreditCardResource.class)
                    .path("typed")
                    .build())
                    .rel("credit-cards-typed").build());

            // expired
            creditCards.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(CreditCardResource.class)
                    .path("expired")
                    .build())
                    .rel("credit-cards-expired").build());

            // not-expired
            creditCards.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(CreditCardResource.class)
                    .path("not-expired")
                    .build())
                    .rel("credit-cards-not-expired").build());

            // expiring-after
            creditCards.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(CreditCardResource.class)
                    .path("expiring-after")
                    .build())
                    .rel("credit-cards-expiring-after").build());

            // expiring-before
            creditCards.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(CreditCardResource.class)
                    .path("expiring-before")
                    .build())
                    .rel("credit-cards-expiring-before").build());

            // expiring-between
            Method expiringBetweenMethod = CreditCardResource.class.getMethod("getCreditCardsExpiringBetween", DateBetweenBeanParam.class);
            creditCards.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(CreditCardResource.class)
                    .path(expiringBetweenMethod)
                    .build())
                    .rel("credit-cards-expiring-between").build());

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
