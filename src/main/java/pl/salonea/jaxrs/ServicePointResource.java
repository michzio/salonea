package pl.salonea.jaxrs;

import pl.salonea.entities.ServicePoint;
import pl.salonea.jaxrs.bean_params.*;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.ServicePointWrapper;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.ws.Response;
import java.lang.reflect.Method;

/**
 * Created by michzio on 12/09/2015.
 */

@Path("/service-points")
public class ServicePointResource {



    /**
     * Method returns number of Service Point entities in database
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countServicePoints( @HeaderParam("authToken") String authToken ) throws ForbiddenException {

        if(authToken == null) throw new ForbiddenException("Unauthorized access to web service.");

        return null;
    }

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList servicePoints, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        if(offset != null && limit != null) {
            // self collection link
            servicePoints.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", offset).queryParam("limit", limit).build()).rel("self").build() );
            // prev collection link
            Integer prevOffset = (offset - limit) < 0 ? 0 : offset - limit;
            Integer prevLimit = offset - prevOffset;
            if(prevLimit > 0)
                servicePoints.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", prevOffset).queryParam("limit", prevLimit).build()).rel("prev").build() );
            else
                servicePoints.getLinks().add( Link.fromUri("").rel("prev").build() );
            // next collection link
            servicePoints.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", (offset+limit)).queryParam("limit", limit).build()).rel("next").build() );
        } else {
            servicePoints.getLinks().add( Link.fromUri(uriInfo.getAbsolutePath()).rel("self").build() );
        }

        try {
            // count resources hypermedia link
            Method countMethod = ServicePointResource.class.getMethod("countServicePoints", String.class);
            servicePoints.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ServicePointResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            servicePoints.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ServicePointResource.class).build()).rel("service-points").build() );

            // TODO

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for(Object object : servicePoints.getResources()) {
            if(object instanceof ServicePoint) {
                ServicePointResource.populateWithHATEOASLinks((ServicePoint) object, uriInfo);
            }  else if(object instanceof ServicePointWrapper) {
                ServicePointResource.populateWithHATEOASLinks( (ServicePointWrapper) object, uriInfo);
            }
        }

    }

    /**
     * This method enables to populate each individual resource wrapper with hypermedia links
     */
    public static void populateWithHATEOASLinks(ServicePointWrapper servicePointWrapper, UriInfo uriInfo) {

        ServicePointResource.populateWithHATEOASLinks(servicePointWrapper.getServicePoint(), uriInfo);

       /* for(ServicePointPhoto photo : servicePointWrapper.getPhotos())
            pl.salonea.jaxrs.ServicePointPhotoResource.populateWithHATEOASLinks(photo, uriInfo);

        for(VirtualTour tour : servicePointWrapper.getVirtualTours())
            pl.salonea.jaxrs.VirtualTourResource.populateWithHATEOASLinks(tour, uriInfo);

        for(WorkStation workStation : servicePointWrapper.getWorkStations())
            pl.salonea.jaxrs.WorkStationResource.populateWithHATEOASLinks(workStation, uriInfo);  TODO
       */
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(ServicePoint servicePoint, UriInfo uriInfo) {

        try {
            // self link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/{sub-id}
            Method servicePointsMethod = ProviderResource.class.getMethod("getServicePointResource");
            servicePoint.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(servicePointsMethod)
                    .path(servicePoint.getServicePointNumber().toString())
                    .resolveTemplate("userId", servicePoint.getProvider().getUserId().toString())
                    .build())
                    .rel("self").build());

            // self eagerly link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/{sub-id}/eagerly
            Method servicePointEagerlyMethod = ProviderResource.ServicePointResource.class.getMethod("getServicePointEagerly", Long.class, Integer.class, GenericBeanParam.class);
            servicePoint.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(servicePointsMethod)
                    .path(servicePointEagerlyMethod)
                    .resolveTemplate("userId", servicePoint.getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", servicePoint.getServicePointNumber().toString())
                    .build())
                    .rel("service-point-eagerly").build());

            // sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}
            servicePoint.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(servicePointsMethod)
                    .resolveTemplate("userId", servicePoint.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-service-points").build());

            // sub-collection eagerly link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}
            Method servicePointsEagerlyMethod = ProviderResource.ServicePointResource.class.getMethod("getProviderServicePointsEagerly", Long.class, ServicePointBeanParam.class);
            servicePoint.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(servicePointsMethod)
                    .path(servicePointsEagerlyMethod)
                    .resolveTemplate("userId", servicePoint.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-service-points-eagerly").build());


            // sub-collection count link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/count
            Method countByProviderMethod = ProviderResource.ServicePointResource.class.getMethod("countServicePointsByProvider", Long.class, GenericBeanParam.class);
            servicePoint.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(servicePointsMethod)
                    .path(countByProviderMethod)
                    .resolveTemplate("userId", servicePoint.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-service-points-count").build());

            // address sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/address
            Method addressMethod = ProviderResource.ServicePointResource.class.getMethod("getProviderServicePointsByAddress", Long.class, AddressBeanParam.class);
            servicePoint.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(servicePointsMethod)
                    .path(addressMethod)
                    .resolveTemplate("userId", servicePoint.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-service-points-address").build());

            // coordinates square sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/coordinates-square
            Method coordinatesSquareMethod = ProviderResource.ServicePointResource.class.getMethod("getProviderServicePointsByCoordinatesSquare", Long.class, CoordinatesSquareBeanParam.class);
            servicePoint.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(servicePointsMethod)
                    .path(coordinatesSquareMethod)
                    .resolveTemplate("userId", servicePoint.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-service-points-coordinates-square").build());

            // coordinates circle sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/coordinates-circle
            Method coordinatesCircleMethod = ProviderResource.ServicePointResource.class.getMethod("getProviderServicePointsByCoordinatesCircle", Long.class, CoordinatesCircleBeanParam.class);
            servicePoint.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ProviderResource.class)
                    .path(servicePointsMethod)
                    .path(coordinatesCircleMethod)
                    .resolveTemplate("userId", servicePoint.getProvider().getUserId().toString())
                    .build())
                    .rel("provider-service-points-coordinates-circle").build());

            // collection link with pattern: http://localhost:port/app/rest/{resources}
            servicePoint.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServicePointResource.class)
                    .build())
                    .rel("service-points").build());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }
}
