package pl.salonea.jaxrs.exceptions;

import pl.salonea.jaxrs.utils.ErrorResponseWrapper;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by michzio on 04/09/2015.
 */
@Provider
public class UnprocessableEntityExceptionMapper implements ExceptionMapper<UnprocessableEntityException> {


    @Override
    @Produces( {MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON} )
    public Response toResponse(UnprocessableEntityException ex) {
        ErrorResponseWrapper errorResponse = new ErrorResponseWrapper(ex.getMessage(), 422, "");
        return Response.status(422).entity(errorResponse).build();
    }
}
