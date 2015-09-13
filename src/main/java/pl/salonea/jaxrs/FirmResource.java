package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.FirmFacade;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.Firm;
import pl.salonea.jaxrs.bean_params.AddressBeanParam;
import pl.salonea.jaxrs.bean_params.FirmBeanParam;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.bean_params.PaginationBeanParam;
import pl.salonea.jaxrs.exceptions.*;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.exceptions.BadRequestException;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;

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
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by michzio on 09/09/2015.
 */
@Path("/firms")
public class FirmResource {

    private static final Logger logger = Logger.getLogger(FirmResource.class.getName());

    @Inject
    private FirmFacade firmFacade;

    /**
     * Method returns all Firm resources
     * They can be additionally filtered or paginated by @QueryParams
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getFirms( @BeanParam FirmBeanParam params) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning all Firms by executing FirmResource.getFirms() method of REST API");

        // calculate number of filter query params
        Integer noOfParams = params.getUriInfo().getQueryParameters().size();
        if(params.getOffset() != null) noOfParams -= 1;
        if(params.getLimit() != null) noOfParams -= 1;

        ResourceList<Firm> firms = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            // get firms filtered by criteria provided in query params
            firms = new ResourceList<>(
                    firmFacade.findByMultipleCriteria( params.getName(), params.getVatin(), params.getCompanyNumber(),
                            params.getStatisticNumber(), params.getPhoneNumber(), params.getSkypeName(),
                            new Address(params.getStreet(), params.getHouseNumber(), params.getFlatNumber(), params.getZipCode(),
                                    params.getCity(), params.getState(), params.getCountry()),
                            params.getOffset(), params.getLimit() )
            );

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all firms without filtering (eventually paginated)
            firms = new ResourceList<>(firmFacade.findAll(params.getOffset(), params.getLimit()));
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(firms, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(firms).build();
    }

    /**
     * Method matches specific Firm resource by identifier and returns its instance.
     */
    @GET
    @Path("/{userId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getFirm( @PathParam("userId") Long userId,
                             @BeanParam GenericBeanParam params) throws NotFoundException, ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning given Firm by executing FirmResource.getFirm(userId) method of REST API");

        Firm foundFirm = firmFacade.find(userId);
        if(foundFirm == null)
            throw new NotFoundException("Could not find firm for id " + userId + ".");

        // adding hypermedia links to firm resource
        populateWithHATEOASLinks(foundFirm, params.getUriInfo());

        return Response.status(Status.OK).entity(foundFirm).build();
    }

    /**
     * Method that takes Firm as XML or JSON and creates its new instance in database
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createFirm( Firm firm,
                                @BeanParam GenericBeanParam params) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "creating new Firm by executing FirmResource.createFirm(firm) method of REST API");

        if(firm.getRegistrationDate() == null) {
            // if registration date of newly created firm hasn't been set by Client set it now to the current datetime value
            firm.setRegistrationDate(new Date());
        }

        Firm createdFirm = null;
        URI locationURI = null;

        try {
            // persist new resource in database
            createdFirm = firmFacade.create(firm);

            // populate created resource with hypermedia links
            populateWithHATEOASLinks(createdFirm, params.getUriInfo());

            // construct link to newly created resource to return in HTTP Header
            String createdFirmId = String.valueOf(createdFirm.getUserId());
            locationURI = params.getUriInfo().getBaseUriBuilder().path(FirmResource.class).path(createdFirmId).build();

        } catch(EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch(EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_CREATION_ERROR_MESSAGE);
        }

        return Response.created(locationURI).entity(createdFirm).build();
    }

    /**
     * Method that takes updated Firm as XML or JSON and its ID as path param.
     * It updates Firm in database for provided ID.
     */
    @PUT
    @Path("/{userId : \\d+}") // catch only numeric identifiers
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateFirm( @PathParam("userId") Long userId,
                                Firm firm,
                                @BeanParam GenericBeanParam params) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "updating existing Firm by executing FirmResource.updateFirm(firm) method of REST API");

        // set resource ID passed in path param on updated resource object
        firm.setUserId(userId);

        Firm updatedFirm = null;
        try {
            // reflect updated resource object in database
            updatedFirm = firmFacade.update(firm);
            // populate created resource with hypermedia links
            populateWithHATEOASLinks(updatedFirm, params.getUriInfo());

        } catch(EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch(EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch(Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_UPDATE_ERROR_MESSAGE);
        }

        return Response.status(Status.OK).entity(updatedFirm).build();
    }

    /**
     * Method that removes Firm entity from database for given ID.
     * The ID is passed through path param.
     */
    @DELETE
    @Path("/{userId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeFirm( @PathParam("userId") Long userId,
                                @HeaderParam("authToken") String authToken) throws ForbiddenException, NotFoundException {

        if(authToken == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "removing given Firm by executing FirmResource.removeFirm(userId) method of REST API");

        // find Firm entity that should be deleted
        Firm toDeleteFirm = firmFacade.find(userId);
        // throw exception if entity hasn't been found
        if(toDeleteFirm == null)
            throw new NotFoundException("Could not find firm to delete for given id: " + userId + ".");

        // remove entity form database
        firmFacade.remove(toDeleteFirm);

        return Response.status(Status.NO_CONTENT).build();
    }

    /**
     * Additional methods returning a subset of resources based on given criteria
     * you can also achieve similar results by applying @QueryParams to generic method
     * returning all resources in order to filter and limit them
     */

    /**
     * Method returns number of Firm entities in database
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countFirms( @HeaderParam("authToken") String authToken ) throws ForbiddenException {

        if(authToken == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning number of firms by executing FirmResource.countFirms() method of REST API");

        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(firmFacade.count()), 200, "number of firms");
        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method returns subset of Firm entities for provided firm name
     */
    @GET
    @Path("/named/{firmName : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getFirmsByName( @PathParam("firmName") String firmName,
                                    @BeanParam PaginationBeanParam params) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning firms for given firm name using FirmResource.getFirmsByName(firmName) method of REST API");

        // find firms by given criteria
        ResourceList<Firm> firms = new ResourceList<>( firmFacade.findByName(firmName, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(firms, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(firms).build();
    }

    /**
     * Method returns Firm entity for provided VAT identification number
     */
    @GET
    @Path("/vatin/{vatin : \\w{10}}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getFirmByVATIN( @PathParam("vatin") String vatin,
                                    @BeanParam GenericBeanParam params) throws NotFoundException, ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning Firm entity for given VATIN using FirmResource.getFirmByVATIN(vatin) method of REST API");

        // find firm for given VATIN
        Firm foundFirm = firmFacade.findByVATIN(vatin);
        if(foundFirm == null)
            throw new NotFoundException("Could not find Firm entity for given VAT identification number: " + vatin + ".");

        // adding hypermedia links to firm resource
        populateWithHATEOASLinks(foundFirm, params.getUriInfo());

        return Response.status(Status.OK).entity(foundFirm).build();
    }

    /**
     * Method returns Firm entity for provided company number
     */
    @GET
    @Path("/company-number/{companyNumber : \\w{10}}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getFirmByCompanyNumber( @PathParam("companyNumber") String companyNumber,
                                            @BeanParam GenericBeanParam params) throws NotFoundException, ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning Firm entity for given company number using FirmResource.getFirmByCompanyNumber(companyNumber) method of REST API");

        // find firm for given company number
        Firm foundFirm = firmFacade.findByCompanyNumber(companyNumber);
        if(foundFirm == null)
            throw new NotFoundException("Could not find Firm entity for given company number: " + companyNumber + ".");

        // adding hypermedia links to firm resource
        populateWithHATEOASLinks(foundFirm, params.getUriInfo());

        return Response.status(Status.OK).entity(foundFirm).build();
    }

    /**
     * Method returns subset of Firm entities for given location query params (company address)
     */
    @GET
    @Path("/located")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getFirmsByAddress( @BeanParam AddressBeanParam params ) throws ForbiddenException, BadRequestException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning firms for given location query params using FirmResource.getFirmsByLocation() method of REST API");

        // check correctness of query params
        Integer noOfParams = params.getUriInfo().getQueryParameters().size();
        if(params.getOffset() != null) noOfParams -= 1;
        if(params.getLimit() != null) noOfParams -= 1;
        if(noOfParams < 1)
            throw new BadRequestException("There is no location related query param in request.");

        // find firms by given criteria
        ResourceList<Firm> firms = new ResourceList<>( firmFacade.findByAddress(params.getCity(), params.getState(),
                params.getCountry(), params.getStreet(), params.getZipCode(), params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(firms, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(firms).build();
    }

    /**
     * Method removes Firm entity for given VAT identification number
     */
    @DELETE
    @Path("/vatin/{vatin : \\w{10}}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeFirmByVATIN( @PathParam("vatin") String vatin,
                                       @BeanParam GenericBeanParam params) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "removing firm for given vat identification number using FirmResource.removeFirmByVATIN() method of REST API");

        // remove firm entity that has specified vat identification number
        Boolean isFirmDeleted = firmFacade.deleteWithVATIN(vatin);

        // create response returning deletion status
        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(isFirmDeleted), 200, "firm deletion status");

        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method removes Firm entity for given company number
     */
    @DELETE
    @Path("/company-number/{companyNumber : \\w{10}}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeFirmByCompanyNumber( @PathParam("companyNumber") String companyNumber,
                                               @BeanParam GenericBeanParam params) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "removing firm for given company number using FirmResource.removeFirmByCompanyNumber() method of REST API");

        // remove firm entity that has specified company number
        Boolean isFirmDeleted = firmFacade.deleteWithCompanyNumber(companyNumber);

        // create response returning deletion status
        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(isFirmDeleted), 200, "firm deletion status");

        return Response.status(Status.OK).entity(responseEntity).build();
    }


    // private helper methods e.g. to populate resources/resource lists with HATEOAS links

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    private void populateWithHATEOASLinks(ResourceList<Firm> firms, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        if(offset != null && limit != null) {
            // self collection link
            firms.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", offset).queryParam("limit", limit).build()).rel("self").build() );
            // prev collection link
            Integer prevOffset = (offset - limit) < 0 ? 0 : offset - limit;
            Integer prevLimit = offset - prevOffset;
            if(prevLimit > 0)
                firms.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", prevOffset).queryParam("limit", prevLimit).build()).rel("prev").build() );
            else
                firms.getLinks().add( Link.fromUri("").rel("prev").build() );
            // next collection link
            firms.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", (offset+limit)).queryParam("limit", limit).build()).rel("next").build() );
        } else {
            firms.getLinks().add( Link.fromUri(uriInfo.getAbsolutePath()).rel("self").build() );
        }

        try {

            // count resources hypermedia link
            Method countMethod = FirmResource.class.getMethod("countFirms", String.class);
            firms.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(FirmResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            firms.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(FirmResource.class).build()).rel("firms").build());

            // get subset of resources hypermedia links
            // named
            firms.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(FirmResource.class).path("named").build()).rel("named").build() );

            // located
            Method locatedMethod = FirmResource.class.getMethod("getFirmsByAddress", AddressBeanParam.class);
            firms.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(FirmResource.class).path(locatedMethod).build()).rel("located").build() );

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for(Firm firm : firms.getResources()) {
            populateWithHATEOASLinks(firm, uriInfo);
        }
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    private void populateWithHATEOASLinks(Firm firm, UriInfo uriInfo) {

        // self link with pattern: http://localhost:port/app/rest/resources/{id}
        firm.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                .path(FirmResource.class)
                                                .path(firm.getUserId().toString())
                                                .build())
                                .rel("self").build() );

        // collection link with pattern: http://localhost:port/app/rest/resources
        firm.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                .path(FirmResource.class)
                                                .build())
                                .rel("firms").build() );

        try {
            // vatin link with pattern: http://localhost:port/app/rest/resources/vatin/{vatin}
            Method vatinMethod = FirmResource.class.getMethod("getFirmByVATIN", String.class, GenericBeanParam.class);
            firm.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                     .path(FirmResource.class)
                                                     .path(vatinMethod)
                                                     .resolveTemplate("vatin", firm.getVatin())
                                                     .build())
                                    .rel("vatin").build() );

            // company-number link with pattern: http://localhost:port/app/rest/resources/company-number/{companyNumber}
            Method companyNumberMethod = FirmResource.class.getMethod("getFirmByCompanyNumber", String.class, GenericBeanParam.class);
            firm.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                    .path(FirmResource.class)
                                                    .path(companyNumberMethod)
                                                    .resolveTemplate("companyNumber", firm.getCompanyNumber())
                                                    .build())
                                    .rel("company-number").build() );

        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}