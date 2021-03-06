package pl.salonea.constraints;

import pl.salonea.validators.FacultyValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * Created by michzio on 06/01/2016.
 */
@Constraint(validatedBy = { FacultyValidator.class })
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Faculty {
    String message() default "{javax.validation.constraints.Faculty.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
