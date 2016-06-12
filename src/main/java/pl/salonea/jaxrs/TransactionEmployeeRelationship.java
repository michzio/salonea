package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.TransactionEmployeeRelationshipManager;
import pl.salonea.entities.idclass.TransactionId;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.exceptions.*;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
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
 * Created by michzio on 08/06/2016.
 */
@Path("/employees-executing-transactions")
public class TransactionEmployeeRelationship {

    private static final Logger logger = Logger.getLogger(TransactionEmployeeRelationship.class.getName());

    @Inject
    TransactionEmployeeRelationshipManager transactionEmployeeRelationshipManager;

    @POST
    @Path("/{employeeId : \\d+}executing{clientId : \\d+}+{transactionNumber : \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response addEmployeeToTransaction( @PathParam("employeeId") Long employeeId,
                                              @PathParam("clientId") Long clientId,
                                              @PathParam("transactionNumber") Integer transactionNumber,
                                              @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "adding association between given transaction and given employee using " +
                "TransactionEmployeeRelationship.addEmployeeToTransaction(employeeId, clientId, transactionNumber) method of REST API");

        transactionEmployeeRelationshipManager.addEmployeeToTransaction(employeeId, new TransactionId(clientId, transactionNumber));

        ResponseWrapper responseEntity = new ResponseWrapper("Employee " + employeeId + " has been added to Transaction (" + clientId + ", " + transactionNumber + ")",
                200, "Result of creating association between Transaction and Employee entities.");

        return Response.status(Status.OK).entity(responseEntity).build();
    }

    @DELETE
    @Path("/{employeeId : \\d+}executing{clientId : \\d+}+{transactionNumber : \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeEmployeeFromTransaction( @PathParam("employeeId") Long employeeId,
                                                   @PathParam("clientId") Long clientId,
                                                   @PathParam("transactionNumber") Integer transactionNumber,
                                                   @BeanParam GenericBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing association between given transaction and given employee using " +
                "TransactionEmployeeRelationship.removeEmployeeFromTransaction(employeeId, clientId, transactionNumber) method of REST API");

        transactionEmployeeRelationshipManager.removeEmployeeFromTransaction(employeeId, new TransactionId(clientId, transactionNumber));

        ResponseWrapper responseEntity = new ResponseWrapper("Employee " + employeeId + " has been removed from Transaction (" + clientId + ", " + transactionNumber + ")",
                200, "Result of removing association between Transaction and Employee entities.");

        return Response.status(Status.OK).entity(responseEntity).build();
    }

    @DELETE
    @Path("/{clientId : \\d+}+{transactionNumber : \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeAllEmployeesFromTransaction( @PathParam("clientId") Long clientId,
                                                       @PathParam("transactionNumber") Integer transactionNumber,
                                                       @BeanParam GenericBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing associations for given transaction using " +
                "TransactionEmployeeRelationship.removeAllEmployeesFromTransaction(clientId, transactionNumber) method of REST API");

        Integer noOfDeleted = transactionEmployeeRelationshipManager.removeAllEmployeesFromTransaction(new TransactionId(clientId, transactionNumber));

        ResponseWrapper responseEntity = new ResponseWrapper("All " + noOfDeleted + " employee(s) have been removed from Transaction (" + clientId + ", " + transactionNumber + ")",
                200, "Result of removing all associations between Transaction and Employee entities for given Transaction.");

        return Response.status(Status.OK).entity(responseEntity).build();
    }
}
