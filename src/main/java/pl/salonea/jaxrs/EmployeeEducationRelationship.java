package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.EmployeeEducationRelationshipManager;
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
 * Created by michzio on 12/01/2016.
 */
@Path("/employees-have-educations/{employeeId : \\d+}has{educationId : \\d+}")
public class EmployeeEducationRelationship {

    private static final Logger logger = Logger.getLogger(EmployeeEducationRelationship.class.getName());

    @Inject
    private EmployeeEducationRelationshipManager employeeEducationRelationshipManager;

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response addEducationToEmployee( @PathParam("educationId") Long educationId,
                                            @PathParam("employeeId") Long employeeId,
                                            @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "adding association between given employee and given education using " +
                "EmployeeEducationRelationship.addEducationToEmployee(educationId, employeeId) method of REST API");

        employeeEducationRelationshipManager.addEducationToEmployee(educationId, employeeId);

        ResponseWrapper responseEntity = new ResponseWrapper("Education " + educationId + " has been added to Employee " + employeeId,
                200, "Result of creating association between Employee and Education entities.");

        return Response.status(Status.OK).entity(responseEntity).build();
    }


    @DELETE
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeEducationFromEmployee( @PathParam("educationId") Long educationId,
                                                 @PathParam("employeeId") Long employeeId,
                                                 @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing association between given employee and given education using " +
                "EmployeeEducationRelationship.removeEducationFromEmployee(educationId, employeeId) method of REST API");

        employeeEducationRelationshipManager.removeEducationFromEmployee(educationId, employeeId);

        ResponseWrapper responseEntity = new ResponseWrapper("Education " + educationId + " has been removed from Employee " + employeeId,
                200, "Result of removing association between Employee and Education entities.");

        return Response.status(Status.OK).entity(responseEntity).build();
    }

}
