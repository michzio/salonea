package pl.salonea.constraints;

import pl.salonea.validators.NaturalPersonOrFirmValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Constraint(validatedBy = NaturalPersonOrFirmValidator.class )
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface NaturalPersonOrFirm {

    String message() default "{javax.validation.constraints.NaturalPersonOrFirm.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
