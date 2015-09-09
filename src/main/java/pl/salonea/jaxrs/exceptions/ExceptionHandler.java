package pl.salonea.jaxrs.exceptions;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.validation.ConstraintViolationException;

/**
 * Created by michzio on 07/09/2015.
 */
public class ExceptionHandler {

    public static final String ENTITY_CREATION_ERROR_MESSAGE = "Some error occurred while trying to create new entity instance.";
    public static final String ENTITY_UPDATE_ERROR_MESSAGE = "Some error occurred while trying to update existing entity instance.";

    public static void handleEJBException(EJBException ex) throws UnprocessableEntityException {

        Throwable t = ex;
        while ((t.getCause() != null) && !(t instanceof ConstraintViolationException)) {
            t = t.getCause();
        }

        if (t instanceof ConstraintViolationException) {
            // Here you're sure you have a ConstraintViolationException, you can handle it
            throw new UnprocessableEntityException("Some constraints have been violated during entity instance persistence: " + t.getMessage());
        }

        throw new UnprocessableEntityException("Transaction aimed at persisting entity instance has been rolled back: "  + t.getMessage());
    }

    public static void handleEJBTransactionRolledbackException(EJBTransactionRolledbackException ex) throws UnprocessableEntityException {

        Throwable t = ex;
        while ((t.getCause() != null) && !(t instanceof ConstraintViolationException)) {
            t = t.getCause();
        }

        if (t instanceof ConstraintViolationException) {
            // Here you're sure you have a ConstraintViolationException, you can handle it
            throw new UnprocessableEntityException("Some constraints have been violated during entity instance persistence: " + t.getMessage());
        }

        throw new UnprocessableEntityException("Transaction aimed at persisting entity instance has been rolled back: " + t.getMessage());
    }
}
