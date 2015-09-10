package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.UserAccountFacade;
import pl.salonea.entities.Firm;
import pl.salonea.entities.NaturalPerson;
import pl.salonea.entities.UserAccount;
import pl.salonea.jaxrs.bean_params.*;
import pl.salonea.jaxrs.exceptions.*;
import pl.salonea.jaxrs.exceptions.BadRequestException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.*;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.utils.Period;

/**
 * Created by michzio on 01/09/2015.
 */
@Path("/user-accounts")
public class UserAccountResource {

    private static final Logger logger = Logger.getLogger(UserAccountResource.class.getName());

    @Inject
    private UserAccountFacade userAccountFacade;

    /**
     * Method returns all User Account resources
     * They can be additionally filtered or paginated by @QueryParams
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getUserAccounts(@BeanParam UserAccountBeanParam params) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning all User Accounts by executing UserAccountResource.getUserAccounts() method of REST API");

        // calculate number of filter query params
        Integer noOfParams = params.getUriInfo().getQueryParameters().size();
        if(params.getOffset() != null) noOfParams -= 1;
        if(params.getLimit() != null) noOfParams -= 1;

        ResourceList<UserAccount> userAccounts = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            // get user accounts filtered by criteria provided in query params
            Period createdBetween = new Period(params.getCreatedAfterDate(), params.getCreatedBeforeDate());
            Period lastLoggedBetween = new Period(params.getLastLoggedAfterDate(), params.getLastLoggedBeforeDate());
            Period lastFailedLoginBetween = new Period(params.getLastFailedLoginAfterDate(), params.getLastFailedLoginBeforeDate());
            userAccounts = new ResourceList<>(userAccountFacade.findByMultipleCriteria(params.getLogin(), params.getEmail(), params.getActivated(), createdBetween, lastLoggedBetween, lastFailedLoginBetween, params.getOffset(), params.getLimit()));

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all user accounts without filtering (eventually paginated)
            userAccounts = new ResourceList<>(userAccountFacade.findAll(params.getOffset(), params.getLimit()));
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(userAccounts, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(userAccounts).build();
    }

    /**
     * Method matches specific User Account resource by identifier and returns its instance.
     */
    @GET
    @Path("/{userId : \\d+}")  // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getUserAccount( @PathParam("userId") Long userId,
                                    @BeanParam GenericBeanParam params) throws NotFoundException, ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning given UserAccount by executing UserAccountResource.getUserAccount(userId) method of REST API");

        UserAccount foundUserAccount = userAccountFacade.find(userId);
        if(foundUserAccount == null)
            throw new NotFoundException("Could not find user account for id " + userId + ".");

        // adding hypermedia links to user account resource
        populateWithHATEOASLinks(foundUserAccount, params.getUriInfo());

        return Response.status(Status.OK).entity(foundUserAccount).build();
    }

    /**
     * Method that takes User Account as XML or JSON and creates its new instance in database
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createUserAccount(UserAccount userAccount,
                                      @BeanParam GenericBeanParam params) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "creating new UserAccount by executing UserAccountResource.createUserAccount(userAccount) method of REST API");

        if(userAccount.getRegistrationDate() == null) {
            // if registration date of newly created user hasn't been set by Client set it now to the current datetime value
            userAccount.setRegistrationDate(new Date());
        }

        UserAccount createdUserAccount = null;
        URI locationURI = null;

        try {
            // persist new resource in database
            createdUserAccount = userAccountFacade.create(userAccount);

            // populate created resource with hypermedia links
            populateWithHATEOASLinks(createdUserAccount, params.getUriInfo());

            // construct link to newly created resource to return in HTTP Header
            String createdUserAccountId = String.valueOf(createdUserAccount.getUserId());
            locationURI = params.getUriInfo().getBaseUriBuilder().path(UserAccountResource.class).path(createdUserAccountId).build();

        } catch(EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch(EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch(Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_CREATION_ERROR_MESSAGE);
        }

        return Response.created(locationURI).entity(createdUserAccount).build();
    }

    /**
     * Method that takes updated User Account as XML or JSON and its ID as path param.
     * It updates User Account in database for provided ID.
     */
    @PUT
    @Path("/{userId : \\d+}") // catch only numeric identifiers
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateUserAccount( @PathParam("userId") Long userId,
                                       UserAccount userAccount,
                                       @BeanParam GenericBeanParam params) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "updating existing UserAccount by executing UserAccountResource.updateUserAccount(userAccount) method of REST API");

        // set resource ID passed in path param on updated resource object
        userAccount.setUserId(userId);

        UserAccount updatedUserAccount = null;
        try {
            // reflect updated resource object in database
            updatedUserAccount = userAccountFacade.update(userAccount);
            // populate created resource with hypermedia links
            populateWithHATEOASLinks(updatedUserAccount, params.getUriInfo());

        } catch(EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch(EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch(Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_UPDATE_ERROR_MESSAGE);
        }

        return Response.status(Status.OK).entity(updatedUserAccount).build();
    }

    /**
     * Method that removes User Account entity from database for given ID.
     * The ID is passed through path param.
     */
    @DELETE
    @Path("/{userId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeUserAccount( @PathParam("userId") Long userId,
                                       @HeaderParam("authToken") String authToken) throws ForbiddenException, NotFoundException {

        if(authToken == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "removing given UserAccount by executing UserAccountResource.removeUserAccount(userId) method of REST API");

        // find User Account entity that should be deleted
        UserAccount toDeleteUserAccount = userAccountFacade.find(userId);
        // throw exception if entity hasn't been found
        if(toDeleteUserAccount == null)
            throw new NotFoundException("Could not found user account to delete for given id: " + userId + ".");

        // remove entity from database
        userAccountFacade.remove(toDeleteUserAccount);

        return Response.status(Status.NO_CONTENT).build();
    }

    /**
     * Additional methods returning a subset of resources based on given criteria
     * you can also achieve similar results by applying @QueryParams to generic method
     * returning all resources in order to filter and limit them
     */

    /**
     * Method returns number of User Account entities in database
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countUserAccounts(@HeaderParam("authToken") String authToken) throws ForbiddenException {

        if(authToken == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning number of user accounts by executing UserAccountResource.countUserAccounts() method of REST API");

        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(userAccountFacade.count()), 200, "number of user accounts");
        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method returns subset of User Account entities limited by creation date
     */
    @GET
    @Path("/created-between")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getUserAccountsCreatedBetween(@BeanParam DateBetweenBeanParam params) throws ForbiddenException, BadRequestException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning subset of user accounts created between given start and end date using UserAccountResource.getUserAccountsCreatedBetween() method of REST API");

        // check correctness of query params
        if(params.getStartDate() == null || params.getEndDate() == null) {
            throw new BadRequestException("Start date or end date query param not specified for request.");
        }

        if(params.getStartDate().after(params.getEndDate())) {
           throw new BadRequestException("Start date is after end date.");
        }

        // find user accounts by given criteria
        ResourceList<UserAccount> userAccounts =  new ResourceList<>( userAccountFacade.findCreatedBetween(params.getStartDate(),
                params.getEndDate(), params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(userAccounts, params.getUriInfo(), params.getOffset(), params.getLimit());

        // GenericEntity<List<UserAccount>> entity = new GenericEntity<List<UserAccount>>( userAccounts ) { };
        return Response.status(Status.OK).entity(userAccounts).build();
    }

    /**
     * Method returns subset of User Account entities that haven't been activated yet
     */
    @GET
    @Path("/not-activated")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getUserAccountsNotActivated(@BeanParam PaginationBeanParam params) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning subset of user accounts not activated using UserAccountResource.getUserAccountsNotActivated() method of REST API");

        // find user accounts by given criteria
        ResourceList<UserAccount> userAccounts = new ResourceList<>( userAccountFacade.findAllNotActivated(params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(userAccounts, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(userAccounts).build();
    }

    /**
     * Method returns subset of User Account entities that have already been activated
     */
    @GET
    @Path("/activated")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getUserAccountsActivated(@BeanParam PaginationBeanParam params) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning subset of user accounts activated using UserAccountResource.getUserAccountsActivated() method of REST API");

        // find user accounts by given criteria
        ResourceList<UserAccount> userAccounts = new ResourceList<>( userAccountFacade.findAllActivated(params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(userAccounts, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(userAccounts).build();
    }

    /**
     * Method returns subset of User Account entities for provided email address
     */
    @GET
    @Path("/email/{email : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getUserAccountsByEmail(@PathParam("email") String email, @BeanParam PaginationBeanParam params) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning user accounts for given email address using UserAccountResource.getUserAccountsByEmail() method of REST API ");

        // find user accounts by given criteria
        ResourceList<UserAccount> userAccounts = new ResourceList<>( userAccountFacade.findByEmail(email, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(userAccounts, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(userAccounts).build();
    }

    /**
     * Method returns subset of User Account entities for provided login name
     */
    @GET
    @Path("/login/{login : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getUserAccountsByLogin(@PathParam("login") String login, @BeanParam PaginationBeanParam params) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning user accounts for given login name using UserAccountResource.getUserAccountsByLogin() method of REST API");

        // find user accounts by given criteria
        ResourceList<UserAccount> userAccounts = new ResourceList<>( userAccountFacade.findByLogin(login, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(userAccounts, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(userAccounts).build();
    }

    /**
     * Method returns subset of User Account entities for provided account type
     */
    @GET
    @Path("/account-type/{accountType : \\w+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getUserAccountsByType(@PathParam("accountType") String accountType, @BeanParam PaginationBeanParam params) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning user accounts for given account type using UserAccountResource.getUserAccountsByType() method of REST API");

        // find user accounts by given criteria
        ResourceList<UserAccount> userAccounts = new ResourceList<>( userAccountFacade.findByAccountType(accountType, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(userAccounts, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(userAccounts).build();
    }

    /**
     * Method returns subset of User Account entities limited by last login time
     */
    @GET
    @Path("/last-logged-between")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getUserAccountsLastLoggedBetween(@BeanParam DateBetweenBeanParam params) throws ForbiddenException, BadRequestException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning subset of user accounts last logged between given start and end time using UserAccountResource.getUserAccountsLastLoggedBetween() method of REST API");

        // check correctness of query params
        if(params.getStartDate() == null || params.getEndDate() == null) {
            throw new BadRequestException("Start date or end date query param not specified for request.");
        }

        if(params.getStartDate().after(params.getEndDate())) {
            throw new BadRequestException("Start date is after end date.");
        }

        // find user accounts by given criteria
        ResourceList<UserAccount> userAccounts =  new ResourceList<>( userAccountFacade.findLastLoggedBetween(params.getStartDate(),
                params.getEndDate(), params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(userAccounts, params.getUriInfo(), params.getOffset(), params.getLimit());

        // GenericEntity<List<UserAccount>> entity = new GenericEntity<List<UserAccount>>( userAccounts ) { };
        return Response.status(Status.OK).entity(userAccounts).build();
    }

    /**
     * Method returns subset of User Account entities limited by last failed login time
     */
    @GET
    @Path("/last-failed-login-between")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getUserAccountsLastFailedLoginBetween(@BeanParam DateBetweenBeanParam params) throws ForbiddenException, BadRequestException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "returning subset of user accounts last failed logged between given start and end time using UserAccountResource.getUserAccountsLastFailedLoginBetween() method of REST API");

        // check correctness of query params
        if(params.getStartDate() == null || params.getEndDate() == null) {
            throw new BadRequestException("Start date or end date query param not specified for request.");
        }

        if(params.getStartDate().after(params.getEndDate())) {
            throw new BadRequestException("Start date is after end date.");
        }

        // find user accounts by given criteria
        ResourceList<UserAccount> userAccounts =  new ResourceList<>( userAccountFacade.findLastFailedLoginBetween(params.getStartDate(),
                params.getEndDate(), params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        populateWithHATEOASLinks(userAccounts, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(userAccounts).build();
    }

    /**
     * Method removes not activated User Account entities older then specified date or now
     */
    @DELETE
    @Path("/not-activated")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeNotActivatedUserAccountsOlderThan(@BeanParam OlderThanBeanParam params) throws ForbiddenException {

        if(params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "removing user accounts that haven't been activated and are older then given date (or now if date not specified) using UserAccountResource.removeNotActivatedUserAccountsOlderThan() method of REST API");

        Date youngestDate = params.getDate(); // youngestDate means delete all entities older than specified youngestDate
        if(youngestDate == null) youngestDate = new Date(); // if date not specified set current time

        // remove all specified entities from database
        Integer noOfDeleted = userAccountFacade.deleteOldNotActivated(youngestDate);

        // create response returning number of deleted entities
        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfDeleted), 200, "number of deleted user accounts");

        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method that updates and activates all not activated User Account entities in database
     */
    @PUT
    @Path("/activate-all")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateActivateAllUserAccounts(@HeaderParam("authToken") String authToken) throws ForbiddenException {

        if(authToken == null) throw new ForbiddenException("Unauthorized access to web service.");
        logger.log(Level.INFO, "updating and activating all not activated user accounts using UserAccountResource.updateActivateAllUserAccounts() method of REST API");

        // update and activate not activated user accounts in database
        Integer noOfUpdated = userAccountFacade.updateActivateAll();

        // create response returning number of updated entities
        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfUpdated), 200, "number of updated user accounts");

        return Response.status(Status.OK).entity(responseEntity).build();
    }


    // private helper methods e.g. to populates resources/resource lists with HATEOAS links

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    private void populateWithHATEOASLinks(ResourceList<UserAccount> userAccounts, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        if(offset != null && limit != null) {
            // self collection link
            userAccounts.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", offset).queryParam("limit", limit).build()).rel("self").build() );
            // prev collection link
            Integer prevOffset = (offset - limit) < 0 ? 0 : offset - limit;
            Integer prevLimit = offset - prevOffset;
            if(prevLimit > 0)
                userAccounts.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", prevOffset).queryParam("limit", prevLimit).build()).rel("prev").build() );
            else
                userAccounts.getLinks().add( Link.fromUri("").rel("prev").build() );
            // next collection link
            userAccounts.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", (offset+limit)).queryParam("limit", limit).build()).rel("next").build() );
        } else {
            userAccounts.getLinks().add( Link.fromUri(uriInfo.getAbsolutePath()).rel("self").build() );
        }

        try {

            // count resources hypermedia link
            Method countMethod = UserAccountResource.class.getMethod("countUserAccounts", String.class);
            userAccounts.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(UserAccountResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            userAccounts.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(UserAccountResource.class).build()).rel("user-accounts").build() );

            // get subset of resources hypermedia links
            // created-between
            Method createdBetweenMethod = UserAccountResource.class.getMethod("getUserAccountsCreatedBetween", DateBetweenBeanParam.class);
            userAccounts.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(UserAccountResource.class).path(createdBetweenMethod).build()).rel("created-between").build());

            // not-activated
            Method notActivatedMethod = UserAccountResource.class.getMethod("getUserAccountsNotActivated", PaginationBeanParam.class);
            userAccounts.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(UserAccountResource.class).path(notActivatedMethod).build()).rel("not-activated").build());

            // activated
            Method activatedMethod = UserAccountResource.class.getMethod("getUserAccountsActivated", PaginationBeanParam.class);
            userAccounts.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(UserAccountResource.class).path(activatedMethod).build()).rel("activated").build());

            // email
            userAccounts.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(UserAccountResource.class).path("email").build()).rel("email").build());

            // login
            userAccounts.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(UserAccountResource.class).path("login").build()).rel("login").build());

            // account-type
            userAccounts.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(UserAccountResource.class).path("account-type").build()).rel("account-type").build());

            // last-logged-between
            Method lastLoggedBetweenMethod = UserAccountResource.class.getMethod("getUserAccountsLastLoggedBetween", DateBetweenBeanParam.class);
            userAccounts.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(UserAccountResource.class).path(lastLoggedBetweenMethod).build()).rel("last-logged-between").build());

            // last-failed-login-between
            Method lastFailedLoginBetweenMethod = UserAccountResource.class.getMethod("getUserAccountsLastFailedLoginBetween", DateBetweenBeanParam.class);
            userAccounts.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(UserAccountResource.class).path(lastFailedLoginBetweenMethod).build()).rel("last-failed-login-between").build());

            // activate-all
            Method activateAllMethod = UserAccountResource.class.getMethod("updateActivateAllUserAccounts", String.class);
            userAccounts.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder().path(UserAccountResource.class).path(activateAllMethod).build()).rel("activate-all").build());


        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for(UserAccount userAccount : userAccounts.getResources()) {
            populateWithHATEOASLinks(userAccount, uriInfo);
        }
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    private void populateWithHATEOASLinks(UserAccount userAccount, UriInfo uriInfo) {

        Class resourceClass = null;
        if(userAccount.getAccountType().equals("natural_person")) {
            resourceClass = NaturalPersonResource.class;
        } else if(userAccount.getAccountType().equals("firm")) {
            resourceClass = FirmResource.class;

            // additional firm related hypermedia links
            try {
                // vatin link with pattern: http://localhost:port/app/rest/resources/vatin/{vatin}
                Method vatinMethod = FirmResource.class.getMethod("getFirmByVATIN", String.class, GenericBeanParam.class);
                userAccount.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                        .path(FirmResource.class)
                        .path(vatinMethod)
                        .resolveTemplate("vatin", ((Firm) userAccount).getVatin())
                        .build())
                        .rel("vatin").build());

                // company-number link with pattern: http://localhost:port/app/rest/resources/company-number/{companyNumber}
                Method companyNumberMethod = FirmResource.class.getMethod("getFirmByCompanyNumber", String.class, GenericBeanParam.class);
                userAccount.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                        .path(FirmResource.class)
                        .path(companyNumberMethod)
                        .resolveTemplate("companyNumber", ((Firm) userAccount).getCompanyNumber())
                        .build())
                        .rel("company-number").build());
            } catch(NoSuchMethodException e) {
                e.printStackTrace();
            }

        } else if(userAccount.getAccountType().equals("provider")) {
            //resourceClass = ProviderResource.class;
        } else if(userAccount.getAccountType().equals("employee")) {
            //resourceClass = EmployeeResource.class;
        } else {
            resourceClass = UserAccountResource.class;
        }

        // self link with pattern: http://localhost:port/app/rest/resources/{id}
        userAccount.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                .path(resourceClass)
                .path(userAccount.getUserId().toString())
                .build())
                .rel("self").build());

        // collection link with pattern: http://localhost:port/app/rest/resources
        userAccount.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                .path(UserAccountResource.class)
                .build())
                .rel("user-accounts").build());
    }

}