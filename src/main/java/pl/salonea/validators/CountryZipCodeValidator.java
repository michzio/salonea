package pl.salonea.validators;


import pl.salonea.constraints.CountryZipCode;
import pl.salonea.embeddables.Address;
import pl.salonea.qualifiers.CDIValidation;
import pl.salonea.qualifiers.Country;
import pl.salonea.qualifiers.CountryQualifierLiteral;
import pl.salonea.utils.zip_codes.ZipCodeChecker;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.logging.Level;
import java.util.logging.Logger;

@CDIValidation
public class CountryZipCodeValidator implements ConstraintValidator<CountryZipCode, Address> {

    private static final Logger logger = Logger.getLogger(CountryZipCodeValidator.class.getName());

    @Inject @Any
    private Instance<ZipCodeChecker> checkerInstance;

    @Override
    public void initialize(CountryZipCode constraintAnnotation) {

    }

    @Override
    public boolean isValid(Address value, ConstraintValidatorContext context) {

        logger.log(Level.INFO, "Validating zip code format for typed country.");

        if(value.getZipCode() == null || value.getCountry() == null)
            return true;

        // selecting ZipCodeChecker instance specific to country name set in Address object
        ZipCodeChecker checker = checkerInstance.select(new CountryQualifierLiteral(value.getCountry())).get();

        if (checker == null) {
                logger.log(Level.INFO, "CountryZipCodeChecker not selected from Instance<ZipCodeChecker> in CountryZipCodeValidator.");
                return false;
        }

        return checker.isFormatValid(value.getZipCode());
    }
}
