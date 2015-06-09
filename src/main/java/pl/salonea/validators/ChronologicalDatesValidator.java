package pl.salonea.validators;


import pl.salonea.constraints.ChronologicalDates;
import pl.salonea.utils.lang.ReflectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ChronologicalDatesValidator implements ConstraintValidator<ChronologicalDates, Object> {

    private String[] dateAttributes;
    private ChronologicalDates.Order order;

    @Override
    public void initialize(ChronologicalDates constraintAnnotation) {

        dateAttributes = constraintAnnotation.dateAttributes();
        order = constraintAnnotation.order();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {

        if (dateAttributes == null || dateAttributes.length == 0)
            return true;

        // check whether date attributes on the list (array)
        // are in chronological order (ASCENDING/DESCENDING)
        Date prevDate = null;

        // get current object class
        Class<?> c = object.getClass();


        for (String dateAttribute : dateAttributes) {

            // read each declared dateAttribute from object
            try {
                Field field = ReflectionUtils.findField(c, dateAttribute);
                field.setAccessible(true);
                Date nextDate = (Date) field.get(object);

                if (nextDate == null)
                    continue;

                if (prevDate == null) {
                    prevDate = nextDate;
                    continue;
                }

                if (order == ChronologicalDates.Order.ASCENDING && nextDate.compareTo(prevDate) < 0)
                    return false;

                if (order == ChronologicalDates.Order.DESCENDING && nextDate.compareTo(prevDate) > 0)
                    return false;

                prevDate = nextDate;

            } catch (IllegalAccessException e){
                    e.printStackTrace();
            } catch (NoSuchFieldException e) {
                    e.printStackTrace();
            }


        }

        return true;
    }

}