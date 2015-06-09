package pl.salonea.validators;

import pl.salonea.constraints.CorporateOwner;
import pl.salonea.entities.Provider;
import pl.salonea.enums.ProviderType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class CorporateOwnerValidator implements ConstraintValidator<CorporateOwner, Provider> {

    @Override
    public void initialize(CorporateOwner constraintAnnotation) {

    }

    @Override
    public boolean isValid(Provider value, ConstraintValidatorContext context) {

        if(value.getType() == ProviderType.SIMPLE)
            return true;

        // else Provider requires to have Corporation association
        if(value.getCorporation() != null)
            return true;

        return false;
    }
}
