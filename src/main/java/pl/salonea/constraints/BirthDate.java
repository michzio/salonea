package pl.salonea.constraints;

import pl.salonea.validators.BirthDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;

/**
 * Created by michzio on 13/07/2015.
 */
@Constraint(validatedBy = BirthDateValidator.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface BirthDate {

    String message() default "{javax.validation.constraints.BirthDate.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int minAge() default 0;
    int maxAge() default 150;
}
