package pl.salonea.constraints;


import pl.salonea.validators.DeliveryAddressFlagMatchValidator;
import pl.salonea.validators.VATINValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = VATINValidator.class)
public @interface VATIN {
    String message() default "javax.validation.constraints.VATIN.message";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
