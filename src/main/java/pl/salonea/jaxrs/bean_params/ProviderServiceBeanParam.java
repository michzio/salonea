package pl.salonea.jaxrs.bean_params;

import pl.salonea.ejb.stateless.*;
import pl.salonea.entities.*;
import pl.salonea.entities.idclass.EmployeeTermId;
import pl.salonea.entities.idclass.ServicePointId;
import pl.salonea.entities.idclass.WorkStationId;
import pl.salonea.jaxrs.exceptions.NotFoundException;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michzio on 28/09/2015.
 */
public class ProviderServiceBeanParam extends PaginationBeanParam {

    private @QueryParam("providerId") List<Long> providerIds;
    private @QueryParam("serviceId") List<Integer> serviceIds;
    private @QueryParam("serviceCategoryId") List<Integer> serviceCategoryIds;
    private @QueryParam("description") List<String> descriptions;
    private @QueryParam("minPrice") Double minPrice;
    private @QueryParam("maxPrice") Double maxPrice;
    private @QueryParam("includeDiscounts") Boolean includeDiscounts;
    private @QueryParam("minDiscount") Short minDiscount;
    private @QueryParam("maxDiscount") Short maxDiscount;
    private @QueryParam("minDuration") Long minDuration;
    private @QueryParam("maxDuration") Long maxDuration;
    private @QueryParam("workStationId") List<WorkStationId> workStationIds; // {providerId}+{servicePointNumber}+{workStationNumber} composite PK
    private @QueryParam("servicePointId") List<ServicePointId> servicePointIds; // {providerId}+{servicePointNumber} composite PK
    private @QueryParam("employeeId") List<Long> employeeIds;
    private @QueryParam("employeeTermId") List<EmployeeTermId> employeeTermIds; // {employeeId}+{termId} composite PK
    private @QueryParam("termId") List<Long> termIds;

    @Inject
    private ProviderFacade providerFacade;
    @Inject
    private ServiceFacade serviceFacade;
    @Inject
    private ServiceCategoryFacade serviceCategoryFacade;
    @Inject
    private WorkStationFacade workStationFacade;
    @Inject
    private ServicePointFacade servicePointFacade;
    @Inject
    private EmployeeFacade employeeFacade;
    @Inject
    private EmployeeTermFacade employeeTermFacade;
    @Inject
    private TermFacade termFacade;

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

    public List<Integer> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<Integer> serviceIds) {
        this.serviceIds = serviceIds;
    }

    public List<Service> getServices() throws NotFoundException {
        if(getServiceIds() != null && getServiceIds().size() > 0) {
            final List<Service> services = serviceFacade.find( new ArrayList<>(getServiceIds()) );
            if(services.size() != getServiceIds().size()) throw new NotFoundException("Could not find services for all provided ids.");
            return services;
        }
        return null;
    }

    public List<Integer> getServiceCategoryIds() {
        return serviceCategoryIds;
    }

    public void setServiceCategoryIds(List<Integer> serviceCategoryIds) {
        this.serviceCategoryIds = serviceCategoryIds;
    }

    public List<ServiceCategory> getServiceCategories() {
        if(getServiceCategoryIds() != null && getServiceCategoryIds().size() > 0) {
            final List<ServiceCategory> serviceCategories = serviceCategoryFacade.find( new ArrayList<>(getServiceCategoryIds()) );
            if(serviceCategories.size() != getServiceCategoryIds().size()) throw new NotFoundException("Could not find service categories for all provided ids.");
            return serviceCategories;
        }
        return null;
    }

    public List<String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<String> descriptions) {
        this.descriptions = descriptions;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Boolean getIncludeDiscounts() {
        return includeDiscounts;
    }

    public void setIncludeDiscounts(Boolean includeDiscounts) {
        this.includeDiscounts = includeDiscounts;
    }

    public Short getMinDiscount() {
        return minDiscount;
    }

    public void setMinDiscount(Short minDiscount) {
        this.minDiscount = minDiscount;
    }

    public Short getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(Short maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public Long getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(Long minDuration) {
        this.minDuration = minDuration;
    }

    public Long getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(Long maxDuration) {
        this.maxDuration = maxDuration;
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

    public List<EmployeeTermId> getEmployeeTermIds() {
        return employeeTermIds;
    }

    public void setEmployeeTermIds(List<EmployeeTermId> employeeTermIds) {
        this.employeeTermIds = employeeTermIds;
    }

    public List<EmployeeTerm> getEmployeeTerms() throws NotFoundException {
        if(getEmployeeTermIds() != null && getEmployeeTermIds().size() > 0) {
            final List<EmployeeTerm> employeeTerms = employeeTermFacade.find( new ArrayList<>(getEmployeeTermIds()) );
            if(employeeTerms.size() != getEmployeeTermIds().size()) throw new NotFoundException("Could not find employee terms for all provided ids.");
            return employeeTerms;
        }
        return null;
    }

    public List<Long> getTermIds() {
        return termIds;
    }

    public void setTermIds(List<Long> termIds) {
        this.termIds = termIds;
    }

    public List<Term> getTerms() throws NotFoundException {
        if(getTermIds() != null && getTermIds().size() > 0) {
            final List<Term> terms = termFacade.find( new ArrayList<>(getTermIds()) );
            if(terms.size() != getTermIds().size()) throw new NotFoundException("Could not find terms for all provided ids.");
            return terms;
        }
        return null;
    }
}
