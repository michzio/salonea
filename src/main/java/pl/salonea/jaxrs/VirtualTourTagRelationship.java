package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.VirtualTourTagRelationshipManager;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
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
 * Created by michzio on 26/02/2016.
 */
@Path("/virtual-tours-have-tags/{tourId : \\d+}has{tagId : \\d+}")
public class VirtualTourTagRelationship {

    private static final Logger logger = Logger.getLogger(VirtualTourTagRelationship.class.getName());

    @Inject
    private VirtualTourTagRelationshipManager virtualTourTagRelationshipManager;

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response addTagToVirtualTour( @PathParam("tagId") Long tagId,
                                         @PathParam("tourId") Long tourId,
                                         @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "adding association between given virtual tour and given tag using " +
                "VirtualTourTagRelationship.addTagToVirtualTour(tagId, tourId) method of REST API");

        virtualTourTagRelationshipManager.addTagToVirtualTour(tagId, tourId);

        ResponseWrapper responseEntity = new ResponseWrapper("Tag " + tagId + " has been added to Virtual Tour " + tourId,
                200, "Result of creating association between Virtual Tour and Tag entities.");

        return Response.status(Status.OK).entity(responseEntity).build();
    }


    @DELETE
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeTagFromVirtualTour( @PathParam("tagId") Long tagId,
                                              @PathParam("tourId") Long tourId,
                                              @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing association between given virtual tour and given tag using " +
                "VirtualTourTagRelationship.removeTagFromVirtualTour(tagId, tourId) method of REST API");

        virtualTourTagRelationshipManager.removeTagFromVirtualTour(tagId, tourId);

        ResponseWrapper responseEntity = new ResponseWrapper("Tag " + tagId + " has been removed from Virtual Tour " + tourId,
                200, "Result of removing association between Virtual Tour and Tag entities.");

        return Response.status(Status.OK).entity(responseEntity).build();
    }
}
