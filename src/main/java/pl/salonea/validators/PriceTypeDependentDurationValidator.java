package pl.salonea.validators;

import pl.salonea.constraints.PriceTypeDependentDuration;
import pl.salonea.entities.ProviderService;
import pl.salonea.enums.PriceType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by michzio on 02/08/2015.
 */
public class PriceTypeDependentDurationValidator implements ConstraintValidator<PriceTypeDependentDuration, ProviderService> {

    @Override
    public void initialize(PriceTypeDependentDuration constraintAnnotation) { }

    @Override
    public boolean isValid(ProviderService value, ConstraintValidatorContext context) {

        if(value.getPriceType() == null) return true;

        switch(value.getPriceType()) {
            case PER_SERVICE:
                return true;
            case PER_HOUR:
                if(value.getServiceDuration() == (60 * 60 * 1000L) ) return true;
                return false;
            case PER_DAY:
                if(value.getServiceDuration() == (24 * 60 * 60 * 1000L)) return true;
                return false;
            default:
                return false;
        }
    }
}
