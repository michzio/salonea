package pl.salonea.jaxrs.bean_params;

import pl.salonea.ejb.stateless.*;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.*;
import pl.salonea.entities.idclass.ProviderServiceId;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.utils.CoordinatesCircle;
import pl.salonea.utils.CoordinatesSquare;

import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michzio on 18/09/2015.
 */
public class ServicePointBeanParam extends PaginationBeanParam {

    private @QueryParam("providerId") List<Long> providerIds;
    private @QueryParam("serviceId") List<Integer> serviceIds;
    private @QueryParam("providerServiceId") List<ProviderServiceId> providerServiceIds; // {providerId}+{serviceId} composite PK
    private @QueryParam("employeeId") List<Long> employeeIds;
    private @QueryParam("corporationId") List<Long> corporationIds;
    private @QueryParam("industryId") List<Long> industryIds;
    private @QueryParam("serviceCategoryId") List<Integer> serviceCategoryIds;
    private @BeanParam AddressBeanParam addressParams;
    private @BeanParam CoordinatesSquareBeanParam coordinatesSquareParams;
    private @BeanParam CoordinatesCircleBeanParam coordinatesCircleParams;

    @Inject
    private ProviderFacade providerFacade;
    @Inject
    private ServiceFacade serviceFacade;
    @Inject
    private EmployeeFacade employeeFacade;
    @Inject
    private CorporationFacade corporationFacade;
    @Inject
    private IndustryFacade industryFacade;
    @Inject
    private ServiceCategoryFacade serviceCategoryFacade;
    @Inject
    private ProviderServiceFacade providerServiceFacade;

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

    public List<Long> getCorporationIds() {
        return corporationIds;
    }

    public void setCorporationIds(List<Long> corporationIds) {
        this.corporationIds = corporationIds;
    }

    public List<Corporation> getCorporations() throws NotFoundException {
        if(getCorporationIds() != null && getCorporationIds().size() > 0) {
            final List<Corporation> corporations = corporationFacade.find( new ArrayList<>(getCorporationIds()) );
            if(corporations.size() != getCorporationIds().size()) throw new NotFoundException("Could not find corporations for all provided ids.");
            return corporations;
        }
        return null;
    }

    public List<Long> getIndustryIds() {
        return industryIds;
    }

    public void setIndustryIds(List<Long> industryIds) {
        this.industryIds = industryIds;
    }

    public List<Industry> getIndustries() throws NotFoundException {
        if(getIndustryIds() != null && getIndustryIds().size() > 0) {
            final List<Industry> industries = industryFacade.find( new ArrayList<>(getIndustryIds()) );
            if(industries.size() != getIndustryIds().size()) throw new NotFoundException("Could not find industries for all provided ids.");
            return industries;
        }
        return null;
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

    public AddressBeanParam getAddressParams() {
        return addressParams;
    }

    public void setAddressParams(AddressBeanParam addressParams) {
        this.addressParams = addressParams;
    }

    public Address getAddress() {

        if(isSetAnyAddressParam()) {
            return new Address(getAddressParams().getStreet(), getAddressParams().getHouseNumber(), getAddressParams().getFlatNumber(),
                    getAddressParams().getZipCode(), getAddressParams().getCity(), getAddressParams().getState(), getAddressParams().getCountry());
        }
        return null;
    }

    private Boolean isSetAnyAddressParam() {

        if(getAddressParams().getStreet() != null) return true;
        if(getAddressParams().getHouseNumber() != null) return true;
        if(getAddressParams().getFlatNumber() != null) return true;
        if(getAddressParams().getCity() != null) return true;
        if(getAddressParams().getZipCode() != null) return true;
        if(getAddressParams().getState() != null) return true;
        if(getAddressParams().getCountry() != null) return true;

        return false;
    }

    public CoordinatesSquareBeanParam getCoordinatesSquareParams() {
        return coordinatesSquareParams;
    }

    public void setCoordinatesSquareParams(CoordinatesSquareBeanParam coordinatesSquareParams) {
        this.coordinatesSquareParams = coordinatesSquareParams;
    }

    public CoordinatesSquare getCoordinatesSquare() {

        if(areSetAllCoordinatesSquareParams()) {
            return new CoordinatesSquare(getCoordinatesSquareParams().getMinLongitudeWGS84(), getCoordinatesSquareParams().getMinLatitudeWGS84(),
                    getCoordinatesSquareParams().getMaxLongitudeWGS84(), getCoordinatesSquareParams().getMaxLatitudeWGS84());
        }

        return null;
    }

    private Boolean areSetAllCoordinatesSquareParams() {

        if(getCoordinatesSquareParams().getMinLongitudeWGS84() == null) return false;
        if(getCoordinatesSquareParams().getMinLatitudeWGS84() == null) return false;
        if(getCoordinatesSquareParams().getMaxLongitudeWGS84() == null) return false;
        if(getCoordinatesSquareParams().getMaxLatitudeWGS84() == null) return false;

        return true;
    }

    public CoordinatesCircleBeanParam getCoordinatesCircleParams() {
        return coordinatesCircleParams;
    }

    public void setCoordinatesCircleParams(CoordinatesCircleBeanParam coordinatesCircleParams) {
        this.coordinatesCircleParams = coordinatesCircleParams;
    }

    public CoordinatesCircle getCoordinatesCircle() {

        if(areSetAllCoordinatesCircleParams()) {
            return new CoordinatesCircle(getCoordinatesCircleParams().getLongitudeWGS84(), getCoordinatesCircleParams().getLatitudeWGS84(),
                                         getCoordinatesCircleParams().getRadius());
        }

        return null;
    }

    private Boolean areSetAllCoordinatesCircleParams() {

        if(getCoordinatesCircleParams().getLongitudeWGS84() == null) return false;
        if(getCoordinatesCircleParams().getLatitudeWGS84() == null) return false;
        if(getCoordinatesCircleParams().getRadius() == null) return false;

        return true;
    }
}
