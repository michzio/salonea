package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.HistoricalTransactionEmployeeRelationshipManager;
import pl.salonea.entities.idclass.TransactionId;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.exceptions.*;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResponseWrapper;

import javax.inject.Inject;
import javax.ws.rs.*;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by michzio on 08/06/2016.
 */
@Path("/employees-executing-historical-transactions")
public class HistoricalTransactionEmployeeRelationship {

    private static final Logger logger = Logger.getLogger(HistoricalTransactionEmployeeRelationship.class.getName());

    @Inject
    HistoricalTransactionEmployeeRelationshipManager historicalTransactionEmployeeRelationshipManager;

    @POST
    @Path("/{employeeId : \\d+}executing{clientId : \\d+}+{transactionNumber : \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response addEmployeeToHistoricalTransaction( @PathParam("employeeId") Long employeeId,
                                                        @PathParam("clientId") Long clientId,
                                                        @PathParam("transactionNumber") Integer transactionNumber,
                                                        @BeanParam GenericBeanParam params ) throws pl.salonea.jaxrs.exceptions.ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "adding association between given historical transaction and given employee using " +
                "HistoricalTransactionEmployeeRelationship.addEmployeeToHistoricalTransaction(employeeId, clientId, transactionNumber) method of REST API");

        historicalTransactionEmployeeRelationshipManager.addEmployeeToHistoricalTransaction(employeeId, new TransactionId(clientId, transactionNumber));

        ResponseWrapper responseEntity = new ResponseWrapper("Employee " + employeeId + " has been added to Historical Transaction (" + clientId + "," + transactionNumber + ")",
                200, "Result of creating association between Historical Transaction and Employee entities.");

        return Response.status(Status.OK).entity(responseEntity).build();
    }

    @DELETE
    @Path("/{employeeId : \\d+}executing{clientId : \\d+}+{transactionNumber : \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeEmployeeFromHistoricalTransaction( @PathParam("employeeId") Long employeeId,
                                                             @PathParam("clientId") Long clientId,
                                                             @PathParam("transactionNumber") Integer transactionNumber,
                                                             @BeanParam GenericBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing association between given historical transaction and given employee using " +
                "HistoricalTransactionEmployeeRelationship.removeEmployeeFromHistoricalTransaction(employeeId, clientId, transactionNumber) method of REST API");

        historicalTransactionEmployeeRelationshipManager.removeEmployeeFromHistoricalTransaction(employeeId, new TransactionId(clientId, transactionNumber));

        ResponseWrapper responseEntity = new ResponseWrapper("Employee " + employeeId + " has been removed from Historical Transaction (" + clientId + ", " + transactionNumber + ")",
                200, "Result of removing association between Historical Transaction and Employee entities.");

        return Response.status(Status.OK).entity(responseEntity).build();
    }

    @DELETE
    @Path("/{clientId : \\d+}+{transactionNumber : \\d+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeAllEmployeesFromHistoricalTransaction( @PathParam("clientId") Long clientId,
                                                                 @PathParam("transactionNumber") Integer transactionNumber,
                                                                 @BeanParam GenericBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing associations for given historical transactions using " +
                "HistoricalTransactionEmployeeRelationship.removeAllEmployeesFromHistoricalTransaction(clientId, transactionNumber) method of REST API");

        Integer noOfDeleted = historicalTransactionEmployeeRelationshipManager.removeAllEmployeesFromHistoricalTransaction(new TransactionId(clientId, transactionNumber));

        ResponseWrapper responseEntity = new ResponseWrapper("All " + noOfDeleted + " employee(s) have been removed from Historical Transaction (" + clientId + ", " + transactionNumber + ")",
                200, "Result of removing all associations between Historical Transaction and Employee entities for given Historical Transaction.");

        return Response.status(Status.OK).entity(responseEntity).build();
    }
}
