package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.ServiceCategoryFacade;
import pl.salonea.ejb.stateless.ServiceFacade;
import pl.salonea.entities.Service;
import pl.salonea.entities.ServiceCategory;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.bean_params.ServiceCategoryBeanParam;
import pl.salonea.jaxrs.exceptions.BadRequestException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.ServiceCategoryWrapper;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
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

            // subcategories eagerly relationship

            // subcategories count

            /**
             * Services belonging to current Service Category resource
             */

            // services relationship

            // services eagerly relationship

            // services count

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
