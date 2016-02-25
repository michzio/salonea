package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.ServicePointPhotoTagRelationshipManager;
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
 * Created by michzio on 25/02/2016.
 */
@Path("/service-point-photos-have-tags/{photoId : \\d+}has{tagId : \\d+}")
public class ServicePointPhotoTagRelationship {

    private static final Logger logger = Logger.getLogger(ServicePointPhotoTagRelationship.class.getName());

    @Inject
    private ServicePointPhotoTagRelationshipManager photoTagRelationshipManager;

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response addTagToServicePointPhoto( @PathParam("tagId") Long tagId,
                                               @PathParam("photoId") Long photoId,
                                               @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "adding association between given service point photo and given tag using " +
                "ServicePointPhotoTagRelationship.addTagToServicePointPhoto(tagId, photoId) method of REST API");

        photoTagRelationshipManager.addTagToServicePointPhoto(tagId, photoId);

        ResponseWrapper responseEntity = new ResponseWrapper("Tag " + tagId + " has been added to Service Point Photo " + photoId,
                200, "Result of creating association between Service Point Photo and Tag entities.");

        return Response.status(Status.OK).entity(responseEntity).build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeTagFromServicePointPhoto( @PathParam("tagId") Long tagId,
                                                    @PathParam("photoId") Long photoId,
                                                    @BeanParam GenericBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "removing association between given service point photo and given tag using " +
                "ServicePointPhotoTagRelationship.removeTagFromServicePointPhoto(tagId, photoId) method of REST API");

        photoTagRelationshipManager.removeTagFromServicePointPhoto(tagId, photoId);

        ResponseWrapper responseEntity = new ResponseWrapper("Tag " + tagId + " has been removed from Service Point Photo " + photoId,
                200, "Result of removing association between Service Point Photo and Tag entities.");

        return Response.status(Status.OK).entity(responseEntity).build();
    }
}
