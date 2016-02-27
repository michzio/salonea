package pl.salonea.ejb.interfaces;

import pl.salonea.entities.idclass.ProviderServiceId;
import pl.salonea.entities.idclass.WorkStationId;

/**
 * Created by michzio on 27/02/2016.
 */
public interface ProviderServiceWorkStationRelationshipManagerInterface {

    void addProviderServiceOnWorkStation(ProviderServiceId providerServiceId, WorkStationId workStationId);
    void removeProviderServiceOnWorkStation(ProviderServiceId providerServiceId, WorkStationId workStationId);

    @javax.ejb.Remote
    interface Remote extends ProviderServiceWorkStationRelationshipManagerInterface { }

    @javax.ejb.Local
    interface Local extends ProviderServiceWorkStationRelationshipManagerInterface { }
}
