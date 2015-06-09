package pl.salonea.constraints;


import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.TYPE;


@ChronologicalDates.List({
    @ChronologicalDates(dateAttributes = {"registrationDate", "lastLogged"}, order = ChronologicalDates.Order.ASCENDING),
    @ChronologicalDates(dateAttributes = {"registrationDate", "lastFailedLogin"}, order = ChronologicalDates.Order.ASCENDING)
})
@Target({ TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@ReportAsSingleViolation
public @interface ChronologicalAccountDates {

    String message() default "{javax.validation.constraints.ChronologicalAccountDates.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
