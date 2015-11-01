package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.NaturalPersonFacade;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.NaturalPerson;
import pl.salonea.enums.Gender;
import pl.salonea.jaxrs.bean_params.*;
import pl.salonea.jaxrs.exceptions.*;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.exceptions.BadRequestException;
import pl.salonea.jaxrs.utils.RESTDateTime;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.utils.Period;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;

import javax.ws.rs.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

/**
 * Created by michzio on 07/09/2015.
 */
@Path("/natural-persons")
public class NaturalPersonResource {

    private static final Logger logger = Logger.getLogger(NaturalPersonResource.class.getName());

    @Inject
    private NaturalPersonFacade naturalPersonFacade;

    /**
     * Method returns all Natural Person resources
     * They can be additionally filtered or paginated by @QueryParams
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getNaturalPersons(@BeanParam NaturalPersonBeanParam params) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning all Natural Persons by executing NaturalPersonResource.getNaturalPersons() method of REST API");

        // calculate number of filter query params
        Integer noOfParams = params.getUriInfo().getQueryParameters().size();
        if(params.getOffset() != null) noOfParams -= 1;
        if(params.getLimit() != null) noOfParams -= 1;

        ResourceList<NaturalPerson> naturalPersons = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            // get natural persons filtered by criteria provided in query params
            naturalPersons = new ResourceList<>(
                naturalPersonFacade.findByMultipleCriteria(params.getFirstName(), params.getLastName(), params.getGender(),
                        new Period(params.getOldestBirthDate(), params.getYoungestBirthDate()), params.getOldestAge(), params.getYoungestAge(),
                        new Address(params.getHomeStreet(), params.getHomeHouseNumber(), params.getHomeFlatNumber(), params.getHomeZipCode(), params.getHomeCity(), params.getHomeState(), params.getHomeCountry()),
                        new Address(params.getDeliveryStreet(), params.getDeliveryHouseNumber(), params.getDeliveryFlatNumber(), params.getDeliveryZipCode(), params.getDeliveryCity(), params.getDeliveryState(), params.getDeliveryCountry()) )
            );
        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all natural persons without filtering (eventually paginated)
            naturalPersons = new ResourceList<>(naturalPersonFacade.findAll(params.getOffset(), params.getLimit()));
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(naturalPersons, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(naturalPersons).build();
    }

    /**
     * Method matches specific Natural Person resource by identifier and returns its instance.
     */
    @GET
    @Path("/{userId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getNaturalPerson( @PathParam("userId") Long userId,
                                      @BeanParam GenericBeanParam params) throws NotFoundException, ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning given NaturalPerson by executing NaturalPersonResource.getNaturalPerson(userId) method of REST API");

        NaturalPerson foundNaturalPerson = naturalPersonFacade.find(userId);
        if(foundNaturalPerson == null)
            throw new NotFoundException("Could not find natural person for id " + userId + ".");

        // adding hypermedia links to natural person resource
        populateWithHATEOASLinks(foundNaturalPerson, params.getUriInfo());

        return Response.status(Status.OK).entity(foundNaturalPerson).build();
    }

    /**
     * Method that takes Natural Person as XML or JSON and creates its new instance in database
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createNaturalPerson(NaturalPerson naturalPerson,
                                        @BeanParam GenericBeanParam params) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "creating new NaturalPerson by executing NaturalPersonResource.createNaturalPerson(naturalPerson) method of REST API");

        if(naturalPerson.getRegistrationDate() == null) {
            // if registration date of newly created natural person hasn't been set by Client set it now to the current datetime value
            naturalPerson.setRegistrationDate(new Date());
        }

        NaturalPerson createdNaturalPerson = null;
        URI locationURI = null;

        try {
            // persist new resource in database
            createdNaturalPerson = naturalPersonFacade.create(naturalPerson);

            // populate created resource with hypermedia links
            populateWithHATEOASLinks(createdNaturalPerson, params.getUriInfo());

            // construct link to newly created resource to return in HTTP Header
            String createdNaturalPersonId = String.valueOf(createdNaturalPerson.getUserId());
            locationURI = params.getUriInfo().getBaseUriBuilder().path(NaturalPersonResource.class).path(createdNaturalPersonId).build();

        } catch(EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch(EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch(Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_CREATION_ERROR_MESSAGE);
        }

        return  Response.created(locationURI).entity(createdNaturalPerson).build();
    }

    /**
     * Method that takes updated Natural Person as XML or JSON and its ID as path param.
     * It updates Natural Person in database for provided ID.
     */
    @PUT
    @Path("/{userId : \\d+}") // catch only numeric identifiers
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateNaturalPerson( @PathParam("userId") Long userId,
                                         NaturalPerson naturalPerson,
                                         @BeanParam GenericBeanParam params) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "updating existing NaturalPerson by executing NaturalPersonResource.updateNaturalPerson(naturalPerson) method of REST API");

        // set resource ID passed in path param on updated resource object
        naturalPerson.setUserId(userId);

        NaturalPerson updatedNaturalPerson = null;
        try {
            // reflect updated resource object in database
            updatedNaturalPerson = naturalPersonFacade.update(naturalPerson);
            // populate created resource with hypermedia links
            populateWithHATEOASLinks(updatedNaturalPerson, params.getUriInfo());

        } catch(EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch(EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch(Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_UPDATE_ERROR_MESSAGE);
        }

        return Response.status(Status.OK).entity(updatedNaturalPerson).build();
    }

    /**
     * Method that removes Natural Person entity from database for given ID.
     * The ID is passed through path param.
     */
    @DELETE
    @Path("/{userId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeNaturalPerson( @PathParam("userId") Long userId,
                                         @HeaderParam("authToken") String authToken) throws ForbiddenException, NotFoundException {

        if(authToken == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "removing given NaturalPerson by executing NaturalPersonResource.removeNaturalPerson(userId) method of REST API");

        // find Natural Person entity that should be deleted
        NaturalPerson toDeleteNaturalPerson = naturalPersonFacade.find(userId);
        // throw exception if entity hasn't been found
        if(toDeleteNaturalPerson == null)
            throw new NotFoundException("Could not found natural person to delete for given id: " + userId + ".");

        // remove entity from database
        naturalPersonFacade.remove(toDeleteNaturalPerson);

        return Response.status(Status.NO_CONTENT).build();
    }

    /**
     * Additional methods returning a subset of resources based on given criteria
     * you can also achieve similar results by applying @QueryParams to generic method
     * returning all resources in order to filter and limit them
     */

    /**
     * Method returns number of Natural Person entities in database
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countNaturalPersons(@HeaderParam("authToken") String authToken) throws ForbiddenException {

        if(authToken == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning number of natural persons by executing NaturalPersonResource.countNaturalPersons() method of REST API");

        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(naturalPersonFacade.count()), 200, "number of natural persons");
        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method returns subset of Natural Person entities for provided first name
     */
    @GET
    @Path("/first-name/{firstName : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getNaturalPersonsByFirstName( @PathParam("firstName") String firstName,
                                                  @BeanParam PaginationBeanParam params) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning natural persons for given first name using NaturalPersonResource.getNaturalPersonsByFirstName(firstName) method of REST API");

        // find natural persons by given criteria
        ResourceList<NaturalPerson> naturalPersons = new ResourceList<>( naturalPersonFacade.findByFirstName(firstName, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(naturalPersons, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(naturalPersons).build();
    }

    /**
     * Method returns subset of Natural Person entities for provided last name
     */
    @GET
    @Path("/last-name/{lastName : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getNaturalPersonsByLastName( @PathParam("lastName") String lastName,
                                                 @BeanParam PaginationBeanParam params) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning natural persons for given last name using NaturalPersonResource.getNaturalPersonsByLastName(lastName) method of REST API");

        // find natural persons by given criteria
        ResourceList<NaturalPerson> naturalPersons = new ResourceList<>( naturalPersonFacade.findByLastName(lastName, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(naturalPersons, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(naturalPersons).build();
    }

    /**
     * Method returns subset of Natural Person entities for provided first name and last name combination
     */
    @GET
    @Path("/named")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getNaturalPersonsByNames( @BeanParam NamesBeanParam params) throws ForbiddenException, BadRequestException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning natural persons for given first name and last name using NaturalPersonResource.getNaturalPersonsByNames(firstName, lastName) method of REST API");

        // check correctness of query params
        if(params.getFirstName() == null || params.getLastName() == null) {
            throw new BadRequestException("First name or last name query param not specified for request.");
        }

        // find natural persons by given criteria
        ResourceList<NaturalPerson> naturalPersons = new ResourceList<>( naturalPersonFacade.findByNames(params.getFirstName(), params.getLastName(), params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(naturalPersons, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(naturalPersons).build();
    }

    /**
     * Method returns subset of Natural Person entities born after given date
     */
    @GET
    @Path("/born-after/{date}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getNaturalPersonsBornAfter( @PathParam("date") RESTDateTime date,
                                                @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning natural persons born after provided date using NaturalPersonResource.getNaturalPersonsBornAfter(date) method of REST API");

        // find natural persons by given criteria
        ResourceList<NaturalPerson> naturalPersons = new ResourceList<>( naturalPersonFacade.findBornAfter(date, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(naturalPersons, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(naturalPersons).build();
    }

    /**
     * Method returns subset of Natural Person entities born before given date
     */
    @GET
    @Path("/born-before/{date}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getNaturalPersonsBornBefore( @PathParam("date") RESTDateTime date,
                                                 @BeanParam PaginationBeanParam params) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning natural persons born before provided date using NaturalPersonResource.getNaturalPersonsBornBefore(date) method of REST API");

        // find natural persons by given criteria
        ResourceList<NaturalPerson> naturalPersons = new ResourceList<>( naturalPersonFacade.findBornBefore(date, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(naturalPersons, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(naturalPersons).build();
    }

    /**
     * Method returns subset of Natural Person entities born between given dates
     */
    @GET
    @Path("/born-between")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getNaturalPersonsBornBetween( @BeanParam DateBetweenBeanParam params ) throws ForbiddenException, BadRequestException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning natural persons born between given start and end date using NaturalPersonResource.getNaturalPersonsBornBetween() method of REST API");

        // check correctness of query params
        if(params.getStartDate() == null || params.getEndDate() == null) {
            throw new BadRequestException("Start date or end date query param not specified for request.");
        }

        if(params.getStartDate().after(params.getEndDate())) {
            throw new BadRequestException("Start date is after end date.");
        }

        // find natural persons by given criteria
        ResourceList<NaturalPerson> naturalPersons = new ResourceList<>( naturalPersonFacade.findBornBetween(params.getStartDate(),
                params.getEndDate(), params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(naturalPersons, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(naturalPersons).build();
    }

    /**
     * Method returns subset of Natural Person entities older than specified age
     */
    @GET
    @Path("/older-than/{age : \\d{1,3}}") // catch only numeric 0-999 path param
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getNaturalPersonsOlderThan( @PathParam("age") Integer age,
                                                @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning natural persons older than specified age using NaturalPersonResource.getNaturalPersonsOlderThan(age) method of REST API");

        // find natural persons by given criteria
        ResourceList<NaturalPerson> naturalPersons = new ResourceList<>( naturalPersonFacade.findOlderThan(age, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(naturalPersons, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(naturalPersons).build();
    }

    /**
     * Method returns subset of Natural Person entities younger than specified age
     */
    @GET
    @Path("/younger-than/{age : \\d{1,3}}") // catch only numeric 0-999 path param
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getNaturalPersonsYoungerThan( @PathParam("age") Integer age,
                                                  @BeanParam PaginationBeanParam params) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning natural persons younger than specified age using NaturalPersonResource.getNaturalPersonsYoungerThan(age) method of REST API");

        // find natural persons by given criteria
        ResourceList<NaturalPerson> naturalPersons = new ResourceList<>( naturalPersonFacade.findYoungerThan(age, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(naturalPersons, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(naturalPersons).build();
    }

    /**
     * Method returns subset of Natural Person entities between specified age
     */
    @GET
    @Path("/aged-between")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getNaturalPersonsAgedBetween( @BeanParam AgeBetweenBeanParam params ) throws ForbiddenException, BadRequestException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning natural persons aged between specified youngest and oldest age using NaturalPersonResource.getNaturalPersonsAgedBetween() method of REST API");

        // check correctness of query params
        if(params.getYoungestAge() == null || params.getOldestAge() == null) {
            throw new BadRequestException("Youngest or oldest age query param not specified for request.");
        }

        if(params.getYoungestAge() > params.getOldestAge()) {
            throw new BadRequestException("Youngest age is greater than oldest age.");
        }

        // find natural persons by given criteria
        ResourceList<NaturalPerson> naturalPersons = new ResourceList<>( naturalPersonFacade.findBetweenAge(params.getYoungestAge(), params.getOldestAge(), params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(naturalPersons, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(naturalPersons).build();
    }

    /**
     * Method returns subset of Natural Person entities for given location query params (home address)
     */
    @GET
    @Path("/located")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getNaturalPersonsByLocation( @BeanParam AddressBeanParam params ) throws ForbiddenException, BadRequestException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning natural persons for given location query params using NaturalPersonResource.getNaturalPersonsByLocation() method of REST API");

        // check correctness of query params
        Integer noOfParams = params.getUriInfo().getQueryParameters().size();
        if(params.getOffset() != null) noOfParams -= 1;
        if(params.getLimit() != null) noOfParams -= 1;
        if(noOfParams < 1 )
            throw new BadRequestException("There is no location related query param in request.");

        // find natural persons by given criteria
        ResourceList<NaturalPerson> naturalPersons = new ResourceList<>( naturalPersonFacade.findByLocation(params.getCity(), params.getState(), params.getCountry(),
                params.getStreet(), params.getZipCode(), params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(naturalPersons, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(naturalPersons).build();
    }

    /**
     * Method returns subset of Natural Person entities for given delivery query params (delivery address)
     */
    @GET
    @Path("/delivered")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getNaturalPersonsByDelivery( @BeanParam AddressBeanParam params ) throws ForbiddenException, BadRequestException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning natural persons for given delivery query params using NaturalPersonResource.getNaturalPersonsByDelivery() method of REST API");

        // check correctness of query params
        Integer noOfParams = params.getUriInfo().getQueryParameters().size();
        if(params.getOffset() != null) noOfParams -= 1;
        if(params.getLimit() != null) noOfParams -= 1;
        if(noOfParams < 1 )
            throw new BadRequestException("There is no delivery related query param in request.");

        // find natural persons by given criteria
        ResourceList<NaturalPerson> naturalPersons = new ResourceList<>( naturalPersonFacade.findByDelivery(params.getCity(), params.getState(), params.getCountry(),
                params.getStreet(), params.getZipCode(), params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(naturalPersons, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(naturalPersons).build();
    }

    /**
     * Method returns subset of Natural Person entities for given gender
     */
    @GET
    @Path("/gender/{gender : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getNaturalPersonsByGender( @PathParam("gender") Gender gender,
                                               @BeanParam PaginationBeanParam params) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning natural persons for given gender using NaturalPersonResource.getNaturalPersonsByGender() method of REST API");

        ResourceList<NaturalPerson> naturalPersons = new ResourceList<>( naturalPersonFacade.findByGender(gender, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(naturalPersons, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(naturalPersons).build();
    }

    // private helper methods e.g. to populate resources/resource lists with HATEOAS links

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    private void populateWithHATEOASLinks(ResourceList<NaturalPerson> naturalPersons, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        if(offset != null && limit != null) {
            // self collection link
            naturalPersons.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", offset).queryParam("limit", limit).build()).rel("self").build() );
            // prev collection link
            Integer prevOffset = (offset - limit) < 0 ? 0 : offset - limit;
            Integer prevLimit = offset - prevOffset;
            if(prevLimit > 0)
                naturalPersons.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", prevOffset).queryParam("limit", prevLimit).build()).rel("prev").build() );
            else
                naturalPersons.getLinks().add( Link.fromUri("").rel("prev").build() );
            // next collection link
            naturalPersons.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", (offset+limit)).queryParam("limit", limit).build()).rel("next").build() );
        } else {
            naturalPersons.getLinks().add( Link.fromUri(uriInfo.getAbsolutePath()).rel("self").build() );
        }

        try {
            // count resources hypermedia link
            Method countMethod = NaturalPersonResource.class.getMethod("countNaturalPersons", String.class);
            naturalPersons.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(NaturalPersonResource.class).path(countMethod).build()).rel("count").build());

            // get all resources hypermedia link
            naturalPersons.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(NaturalPersonResource.class).build()).rel("natural-persons").build());

            // get subset of resources hypermedia links
            // first-name
            naturalPersons.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(NaturalPersonResource.class).path("first-name").build()).rel("first-name").build());

            // last-name
            naturalPersons.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(NaturalPersonResource.class).path("last-name").build()).rel("last-name").build());

            // named
            Method namedMethod = NaturalPersonResource.class.getMethod("getNaturalPersonsByNames", NamesBeanParam.class);
            naturalPersons.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(NaturalPersonResource.class).path(namedMethod).build()).rel("named").build());

            // born-after
            naturalPersons.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(NaturalPersonResource.class).path("born-after").build()).rel("born-after").build());

            // born-before
            naturalPersons.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(NaturalPersonResource.class).path("born-before").build()).rel("born-before").build());

            // born-between
            Method bornBetweenMethod = NaturalPersonResource.class.getMethod("getNaturalPersonsBornBetween", DateBetweenBeanParam.class);
            naturalPersons.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(NaturalPersonResource.class).path(bornBetweenMethod).build()).rel("born-between").build());

            // older-than
            naturalPersons.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(NaturalPersonResource.class).path("older-than").build()).rel("older-than").build());

            // younger-than
            naturalPersons.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(NaturalPersonResource.class).path("younger-than").build()).rel("younger-than").build());

            // aged-between
            Method agedBetweenMethod = NaturalPersonResource.class.getMethod("getNaturalPersonsAgedBetween", AgeBetweenBeanParam.class);
            naturalPersons.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(NaturalPersonResource.class).path(agedBetweenMethod).build()).rel("aged-between").build());

            // located
            Method locatedMethod = NaturalPersonResource.class.getMethod("getNaturalPersonsByLocation", AddressBeanParam.class);
            naturalPersons.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(NaturalPersonResource.class).path(locatedMethod).build()).rel("located").build());

            // delivered
            Method deliveredMethod = NaturalPersonResource.class.getMethod("getNaturalPersonsByDelivery", AddressBeanParam.class);
            naturalPersons.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(NaturalPersonResource.class).path(deliveredMethod).build()).rel("delivered").build());

            // gender
            naturalPersons.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(NaturalPersonResource.class).path("gender").build()).rel("gender").build());

        }  catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for(NaturalPerson naturalPerson : naturalPersons.getResources()) {
            populateWithHATEOASLinks(naturalPerson, uriInfo);
        }

    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    private void populateWithHATEOASLinks(NaturalPerson naturalPerson, UriInfo uriInfo) {

        // self link with pattern: http://localhost:port/app/rest/resources/{id}
        naturalPerson.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                          .path(NaturalPersonResource.class)
                                                          .path(naturalPerson.getUserId().toString())
                                                          .build())
                                          .rel("self").build() );

        // collection link with pattern: http://localhost:port/app/rest/resources
        naturalPerson.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                          .path(NaturalPersonResource.class)
                                                          .build())
                                          .rel("natural-persons").build());
    }


}
