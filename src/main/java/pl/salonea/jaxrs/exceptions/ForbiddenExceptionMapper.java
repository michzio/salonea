package pl.salonea.jaxrs.exceptions;

import pl.salonea.jaxrs.utils.ErrorResponseWrapper;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.core.Response.Status;

/**
 * Created by michzio on 04/09/2015.
 */
@Provider
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {

    @Override
    @Produces( {MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON} )
    public Response toResponse(ForbiddenException ex) {
        ErrorResponseWrapper errorResponse = new ErrorResponseWrapper(ex.getMessage(), 403, "");
        return Response.status(Status.FORBIDDEN).entity(errorResponse).build();
    }
}
