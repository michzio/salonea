package pl.salonea.ejb.interfaces;

import pl.salonea.entities.idclass.ProviderServiceId;

/**
 * Created by michzio on 27/02/2016.
 */
public interface EmployeeProviderServiceRelationshipManagerInterface {

    void addEmployeeSupplyProviderService(Long employeeId, ProviderServiceId providerServiceId);
    void removeEmployeeSupplyProviderService(Long employeeId, ProviderServiceId providerServiceId);

    @javax.ejb.Remote
    interface Remote extends EmployeeProviderServiceRelationshipManagerInterface { }

    @javax.ejb.Local
    interface Local extends EmployeeProviderServiceRelationshipManagerInterface { }
}
