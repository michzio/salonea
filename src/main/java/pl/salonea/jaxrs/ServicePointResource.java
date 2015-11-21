package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.ServicePointFacade;
import pl.salonea.entities.ServicePoint;
import pl.salonea.jaxrs.bean_params.*;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.ServicePointWrapper;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.exceptions.BadRequestException;
import javax.ws.rs.core.Response.Status;

/**
 * Created by michzio on 12/09/2015.
 */

@Path("/service-points")
public class ServicePointResource {

    private static final Logger logger = Logger.getLogger(ServicePointResource.class.getName());

    @Inject
    private ServicePointFacade servicePointFacade;

    /**
     * Method returns all Service Point entities
     * They can be additionally filtered and paginated by @QueryParam
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getServicePoints( @BeanParam ServicePointBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Service Points by executing ServicePointResource.getServicePoints() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<ServicePoint> servicePoints = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            // get service points filtered by criteria provided in query params
            if(params.getAddress() != null) {
                if(params.getCoordinatesSquare() != null || params.getCoordinatesCircle() != null)
                    throw new BadRequestException("Query params cannot include address params and coordinate square params or coordinate circle params at the same time.");
                // only address params
                servicePoints = new ResourceList<>(
                        servicePointFacade.findByMultipleCriteria(params.getProviders(), params.getServices(), params.getEmployees(),
                                params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getAddress(), params.getOffset(), params.getLimit())
                );

            } else if(params.getCoordinatesSquare() != null) {
                if(params.getAddress() != null || params.getCoordinatesCircle() != null)
                    throw new BadRequestException("Query params cannot include coordinates square params and address params or coordinates circle params at the same time.");
                // only coordinates square params
                servicePoints = new ResourceList<>(
                        servicePointFacade.findByMultipleCriteria(params.getProviders(), params.getServices(), params.getEmployees(),
                                params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesSquare(), params.getOffset(), params.getLimit())
                );

            } else if(params.getCoordinatesCircle() != null) {
                if(params.getAddress() != null || params.getCoordinatesSquare() != null)
                    throw new BadRequestException("Query params cannot include coordinates circle params and address params or coordinates square params at the same time.");
                // only coordinates circle params
                servicePoints = new ResourceList<>(
                        servicePointFacade.findByMultipleCriteria(params.getProviders(), params.getServices(), params.getEmployees(),
                                params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesCircle(),params.getOffset(), params.getLimit())
                );

            } else {
                // no location params
                servicePoints = new ResourceList<>(
                        servicePointFacade.findByMultipleCriteria(params.getProviders(), params.getServices(), params.getEmployees(),
                                params.getCorporations(), params.getIndustries(), params.getServiceCategories(),params.getOffset(), params.getLimit())
                );
            }
        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all service points without filtering (eventually paginated)
            servicePoints = new ResourceList<>( servicePointFacade.findAll(params.getOffset(), params.getLimit()) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(servicePoints).build();
    }

    @GET
    @Path("/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getServicePointsEagerly( @BeanParam ServicePointBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Service Points eagerly by executing ServicePointResource.getServicePointsEagerly() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<ServicePointWrapper> servicePoints = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            // get service points filtered by criteria provided in query params
            if(params.getAddress() != null) {
                if(params.getCoordinatesSquare() != null || params.getCoordinatesCircle() != null)
                    throw new BadRequestException("Query params cannot include address params and coordinates square params or coordinates circle params at the same time.");
                // only address params
                servicePoints = new ResourceList<>(
                        ServicePointWrapper.wrap(
                                servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getServices(), params.getEmployees(),
                                        params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getAddress(), params.getOffset(), params.getLimit())
                        )
                );

            } else if(params.getCoordinatesSquare() != null) {
                if(params.getAddress() != null || params.getCoordinatesCircle() != null)
                    throw new BadRequestException("Query params cannot include coordinates square params and address params or coordinates circle params at the same time.");
                // only coordinates circle params
                servicePoints = new ResourceList<>(
                        ServicePointWrapper.wrap(
                                servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getServices(), params.getEmployees(),
                                        params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesSquare(), params.getOffset(), params.getLimit())
                        )
                );

            } else if(params.getCoordinatesCircle() != null) {
                if (params.getAddress() != null || params.getCoordinatesSquare() != null)
                    throw new BadRequestException("Query params cannot include coordinates circle params and address params or coordinates square params at the same time.");
                // only coordinates circle params
                servicePoints = new ResourceList<>(
                        ServicePointWrapper.wrap(
                                servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getServices(), params.getEmployees(),
                                        params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesCircle(), params.getOffset(), params.getLimit())
                        )
                );

            } else {
                // no location params
                servicePoints = new ResourceList<>(
                        ServicePointWrapper.wrap(
                                servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getServices(), params.getEmployees(),
                                        params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getOffset(), params.getLimit())
                        )
                );
            }

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all providers without filtering (eventually paginated)
            servicePoints = new ResourceList<>( ServicePointWrapper.wrap(servicePointFacade.findAllEagerly(params.getOffset(), params.getLimit())) );

        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(servicePoints).build();
    }

    /**
     * Additional methods returning a subset of resources based on given criteria
     * you can achieve similar results by applying @QueryParams to generic method
     * returning all resources in order to filter and limit them
     */

    /**
     * Method returns number of Service Point entities in database
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countServicePoints( @BeanParam GenericBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning number of service points by executing ServicePointResource.countServicePoints() method of REST API");

        ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(servicePointFacade.count()), 200, "number of service points");
        return Response.status(Status.OK).entity(responseEntity).build();
    }

    /**
     * Method returns subset of Service Point entities for given Address query params.
     */
    @GET
    @Path("/address")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getServicePointsByAddress( @BeanParam AddressBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning service points for given address params using ServicePointResource.getServicePointsByAddress(address) method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);
        if(noOfParams < 1)
            throw new BadRequestException("There is no address related query param in request.");

        // find service points by given criteria
        ResourceList<ServicePoint> servicePoints = new ResourceList<>(
                servicePointFacade.findByAddress(params.getCity(), params.getState(), params.getCountry(), params.getStreet(),
                        params.getZipCode(), params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(servicePoints).build();
    }

    /**
     * Method returns subset of Service Point entities for given Coordinates Square query params.
     */
    @GET
    @Path("/coordinates-square")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getServicePointsByCoordinatesSquare( @BeanParam CoordinatesSquareBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning service points for given coordinates square params using ServicePointResource.getServicePointsByCoordinatesSquare(coordinatesSquare) method of REST API");

        if(params.getMinLongitudeWGS84() == null || params.getMinLatitudeWGS84() == null ||
                params.getMaxLongitudeWGS84() == null || params.getMaxLatitudeWGS84() == null)
            throw new BadRequestException("All coordinates square query params must be specified.");

        // find service points by given criteria
        ResourceList<ServicePoint> servicePoints = new ResourceList<>(
                servicePointFacade.findByCoordinatesSquare(params.getMinLongitudeWGS84(), params.getMinLatitudeWGS84(),
                        params.getMaxLongitudeWGS84(), params.getMaxLatitudeWGS84(), params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(servicePoints).build();
    }

    /**
     * Method returns subset of Service Point entities for given Coordinates Circle query params.
     */
    @GET
    @Path("/coordinates-circle")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getServicePointsByCoordinatesCircle( @BeanParam CoordinatesCircleBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning service points for given coordinates circle params using ServicePointResource.getServicePointsByCoordinatesCircle(coordinatesCircle) method of REST API");

        if(params.getLongitudeWGS84() == null || params.getLatitudeWGS84() == null || params.getRadius() == null)
            throw new BadRequestException("All coordinates circle query params must be specified.");

        // find service points by given criteria
        ResourceList<ServicePoint> servicePoints = new ResourceList<>(
                servicePointFacade.findByCoordinatesCircle(params.getLongitudeWGS84(), params.getLatitudeWGS84(), params.getRadius(),
                        params.getOffset(), params.getLimit())
        );

        // result resources need to be populated with hypermedia links to enable resource discovery
        ServicePointResource.populateWithHATEOASLinks(servicePoints, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(servicePoints).build();
    }

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList servicePoints, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(servicePoints, uriInfo, offset, limit);

        try {
            // count resources hypermedia link
            Method countMethod = ServicePointResource.class.getMethod("countServicePoints", GenericBeanParam.class);
            servicePoints.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ServicePointResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            servicePoints.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ServicePointResource.class).build()).rel("service-points").build() );

            // get all resources eagerly hypermedia link
            Method servicePointsEagerlyMethod = ServicePointResource.class.getMethod("getServicePointsEagerly", ServicePointBeanParam.class);
            servicePoints.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                        .path(ServicePointResource.class)
                        .path(servicePointsEagerlyMethod)
                        .build())
                        .rel("service-points-eagerly").build());

            // get subset of resources hypermedia links
            // address
            Method servicePointsByAddressMethod = ServicePointResource.class.getMethod("getServicePointsByAddress", AddressBeanParam.class);
            servicePoints.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ServicePointResource.class).path(servicePointsByAddressMethod).build()).rel("address").build() );

            // coordinates-square
            Method servicePointsByCoordinatesSquareMethod = ServicePointResource.class.getMethod("getServicePointsByCoordinatesSquare", CoordinatesSquareBeanParam.class);
            servicePoints.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ServicePointResource.class).path(servicePointsByCoordinatesSquareMethod).build()).rel("coordinates-square").build() );

            // coordinates-circle
            Method servicePointsByCoordinatesCircleMethod = ServicePointResource.class.getMethod("getServicePointsByCoordinatesCircle", CoordinatesCircleBeanParam.class);
            servicePoints.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(ServicePointResource.class).path(servicePointsByCoordinatesCircleMethod).build()).rel("coordinates-circle").build() );

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

            // collection link with pattern: http://localhost:port/app/rest/{resources}
            servicePoint.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServicePointResource.class)
                    .build())
                    .rel("service-points").build());

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

           // TODO

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }
}
