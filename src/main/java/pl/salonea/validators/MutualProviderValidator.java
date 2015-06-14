package pl.salonea.validators;

import pl.salonea.constraints.MutualProvider;
import pl.salonea.entities.ProviderService;
import pl.salonea.entities.WorkStation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.Serializable;

/**
 * Created by michzio on 14/06/2015.
 */
public class MutualProviderValidator implements ConstraintValidator<MutualProvider, ProviderService>, Serializable {

    @Override
    public void initialize(MutualProvider constraintAnnotation) {

    }

    @Override
    public boolean isValid(ProviderService value, ConstraintValidatorContext context) {

        if(value.getProvider() == null)
            return true;
        if(value.getWorkStations() == null)
            return true;

        for(WorkStation workStation : value.getWorkStations()) {
            Long workStationProviderId = workStation.getServicePoint().getProvider().getUserId();

            // checking whether corresponding provider ids on ProviderService and WorkStation matches !
            if(workStationProviderId != value.getProvider().getUserId())
                return false;
        }

        return true;
    }
}
