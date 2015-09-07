package pl.salonea.jaxrs.exceptions;

/**
 * Created by michzio on 03/09/2015.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
