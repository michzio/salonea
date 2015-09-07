package pl.salonea.jaxrs.exceptions;

import pl.salonea.jaxrs.utils.ErrorResponseWrapper;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by michzio on 03/09/2015.
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {


    @Override
    @Produces( {MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON} )
    public Response toResponse(Throwable ex)  {
        StringWriter errors = new StringWriter();
        ex.printStackTrace(new PrintWriter(errors));

        ErrorResponseWrapper errorResponse = new ErrorResponseWrapper(ex.getMessage(), 500, errors.toString());
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
    }
}
