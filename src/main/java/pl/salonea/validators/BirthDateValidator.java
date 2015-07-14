package pl.salonea.validators;

import pl.salonea.constraints.BirthDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by michzio on 13/07/2015.
 */
public class BirthDateValidator implements ConstraintValidator<BirthDate, Date> {

    private int minAge;
    private int maxAge;

    @Override
    public void initialize(BirthDate birthDate) {
        this.minAge = birthDate.minAge();
        this.maxAge = birthDate.maxAge();
    }

    @Override
    public boolean isValid(Date value, ConstraintValidatorContext context) {

        if(value == null) return true;

        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(value);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
        if(today.get(Calendar.MONTH) < birthDate.get(Calendar.MONTH)) {
            age--;
        } else if (today.get(Calendar.MONTH) == birthDate.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) < birthDate.get(Calendar.DAY_OF_MONTH)) {
            age--;
        }

        if(age > minAge && age < maxAge)
            return true;

        return false;
    }
}

