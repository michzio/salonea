package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.ProviderIndustryRelationshipManager;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.utils.RESTToolkit;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.utils.ResponseWrapper;

/**
 * Created by michzio on 15/11/2015.
 */
@Path("/providers-in-industries/{providerId : \\d+}in{industryId : \\d+}")
public class ProviderIndustryRelationship {

    private static final Logger logger = Logger.getLogger(ProviderIndustryRelationship.class.getName());

    @Inject
    ProviderIndustryRelationshipManager providerIndustryRelationshipManager;

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response addProviderToIndustry( @PathParam("providerId") Long providerId,
                                           @PathParam("industryId") Long industryId,
                                           @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "adding association between given provider and given industry using " +
                "ProviderIndustryRelationship.addProviderToIndustry(providerId, industryId) method of REST API");

        providerIndustryRelationshipManager.addProviderToIndustry(providerId, industryId);

        ResponseWrapper responseEntity = new ResponseWrapper("Provider " + providerId + " has been added to Industry " + industryId,
                200, "Result of creating association between Provider and Industry entities.");

        return Response.status(Response.Status.OK).entity(responseEntity).build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeProviderFromIndustry( @PathParam("providerId") Long providerId,
                                                @PathParam("industryId") Long industryId,
                                                @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing association between given provider and given industry using " +
                "ProviderIndustryRelationship.removeProviderFromIndustry(providerId, industryId) method of REST API");

        providerIndustryRelationshipManager.removeProviderFromIndustry(providerId, industryId);

        ResponseWrapper responseEntity = new ResponseWrapper("Provider " + providerId + " has been removed from Industry " + industryId,
                200, "Result of removing association between Provider and Industry entities.");

        return Response.status(Response.Status.OK).entity(responseEntity).build();
    }

}
