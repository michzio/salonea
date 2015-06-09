package pl.salonea.validators;

import pl.salonea.constraints.VATIN;
import pl.salonea.entities.Firm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class VATINValidator implements ConstraintValidator<VATIN, Firm> {

    @Override
    public void initialize(VATIN constraintAnnotation) {

    }

    @Override
    public boolean isValid(Firm value, ConstraintValidatorContext context) {
        if(value.getVatin() == null || value.getAddress() == null)
            return true;

        if(value.getAddress().getCountry() == null)
            return true;

        /* TODO implement country specific VAT Identification Number check */
        return true;
    }
}
