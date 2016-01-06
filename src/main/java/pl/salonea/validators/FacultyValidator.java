package pl.salonea.validators;

import pl.salonea.constraints.Faculty;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by michzio on 06/01/2016.
 */
public class FacultyValidator implements ConstraintValidator<Faculty, String> {
    @Override
    public void initialize(Faculty constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null)
            return true;

        // TODO check if given faculty is on the list of all possible faculties
        return true;
    }
}
