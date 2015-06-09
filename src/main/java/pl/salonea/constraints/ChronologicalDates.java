package pl.salonea.constraints;


import pl.salonea.validators.ChronologicalDatesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;


@Constraint(validatedBy = ChronologicalDatesValidator.class)
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ChronologicalDates {

    // date attributes should be specified in chronological order
    String[] dateAttributes() default {};
    Order order() default Order.ASCENDING;

    String message() default "{javax.validation.constraints.ChronologicalDates.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    enum Order { ASCENDING, DESCENDING };

    // Defines several @ChronologicalDates annotations on the same element
    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        ChronologicalDates[] value();
    }
}
