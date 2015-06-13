package pl.salonea.constraints;

import pl.salonea.validators.DegreeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.PARAMETER;


@Constraint(validatedBy = { DegreeValidator.class })
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Degree {
    String message() default "{javax.validation.constraints.Degree.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
