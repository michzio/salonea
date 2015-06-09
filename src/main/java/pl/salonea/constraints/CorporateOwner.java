package pl.salonea.constraints;

import pl.salonea.validators.CorporateOwnerValidator;
import pl.salonea.validators.CountryZipCodeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Constraint enforces that if Provider has
 * CORPORATE or FRANCHISE type that this
 * corporate owner is set by relationship
 * to Corporation.
 */
@Constraint(validatedBy = CorporateOwnerValidator.class)
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CorporateOwner {

    String message() default "{javax.validation.constraints.CorporateOwner.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
