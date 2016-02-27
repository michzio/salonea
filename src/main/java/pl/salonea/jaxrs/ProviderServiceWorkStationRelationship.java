package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.ProviderServiceWorkStationRelationshipManager;
import pl.salonea.entities.idclass.ProviderServiceId;
import pl.salonea.entities.idclass.WorkStationId;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.salonea.jaxrs.exceptions.BadRequestException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResponseWrapper;

/**
 * Created by michzio on 27/02/2016.
 */
@Path("/provider-services-on-work-stations/{providerId : \\d+}+{serviceId : \\d+}on{workStationProviderId : \\d+}+{servicePointNumber : \\d+}+{workStationNumber : \\d+}")
public class ProviderServiceWorkStationRelationship {

    private static final Logger logger = Logger.getLogger(ProviderServiceWorkStationRelationship.class.getName());

    @Inject
    private ProviderServiceWorkStationRelationshipManager providerServiceWorkStationRelationshipManager;

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response addProviderServiceOnWorkStation( @PathParam("providerId") Long providerId,
                                                     @PathParam("serviceId") Integer serviceId,
                                                     @PathParam("workStationProviderId") Long workStationProviderId,
                                                     @PathParam("servicePointNumber") Integer servicePointNumber,
                                                     @PathParam("workStationNumber") Integer workStationNumber,
                                                     @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "adding association between given provider service and given work station using " +
                "ProviderServiceWorkStationRelationship.addProviderServiceOnWorkStation(providerId, serviceId, workStationProviderId, servicePointNumber, workStationNumber) method of REST API");

        if(providerId != workStationProviderId)
            throw new BadRequestException("Provider Service can only be provided on Work Station of the same provider. " +
                    "This means Provider Service provider id and Work Station provider id cannot be different.");

        // providerId == workStationProviderId
        providerServiceWorkStationRelationshipManager
                .addProviderServiceOnWorkStation( new ProviderServiceId(providerId, serviceId),
                                                  new WorkStationId(providerId, servicePointNumber, workStationNumber) );

        ResponseWrapper responseEntity = new ResponseWrapper("Provider Service (" + providerId + "," + serviceId + ") has been added on Work Station (" + providerId + "," + servicePointNumber + "," + workStationNumber + ")",
                200, "Result of creating association between Provider Service and Work Station entities.");

        return Response.status(Status.OK).entity(responseEntity).build();
    }


    @DELETE
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeProviderServiceOnWorkStation( @PathParam("providerId") Long providerId,
                                                        @PathParam("serviceId") Integer serviceId,
                                                        @PathParam("workStationProviderId") Long workStationProviderId,
                                                        @PathParam("servicePointNumber") Integer servicePointNumber,
                                                        @PathParam("workStationNumber") Integer workStationNumber,
                                                        @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing association between given provider service and given work station using " +
                "ProviderServiceWorkStationRelationship.removeProviderServiceOnWorkStation(providerId, serviceId, workStationProviderId, servicePointNumber, workStationNumber) method of REST API");

        if(providerId != workStationProviderId)
            throw new BadRequestException("Provider Service can only be provided on Work Station of the same provider. " +
                    "This means Provider Service provider id and Work Station provider id cannot be different.");

        // providerId == workStationProviderId
        providerServiceWorkStationRelationshipManager
                .removeProviderServiceOnWorkStation( new ProviderServiceId(providerId, serviceId),
                                                     new WorkStationId(providerId, servicePointNumber, workStationNumber) );

        ResponseWrapper responseEntity = new ResponseWrapper("Provider Service (" + providerId + "," + serviceId + ") has been removed on Work Station (" + providerId + "," + servicePointNumber + "," + workStationNumber + ")",
                200, "Result of removing association between Provider Service and Work Station entities.");

        return Response.status(Status.OK).entity(responseEntity).build();
    }

}
