package pl.salonea.constraints;

import pl.salonea.validators.PriceTypeDependentDurationValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Created by michzio on 02/08/2015.
 */
@Constraint(validatedBy = PriceTypeDependentDurationValidator.class)
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PriceTypeDependentDuration {
    String message() default "{javax.validation.constraints.PriceTypeDependentDuration.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
