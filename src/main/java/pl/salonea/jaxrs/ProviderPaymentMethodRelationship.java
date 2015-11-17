package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.ProviderPaymentMethodRelationshipManager;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResponseWrapper;

/**
 * Created by michzio on 17/11/2015.
 */
@Path("/providers-accept-payment-methods/{providerId : \\d+}accept{paymentMethodId : \\d+}")
public class ProviderPaymentMethodRelationship {

    private static final Logger logger = Logger.getLogger(ProviderPaymentMethodRelationship.class.getName());

    @Inject
    ProviderPaymentMethodRelationshipManager providerPaymentMethodRelationshipManager;

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response addProviderAcceptPaymentMethod( @PathParam("providerId") Long providerId,
                                                    @PathParam("paymentMethodId") Integer paymentMethodId,
                                                    @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "adding association between given provider and given payment method using " +
                "ProviderPaymentMethodRelationship.addProviderAcceptPaymentMethod(providerId, paymentMethodId) method of REST API");

        providerPaymentMethodRelationshipManager.addProviderAcceptPaymentMethod(providerId, paymentMethodId);

        ResponseWrapper responseEntity = new ResponseWrapper("Provider " + providerId + " has added acceptance of Payment Method " + paymentMethodId,
                200, "Result of creating association between Provider and Payment Method entities.");

        return Response.status(Response.Status.OK).entity(responseEntity).build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeProviderAcceptPaymentMethod( @PathParam("providerId") Long providerId,
                                                       @PathParam("paymentMethodId") Integer paymentMethodId,
                                                       @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing association between given provider and given payment method using " +
                "ProviderPaymentMethodRelationship.removeProviderAcceptPaymentMethod(providerId, paymentMethodId) method of REST API");

        providerPaymentMethodRelationshipManager.removeProviderAcceptPaymentMethod(providerId, paymentMethodId);

        ResponseWrapper responseEntity = new ResponseWrapper("Provider " + providerId + " has removed acceptance of Payment Method " + paymentMethodId,
                200, "Result of removing association between Provider and Payment Method entities.");

        return Response.status(Response.Status.OK).entity(responseEntity).build();
    }


}
