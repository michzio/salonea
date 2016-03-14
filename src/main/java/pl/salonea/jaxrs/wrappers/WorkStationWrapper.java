package pl.salonea.jaxrs.wrappers;

import pl.salonea.entities.EmployeeTerm;
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
 * Created by michzio on 24/01/2016.
 */
@XmlRootElement(name = "work-station")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class WorkStationWrapper {

    private WorkStation workStation;
    private Set<EmployeeTerm> termsEmployeesWorkOn;
    private Set<ProviderService> providedServices;

    // default no-args constructor
    public WorkStationWrapper() { }

    public WorkStationWrapper(WorkStation workStation) {
        this.workStation = workStation;
        this.termsEmployeesWorkOn = workStation.getTermsEmployeesWorkOn();
        this.providedServices = workStation.getProvidedServices();
    }

    public static List<WorkStationWrapper> wrap( List<WorkStation> workStations ) {

        List<WorkStationWrapper> wrappedWorkStations = new ArrayList<>();

        for(WorkStation workStation : workStations)
            wrappedWorkStations.add( new WorkStationWrapper(workStation) );

        return wrappedWorkStations;
    }

    @XmlElement(name = "entity", nillable = true)
    public WorkStation getWorkStation() {
        return workStation;
    }

    public void setWorkStation(WorkStation workStation) {
        this.workStation = workStation;
    }

    @XmlElement(name = "termsEmployeesWorkOn", nillable = true)
    public Set<EmployeeTerm> getTermsEmployeesWorkOn() {
        return termsEmployeesWorkOn;
    }

    public void setTermsEmployeesWorkOn(Set<EmployeeTerm> termsEmployeesWorkOn) {
        this.termsEmployeesWorkOn = termsEmployeesWorkOn;
    }

    @XmlElement(name = "providedServices", nillable = true)
    public Set<ProviderService> getProvidedServices() {
        return providedServices;
    }

    public void setProvidedServices(Set<ProviderService> providedServices) {
        this.providedServices = providedServices;
    }
}