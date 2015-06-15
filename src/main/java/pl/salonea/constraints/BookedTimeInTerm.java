package pl.salonea.constraints;

import pl.salonea.validators.BookedTimeInTermValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;

@Constraint(validatedBy = BookedTimeInTermValidator.class)
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface BookedTimeInTerm {

    String message() default "{javax.validation.constraints.BookedTimeInTerm.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
