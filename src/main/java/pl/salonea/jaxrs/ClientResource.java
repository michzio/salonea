package pl.salonea.jaxrs;


import pl.salonea.ejb.stateless.ClientFacade;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.Client;
import pl.salonea.jaxrs.bean_params.ClientBeanParam;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.exceptions.ExceptionHandler;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.exceptions.UnprocessableEntityException;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.ClientWrapper;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashSet;
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
     * related subresources (through relationships)
     */

    @Path("/{clientId: \\d+}/provider-ratings")
    public ProviderRatingResource getProviderRatingResource() {
        return new ProviderRatingResource();
    }

    public class ProviderRatingResource {

        public ProviderRatingResource() { }

        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countProviderRatingsByClient( @PathParam("clientId") Long clientId,
                                                      @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException  {

            if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");


            return null;

        }
    }

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


        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }


    }

}
