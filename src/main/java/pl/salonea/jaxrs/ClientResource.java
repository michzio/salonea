package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.*;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.*;
import pl.salonea.entities.Transaction;
import pl.salonea.entities.idclass.CreditCardId;
import pl.salonea.entities.idclass.TransactionId;
import pl.salonea.enums.*;
import pl.salonea.jaxrs.bean_params.*;
import pl.salonea.jaxrs.exceptions.*;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.exceptions.BadRequestException;
import pl.salonea.jaxrs.utils.RESTDateTime;
import pl.salonea.jaxrs.utils.RESTToolkit;
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
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by michzio on 09/10/2015.
 */
@Path("/clients")
public class ClientResource {

    private static final Logger logger = Logger.getLogger(ClientResource.class.getName());

    @Inject
    private UserTransaction utx;

    @Inject
    private ClientFacade clientFacade;
    @Inject
    private ProviderFacade providerFacade;
    @Inject
    private ProviderRatingFacade providerRatingFacade;
    @Inject
    private EmployeeFacade employeeFacade;
    @Inject
    private EmployeeRatingFacade employeeRatingFacade;
    @Inject
    private CreditCardFacade creditCardFacade;
    @Inject
    private TransactionFacade transactionFacade;
    @Inject
    private HistoricalTransactionFacade historicalTransactionFacade;
    @Inject
    private TransactionEmployeeRelationshipManager transactionEmployeeRelationshipManager;
    @Inject
    private HistoricalTransactionEmployeeRelationshipManager historicalTransactionEmployeeRelationshipManager;

    /**
     * Method returns all Client resources
     * They can be additionally filtered or paginated by @QueryParams
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getClients(@BeanParam ClientBeanParam params) throws ForbiddenException, NotFoundException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning all Clients by executing ClientResource.getClients() method of REST API");

        // calculate number of filter query params
        Integer noOfParams = params.getUriInfo().getQueryParameters().size();
        if (params.getOffset() != null) noOfParams -= 1;
        if (params.getLimit() != null) noOfParams -= 1;

        ResourceList<Client> clients = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            Address location = new Address(params.getStreet(), params.getHouseNumber(), params.getFlatNumber(), params.getZipCode(), params.getCity(), params.getState(), params.getCountry());
            Address delivery = new Address(params.getDeliveryStreet(), params.getDeliveryHouseNumber(), params.getDeliveryFlatNumber(), params.getDeliveryZipCode(), params.getDeliveryCity(), params.getDeliveryState(), params.getDeliveryCountry());

            // get clients filtered by criteria provided in query params
            clients = new ResourceList<>(
                    clientFacade.findByMultipleCriteria(params.getFirstName(), params.getLastName(), params.getFirmName(), params.getName(), params.getDescription(),
                            new HashSet<>(params.getClientTypes()), params.getOldestBirthDate(), params.getYoungestBirthDate(),
                            params.getYoungestAge(), params.getOldestAge(), location, delivery, params.getGender(), params.getRatedProviders(),
                            params.getRatedEmployees(), params.getOffset(), params.getLimit())
            );

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all clients without filtering (eventually paginated)
            clients = new ResourceList<>( clientFacade.findAll(params.getOffset(), params.getLimit()) );

        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        ClientResource.populateWithHATEOASLinks(clients, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(clients).build();
    }

    @GET
    @Path("/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getClientsEagerly(@BeanParam ClientBeanParam params) throws ForbiddenException, NotFoundException {

        if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning all Clients eagerly by executing ClientResource.getClientsEagerly() method of REST API");

        // calculate number of filter query params
        Integer noOfParams = params.getUriInfo().getQueryParameters().size();
        if (params.getOffset() != null) noOfParams -= 1;
        if (params.getLimit() != null) noOfParams -= 1;

        ResourceList<ClientWrapper> clients = null;

        if (noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            Address location = new Address(params.getStreet(), params.getHouseNumber(), params.getFlatNumber(), params.getZipCode(), params.getCity(), params.getState(), params.getCountry());
            Address delivery = new Address(params.getDeliveryStreet(), params.getDeliveryHouseNumber(), params.getDeliveryFlatNumber(), params.getDeliveryZipCode(), params.getDeliveryCity(), params.getDeliveryState(), params.getDeliveryCountry());

            // get clients filtered by criteria provided in query params
            clients = new ResourceList<>(
                    ClientWrapper.wrap(
                            clientFacade.findByMultipleCriteriaEagerly(params.getFirstName(), params.getLastName(), params.getFirmName(), params.getName(),
                                    params.getDescription(), new HashSet<>(params.getClientTypes()), params.getOldestBirthDate(), params.getYoungestBirthDate(),
                                    params.getYoungestAge(), params.getOldestAge(), location, delivery, params.getGender(), params.getRatedProviders(),
                                    params.getRatedEmployees(), params.getOffset(), params.getLimit())
                    )
            );

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all clients without filtering (eventually paginated)
            clients = new ResourceList<>(ClientWrapper.wrap(clientFacade.findAllEagerly(params.getOffset(), params.getLimit())));
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        ClientResource.populateWithHATEOASLinks(clients, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(clients).build();
    }

    /**
     * Method matches specific Client resource by identifier and returns its instance.
     */
    @GET
    @Path("/{clientId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getClient(@PathParam("clientId") Long clientId,
                              @BeanParam GenericBeanParam params) throws NotFoundException, ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning given Client by executing ClientResource.getClient(clientId) method of REST API");

        Client foundClient = clientFacade.find(clientId);
        if(foundClient == null)
            throw new NotFoundException("Could not find client for id " + clientId + ".");

        // adding hypermedia links to client resource
        ClientResource.populateWithHATEOASLinks(foundClient, params.getUriInfo());

        return Response.status(Status.OK).entity(foundClient).build();
    }

    /**
     * Method matches specific Client resource by identifier and returns its instance fetching it eagerly
     */
    @GET
    @Path("{clientId : \\d+}/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getClientEagerly(@PathParam("clientId") Long clientId,
                                     @BeanParam GenericBeanParam params) throws NotFoundException, ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning given Client eagerly by executing ClientResource.getClientEagerly(clientId) method of REST API");

        Client foundClient = clientFacade.findByIdEagerly(clientId);
        if(foundClient == null)
            throw new NotFoundException("Could not find client for id " + clientId + ".");

        // wrapping Client into ClientWrapper in order to marshall eagerly fetched associated collection of entities
        ClientWrapper wrappedClient = new ClientWrapper(foundClient);

        // adding hypermedia links to wrapped client resource
        ClientResource.populateWithHATEOASLinks(wrappedClient, params.getUriInfo());

        return Response.status(Status.OK).entity(wrappedClient).build();
    }

    /**
     * Method that takes Client as XML or JSON and creates its new instance in database
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createClient(Client client,
                                 @BeanParam GenericBeanParam params) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "creating new Client by executing ClientResource.createClient(client) method of REST API");

        Client createdClient = null;
        URI locationURI = null;

        try {
            // persist new resource in database
            createdClient = clientFacade.create(client);

            // populate created resource with hypermedia links
            ClientResource.populateWithHATEOASLinks(createdClient, params.getUriInfo());

            // construct link to newly created resource to return in HTTP Header
            String createdClientId = String.valueOf(createdClient.getClientId());
            locationURI = params.getUriInfo().getBaseUriBuilder().path(ClientResource.class).path(createdClientId).build();

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_CREATION_ERROR_MESSAGE);
        }

        return Response.created(locationURI).entity(createdClient).build();
    }

    /**
     * Method that takes updated Client as XML or JSON and its ID as path param.
     * It updates Client in database for provided ID.
     */
    @PUT
    @Path("/{clientId : \\d+}") // catch only numeric identifiers
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateClient(@PathParam("clientId") Long clientId,
                                 Client client,
                                 @BeanParam GenericBeanParam params) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "updating existing Client by executing ClientResource.updateClient(clientId, client) method of REST API");

        // set resource ID passed in path param on updated resource object
        client.setClientId(clientId);

        Client updatedClient = null;
        try {
            // reflect updated resource object in database
            updatedClient = clientFacade.update(client, true);
            // populate created resource with hypermedia links
            ClientResource.populateWithHATEOASLinks(updatedClient, params.getUriInfo());

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_UPDATE_ERROR_MESSAGE);
        }

        return Response.status(Status.OK).entity(updatedClient).build();
    }

    /**
     * Method that removes Client entity from database for given ID.
     * The ID is passed through path param.
     */
    @DELETE
    @Path("/{clientId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeClient(@PathParam("clientId") Long clientId,
                                 @HeaderParam("authToken") String authToken) throws ForbiddenException, NotFoundException {

        if(authToken == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "removing given Client by executing ClientResource.removeClient(clientId) method of REST API");

        // find Client entity that should be deleted
        Client toDeleteClient = clientFacade.find(clientId);
        // throw exception if entity hasn't been found
        if(toDeleteClient == null)
            throw new NotFoundException("Could not find client to delete for given id: " + clientId + ".");

        // remove entity from database
        clientFacade.remove(toDeleteClient);

        return Response.status(Status.NO_CONTENT).build();
    }

    /**
     * Additional methods returning a subset of resources based on given criteria
     * you can also achieve similar results by applying @QueryParams to generic method
     * returning all resources in order to filter and limit them
     */

    /**
     * Method returns number of Client entities in database
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countClients(@HeaderParam("authToken") String authToken) throws ForbiddenException {

        if(authToken == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning number of clients by executing ClientResource.countClients() method of REST API");

        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(clientFacade.count()), 200, "number of clients");
        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method returns subset of Client entities for given first name
     */
    @GET
    @Path("/first-name/{firstName : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getClientsByFirstName( @PathParam("firstName") String firstName,
                                           @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning clients for given first name using ClientResource.getClientsByFirstName(firstName) method of REST API");

        // find clients by given criteria
        ResourceList<Client> clients = new ResourceList<>( clientFacade.findByFirstName(firstName, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(clients, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(clients).build();
    }

    /**
     * Method returns subset of Client entities for given last name
     */
    @GET
    @Path("/last-name/{lastName : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getClientsByLastName( @PathParam("lastName") String lastName,
                                          @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning clients for given last name using ClientResource.getClientsByLastName(lastName) method of REST API");

        // find clients by given criteria
        ResourceList<Client> clients = new ResourceList<>( clientFacade.findByLastName(lastName, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(clients, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(clients).build();
    }

    /**
     * Method returns subset of Client entities for given first and last names
     */
    @GET
    @Path("/person-names")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getClientsByPersonNames( @BeanParam NamesBeanParam params ) throws ForbiddenException, BadRequestException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning clients for given first and last names using ClientResource.getClientsByPersonNames(firstName, lastName) method of REST API");

        // check correctness of query params
        if(params.getFirstName() == null || params.getLastName() == null) {
            throw new BadRequestException("First name or last name query param not specified for request.");
        }

        // find clients by given criteria
        ResourceList<Client> clients = new ResourceList<>( clientFacade.findByPersonNames(params.getFirstName(), params.getLastName(), params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(clients, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(clients).build();
    }

    /**
     * Method returns subset of Client entities for given firm name
     */
    @GET
    @Path("/firm-name/{firmName : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getClientsByFirmName( @PathParam("firmName") String firmName,
                                          @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning clients for given firm name using ClientResource.getClientsByFirmName(firmName) method of REST API");

        // find clients by given criteria
        ResourceList<Client> clients = new ResourceList<>( clientFacade.findByFirmName(firmName, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(clients, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(clients).build();
    }

    /**
     * Method returns subset of Client entities for given name (searched in first name or last name or firm name)
     */
    @GET
    @Path("/named/{name : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getClientsByName( @PathParam("name") String name,
                                      @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning clients for given name using ClientResource.getClientsByName(name) method of REST API");

        // find clients by given criteria
        ResourceList<Client> clients = new ResourceList<>( clientFacade.findByName(name, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(clients, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(clients).build();
    }

    /**
     * Method returns subset of Client entities for given description
     */
    @GET
    @Path("/described/{description : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getClientsByDescription( @PathParam("description") String description,
                                             @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning clients for given description using ClientResource.getClientsByDescription(description) method of REST API ");

        // find clients by given criteria
        ResourceList<Client> clients = new ResourceList<>( clientFacade.findByDescription(description, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(clients, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(clients).build();
    }

    /**
     * Method returns subset of Client entities born after given date
     */
    @GET
    @Path("/born-after/{date}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getClientsBornAfter( @PathParam("date") RESTDateTime date,
                                         @BeanParam PaginationBeanParam params ) throws ForbiddenException  {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning clients born after provided date using ClientResource.getClientsBornAfter(date) method of REST API");

        // find clients by given criteria
        ResourceList<Client> clients = new ResourceList<>( clientFacade.findBornAfter(date, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(clients, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(clients).build();
    }

    /**
     * Method returns subset of Client entities born before given date
     */
    @GET
    @Path("/born-before/{date}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getClientsBornBefore( @PathParam("date") RESTDateTime date,
                                          @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning clients born before provided date using ClientResource.getClientsBornBefore(date) method of REST API");

        // find clients by given criteria
        ResourceList<Client> clients = new ResourceList<>( clientFacade.findBornBefore(date, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(clients, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(clients).build();
    }

    /**
     * Method returns subset of Client entities born between given dates
     */
    @GET
    @Path("/born-between")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getClientsBornBetween( @BeanParam DateRangeBeanParam params ) throws ForbiddenException, BadRequestException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning clients born between given start and end date using ClientResource.getClientsBornBetween() method of REST API");

        // check correctness of query params
        if(params.getStartDate() == null || params.getEndDate() == null) {
            throw new BadRequestException("Start date or end date query param not specified for request.");
        }

        if(params.getStartDate().after(params.getEndDate())) {
            throw new BadRequestException("Start date is after end date.");
        }

        // find clients by given criteria
        ResourceList<Client> clients = new ResourceList<>( clientFacade.findBornBetween(params.getStartDate(),
                params.getEndDate(), params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(clients, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(clients).build();
    }

    /**
     * Method returns subset of Client entities older than specified age
     */
    @GET
    @Path("/older-than/{age : \\d{1,3}}") // catch only numeric 0-999 path param
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getClientsOlderThan( @PathParam("age") Integer age,
                                         @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning clients older than specified age using ClientResource.getClientsOlderThan(age) method of REST API");

        // find clients by given criteria
        ResourceList<Client> clients = new ResourceList<>( clientFacade.findOlderThan(age, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(clients, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(clients).build();
    }

    /**
     * Method returns subset of Client entities younger than specified age
     */
    @GET
    @Path("/younger-than/{age : \\d{1,3}}") // catch only numeric 0-999 path param
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getClientsYoungerThan( @PathParam("age") Integer age,
                                           @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning clients younger than specified age using ClientResource.getClientsYoungerThan(age) method of REST API");

        // find clients by given criteria
        ResourceList<Client> clients = new ResourceList<>( clientFacade.findYoungerThan(age, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(clients, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(clients).build();
    }

    /**
     * Method returns subset of Client entities between specified age
     */
    @GET
    @Path("/aged-between")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getClientsAgedBetween( @BeanParam AgeBetweenBeanParam params ) throws ForbiddenException, BadRequestException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning clients aged between specified youngest and oldest age using ClientResource.getClientsAgedBetween() method of REST API");

        // check correctness of query params
        if(params.getYoungestAge() == null || params.getOldestAge() == null) {
            throw new BadRequestException("Youngest or oldest age query param not specified for request.");
        }

        if(params.getYoungestAge() > params.getOldestAge()) {
            throw new BadRequestException("Youngest age is greater than oldest age.");
        }

        // find clients by given criteria
        ResourceList<Client> clients = new ResourceList<>( clientFacade.findBetweenAge(params.getYoungestAge(), params.getOldestAge(), params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(clients, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(clients).build();
    }

    /**
     * Method returns subset of Client entities for given location query params (home address or firm address)
     */
    @GET
    @Path("/located")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getClientsByLocation( @BeanParam AddressBeanParam params ) throws ForbiddenException, BadRequestException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning clients for given location query params using ClientResource.getClientsByLocation() method of REST API");

        // check correctness of query params
        Integer noOfParams = params.getUriInfo().getQueryParameters().size();
        if(params.getOffset() != null) noOfParams -= 1;
        if(params.getLimit() != null) noOfParams -= 1;
        if(noOfParams < 1 )
            throw new BadRequestException("There is no location related query param in request.");

        // find clients by given criteria
        ResourceList<Client> clients = new ResourceList<>( clientFacade.findByLocation(params.getCity(), params.getState(), params.getCountry(),
                params.getStreet(), params.getZipCode(), params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(clients, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(clients).build();
    }

    /**
     * Method returns subset of Client entities for given delivery query params (delivery address or firm address)
     */
    @GET
    @Path("/delivered")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getClientsByDelivery(  @BeanParam AddressBeanParam params ) throws ForbiddenException, BadRequestException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning clients for given delivery query params using ClientResource.getClientsByDelivery() method of REST API");

        // check correctness
        Integer noOfParams = params.getUriInfo().getQueryParameters().size();
        if(params.getOffset() != null) noOfParams -= 1;
        if(params.getLimit() != null) noOfParams -= 1;
        if(noOfParams < 1)
            throw new BadRequestException("There is no delivery related query param in request.");

        // find clients by given criteria
        ResourceList<Client> clients = new ResourceList<>( clientFacade.findByDelivery(params.getCity(), params.getState(), params.getCountry(),
                params.getStreet(), params.getZipCode(), params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(clients, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(clients).build();
    }

    /**
     * Method returns subset of Client entities for given gender
     */
    @GET
    @Path("/gender/{gender : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getClientsByGender( @PathParam("gender") Gender gender,
                                        @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning clients for given gender using ClientResource.getClientsByGender(gender) method of REST API");

        ResourceList<Client> clients = new ResourceList<>( clientFacade.findByGender(gender, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(clients, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(clients).build();
    }

    /**
     * Method returns subset of Client entities for given client type
     */
    @GET
    @Path("/typed/{clientType : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getClientsByType( @PathParam("clientType") ClientType clientType,
                                      @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning clients for given client type using ClientResource.getClientsByType(clientType) method of REST API");

        ResourceList<Client> clients = new ResourceList<>( clientFacade.findByType(clientType, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(clients, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(clients).build();
    }

    /**
     * related subresources (through relationships)
     */

    @Path("/{clientId: \\d+}/provider-ratings")
    public ProviderRatingResource getProviderRatingResource() {
        return new ProviderRatingResource();
    }

    @Path("/{clientId: \\d+}/employee-ratings")
    public EmployeeRatingResource getEmployeeRatingResource() {
        return new EmployeeRatingResource();
    }

    @Path("/{clientId: \\d+}/rated-providers")
    public ProviderResource getProviderResource() { return new ProviderResource(); }

    @Path("/{clientId: \\d+}/rated-employees")
    public EmployeeResource getEmployeeResource() { return new EmployeeResource(); }

    @Path("/{clientId: \\d+}/credit-cards")
    public CreditCardResource getCreditCardResource() { return new CreditCardResource(); }

    @Path("/{clientId: \\d+}/transactions")
    public TransactionResource getTransactionResource() { return new TransactionResource(); }

    @Path("/{clientId: \\d+}/historical-transactions")
    public HistoricalTransactionResource getHistoricalTransactionResource() { return new HistoricalTransactionResource(); }

    /**
     * This method enables to populate list of resources and each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList clients, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(clients, uriInfo, offset, limit);

        try {

            // count resources hypermedia link
            Method countMethod = ClientResource.class.getMethod("countClients", String.class);
            clients.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(countMethod)
                    .build())
                    .rel("count").build());

            // get all resources hypermedia link
            clients.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .build())
                    .rel("clients").build());

            // get all resources eagerly hypermedia link
            Method clientsEagerlyMethod = ClientResource.class.getMethod("getClientsEagerly", ClientBeanParam.class);
            clients.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(clientsEagerlyMethod)
                    .build())
                    .rel("clients-eagerly").build());

            // get subset of resources hypermedia links
            // first-name
            clients.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ClientResource.class).path("first-name").build()).rel("first-name").build() );

            // last-name
            clients.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ClientResource.class).path("last-name").build()).rel("last-name").build() );

            // person-names
            Method personNamesMethod = ClientResource.class.getMethod("getClientsByPersonNames", NamesBeanParam.class);
            clients.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ClientResource.class).path(personNamesMethod).build()).rel("person-names").build() );

            // firm-name
            clients.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ClientResource.class).path("firm-name").build()).rel("firm-name").build() );

            // named
            clients.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ClientResource.class).path("named").build()).rel("named").build() );

            // described
            clients.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ClientResource.class).path("described").build()).rel("described").build() );

            // born-after
            clients.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ClientResource.class).path("born-after").build()).rel("born-after").build() );

            // born-before
            clients.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ClientResource.class).path("born-before").build()).rel("born-before").build() );

            // born-between
            Method bornBetweenMethod = ClientResource.class.getMethod("getClientsBornBetween", DateRangeBeanParam.class);
            clients.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ClientResource.class).path(bornBetweenMethod).build()).rel("born-between").build() );

            // older-than
            clients.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ClientResource.class).path("older-than").build()).rel("older-than").build() );

            // younger-than
            clients.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ClientResource.class).path("younger-than").build()).rel("younger-than").build() );

            // aged-between
            Method agedBetweenMethod = ClientResource.class.getMethod("getClientsAgedBetween", AgeBetweenBeanParam.class);
            clients.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ClientResource.class).path(agedBetweenMethod).build()).rel("aged-between").build() );

            // located
            Method locatedMethod = ClientResource.class.getMethod("getClientsByLocation", AddressBeanParam.class);
            clients.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ClientResource.class).path(locatedMethod).build()).rel("located").build() );

            // delivered
            Method deliveredMethod = ClientResource.class.getMethod("getClientsByDelivery", AddressBeanParam.class);
            clients.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ClientResource.class).path(deliveredMethod).build()).rel("delivered").build() );

            // gender
            clients.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ClientResource.class).path("gender").build()).rel("gender").build() );

            // typed
            clients.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ClientResource.class).path("typed").build()).rel("typed").build() );

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for(Object object : clients.getResources()) {
            if(object instanceof Client) {
                ClientResource.populateWithHATEOASLinks( (Client) object, uriInfo);
            } else if(object instanceof ClientWrapper) {
                ClientResource.populateWithHATEOASLinks( (ClientWrapper) object, uriInfo);
            }
        }
    }

    /**
     * This method enables to populate each individual resource wrapper with hypermedia links
     */
    public static void populateWithHATEOASLinks(ClientWrapper clientWrapper, UriInfo uriInfo) {

        ClientResource.populateWithHATEOASLinks(clientWrapper.getClient(), uriInfo);

        for(CreditCard creditCard : clientWrapper.getCreditCards())
            pl.salonea.jaxrs.CreditCardResource.populateWithHATEOASLinks(creditCard, uriInfo);

        for(EmployeeRating employeeRating : clientWrapper.getEmployeeRatings())
            pl.salonea.jaxrs.EmployeeRatingResource.populateWithHATEOASLinks(employeeRating, uriInfo);

        for(ProviderRating providerRating : clientWrapper.getProviderRatings())
            pl.salonea.jaxrs.ProviderRatingResource.populateWithHATEOASLinks(providerRating, uriInfo);
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(Client client, UriInfo uriInfo) {

        // self link with pattern: http://localhost:port/app/rest/{resources}/{id}
        client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                .path(ClientResource.class)
                .path(client.getClientId().toString())
                .build())
                .rel("self").build());

        // natural-person link with pattern: http://localhost:port/app/rest/{resources}/{id}
        if(client.getNaturalPersonId() != null) {
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                .path(NaturalPersonResource.class)
                .path(client.getNaturalPersonId().toString())
                .build())
                .rel("natural-person").build());
        } else {
            client.getLinks().add(Link.fromUri("").rel("natural-person").build());
        }

        // firm link with pattern: http://localhost:port/app/rest/{resources}/{id}
        if(client.getFirmId() != null) {
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                .path(FirmResource.class)
                .path(client.getFirmId().toString())
                .build())
                .rel("firm").build());
        } else {
            client.getLinks().add(Link.fromUri("").rel("firm").build());
        }

        // collection link with pattern: http://localhost:port/app/rest/{resources}
        client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                .path(ClientResource.class)
                .build())
                .rel("clients").build());

        try {
            // self eagerly link with pattern http://localhost:port/app/rest/{resources}/{id}/eagerly
            Method clientEagerlyMethod = ClientResource.class.getMethod("getClientEagerly", Long.class, GenericBeanParam.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(clientEagerlyMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("client-eagerly").build());

            // associated collections links with pattern: http://localhost:port/app/rest/{resources}/{id}/{relationship}

            // employee-ratings
            Method employeeRatingsMethod = ClientResource.class.getMethod("getEmployeeRatingResource");
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(employeeRatingsMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("employee-ratings").build());

            // employee-ratings count
            Method countEmployeeRatingsByClientMethod = ClientResource.EmployeeRatingResource.class.getMethod("countClientEmployeeRatings", Long.class, GenericBeanParam.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(employeeRatingsMethod)
                    .path(countEmployeeRatingsByClientMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("employee-ratings-count").build());

            // employee-ratings rated
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(employeeRatingsMethod)
                    .path("rated")
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("employee-ratings-rated").build());

            // employee-ratings rated-above
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(employeeRatingsMethod)
                    .path("rated-above")
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("employee-ratings-rated-above").build());

            // employee-ratings rated-below
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(employeeRatingsMethod)
                    .path("rated-below")
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("employee-ratings-rated-below").build());

            // provider-ratings
            Method providerRatingsMethod = ClientResource.class.getMethod("getProviderRatingResource");
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(providerRatingsMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("provider-ratings").build());

            // provider-ratings count
            Method countProviderRatingsByClientMethod = ClientResource.ProviderRatingResource.class.getMethod("countClientProviderRatings", Long.class, GenericBeanParam.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(providerRatingsMethod)
                    .path(countProviderRatingsByClientMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("provider-ratings-count").build());

            // provider-ratings rated
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(providerRatingsMethod)
                    .path("rated")
                    .resolveTemplate("clientId",client.getClientId().toString())
                    .build())
                    .rel("provider-ratings-rated").build());

            // provider-ratings rated-above
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(providerRatingsMethod)
                    .path("rated-above")
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("provider-ratings-rated-above").build());

            // provider-ratings rated-below
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(providerRatingsMethod)
                    .path("rated-below")
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("provider-ratings-rated-below").build());

            // rated-providers
            Method providersMethod = ClientResource.class.getMethod("getProviderResource");
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(providersMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("rated-providers").build());

            // rated-providers eagerly
            Method providersEagerlyMethod = ClientResource.ProviderResource.class.getMethod("getClientRatedProvidersEagerly", Long.class, ProviderBeanParam.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(providersMethod)
                    .path(providersEagerlyMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("rated-providers-eagerly").build());

            // rated-employees
            Method employeesMethod = ClientResource.class.getMethod("getEmployeeResource");
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(employeesMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("rated-employees").build());

            // rated-employees eagerly
            Method employeesEagerlyMethod = ClientResource.EmployeeResource.class.getMethod("getClientRatedEmployeesEagerly", Long.class, EmployeeBeanParam.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(employeesMethod)
                    .path(employeesEagerlyMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("rated-employees-eagerly").build());

            // credit-cards
            Method creditCardsMethod = ClientResource.class.getMethod("getCreditCardResource");
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(creditCardsMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("credit-cards").build());

            // credit-cards count
            Method countCreditCardsByClientMethod = ClientResource.CreditCardResource.class.getMethod("countCreditCardsByClient", Long.class, GenericBeanParam.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(creditCardsMethod)
                    .path(countCreditCardsByClientMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("credit-cards-count").build());

            // credit-cards typed
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(creditCardsMethod)
                    .path("typed")
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("credit-cards-typed").build());

            // credit-cards expired
            Method creditCardsThatExpiredMethod = ClientResource.CreditCardResource.class.getMethod("getClientCreditCardsThatExpired", Long.class, PaginationBeanParam.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(creditCardsMethod)
                    .path(creditCardsThatExpiredMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("credit-cards-expired").build());

            // credit-cards not-expired
            Method creditCardsThatNotExpiredMethod = ClientResource.CreditCardResource.class.getMethod("getClientCreditCardsThatNotExpired", Long.class, PaginationBeanParam.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(creditCardsMethod)
                    .path(creditCardsThatNotExpiredMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("credit-cards-not-expired").build());

            // credit-cards expiring-after
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(creditCardsMethod)
                    .path("expiring-after")
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("credit-cards-expiring-after").build());

            // credit-cards expiring-before
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(creditCardsMethod)
                    .path("expiring-before")
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("credit-cards-expiring-before").build());

            // credit-cards expiring-between
            Method creditCardsExpiringBetweenMethod = ClientResource.CreditCardResource.class.getMethod("getClientCreditCardsExpiringBetween", Long.class, DateRangeBeanParam.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(creditCardsMethod)
                    .path(creditCardsExpiringBetweenMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("credit-cards-expiring-between").build());

            // transactions
            Method transactionsMethod = ClientResource.class.getMethod("getTransactionResource");
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(transactionsMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("transactions").build());

            // transactions eagerly
            Method transactionsEagerlyMethod = ClientResource.TransactionResource.class.getMethod("getClientTransactionsEagerly", Long.class, TransactionBeanParam.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(transactionsMethod)
                    .path(transactionsEagerlyMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("transactions-eagerly").build());

            // transactions count
            Method countTransactionsByClientMethod = ClientResource.TransactionResource.class.getMethod("countTransactionsByClient", Long.class, GenericBeanParam.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(transactionsMethod)
                    .path(countTransactionsByClientMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("transactions-count").build());

            // transactions by-transaction-time
            Method transactionsByTransactionTimeMethod = ClientResource.TransactionResource.class.getMethod("getClientTransactionsByTransactionTime", Long.class, DateRangeBeanParam.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(transactionsMethod)
                    .path(transactionsByTransactionTimeMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("transactions-by-transaction-time").build());

            // transactions by-booked-time
            Method transactionsByBookedTimeMethod = ClientResource.TransactionResource.class.getMethod("getClientTransactionsByBookedTime", Long.class, DateRangeBeanParam.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(transactionsMethod)
                    .path(transactionsByBookedTimeMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("transactions-by-booked-time").build());

            // transactions paid
            Method transactionsPaidMethod = ClientResource.TransactionResource.class.getMethod("getClientTransactionsPaid", Long.class, PaginationBeanParam.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(transactionsMethod)
                    .path(transactionsPaidMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("transactions-paid").build());

            // transactions unpaid
            Method transactionsUnpaidMethod = ClientResource.TransactionResource.class.getMethod("getClientTransactionsUnpaid", Long.class, PaginationBeanParam.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(transactionsMethod)
                    .path(transactionsUnpaidMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("transactions-unpaid").build());

            // transactions by-price
            Method transactionsByPriceMethod = ClientResource.TransactionResource.class.getMethod("getClientTransactionsByPrice", Long.class, PriceRangeBeanParam.class, CurrencyCode.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(transactionsMethod)
                    .path(transactionsByPriceMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("transactions-by-price").build());

            // transactions by-currency
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(transactionsMethod)
                    .path("by-currency")
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("transactions-by-currency").build());

            // historical-transactions
            Method historicalTransactionsMethod = ClientResource.class.getMethod("getHistoricalTransactionResource");
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(historicalTransactionsMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("historical-transactions").build());

            // historical-transactions eagerly
            Method historicalTransactionsEagerlyMethod = ClientResource.HistoricalTransactionResource.class.getMethod("getClientHistoricalTransactionsEagerly", Long.class, HistoricalTransactionBeanParam.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(historicalTransactionsMethod)
                    .path(historicalTransactionsEagerlyMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("historical-transactions-eagerly").build());

            // historical-transactions count
            Method countHistoricalTransactionsByClientMethod = ClientResource.HistoricalTransactionResource.class.getMethod("countHistoricalTransactionsByClient", Long.class, GenericBeanParam.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(historicalTransactionsMethod)
                    .path(countHistoricalTransactionsByClientMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("historical-transactions-count").build());

            // historical-transactions by-transaction-time
            Method historicalTransactionsByTransactionTimeMethod = ClientResource.HistoricalTransactionResource.class.getMethod("getClientHistoricalTransactionsByTransactionTime", Long.class, DateRangeBeanParam.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(historicalTransactionsMethod)
                    .path(historicalTransactionsByTransactionTimeMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("historical-transactions-by-transaction-time").build());

            // historical-transactions by-booked-time
            Method historicalTransactionsByBookedTimeMethod = ClientResource.HistoricalTransactionResource.class.getMethod("getClientHistoricalTransactionsByBookedTime", Long.class, DateRangeBeanParam.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(historicalTransactionsMethod)
                    .path(historicalTransactionsByBookedTimeMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("historical-transactions-by-booked-time").build());

            // historical-transactions paid
            Method historicalTransactionsPaidMethod = ClientResource.HistoricalTransactionResource.class.getMethod("getClientHistoricalTransactionsPaid", Long.class, PaginationBeanParam.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(historicalTransactionsMethod)
                    .path(historicalTransactionsPaidMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("historical-transactions-paid").build());

            // historical-transactions unpaid
            Method historicalTransactionsUnpaidMethod = ClientResource.HistoricalTransactionResource.class.getMethod("getClientHistoricalTransactionsUnpaid", Long.class, PaginationBeanParam.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(historicalTransactionsMethod)
                    .path(historicalTransactionsUnpaidMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("historical-transactions-unpaid").build());

            // historical-transactions by-price
            Method historicalTransactionsByPriceMethod = ClientResource.HistoricalTransactionResource.class.getMethod("getClientHistoricalTransactionsByPrice", Long.class, PriceRangeBeanParam.class, CurrencyCode.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(historicalTransactionsMethod)
                    .path(historicalTransactionsByPriceMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("historical-transactions-by-price").build());

            // historical-transactions by-currency
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(historicalTransactionsMethod)
                    .path("by-currency")
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("historical-transactions-by-currency").build());

            // historical-transactions by-completion-status
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(historicalTransactionsMethod)
                    .path("by-completion-status")
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("historical-transactions-by-completion-status").build());

            // historical-transactions by-client-rating
            Method historicalTransactionsByClientRatingMethod = ClientResource.HistoricalTransactionResource.class.getMethod("getClientHistoricalTransactionsByClientRating", Long.class, RatingBeanParam.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(historicalTransactionsMethod)
                    .path(historicalTransactionsByClientRatingMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("historical-transactions-by-client-rating").build());

            // historical-transactions by-client-comment
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(historicalTransactionsMethod)
                    .path("by-client-comment")
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("historical-transactions-by-client-comment").build());

            // historical-transactions by-provider-rating
            Method historicalTransactionsByProviderRatingMethod = ClientResource.HistoricalTransactionResource.class.getMethod("getClientHistoricalTransactionsByProviderRating", Long.class, RatingBeanParam.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(historicalTransactionsMethod)
                    .path(historicalTransactionsByProviderRatingMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("historical-transactions-by-provider-rating").build());

            // historical-transactions by-provider-dementi
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(historicalTransactionsMethod)
                    .path("by-provider-dementi")
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("historical-transactions-by-provider-dementi").build());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public class ProviderRatingResource {

        public ProviderRatingResource() { }

        /**
         * Method returns subset of Provider Rating entities for given Client.
         * The client id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientProviderRatings( @PathParam("clientId") Long clientId,
                                                  @BeanParam ProviderRatingBeanParam params ) throws NotFoundException, ForbiddenException {

            if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning subset of Provider Rating entities for given Client using ClientResource.ProviderRatingResource.getClientProviderRatings(clientId) method of REST API");

            // find client entity for which to get associated provider ratings
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ProviderRating> providerRatings = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Client> clients = new ArrayList<>();
                clients.add(client);

                // get provider ratings for given client filtered by given params
                providerRatings = new ResourceList<>(
                        providerRatingFacade.findByMultipleCriteria(clients, params.getProviders(), params.getMinRating(), params.getMaxRating(),
                                params.getExactRating(), params.getClientComment(), params.getProviderDementi(), params.getOffset(), params.getLimit())
                );

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get provider ratings for given client without filtering
                providerRatings = new ResourceList<>(providerRatingFacade.findByClient(client, params.getOffset(), params.getLimit()));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderRatingResource.populateWithHATEOASLinks(providerRatings, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providerRatings).build();
        }

        /**
         * Method that removes subset of Provider Rating entities from database for given Client.
         * The client id is passed through path param.
         */
        @DELETE
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response removeClientProviderRatings( @PathParam("clientId") Long clientId,
                                                     @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "removing subset of Provider Rating entities for given Client by executing ClientResource.ProviderRatingResource.removeClientProviderRatings(clientId) method of REST API");

            // find client entity for which to remove provider ratings
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // remove all specified entities from database
            Integer noOfDeleted = providerRatingFacade.deleteByClient(client);

            // create response returning number of deleted entities
            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfDeleted), 200, "number of deleted provider ratings for client with id " + clientId);

            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Additional methods returning subset of resources based on given criteria
         * You can also achieve similar results by applying @QueryParams to generic method
         * returning all resources in order to filter and limit them
         */

        /**
         * Method that counts Provider Rating entities for given Client resource
         * The client id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countClientProviderRatings( @PathParam("clientId") Long clientId,
                                                    @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException  {

            if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning number of provider ratings for given client by executing ClientResource.ProviderRatingResource.countClientProviderRatings(clientId) method of REST API");

            // find client entity for which to count provider ratings
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(providerRatingFacade.countClientRatings(client)), 200,
                    "number of provider ratings for client with id " + client.getClientId());

            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method returns subset of Provider Rating entities for given Client
         * that have been granted given rating.
         * The client id and rating are passed through path params.
         */
        @GET
        @Path("/rated/{rating : \\d+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientProviderRatingsByRating(@PathParam("clientId") Long clientId,
                                                         @PathParam("rating") Short rating,
                                                         @BeanParam PaginationBeanParam params) throws ForbiddenException, NotFoundException {

            if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning provider ratings for given client and rating using ClientResource.ProviderRatingResource.getClientProviderRatingsByRating(clientId, rating) method of REST API");

            // find client entity for which to get associated provider ratings
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // find provider ratings by given criteria (client and rating)
            ResourceList<ProviderRating> providerRatings = new ResourceList<>(
                    providerRatingFacade.findFromClientByRating(client, rating, params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderRatingResource.populateWithHATEOASLinks(providerRatings, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providerRatings).build();
        }

        /**
         * Method returns subset of Provider Rating entities for given Client
         * rated above given minimal rating.
         * The client id and minimal rating are passed through path params.
         */
        @GET
        @Path("/rated-above/{minRating : \\d+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientProviderRatingsAboveMinimalRating(@PathParam("clientId") Long clientId,
                                                                   @PathParam("minRating") Short minRating,
                                                                   @BeanParam PaginationBeanParam params) throws ForbiddenException, NotFoundException {

            if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning provider ratings for given client rated above given minimal rating using " +
                    "ClientResource.ProviderRatingResource.getClientProviderRatingsAboveMinimalRating(clientId, minRating) method of REST API");

            // find client entity for which to get associated provider ratings
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // find provider ratings by given criteria (client and min rating)
            ResourceList<ProviderRating> providerRatings = new ResourceList<>(
                    providerRatingFacade.findFromClientAboveRating(client, minRating, params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderRatingResource.populateWithHATEOASLinks(providerRatings, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providerRatings).build();
        }

        /**
         * Method returns subset of Provider Rating entities for given Client
         * rated below given maximal rating.
         * The client id and maximal rating are passed through path params.
         */
        @GET
        @Path("/rated-below/{maxRating : \\d+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientProviderRatingsBelowMaximalRating(@PathParam("clientId") Long clientId,
                                                                   @PathParam("maxRating") Short maxRating,
                                                                   @BeanParam PaginationBeanParam params) throws ForbiddenException, NotFoundException {

            if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning provider ratings for given client rated below given maximal rating using " +
                    "ClientResource.ProviderRatingResource.getClientProviderRatingsBelowMaximalRating(clientId, maxRating) method of REST API");

            // find client entity for which to get associated provider ratings
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // find provider ratings by given criteria (client and max rating)
            ResourceList<ProviderRating> providerRatings = new ResourceList<>(
                    providerRatingFacade.findFromClientBelowRating(client, maxRating, params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderRatingResource.populateWithHATEOASLinks(providerRatings, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providerRatings).build();
        }

    }

    public class EmployeeRatingResource {

        public EmployeeRatingResource() { }

        /**
         * Method returns subset of Employee Rating entities for given Client.
         * The client id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientEmployeeRatings( @PathParam("clientId") Long clientId,
                                                  @BeanParam EmployeeRatingBeanParam params ) throws NotFoundException, ForbiddenException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Employee Rating entities for given Client using ClientResource.EmployeeRatingResource.getClientEmployeeRatings(clientId) method of REST API");

            // find client entity for which to get associated employee ratings
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<EmployeeRating> employeeRatings = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Client> clients = new ArrayList<>();
                clients.add(client);

                // get employee ratings for given client filtered by given params
                employeeRatings = new ResourceList<>(
                        employeeRatingFacade.findByMultipleCriteria(clients, params.getEmployees(), params.getMinRating(), params.getMaxRating(),
                                params.getExactRating(), params.getClientComment(), params.getEmployeeDementi(), params.getOffset(), params.getLimit())
                );

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get employee ratings for given client without filtering
                employeeRatings = new ResourceList<>(employeeRatingFacade.findByClient(client, params.getOffset(), params.getLimit()));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeRatingResource.populateWithHATEOASLinks(employeeRatings, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employeeRatings).build();
        }

        /**
         * Method that removes subset of Employee Rating entities from database for given Client.
         * The client id is passed through path param.
         */
        @DELETE
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response removeClientEmployeeRatings( @PathParam("clientId") Long clientId,
                                                     @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "removing subset of Employee Rating entities for given Client by executing ClientResource.EmployeeRatingResource.removeClientEmployeeRatings(clientId) method of REST API");

            // find client entity for which to remove employee ratings
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // remove all specified entities from database
            Integer noOfDeleted = employeeRatingFacade.deleteByClient(client);

            // create response returning number of deleted entities
            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfDeleted), 200, "number of deleted employee ratings for client with id " + clientId );

            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Additional methods returning subset of resources based on given criteria.
         * You can also achieve similar results by applying @QueryParams to generic method
         * returning all resources in order to filter and limit them
         */

        /**
         * Method that counts Employee Rating entities for given Client resource
         * The client id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countClientEmployeeRatings( @PathParam("clientId") Long clientId,
                                                    @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of employee ratings for given client by executing ClientResource.EmployeeRatingResource.countClientEmployeeRatings(clientId) method of REST API");

            // find client entity for which to count employee ratings
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(employeeRatingFacade.countClientRatings(client)), 200,
                    "number of employee ratings for client with id " + client.getClientId());

            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method returns subset of Employee Rating entities for given Client
         * that have been granted given rating.
         * The client id and rating are passed through path params.
         */
        @GET
        @Path("/rated/{rating : \\d+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientEmployeeRatingsByRating( @PathParam("clientId") Long clientId,
                                                          @PathParam("rating") Short rating,
                                                          @BeanParam PaginationBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employee ratings for given client and rating using ClientResource.EmployeeRatingResource.getClientEmployeeRatingsByRating(clientId, rating) method of REST API");

            // find client entity for which to get associated employee ratings
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // find employee ratings by given criteria (client and rating)
            ResourceList<EmployeeRating> employeeRatings = new ResourceList<>(
                    employeeRatingFacade.findFromClientByRating(client, rating, params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeRatingResource.populateWithHATEOASLinks(employeeRatings, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employeeRatings).build();
        }

        /**
         * Method returns subset of Employee Rating entities for given Client
         * rated above given minimal rating.
         * The client id and minimal rating are passed through path params.
         */
        @GET
        @Path("/rated-above/{minRating : \\d+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientEmployeeRatingsAboveMinimalRating( @PathParam("clientId") Long clientId,
                                                                    @PathParam("minRating") Short minRating,
                                                                    @BeanParam PaginationBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employee ratings for given client rated above given minimal rating using " +
                    "ClientResource.EmployeeRatingResource.getClientEmployeeRatingsAboveMinimalRating(clientId, minRating) method of REST API");

            // find client entity for which to get associated employee ratings
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // find employee ratings by given criteria (client and min rating)
            ResourceList<EmployeeRating> employeeRatings = new ResourceList<>(
                    employeeRatingFacade.findFromClientAboveRating(client, minRating, params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeRatingResource.populateWithHATEOASLinks(employeeRatings, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employeeRatings).build();
        }

        /**
         * Method returns subset of Employee Rating entities for given Client
         * rated below given maximal rating.
         * The client id and maximal rating are passed through path params.
         */
        @GET
        @Path("/rated-below/{maxRating : \\d+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientEmployeeRatingsBelowMaximalRating( @PathParam("clientId") Long clientId,
                                                                    @PathParam("maxRating") Short maxRating,
                                                                    @BeanParam PaginationBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employee ratings for given client rated below given maximal rating using " +
                    "ClientResource.EmployeeRatingResource.getClientEmployeeRatingsBelowMaximalRating(clientId, maxRating) method of REST API");

            // find client entity for which to get associated employee ratings
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // find employee ratings by given criteria (client and max rating)
            ResourceList<EmployeeRating> employeeRatings = new ResourceList<>(
                    employeeRatingFacade.findFromClientBelowRating(client, maxRating, params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeRatingResource.populateWithHATEOASLinks(employeeRatings, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employeeRatings).build();
        }

    }

    public class ProviderResource {

        public ProviderResource() {
        }

        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientRatedProviders(@PathParam("clientId") Long clientId,
                                                @BeanParam ProviderBeanParam params) throws ForbiddenException, NotFoundException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning providers rated by given client using ClientResource.ProviderResource.getClientRatedProviders(clientId) method of REST API");

            // find client entity for which to get rated by it providers
            Client client = clientFacade.find(clientId);
            if (client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<Provider> providers = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Client> clients = new ArrayList<>();
                clients.add(client);

                // get providers for given client filtered by given params.
                providers = new ResourceList<>(
                        providerFacade.findByMultipleCriteria(params.getCorporations(), params.getProviderTypes(), params.getIndustries(), params.getPaymentMethods(),
                                params.getServices(), params.getRated(), params.getMinAvgRating(), params.getMaxAvgRating(), clients,
                                params.getProviderName(), params.getDescription(), params.getOffset(), params.getLimit())
                );
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get providers for given client without filtering
                providers = new ResourceList<>(providerFacade.findRatedByClient(client, params.getOffset(), params.getLimit()));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providers).build();
        }

        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientRatedProvidersEagerly(@PathParam("clientId") Long clientId,
                                                       @BeanParam ProviderBeanParam params) throws ForbiddenException, NotFoundException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning providers eagerly rated by given client using ClientResource.ProviderResource.getClientRatedProvidersEagerly(clientId) method of REST API");

            // find client entity for which to get rated by it providers
            Client client = clientFacade.find(clientId);
            if (client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

           Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ProviderWrapper> providers = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Client> clients = new ArrayList<>();
                clients.add(client);

                // get providers eagerly for given client filtered by given params
                providers = new ResourceList<>(
                        ProviderWrapper.wrap(
                                providerFacade.findByMultipleCriteriaEagerly(params.getCorporations(), params.getProviderTypes(), params.getIndustries(),
                                        params.getPaymentMethods(), params.getServices(), params.getRated(), params.getMinAvgRating(), params.getMaxAvgRating(),
                                        clients, params.getProviderName(), params.getDescription(), params.getOffset(), params.getLimit())
                        )
                );

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get providers eagerly for given client without filtering
                providers = new ResourceList<>(
                        ProviderWrapper.wrap(providerFacade.findRatedByClientEagerly(client, params.getOffset(), params.getLimit()))
                );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providers).build();
        }
    }

    public class EmployeeResource {

        public EmployeeResource() { }

        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientRatedEmployees( @PathParam("clientId") Long clientId,
                                                 @BeanParam EmployeeBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, javax.transaction.NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employees rated by given client using ClientResource.EmployeeResource.getClientRatedEmployees(clientId) method of REST API");

            // find client entity for which to get rated by it employees
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<Employee> employees = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Client> clients = new ArrayList<>();
                clients.add(client);

                utx.begin();

                // get employees for given client filtered by given params
                employees = new ResourceList<>(
                        employeeFacade.findByMultipleCriteria(params.getDescriptions(), params.getJobPositions(), params.getSkills(), params.getEducations(),
                                params.getServices(), params.getProviderServices(), params.getServicePoints(), params.getWorkStations(), params.getPeriod(),
                                params.getStrictTerm(),  params.getTerms(), params.getRated(), params.getMinAvgRating(), params.getMaxAvgRating(), clients,
                                params.getOffset(), params.getLimit())
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                /// get employees for given client without filtering
                employees = new ResourceList<>(employeeFacade.findRatedByClient(client, params.getOffset(), params.getLimit()));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeResource.populateWithHATEOASLinks(employees, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employees).build();
        }

        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientRatedEmployeesEagerly( @PathParam("clientId") Long clientId,
                                                        @BeanParam EmployeeBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, javax.transaction.NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employees eagerly rated by given client using ClientResource.EmployeeResource.getClientRatedEmployeesEagerly(clientId) method of REST API");

            // find client entity for which to get rated by it employees
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<EmployeeWrapper> employees = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Client> clients = new ArrayList<>();
                clients.add(client);

                utx.begin();

                // get employees eagerly for given client filtered by given params
                employees = new ResourceList<>(
                        EmployeeWrapper.wrap(
                                employeeFacade.findByMultipleCriteriaEagerly(params.getDescriptions(), params.getJobPositions(), params.getSkills(),
                                        params.getEducations(), params.getServices(), params.getProviderServices(), params.getServicePoints(),
                                        params.getWorkStations(), params.getPeriod(), params.getStrictTerm(), params.getTerms(), params.getRated(),
                                        params.getMinAvgRating(), params.getMaxAvgRating(), clients, params.getOffset(), params.getLimit())
                        )
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get employees eagerly for given client without filtering
                employees = new ResourceList<>(
                        EmployeeWrapper.wrap( employeeFacade.findRatedByClientEagerly(client, params.getOffset(), params.getLimit()) )
                );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeResource.populateWithHATEOASLinks(employees, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employees).build();
        }

    }

    public class CreditCardResource {

        public CreditCardResource() { }

        /**
         * Method that returns subset of Credit Card entities for given Client.
         * The client id is passed through path param.
         * Credit cards can be additionally filtered and paginated by given @QueryParams
         * passed in by @BeanParam object.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientCreditCards( @PathParam("clientId") Long clientId,
                                              @BeanParam CreditCardBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning credit cards by given client using ClientResource.CreditCardResource.getClientCreditCards(clientId) method of REST API");

            // find client entity for which to get associated credit cards
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId);

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<CreditCard> creditCards = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Client> clients = new ArrayList<>();
                clients.add(client);

                // get credit cards for given client filtered by given criteria
                creditCards = new ResourceList<>(
                    creditCardFacade.findByMultipleCriteria(clients, params.getCardTypes(), params.getCardNumber(), params.getCardHolder(),
                            params.getExpired(), params.getTheEarliestExpirationDate(), params.getTheLatestExpirationDate(), params.getOffset(), params.getLimit())
                );

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get all credit cards for given client without additional filtering (eventually paginated)
                creditCards = new ResourceList<>( creditCardFacade.findByClient(client, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.CreditCardResource.populateWithHATEOASLinks(creditCards, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(creditCards).build();
        }

        /**
         * Method that returns specified Credit Card entity by given composite Credit Card Id.
         * The Credit Card Id consist of client id, card number and expiration date passed through path params.
         */
        @GET
        @Path("/{cardNumber : \\S+}/expiring/{expirationDate: \\S+}")
        public Response getCreditCard( @PathParam("clientId") Long clientId,
                                       @PathParam("cardNumber") String cardNumber,
                                       @PathParam("expirationDate") RESTDateTime expirationDate,
                                       @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning given Credit Card for given client, card number and expiration date by executing " +
                    "ClientResource.CreditCardResource.getCreditCard(clientId, cardNumber, expirationDate) method of REST API");

            CreditCard foundCreditCard = creditCardFacade.find( new CreditCardId(clientId, cardNumber, expirationDate));
            if(foundCreditCard == null)
                throw new NotFoundException("Could not find credit card for id (" + clientId + "," + cardNumber + "," + expirationDate + ").");

            // adding hypermedia links to credit card resource
            pl.salonea.jaxrs.CreditCardResource.populateWithHATEOASLinks(foundCreditCard, params.getUriInfo());

            return Response.status(Status.OK).entity(foundCreditCard).build();
        }

        /**
         * Method that takes Credit Card as XML or JSON and creates its new instance for given Client in database.
         * The client id for which Credit Card is created is passed through path param.
         */
        @POST
        @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response createCreditCard( @PathParam("clientId") Long clientId,
                                          CreditCard creditCard,
                                          @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "creating new Credit Card for given Client by executing ClientResource.CreditCardResource.createCreditCard(clientId, creditCard) method of REST API");

            CreditCard createdCreditCard = null;
            URI locationURI = null;

            try {
                // persist new resource in database
                createdCreditCard = creditCardFacade.createForClient(clientId, creditCard);

                // populate created resource with hypermedia links
                pl.salonea.jaxrs.CreditCardResource.populateWithHATEOASLinks(createdCreditCard, params.getUriInfo());

                // construct link to newly created resource to return in HTTP Header
                Method clientCreditCardsMethod = ClientResource.class.getMethod("getCreditCardResource");
                Method creditCardMethod = ClientResource.CreditCardResource.class.getMethod("getCreditCard", Long.class, String.class, RESTDateTime.class, GenericBeanParam.class);
                locationURI = params.getUriInfo().getBaseUriBuilder()
                        .path(ClientResource.class)
                        .path(clientCreditCardsMethod)
                        .path(creditCardMethod)
                        .resolveTemplate("clientId", String.valueOf(createdCreditCard.getClient().getClientId()))
                        .resolveTemplate("cardNumber", createdCreditCard.getCreditCardNumber())
                        .resolveTemplate("expirationDate", createdCreditCard.getExpirationDate().toString())
                        .build();

            } catch (EJBTransactionRolledbackException ex) {
                ExceptionHandler.handleEJBTransactionRolledbackException(ex);
            } catch (EJBException ex) {
                ExceptionHandler.handleEJBException(ex);
            } catch (Exception ex) {
                throw new InternalServerErrorException(ExceptionHandler.ENTITY_CREATION_ERROR_MESSAGE);
            }

            return Response.created(locationURI).entity(createdCreditCard).build();
        }

        /**
         * Method that takes updated Credit Card as XML or JSON and its composite Credit Card Id as path param.
         * It updated Credit Card in database for provided id consisting of: clientId, card number and expiration date.
         */
        @PUT
        @Path("/{cardNumber : \\S+}/expiring/{expirationDate: \\S+}")
        @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response updateCreditCard( @PathParam("clientId") Long clientId,
                                          @PathParam("cardNumber") String cardNumber,
                                          @PathParam("expirationDate") RESTDateTime expirationDate,
                                          CreditCard creditCard,
                                          @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "updating existing Credit Card for given composite id: clientId, card number and expiration date by executing " +
                    "ClientResource.CreditCardResource.updateCreditCard(clientId, cardNumber, expirationDate, creditCard) method of REST API");

            // create composite ID based on path params
            CreditCardId creditCardId = new CreditCardId(clientId, cardNumber, expirationDate);

            CreditCard updatedCreditCard = null;
            try {
                // reflect updated resource object in database
                updatedCreditCard = creditCardFacade.update(creditCardId, creditCard);
                // populate created resource with hypermedia links
                pl.salonea.jaxrs.CreditCardResource.populateWithHATEOASLinks(updatedCreditCard, params.getUriInfo());

            } catch (EJBTransactionRolledbackException ex) {
                ExceptionHandler.handleEJBTransactionRolledbackException(ex);
            } catch (EJBException ex) {
                ExceptionHandler.handleEJBException(ex);
            } catch (Exception ex) {
                throw new InternalServerErrorException(ExceptionHandler.ENTITY_UPDATE_ERROR_MESSAGE);
            }

            return Response.status(Status.OK).entity(updatedCreditCard).build();
        }

        /**
         * Method that removes subset of Credit Card entities from database for given Client.
         * The client id is passed through path params.
         */
        @DELETE
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response removeClientCreditCards( @PathParam("clientId") Long clientId,
                                                 @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "removing subset of Credit Card entities for given Client by executing ClientResource.CreditCardResource.removeClientCreditCards(clientId) method of REST API");

            // find client entity for which to remove credit cards
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // remove all specified entities from database
            Integer noOfDeleted = creditCardFacade.deleteForClient(client);

            // create response returning number of deleted entities
            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfDeleted), 200, "number of deleted credit cards for client with id " + clientId);

            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method that removes Credit Card entity from database for given ID.
         * The credit card composite id consisting of client id, card number,
         * expiration date is passed through path params.
         */
        @DELETE
        @Path("/{cardNumber : \\S+}/expiring/{expirationDate: \\S+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response removeCreditCard( @PathParam("clientId") Long clientId,
                                          @PathParam("cardNumber") String cardNumber,
                                          @PathParam("expirationDate") RESTDateTime expirationDate,
                                          @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException, InternalServerErrorException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "removing given Credit Card by executing ClientResource.CreditCardResource.removeCreditCard(clientId, cardNumber, expirationDate) method of REST API");

            // remove entity from database
            Integer noOfDeleted = creditCardFacade.deleteById( new CreditCardId(clientId, cardNumber, expirationDate) );

            if(noOfDeleted == 0)
                throw new NotFoundException("Could not find credit card to delete for id (" + clientId + "," + cardNumber + "," + expirationDate + ").");
            else if(noOfDeleted != 1)
                throw new InternalServerErrorException("Some error occurred while trying to delete credit card with id (" + clientId + "," + cardNumber + "," + expirationDate + ").");

            return Response.status(Status.NO_CONTENT).build();
        }


        /**
         * Additional methods returning subset of resources based on given criteria
         * you can achieve similar results by applying @QueryParams to generic method
         * returning all resources in order to filter and limit them
         */

        /**
         * Method that counts Credit Card entities for given Client
         * The client id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countCreditCardsByClient( @PathParam("clientId") Long clientId,
                                                  @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of credit cards for given client by executing ClientResource.CreditCardResource.countCreditCardsByClient(clientId) method of REST API");

            // find client entity for which to count credit cards
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(creditCardFacade.countByClient(client)), 200, "number of credit cards for client with id " + client.getClientId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method returns subset of Credit Card entities for given client
         * and card type. The client id and card type are passed through path params.
         */
        @GET
        @Path("/typed/{cardType : \\S+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientCreditCardsByCardType( @PathParam("clientId") Long clientId,
                                                        @PathParam("cardType") CreditCardType cardType,
                                                        @BeanParam PaginationBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning credit cards for given client and card type using ClientResource.CreditCardResource.getClientCreditCardsByCardType(clientId, cardType) method of REST API");

            // find client entity for which to get associated credit cards
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            if(cardType == null)
                throw new BadRequestException("Card type param cannot be null.");

            // find credit cards by given criteria (client and card type)
            ResourceList<CreditCard> creditCards = new ResourceList<>(
                    creditCardFacade.findByClientAndType(client, cardType, params.getOffset(), params.getLimit()) );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.CreditCardResource.populateWithHATEOASLinks(creditCards, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(creditCards).build();
        }

        /**
         * Method that returns subset of Credit Card entities for given client
         * that have already expired. The client id is passed through path param.
         */
        @GET
        @Path("/expired")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientCreditCardsThatExpired( @PathParam("clientId") Long clientId,
                                                         @BeanParam PaginationBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning credit cards for given client that have already expired using " +
                    "ClientResource.CreditCardResource.getClientCreditCardsThatExpired(clientId) method of REST API");

            // find client entity for which to get associated credit cards
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // find credit cards by given criteria (client and expired)
            ResourceList<CreditCard> creditCards = new ResourceList<>(
                    creditCardFacade.findExpiredByClient(client, params.getOffset(), params.getLimit()) );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.CreditCardResource.populateWithHATEOASLinks(creditCards, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(creditCards).build();
        }

        /**
         * Method that returns subset of Credit Card entities for given client
         * that haven't expired yet. The client id is passed through path param.
         */
        @GET
        @Path("/not-expired")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientCreditCardsThatNotExpired( @PathParam("clientId") Long clientId,
                                                            @BeanParam PaginationBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning credit cards for given client that haven't expired yet using " +
                    "ClientResource.CreditCardResource.getClientCreditCardsThatNotExpired(clientId) method of REST API");

            // find client entity for which to get associated credit cards
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // find credit cards by given criteria (client and not expired)
            ResourceList<CreditCard> creditCards = new ResourceList<>(
                    creditCardFacade.findNotExpiredByClient(client, params.getOffset(), params.getLimit()) );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.CreditCardResource.populateWithHATEOASLinks(creditCards, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(creditCards).build();
        }

        /**
         * Method that returns subset of Credit Card entities for given client
         * and expiration date after given date.
         * The client id and date are passed through path params.
         */
        @GET
        @Path("/expiring-after/{expirationDate : \\S+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientCreditCardsExpiringAfter( @PathParam("clientId") Long clientId,
                                                           @PathParam("expirationDate") RESTDateTime expirationDate,
                                                           @BeanParam PaginationBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning credit cards for given client expiring after given date " +
                    "ClientResource.CreditCardResource.getClientCreditCardsExpiringAfter(clientId, expirationDate) method of REST API");

            // find client entity for which to get associated credit cards
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            if(expirationDate == null)
                throw new BadRequestException("Expiration date param cannot be null.");

            // find credit cards by given criteria (client and expiration date)
            ResourceList<CreditCard> creditCards = new ResourceList<>(
                    creditCardFacade.findExpirationDateAfterByClient(expirationDate, client, params.getOffset(), params.getLimit()) );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.CreditCardResource.populateWithHATEOASLinks(creditCards, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(creditCards).build();
        }

        /**
         * Method that returns subset of Credit Card entities for given client
         * and expiration date before given date.
         * The client id and date are passed through path params.
         */
        @GET
        @Path("/expiring-before/{expirationDate : \\S+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientCreditCardsExpiringBefore( @PathParam("clientId") Long clientId,
                                                            @PathParam("expirationDate") RESTDateTime expirationDate,
                                                            @BeanParam PaginationBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning credit cards for given client expiring before given date " +
                    "ClientResource.CreditCardResource.getClientCreditCardsExpiringBefore(clientId, expirationDate) method of REST API");

            // find client entity for which to get associated credit cards
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            if(expirationDate == null)
                throw new BadRequestException("Expiration date param cannot be null.");

            // find credit cards by given criteria (client and expiration date)
            ResourceList<CreditCard> creditCards = new ResourceList<>(
                    creditCardFacade.findExpirationDateBeforeByClient(expirationDate, client, params.getOffset(), params.getLimit()) );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.CreditCardResource.populateWithHATEOASLinks(creditCards, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(creditCards).build();
        }

        /**
         * Method that returns subset of Credit Card entities for given Client
         * and expiration date between given the earliest date and the latest date.
         * The client id is passed through path param and  the earliest date (start date)
         * and the latest date (end date) are passed through query params.
         */
        @GET
        @Path("/expiring-between")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientCreditCardsExpiringBetween( @PathParam("clientId") Long clientId,
                                                             @BeanParam DateRangeBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning credit cards for given client expiring between given start and end date using " +
                   "ClientResource.CreditCardResource.getClientCreditCardsExpiringBetween(clientId, dates) method of REST API" );

            // check correctness of query params
            if(params.getStartDate() == null || params.getEndDate() == null)
                throw new BadRequestException("Start date or end date query param not specified for request.");

            if(params.getStartDate().after(params.getEndDate()))
                throw new BadRequestException("Start date is after end date.");

            // find client entity for which to get associated credit cards
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // find credit cards by given criteria (client, start and end expiration dates)
            ResourceList<CreditCard> creditCards = new ResourceList<>(
                    creditCardFacade.findExpirationDateBetweenByClient(params.getStartDate(), params.getEndDate(), client, params.getOffset(), params.getLimit()));

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.CreditCardResource.populateWithHATEOASLinks(creditCards, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(creditCards).build();
        }

        /**
         * Additional methods removing subset of resources by given criteria
         */

        /**
         * Method that removes subset of Credit Card entities from database
         * for given Client and expiration date after given date.
         * The client id and date are passed through path params.
         */
        @DELETE
        @Path("/expiring-after/{expirationDate : \\S+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response removeClientCreditCardsExpiringAfter( @PathParam("clientId") Long clientId,
                                                              @PathParam("expirationDate") RESTDateTime expirationDate,
                                                              @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "removing subset of Credit Card entities for given Client expiring after given date " +
                    "by executing ClientResource.CreditCardResource.removeClientCreditCardsExpiringAfter(clientId, expirationDate) method of REST API");

            // find client entity for which to remove credit cards
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            if(expirationDate == null)
                throw new BadRequestException("Expiration date param cannot be null.");

            // remove specified entities from database
            Integer noOfDeleted = creditCardFacade.deleteWithExpirationDateAfterForClient(expirationDate, client);

            // create response returning number of deleted entities
            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfDeleted), 200, "number of deleted credit cards for client with id " + clientId + " and expiring after " + expirationDate);

            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method that removes subset of Credit Card entities from database
         * for given Client and expiration date before given date.
         * The client id and date are passed through path params.
         */
        @DELETE
        @Path("/expiring-before/{expirationDate : \\S+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response removeClientCreditCardsExpiringBefore( @PathParam("clientId") Long clientId,
                                                               @PathParam("expirationDate") RESTDateTime expirationDate,
                                                               @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "removing subset of Credit Card entities for given Client expiring before given date " +
                "by executing ClientResource.CreditCardResource.removeClientCreditCardsExpiringBefore(clientId, expirationDate) method of REST API");

            // find client entity for which to remove credit cards
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            if(expirationDate == null)
                throw new BadRequestException("Expiration date param cannot be null.");

            // remove specified entities from database
            Integer noOfDeleted = creditCardFacade.deleteWithExpirationDateBeforeForClient(expirationDate, client);

            // create response returning number of deleted entities
            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfDeleted), 200, "number of deleted credit cards for client with id " + clientId + " and expiring before " + expirationDate);

            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method that removes subset of Credit Card entities from database
         * for given Client and expiration date between given start and end dates.
         * The client id is passed through path param,
         * start date and end date are passed through query params.
         */
        @DELETE
        @Path("/expiring-between")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response removeClientCreditCardsExpiringBetween( @PathParam("clientId") Long clientId,
                                                                @BeanParam DateRangeBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "removing subset of Credit Card entities for given Client expiring between given start and end dates " +
                    "by executing ClientResource.CreditCardResource.removeClientCreditCardsExpiringBetween(clientId, dates) method of REST API");

            // check correctness of query params
            if(params.getStartDate() == null || params.getEndDate() == null)
                throw new BadRequestException("Start date or end date query param not specified for request.");

            if(params.getStartDate().after(params.getEndDate()))
                throw new BadRequestException("Start date is after end date.");

            // find client entity for which to remove credit cards
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // remove specified entities from database
            Integer noOfDeleted = creditCardFacade.deleteWithExpirationDateBetweenForClient(params.getStartDate(), params.getEndDate(), client);

            // create response returning number of deleted entities
            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfDeleted), 200, "number of deleted credit cards for client with id " + clientId + " and expiring after " + params.getStartDate() + " and before " + params.getEndDate());

            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method that removes subset of Credit Card entities from database
         * for given Client that have already expired.
         * The client id is passed through path param.
         */
        @DELETE
        @Path("/expired")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response removeClientCreditCardsThatExpired( @PathParam("clientId") Long clientId,
                                                            @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "removing subset of Credit Card entities for given Client that have already expired " +
                    "by executing ClientResource.CreditCardResource.removeClientCreditCardsThatExpired(clientId) method of REST API");

            // find client entity for which to remove credit cards
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // remove specified entities from database
            Integer noOfDeleted = creditCardFacade.deleteExpiredForClient(client);

            // create response returning number of deleted entities
            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfDeleted), 200, "number of deleted credit cards for client with id " + clientId + " that have already expired");

            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method that removes subset of Credit Card entities from database
         * for given client and card type. The client id and card type are passed through path params.
         */
        @DELETE
        @Path("/typed/{cardType : \\S+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response removeClientCreditCardsByCardType( @PathParam("clientId") Long clientId,
                                                           @PathParam("cardType") CreditCardType cardType,
                                                           @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "removing subset of Credit Card entities for given Client and card type " +
                    "by executing ClientResource.CreditCardResource.removeClientCreditCardsByCardType(clientId, cardType) method of REST API");

            // find client entity for which to remove credit cards
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            if(cardType == null)
                throw new BadRequestException("Card type param cannot be null.");

            // remove specified entities from database
            Integer noOfDeleted = creditCardFacade.deleteWithTypeForClient(cardType, client);

            // create response returning number of deleted entities
            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfDeleted), 200, "number of deleted credit cards for client with id " + clientId + " and card type " + cardType);

            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    public class TransactionResource {

        public TransactionResource() { }

        /**
         * Method returns subset of Transaction entities for given Client
         * The client id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientTransactions(@PathParam("clientId") Long clientId,
                                              @BeanParam TransactionBeanParam params) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Transaction entities for given Client using ClientResource.TransactionResource.getClientTransactions(clientId) method of REST API");

            // find client entity for which to get associated transactions
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<Transaction> transactions = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Client> clients = new ArrayList<>();
                clients.add(client);

                // get transaction entities for given client filtered by given query params
                utx.begin();

                transactions = new ResourceList<>(
                        transactionFacade.findByMultipleCriteria(clients, params.getProviders(), params.getServices(),
                                params.getServicePoints(), params.getWorkStations(), params.getEmployees(), params.getProviderServices(),
                                params.getTransactionTimePeriod(), params.getBookedTimePeriod(), params.getTerms(), params.getPriceRange(),
                                params.getCurrencyCodes(), params.getPaymentMethods(), params.getPaid(), params.getOffset(), params.getLimit())
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get transaction entities for given client without filtering (eventually paginated)
                transactions = new ResourceList<>( transactionFacade.findByClient(client, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.TransactionResource.populateWithHATEOASLinks(transactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(transactions).build();
        }

        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientTransactionsEagerly(@PathParam("clientId") Long clientId,
                                                     @BeanParam TransactionBeanParam params) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Transaction entities for given Client eagerly using ClientResource.TransactionResource.getClientTransactionsEagerly(clientId) method of REST API");

            // find client entity for which to get associated transactions
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<TransactionWrapper> transactions = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Client> clients = new ArrayList<>();
                clients.add(client);

                // get transaction entities eagerly for given client filtered by given query params
                utx.begin();

                transactions = new ResourceList<>(
                        TransactionWrapper.wrap(
                                transactionFacade.findByMultipleCriteriaEagerly(clients, params.getProviders(), params.getServices(),
                                        params.getServicePoints(), params.getWorkStations(), params.getEmployees(), params.getProviderServices(),
                                        params.getTransactionTimePeriod(), params.getBookedTimePeriod(), params.getTerms(), params.getPriceRange(),
                                        params.getCurrencyCodes(), params.getPaymentMethods(), params.getPaid(), params.getOffset(), params.getLimit())
                        )
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get transaction entities eagerly for given client without filtering (eventually paginated)
                transactions = new ResourceList<>( TransactionWrapper.wrap(transactionFacade.findByClientEagerly(client, params.getOffset(), params.getLimit())) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.TransactionResource.populateWithHATEOASLinks(transactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(transactions).build();
        }

        /**
         * Method matches specific Transaction resource by composite identifier and returns its instance.
         */
        @GET
        @Path("/{transactionNumber: \\d+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getTransaction( @PathParam("clientId") Long clientId,
                                        @PathParam("transactionNumber") Integer transactionNumber,
                                        @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning given Transaction by executing ClientResource.TransactionResource.getTransaction(clientId, transactionNumber) method of REST API");

            Transaction foundTransaction = transactionFacade.find(new TransactionId(clientId, transactionNumber));
            if(foundTransaction == null)
                throw new NotFoundException("Could not find transaction for id (" + clientId + "," + transactionNumber + ").");

            // adding hypermedia links to transaction resource
            pl.salonea.jaxrs.TransactionResource.populateWithHATEOASLinks(foundTransaction, params.getUriInfo());

            return Response.status(Status.OK).entity(foundTransaction).build();
        }

        /**
         * Method matches specific Transaction resource by composite identifier and returns its instance fetching it eagerly
         */
        @GET
        @Path("/{transactionNumber: \\d+}/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getTransactionEagerly( @PathParam("clientId") Long clientId,
                                               @PathParam("transactionNumber") Integer transactionNumber,
                                               @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning given Transaction eagerly by executing ClientResource.TransactionResource.getTransactionEagerly(clientId, transactionNumber) method of REST API");

            Transaction foundTransaction = transactionFacade.findByIdEagerly(new TransactionId(clientId, transactionNumber));
            if(foundTransaction == null)
                throw new NotFoundException("Could not find transaction for id (" + clientId + "," + transactionNumber + ").");

            // wrapping Transaction into TransactionWrapper in order to marshall eagerly fetched associated collection of entities
            TransactionWrapper wrappedTransaction = new TransactionWrapper(foundTransaction);

            // adding hypermedia links to wrapped transaction resource
            pl.salonea.jaxrs.TransactionResource.populateWithHATEOASLinks(wrappedTransaction, params.getUriInfo());

            return Response.status(Status.OK).entity(wrappedTransaction).build();
        }

        /**
         *  Method that takes Transaction as XML or JSON and creates its new instance in database
         */
        @POST
        @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response createTransaction( @PathParam("clientId") Long clientId,
                                           Transaction transaction,
                                           @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "creating new Transaction by executing ClientResource.TransactionResource.createTransaction(transaction) method of REST API");

            Transaction createdTransaction = null;
            URI locationURI = null;

            try {
                // persist new resource in database
                createdTransaction = transactionFacade.createForClient(clientId, transaction);

                // populate created resource with hypermedia links
                pl.salonea.jaxrs.TransactionResource.populateWithHATEOASLinks(createdTransaction, params.getUriInfo());

                // construct link to newly created resource to return in HTTP Header
                String txClientId = String.valueOf(createdTransaction.getClient().getClientId());
                String transactionNumber = String.valueOf(createdTransaction.getTransactionNumber());

                Method transactionsMethod = ClientResource.class.getMethod("getTransactionResource");
                locationURI = params.getUriInfo().getBaseUriBuilder()
                        .path(ClientResource.class)
                        .path(transactionsMethod)
                        .path(transactionNumber)
                        .resolveTemplate("clientId", txClientId)
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

            return Response.created(locationURI).entity(createdTransaction).build();
        }

        /**
         * Method that takes updated Transaction as XML or JSON and its composite ID as path params.
         * It updates Transaction in database for provided composite ID.
         */
        @PUT
        @Path("/{transactionNumber : \\d+}")
        @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response updateTransaction( @PathParam("clientId") Long clientId,
                                           @PathParam("transactionNumber")  Integer transactionNumber,
                                           Transaction transaction,
                                           @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "updating existing Transaction by executing ClientResource.TransactionResource.updateTransaction(transaction) method of REST API");

            // create composite ID based on path params
            TransactionId transactionId = new TransactionId(clientId, transactionNumber);

            Transaction updatedTransaction = null;
            try {
                // reflect updated resource object in database
                updatedTransaction = transactionFacade.update(transactionId, transaction);
                // populate created resource with hypermedia links
                pl.salonea.jaxrs.TransactionResource.populateWithHATEOASLinks(updatedTransaction, params.getUriInfo());

            } catch (EJBTransactionRolledbackException ex) {
                ExceptionHandler.handleEJBTransactionRolledbackException(ex);
            } catch (EJBException ex) {
                ExceptionHandler.handleEJBException(ex);
            } catch (Exception ex) {
                throw new InternalServerErrorException(ExceptionHandler.ENTITY_UPDATE_ERROR_MESSAGE);
            }

            return Response.status(Status.OK).entity(updatedTransaction).build();
        }

        /**
         * Method that removes subset of Transaction entities for given Client from database.
         * The client id is passed through path param.
         */
        @DELETE
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response removeClientTransactions(@PathParam("clientId") Long clientId,
                                                 @BeanParam GenericBeanParam params) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "removing subset of Transaction entities for given Client by executing ClientResource.TransactionResource.removeClientTransactions(clientId) method of REST API");

            // find client entity for which to remove transactions
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            utx.begin();

            // remove associations between transactions and employees for given client from database
            transactionEmployeeRelationshipManager.removeEmployeesFromTransactionsByClient(client.getClientId());

            // remove transactions for given client from database
            Integer noOfDeleted = transactionFacade.deleteByClient(client);

            utx.commit();

            // create response returning number of deleted entities
            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfDeleted), 200, "number of deleted transactions for client with id " + clientId);

            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method that removes Transaction entity from database for given ID.
         * The transaction composite id is passed through path param.
         */
        @DELETE
        @Path("/{transactionNumber : \\d+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response removeTransaction(@PathParam("clientId") Long clientId,
                                          @PathParam("transactionNumber") Integer transactionNumber,
                                          @BeanParam GenericBeanParam params) throws ForbiddenException, NotFoundException, InternalServerErrorException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "removing given Transaction by executing ClientResource.TransactionResource.removeTransaction(clientId, transactionNumber) method of REST API");

            utx.begin();

            // remove associations between transaction and employees from database
            transactionEmployeeRelationshipManager.removeAllEmployeesFromTransaction(new TransactionId(clientId, transactionNumber));

            // remove transaction entity from database
            Integer noOfDeleted = transactionFacade.deleteById(new TransactionId(clientId, transactionNumber));

            utx.commit();

            if (noOfDeleted == 0)
                throw new NotFoundException("Could not find transaction to delete for id (" + clientId + "," + transactionNumber + ").");
            else if (noOfDeleted != 1)
                throw new InternalServerErrorException("Some error occurred while trying to delete transaction with id (" + clientId + "," + transactionNumber + ").");

            return Response.status(Status.NO_CONTENT).build();
        }

        /**
         * Additional methods returning subset of resources based on given criteria
         * you can also achieve similar results by applying @QueryParams to generic method
         * returning all resources in order to filter and limit them
         */

        /**
         * Method that counts Transaction entities for given Client resource
         * The client id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countTransactionsByClient(@PathParam("clientId") Long clientId,
                                                  @BeanParam GenericBeanParam params) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of transactions for given client by executing ClientResource.TransactionResource.countTransactionsByClient(clientId) method of REST API");

            // find client entity for which to count transactions
            Client client = clientFacade.find(clientId);
            if (client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(transactionFacade.countByClient(client)), 200, "number of transactions for client with id " + client.getClientId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method returns subset of Transaction entities for given Client entity
         * and transaction time. The client id is passed through path param.
         * Transaction time range (start and end dates) is passed through query params.  i
         */
        @GET
        @Path("/by-transaction-time")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientTransactionsByTransactionTime( @PathParam("clientId") Long clientId,
                                                                @BeanParam DateRangeBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning transactions for given client and transaction time (startDate, endDate) using " +
                    "ClientResource.TransactionResource.getClientTransactionsByTransactionTime(clientId, transactionTime) method of REST API");

            RESTToolkit.validateDateRange(params); // i.e. startDate and endDate

            // find client entity for which to get associated transactions
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // find transactions by given criteria (client, transaction time)
            ResourceList<Transaction> transactions = new ResourceList<>(
                    transactionFacade.findByClientAndTransactionTime(client, params.getStartDate(), params.getEndDate(),
                            params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.TransactionResource.populateWithHATEOASLinks(transactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(transactions).build();
        }

        /**
         * Method returns subset of Transaction entities for given Client entity
         * and booked time. The client id is passed through path param.
         * Booked time range (start and end dates) is passed through query params.
         */
        @GET
        @Path("/by-booked-time")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientTransactionsByBookedTime( @PathParam("clientId") Long clientId,
                                                           @BeanParam DateRangeBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning transactions for given client and booked time (startDate, endDate) using " +
                    "ClientResource.TransactionResource.getClientTransactionsByBookedTime(clientId, bookedTime) method of REST API");

            RESTToolkit.validateDateRange(params); // i.e. startDate and endDate

            // find client entity for which to get associated transactions
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // find transactions by given criteria (client, booked time)
            ResourceList<Transaction> transactions = new ResourceList<>(
                    transactionFacade.findByClientAndBookedTime(client, params.getStartDate(), params.getEndDate(),
                            params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.TransactionResource.populateWithHATEOASLinks(transactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(transactions).build();
        }

        /**
         * Method returns subset of Transaction entities for given Client entity
         * that have already been paid. The client id is passed through path param.
         */
        @GET
        @Path("/paid")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientTransactionsPaid( @PathParam("clientId") Long clientId,
                                                   @BeanParam PaginationBeanParam params ) throws ForbiddenException, NotFoundException  {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning paid transactions for given client using " +
                    "ClientResource.TransactionResource.getClientTransactionsPaid(clientId) method of REST API");

            // find client entity for which to get associated transactions
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // find transactions by given criteria (client, paid)
            ResourceList<Transaction> transactions = new ResourceList<>(
                    transactionFacade.findByClientOnlyPaid(client, params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.TransactionResource.populateWithHATEOASLinks(transactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(transactions).build();
        }

        /**
         * Method returns subset of Transaction entities for given Client entity
         * that haven't been paid yet. The client id is passed through path param.
         */
        @GET
        @Path("/unpaid")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientTransactionsUnpaid( @PathParam("clientId") Long clientId,
                                                     @BeanParam PaginationBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning unpaid transactions for given client using " +
                    "ClientResource.TransactionResource.getClientTransactionsUnpaid(clientId) method of REST API");

            // find client entity for which to get associated transactions
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // find transactions by given criteria (client, unpaid)
            ResourceList<Transaction> transactions = new ResourceList<>(
                    transactionFacade.findByClientOnlyUnpaid(client, params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.TransactionResource.populateWithHATEOASLinks(transactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(transactions).build();
        }

        /**
         * Method returns subset of Transaction entities for given Client entity
         * and price range (and optionally currency code).
         * The client id is passed through path param.
         * The price range (and optionally currency code) are passed through query params.
         */
        @GET
        @Path("/by-price")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientTransactionsByPrice( @PathParam("clientId") Long clientId,
                                                      @BeanParam PriceRangeBeanParam params,
                                                      @QueryParam("currency") CurrencyCode currencyCode ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning transactions for given client, price range (minPrice, maxPrice) and optionally currency code using " +
                    "ClientResource.TransactionResource.getClientTransactionsByPrice(clientId, priceRange, currencyCode) method of REST API");

            RESTToolkit.validatePriceRange(params); // i.e. minPrice and maxPrice

            // find client entity for which to get associated transactions
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            ResourceList<Transaction> transactions = null;

            if(currencyCode != null) {
                // find transactions by given criteria (client, price, currency code)
                transactions = new ResourceList<>(
                        transactionFacade.findByClientAndPriceRangeAndCurrencyCode(client, params.getMinPrice(), params.getMaxPrice(),
                                currencyCode, params.getOffset(), params.getLimit())
                );
            } else {
                // find transactions by given criteria (client, price)
                transactions = new ResourceList<>(
                        transactionFacade.findByClientAndPriceRange(client, params.getMinPrice(), params.getMaxPrice(),
                                params.getOffset(), params.getLimit())
                );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.TransactionResource.populateWithHATEOASLinks(transactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(transactions).build();
        }

        /**
         * Method returns subset of Transaction entities for given Client entity
         * and currency code. The client id is passed through path param.
         * The currency code is also passed through path param.
         */
        @GET
        @Path("/by-currency/{currencyCode : \\S+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientTransactionsByCurrency( @PathParam("clientId") Long clientId,
                                                         @PathParam("currencyCode") CurrencyCode currencyCode,
                                                         @BeanParam PaginationBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning transactions for given client and currency code using " +
                    "ClientResource.TransactionResource.getClientTransactionsByCurrency(clientId, currencyCode) method of REST API");

            // find client entity for which to get associated transactions
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // find transactions by given criteria (client, currency code)
            ResourceList<Transaction> transactions = new ResourceList<>(
                    transactionFacade.findByClientAndCurrencyCode(client, currencyCode, params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.TransactionResource.populateWithHATEOASLinks(transactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(transactions).build();
        }
    }

    public class HistoricalTransactionResource {

        public HistoricalTransactionResource() { }

        /**
         * Method returns subset of Historical Transaction entities for given Client
         * The client id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientHistoricalTransactions(@PathParam("clientId") Long clientId,
                                                        @BeanParam HistoricalTransactionBeanParam params) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Historical Transaction entities for given Client using ClientResource.HistoricalTransactionResource.getClientHistoricalTransactions(clientId) method of REST API");

            // find client entity for which to get associated historical transactions
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<HistoricalTransaction> historicalTransactions = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Client> clients = new ArrayList<>();
                clients.add(client);

                // get historical transaction entities for given client filtered by given query params
                utx.begin();

                historicalTransactions = new ResourceList<>(
                        historicalTransactionFacade.findByMultipleCriteria(clients, params.getProviders(), params.getServices(),
                                params.getServicePoints(), params.getWorkStations(), params.getEmployees(), params.getProviderServices(),
                                params.getTransactionTimePeriod(), params.getBookedTimePeriod(), params.getTerms(), params.getPriceRange(),
                                params.getCurrencyCodes(), params.getPaymentMethods(), params.getPaid(), params.getCompletionStatuses(),
                                params.getClientRatingRange(), params.getClientComments(), params.getProviderRatingRange(), params.getProviderDementis(),
                                params.getOffset(), params.getLimit())
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get historical transaction entities for given client without filtering (eventually paginated)
                historicalTransactions = new ResourceList<>( historicalTransactionFacade.findByClient(client, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(historicalTransactions).build();
        }

        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientHistoricalTransactionsEagerly(@PathParam("clientId") Long clientId,
                                                               @BeanParam HistoricalTransactionBeanParam params) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Historical Transaction entities for given Client eagerly using ClientResource.HistoricalTransactionResource.getClientHistoricalTransactionsEagerly(clientId) method of REST API");

            // find client entity for which to get associated historical transactions
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<HistoricalTransactionWrapper> historicalTransactions = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Client> clients = new ArrayList<>();
                clients.add(client);

                // get historical transaction entities eagerly for given client filtered by given query params
                utx.begin();

                historicalTransactions = new ResourceList<>(
                        HistoricalTransactionWrapper.wrap(
                                historicalTransactionFacade.findByMultipleCriteriaEagerly(clients, params.getProviders(), params.getServices(),
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

                // get historical transaction entities eagerly for given client without filtering (eventually paginated)
                historicalTransactions = new ResourceList<>( HistoricalTransactionWrapper.wrap(historicalTransactionFacade.findByClientEagerly(client, params.getOffset(), params.getLimit())) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(historicalTransactions).build();
        }

        /**
         * Method matches specific Historical Transaction resource by composite identifier and returns its instance.
         */
        @GET
        @Path("/{transactionNumber: \\d+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getHistoricalTransaction( @PathParam("clientId") Long clientId,
                                                  @PathParam("transactionNumber") Integer transactionNumber,
                                                  @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning given Historical Transaction by executing ClientResource.HistoricalTransactionResource.getHistoricalTransaction(clientId, transactionNumber) method of REST API");

            HistoricalTransaction foundHistoricalTransaction = historicalTransactionFacade.find(new TransactionId(clientId, transactionNumber));
            if(foundHistoricalTransaction == null)
                throw new NotFoundException("Could not find historical transaction for id (" + clientId + "," + transactionNumber + ").");

            // adding hypermedia links to historical transaction resource
            pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(foundHistoricalTransaction, params.getUriInfo());

            return Response.status(Status.OK).entity(foundHistoricalTransaction).build();
        }

        /**
         * Method matches specific Historical Transaction resource by composite identifier and returns its instance fetching it eagerly
         */
        @GET
        @Path("/{transactionNumber: \\d+}/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getHistoricalTransactionEagerly( @PathParam("clientId") Long clientId,
                                                         @PathParam("transactionNumber") Integer transactionNumber,
                                                         @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning given Historical Transaction eagerly by executing ClientResource.HistoricalTransactionResource.getHistoricalTransactionEagerly(clientId, transactionNumber) method of REST API");

            HistoricalTransaction foundHistoricalTransaction = historicalTransactionFacade.findByIdEagerly(new TransactionId(clientId, transactionNumber));
            if(foundHistoricalTransaction == null)
                throw new NotFoundException("Could not find historical transaction for id (" + clientId + "," + transactionNumber + ").");

            // wrapping HistoricalTransaction into HistoricalTransactionWrapper in order to marshall eagerly fetched associated collection of entities
            HistoricalTransactionWrapper wrappedHistoricalTransaction = new HistoricalTransactionWrapper(foundHistoricalTransaction);

            // adding hypermedia links to wrapped historical transaction resource
            pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(wrappedHistoricalTransaction, params.getUriInfo());

            return Response.status(Status.OK).entity(wrappedHistoricalTransaction).build();
        }

        /**
         * Method that takes Historical Transaction as XML or JSON and creates its new instance in database
         */
        @POST
        @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response createHistoricalTransaction( @PathParam("clientId") Long clientId,
                                                     HistoricalTransaction historicalTransaction,
                                                     @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "creating new Historical Transaction by executing ClientResource.HistoricalTransactionResource.createHistoricalTransaction(historicalTransaction) method of REST API");

            HistoricalTransaction createdHistoricalTransaction = null;
            URI locationURI = null;

            try {
                // persist new resource in database
                createdHistoricalTransaction = historicalTransactionFacade.createForClient(clientId, historicalTransaction);

                // populate created resource with hypermedia links
                pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(createdHistoricalTransaction, params.getUriInfo());

                // construct link to newly created resource to return in HTTP Header
                String txClientId = String.valueOf(createdHistoricalTransaction.getClient().getClientId());
                String transactionNumber = String.valueOf(createdHistoricalTransaction.getTransactionNumber());

                Method historicalTransactionsMethod = ClientResource.class.getMethod("getHistoricalTransactionResource");
                locationURI = params.getUriInfo().getBaseUriBuilder()
                        .path(ClientResource.class)
                        .path(historicalTransactionsMethod)
                        .path(transactionNumber)
                        .resolveTemplate("clientId", txClientId)
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

            return Response.created(locationURI).entity(createdHistoricalTransaction).build();
        }

        /**
         * Method that takes updated Historical Transaction as XML or JSON and its composite ID as path params.
         *  It updates Historical Transaction in database for provided composite ID.
         */
        @PUT
        @Path("/{transactionNumber : \\d+}")
        @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response updateHistoricalTransaction( @PathParam("clientId") Long clientId,
                                                     @PathParam("transactionNumber")  Integer transactionNumber,
                                                     HistoricalTransaction historicalTransaction,
                                                     @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "updating existing Historical Transaction by executing ClientResource.HistoricalTransactionResource.updateHistoricalTransaction(historicalTransaction) method of REST API");

            // create composite ID based on path params
            TransactionId transactionId = new TransactionId(clientId, transactionNumber);

            HistoricalTransaction updatedHistoricalTransaction = null;
            try {
                // reflect updated resource object in database
                updatedHistoricalTransaction = historicalTransactionFacade.update(transactionId, historicalTransaction);
                // populate created resource with hypermedia links
                pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(updatedHistoricalTransaction, params.getUriInfo());

            } catch (EJBTransactionRolledbackException ex) {
                ExceptionHandler.handleEJBTransactionRolledbackException(ex);
            } catch (EJBException ex) {
                ExceptionHandler.handleEJBException(ex);
            } catch (Exception ex) {
                throw new InternalServerErrorException(ExceptionHandler.ENTITY_UPDATE_ERROR_MESSAGE);
            }

            return Response.status(Status.OK).entity(updatedHistoricalTransaction).build();
        }

        /**
         * Method that removes subset of Historical Transaction entities for given Client from database.
         * The client id is passed through path param.
         */
        @DELETE
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response removeClientHistoricalTransactions(@PathParam("clientId") Long clientId,
                                                           @BeanParam GenericBeanParam params) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "removing subset of Historical Transaction entities for given Client by executing ClientResource.HistoricalTransactionResource.removeClientHistoricalTransactions(clientId) method of REST API");

            // find client entity for which to remove historical transactions
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            utx.begin();

            // remove associations between historical transactions and employees for given client from database
            historicalTransactionEmployeeRelationshipManager.removeEmployeesFromHistoricalTransactionsByClient(client.getClientId());

            // remove historical transactions for given client from database
            Integer noOfDeleted = historicalTransactionFacade.deleteByClient(client);

            utx.commit();

            // create response returning number of deleted entities
            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfDeleted), 200, "number of deleted historical transactions for client with id " + clientId);

            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method that removes Historical Transaction entity from database for given ID.
         * The historical transaction composite id is passed through path param.
         */
        @DELETE
        @Path("/{transactionNumber : \\d+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response removeHistoricalTransaction(@PathParam("clientId") Long clientId,
                                                    @PathParam("transactionNumber") Integer transactionNumber,
                                                    @BeanParam GenericBeanParam params) throws ForbiddenException, NotFoundException, InternalServerErrorException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "removing given Historical Transaction by executing ClientResource.HistoricalTransactionResource.removeHistoricalTransaction(clientId, transactionNumber) method of REST API");

            utx.begin();

            // remove associations between historical transaction and employees from database
            historicalTransactionEmployeeRelationshipManager.removeAllEmployeesFromHistoricalTransaction(new TransactionId(clientId, transactionNumber));

            // remove historical transaction entity from database
            Integer noOfDeleted = historicalTransactionFacade.deleteById(new TransactionId(clientId, transactionNumber));

            utx.commit();

            if (noOfDeleted == 0)
                throw new NotFoundException("Could not find historical transaction to delete for id (" + clientId + "," + transactionNumber + ").");
            else if (noOfDeleted != 1)
                throw new InternalServerErrorException("Some error occurred while trying to delete historical transaction with id (" + clientId + "," + transactionNumber + ").");

            return Response.status(Status.NO_CONTENT).build();
        }

        /**
         * Additional methods returning subset of resources based on given criteria
         * you can also achieve similar results by applying @QueryParams to generic method
         * returning all resources in order to filter and limit them
         */

        /**
         * Method that counts Historical Transaction entities for given Client resource
         * The client id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countHistoricalTransactionsByClient(@PathParam("clientId") Long clientId,
                                                            @BeanParam GenericBeanParam params) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of historical transactions for given client by executing ClientResource.HistoricalTransactionResource.countHistoricalTransactionsByClient(clientId) method of REST API");

            // find client entity for which to count historical transactions
            Client client = clientFacade.find(clientId);
            if (client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(historicalTransactionFacade.countByClient(client)), 200, "number of historical transactions for client with id " + client.getClientId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method returns subset of Historical Transaction entities for given Client entity
         * and transaction time. The client id is passed through path param.
         * Transaction time range (start and end dates) is passed through query params.
         */
        @GET
        @Path("/by-transaction-time")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientHistoricalTransactionsByTransactionTime( @PathParam("clientId") Long clientId,
                                                                          @BeanParam DateRangeBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {
            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning historical transactions for given client and transaction time (startDate, endDate) using " +
                    "ClientResource.HistoricalTransactionResource.getClientHistoricalTransactionsByTransactionTime(clientId, transactionTime) method of REST API");

            RESTToolkit.validateDateRange(params); // i.e. startDate and endDate

            // find client entity for which to get associated historical transactions
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // find historical transactions by given criteria (client, transaction time)
            ResourceList<HistoricalTransaction> historicalTransactions = new ResourceList<>(
                    historicalTransactionFacade.findByClientAndTransactionTime(client, params.getStartDate(), params.getEndDate(),
                            params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(historicalTransactions).build();
        }

        /**
         * Method returns subset of Historical Transaction entities for given Client entity
         * and booked time. The client id is passed through path param.
         * Booked time range (start and end dates) is passed through query params.
         */
        @GET
        @Path("/by-booked-time")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientHistoricalTransactionsByBookedTime( @PathParam("clientId") Long clientId,
                                                                     @BeanParam DateRangeBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning historical transactions for given client and booked time (startDate, endDate) using " +
                    "ClientResource.HistoricalTransactionResource.getClientHistoricalTransactionsByBookedTime(clientId, bookedTime) method of REST API");

            RESTToolkit.validateDateRange(params); // i.e. startDate and endDate

            // find client entity for which to get associated historical transactions
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // find historical transactions by given criteria (client, booked time)
            ResourceList<HistoricalTransaction> historicalTransactions = new ResourceList<>(
                    historicalTransactionFacade.findByClientAndBookedTime(client, params.getStartDate(), params.getEndDate(),
                            params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(historicalTransactions).build();
        }

        /**
         * Method returns subset of Historical Transaction entities for given Client entity
         * that have been already paid. The client id is passed through path param.
         */
        @GET
        @Path("/paid")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientHistoricalTransactionsPaid( @PathParam("clientId") Long clientId,
                                                             @BeanParam PaginationBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning paid historical transactions for given client using " +
                    "ClientResource.HistoricalTransactionResource.getClientHistoricalTransactionsPaid(clientId) method of REST API");

            // find client entity for which to get associated historical transactions
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // find historical transactions by given criteria (client, paid)
            ResourceList<HistoricalTransaction> historicalTransactions = new ResourceList<>(
                    historicalTransactionFacade.findByClientOnlyPaid(client, params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(historicalTransactions).build();
        }

        /**
         * Method returns subset of Historical Transaction entities for given Client entity
         * that haven't been paid yet. The client id is passed through path param.
         */
        @GET
        @Path("/unpaid")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientHistoricalTransactionsUnpaid( @PathParam("clientId") Long clientId,
                                                               @BeanParam PaginationBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning unpaid historical transactions for given client using " +
                    "ClientResource.HistoricalTransactionResource.getClientHistoricalTransactionsUnpaid(clientId) method of REST API");

            // find client entity for which to get associated historical transactions
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // find historical transactions by given criteria (client, unpaid)
            ResourceList<HistoricalTransaction> historicalTransactions = new ResourceList<>(
                    historicalTransactionFacade.findByClientOnlyUnpaid(client, params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(historicalTransactions).build();
        }

        /**
         * Method returns subset of Historical Transaction entities for given Client entity
         * and price range (and optionally currency code).
         * The client id is passed through path param.
         * The price range (and optionally currency code) are passed through query params.
         */
        @GET
        @Path("/by-price")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientHistoricalTransactionsByPrice( @PathParam("clientId") Long clientId,
                                                                @BeanParam PriceRangeBeanParam params,
                                                                @QueryParam("currency") CurrencyCode currencyCode ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning historical transactions for given client, price range (minPrice, maxPrice) and optionally currency code using " +
                    "ClientResource.HistoricalTransactionResource.getClientHistoricalTransactionsByPrice(clientId, priceRange, currencyCode) method of REST API");

            RESTToolkit.validatePriceRange(params); // i.e. minPrice and maxPrice

            // find client entity for which to get associated historical transactions
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            ResourceList<HistoricalTransaction> historicalTransactions = null;

            if(currencyCode != null) {
                // find historical transactions by given criteria (client, price, currency code)
                historicalTransactions = new ResourceList<>(
                        historicalTransactionFacade.findByClientAndPriceRangeAndCurrencyCode(client, params.getMinPrice(), params.getMaxPrice(),
                                currencyCode, params.getOffset(), params.getLimit())
                );
            } else {
                // find historical transactions by given criteria (client, price)
                historicalTransactions = new ResourceList<>(
                        historicalTransactionFacade.findByClientAndPriceRange(client, params.getMinPrice(), params.getMaxPrice(),
                                params.getOffset(), params.getLimit())
                );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(historicalTransactions).build();
        }

        /**
         * Method returns subset of Historical Transaction entities for given Client entity
         * and currency code. The client id is passed through path param.
         * The currency code is also passed through path param.
         */
        @GET
        @Path("/by-currency/{currencyCode : \\S+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientHistoricalTransactionsByCurrency( @PathParam("clientId") Long clientId,
                                                                   @PathParam("currencyCode") CurrencyCode currencyCode,
                                                                   @BeanParam PaginationBeanParam params ) throws ForbiddenException, NotFoundException {
            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning historical transactions for given client and currency code using " +
                    "ClientResource.HistoricalTransactionResource.getClientHistoricalTransactionsByCurrency(clientId, currencyCode) method of REST API");

            // find client entity for which to get associated historical transactions
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // find historical transactions by given criteria (client, currency code)
            ResourceList<HistoricalTransaction> historicalTransactions = new ResourceList<>(
                    historicalTransactionFacade.findByClientAndCurrencyCode(client, currencyCode, params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(historicalTransactions).build();
        }

        /**
         * Method returns subset of Historical Transaction entities for given Client entity
         * and completion status. The client id is passed through path param.
         * The completion status is also passed through path param.
         */
        @GET
        @Path("/by-completion-status/{completionStatus : \\S+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientHistoricalTransactionsByCompletionStatus( @PathParam("clientId") Long clientId,
                                                                           @PathParam("completionStatus") TransactionCompletionStatus completionStatus,
                                                                           @BeanParam PaginationBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning historical transactions for given client and completion status using " +
                    "ClientResource.HistoricalTransactionResource.getClientHistoricalTransactionsByCompletionStatus(clientId, completionStatus) method of REST API");

            // find client entity for which to get associated historical transactions
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // find historical transactions by given criteria (client, completion status)
            ResourceList<HistoricalTransaction> historicalTransactions = new ResourceList<>(
                    historicalTransactionFacade.findByClientAndCompletionStatus(client, completionStatus, params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(historicalTransactions).build();
        }

        /**
         * Method returns subset of Historical Transaction entities for given Client entity
         * and client rating range (minRating, maxRating). The client id is passed through path param.
         * The client rating range is passed through query params.
         */
        @GET
        @Path("/by-client-rating")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientHistoricalTransactionsByClientRating( @PathParam("clientId") Long clientId,
                                                                       @BeanParam RatingBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning historical transactions for given client and client rating range (minRating, maxRating) using " +
                    "ClientResource.HistoricalTransactionResource.getClientHistoricalTransactionsByClientRating(clientId, clientRatingRange) method of REST API");

            if(params.getExactRating() != null) {
                params.setMinRating(params.getExactRating());
                params.setMaxRating(params.getExactRating());
            }

            // check client rating params correctness
            if(params.getMinRating() == null || params.getMaxRating() == null)
                throw new BadRequestException("Min rating and max rating (optionally exact rating) cannot be null.");

            if(params.getMaxRating() < params.getMinRating())
                throw new BadRequestException("Max rating cannot be less than min rating.");

            // find client entity for which to get associated historical transactions
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // find historical transactions by given criteria (client, client rating)
            ResourceList<HistoricalTransaction> historicalTransactions = new ResourceList<>(
                    historicalTransactionFacade.findByClientAndClientRatingRange(client, params.getMinRating(), params.getMaxRating(),
                            params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(historicalTransactions).build();
        }

        /**
         * Method returns subset of Historical Transaction entities for given Client entity
         * and client comment. The client id is passed through path param.
         * The client comment is also passed through path param.
         */
        @GET
        @Path("/by-client-comment/{clientComment : \\S+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientHistoricalTransactionsByClientComment( @PathParam("clientId") Long clientId,
                                                                        @PathParam("clientComment") String clientComment,
                                                                        @BeanParam PaginationBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning historical transactions for given client and client comment using " +
                    "ClientResource.HistoricalTransactionResource.getClientHistoricalTransactionsByClientComment(clientId, clientComment) method of REST API");

            // find client entity for which to get associated historical transactions
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // find historical transactions by given criteria (client, client comment)
            ResourceList<HistoricalTransaction> historicalTransactions = new ResourceList<>(
                    historicalTransactionFacade.findByClientAndClientComment(client, clientComment, params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(historicalTransactions).build();
        }

        /**
         * Method returns subset of Historical Transaction entities for given Client entity
         * and provider rating range (minRating, maxRating). The client id is passed through path param.
         * The provider rating range is passed through query params.
         */
        @GET
        @Path("/by-provider-rating")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientHistoricalTransactionsByProviderRating( @PathParam("clientId") Long clientId,
                                                                         @BeanParam RatingBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning historical transactions for given client and provider rating range (minRating, maxRating) using " +
                    "ClientResource.HistoricalTransactionResource.getClientHistoricalTransactionsByProviderRating(clientId, providerRatingRange) method of REST API");

            if(params.getExactRating() != null) {
                params.setMinRating(params.getExactRating());
                params.setMaxRating(params.getExactRating());
            }

            // check provider rating params correctness
            if(params.getMinRating() == null || params.getMaxRating() == null)
                throw new BadRequestException("Min rating and max rating (optionally exact rating) cannot be null.");

            if(params.getMaxRating() < params.getMinRating())
                throw new BadRequestException("Max rating cannot be less than min rating.");

            // find client entity for which to get associated historical transactions
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // find historical transactions by given criteria (client, provider rating)
            ResourceList<HistoricalTransaction> historicalTransactions = new ResourceList<>(
                    historicalTransactionFacade.findByClientAndProviderRatingRange(client, params.getMinRating(), params.getMaxRating(),
                            params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(historicalTransactions).build();
        }

        /**
         * Method returns subset of Historical Transaction entities for given Client entity
         * and provider dementi. The client id is passed through path param.
         * The provider dementi is also passed through path param.
         */
        @GET
        @Path("/by-provider-dementi/{providerDementi : \\S+}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getClientHistoricalTransactionsByProviderDementi( @PathParam("clientId") Long clientId,
                                                                          @PathParam("providerDementi") String providerDementi,
                                                                          @BeanParam PaginationBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning historical transactions for given client and provider dementi using " +
                    "ClientResource.HistoricalTransactionResource.getClientHistoricalTransactionsByProviderDementi(clientId, providerDementi) method of REST API");

            // find client entity for which to get associated historical transactions
            Client client = clientFacade.find(clientId);
            if(client == null)
                throw new NotFoundException("Could not find client for id " + clientId + ".");

            // find historical transactions by given criteria (client, provider dementi)
            ResourceList<HistoricalTransaction> historicalTransactions = new ResourceList<>(
                   historicalTransactionFacade.findByClientAndProviderDementi(client, providerDementi, params.getOffset(), params.getLimit())
            );

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(historicalTransactions).build();
        }
    }
}