package pl.salonea.jaxrs.exceptions;

/**
 * Created by michzio on 04/09/2015.
 *  For example, this error condition may occur if an XML request body contains well-formed
 *  (i.e., syntactically correct), but semantically erroneous, XML instructions.
 */
public class UnprocessableEntityException extends RuntimeException {

    public UnprocessableEntityException(String message) {
        super(message);
    }
}
