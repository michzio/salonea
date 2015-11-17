package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.PaymentMethodFacade;
import pl.salonea.ejb.stateless.ProviderFacade;
import pl.salonea.entities.Industry;
import pl.salonea.entities.PaymentMethod;
import pl.salonea.entities.Provider;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.bean_params.PaginationBeanParam;
import pl.salonea.jaxrs.bean_params.PaymentMethodBeanParam;
import pl.salonea.jaxrs.bean_params.ProviderBeanParam;
import pl.salonea.jaxrs.exceptions.ExceptionHandler;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.UnprocessableEntityException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.PaymentMethodWrapper;
import pl.salonea.jaxrs.wrappers.ProviderWrapper;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by michzio on 13/09/2015.
 */
@Path("/payment-methods")
public class PaymentMethodResource {

    private static final Logger logger = Logger.getLogger(PaymentMethodResource.class.getName());

    @Inject
    private PaymentMethodFacade paymentMethodFacade;
    @Inject
    private ProviderFacade providerFacade;

    /**
     * Method returns all Payment Method resources
     * They can be additionally filtered or paginated by @QueryParams
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPaymentMethods( @BeanParam PaymentMethodBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Payment Methods by executing PaymentMethodResource.getPaymentMethods() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<PaymentMethod> paymentMethods = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            // get all payment methods filtered by criteria provided in query params
            paymentMethods = new ResourceList<>(
                    paymentMethodFacade.findByMultipleCriteria(params.getProviders(), params.getName(), params.getDescription(), params.getInAdvance(), params.getOffset(), params.getLimit())
            );

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all payment methods without filtering (eventually paginated)
            paymentMethods = new ResourceList<>( paymentMethodFacade.findAll(params.getOffset(), params.getLimit()) );

        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        PaymentMethodResource.populateWithHATEOASLinks(paymentMethods, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(paymentMethods).build();
    }

    @GET
    @Path("/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPaymentMethodsEagerly( @BeanParam PaymentMethodBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Payment Methods eagerly by executing PaymentMethodResource.getPaymentMethodsEagerly() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<PaymentMethodWrapper> paymentMethods = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            // get payment methods filtered by criteria provided in query params
            paymentMethods = new ResourceList<>(
                    PaymentMethodWrapper.wrap(
                            paymentMethodFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getName(),
                                    params.getDescription(), params.getInAdvance(), params.getOffset(), params.getLimit())
                    )
            );

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all payment methods without filtering (eventually paginated)
            paymentMethods = new ResourceList<>( PaymentMethodWrapper.wrap(paymentMethodFacade.findAllEagerly(params.getOffset(), params.getLimit())) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        PaymentMethodResource.populateWithHATEOASLinks(paymentMethods, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(paymentMethods).build();
    }

    /**
     * Method matches specific Payment Method resource by identifier and returns its instance
     */
    @GET
    @Path("/{paymentMethodId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPaymentMethod( @PathParam("paymentMethodId") Integer paymentMethodId,
                                      @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException  {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning given Payment Method by executing PaymentMethodResource.getPaymentMethod(paymentMethodId) method of REST API");

        PaymentMethod foundPaymentMethod = paymentMethodFacade.find(paymentMethodId);
        if(foundPaymentMethod == null)
            throw new NotFoundException("Could not find payment method for id " + paymentMethodId + ".");

        // adding hypermedia links to payment method resource
        PaymentMethodResource.populateWithHATEOASLinks(foundPaymentMethod, params.getUriInfo());

        return Response.status(Status.OK).entity(foundPaymentMethod).build();
    }

    /**
     * Method matches specific Payment Method resource by identifier and returns its instance fetching it eagerly
     */
    @GET
    @Path("/{paymentMethodId : \\d+}/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPaymentMethodEagerly( @PathParam("paymentMethodId") Integer paymentMethodId,
                                             @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning given Payment Method eagerly by executing PaymentMethodResource.getPaymentMethodEagerly(paymentMethodId) method of REST API");

        PaymentMethod foundPaymentMethod = paymentMethodFacade.findByIdEagerly(paymentMethodId);
        if(foundPaymentMethod == null)
            throw new NotFoundException("Could not find payment method for id " + paymentMethodId + ".");

        // wrapping Payment Method into PaymentMethodWrapper in order to marshall eagerly fetched associated collection of entities
        PaymentMethodWrapper wrappedPaymentMethod = new PaymentMethodWrapper(foundPaymentMethod);

        // adding hypermedia links to wrapped payment method resource
        PaymentMethodResource.populateWithHATEOASLinks(wrappedPaymentMethod, params.getUriInfo());

        return Response.status(Status.OK).entity(wrappedPaymentMethod).build();
    }

    /**
     * Method that takes PaymentMethod as XML or JSON and creates its new instance in database
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createPaymentMethod( PaymentMethod paymentMethod,
                                         @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "creating new PaymentMethod by executing PaymentMethodResource.createPaymentMethod(paymentMethod) method of REST API");

        PaymentMethod createdPaymentMethod = null;
        URI locationURI = null;

        try {
            // persist new resource in database
            createdPaymentMethod = paymentMethodFacade.create(paymentMethod);

            // populate created resource with hypermedia links
            PaymentMethodResource.populateWithHATEOASLinks(createdPaymentMethod, params.getUriInfo());

            // construct link to newly created resource to return in HTTP header
            String createdPaymentMethodId = String.valueOf(createdPaymentMethod.getId());
            locationURI = params.getUriInfo().getBaseUriBuilder().path(PaymentMethodResource.class).path(createdPaymentMethodId).build();

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_CREATION_ERROR_MESSAGE);
        }

        return Response.created(locationURI).entity(createdPaymentMethod).build();
    }

    /**
     * Method that takes updated Payment Method as XML or JSON and its ID as path param.
     * It updates Payment Method in database for provided ID.
     */
    @PUT
    @Path("/{paymentMethodId : \\d+}") // catch only numeric identifiers
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updatePaymentMethod( @PathParam("paymentMethodId") Integer paymentMethodId,
                                         PaymentMethod paymentMethod,
                                         @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "updating existing Payment Method by executing PaymentMethodResource.updatePaymentMethod(paymentMethodId, paymentMethod) method of REST API");

        // set resource ID passed in path param on updated resource object
        paymentMethod.setId(paymentMethodId);

        PaymentMethod updatedPaymentMethod = null;
        try {
            // reflect updated resource object in database
            updatedPaymentMethod = paymentMethodFacade.update(paymentMethod, true);
            // populate created resource with hypermedia links
            PaymentMethodResource.populateWithHATEOASLinks(updatedPaymentMethod, params.getUriInfo());

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_UPDATE_ERROR_MESSAGE);
        }

        return Response.status(Status.OK).entity(updatedPaymentMethod).build();
    }

    /**
     * Method that removes Payment Method entity from database for given ID.
     * The ID is passed through path param.
     */
    @DELETE
    @Path("/{paymentMethodId : \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removePaymentMethod( @PathParam("paymentMethodId") Integer paymentMethodId,
                                         @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing given Payment Method by executing PaymentMethodResource.removePaymentMethod(paymentMethodId) method of REST API");

        // find Payment Method entity that should be deleted
        PaymentMethod toDeletePaymentMethod = paymentMethodFacade.find(paymentMethodId);
        // throw exception if entity hasn't been found
        if(toDeletePaymentMethod == null)
            throw new NotFoundException("Could not find payment method to delete for given id " + paymentMethodId + ".");

        // remove entity from database
        paymentMethodFacade.remove(toDeletePaymentMethod);

        return Response.status(Status.NO_CONTENT).build();
    }

    /**
     * Additional methods returning a subset of resources based on given criteria
     * You can also achieve similar results by applying @QueryParams to generic method
     * returning all resources in order to filter and limit them.
     */

    /**
     * Method returns number of Payment Method entities in database
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countPaymentMethods( @BeanParam GenericBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning number of payment methods by executing PaymentMethodResource.countPaymentMethods() method of REST API");

        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(paymentMethodFacade.count()), 200, "number of payment methods");
        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method returns subset of Payment Method entities for given name.
     * The name is passed through path param.
     */
    @GET
    @Path("/named/{name : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPaymentMethodsByName( @PathParam("name") String name,
                                             @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning payment methods for given name using PaymentMethodResource.getPaymentMethodsByName(name) method of REST API");

        // find payment methods by given criteria
        ResourceList<PaymentMethod> paymentMethods = new ResourceList<>( paymentMethodFacade.findByName(name, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        PaymentMethodResource.populateWithHATEOASLinks(paymentMethods, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(paymentMethods).build();
    }

    /**
     * Method returns subset of Payment Method entities for given description.
     * The description is passed through path param.
     */
    @GET
    @Path("/described/{description : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPaymentMethodsByDescription( @PathParam("description") String description,
                                                    @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning payment methods for given description using PaymentMethodResource.getPaymentMethodsByDescription(description) method of REST API");

        // find payment methods by given criteria
        ResourceList<PaymentMethod> paymentMethods = new ResourceList<>( paymentMethodFacade.findByDescription(description, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        PaymentMethodResource.populateWithHATEOASLinks(paymentMethods, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(paymentMethods).build();
    }

    /**
     * Method returns subset of Payment Method entities for given keyword.
     * The keyword is passed through path param.
     */
    @GET
    @Path("/containing-keyword/{keyword : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPaymentMethodsByKeyword( @PathParam("keyword") String keyword,
                                                @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning payment methods for given keyword using PaymentMethodResource.getPaymentMethodsByKeyword(keyword) method of REST API");

        // find payment methods by given criteria
        ResourceList<PaymentMethod> paymentMethods = new ResourceList<>( paymentMethodFacade.findByKeyword(keyword, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        PaymentMethodResource.populateWithHATEOASLinks(paymentMethods, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(paymentMethods).build();
    }

    /**
     * Method returns subset of Payment Method entities paid in advance.
     */
    @GET
    @Path("/in-advance")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPaymentMethodsPaidInAdvance( @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning payment methods paid in advance using PaymentMethodResource.getPaymentMethodsPaidInAdvance() method of REST API");

        // find payment methods by given criteria
        ResourceList<PaymentMethod> paymentMethods = new ResourceList<>( paymentMethodFacade.findInAdvance(true, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        PaymentMethodResource.populateWithHATEOASLinks(paymentMethods, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(paymentMethods).build();
    }

    /**
     * Method returns subset of Payment Method entities paid upon completion. (on delivery)
     */
    @GET
    @Path("/on-delivery")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPaymentMethodsPaidOnDelivery( @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning payment methods paid on delivery using PaymentMethodResource.getPaymentMethodsPaidOnDelivery() method of REST API");

        // find payment methods by given criteria
        ResourceList<PaymentMethod> paymentMethods = new ResourceList<>( paymentMethodFacade.findInAdvance(false, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        PaymentMethodResource.populateWithHATEOASLinks(paymentMethods, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(paymentMethods).build();
    }

    /**
     * related subresources (through relationships)
     */

    @Path("/{paymentMethodId : \\d+}/providers")
    public ProviderResource getProviderResource() {
        return new ProviderResource();
    }

    // helper methods e.g. to populate resources/resource lists with HATEOAS links

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList paymentMethods, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(paymentMethods, uriInfo, offset, limit);

        try {
            // count resources hypermedia link
            Method countMethod = PaymentMethodResource.class.getMethod("countPaymentMethods", GenericBeanParam.class);
            paymentMethods.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(PaymentMethodResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            paymentMethods.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(PaymentMethodResource.class).build()).rel("payment-methods").build() );

            // get all resources eagerly hypermedia link
            Method paymentMethodsEagerlyMethod = PaymentMethodResource.class.getMethod("getPaymentMethodsEagerly", PaymentMethodBeanParam.class);
            paymentMethods.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(PaymentMethodResource.class)
                    .path(paymentMethodsEagerlyMethod)
                    .build())
                    .rel("payment-methods-eagerly").build());

            // get subset of resources hypermedia links
            // named
            paymentMethods.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(PaymentMethodResource.class).path("named").build()).rel("named").build() );

            // described
            paymentMethods.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(PaymentMethodResource.class).path("described").build()).rel("described").build() );

            // containing-keyword
            paymentMethods.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(PaymentMethodResource.class).path("containing-keyword").build()).rel("containing-keyword").build() );

            // in-advance
            paymentMethods.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(PaymentMethodResource.class).path("in-advance").build()).rel("in-advance").build() );

            // on-delivery
            paymentMethods.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(PaymentMethodResource.class).path("on-delivery").build()).rel("on-delivery").build() );


        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for(Object object : paymentMethods.getResources()) {
            if(object instanceof PaymentMethod) {
                PaymentMethodResource.populateWithHATEOASLinks((PaymentMethod) object, uriInfo);
            } else if(object instanceof PaymentMethodWrapper) {
                PaymentMethodResource.populateWithHATEOASLinks( (PaymentMethodWrapper) object, uriInfo);
            }
        }

    }

    /**
     * This method enables to populate each individual resource wrapper with hypermedia links
     */
    public static void populateWithHATEOASLinks(PaymentMethodWrapper paymentMethodWrapper, UriInfo uriInfo) {

        PaymentMethodResource.populateWithHATEOASLinks(paymentMethodWrapper.getPaymentMethod(), uriInfo);

        for(Provider provider : paymentMethodWrapper.getProviders())
            pl.salonea.jaxrs.ProviderResource.populateWithHATEOASLinks(provider, uriInfo);
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(PaymentMethod paymentMethod, UriInfo uriInfo) {

        // self link with pattern: http://localhost:port/app/rest/{resources}/{id}
        paymentMethod.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                            .path(PaymentMethodResource.class)
                                                            .path(paymentMethod.getId().toString())
                                                            .build())
                                            .rel("self").build() );

        // collection link with pattern: http://localhost:port/app/rest/{resources}
        paymentMethod.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                          .path(PaymentMethodResource.class)
                                                          .build())
                                          .rel("payment-methods").build() );

        try {
            // self eagerly link with pattern http://localhost:port/app/rest/{resources}/{id}/eagerly
            Method paymentMethodEagerlyMethod = PaymentMethodResource.class.getMethod("getPaymentMethodEagerly", Integer.class, GenericBeanParam.class);
            paymentMethod.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(PaymentMethodResource.class)
                    .path(paymentMethodEagerlyMethod)
                    .resolveTemplate("paymentMethodId", paymentMethod.getId().toString())
                    .build())
                    .rel("payment-method-eagerly").build());

            // associated collections links with pattern: http://localhost:port/app/rest/{resources}/{id}/{relationship}

            // providers
            Method providersMethod = PaymentMethodResource.class.getMethod("getProviderResource");
            paymentMethod.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(PaymentMethodResource.class)
                    .path(providersMethod)
                    .resolveTemplate("paymentMethodId", paymentMethod.getId().toString())
                    .build())
                    .rel("providers").build());

            // providers-eagerly
            Method providersEagerlyMethod = PaymentMethodResource.ProviderResource.class.getMethod("getPaymentMethodProvidersEagerly", Integer.class, ProviderBeanParam.class);
            paymentMethod.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(PaymentMethodResource.class)
                    .path(providersMethod)
                    .path(providersEagerlyMethod)
                    .resolveTemplate("paymentMethodId", paymentMethod.getId().toString())
                    .build())
                    .rel("providers-eagerly").build());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public class ProviderResource {

        public ProviderResource() { }

        /**
         * Method returns subset of Provider entities for given Payment Method entity.
         * The payment method id is passed through path param.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getPaymentMethodProviders( @PathParam("paymentMethodId") Integer paymentMethodId,
                                                   @BeanParam ProviderBeanParam params) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning providers for given payment method using " +
                    "PaymentMethodResource.ProviderResource.getPaymentMethodProviders(paymentMethodId) method of REST API");

            // find payment method entity for which to get associated providers
            PaymentMethod paymentMethod = paymentMethodFacade.find(paymentMethodId);
            if(paymentMethod == null)
                throw new NotFoundException("Could not find payment method for id " + paymentMethodId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<Provider> providers = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<PaymentMethod> paymentMethods = new ArrayList<>();
                paymentMethods.add(paymentMethod);

                // get providers for given payment method filtered by given params.
                providers = new ResourceList<>(
                        providerFacade.findByMultipleCriteria(params.getCorporations(), params.getProviderTypes(), params.getIndustries(), paymentMethods,
                                params.getServices(), params.getRated(), params.getMinAvgRating(), params.getMaxAvgRating(), params.getRatingClients(),
                                params.getProviderName(), params.getDescription(), params.getOffset(), params.getLimit())
                );
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get providers for given payment method without filtering
                providers = new ResourceList<>( providerFacade.findByPaymentMethod(paymentMethod, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providers).build();
        }

        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getPaymentMethodProvidersEagerly( @PathParam("paymentMethodId") Integer paymentMethodId,
                                                          @BeanParam ProviderBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subset of Provider entities for given Payment Method eagerly using " +
                    "PaymentMethodResource.ProviderResource.getPaymentMethodProviders(paymentMethodId) method of REST API");

            // find payment method entity for which to get associated providers
            PaymentMethod paymentMethod = paymentMethodFacade.find(paymentMethodId);
            if (paymentMethod == null)
                throw new NotFoundException("Could not find payment method for id " + paymentMethodId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ProviderWrapper> providers = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<PaymentMethod> paymentMethods = new ArrayList<>();
                paymentMethods.add(paymentMethod);

                // get providers for given payment method eagerly filtered by given params
                providers = new ResourceList<>(
                        ProviderWrapper.wrap(
                                providerFacade.findByMultipleCriteriaEagerly(params.getCorporations(), params.getProviderTypes(), params.getIndustries(), paymentMethods,
                                        params.getServices(), params.getRated(), params.getMinAvgRating(), params.getMaxAvgRating(), params.getRatingClients(),
                                        params.getProviderName(), params.getDescription(), params.getOffset(), params.getLimit())
                        )
                );
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get providers for given payment method eagerly without filtering (eventually paginated)
                providers = new ResourceList<>( ProviderWrapper.wrap(providerFacade.findByPaymentMethodEagerly(paymentMethod, params.getOffset(), params.getLimit())) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ProviderResource.populateWithHATEOASLinks(providers, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(providers).build();
        }
    }
}
