package pl.salonea.jaxrs.utils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by michzio on 02/09/2015.
 */
public class RESTDateTime extends Date {

    private static final SimpleDateFormat dfz = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ" );
    private static final SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss" );

    public RESTDateTime( String datetimeString ) throws WebApplicationException {

        try {
            this.setTime( dfz.parse(datetimeString).getTime() );
        } catch ( final ParseException ex1 ) {
            try {
                this.setTime( df.parse(datetimeString).getTime() );
            } catch( final ParseException ex2 ) {
                throw new RESTDateTimeException("Date format could not be parsed. You should provide date in one of the following formats: " +
                        dfz.toPattern() + " or " + df.toPattern() + ".");
            }
        }
    }


    public class RESTDateTimeException extends WebApplicationException {

        public RESTDateTimeException(String message) {

            super(Response.status(Status.BAD_REQUEST).entity(new ErrorResponseWrapper(message, 400, "")).build());
        }
    }
}
