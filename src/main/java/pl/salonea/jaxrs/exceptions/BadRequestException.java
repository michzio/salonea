package pl.salonea.jaxrs.exceptions;

/**
 * Created by michzio on 03/09/2015.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

}
