package pl.salonea.constraints;

import pl.salonea.validators.MutualProviderValidator;
import pl.salonea.validators.PriceNeedTypeValidator;

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
@Constraint(validatedBy = PriceNeedTypeValidator.class)
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PriceNeedType {
    String message() default "{javax.validation.constraints.PriceNeedType.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
