package pl.salonea.constraints;

import pl.salonea.validators.DeliveryAddressFlagMatchValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Created by michzio on 05/06/15.
 * This constraint checks whether
 * there is consistancy between
 * delivery address flag and delivery address object
 */
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DeliveryAddressFlagMatchValidator.class)
public @interface DeliveryAddressFlagMatch {

    String message() default "javax.validation.constraints.DeliveryAddressFlagMatch.message";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
