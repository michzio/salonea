package pl.salonea.jaxrs.bean_params;

import pl.salonea.ejb.stateless.*;
import pl.salonea.entities.*;
import pl.salonea.entities.idclass.ServicePointId;
import pl.salonea.entities.idclass.WorkStationId;
import pl.salonea.jaxrs.exceptions.NotFoundException;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michzio on 22/01/2016.
 */
public class ServiceBeanParam extends PaginationBeanParam {

    private @QueryParam("name") List<String> names;
    private @QueryParam("description") List<String> descriptions;
    private @QueryParam("keyword") List<String> keywords;

    private @QueryParam("serviceCategoryId") List<Integer> serviceCategoryIds;
    private @QueryParam("providerId") List<Long> providerIds;
    private @QueryParam("employeeId") List<Long> employeeIds;
    private @QueryParam("workStationId") List<WorkStationId> workStationIds; // {providerId}+{servicePointNumber}+{workStationNumber} composite PK
    private @QueryParam("servicePointId") List<ServicePointId> servicePointIds; // {providerId}+{servicePointNumber} composite PK

    @Inject
    private ServiceCategoryFacade serviceCategoryFacade;
    @Inject
    private ProviderFacade providerFacade;
    @Inject
    private EmployeeFacade employeeFacade;
    @Inject
    private WorkStationFacade workStationFacade;
    @Inject
    private ServicePointFacade servicePointFacade;

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public List<String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<String> descriptions) {
        this.descriptions = descriptions;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<Integer> getServiceCategoryIds() {
        return serviceCategoryIds;
    }

    public void setServiceCategoryIds(List<Integer> serviceCategoryIds) {
        this.serviceCategoryIds = serviceCategoryIds;
    }

    public List<ServiceCategory> getServiceCategories() throws NotFoundException {
        if(getServiceCategoryIds() != null && getServiceCategoryIds().size() > 0) {
            final List<ServiceCategory> serviceCategories = serviceCategoryFacade.find( new ArrayList<>(getServiceCategoryIds()) );
            if(serviceCategories.size() != getServiceCategoryIds().size()) throw new NotFoundException("Could not find service categories for all provided ids.");
            return serviceCategories;
        }
        return null;
    }

    public List<Long> getProviderIds() {
        return providerIds;
    }

    public void setProviderIds(List<Long> providerIds) {
        this.providerIds = providerIds;
    }

    public List<Provider> getProviders() throws NotFoundException {
        if(getProviderIds() != null && getProviderIds().size() > 0) {
            final List<Provider> providers = providerFacade.find( new ArrayList<>(getProviderIds()) );
            if(providers.size() != getProviderIds().size()) throw new NotFoundException("Could not find providers for all provided ids.");
            return providers;
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

    public List<WorkStationId> getWorkStationIds() {
        return workStationIds;
    }

    public void setWorkStationIds(List<WorkStationId> workStationIds) {
        this.workStationIds = workStationIds;
    }

    public List<WorkStation> getWorkStations() throws NotFoundException {
        if(getWorkStationIds() != null && getWorkStationIds().size() > 0) {
            final List<WorkStation> workStations = workStationFacade.find( new ArrayList<>(getWorkStationIds()) );
            if(workStations.size() != getWorkStationIds().size()) throw new NotFoundException("Could not find work stations for all provided ids.");
            return workStations;
        }
        return null;
    }

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
}
