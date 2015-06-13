package pl.salonea.validators;

import pl.salonea.constraints.School;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by michzio on 12/06/2015.
 */
public class SchoolValidator implements ConstraintValidator<School, String> {
    @Override
    public void initialize(School constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null)
            return true;

        // TODO check if given school is on the list of all possible schools
        return true;
    }
}
