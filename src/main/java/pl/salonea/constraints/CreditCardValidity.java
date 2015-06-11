package pl.salonea.constraints;

import pl.salonea.validators.CountryZipCodeValidator;
import pl.salonea.validators.CreditCardValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Created by michzio on 10/06/2015.
 */
@Constraint(validatedBy = CreditCardValidator.class)
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CreditCardValidity {

    String message() default "{javax.validation.constraints.CreditCardValidity.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
