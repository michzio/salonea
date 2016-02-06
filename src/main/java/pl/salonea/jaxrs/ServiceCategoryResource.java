package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.ServiceCategoryFacade;
import pl.salonea.ejb.stateless.ServiceFacade;
import pl.salonea.entities.Service;
import pl.salonea.entities.ServiceCategory;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.bean_params.PaginationBeanParam;
import pl.salonea.jaxrs.bean_params.ServiceBeanParam;
import pl.salonea.jaxrs.bean_params.ServiceCategoryBeanParam;
import pl.salonea.jaxrs.exceptions.*;
import pl.salonea.jaxrs.exceptions.BadRequestException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.ServiceCategoryWrapper;
import pl.salonea.jaxrs.wrappers.ServiceWrapper;

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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by michzio on 03/02/2016.
 */
@Path("/service-categories")
public class ServiceCategoryResource {

    private static final Logger logger = Logger.getLogger(ServiceCategoryResource.class.getName());

    @Inject
    private UserTransaction utx;

    @Inject
    private ServiceCategoryFacade serviceCategoryFacade;
    @Inject
    private ServiceFacade serviceFacade;

    /**
     * Method returns all Service Category resources
     * They can be additionally filtered or paginated by @QueryParams.
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getServiceCategories( @BeanParam ServiceCategoryBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Service Categories by executing ServiceCategoryResource.getServiceCategories() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<ServiceCategory> serviceCategories = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            // get all service categories filtered by given query params
            if( RESTToolkit.isSet(params.getKeywords()) ) {
                if( RESTToolkit.isSet(params.getNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                    throw new BadRequestException("Query params cannot include keywords and category names or descriptions at the same time.");

                // find only by keywords
                serviceCategories = new ResourceList<>(
                        serviceCategoryFacade.findByMultipleCriteria(params.getKeywords(), params.getSuperCategories(),
                                params.getOffset(), params.getLimit())
                );
            } else {
                // find by category names and descriptions
                serviceCategories = new ResourceList<>(
                        serviceCategoryFacade.findByMultipleCriteria(params.getNames(), params.getDescriptions(),
                                params.getSuperCategories(), params.getOffset(), params.getLimit())
                );
            }
        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all service categories without filtering (eventually paginated)
            serviceCategories = new ResourceList<>( serviceCategoryFacade.findAll(params.getOffset(), params.getLimit()) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        ServiceCategoryResource.populateWithHATEOASLinks(serviceCategories, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(serviceCategories).build();
    }

    @GET
    @Path("/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getServiceCategoriesEagerly( @BeanParam ServiceCategoryBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Service Categories eagerly by executing ServiceCategoryResource.getServiceCategoriesEagerly() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<ServiceCategoryWrapper> serviceCategories = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            // get all service categories eagerly filtered by given query params

            if( RESTToolkit.isSet(params.getKeywords()) ) {
                if(RESTToolkit.isSet(params.getNames()) || RESTToolkit.isSet(params.getDescriptions()))
                    throw new BadRequestException("Query params cannot include keywords and category names or descriptions at the same time.");

                // find only by keywords
                serviceCategories = new ResourceList<>(
                        ServiceCategoryWrapper.wrap(
                                serviceCategoryFacade.findByMultipleCriteriaEagerly(params.getKeywords(),
                                        params.getSuperCategories(), params.getOffset(), params.getLimit())
                        )
                );
            } else {
                // find by category names and descriptions
                serviceCategories = new ResourceList<>(
                        ServiceCategoryWrapper.wrap(
                                serviceCategoryFacade.findByMultipleCriteriaEagerly(params.getNames(),
                                        params.getDescriptions(), params.getSuperCategories(), params.getOffset(),
                                        params.getLimit())
                        )
                );
            }
        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all service categories eagerly without filtering (eventually paginated)
            serviceCategories = new ResourceList<>( ServiceCategoryWrapper.wrap(serviceCategoryFacade.findAllEagerly(params.getOffset(), params.getLimit())) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        ServiceCategoryResource.populateWithHATEOASLinks(serviceCategories, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(serviceCategories).build();
    }

    /**
     * Method matches specific Service Category resource by identifier and returns its instance.
     */
    @GET
    @Path("/{serviceCategoryId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getServiceCategory( @PathParam("serviceCategoryId") Integer serviceCategoryId,
                                        @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning given Service Category by executing ServiceCategoryResource.getServiceCategory(serviceCategoryId) method of REST API");

        ServiceCategory foundServiceCategory = serviceCategoryFacade.find(serviceCategoryId);
        if(foundServiceCategory == null)
            throw new NotFoundException("Could not find service category for id " + serviceCategoryId + ".");

        // adding hypermedia links to service category resource
        ServiceCategoryResource.populateWithHATEOASLinks(foundServiceCategory, params.getUriInfo());

        return Response.status(Status.OK).entity(foundServiceCategory).build();
    }

    /**
     * Method matches specific Service Category resource by identifier and returns its instance fetching it eagerly
     */
    @GET
    @Path("/{serviceCategoryId : \\d+}/eagerly") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getServiceCategoryEagerly( @PathParam("serviceCategoryId") Integer serviceCategoryId,
                                               @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning given Service Category eagerly by executing " +
                "ServiceCategoryResource.getServiceCategoryEagerly(serviceCategoryId) method of REST API");

        ServiceCategory foundServiceCategory = serviceCategoryFacade.findByIdEagerly(serviceCategoryId);
        if(foundServiceCategory == null)
            throw new NotFoundException("Could not find service category for id " + serviceCategoryId + ".");

        // wrapping ServiceCategory into ServiceCategoryWrapper in order to marshall eagerly fetched associated collections of entities
        ServiceCategoryWrapper wrappedServiceCategory = new ServiceCategoryWrapper(foundServiceCategory);

        // adding hypermedia links to wrapped service category resource
        ServiceCategoryResource.populateWithHATEOASLinks(wrappedServiceCategory, params.getUriInfo());

        return Response.status(Status.OK).entity(wrappedServiceCategory).build();
    }

    /**
     * Method that takes Service Category as XML or JSON and creates its new instance in database
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createServiceCategory( ServiceCategory serviceCategory,
                                           @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "creating new Service Category by executing ServiceCategoryResource.createServiceCategory(serviceCategory) method of REST API");

        ServiceCategory createdServiceCategory = null;
        URI locationURI = null;

        try {
            // persist new resource in database
            createdServiceCategory = serviceCategoryFacade.create(serviceCategory);

            // populate created resource with hypermedia links
            ServiceCategoryResource.populateWithHATEOASLinks(createdServiceCategory, params.getUriInfo());

            // construct link to newly created resource to return in HTTP Header
            String createdServiceCategoryId = String.valueOf(createdServiceCategory.getCategoryId());
            locationURI = params.getUriInfo().getBaseUriBuilder()
                    .path(ServiceCategoryResource.class).path(createdServiceCategoryId).build();

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_CREATION_ERROR_MESSAGE);
        }

        return Response.created(locationURI).entity(createdServiceCategory).build();
    }

    /**
     * Method that takes updated Service Category as XML or JSON and its ID as path param.
     * It updates Service Category in database for provided ID.
     */
    @PUT
    @Path("/{serviceCategoryId : \\d+}") // catch only numeric identifiers
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateServiceCategory( @PathParam("serviceCategoryId") Integer serviceCategoryId,
                                           ServiceCategory serviceCategory,
                                           @BeanParam GenericBeanParam params ) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "updating existing Service Category by executing " +
                "ServiceCategoryResource.updateServiceCategory(serviceCategoryId, serviceCategory) method of REST API");

        // set resource ID passed in path param on updated resource object
        serviceCategory.setCategoryId(serviceCategoryId);

        ServiceCategory updatedServiceCategory = null;
        try {
            // reflect updated resource object in database
            updatedServiceCategory = serviceCategoryFacade.update(serviceCategory, true);
            // populate created resource with hypermedia links
            ServiceCategoryResource.populateWithHATEOASLinks(updatedServiceCategory, params.getUriInfo());

        } catch (EJBTransactionRolledbackException ex) {
            ExceptionHandler.handleEJBTransactionRolledbackException(ex);
        } catch (EJBException ex) {
            ExceptionHandler.handleEJBException(ex);
        } catch (Exception ex) {
            throw new InternalServerErrorException(ExceptionHandler.ENTITY_UPDATE_ERROR_MESSAGE);
        }

        return Response.status(Status.OK).entity(updatedServiceCategory).build();
    }

    /**
     * Method that removes Service Category entity from database for given ID.
     * The ID is passed through path param.
     */
    @DELETE
    @Path("/{serviceCategoryId : \\d+}") // catch only numeric identifiers
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeServiceCategory( @PathParam("serviceCategoryId") Integer serviceCategoryId,
                                           @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing given Service Category by executing " +
                "ServiceCategoryResource.removeServiceCategory(serviceCategoryId) method of REST API");

        // find Service Category entity that should be deleted
        ServiceCategory toDeleteServiceCategory = serviceCategoryFacade.find(serviceCategoryId);
        // throw exception if entity hasn't been found
        if(toDeleteServiceCategory == null)
            throw new NotFoundException("Could not find service category to delete for given id: " + serviceCategoryId + ".");

        // remove entity from database
        serviceCategoryFacade.remove(toDeleteServiceCategory);

        return Response.status(Status.NO_CONTENT).build();
    }

    /**
     * Additional methods returning a subset of resources based on given criteria.
     * You can also achieve similar results by applying @QueryParams to generic method
     * returning all resources in order to filter and limit them.
     */

    /**
     * Method returns number of Service Category entities in database
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countServiceCategories( @BeanParam GenericBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning number of service categories by executing " +
                "ServiceCategoryResource.countServiceCategories() method of REST API");

        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(serviceCategoryFacade.count()),
                200, "number of service categories");
        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method returns subset of Service Category entities for given category name.
     * The category name is passed through path param.
     */
    @GET
    @Path("/named/{categoryName : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getServiceCategoriesByName( @PathParam("categoryName") String categoryName,
                                                @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning service categories for given category name using " +
                "ServiceCategoryResource.getServiceCategoriesByName(categoryName) method of REST API");

        // find service categories by given criteria (category name)
        ResourceList<ServiceCategory> serviceCategories = new ResourceList<>( serviceCategoryFacade.findByName(categoryName, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        ServiceCategoryResource.populateWithHATEOASLinks(serviceCategories, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(serviceCategories).build();
    }

    /**
     * Method returns subset of Service Category entities for given description.
     * The description is passed through path param.
     */
    @GET
    @Path("/described/{description : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getServiceCategoriesByDescription( @PathParam("description") String description,
                                                       @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning service categories for given description using " +
                "ServiceCategoryResource.getServiceCategoriesByDescription(description) method of REST API");

        // find service categories by given criteria (description)
        ResourceList<ServiceCategory> serviceCategories = new ResourceList<>( serviceCategoryFacade.findByDescription(description, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        ServiceCategoryResource.populateWithHATEOASLinks(serviceCategories, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(serviceCategories).build();
    }

    /**
     * Method returns subset of Service Category entities for given keyword.
     * The keyword is passed through path param.
     */
    @GET
    @Path("/containing-keyword/{keyword : \\S+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getServiceCategoriesByKeyword( @PathParam("keyword") String keyword,
                                                   @BeanParam PaginationBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning service categories for given keyword using " +
                "ServiceCategoryResource.getServiceCategoriesByKeyword(keyword) method of REST API");

        // find service categories by given criteria (keyword)
        ResourceList<ServiceCategory> serviceCategories = new ResourceList<>( serviceCategoryFacade.findByKeyword(keyword, params.getOffset(), params.getLimit()) );

        // result resources need to be populated with hypermedia links to enable resource discovery
        ServiceCategoryResource.populateWithHATEOASLinks(serviceCategories, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(serviceCategories).build();
    }

    /**
     * related subresources (through relationships)
     */

    @Path("/{serviceCategoryId : \\d+}/subcategories")
    public SubCategoryResource getSubCategoryResource() {
        return new SubCategoryResource();
    }

    @Path("/{serviceCategoryId : \\d+}/services")
    public ServiceResource getServiceResource() {
        return new ServiceResource();
    }

    // helper methods e.g. to populate resources/resource lists with HATEOAS links

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList serviceCategories, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(serviceCategories, uriInfo, offset, limit);

        try {
            // count resources hypermedia link
            Method countMethod = ServiceCategoryResource.class.getMethod("countServiceCategories", GenericBeanParam.class);
            serviceCategories.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ServiceCategoryResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            serviceCategories.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ServiceCategoryResource.class).build()).rel("service-categories").build() );

            // get all resources eagerly hypermedia link
            Method serviceCategoriesEagerlyMethod = ServiceCategoryResource.class.getMethod("getServiceCategoriesEagerly", ServiceCategoryBeanParam.class);
            serviceCategories.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ServiceCategoryResource.class).path(serviceCategoriesEagerlyMethod).build()).rel("service-categories-eagerly").build() );

            // get subset of resources hypermedia links

            // named
            serviceCategories.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                            .path(ServiceCategoryResource.class)
                            .path("named")
                            .build())
                            .rel("named").build() );

            // described
            serviceCategories.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                            .path(ServiceCategoryResource.class)
                            .path("described")
                            .build())
                            .rel("described").build() );

            // containing-keyword
            serviceCategories.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                            .path(ServiceCategoryResource.class)
                            .path("containing-keyword")
                            .build())
                            .rel("containing-keyword").build() );

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for(Object object : serviceCategories.getResources()) {
            if(object instanceof ServiceCategory) {
                ServiceCategoryResource.populateWithHATEOASLinks( (ServiceCategory) object, uriInfo);
            } else if(object instanceof ServiceCategoryWrapper) {
                ServiceCategoryResource.populateWithHATEOASLinks( (ServiceCategoryWrapper) object, uriInfo);
            }
        }
    }

    /**
     * This method enables to populate each individual resource wrapper with hypermedia links
     */
    public static void populateWithHATEOASLinks(ServiceCategoryWrapper serviceCategoryWrapper, UriInfo uriInfo) {

        ServiceCategoryResource.populateWithHATEOASLinks(serviceCategoryWrapper.getServiceCategory(), uriInfo);

        for(ServiceCategory subCategory : serviceCategoryWrapper.getSubCategories())
            pl.salonea.jaxrs.ServiceCategoryResource.populateWithHATEOASLinks(subCategory, uriInfo);

        for(Service service : serviceCategoryWrapper.getServices())
            pl.salonea.jaxrs.ServiceResource.populateWithHATEOASLinks(service, uriInfo);
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(ServiceCategory serviceCategory, UriInfo uriInfo) {

        // self link with pattern: http://localhost:port/app/rest/{resources}/{id}
        serviceCategory.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                            .path(ServiceCategoryResource.class)
                                                            .path(serviceCategory.getCategoryId().toString())
                                                            .build())
                                            .rel("self").build() );

        // collection link with pattern: http://localhost:port/app/rest/{resources}
        serviceCategory.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                            .path(ServiceCategoryResource.class)
                                                            .build())
                                            .rel("service-categories").build() );

        try {
            // self eagerly link with pattern: http://localhost:port/app/rest/{resources}/{id}/eagerly
            Method serviceCategoryEagerlyMethod = ServiceCategoryResource.class.getMethod("getServiceCategoryEagerly", Integer.class, GenericBeanParam.class);
            serviceCategory.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                                .path(ServiceCategoryResource.class)
                                                                .path(serviceCategoryEagerlyMethod)
                                                                .resolveTemplate("serviceCategoryId", serviceCategory.getCategoryId().toString())
                                                                .build())
                                                .rel("service-category-eagerly").build() );

            // associated collections links with pattern: http://localhost:port/app/rest/{resources}/{id}/{relationship}

            /**
             * SubCategories belonging to current Service Category resource
             */

            // subcategories relationship
            Method subcategoriesMethod = ServiceCategoryResource.class.getMethod("getSubCategoryResource");
            serviceCategory.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                            .path(ServiceCategoryResource.class)
                            .path(subcategoriesMethod)
                            .resolveTemplate("serviceCategoryId", serviceCategory.getCategoryId().toString())
                            .build())
                            .rel("subcategories").build() );

            // subcategories eagerly relationship
            Method subcategoriesEagerlyMethod = ServiceCategoryResource.SubCategoryResource.class.getMethod("getServiceCategorySubCategoriesEagerly", Integer.class, ServiceCategoryBeanParam.class);
            serviceCategory.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                            .path(ServiceCategoryResource.class)
                            .path(subcategoriesMethod)
                            .path(subcategoriesEagerlyMethod)
                            .resolveTemplate("serviceCategoryId", serviceCategory.getCategoryId().toString())
                            .build())
                            .rel("subcategories-eagerly").build());

            // subcategories count
            Method countSubcategoriesByServiceCategoryMethod = ServiceCategoryResource.SubCategoryResource.class.getMethod("countSubCategoriesByServiceCategory", Integer.class, GenericBeanParam.class);
            serviceCategory.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                            .path(ServiceCategoryResource.class)
                            .path(subcategoriesMethod)
                            .path(countSubcategoriesByServiceCategoryMethod)
                            .resolveTemplate("serviceCategoryId", serviceCategory.getCategoryId().toString())
                            .build())
                            .rel("subcategories-count").build() );

            /**
             * Services belonging to current Service Category resource
             */

            // services relationship
            Method servicesMethod = ServiceCategoryResource.class.getMethod("getServiceResource");
            serviceCategory.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                            .path(ServiceCategoryResource.class)
                            .path(servicesMethod)
                            .resolveTemplate("serviceCategoryId", serviceCategory.getCategoryId().toString())
                            .build())
                            .rel("services").build() );

            // services eagerly relationship
            Method servicesEagerlyMethod = ServiceCategoryResource.ServiceResource.class.getMethod("getServiceCategoryServicesEagerly", Integer.class, ServiceBeanParam.class);
            serviceCategory.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                            .path(ServiceCategoryResource.class)
                            .path(servicesMethod)
                            .path(servicesEagerlyMethod)
                            .resolveTemplate("serviceCategoryId", serviceCategory.getCategoryId().toString())
                            .build())
                            .rel("services-eagerly").build() );

            // services count
            Method countServicesByServiceCategoryMethod = ServiceCategoryResource.ServiceResource.class.getMethod("countServicesByServiceCategory", Integer.class, GenericBeanParam.class);
            serviceCategory.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                            .path(ServiceCategoryResource.class)
                            .path(servicesMethod)
                            .path(countServicesByServiceCategoryMethod)
                            .resolveTemplate("serviceCategoryId", serviceCategory.getCategoryId().toString())
                            .build())
                            .rel("services-count").build() );


        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public class SubCategoryResource {

        public SubCategoryResource() { }

        /**
         * Method returns subset of SubCategory (ServiceCategory) entities for given Service Category entity.
         * The service category id is passed through path param.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceCategorySubCategories( @PathParam("serviceCategoryId") Integer serviceCategoryId,
                                                         @BeanParam ServiceCategoryBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subcategories for given service category using " +
                    "ServiceCategoryResource.SubCategoryResource.getServiceCategorySubCategories(serviceCategoryId) method of REST API");

            // find service category entity for which to get associated subcategories (service category)
            ServiceCategory serviceCategory = serviceCategoryFacade.find(serviceCategoryId);
            if(serviceCategory == null)
                throw new NotFoundException("Could not find service category for id " + serviceCategoryId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ServiceCategory> subCategories = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<ServiceCategory> serviceCategories = new ArrayList<>();
                serviceCategories.add(serviceCategory);

                // get subcategories for given service category filtered by given query params

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                        throw new BadRequestException("Query params cannot include keywords and category names or descriptions at the same time.");

                    // find only by keywords
                    subCategories = new ResourceList<>(
                            serviceCategoryFacade.findByMultipleCriteria(params.getKeywords(), serviceCategories,
                                    params.getOffset(), params.getLimit())
                    );
                } else {
                    // find by category names, descriptions
                    subCategories = new ResourceList<>(
                            serviceCategoryFacade.findByMultipleCriteria(params.getNames(), params.getDescriptions(), serviceCategories,
                                    params.getOffset(), params.getLimit())
                    );
                }
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get subcategories for given service category without filtering (eventually paginated)
                subCategories = new ResourceList<>( serviceCategoryFacade.findBySuperCategory(serviceCategory, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServiceCategoryResource.populateWithHATEOASLinks(subCategories, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(subCategories).build();
        }

        /**
         * Method returns subset of SubCategory (ServiceCategory) entities for given
         * Service Category fetching them eagerly.
         * The service category id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceCategorySubCategoriesEagerly( @PathParam("serviceCategoryId") Integer serviceCategoryId,
                                                                @BeanParam ServiceCategoryBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning subcategories eagerly for given service category using " +
                    "ServiceCategoryResource.SubCategoryResource.getServiceCategorySubCategoriesEagerly(serviceCategoryId) method of REST API");

            // find service category entity for which to get associated subcategories (service category)
            ServiceCategory serviceCategory = serviceCategoryFacade.find(serviceCategoryId);
            if(serviceCategory == null)
                throw new NotFoundException("Could not find service category for id " + serviceCategoryId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ServiceCategoryWrapper> subCategories = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<ServiceCategory> serviceCategories = new ArrayList<>();
                serviceCategories.add(serviceCategory);

                // get subcategories eagerly for given service category filtered by given params

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                        throw new BadRequestException("Query params cannot include keywords and category names or descriptions at the same time.");

                    // find only by keywords
                    subCategories = new ResourceList<>(
                            ServiceCategoryWrapper.wrap(
                                    serviceCategoryFacade.findByMultipleCriteriaEagerly(params.getKeywords(), serviceCategories,
                                            params.getOffset(), params.getLimit())
                            )
                    );
                } else {
                    // find by category names, descriptions
                    subCategories = new ResourceList<>(
                            ServiceCategoryWrapper.wrap(
                                    serviceCategoryFacade.findByMultipleCriteriaEagerly(params.getNames(), params.getDescriptions(),
                                            serviceCategories, params.getOffset(), params.getLimit())
                            )
                    );
                }
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get subcategories eagerly for given service category without filtering (eventually paginated)
                subCategories = new ResourceList<>( ServiceCategoryWrapper.wrap(serviceCategoryFacade.findBySuperCategoryEagerly(serviceCategory, params.getOffset(), params.getLimit())) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServiceCategoryResource.populateWithHATEOASLinks(subCategories, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(subCategories).build();
        }

        /**
         * Method that counts SubCategory (ServiceCategory) entities for given Service Category resource.
         * The service category id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countSubCategoriesByServiceCategory( @PathParam("serviceCategoryId") Integer serviceCategoryId,
                                                             @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of subcategories for given service category by executing " +
                    "ServiceCategoryResource.SubCategoryResource.countSubCategoriesByServiceCategory(serviceCategoryId) method of REST API");

            // find service category entity for which to count subcategories (service category)
            ServiceCategory serviceCategory = serviceCategoryFacade.find(serviceCategoryId);
            if(serviceCategory == null)
                throw new NotFoundException("Could not find service category for id " + serviceCategoryId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(serviceCategoryFacade.countBySuperCategory(serviceCategory)),
                    200, "number of subcategories for service category with id " + serviceCategory.getCategoryId() );
            return Response.status(Status.OK).entity(responseEntity).build();
        }

        // TODO named, described, keyworded
    }

    public class ServiceResource {

        public ServiceResource() { }

        /**
         * Method returns subset of Service entities for given Service Category entity.
         * The service category id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceCategoryServices( @PathParam("serviceCategoryId") Integer serviceCategoryId,
                                                    @BeanParam ServiceBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning services for given service category using " +
                    "ServiceCategoryResource.ServiceResource.getServiceCategoryServices(serviceCategoryId) method of REST API");

            // find service category entity for which to get associated services
            ServiceCategory serviceCategory = serviceCategoryFacade.find(serviceCategoryId);
            if(serviceCategory == null)
                throw new NotFoundException("Could not find service category for id " + serviceCategoryId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<Service> services = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<ServiceCategory> serviceCategories = new ArrayList<>();
                serviceCategories.add(serviceCategory);

                // get services for given service category filtered by given query params

                utx.begin();

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                        throw new BadRequestException("Query params cannot include keywords and names or descriptions at the same time.");

                    // find only by keywords
                    services = new ResourceList<>(
                            serviceFacade.findByMultipleCriteria(params.getKeywords(), serviceCategories, params.getProviders(),
                                    params.getEmployees(), params.getWorkStations(), params.getServicePoints(), params.getOffset(), params.getLimit())
                    );
                } else {
                    // find by names, descriptions
                    services = new ResourceList<>(
                            serviceFacade.findByMultipleCriteria(params.getNames(), params.getDescriptions(), serviceCategories,
                                    params.getProviders(), params.getEmployees(), params.getWorkStations(), params.getServicePoints(),
                                    params.getOffset(), params.getLimit())
                    );
                }

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get services for given service category without filtering (eventually paginated)
                services = new ResourceList<>( serviceFacade.findByCategory(serviceCategory, params.getOffset(), params.getLimit()) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServiceResource.populateWithHATEOASLinks(services, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(services).build();
        }

        /**
         * Method returns subset of Service entities for given Service Category fetching them eagerly.
         * The service category id is passed through path param.
         * They can be additionally filtered and paginated by @QueryParams.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServiceCategoryServicesEagerly( @PathParam("serviceCategoryId") Integer serviceCategoryId,
                                                           @BeanParam ServiceBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning services eagerly for given service category using " +
                    "ServiceCategoryResource.ServiceResource.getServiceCategoryServicesEagerly(serviceCategoryId) method of REST API");

            // find service category entity for which to get associated services
            ServiceCategory serviceCategory = serviceCategoryFacade.find(serviceCategoryId);
            if(serviceCategory == null)
                throw new NotFoundException("Could not find service category for id " + serviceCategoryId + ".");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ServiceWrapper> services = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<ServiceCategory> serviceCategories = new ArrayList<>();
                serviceCategories.add(serviceCategory);

                // get services eagerly for given service category filtered by given query params

                utx.begin();

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                        throw new BadRequestException("Query params cannot include keywords and names or descriptions at the same time.");

                    // find only by keywords
                    services = new ResourceList<>(
                            ServiceWrapper.wrap(
                                    serviceFacade.findByMultipleCriteriaEagerly(params.getKeywords(), serviceCategories, params.getProviders(),
                                            params.getEmployees(), params.getWorkStations(), params.getServicePoints(), params.getOffset(), params.getLimit())
                            )
                    );
                } else {
                    // find by names, descriptions
                    services = new ResourceList<>(
                            ServiceWrapper.wrap(
                                    serviceFacade.findByMultipleCriteriaEagerly(params.getNames(), params.getDescriptions(), serviceCategories,
                                            params.getProviders(), params.getEmployees(), params.getWorkStations(), params.getServicePoints(),
                                            params.getOffset(), params.getLimit())
                            )
                    );
                }

                utx.commit();

            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get services eagerly for given service category without filtering (eventually paginated)
                services = new ResourceList<>( ServiceWrapper.wrap(serviceFacade.findByCategoryEagerly(serviceCategory, params.getOffset(), params.getLimit())) );
            }

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServiceResource.populateWithHATEOASLinks(services, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(services).build();
        }

        /**
         * Method that counts Service entities for given Service Category resource.
         * The service category id is passed through path param.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countServicesByServiceCategory( @PathParam("serviceCategoryId") Integer serviceCategoryId,
                                                        @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of services for given service category by executing " +
                    "ServiceCategoryResource.ServiceResource.countServicesByServiceCategory(serviceCategoryId) method of REST API");

            // find service category entity for which to count services
            ServiceCategory serviceCategory = serviceCategoryFacade.find(serviceCategoryId);
            if(serviceCategory == null)
                throw new NotFoundException("Could not find service category for id " + serviceCategoryId + ".");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(serviceFacade.countByCategory(serviceCategory)),
                    200, "number of services for service category with id " + serviceCategory.getCategoryId());
            return Response.status(Status.OK).entity(responseEntity).build();
        }

        // TODO named, described, keyworded
    }
}
