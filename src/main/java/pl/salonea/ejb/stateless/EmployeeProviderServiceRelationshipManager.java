package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.EmployeeProviderServiceRelationshipManagerInterface;
import pl.salonea.entities.Employee;
import pl.salonea.entities.ProviderService;
import pl.salonea.entities.idclass.ProviderServiceId;
import pl.salonea.jaxrs.exceptions.NotFoundException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Created by michzio on 27/02/2016.
 */
@Stateless
@LocalBean
public class EmployeeProviderServiceRelationshipManager
        implements EmployeeProviderServiceRelationshipManagerInterface.Local, EmployeeProviderServiceRelationshipManagerInterface.Remote {

    @Inject
    private EmployeeFacade employeeFacade;
    @Inject
    private ProviderServiceFacade providerServiceFacade;

    @Override
    public void addEmployeeSupplyProviderService(Long employeeId, ProviderServiceId providerServiceId) throws NotFoundException{

        Employee employee = employeeFacade.find(employeeId);
        if(employee == null)
            throw new NotFoundException("Employee entity could not be found for id " + employeeId);
        ProviderService providerService = providerServiceFacade.find(providerServiceId);
        if(providerService == null)
            throw new NotFoundException("Provider Service entity could not be found for id (" + providerServiceId.getProvider() + "," + providerServiceId.getService() + ")");

        employee.getSuppliedServices().add(providerService);
        providerService.getSupplyingEmployees().add(employee);
    }

    @Override
    public void removeEmployeeSupplyProviderService(Long employeeId, ProviderServiceId providerServiceId) throws NotFoundException {

        Employee employee = employeeFacade.find(employeeId);
        if(employee == null)
            throw new NotFoundException("Employee entity could not be found for id " + employeeId);
        ProviderService providerService = providerServiceFacade.find(providerServiceId);
        if(providerService == null)
            throw new NotFoundException("Provider Service entity could not be found for id (" + providerServiceId.getProvider() + "," + providerServiceId.getService() + ")");

        employee.getSuppliedServices().remove(providerService);
        providerService.getSupplyingEmployees().remove(employee);
    }
}
