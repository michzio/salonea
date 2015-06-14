package pl.salonea.constraints;

import pl.salonea.validators.MutualProviderValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Created by michzio on 14/06/2015.
 */
@Constraint(validatedBy = MutualProviderValidator.class)
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MutualProvider {
    String message() default "{javax.validation.constraints.MutualProvider.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
