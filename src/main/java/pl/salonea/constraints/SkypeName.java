package pl.salonea.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.CONSTRUCTOR;

/**
 Skype Name is your unique username for Skype. It must be between 6-32 characters,
 start with a letter and contain only letters and numbers (no spaces or special characters).
 */
@Pattern(regexp = "[a-zA-Z][a-zA-Z0-9\\.,\\-_]{5,31}")
@Constraint(validatedBy = {})
@Target({FIELD, METHOD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface SkypeName {
    String message() default "{javax.validation.constraints.SkypeName.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
