package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.*;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.*;
import pl.salonea.enums.ClientType;
import pl.salonea.enums.Gender;
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
import pl.salonea.jaxrs.wrappers.ClientWrapper;
import pl.salonea.jaxrs.wrappers.EmployeeWrapper;
import pl.salonea.jaxrs.wrappers.ProviderWrapper;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.print.attribute.standard.Media;
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
    public Response getClientsBornBetween( @BeanParam DateBetweenBeanParam params ) throws ForbiddenException, BadRequestException {

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
            Method bornBetweenMethod = ClientResource.class.getMethod("getClientsBornBetween", DateBetweenBeanParam.class);
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

        //clientWrapper.getCreditCards()

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

            // provider-ratings
            Method providerRatingsMethod = ClientResource.class.getMethod("getProviderRatingResource");
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(providerRatingsMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("provider-ratings").build());

            // rated-providers
            Method providersMethod = ClientResource.class.getMethod("getProviderResource");
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(providersMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("rated-providers").build());

            // rated-providers eagerly
            Method providersEagerlyMethod = ProviderResource.class.getMethod("getClientRatedProvidersEagerly", Long.class, ProviderBeanParam.class);
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
            Method employeesEagerlyMethod = EmployeeResource.class.getMethod("getClientRatedEmployeesEagerly", Long.class, EmployeeBeanParam.class);
            client.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(employeesMethod)
                    .path(employeesEagerlyMethod)
                    .resolveTemplate("clientId", client.getClientId().toString())
                    .build())
                    .rel("rated-employees-eagerly").build());

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
                                                 @BeanParam EmployeeBeanParam params ) throws ForbiddenException, NotFoundException {

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

                // get employees for given client filtered by given params
                employees = new ResourceList<>(
                        employeeFacade.findByMultipleCriteria(params.getDescription(), params.getJobPositions(), params.getSkills(), params.getEducations(),
                                params.getServices(), params.getProviderServices(), params.getServicePoints(), params.getWorkStations(), params.getPeriod(),
                                params.getStrictTerm(), params.getRated(), params.getMinAvgRating(), params.getMaxAvgRating(), clients,
                                params.getOffset(), params.getLimit())
                );

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
                                                        @BeanParam EmployeeBeanParam params ) throws ForbiddenException, NotFoundException {

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

                // get employees eagerly for given client filtered by given params
                employees = new ResourceList<>(
                        EmployeeWrapper.wrap(
                                employeeFacade.findByMultipleCriteriaEagerly(params.getDescription(), params.getJobPositions(), params.getSkills(),
                                        params.getEducations(), params.getServices(), params.getProviderServices(), params.getServicePoints(),
                                        params.getWorkStations(), params.getPeriod(), params.getStrictTerm(), params.getRated(), params.getMinAvgRating(),
                                        params.getMaxAvgRating(), clients, params.getOffset(), params.getLimit())
                        )
                );

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



            return null;
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

            return null;
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

    }
}