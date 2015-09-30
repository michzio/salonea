package pl.salonea.jaxrs.wrappers;

import pl.salonea.entities.Employee;
import pl.salonea.entities.ProviderService;
import pl.salonea.entities.WorkStation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by michzio on 29/09/2015.
 */
@XmlRootElement(name = "provider-service")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ProviderServiceWrapper {

    private ProviderService providerService;
    private Set<Employee> employees;
    private Set<WorkStation> workStations;

    // default no-args constructor
    public ProviderServiceWrapper() { }

    public ProviderServiceWrapper(ProviderService providerService) {
        this.providerService = providerService;
        this.employees = providerService.getSupplyingEmployees();
        this.workStations = providerService.getWorkStations();
    }

    public static List<ProviderServiceWrapper> wrap(List<ProviderService> providerServices) {

        List<ProviderServiceWrapper> wrappedProviderServices = new ArrayList<>();

        for(ProviderService providerService : providerServices)
            wrappedProviderServices.add(new ProviderServiceWrapper(providerService));

        return wrappedProviderServices;
    }

    @XmlElement(name = "entity", nillable = true)
    public ProviderService getProviderService() {
        return providerService;
    }

    public void setProviderService(ProviderService providerService) {
        this.providerService = providerService;
    }

    @XmlElement(name = "employees", nillable = true)
    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    @XmlElement(name = "work-stations", nillable = true)
    public Set<WorkStation> getWorkStations() {
        return workStations;
    }

    public void setWorkStations(Set<WorkStation> workStations) {
        this.workStations = workStations;
    }
}
