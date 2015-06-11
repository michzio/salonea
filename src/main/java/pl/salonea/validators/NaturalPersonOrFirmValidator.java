package pl.salonea.validators;

import pl.salonea.constraints.NaturalPersonOrFirm;
import pl.salonea.entities.Client;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class NaturalPersonOrFirmValidator implements ConstraintValidator<NaturalPersonOrFirm, Client> {

    @Override
    public void initialize(NaturalPersonOrFirm constraintAnnotation) {

    }

    @Override
    public boolean isValid(Client value, ConstraintValidatorContext context) {

        // we remain possibility for client to be neither natural person nor firm
        // e.g. temporary client that can make transactions about which data are also stored in DB
        if(value.getNaturalPerson() == null || value.getFirm() == null)
            return true;

        // it is not valid for Client to have set both NaturalPerson and Firm
        return false;
    }
}
