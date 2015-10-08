package pl.salonea.jaxrs;


import pl.salonea.entities.Provider;
import pl.salonea.entities.ProviderService;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.bean_params.PaginationBeanParam;
import pl.salonea.jaxrs.bean_params.ProviderServiceBeanParam;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.ProviderServiceWrapper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * Created by michzio on 12/09/2015.
 */
@Path("/provider-services")
public class ProviderServiceResource {

    private static final Logger logger = Logger.getLogger(ProviderServiceResource.class.getName());

    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countProviderServices(  @HeaderParam("authToken") String authToken ) throws ForbiddenException {

        if(authToken == null) throw new ForbiddenException("Unauthorized access to web service.");

        return null;
    }

    /**
     * This method enables to populate list of resources and each individual resource on list
     * with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList providerServices, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        if(offset != null && limit != null) {
            // self collection link
            providerServices.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", offset).queryParam("limit", limit).build()).rel("self").build() );
            // prev collection link
            Integer prevOffset = (offset - limit) < 0 ? 0 : offset - limit;
            Integer prevLimit = offset - prevOffset;
            if(prevLimit > 0)
                providerServices.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", prevOffset).queryParam("limit", prevLimit).build()).rel("prev").build() );
            else
                providerServices.getLinks().add( Link.fromUri("").rel("prev").build() );
            // next collection link
            providerServices.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", (offset+limit)).queryParam("limit", limit).build()).rel("next").build() );
        } else {
            providerServices.getLinks().add( Link.fromUri(uriInfo.getAbsolutePath()).rel("self").build() );
        }

        try {
            // count resources hypermedia link
            Method countMethod = ProviderServiceResource.class.getMethod("countProviderServices", String.class);
            providerServices.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderServiceResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            providerServices.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ProviderServiceResource.class).build()).rel("provider-services").build() );

            // TODO

        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        }

        for(Object object : providerServices.getResources()) {
            if(object instanceof ProviderService) {
                ProviderServiceResource.populateWithHATEOASLinks( (ProviderService) object, uriInfo);
            } else if(object instanceof ProviderServiceWrapper) {
                ProviderServiceResource.populateWithHATEOASLinks( (ProviderServiceWrapper) object, uriInfo);
            }
        }

    }

    /**
     * This method enables to populate each individual resource wrapper with hypermedia links
     */
    public static void populateWithHATEOASLinks(ProviderServiceWrapper providerServiceWrapper, UriInfo uriInfo) {

        ProviderServiceResource.populateWithHATEOASLinks(providerServiceWrapper.getProviderService(), uriInfo);

        // TODO
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(ProviderService providerService, UriInfo uriInfo) {

        try {
            // self link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/{sub-id}
            Method providerServicesMethod = ProviderResource.class.getMethod("getProviderServiceResource");
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerServicesMethod)
                    .path(providerService.getService().getServiceId().toString())
                    .resolveTemplate("userId", providerService.getProvider().getUserId().toString())
                    .build())
                    .rel("self").build());

            // self eagerly link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/{sub-id}/eagerly
            Method providerServiceEagerlyMethod = ProviderResource.ProviderServiceResource.class.getMethod("getProviderServiceEagerly", Long.class, Integer.class, GenericBeanParam.class);
            providerService.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerServicesMethod)
                    .path(providerServiceEagerlyMethod)
                    .resolveTemplate("userId", providerService.getProvider().getUserId().toString())
                    .resolveTemplate("serviceId", providerService.getService().getServiceId().toString())
                    .build())
                    .rel("provider-service-eagerly").build());

            // sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerServicesMethod)
                    .resolveTemplate("userId", providerService.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-provider-services").build());

            // sub-collection eagerly link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}
            Method providerServicesEagerlyMethod = ProviderResource.ProviderServiceResource.class.getMethod("getProviderServicesEagerly", Long.class, ProviderServiceBeanParam.class);
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerServicesMethod)
                    .path(providerServicesEagerlyMethod)
                    .resolveTemplate("userId", providerService.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-provider-services-eagerly").build());

            // sub-collection count link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/count
            Method countByProviderMethod = ProviderResource.ProviderServiceResource.class.getMethod("countProviderServicesByProvider", Long.class, GenericBeanParam.class);
            providerService.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerServicesMethod)
                    .path(countByProviderMethod)
                    .resolveTemplate("userId", providerService.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-provider-services-count").build());

            // categorized sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/categorized-in/{categoryId}
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerServicesMethod)
                    .path("categorized-in")
                    .resolveTemplate("userId", providerService.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-provider-services-categorized").build());

            // described sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/described-by/{description}
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerServicesMethod)
                    .path("described-by")
                    .resolveTemplate("userId", providerService.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-provider-services-described").build());

            // discounted sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/discounted-between?minDiscount={minDiscount}&maxDiscount={maxDiscount}
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerServicesMethod)
                    .path("discounted-between")
                    .resolveTemplate("userId", providerService.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-provider-services-discounted").build());

            // supplied-by-employee sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/supplied-by/{employeeId}
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(providerServicesMethod)
                    .path("supplied-by")
                    .resolveTemplate("userId", providerService.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-provider-services-supplied-by-employee").build());

            // collection link with pattern: http://localhost:port/app/rest/{resources}
            providerService.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderServiceResource.class)
                    .build())
                    .rel("provider-services").build());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }
}
