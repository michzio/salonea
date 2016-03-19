package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.EmployeeTermFacade;
import pl.salonea.ejb.stateless.TermFacade;
import pl.salonea.entities.EmployeeTerm;
import pl.salonea.entities.Term;
import pl.salonea.jaxrs.bean_params.EmployeeTermBeanParam;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.bean_params.TermBeanParam;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.TermWrapper;

import javax.inject.Inject;
import javax.transaction.*;
import javax.transaction.NotSupportedException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
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

    /**
     * Method returns all Term resources
     * They can be additionally filtered and paginated by @QueryParams
     */
    // TODO

    /**
     * related subresources (through relationships)
     */
    @Path("/{termId: \\d+}/employee-terms")
    public EmployeeTermResource getEmployeeTermResource() { return new EmployeeTermResource(); }

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

   // TODO UNCOMMENT WHEN DONE!
   //     for(Transaction transaction : termWrapper.getTransactions())
   //         pl.salonea.jaxrs.TransactionResource.populateWithHATEOASLinks(transaction, uriInfo);

   //     for(HistoricalTransaction historicalTransaction : termWrapper.getHistoricalTransactions())
   //         pl.salonea.jaxrs.HistoricalTransactionResource.populateWithHATEOASLinks(historicalTransaction, uriInfo);
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
}
