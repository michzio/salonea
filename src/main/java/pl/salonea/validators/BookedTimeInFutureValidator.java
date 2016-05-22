package pl.salonea.validators;

import pl.salonea.constraints.BookedTimeInFuture;
import pl.salonea.mapped_superclasses.AbstractTransaction;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * Created by michzio on 17/05/2016.
 */
public class BookedTimeInFutureValidator implements ConstraintValidator<BookedTimeInFuture, AbstractTransaction> {

    @Override
    public void initialize(BookedTimeInFuture constraintAnnotation) {

    }

    @Override
    public boolean isValid(AbstractTransaction value, ConstraintValidatorContext context) {

        if(value == null || value.getBookedTime() == null)
            return true;

        Date bookedTime = value.getBookedTime();

        // get now using Java 8
        LocalDateTime ldt = LocalDateTime.now();
        Instant instant = ldt.toInstant(ZoneOffset.UTC);
        Date now = Date.from(instant);

        // check if bookedTime is in the future (later than now)
        if(bookedTime.after(now))
            return true;

        return false;
    }
}
