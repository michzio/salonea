package pl.salonea.constraints;

import pl.salonea.validators.StatisticNumberValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StatisticNumberValidator.class)
public @interface StatisticNumber {
    String message() default "javax.validation.constraints.StatisticNumber.message";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
