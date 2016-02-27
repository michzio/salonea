package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.EmployeeProviderServiceRelationshipManager;
import pl.salonea.entities.idclass.ProviderServiceId;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResponseWrapper;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by michzio on 27/02/2016.
 */
@Path("/employees-supplies-provider-services/{employeeId : \\d+}supply{providerId : \\d+}+{serviceId : \\d+}")
public class EmployeeProviderServiceRelationship {

    private static final Logger logger = Logger.getLogger(EmployeeProviderServiceRelationship.class.getName());

    @Inject
    private EmployeeProviderServiceRelationshipManager employeeProviderServiceRelationshipManager;

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response addEmployeeSupplyProviderService( @PathParam("employeeId") Long employeeId,
                                                      @PathParam("providerId") Long providerId,
                                                      @PathParam("serviceId") Integer serviceId,
                                                      @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "adding association between given employee and given provider service using " +
                "EmployeeProviderServiceRelationship.addEmployeeSupplyProviderService(employeeId, providerId, serviceId) method of REST API");

        employeeProviderServiceRelationshipManager.addEmployeeSupplyProviderService(employeeId, new ProviderServiceId(providerId, serviceId));

        ResponseWrapper responseEntity = new ResponseWrapper("Employee " + employeeId + " has been added as supplier of Provider Service (" + providerId + "," + serviceId + ")",
                200, "Result of creating association between Employee and Provider Service entities.");

        return Response.status(Status.OK).entity(responseEntity).build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeEmployeeSupplyProviderService( @PathParam("employeeId") Long employeeId,
                                                         @PathParam("providerId") Long providerId,
                                                         @PathParam("serviceId") Integer serviceId,
                                                         @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing association between given employee and given provider service using " +
                "EmployeeProviderServiceRelationship.removeEmployeeSupplyProviderService(employeeId, providerId, serviceId) method of REST API");

        employeeProviderServiceRelationshipManager.removeEmployeeSupplyProviderService(employeeId, new ProviderServiceId(providerId, serviceId));

        ResponseWrapper responseEntity = new ResponseWrapper("Employee " + employeeId + " has been removed as supplier of Provider Service (" + providerId + "," + serviceId + ")",
                200, "Result of removing association between Employee and Provider Service entities.");

        return Response.status(Status.OK).entity(responseEntity).build();
    }
}
