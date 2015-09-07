package pl.salonea.jaxrs.exceptions;

import pl.salonea.jaxrs.utils.ErrorResponseWrapper;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by michzio on 03/09/2015.
 */
@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Override
    @Produces( {MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON} )
    public Response toResponse(NotFoundException ex) {
        ErrorResponseWrapper errorResponse = new ErrorResponseWrapper(ex.getMessage(), 404, "");
        return Response.status(Status.NOT_FOUND).entity(errorResponse).build();
    }
}
