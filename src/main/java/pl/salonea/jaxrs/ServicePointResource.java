package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.EmployeeFacade;
import pl.salonea.ejb.stateless.ServicePointFacade;
import pl.salonea.ejb.stateless.ServicePointPhotoFacade;
import pl.salonea.ejb.stateless.VirtualTourFacade;
import pl.salonea.entities.Employee;
import pl.salonea.entities.ServicePoint;
import pl.salonea.entities.ServicePointPhoto;
import pl.salonea.entities.VirtualTour;
import pl.salonea.entities.idclass.ServicePointId;
import pl.salonea.jaxrs.bean_params.*;
import pl.salonea.jaxrs.exceptions.UnprocessableEntityException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.ResponseWrapper;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.EmployeeWrapper;
import pl.salonea.jaxrs.wrappers.ServicePointPhotoWrapper;
import pl.salonea.jaxrs.wrappers.ServicePointWrapper;

import javax.inject.Inject;
import javax.transaction.*;
import javax.transaction.NotSupportedException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.exceptions.BadRequestException;
import pl.salonea.jaxrs.wrappers.VirtualTourWrapper;

import javax.ws.rs.core.Response.Status;

/**
 * Created by michzio on 12/09/2015.
 */

@Path("/service-points")
public class ServicePointResource {

    private static final Logger logger = Logger.getLogger(ServicePointResource.class.getName());

    @Inject
    private UserTransaction utx;

    @Inject
    private ServicePointFacade servicePointFacade;
    @Inject
    private ServicePointPhotoFacade servicePointPhotoFacade;
    @Inject
    private VirtualTourFacade virtualTourFacade;
    @Inject
    private EmployeeFacade employeeFacade;

    @Inject
    private ProviderResource providerResource;


    /**
     * Alternative methods to access Service Point resource
     */
    @GET
    @Path("/{providerId: \\d+}+{servicePointNumber: \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getServicePoint(@PathParam("providerId") Long providerId,
                                    @PathParam("servicePointNumber") Integer servicePointNumber,
                                    @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        return providerResource.getServicePointResource().getServicePoint(providerId, servicePointNumber, params);
    }

    @GET
    @Path("/{providerId: \\d+}+{servicePointNumber: \\d+}/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getServicePointEagerly(@PathParam("providerId") Long providerId,
                                           @PathParam("servicePointNumber") Integer servicePointNumber,
                                           @BeanParam GenericBeanParam params) throws ForbiddenException, NotFoundException {

        return providerResource.getServicePointResource().getServicePointEagerly(providerId, servicePointNumber, params);
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createServicePoint(ServicePoint servicePoint,
                                       @BeanParam GenericBeanParam params) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        return providerResource.getServicePointResource().createServicePoint(servicePoint.getProvider().getUserId(), servicePoint, params);
    }

    @PUT
    @Path("/{providerId: \\d+}+{servicePointNumber: \\d+}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateServicePoint(@PathParam("providerId") Long providerId,
                                       @PathParam("servicePointNumber") Integer servicePointNumber,
                                       ServicePoint servicePoint,
                                       @BeanParam GenericBeanParam params) throws ForbiddenException, UnprocessableEntityException, InternalServerErrorException {

        return providerResource.getServicePointResource().updateServicePoint(providerId, servicePointNumber, servicePoint, params);
    }

    @DELETE
    @Path("/{providerId: \\d+}+{servicePointNumber: \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeServicePoint(@PathParam("providerId") Long providerId,
                                       @PathParam("servicePointNumber") Integer servicePointNumber,
                                       @BeanParam GenericBeanParam params) throws ForbiddenException, NotFoundException, InternalServerErrorException {

        return providerResource.getServicePointResource().removeServicePoint(providerId, servicePointNumber, params);
    }

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
                        servicePointFacade.findByMultipleCriteria(params.getProviders(), params.getServices(), params.getProviderServices(), params.getEmployees(),
                                params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getAddress(), params.getOffset(), params.getLimit())
                );

            } else if(params.getCoordinatesSquare() != null) {
                if(params.getAddress() != null || params.getCoordinatesCircle() != null)
                    throw new BadRequestException("Query params cannot include coordinates square params and address params or coordinates circle params at the same time.");
                // only coordinates square params
                servicePoints = new ResourceList<>(
                        servicePointFacade.findByMultipleCriteria(params.getProviders(), params.getServices(), params.getProviderServices(), params.getEmployees(),
                                params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesSquare(), params.getOffset(), params.getLimit())
                );

            } else if(params.getCoordinatesCircle() != null) {
                if(params.getAddress() != null || params.getCoordinatesSquare() != null)
                    throw new BadRequestException("Query params cannot include coordinates circle params and address params or coordinates square params at the same time.");
                // only coordinates circle params
                servicePoints = new ResourceList<>(
                        servicePointFacade.findByMultipleCriteria(params.getProviders(), params.getServices(), params.getProviderServices(), params.getEmployees(),
                                params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesCircle(), params.getOffset(), params.getLimit())
                );

            } else {
                // no location params
                servicePoints = new ResourceList<>(
                        servicePointFacade.findByMultipleCriteria(params.getProviders(), params.getServices(), params.getProviderServices(), params.getEmployees(),
                                params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getOffset(), params.getLimit())
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
                                servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getServices(), params.getProviderServices(), params.getEmployees(),
                                        params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getAddress(), params.getOffset(), params.getLimit())
                        )
                );

            } else if(params.getCoordinatesSquare() != null) {
                if(params.getAddress() != null || params.getCoordinatesCircle() != null)
                    throw new BadRequestException("Query params cannot include coordinates square params and address params or coordinates circle params at the same time.");
                // only coordinates circle params
                servicePoints = new ResourceList<>(
                        ServicePointWrapper.wrap(
                                servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getServices(), params.getProviderServices(), params.getEmployees(),
                                        params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesSquare(), params.getOffset(), params.getLimit())
                        )
                );

            } else if(params.getCoordinatesCircle() != null) {
                if (params.getAddress() != null || params.getCoordinatesSquare() != null)
                    throw new BadRequestException("Query params cannot include coordinates circle params and address params or coordinates square params at the same time.");
                // only coordinates circle params
                servicePoints = new ResourceList<>(
                        ServicePointWrapper.wrap(
                                servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getServices(), params.getProviderServices(), params.getEmployees(),
                                        params.getCorporations(), params.getIndustries(), params.getServiceCategories(), params.getCoordinatesCircle(), params.getOffset(), params.getLimit())
                        )
                );

            } else {
                // no location params
                servicePoints = new ResourceList<>(
                        ServicePointWrapper.wrap(
                                servicePointFacade.findByMultipleCriteriaEagerly(params.getProviders(), params.getServices(), params.getProviderServices(), params.getEmployees(),
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
     * related subresources (through relationships)
     */

    @Path("/{providerId: \\d+}+{servicePointNumber: \\d+}/photos")
    public PhotoResource getServicePointPhotoResource() {
        return new PhotoResource();
    }

    @Path("/{providerId: \\d+}+{servicePointNumber: \\d+}/virtual-tours")
    public VirtualTourResource getVirtualTourResource() {
        return new VirtualTourResource();
    }

    @Path("/{providerId: \\d+}+{servicePointNumber: \\d+}/employees")
    public EmployeeResource getEmployeeResource() { return new EmployeeResource(); }

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

        for(ServicePointPhoto photo : servicePointWrapper.getPhotos())
            pl.salonea.jaxrs.ServicePointPhotoResource.populateWithHATEOASLinks(photo, uriInfo);

        for(VirtualTour tour : servicePointWrapper.getVirtualTours())
            pl.salonea.jaxrs.VirtualTourResource.populateWithHATEOASLinks(tour, uriInfo);

      /*
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

            // associated collections links with pattern: http://localhost:port/app/rest/{resources}/{id}/{relationship}

            /**
             * Service Point Photos associated with current Service Point resource
             */

            // service-point-photos
            Method servicePointPhotosMethod = ServicePointResource.class.getMethod("getServicePointPhotoResource");
            servicePoint.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServicePointResource.class)
                    .path(servicePointPhotosMethod)
                    .resolveTemplate("providerId", servicePoint.getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", servicePoint.getServicePointNumber().toString())
                    .build())
                    .rel("service-point-photos").build());

            // service-point-photos eagerly
            Method servicePointPhotosEagerlyMethod = ServicePointResource.PhotoResource.class.getMethod("getServicePointPhotosEagerly", Long.class, Integer.class, ServicePointPhotoBeanParam.class);
            servicePoint.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServicePointResource.class)
                    .path(servicePointPhotosMethod)
                    .path(servicePointPhotosEagerlyMethod)
                    .resolveTemplate("providerId", servicePoint.getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", servicePoint.getServicePointNumber().toString())
                    .build())
                    .rel("service-point-photos-eagerly").build());

            // service-point-photos count
            Method countServicePointPhotosByServicePointMethod = ServicePointResource.PhotoResource.class.getMethod("countServicePointPhotosByServicePoint", Long.class, Integer.class, GenericBeanParam.class);
            servicePoint.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServicePointResource.class)
                    .path(servicePointPhotosMethod)
                    .path(countServicePointPhotosByServicePointMethod)
                    .resolveTemplate("providerId",servicePoint.getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", servicePoint.getServicePointNumber().toString())
                    .build())
                    .rel("service-point-photos-count").build());

            /**
             * Virtual Tours associated with current Service Point resource
             */

            // virtual-tours
            Method virtualToursMethod = ServicePointResource.class.getMethod("getVirtualTourResource");
            servicePoint.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServicePointResource.class)
                    .path(virtualToursMethod)
                    .resolveTemplate("providerId",servicePoint.getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", servicePoint.getServicePointNumber().toString())
                    .build())
                    .rel("virtual-tours").build());

            // virtual-tours eagerly
            Method virtualToursEagerlyMethod = ServicePointResource.VirtualTourResource.class.getMethod("getServicePointVirtualToursEagerly", Long.class, Integer.class, VirtualTourBeanParam.class);
            servicePoint.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServicePointResource.class)
                    .path(virtualToursMethod)
                    .path(virtualToursEagerlyMethod)
                    .resolveTemplate("providerId", servicePoint.getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", servicePoint.getServicePointNumber().toString())
                    .build())
                    .rel("virtual-tours-eagerly").build());

            // virtual-tours count
            Method countVirtualToursByServicePointMethod = ServicePointResource.VirtualTourResource.class.getMethod("countVirtualToursByServicePoint", Long.class, Integer.class, GenericBeanParam.class);
            servicePoint.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServicePointResource.class)
                    .path(virtualToursMethod)
                    .path(countVirtualToursByServicePointMethod)
                    .resolveTemplate("providerId",servicePoint.getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", servicePoint.getServicePointNumber().toString())
                    .build())
                    .rel("virtual-tours-count").build());

            /**
             * Employees working in current Service Point resource
             */

            // employees
            Method employeesMethod = ServicePointResource.class.getMethod("getEmployeeResource");
            servicePoint.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServicePointResource.class)
                    .path(employeesMethod)
                    .resolveTemplate("providerId", servicePoint.getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", servicePoint.getServicePointNumber().toString())
                    .build())
                    .rel("employees").build());

            // employees eagerly
            Method employeesEagerlyMethod = ServicePointResource.EmployeeResource.class.getMethod("getServicePointEmployeesEagerly", Long.class, Integer.class, EmployeeBeanParam.class);
            servicePoint.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServicePointResource.class)
                    .path(employeesMethod)
                    .path(employeesEagerlyMethod)
                    .resolveTemplate("providerId", servicePoint.getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", servicePoint.getServicePointNumber().toString())
                    .build())
                    .rel("employees-eagerly").build());

            // employees count
            Method countEmployeesByServicePointMethod = ServicePointResource.EmployeeResource.class.getMethod("countEmployeesByServicePoint", Long.class, Integer.class, GenericBeanParam.class);
            servicePoint.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServicePointResource.class)
                    .path(employeesMethod)
                    .path(countEmployeesByServicePointMethod)
                    .resolveTemplate("providerId", servicePoint.getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", servicePoint.getServicePointNumber().toString())
                    .build())
                    .rel("employees-count").build());

            // employees by-term
            Method employeesByTermMethod = ServicePointResource.EmployeeResource.class.getMethod("getServicePointEmployeesByTerm", Long.class, Integer.class, DateBetweenBeanParam.class);
            servicePoint.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServicePointResource.class)
                    .path(employeesMethod)
                    .path(employeesByTermMethod)
                    .resolveTemplate("providerId", servicePoint.getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", servicePoint.getServicePointNumber().toString())
                    .build())
                    .rel("employees-by-term").build());

            // employees by-term-strict
            Method employeesByTermStrictMethod = ServicePointResource.EmployeeResource.class.getMethod("getServicePointEmployeesByTermStrict", Long.class, Integer.class, DateBetweenBeanParam.class);
            servicePoint.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ServicePointResource.class)
                    .path(employeesMethod)
                    .path(employeesByTermStrictMethod)
                    .resolveTemplate("providerId", servicePoint.getProvider().getUserId().toString())
                    .resolveTemplate("servicePointNumber", servicePoint.getServicePointNumber().toString())
                    .build())
                    .rel("employees-by-term-strict").build());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /* related ServicePointPhoto subresource */
    public class PhotoResource {

        public PhotoResource() { }

        /**
         * Method returns subset of Service Point Photo entities for given Service Point entity.
         * The provider id and service point number are passed through path param.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServicePointPhotos( @PathParam("providerId") Long providerId,
                                               @PathParam("servicePointNumber") Integer servicePointNumber,
                                               @BeanParam ServicePointPhotoBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning service point photos for given service point using " +
                    "ServicePointResource.PhotoResource.getServicePointPhotos(providerId, servicePointNumber) method of REST API");

            utx.begin();

            // find service point entity for which to get associated service point photos
            ServicePoint servicePoint = servicePointFacade.find(new ServicePointId(providerId, servicePointNumber));
            if(servicePoint == null)
                throw new NotFoundException("Could not find service point for id (" + providerId + "," + servicePointNumber + ").");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ServicePointPhoto> photos = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<ServicePoint> servicePoints = new ArrayList<>();
                servicePoints.add(servicePoint);

                // get photos for given service point filtered by given params

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getFileNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                        throw new BadRequestException("Query params cannot include keywords and fileNames or descriptions at the same time.");

                    if( RESTToolkit.isSet(params.getTagNames()) ) {
                        // find by keywords and tag names
                        photos = new ResourceList<>(
                                servicePointPhotoFacade.findByMultipleCriteria(params.getKeywords(), params.getTagNames(), servicePoints,
                                        params.getProviders(), params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                        );
                    } else {
                        // find only by keywords
                        photos = new ResourceList<>(
                                servicePointPhotoFacade.findByMultipleCriteria(params.getKeywords(), servicePoints, params.getProviders(),
                                        params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                        );
                    }
                } else {
                    // find by fileNames, descriptions or tagNames
                    photos = new ResourceList<>(
                            servicePointPhotoFacade.findByMultipleCriteria(params.getFileNames(), params.getDescriptions(), params.getTagNames(),
                                    servicePoints, params.getProviders(), params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                    );
                }
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get photos for given service point without filtering
                photos = new ResourceList<>( servicePointPhotoFacade.findByServicePoint(servicePoint, params.getOffset(), params.getLimit()) );
            }

            utx.commit();

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointPhotoResource.populateWithHATEOASLinks(photos, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(photos).build();
        }

        /**
         * Method returns subset of Service Point Photo entities for given Service Point fetching them eagerly.
         * The provider id and service point number are passed through path param.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServicePointPhotosEagerly( @PathParam("providerId") Long providerId,
                                                      @PathParam("servicePointNumber") Integer servicePointNumber,
                                                      @BeanParam ServicePointPhotoBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning service point photos eagerly for given service point using " +
                    "ServicePointResource.PhotoResource.getServicePointPhotosEagerly(providerId, servicePointNumber) method of REST API");

            utx.begin();

            // find service point entity for which to get associated service point photos
            ServicePoint servicePoint = servicePointFacade.find(new ServicePointId(providerId, servicePointNumber));
            if(servicePoint == null)
                throw new NotFoundException("Could not find service point for id (" + providerId + "," + servicePointNumber + ").");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<ServicePointPhotoWrapper> photos = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<ServicePoint> servicePoints = new ArrayList<>();
                servicePoints.add(servicePoint);

                // get photos eagerly for given service point filtered by given params

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getFileNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                        throw new BadRequestException("Query params cannot include keywords and fileNames or descriptions at the same time.");

                    if( RESTToolkit.isSet(params.getTagNames()) ) {
                        // find by keywords and tag names
                        photos = new ResourceList<>(
                                ServicePointPhotoWrapper.wrap(
                                        servicePointPhotoFacade.findByMultipleCriteriaEagerly(params.getKeywords(), params.getTagNames(), servicePoints,
                                                params.getProviders(), params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                                )
                        );
                    } else {
                        // find only by keywords
                        photos = new ResourceList<>(
                                ServicePointPhotoWrapper.wrap(
                                        servicePointPhotoFacade.findByMultipleCriteriaEagerly(params.getKeywords(), servicePoints, params.getProviders(),
                                                params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                                )
                        );
                    }
                } else {
                    // find by fileNames, descriptions or tagNames
                    photos = new ResourceList<>(
                            ServicePointPhotoWrapper.wrap(
                                    servicePointPhotoFacade.findByMultipleCriteriaEagerly(params.getFileNames(), params.getDescriptions(), params.getTagNames(),
                                            servicePoints, params.getProviders(), params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                            )
                    );
                }
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get photos eagerly for given service point without filtering
                photos = new ResourceList<>( ServicePointPhotoWrapper.wrap(servicePointPhotoFacade.findByServicePointEagerly(servicePoint, params.getOffset(), params.getLimit())) );
            }

            utx.commit();

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.ServicePointPhotoResource.populateWithHATEOASLinks(photos, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(photos).build();
        }

        /**
         * Method that counts Service Point Photo entities for given Service Point resource.
         * The provider id and service point number are passed through path params.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countServicePointPhotosByServicePoint( @PathParam("providerId") Long providerId,
                                                               @PathParam("servicePointNumber") Integer servicePointNumber,
                                                               @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of service point photos for given service point by executing " +
                    "ServicePointResource.PhotoResource.countServicePointPhotosByServicePoint(providerId, servicePointNumber) method of REST API");

            utx.begin();

            // find service point entity for which to count photos
            ServicePoint servicePoint = servicePointFacade.find(new ServicePointId(providerId, servicePointNumber));
            if(servicePoint == null)
                throw new NotFoundException("Could not find service point for id (" + providerId + "," + servicePointNumber + ").");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(servicePointPhotoFacade.countByServicePoint(servicePoint)), 200,
                    "number of photos for service point with id (" + providerId + ","+ servicePointNumber + ").");

            utx.commit();

            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method that removes subset of Service Point Photo entities from database for given Service Point.
         * The provider id and service point number are passed through path param.
         */
        @DELETE
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response removeServicePointPhotos( @PathParam("providerId") Long providerId,
                                                  @PathParam("servicePointNumber") Integer servicePointNumber,
                                                  @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "removing subset of Service Point Photo entities for given Service Point by executing " +
                    "ServicePointResource.PhotoResource.removeServicePointPhotos(providerId, servicePointNumber) method of REST API");

            utx.begin();

            // find service point entity for which to remove service point photos
            ServicePoint servicePoint = servicePointFacade.find(new ServicePointId(providerId, servicePointNumber));
            if(servicePoint == null)
                throw new NotFoundException("Could not find service point for id (" + providerId + "," + servicePointNumber + ").");

            // remove all specified entities from database
            Integer noOfDeleted = servicePointPhotoFacade.deleteByServicePoint(servicePoint);

            utx.commit();

            // create response returning number of deleted entities
            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfDeleted), 200,
                    "number of deleted service point photos for service point with id (" + providerId + "," + servicePointNumber +  ")");

            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    /* related VirtualTour subresource */
    public class VirtualTourResource {

        public VirtualTourResource() { }

        /**
         * Method returns subset of Virtual Tour entities for given Service Point entity.
         * The provider id and service point number are passed through path param.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServicePointVirtualTours( @PathParam("providerId") Long providerId,
                                                     @PathParam("servicePointNumber") Integer servicePointNumber,
                                                     @BeanParam VirtualTourBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException
        {
            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning virtual tours for given service point using " +
                    "ServicePointResource.VirtualTourResource.getServicePointVirtualTours(providerId, servicePointNumber) method of REST API");

            utx.begin();

            // find service point entity for which to get associated virtual tours
            ServicePoint servicePoint = servicePointFacade.find(new ServicePointId(providerId, servicePointNumber));
            if(servicePoint == null)
                throw new NotFoundException("Could not find service point for id (" + providerId + "," + servicePointNumber + ").");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<VirtualTour> virtualTours = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<ServicePoint> servicePoints = new ArrayList<>();
                servicePoints.add(servicePoint);

                // get virtual tours for given service point filtered by given params

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getFileNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                        throw new BadRequestException("Query params cannot include keywords and fileNames or descriptions at the same time.");

                    if( RESTToolkit.isSet(params.getTagNames()) ) {
                        // find by keywords and tag names
                        virtualTours = new ResourceList<>(
                                virtualTourFacade.findByMultipleCriteria(params.getKeywords(), params.getTagNames(), servicePoints,
                                        params.getProviders(), params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                        );
                    } else {
                        // find only by keywords
                        virtualTours = new ResourceList<>(
                                virtualTourFacade.findByMultipleCriteria(params.getKeywords(), servicePoints, params.getProviders(),
                                        params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                        );
                    }
                } else {
                    // find by fileNames, descriptions or tagNames
                    virtualTours = new ResourceList<>(
                            virtualTourFacade.findByMultipleCriteria(params.getFileNames(), params.getDescriptions(), params.getTagNames(),
                                    servicePoints, params.getProviders(), params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                    );
                }
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get virtual tours for given service point without filtering (eventually paginated)
                virtualTours = new ResourceList<>(virtualTourFacade.findByServicePoint(servicePoint, params.getOffset(), params.getLimit()) );
            }

            utx.commit();

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.VirtualTourResource.populateWithHATEOASLinks(virtualTours, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(virtualTours).build();
        }

        /**
         * Method returns subset of Virtual Tour entities for given Service Point fetching them eagerly.
         * The provider id and service point number are passed through path param.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServicePointVirtualToursEagerly( @PathParam("providerId") Long providerId,
                                                            @PathParam("servicePointNumber") Integer servicePointNumber,
                                                            @BeanParam VirtualTourBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning virtual tours eagerly for given service point using " +
                    "ServicePointResource.VirtualTourResource.getServicePointVirtualToursEagerly(providerId, servicePointNumber) method of REST API");

            utx.begin();

            // find service point entity for which to get associated virtual tours
            ServicePoint servicePoint = servicePointFacade.find(new ServicePointId(providerId, servicePointNumber));
            if(servicePoint == null)
                throw new NotFoundException("Could not find service point for id (" + providerId + "," + servicePointNumber + ").");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<VirtualTourWrapper> virtualTours = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<ServicePoint> servicePoints = new ArrayList<>();
                servicePoints.add(servicePoint);

                // get virtual tours eagerly for given service point filtered by given params

                if( RESTToolkit.isSet(params.getKeywords()) ) {
                    if( RESTToolkit.isSet(params.getFileNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                        throw new BadRequestException("Query params cannot include keywords and fileNames or descriptions at the same time.");

                    if( RESTToolkit.isSet(params.getTagNames()) ) {
                        // find by keywords and tag names
                        virtualTours = new ResourceList<>(
                                VirtualTourWrapper.wrap(
                                        virtualTourFacade.findByMultipleCriteriaEagerly(params.getKeywords(), params.getTagNames(), servicePoints,
                                                params.getProviders(), params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                                )
                        );
                    } else {
                        // find only by keywords
                        virtualTours = new ResourceList<>(
                                VirtualTourWrapper.wrap(
                                        virtualTourFacade.findByMultipleCriteriaEagerly(params.getKeywords(), servicePoints, params.getProviders(),
                                                params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                                )
                        );
                    }
                } else {
                    // find by fileNames, descriptions or tagNames
                    virtualTours = new ResourceList<>(
                            VirtualTourWrapper.wrap(
                                    virtualTourFacade.findByMultipleCriteriaEagerly(params.getFileNames(), params.getDescriptions(), params.getTagNames(),
                                            servicePoints, params.getProviders(), params.getCorporations(), params.getTags(), params.getOffset(), params.getLimit())
                            )
                    );
                }
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get virtual tours for given service point without filtering (eventually paginated)
                virtualTours = new ResourceList<>( VirtualTourWrapper.wrap(virtualTourFacade.findByServicePointEagerly(servicePoint, params.getOffset(), params.getLimit())) );
            }

            utx.commit();

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.VirtualTourResource.populateWithHATEOASLinks(virtualTours, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(virtualTours).build();
        }

        /**
         * Method that counts Virtual Tour entities for given Service Point resource.
         * The provider id and service point number are passed through path params.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countVirtualToursByServicePoint( @PathParam("providerId") Long providerId,
                                                         @PathParam("servicePointNumber") Integer servicePointNumber,
                                                         @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of virtual tours for given service point by executing " +
                    "ServicePointResource.VirtualTourResource.countVirtualToursByServicePoint(providerId, servicePointNumber) method of REST API");

            utx.begin();

            // find service point entity for which to count virtual tours
            ServicePoint servicePoint = servicePointFacade.find(new ServicePointId(providerId, servicePointNumber));
            if(servicePoint == null)
                throw new NotFoundException("Could not find service point for id (" + providerId + "," + servicePointNumber + ").");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(virtualTourFacade.countByServicePoint(servicePoint)), 200,
                    "number of virtual tours for service point with id (" + providerId + "," + servicePointNumber + ").");

            utx.commit();

            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method that removes subset of Virtual Tour entities from database for given Service Point.
         * The provider id and service point number are passed through path param.
         */
        @DELETE
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response removeServicePointVirtualTours(  @PathParam("providerId") Long providerId,
                                                         @PathParam("servicePointNumber") Integer servicePointNumber,
                                                         @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "removing subset of Virtual Tour entities for given Service Point by executing " +
                    "ServicePointResource.VirtualTourResource.removeServicePointVirtualTours(providerId, servicePointNumber) method of REST API");

            utx.begin();

            // find service point entity for which to remove virtual tours
            ServicePoint servicePoint = servicePointFacade.find(new ServicePointId(providerId, servicePointNumber));
            if(servicePoint == null)
                throw new NotFoundException("Could not find service point for id (" + providerId + "," + servicePointNumber + ").");

            // remove all specified entities from database
            Integer noOfDeleted = virtualTourFacade.deleteByServicePoint(servicePoint);

            utx.commit();

            // create response returning number of deleted entities
            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(noOfDeleted), 200,
                    "number of deleted virtual tours for service point with id (" + providerId + "," + servicePointNumber + ")");

            return Response.status(Status.OK).entity(responseEntity).build();
        }
    }

    public class EmployeeResource {

        public EmployeeResource() { }

        /**
         * Method returns subset of Employee entities for given Service Point entity.
         * The provider id and service point number are passed through path params.
         */
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServicePointEmployees( @PathParam("providerId") Long providerId,
                                                  @PathParam("servicePointNumber") Integer servicePointNumber,
                                                  @BeanParam EmployeeBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */  HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employees for given service point using ServicePointResource.EmployeeResource.getServicePointEmployees(providerId, servicePointNumber) method of REST API");

            utx.begin();

            // find service point entity for which to get associated employees
            ServicePoint servicePoint = servicePointFacade.find(new ServicePointId(providerId, servicePointNumber));
            if(servicePoint == null)
                throw new NotFoundException("Could not find service point for id (" + providerId + "," + servicePointNumber + ").");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<Employee> employees = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<ServicePoint> servicePoints = new ArrayList<>();
                servicePoints.add(servicePoint);

                // get employees for given service point filtered by given params
                employees = new ResourceList<>(
                        employeeFacade.findByMultipleCriteria(params.getDescriptions(), params.getJobPositions(), params.getSkills(),
                                params.getEducations(), params.getServices(), params.getProviderServices(), servicePoints,
                                params.getWorkStations(), params.getPeriod(), params.getStrictTerm(), params.getRated(),
                                params.getMinAvgRating(), params.getMaxAvgRating(), params.getRatingClients(), params.getOffset(), params.getLimit())
                );
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get employees for given service point without filtering (eventually paginated)
                employees = new ResourceList<>( employeeFacade.findByServicePoint(servicePoint, params.getOffset(), params.getLimit()) );
            }

            utx.commit();

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeResource.populateWithHATEOASLinks(employees, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employees).build();
        }


        /**
         * Method returns subset of Employee entities for given Service Point fetching them eagerly.
         * The provider id and service point number are passed through path params.
         */
        @GET
        @Path("/eagerly")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServicePointEmployeesEagerly( @PathParam("providerId") Long providerId,
                                                         @PathParam("servicePointNumber") Integer servicePointNumber,
                                                         @BeanParam EmployeeBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employees eagerly for given service point using " +
                    "ServicePointResource.EmployeeResource.getServicePointEmployeesEagerly(providerId, servicePointNumber) method of REST API");

            utx.begin();

            // find service point entity for which to get associated employees
            ServicePoint servicePoint = servicePointFacade.find(new ServicePointId(providerId, servicePointNumber));
            if(servicePoint == null)
                throw new NotFoundException("Could not find service point for id (" + providerId + "," + servicePointNumber + ").");

            Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

            ResourceList<EmployeeWrapper> employees = null;

            if(noOfParams > 0) {
                logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

                List<ServicePoint> servicePoints = new ArrayList<>();
                servicePoints.add(servicePoint);

                // get employees eagerly for given service point filtered by given params
                employees = new ResourceList<>(
                        EmployeeWrapper.wrap(
                                employeeFacade.findByMultipleCriteriaEagerly(params.getDescriptions(), params.getJobPositions(), params.getSkills(),
                                        params.getEducations(), params.getServices(), params.getProviderServices(), servicePoints,
                                        params.getWorkStations(), params.getPeriod(), params.getStrictTerm(), params.getRated(),
                                        params.getMinAvgRating(), params.getMaxAvgRating(), params.getRatingClients(), params.getOffset(), params.getLimit())
                        )
                );
            } else {
                logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

                // get employees eagerly for given service point without filtering (eventually paginated)
                employees = new ResourceList<>( EmployeeWrapper.wrap(employeeFacade.findByServicePointEagerly(servicePoint, params.getOffset(), params.getLimit())) );
            }

            utx.commit();

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeResource.populateWithHATEOASLinks(employees, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employees).build();
        }

        /**
         * Method counts Employee entities for given Service Point entity.
         * The provider id and service point number are passed through path params.
         */
        @GET
        @Path("/count")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response countEmployeesByServicePoint( @PathParam("providerId") Long providerId,
                                                      @PathParam("servicePointNumber") Integer servicePointNumber,
                                                      @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning number of employees for given service point by executing " +
                    "ServicePointResource.EmployeeResource.countEmployeesByServicePoint(providerId, servicePointNumber) method of REST API");

            utx.begin();

            // find service point entity for which to count employees
            ServicePoint servicePoint = servicePointFacade.find( new ServicePointId(providerId, servicePointNumber) );
            if(servicePoint == null)
                throw new NotFoundException("Could not find service point for id (" + providerId + "," + servicePointNumber + ").");

            ResponseWrapper responseEntity = new ResponseWrapper(String.valueOf(employeeFacade.countByServicePoint(servicePoint)), 200,
                    "number of employees for service point with id (" + providerId + "," + servicePointNumber + ").");

            utx.commit();

            return Response.status(Status.OK).entity(responseEntity).build();
        }

        /**
         * Method returns subset of Employee entities for given Service Point entity and
         * Term they work in it. The provider id and service point number are passed through
         * path params. Term start and end dates are passed through query params.
         */
        @GET
        @Path("/by-term")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServicePointEmployeesByTerm( @PathParam("providerId") Long providerId,
                                                        @PathParam("servicePointNumber") Integer servicePointNumber,
                                                        @BeanParam DateBetweenBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
          /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employees for given service point and term (startDate, endDate) using " +
                    "ServicePointResource.EmployeeResource.getServicePointEmployeesByTerm(providerId, servicePointNumber, term) method of REST API");

            RESTToolkit.validateDateRange(params); // i.e. startDate and endDate

            utx.begin();

            // find service point entity for which to get associated employees
            ServicePoint servicePoint = servicePointFacade.find(new ServicePointId(providerId, servicePointNumber));
            if(servicePoint == null)
                throw new NotFoundException("Could not find service point for id (" + providerId + "," + servicePointNumber + ").");

            // find employees by given criteria (service point, term)
            ResourceList<Employee> employees = new ResourceList<>(
                    employeeFacade.findByServicePointAndTerm(servicePoint, params.getStartDate(), params.getEndDate(),
                            params.getOffset(), params.getLimit())
            );

            utx.commit();

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeResource.populateWithHATEOASLinks(employees, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employees).build();
        }

        /**
         * Method returns subset of Employee entities for given Service Point entity and
         * Term (strict) they work in it. The provider id and service point number are passed through
         * path params. Term (strict) start and end dates are passed through query params.
         */
        @GET
        @Path("/by-term-strict")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getServicePointEmployeesByTermStrict( @PathParam("providerId") Long providerId,
                                                              @PathParam("servicePointNumber") Integer servicePointNumber,
                                                              @BeanParam DateBetweenBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException,
        /* UserTransaction exceptions */ HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

            RESTToolkit.authorizeAccessToWebService(params);
            logger.log(Level.INFO, "returning employees for given service point and term strict (startDate, endDate) using " +
                    "ServicePointResource.EmployeeResource.getServicePointEmployeesByTermStrict(providerId, servicePointNumber, termStrict) method of REST API");

            RESTToolkit.validateDateRange(params); // i.e. startDate and endDate

            utx.begin();

            // find service point entity for which to get associated employees
            ServicePoint servicePoint = servicePointFacade.find(new ServicePointId(providerId, servicePointNumber));
            if(servicePoint == null)
                throw new NotFoundException("Could not find service point for id (" + providerId + "," + servicePointNumber + ").");

            // find employees by given criteria (service point, term strict)
            ResourceList<Employee> employees = new ResourceList<>(
                    employeeFacade.findByServicePointAndTermStrict(servicePoint, params.getStartDate(), params.getEndDate(),
                            params.getOffset(), params.getLimit())
            );

            utx.commit();

            // result resources need to be populated with hypermedia links to enable resource discovery
            pl.salonea.jaxrs.EmployeeResource.populateWithHATEOASLinks(employees, params.getUriInfo(), params.getOffset(), params.getLimit());

            return Response.status(Status.OK).entity(employees).build();
        }

    }

}
