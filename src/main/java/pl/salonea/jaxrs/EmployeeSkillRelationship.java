package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.EmployeeSkillRelationshipManager;
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
@Path("/employees-has-skills/{employeeId : \\d+}has{skillId : \\d+}")
public class EmployeeSkillRelationship {

    private static final Logger logger = Logger.getLogger(EmployeeSkillRelationship.class.getName());

    @Inject
    private EmployeeSkillRelationshipManager employeeSkillRelationshipManager;

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response addSkillToEmployee( @PathParam("skillId") Integer skillId,
                                        @PathParam("employeeId") Long employeeId,
                                        @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "adding association between given employee and given skill using " +
                "EmployeeSkillRelationship.addSkillToEmployee(skillId, employeeId) method of REST API");

        employeeSkillRelationshipManager.addSkillToEmployee(skillId, employeeId);

        ResponseWrapper responseEntity = new ResponseWrapper("Skill " + skillId + " has been added to Employee " + employeeId,
                200, "Result of creating association between Employee and Skill entities.");

        return Response.status(Status.OK).entity(responseEntity).build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeSkillFromEmployee( @PathParam("skillId") Integer skillId,
                                             @PathParam("employeeId") Long employeeId,
                                             @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing association between given employee and given skill using " +
                "EmployeeSkillRelationship.removeSkillFromEmployee(skillId, employeeId) method of REST API");

        employeeSkillRelationshipManager.removeSkillFromEmployee(skillId, employeeId);

        ResponseWrapper responseEntity = new ResponseWrapper("Skill " + skillId + " has been removed from Employee " + employeeId,
                200, "Result of removing association between Employee and Skill entities.");

        return Response.status(Status.OK).entity(responseEntity).build();
    }
}
