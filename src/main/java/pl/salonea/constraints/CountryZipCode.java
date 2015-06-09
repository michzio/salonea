package pl.salonea.constraints;

import pl.salonea.validators.CountryZipCodeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CountryZipCodeValidator.class)
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CountryZipCode {

    String message() default "{javax.validation.constraints.ZipCodeCountryMatch.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
