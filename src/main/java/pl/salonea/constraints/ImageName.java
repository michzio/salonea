package pl.salonea.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.PARAMETER;

@Size(min=3)
@Pattern(regexp = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp|jpeg))$)",
        message = "Image name doesn't match regex pattern.")
@ReportAsSingleViolation
@Constraint(validatedBy = {}) // constraints composition
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ImageName {

    String message() default "{javax.validation.constraints.ImageName.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
