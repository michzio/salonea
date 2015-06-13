package pl.salonea.validators;

import pl.salonea.constraints.Degree;
import pl.salonea.entities.Education;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by michzio on 12/06/2015.
 */
public class DegreeValidator implements ConstraintValidator<Degree, String> {

    @Override
    public void initialize(Degree constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null)
            return true;

        // TODO check whether degree is on the list of possible university degrees
        return true;
    }
}
