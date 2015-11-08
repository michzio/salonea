package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.ClientFacade;
import pl.salonea.ejb.stateless.EmployeeFacade;
import pl.salonea.ejb.stateless.EmployeeRatingFacade;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.*;
import pl.salonea.jaxrs.bean_params.ClientBeanParam;
import pl.salonea.jaxrs.bean_params.EmployeeRatingBeanParam;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import static javax.ws.rs.core.Response.Status;

import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.ClientWrapper;
import pl.salonea.jaxrs.wrappers.EmployeeWrapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by michzio on 14/10/2015.
 */
@Path("/employees")
public class EmployeeResource {

    private static final Logger logger = Logger.getLogger(EmployeeResource.class.getName());

    @Inject
    private EmployeeFacade employeeFacade;

    @Inject
    private ClientFacade clientFacade;

    @Inject
    private EmployeeRatingFacade employeeRatingFacade;

    /**
     * related subresources (through relationships)
     */

    @Path("/{userId: \\d+}/rating-clients")
    public ClientResource getClientResource() { return new ClientResource(); }

    @Path("/{userId: \\d+}/employee-ratings")
    public EmployeeRatingResource getEmployeeRatingResource() {
        return new EmployeeRatingResource();
    }

    // private helper methods e.g. to populate resources/resource lists with HATEOAS links

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList employees, UriInfo uriInfo, Integer offset, Integer limit) {

        ResourceList.generateNavigationLinks(employees,uriInfo, offset, limit);

        // TODO add hypermedia links

        for(Object object : employees.getResources()) {
            if(object instanceof Employee) {
                EmployeeResource.populateWithHATEOASLinks((Employee) object, uriInfo);
            } else if (object instanceof EmployeeWrapper) {
                EmployeeResource.populateWithHATEOASLinks((EmployeeWrapper) object, uriInfo);
            }

        }
    }

    /**
     * This method enables to populate each individual resource wrapper with hypermedia links
     */
    public static void populateWithHATEOASLinks(EmployeeWrapper employeeWrapper, UriInfo uriInfo) {

        EmployeeResource.populateWithHATEOASLinks(employeeWrapper.getEmployee(), uriInfo);

       // for(Education education : employeeWrapper.getEducations())
       //     pl.salonea.jaxrs.EducationResource.

       // for(Skill skill : employeeWrapper.getSkills())
       //     pl.salonea.jaxrs.SkillResource.

       for(ProviderService providerService : employeeWrapper.getSuppliedServices())
           pl.salonea.jaxrs.ProviderServiceResource.populateWithHATEOASLinks(providerService, uriInfo);

       // for(TermEmployeeWorkOn term : employeeWrapper.getTermsOnWorkStation())
       //    pl.salonea.jaxrs.TermEmployeeWorkOnResource.

       // for(EmployeeRating employeeRating : employeeWrapper.getReceivedRatings())
       //    pl.salonea.jaxrs.EmployeeRatingResource.

    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(Employee employee, UriInfo uriInfo) {

        // self link with pattern: http://localhost:port/app/rest/{resources}/{id}
        employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                .path(EmployeeResource.class)
                .path(employee.getUserId().toString())
                .build())
                .rel("self").build());

        // collection link with pattern: http://localhost:port/app/rest/{resources}
        employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                .path(EmployeeResource.class)
                .build())
                .rel("employees").build());

        try {

            // associated collections links with pattern: http://localhost:port/app/rest/{resources}/{id}/{relationship}

            // rating-clients
            Method ratingClientsMethod = EmployeeResource.class.getMethod("getClientResource");
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(ratingClientsMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("rating-clients").build());


            // rating-clients eagerly
            Method ratingClientsEagerlyMethod = ClientResource.class.getMethod("getEmployeeRatingClientsEagerly", Long.class, ClientBeanParam.class);
            employee.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(ratingClientsMethod)
                    .path(ratingClientsEagerlyMethod)
                    .resolveTemplate("userId", employee.getUserId().toString())
                    .build())
                    .rel("rating-clients-eagerly").build());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        // TODO more links
    }

    public class ClientResource {

        public ClientResource() {
        }

        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeRatingClients(@PathParam("userId") Long employeeId,
                                                 @BeanParam ClientBeanParam params) throws ForbiddenException, NotFoundException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning clients rating given employee using EmployeeResource.ClientResource.getEmployeeRatingClients(employeeId) method of REST API");

            // find employee entity for which to get rating it clients
            Employee employee = employeeFacade.find(employeeId);
            if (employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            // calculate number of filter query params
            Integer noOfParams = params.getUriInfo().getQueryParameters().size();
            if (params.getOffset() != null) noOfParams -= 1;
            if (params.getLimit() != null) noOfParams -= 1;

            ResourceList<Client> clients = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Employee> ratedEmployees = new ArrayList<>();
                ratedEmployees.add(employee);

                Address location = new Address(params.getStreet(), params.getHouseNumber(), params.getFlatNumber(), params.getZipCode(), params.getCity(), params.getState(), params.getCountry());
                Address delivery = new Address(params.getDeliveryStreet(), params.getDeliveryHouseNumber(), params.getDeliveryFlatNumber(), params.getDeliveryZipCode(), params.getDeliveryCity(), params.getDeliveryState(), params.getDeliveryCountry());

                // get clients for given rated employee filtered by given params
                clients = new ResourceList<>(
                        clientFacade.findByMultipleCriteria(params.getFirstName(), params.getLastName(), params.getFirmName(), params.getName(),
                                params.getDescription(), new HashSet<>(params.getClientTypes()), params.getOldestBirthDate(), params.getYoungestBirthDate(),
                                params.getYoungestAge(), params.getOldestAge(), location, delivery, params.getGender(), params.getRatedProviders(),
                                ratedEmployees, params.getOffset(), params.getLimit())
                );

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get clients for given rated employee without filtering
                clients = new ResourceList<>(clientFacade.findRatingEmployee(employee, params.getOffset(), params.getLimit()));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ClientResource.populateWithHATEOASLinks(clients, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Response.Status.OK).entity(clients).build();
        }

        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeRatingClientsEagerly(@PathParam("userId") Long employeeId,
                                                        @BeanParam ClientBeanParam params) throws ForbiddenException, NotFoundException {

            if (params.getAuthToken() == null) throw new ForbiddenException("Unauthorized access to web service.");
            logger.log(Level.INFO, "returning clients rating given employee eagerly using EmployeeResource.ClientResource.getEmployeeRatingClientsEagerly(employeeId) method of REST API");

            // find employee entity for which to get rating it clients
            Employee employee = employeeFacade.find(employeeId);
            if (employee == null)
                throw new NotFoundException("Could not find employee for id " + employeeId + ".");

            // calculate number of filter query params
            Integer noOfParams = params.getUriInfo().getQueryParameters().size();
            if (params.getOffset() != null) noOfParams -= 1;
            if (params.getLimit() != null) noOfParams -= 1;

            ResourceList<ClientWrapper> clients = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Employee> ratedEmployees = new ArrayList<>();
                ratedEmployees.add(employee);

                Address location = new Address(params.getStreet(), params.getHouseNumber(), params.getFlatNumber(), params.getZipCode(), params.getCity(), params.getState(), params.getCountry());
                Address delivery = new Address(params.getDeliveryStreet(), params.getDeliveryHouseNumber(), params.getDeliveryFlatNumber(), params.getDeliveryZipCode(), params.getDeliveryCity(), params.getDeliveryState(), params.getDeliveryCountry());

                // get clients eagerly for given rated employee filtered by given params
                clients = new ResourceList<>(
                        ClientWrapper.wrap(
                                clientFacade.findByMultipleCriteriaEagerly(params.getFirstName(), params.getLastName(), params.getFirmName(), params.getName(),
                                        params.getDescription(), new HashSet<>(params.getClientTypes()), params.getOldestBirthDate(), params.getYoungestBirthDate(),
                                        params.getYoungestAge(), params.getOldestAge(), location, delivery, params.getGender(), params.getRatedProviders(),
                                        ratedEmployees, params.getOffset(), params.getLimit())
                        )
                );

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get clients eagerly for given rated employee without filtering
                clients = new ResourceList<>(
                        ClientWrapper.wrap(clientFacade.findRatingEmployeeEagerly(employee, params.getOffset(), params.getLimit()))
                );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ClientResource.populateWithHATEOASLinks(clients, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Response.Status.OK).entity(clients).build();
        }
    }

    public class EmployeeRatingResource {

        public EmployeeRatingResource() { }

        /**
         * Method returns subset of Employee Rating entities for given Employee.
         * The employee id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getEmployeeRatings(@PathParam("userId") Long userId,
                                           @BeanParam EmployeeRatingBeanParam params) throws NotFoundException, ForbiddenException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Employee Rating entities for given Employee using EmployeeResource.EmployeeRatingResource.getEmployeeRatings(userId) method of REST API");

            // find employee entity for which to get associated employee ratings
            Employee employee = employeeFacade.find(userId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + userId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<EmployeeRating> employeeRatings = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Employee> employees = new ArrayList<>();
                employees.add(employee);

                // get employee ratings for given employee and filter params
                employeeRatings = new ResourceList<>(
                        employeeRatingFacade.findByMultipleCriteria(params.getClients(), employees, params.getMinRating(), params.getMaxRating(),
                                params.getExactRating(), params.getClientComment(), params.getEmployeeDementi(), params.getOffset(), params.getLimit())
                );

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get employee ratings for given employee without filtering
                employeeRatings = new ResourceList<>( employeeRatingFacade.findByEmployee(employee, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeRatingResource.populateWithHATEOASLinks(employeeRatings, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employeeRatings).build();
        }

        /**
         * Method that removes subset of Employee Rating entities from database for given Employee.
         * The employee id is passed through path params.
         */
        @DELETE
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response removeEmployeeRatings( @PathParam("userId") Long userId,
                                               @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "removing subset of Employee Rating entities for given Employee by executing EmployeeResource.EmployeeRatingResource.removeEmployeeRatings(userId) method of REST API");

            // find employee entity for which to remove employee ratings
            Employee employee = employeeFacade.find(userId);
            if(employee == null)
                throw new NotFoundException("Could not find employee for id " + userId + ".");

            // remove all specified entities from database
            Integer noOfDeleted = employeeRatingFacade.deleteByEmployee(employee);

            // create response returning number of deleted entities
            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfDeleted), 200, "number of deleted employee ratings for employee with id " + userId);

            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }
}
