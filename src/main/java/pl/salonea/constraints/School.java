package pl.salonea.constraints;

import pl.salonea.validators.DegreeValidator;
import pl.salonea.validators.SchoolValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * Created by michzio on 12/06/2015.
 */
@Constraint(validatedBy = { SchoolValidator.class })
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface School {
    String message() default "{javax.validation.constraints.School.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
