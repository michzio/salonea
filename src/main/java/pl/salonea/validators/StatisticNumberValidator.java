package pl.salonea.validators;

import pl.salonea.constraints.StatisticNumber;
import pl.salonea.entities.Firm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class StatisticNumberValidator implements ConstraintValidator<StatisticNumber, Firm> {
    @Override
    public void initialize(StatisticNumber constraintAnnotation) {

    }

    @Override
    public boolean isValid(Firm value, ConstraintValidatorContext context) {
        if(value.getStatisticNumber() == null || value.getAddress() == null)
            return true;

        if(value.getAddress().getCountry() == null)
            return true;

        /* TODO country specific Statistic Number check */
        return true;
    }
}
