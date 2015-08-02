package pl.salonea.validators;

import pl.salonea.constraints.PriceNeedType;
import pl.salonea.entities.ProviderService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by michzio on 02/08/2015.
 */
public class PriceNeedTypeValidator implements ConstraintValidator<PriceNeedType, ProviderService> {

    @Override
    public void initialize(PriceNeedType constraintAnnotation) { }

    @Override
    public boolean isValid(ProviderService value, ConstraintValidatorContext context) {

        if(value.getPrice() == null) return true;

        if(value.getPriceType() != null) return true;

        return false;
    }
}
