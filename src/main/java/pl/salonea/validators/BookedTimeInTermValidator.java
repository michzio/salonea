package pl.salonea.validators;


import pl.salonea.constraints.BookedTimeInTerm;
import pl.salonea.entities.Term;
import pl.salonea.mapped_superclasses.AbstractTransaction;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Date;

public class BookedTimeInTermValidator implements ConstraintValidator<BookedTimeInTerm, AbstractTransaction> {

    @Override
    public void initialize(BookedTimeInTerm constraintAnnotation) {

    }

    @Override
    public boolean isValid(AbstractTransaction value, ConstraintValidatorContext context) {

        if(value == null || value.getTerm() == null)
            return true;

        Term term = value.getTerm();

        if(term.getOpeningTime() == null || term.getClosingTime() == null)
            return true;

        if(value.getBookedTime() == null)
            return true;

        Date bookedTime = value.getBookedTime();

        // check if bookedTime is contained between openingTime and closingTime
       if(bookedTime.compareTo(term.getOpeningTime()) >= 0 && bookedTime.compareTo(term.getClosingTime()) <= 0)
           return true;

        return false;
    }
}
