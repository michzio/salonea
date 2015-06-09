package pl.salonea.validators;

import pl.salonea.constraints.EmailAvailability;
import pl.salonea.entities.UserAccount;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.logging.Logger;

/**
 * Created by michzio on 08/06/2015.
 */
public class EmailAvailabilityValidator implements ConstraintValidator<EmailAvailability, String> {

    private static final Logger logger = Logger.getLogger(EmailAvailability.class.getName());

    @Override
    public void initialize(EmailAvailability constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if(value == null)
            return true;

        // check if email address is available and not actually stored in database
        /* TODO jpql datasource and check email availability */
        return true;
    }
}
