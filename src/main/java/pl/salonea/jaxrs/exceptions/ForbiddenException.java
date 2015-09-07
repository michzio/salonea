package pl.salonea.jaxrs.exceptions;

/**
 * Created by michzio on 04/09/2015.
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
