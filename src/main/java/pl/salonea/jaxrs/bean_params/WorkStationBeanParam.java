package pl.salonea.jaxrs.bean_params;

import pl.salonea.ejb.stateless.EmployeeFacade;
import pl.salonea.ejb.stateless.ProviderServiceFacade;
import pl.salonea.ejb.stateless.ServiceFacade;
import pl.salonea.ejb.stateless.ServicePointFacade;
import pl.salonea.entities.Employee;
import pl.salonea.entities.ProviderService;
import pl.salonea.entities.Service;
import pl.salonea.entities.ServicePoint;
import pl.salonea.entities.idclass.ProviderServiceId;
import pl.salonea.entities.idclass.ServicePointId;
import pl.salonea.enums.WorkStationType;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.utils.Period;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michzio on 24/01/2016.
 */
public class WorkStationBeanParam extends DateBetweenBeanParam { // incl. PaginationBeanParam

    private @QueryParam("servicePointId") List<ServicePointId> servicePointIds; // {providerId}+{servicePointNumber} composite PK
    private @QueryParam("serviceId") List<Integer> serviceIds;
    private @QueryParam("providerServiceId") List<ProviderServiceId> providerServiceIds; // {providerId}+{serviceId} composite PK
    private @QueryParam("employeeId") List<Long> employeeIds;
    private @QueryParam("workStationType") List<WorkStationType> workStationTypes;
    private @QueryParam("strictTerm") Boolean strictTerm;

    @Inject
    private ServicePointFacade servicePointFacade;
    @Inject
    private ServiceFacade serviceFacade;
    @Inject
    private ProviderServiceFacade providerServiceFacade;
    @Inject
    private EmployeeFacade employeeFacade;


    public List<ServicePointId> getServicePointIds() {
        return servicePointIds;
    }

    public void setServicePointIds(List<ServicePointId> servicePointIds) {
        this.servicePointIds = servicePointIds;
    }

    public List<ServicePoint> getServicePoints() throws NotFoundException {
        if(getServicePointIds() != null && getServicePointIds().size() > 0) {
            final List<ServicePoint> servicePoints = servicePointFacade.find( new ArrayList<>(getServicePointIds()) );
            if(servicePoints.size() != getServicePointIds().size()) throw new NotFoundException("Could not find service points for all provided ids.");
            return servicePoints;
        }
        return null;
    }

    public List<Integer> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<Integer> serviceIds) {
        this.serviceIds = serviceIds;
    }

    public List<Service> getServices() throws NotFoundException {
        if(getServiceIds() != null && getServiceIds().size() > 0) {
            final List<Service> services = serviceFacade.find(new ArrayList<>(getServiceIds()));
            if(services.size() != getServiceIds().size()) throw new NotFoundException("Could not find services for all provided ids.");
            return services;
        }
        return null;
    }

    public List<ProviderServiceId> getProviderServiceIds() {
        return providerServiceIds;
    }

    public void setProviderServiceIds(List<ProviderServiceId> providerServiceIds) {
        this.providerServiceIds = providerServiceIds;
    }

    public List<ProviderService> getProviderServices() throws NotFoundException {
        if(getProviderServiceIds() != null && getProviderServiceIds().size() > 0) {
            final List<ProviderService> providerServices = providerServiceFacade.find( new ArrayList<>(getProviderServiceIds()) );
            if(providerServices.size() != getProviderServiceIds().size()) throw new NotFoundException("Could not find provider services for all provided ids.");
            return providerServices;
        }
        return null;
    }

    public List<Long> getEmployeeIds() {
        return employeeIds;
    }

    public void setEmployeeIds(List<Long> employeeIds) {
        this.employeeIds = employeeIds;
    }

    public List<Employee> getEmployees() throws NotFoundException {
        if(getEmployeeIds() != null && getEmployeeIds().size() > 0) {
            final List<Employee> employees = employeeFacade.find( new ArrayList<>(getEmployeeIds()) );
            if(employees.size() != getEmployeeIds().size()) throw new NotFoundException("Could not find employees for all provided ids.");
            return employees;
        }
        return null;
    }

    public List<WorkStationType> getWorkStationTypes() {
        return workStationTypes;
    }

    public void setWorkStationTypes(List<WorkStationType> workStationTypes) {
        this.workStationTypes = workStationTypes;
    }

    public Period getPeriod() {
        if(getStartDate() == null && getEndDate() == null)
            return null;

        return new Period(getStartDate(), getEndDate());
    }

    public Boolean getStrictTerm() {
        return strictTerm;
    }

    public void setStrictTerm(Boolean strictTerm) {
        this.strictTerm = strictTerm;
    }
}
