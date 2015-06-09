package pl.salonea.validators;

import pl.salonea.constraints.CompanyNumber;
import pl.salonea.entities.Firm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CompanyNumberValidator implements ConstraintValidator<CompanyNumber, Firm> {


    @Override
    public void initialize(CompanyNumber constraintAnnotation) {

    }

    @Override
    public boolean isValid(Firm value, ConstraintValidatorContext context) {

        if(value.getCompanyNumber() == null || value.getAddress() == null)
            return true;

        if(value.getAddress().getCountry() == null)
            return true;

        /* TODO Country specific Company Number check */
        return true;
    }
}
