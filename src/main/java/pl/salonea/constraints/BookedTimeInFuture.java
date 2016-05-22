package pl.salonea.constraints;

import pl.salonea.validators.BookedTimeInFutureValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Created by michzio on 17/05/2016.
 */
@Constraint(validatedBy = BookedTimeInFutureValidator.class)
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface BookedTimeInFuture {

    String message() default "{javax.validation.constraints.BookedTimeInFuture.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
