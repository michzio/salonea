package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.ProviderServiceWorkStationRelationshipManagerInterface;
import pl.salonea.entities.ProviderService;
import pl.salonea.entities.WorkStation;
import pl.salonea.entities.idclass.ProviderServiceId;
import pl.salonea.entities.idclass.WorkStationId;
import pl.salonea.jaxrs.exceptions.NotFoundException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Created by michzio on 27/02/2016.
 */
@Stateless
@LocalBean
public class ProviderServiceWorkStationRelationshipManager
        implements ProviderServiceWorkStationRelationshipManagerInterface.Local, ProviderServiceWorkStationRelationshipManagerInterface.Remote {

    @Inject
    private ProviderServiceFacade providerServiceFacade;
    @Inject
    private WorkStationFacade workStationFacade;

    @Override
    public void addProviderServiceOnWorkStation(ProviderServiceId providerServiceId, WorkStationId workStationId) throws NotFoundException {

        ProviderService providerService = providerServiceFacade.find(providerServiceId);
        if(providerService == null)
            throw new NotFoundException("Provider Service entity could not be found for id (" + providerServiceId.getProvider() + "," + providerServiceId.getService() + ")");
        WorkStation workStation = workStationFacade.find(workStationId);
        if(workStation == null)
            throw new NotFoundException("Work Station entity could not be found for id (" + workStationId.getServicePoint().getProvider() + "," + workStationId.getServicePoint().getServicePointNumber() + "," + workStationId.getWorkStationNumber() + ")");

        providerService.getWorkStations().add(workStation);
        workStation.getProvidedServices().add(providerService);
    }

    @Override
    public void removeProviderServiceOnWorkStation(ProviderServiceId providerServiceId, WorkStationId workStationId) throws NotFoundException {

        ProviderService providerService = providerServiceFacade.find(providerServiceId);
        if(providerService == null)
            throw new NotFoundException("Provider Service entity could not be found for id (" + providerServiceId.getProvider() + "," + providerServiceId.getService() + ")");
        WorkStation workStation = workStationFacade.find(workStationId);
        if(workStation == null)
            throw new NotFoundException("Work Station entity could not be found for id (" + workStationId.getServicePoint().getProvider() + "," + workStationId.getServicePoint().getServicePointNumber() + "," + workStationId.getWorkStationNumber() + ")");

        providerService.getWorkStations().remove(workStation);
        workStation.getProvidedServices().remove(providerService);
    }
}
