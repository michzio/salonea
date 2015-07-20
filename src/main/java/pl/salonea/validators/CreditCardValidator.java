package pl.salonea.validators;

import org.luhn.Luhn;
import pl.salonea.constraints.CreditCardValidity;
import pl.salonea.entities.CreditCard;
import pl.salonea.qualifiers.CDIValidation;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by michzio on 10/06/2015.
 */
public class CreditCardValidator implements ConstraintValidator<CreditCardValidity, CreditCard> {

    @Override
    public void initialize(CreditCardValidity constraintAnnotation) {

    }

    @Override
    public boolean isValid(CreditCard value, ConstraintValidatorContext context) {

        if(value.getCreditCardNumber() == null)
            return true;

        // credit card number validation using Luhn's algorithm
        // Luhn's checksum is suitable for most of credit card providers
        // exception are: Diners Club enRout, China UnionPay
        // TODO implement alternative check for other providers, or credit card specific check:
        // Visa = 4XXX - XXXX - XXXX - XXXX
        // MasterCard = 5[1-5]XX - XXXX - XXXX - XXXX
        // Discover = 6011 - XXXX - XXXX - XXXX
        // Amex = 3[4,7]X - XXXX - XXXX - XXXX
        // Diners = 3[0,6,8] - XXXX - XXXX - XXXX
        // Any Bankcard = 5610 - XXXX - XXXX - XXXX
        // JCB =  [3088|3096|3112|3158|3337|3528] - XXXX - XXXX - XXXX
        // Enroute = [2014|2149] - XXXX - XXXX - XXX
        // Switch = [4903|4911|4936|5641|6333|6759|6334|6767] - XXXX - XXXX - XXXX

        Boolean valid =  Luhn.validate(value.getCreditCardNumber());

        return valid;
    }
}
