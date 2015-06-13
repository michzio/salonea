package pl.salonea.constraints;


import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.PARAMETER;

@Pattern(regexp = "([A-Z][\\-\\_a-zA-Z]*)(\\s[\\-\\_a-zA-Z]*)*(\\s[\\d]+)?")
@Constraint(validatedBy = {}) // constraints composition
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CategoryPhrase {

    String message() default "{javax.validation.constraints.CategoryPhrase.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
