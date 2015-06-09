package pl.salonea.validators;


import pl.salonea.entities.NaturalPerson;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import pl.salonea.constraints.DeliveryAddressFlagMatch;

public class DeliveryAddressFlagMatchValidator implements ConstraintValidator<DeliveryAddressFlagMatch, NaturalPerson> {

    @Override
    public void initialize(DeliveryAddressFlagMatch constraintAnnotation) {

    }

    @Override
    public boolean isValid(NaturalPerson entity, ConstraintValidatorContext context) {

        if(entity.getDeliveryAddress() == null && entity.getHomeAddress() == null)
            return true;

        if(entity.isDeliveryAsHome() && entity.getDeliveryAddress() == null)
            return true;
        if(!entity.isDeliveryAsHome() && entity.getDeliveryAddress() != null)
            return true;

        return false;
    }
}
