package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.*;
import pl.salonea.entities.*;
import pl.salonea.entities.Transaction;
import pl.salonea.jaxrs.bean_params.*;
import pl.salonea.jaxrs.exceptions.*;
import pl.salonea.jaxrs.exceptions.BadRequestException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.RESTDateTime;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.EmployeeWrapper;
import pl.salonea.jaxrs.wrappers.HistoricalTransactionWrapper;
import pl.salonea.jaxrs.wrappers.TermWrapper;
import pl.salonea.jaxrs.wrappers.TransactionWrapper;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.transaction.*;
import javax.transaction.NotSupportedException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by michzio on 18/03/2016.
 */
@Path("/terms")
public class TermResource {

    private static final Logger logger = Logger.getLogger(TermResource.class.getName());

    @Inject
    private UserTransaction utx;

    @Inject
    private TermFacade termFacade;
    @Inject
    private EmployeeTermFacade employeeTermFacade;
    @Inject
    private TransactionFacade transactionFacade;
    @Inject
    private HistoricalTransactionFacade historicalTransactionFacade;
    @Inject
    private EmployeeFacade employeeFacade;
    @Inject
    private WorkStationFacade workStationFacade;
    @Inject
    private ServiceFacade serviceFacade;
    @Inject
    private ProviderServiceFacade providerServiceFacade;
    @Inject
    private ServicePointFacade servicePointFacade;

    /**
     * Method returns all Term entities.
     * They can be additionally filtered and paginated by @QueryParams
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTerms(@BeanParam TermBeanParam params) throws ForbiddenException,
    /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Terms by executing TermResource.getTerms() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<Term> terms = null;

        if (noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            utx.begin();

            // get terms filtered by criteria provided in query params
            terms = new ResourceList<>(
                    termFacade.findByMultipleCriteria(params.getServicePoints(), params.getWorkStations(), params.getEmployees(),
                            params.getServices(), params.getProviderServices(), params.getPeriod(), params.getStrictTerm(),
                            params.getOffset(), params.getLimit())
            );

            utx.commit();

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all terms without filtering (eventually paginated)
            terms = new ResourceList<>(termFacade.findAll(params.getOffset(), params.getLimit()));
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        TermResource.populateWithHATEOASLinks(terms, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(terms).build();
    }

    @GET
    @Path("/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTermsEagerly(@BeanParam TermBeanParam params) throws ForbiddenException,
    /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Terms eagerly by executing TermResource.getTermsEagerly() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<TermWrapper> terms = null;

        if (noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            utx.begin();

            // get terms eagerly filtered by criteria provided in query params
            terms = new ResourceList<>(
                    TermWrapper.wrap(
                            termFacade.findByMultipleCriteriaEagerly(params.getServicePoints(), params.getWorkStations(), params.getEmployees(),
                                    params.getServices(), params.getProviderServices(), params.getPeriod(), params.getStrictTerm(),
                                    params.getOffset(), params.getLimit())
                    )
            );

            utx.commit();

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all terms eagerly without filtering (eventually paginated)
            terms = new ResourceList<>(TermWrapper.wrap(termFacade.findAllEagerly(params.getOffset(), params.getLimit())));
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        TermResource.populateWithHATEOASLinks(terms, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(terms).build();
    }

    /**
     * Method matches specific Term resource by identifier and returns its instance.
     */
    @GET
    @Path("/{termId: \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTerm(@PathParam("termId") Long termId,
                            @BeanParam GenericBeanParam params) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning given Term by executing TermResource.getTerm(termId) method of REST API");

        Term foundTerm = termFacade.find(termId);
        if (foundTerm == null)
            throw new NotFoundException("Could not find term for id " + termId + ".");

        // adding hypermedia links to term resource
        TermResource.populateWithHATEOASLinks(foundTerm, params.getUriInfo());

        return Response.status(Status.OK).entity(foundTerm).build();
    }

    /**
     * Method matches specific Term resource by identifier and returns its instance fetching it eagerly
     */
    @GET
    @Path("/{termId: \\d+}/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTermEagerly(@PathParam("termId") Long termId,
                                   @BeanParam GenericBeanParam params) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning given Term eagerly by executing TermResource.getTermEagerly(termId) method of REST API");

        Term foundTerm = termFacade.findByIdEagerly(termId);
        if (foundTerm == null)
            throw new NotFoundException("Could not find term for id " + termId + ".");

        // wrapping Term into TermWrapper in order to marshall eagerly fetched associated collections of entities
        TermWrapper wrappedTerm = new TermWrapper(foundTerm);

        // adding hypermedia links to wrapped term resource
        TermResource.populateWithHATEOASLinks(wrappedTerm, params.getUriInfo());

        return Response.status(Status.OK).entity(wrappedTerm).build();
    }

    /**
     * Method that takes Term as XML or JSON and creates its new instance in database
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createTerm(Term term,
                               @BeanParam GenericBeanParam params) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "creating new Term by executing TermResource.createTerm(term) method of REST API");

        Term createdTerm = null;
        URI locationURI = null;

        try {
            // persist new resource in database
            createdTerm = termFacade.create(term);

            // populate created resource with hypermedia links
            TermResource.populateWithHATEOASLinks(createdTerm, params.getUriInfo());

            // construct link to newly created resource to return in HTTP Header
            String createdTermId = String.valueOf(createdTerm.getTermId());
            locationURI = params.getUriInfo().getBaseUriBuilder().path(TermResource.class).path(createdTermId).build();

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_CREATION_ERROR_MESSAGE);
        }

        return Response.created(locationURI).entity(createdTerm).build();
    }

    /**
     * Method that takes updated Term as XML or JSON and its ID as path param.
     * It updates Term in database for provided ID.
     */
    @PUT
    @Path("/{termId: \\d+}") // catch only numeric identifiers
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateTerm(@PathParam("termId") Long termId,
                               Term term,
                               @BeanParam GenericBeanParam params) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "updating existing Term by executing TermResource.updateTerm(termId, term) method of REST API");

        // set resource ID passed in path param on updated resource object
        term.setTermId(termId);

        Term updatedTerm = null;
        try {
            // reflect updated resource object in database
            updatedTerm = termFacade.update(term, true);
            // populate created resource with hypermedia links
            TermResource.populateWithHATEOASLinks(updatedTerm, params.getUriInfo());

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_UPDATE_ERROR_MESSAGE);
        }

        return Response.status(Status.OK).entity(updatedTerm).build();
    }

    /**
     * Method that removes Term entity from database for given ID.
     * The ID is passed through path param.
     */
    @DELETE
    @Path("/{termId: \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeTerm(@PathParam("termId") Long termId,
                               @BeanParam GenericBeanParam params) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing given Term by executing TermResource.removeTerm(termId) method of REST API");

        // find Term entity that should be deleted
        Term toDeleteTerm = termFacade.find(termId);
        // throw exception if entity hasn't been found
        if (toDeleteTerm == null)
            throw new NotFoundException("Could not find term to delete for given id: " + termId + ".");

        // remove entity from database
        termFacade.remove(toDeleteTerm);

        return Response.status(Status.NO_CONTENT).build();
    }

    /**
     * Additional methods returning a subset of resources based on given criteria
     * You can also achieve similar results by applying @QueryParams to generic method
     * returning all resources in order to filter and limit them.
     */

    /**
     * Method returns number of Term entities in database
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countTerms(@BeanParam GenericBeanParam params) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning number of terms by executing TermResource.countTerms() method of REST API");

        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(termFacade.count()), 200, "number of terms");
        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method returns subset of Term entities for given Term's date range (Period).
     * Term start and end dates are passed through query params.
     */
    @GET
    @Path("/by-term")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTermsByTerm(@BeanParam DateBetweenBeanParam params) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning terms for given term (startDate, endDate) using " +
                "TermResource.getTermsByTerm(term) method of REST API");

        RESTToolkit.validateDateRange(params); // i.e. startDate and endDate

        // find terms by given criteria (term)
        ResourceList<Term> terms = new ResourceList<>(
                termFacade.findByPeriod(params.getStartDate(), params.getEndDate(), params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        TermResource.populateWithHATEOASLinks(terms, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(terms).build();
    }

    /**
     * Method returns subset of Term entities for given Term's date range strict (Period strict)
     * Term (strict) start and end dates are passed through query params.
     */
    @GET
    @Path("/by-term-strict")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTermsByTermStrict(@BeanParam DateBetweenBeanParam params) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning terms for given term strict (startDate, endDate) using " +
                "TermResource.getTermsByTermStrict(termStrict) method of REST API");

        RESTToolkit.validateDateRange(params); // i.e. startDate and endDate

        // find terms by given criteria (term strict)
        ResourceList<Term> terms = new ResourceList<>(
                termFacade.findByPeriodStrict(params.getStartDate(), params.getEndDate(), params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        TermResource.populateWithHATEOASLinks(terms, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(terms).build();
    }

    /**
     * Method returns subset of Term entities with Term defined after given date.
     * REST Date Time is passed through path param.
     */
    @GET
    @Path("/after/{date : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTermsAfterDate(@PathParam("date") RESTDateTime date,
                                      @BeanParam PaginationBeanParam params) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning terms defined after given date using " +
                "TermResource.getTermsAfterDate(date) method of REST API");

        if (date == null)
            throw new BadRequestException("Date param must be specified correctly.");

        // find terms after given date
        ResourceList<Term> terms = new ResourceList<>(
                termFacade.findAfter(date, params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        TermResource.populateWithHATEOASLinks(terms, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(terms).build();
    }

    /**
     * Method returns subset of Term entities with Term defined after given date (strict).
     * REST Date Time is passed through path param.
     */
    @GET
    @Path("/after-strict/{date : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTermsAfterDateStrict(@PathParam("date") RESTDateTime date,
                                            @BeanParam PaginationBeanParam params) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning terms defined after given date (strict) using " +
                "TermResource.getTermsAfterDateStrict(date) method of REST API");

        if (date == null)
            throw new BadRequestException("Date param must be specified correctly.");

        // find terms after given date (strict)
        ResourceList<Term> terms = new ResourceList<>(
                termFacade.findAfterStrict(date, params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        TermResource.populateWithHATEOASLinks(terms, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(terms).build();
    }

    /**
     * Method returns subset of Term entities with Term defined before given date.
     * REST Date Time is passed through path param.
     */
    @GET
    @Path("/before/{date : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTermsBeforeDate( @PathParam("date") RESTDateTime date,
                                        @BeanParam PaginationBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning terms defined before given date using " +
                "TermResource.getTermsBeforeDate(date) method of REST API");

        if(date == null)
            throw new BadRequestException("Date param must be specified correctly.");

        // find terms before given date
        ResourceList<Term> terms = new ResourceList<>(
                termFacade.findBefore(date, params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        TermResource.populateWithHATEOASLinks(terms, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(terms).build();
    }

    /**
     * Method returns subset of Term entities with Term defined before given date (strict).
     * REST Date Time is passed through path param.
     */
    @GET
    @Path("/before-strict/{date : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTermsBeforeDateStrict( @PathParam("date") RESTDateTime date,
                                              @BeanParam PaginationBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning terms defined before given date (strict) using " +
                "TermResource.getTermsBeforeDateStrict(date) method of REST API");

        if(date == null)
            throw new BadRequestException("Date param must be specified correctly.");

        // find terms before given date (strict)
        ResourceList<Term> terms = new ResourceList<>(
                termFacade.findBeforeStrict(date, params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        TermResource.populateWithHATEOASLinks(terms, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(terms).build();
    }

    /**
     * related subresources (through relationships)
     */
    @Path("/{termId: \\d+}/employee-terms")
    public EmployeeTermResource getEmployeeTermResource() { return new EmployeeTermResource(); }
    @Path("/{termId: \\d+}/transactions")
    public TransactionResource getTransactionResource() { return new TransactionResource(); }
    @Path("/{termId: \\d+}/historical-transactions")
    public HistoricalTransactionResource getHistoricalTransactionResource() { return new HistoricalTransactionResource(); }
    @Path("/{termId: \\d+}/employees")
    public EmployeeResource getEmployeeResource() { return new EmployeeResource(); }
    @Path("/{termId: \\d+}/work-stations")
    public WorkStationResource getWorkStationResource() { return new WorkStationResource(); }
    @Path("/{termId: \\d+}/services")
    public ServiceResource getServiceResource() { return new ServiceResource(); }
    @Path("/{termId: \\d+}/provider-services")
    public ProviderServiceResource getProviderServiceResource() {
        return new ProviderServiceResource();
    }
    @Path("/{termId: \\d+}/service-points")
    public ServicePointResource getServicePointResource() { return new ServicePointResource(); }

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList terms, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(terms, uriInfo, offset, limit);

        try {
            // count resources hypermedia link
            Method countMethod = TermResource.class.getMethod("countTerms", GenericBeanParam.class);
            terms.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(TermResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            terms.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(TermResource.class).build()).rel("terms").build() );

            // get all resources eagerly hypermedia link
            Method termsEagerlyMethod = TermResource.class.getMethod("getTermsEagerly", TermBeanParam.class);
            terms.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(TermResource.class).path(termsEagerlyMethod).build()).rel("terms-eagerly").build() );

            // get subset of resources hypermedia links

            // by-term
            Method byTermMethod = TermResource.class.getMethod("getTermsByTerm", DateBetweenBeanParam.class);
            terms.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TermResource.class)
                    .path(byTermMethod)
                    .build())
                    .rel("by-term").build() );

            // by-term-strict
            Method byTermStrictMethod = TermResource.class.getMethod("getTermsByTermStrict", DateBetweenBeanParam.class);
            terms.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TermResource.class)
                    .path(byTermStrictMethod)
                    .build())
                    .rel("by-term-strict").build() );

            // after
            terms.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TermResource.class)
                    .path("after")
                    .build())
                    .rel("after").build() );

            // after-strict
            terms.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TermResource.class)
                    .path("after-strict")
                    .build())
                    .rel("after-strict").build() );

            // before
            terms.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TermResource.class)
                    .path("before")
                    .build())
                    .rel("before").build() );

            // before-strict
            terms.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TermResource.class)
                    .path("before-strict")
                    .build())
                    .rel("before-strict").build() );

        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        }

        for( Object object : terms.getResources() ) {
            if (object instanceof Term) {
                TermResource.populateWithHATEOASLinks( (Term) object, uriInfo );
            } else if (object instanceof TermWrapper) {
                TermResource.populateWithHATEOASLinks( (TermWrapper) object, uriInfo );
            }
        }
    }

    /**
     * This method enables to populate each individual resource wrapper with hypermedia links
     */
    public static void populateWithHATEOASLinks(TermWrapper termWrapper, UriInfo uriInfo) {

        TermResource.populateWithHATEOASLinks(termWrapper.getTerm(), uriInfo);

        for(EmployeeTerm employeeTerm : termWrapper.getEmployeeTerms())
            pl.salonea.jaxrs.EmployeeTermResource.populateWithHATEOASLinks(employeeTerm, uriInfo);

        for(Transaction transaction : termWrapper.getTransactions())
            pl.salonea.jaxrs.TransactionResource.populateWithHATEOASLinks(transaction, uriInfo);

        for(HistoricalTransaction historicalTransaction : termWrapper.getHistoricalTransactions())
            pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransaction, uriInfo);
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(Term term, UriInfo uriInfo) {

        // self link with pattern: http://localhost:port/app/rest/{resources}/{id}
        term.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                .path(TermResource.class)
                .path(term.getTermId().toString())
                .build())
                .rel("self").build() );

        // collection link with pattern: http://localhost:port/app/rest/{resources}
        term.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                .path(TermResource.class)
                .build())
                .rel("terms").build() );

        try {
            // self eagerly link with pattern http://localhost:port/app/rest/{resources}/{id}/eagerly
            Method termEagerlyMethod = TermResource.class.getMethod("getTermEagerly", Long.class, GenericBeanParam.class);
            term.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TermResource.class)
                    .path(termEagerlyMethod)
                    .resolveTemplate("termId", term.getTermId().toString())
                    .build())
                    .rel("term-eagerly").build() );

            // associated collections links with pattern: http://localhost:port/app/rest/{resources}/{id}/{relationship}

            /**
             * Employee Terms associated with current Term resource
             */
            // employee-terms
            Method employeeTermsMethod = TermResource.class.getMethod("getEmployeeTermResource");
            term.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TermResource.class)
                    .path(employeeTermsMethod)
                    .resolveTemplate("termId", term.getTermId().toString())
                    .build())
                    .rel("employee-terms").build() );

            // employee-terms count
            Method countEmployeeTermsByTermMethod = TermResource.EmployeeTermResource.class.getMethod("countEmployeeTermsByTerm", Long.class, GenericBeanParam.class);
            term.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TermResource.class)
                    .path(employeeTermsMethod)
                    .path(countEmployeeTermsByTermMethod)
                    .resolveTemplate("termId", term.getTermId().toString())
                    .build())
                    .rel("employee-terms-count").build() );

            /**
             * Transactions associated with current Term resource
             */
            // transactions
            Method transactionsMethod = TermResource.class.getMethod("getTransactionResource");
            term.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TermResource.class)
                    .path(transactionsMethod)
                    .resolveTemplate("termId", term.getTermId().toString())
                    .build())
                    .rel("transactions").build() );

            // transactions eagerly
            Method transactionsEagerlyMethod = TermResource.TransactionResource.class.getMethod("getTermTransactionsEagerly", Long.class, TransactionBeanParam.class);
            term.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TermResource.class)
                    .path(transactionsMethod)
                    .path(transactionsEagerlyMethod)
                    .resolveTemplate("termId", term.getTermId().toString())
                    .build())
                    .rel("transactions-eagerly").build() );

            // transactions count
            Method countTransactionsByTermMethod = TermResource.TransactionResource.class.getMethod("countTransactionsByTerm", Long.class, GenericBeanParam.class);
            term.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TermResource.class)
                    .path(transactionsMethod)
                    .path(countTransactionsByTermMethod)
                    .resolveTemplate("termId", term.getTermId().toString())
                    .build())
                    .rel("transactions-count").build() );

            /**
             * Historical Transactions associated with current Term resource
             */
            // historical-transactions
            Method historicalTransactionsMethod = TermResource.class.getMethod("getHistoricalTransactionResource");
            term.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TermResource.class)
                    .path(historicalTransactionsMethod)
                    .resolveTemplate("termId", term.getTermId().toString())
                    .build())
                    .rel("historical-transactions").build() );

            // historical-transactions eagerly
            Method historicalTransactionsEagerlyMethod = TermResource.HistoricalTransactionResource.class.getMethod("getTermHistoricalTransactionsEagerly", Long.class, HistoricalTransactionBeanParam.class);
            term.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TermResource.class)
                    .path(historicalTransactionsMethod)
                    .path(historicalTransactionsEagerlyMethod)
                    .resolveTemplate("termId", term.getTermId().toString())
                    .build())
                    .rel("historical-transactions-eagerly").build());

            // historical-transactions count
            Method countHistoricalTransactionsByTermMethod = TermResource.HistoricalTransactionResource.class.getMethod("countHistoricalTransactionsByTerm", Long.class, GenericBeanParam.class);
            term.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TermResource.class)
                    .path(historicalTransactionsMethod)
                    .path(countHistoricalTransactionsByTermMethod)
                    .resolveTemplate("termId", term.getTermId().toString())
                    .build())
                    .rel("historical-transactions-count").build());

            /**
             * Employees working during current Term resource
             */
            // employees
            Method employeesMethod = TermResource.class.getMethod("getEmployeeResource");
            term.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TermResource.class)
                    .path(employeesMethod)
                    .resolveTemplate("termId", term.getTermId().toString())
                    .build())
                    .rel("employees").build() );

            // employees eagerly
            Method employeesEagerlyMethod = TermResource.EmployeeResource.class.getMethod("getTermEmployeesEagerly", Long.class, EmployeeBeanParam.class);
            term.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TermResource.class)
                    .path(employeesMethod)
                    .path(employeesEagerlyMethod)
                    .resolveTemplate("termId", term.getTermId().toString())
                    .build())
                    .rel("employees-eagerly").build() );

            // employees count
            Method countEmployeesByTermMethod = TermResource.EmployeeResource.class.getMethod("countEmployeesByTerm", Long.class, GenericBeanParam.class);
            term.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(TermResource.class)
                    .path(employeesMethod)
                    .path(countEmployeesByTermMethod)
                    .resolveTemplate("termId", term.getTermId().toString())
                    .build())
                    .rel("employees-count").build() );

            /**
             * Work Stations for which current Term resource is defined
             */
            // work-stations

            // work-stations eagerly

            // work-stations count

            /**
             * Services provided in current Term
             */
            // services

            // services eagerly

            // services count

            /**
             * Provider Services provided in current Term
             */
            // provider-services

            // provider-services eagerly

            // provider-services count

            /**
             * Service Points for which current Term resource is defined
             */
            // service-points

            // service-points eagerly

            // service-points count

        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public class EmployeeTermResource {

        public EmployeeTermResource() { }

        /**
         * Method returns subset of Employee Term entities for given Term entity.
         * The term id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getTermEmployeeTerms( @PathParam("termId") Long termId,
                                              @BeanParam EmployeeTermBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employee terms for given term using " +
                    "TermResource.EmployeeTermResource.getTermEmployeeTerms(termId) method of REST API");

            // find term entity for which to get associated employee terms
            Term term = termFacade.find(termId);
            if(term == null)
                throw new NotFoundException("Could not find term for id " + termId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<EmployeeTerm> employeeTerms = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Term> terms = new ArrayList<>();
                terms.add(term);

                // get employee terms for given term filtered by given query params

                utx.begin();

                employeeTerms = new ResourceList<>(
                        employeeTermFacade.findByMultipleCriteria(params.getServicePoints(), params.getWorkStations(), params.getEmployees(),
                                terms, params.getServices(), params.getProviderServices(), params.getPeriod(), params.getStrictTerm(),
                                params.getOffset(), params.getLimit())
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get employee terms for given term without filtering (eventually paginated)
                employeeTerms = new ResourceList<>( employeeTermFacade.findByTerm(term, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeTermResource.populateWithHATEOASLinks(employeeTerms, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employeeTerms).build();
        }

        /**
         * Method that counts Employee Term entities for given Term resource.
         * The term id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countEmployeeTermsByTerm( @PathParam("termId") Long termId,
                                                  @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of employee terms for given term by executing " +
                    "TermResource.EmployeeTermResource.countEmployeeTermsByTerm(termId) method of REST API");

            // find term entity for which to count employee terms
            Term term = termFacade.find(termId);
            if(term == null)
                throw new NotFoundException("Could not find term for id " + termId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(employeeTermFacade.countByTerm(term)), 200,
                    "number of employee terms for term with id " + term.getTermId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    public class TransactionResource {

        public TransactionResource() { }

        /**
         * Method returns subset of Transaction entities for given Term entity.
         * The term id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getTermTransactions( @PathParam("termId") Long termId,
                                             @BeanParam TransactionBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning transactions for given term using " +
                    "TermResource.TransactionResource.getTermTransactions(termId) method of REST API");

            // find term entity for which to get associated transactions
            Term term = termFacade.find(termId);
            if(term == null)
                throw new NotFoundException("Could not find term for id " + termId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<Transaction> transactions = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Term> terms = new ArrayList<>();
                terms.add(term);

                // get transactions for given term filtered by given query params

                utx.begin();

                transactions = new ResourceList<>(
                        transactionFacade.findByMultipleCriteria(params.getClients(), params.getProviders(), params.getServices(), params.getServicePoints(),
                                params.getWorkStations(), params.getEmployees(), params.getProviderServices(), params.getTransactionTimePeriod(),
                                params.getBookedTimePeriod(), terms, params.getPriceRange(), params.getCurrencyCodes(), params.getPaymentMethods(),
                                params.getPaid(), params.getOffset(), params.getLimit())
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get transactions for given term without filtering (eventually paginated)
                transactions = new ResourceList<>( transactionFacade.findByTerm(term, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.TransactionResource.populateWithHATEOASLinks(transactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(transactions).build();
        }

        /**
         * Method returns subset of Transaction entities for given Term fetching them eagerly.
         * The term id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getTermTransactionsEagerly( @PathParam("termId") Long termId,
                                                    @BeanParam TransactionBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning transactions eagerly for given term using " +
                    "TermResource.TransactionResource.getTermTransactionsEagerly(termId) method of REST API");

            // find term entity for which to get associated transactions
            Term term = termFacade.find(termId);
            if(term == null)
                throw new NotFoundException("Could not find term for id " + termId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<TransactionWrapper> transactions = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Term> terms = new ArrayList<>();
                terms.add(term);

                // get transactions eagerly for given term filtered by given query params

                utx.begin();

                transactions = new ResourceList<>(
                        TransactionWrapper.wrap(
                                transactionFacade.findByMultipleCriteriaEagerly(params.getClients(), params.getProviders(), params.getServices(),
                                        params.getServicePoints(), params.getWorkStations(), params.getEmployees(), params.getProviderServices(),
                                        params.getTransactionTimePeriod(), params.getBookedTimePeriod(), terms, params.getPriceRange(),
                                        params.getCurrencyCodes(), params.getPaymentMethods(), params.getPaid(), params.getOffset(), params.getLimit())
                        )
                );

                utx.commit();

            }  else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get transactions eagerly for given term without filtering (eventually paginated)
                transactions = new ResourceList<>( TransactionWrapper.wrap(transactionFacade.findByTermEagerly(term, params.getOffset(), params.getLimit())) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.TransactionResource.populateWithHATEOASLinks(transactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(transactions).build();
        }

        /**
         * Method that counts Transaction entities for given Term resource.
         * The term id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countTransactionsByTerm( @PathParam("termId") Long termId,
                                                 @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of transactions for given term by executing " +
                    "TermResource.TransactionResource.countTransactionsByTerm(termId) method of REST API");

            // find term entity for which to count transactions
            Term term = termFacade.find(termId);
            if(term == null)
                throw new NotFoundException("Could not find term for id " + termId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(transactionFacade.countByTerm(term)), 200,
                    "number of transactions for term with id " + term.getTermId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    public class HistoricalTransactionResource {

        public HistoricalTransactionResource() { }

        /**
         * Method returns subset of Historical Transaction entities for given Term entity.
         * The term id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getTermHistoricalTransactions(@PathParam("termId") Long termId,
                                                      @BeanParam HistoricalTransactionBeanParam params) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning historical transactions for given term using " +
                    "TermResource.HistoricalTransactionResource.getTermHistoricalTransactions(termId) method of REST API");

            // find term entity for which to get associated historical transactions
            Term term = termFacade.find(termId);
            if (term == null)
                throw new NotFoundException("Could not find term for id " + termId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<HistoricalTransaction> historicalTransactions = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Term> terms = new ArrayList<>();
                terms.add(term);

                // get historical transactions for given term filtered by given query params

                utx.begin();

                historicalTransactions = new ResourceList<>(
                        historicalTransactionFacade.findByMultipleCriteria(params.getClients(), params.getProviders(), params.getServices(), params.getServicePoints(),
                                params.getWorkStations(), params.getEmployees(), params.getProviderServices(), params.getTransactionTimePeriod(),
                                params.getBookedTimePeriod(), terms, params.getPriceRange(), params.getCurrencyCodes(), params.getPaymentMethods(),
                                params.getPaid(), params.getCompletionStatuses(), params.getClientRatingRange(), params.getClientComments(),
                                params.getProviderRatingRange(), params.getProviderDementis(), params.getOffset(), params.getLimit())
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get historical transactions for given term without filtering (eventually paginated)
                historicalTransactions = new ResourceList<>(historicalTransactionFacade.findByTerm(term, params.getOffset(), params.getLimit()));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(historicalTransactions).build();
        }

        /**
         * Method returns subset of Historical Transaction entities for given Term fetching them eagerly.
         * The term id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getTermHistoricalTransactionsEagerly(@PathParam("termId") Long termId,
                                                             @BeanParam HistoricalTransactionBeanParam params) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning historical transactions eagerly for given term using " +
                    "TermResource.HistoricalTransactionResource.getTermHistoricalTransactionsEagerly(termId) method of REST API");

            // find term entity for which to get associated historical transactions
            Term term = termFacade.find(termId);
            if (term == null)
                throw new NotFoundException("Could not find term for id " + termId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<HistoricalTransactionWrapper> historicalTransactions = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Term> terms = new ArrayList<>();
                terms.add(term);

                // get historical transactions eagerly for given term filtered by given query params

                utx.begin();

                historicalTransactions = new ResourceList<>(
                        HistoricalTransactionWrapper.wrap(
                                historicalTransactionFacade.findByMultipleCriteriaEagerly(params.getClients(), params.getProviders(), params.getServices(), params.getServicePoints(),
                                        params.getWorkStations(), params.getEmployees(), params.getProviderServices(), params.getTransactionTimePeriod(),
                                        params.getBookedTimePeriod(), terms, params.getPriceRange(), params.getCurrencyCodes(), params.getPaymentMethods(),
                                        params.getPaid(), params.getCompletionStatuses(), params.getClientRatingRange(), params.getClientComments(),
                                        params.getProviderRatingRange(), params.getProviderDementis(), params.getOffset(), params.getLimit())
                        )
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get historical transactions eagerly for given term without filtering (eventually paginated)
                historicalTransactions = new ResourceList<>(HistoricalTransactionWrapper.wrap(historicalTransactionFacade.findByTermEagerly(term, params.getOffset(), params.getLimit())));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransactions, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(historicalTransactions).build();
        }

        /**
         * Method that counts Historical Transaction entities for given Term resource.
         * The term id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countHistoricalTransactionsByTerm( @PathParam("termId") Long termId,
                                                           @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of historical transactions for given term by executing " +
                    "TermResource.HistoricalTransactionResource.countHistoricalTransactionsByTerm(termId) method of REST API");

            // find term entity for which to count historical transactions
            Term term = termFacade.find(termId);
            if (term == null)
                throw new NotFoundException("Could not find term for id " + termId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(historicalTransactionFacade.countByTerm(term)), 200,
                    "number of historical transactions for term with id " + term.getTermId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    public class EmployeeResource {

        public EmployeeResource() { }

        /**
         * Method returns subset of Employee entities for given Term entity.
         * The term id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getTermEmployees(@PathParam("termId") Long termId,
                                         @BeanParam EmployeeBeanParam params) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employees for given term using " +
                    "TermResource.EmployeeResource.getTermEmployees(termId) method of REST API");

            // find term entity for which to get associated employees
            Term term = termFacade.find(termId);
            if (term == null)
                throw new NotFoundException("Could not find term for id " + termId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<Employee> employees = null;

            if (noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Term> terms = new ArrayList<>();
                terms.add(term);

                // get employees for given term filtered by given query params

                utx.begin();

                employees = new ResourceList<>(
                        employeeFacade.findByMultipleCriteria(params.getDescriptions(), params.getJobPositions(), params.getSkills(),
                                params.getEducations(), params.getServices(), params.getProviderServices(), params.getServicePoints(),
                                params.getWorkStations(), params.getPeriod(), params.getStrictTerm(), terms, params.getRated(),
                                params.getMinAvgRating(), params.getMaxAvgRating(), params.getRatingClients(), params.getOffset(), params.getLimit())
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get employees for given term without filtering (eventually paginated)
                employees = new ResourceList<>(employeeFacade.findByTerm(term, params.getOffset(), params.getLimit()));
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeResource.populateWithHATEOASLinks(employees, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employees).build();
        }

        /**
         * Method returns subset of Employee entities for given Term fetching them eagerly.
         * The term id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getTermEmployeesEagerly( @PathParam("termId") Long termId,
                                                 @BeanParam EmployeeBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employees eagerly for given term using " +
                    "TermResource.EmployeeResource.getTermEmployeesEagerly(termId) method of REST API");

            // find term entity for which to get associated employees
            Term term = termFacade.find(termId);
            if (term == null)
                throw new NotFoundException("Could not find term for id " + termId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<EmployeeWrapper> employees = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<Term> terms = new ArrayList<>();
                terms.add(term);

                // get employees eagerly for given term filtered by given query params

                utx.begin();

                employees = new ResourceList<>(
                        EmployeeWrapper.wrap(
                                employeeFacade.findByMultipleCriteriaEagerly(params.getDescriptions(), params.getJobPositions(),
                                        params.getSkills(), params.getEducations(), params.getServices(), params.getProviderServices(),
                                        params.getServicePoints(), params.getWorkStations(), params.getPeriod(), params.getStrictTerm(),
                                        terms, params.getRated(), params.getMinAvgRating(), params.getMaxAvgRating(), params.getRatingClients(),
                                        params.getOffset(), params.getLimit())
                        )
                );

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get employees eagerly for given term without filtering (eventually paginated)
                employees = new ResourceList<>( EmployeeWrapper.wrap(employeeFacade.findByTermEagerly(term, params.getOffset(), params.getLimit())) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeResource.populateWithHATEOASLinks(employees, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employees).build();
        }

        /**
         * Method that counts Employee entities for given Term resource
         * The term id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countEmployeesByTerm( @PathParam("termId") Long termId,
                                              @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of employees for given term by executing " +
                    "TermResource.EmployeeResource.countEmployeesByTerm(termId) method of REST API");

            // find term entity for which to count employees
            Term term = termFacade.find(termId);
            if (term == null)
                throw new NotFoundException("Could not find term for id " + termId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(employeeFacade.countByTerm(term)), 200,
                    "number of employees for term with id " + term.getTermId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    public class WorkStationResource {

        public WorkStationResource() { }

        // TODO
    }

    public class ServiceResource {

        public ServiceResource() { }

        // TODO
    }

    public class ProviderServiceResource {

        public ProviderServiceResource() { }

        // TODO
    }

    public class ServicePointResource {

        public ServicePointResource() { }

        // TODO
    }
}
