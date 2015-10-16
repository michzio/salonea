package pl.salonea.jaxrs.bean_params;

import pl.salonea.ejb.stateless.EmployeeFacade;
import pl.salonea.ejb.stateless.ProviderFacade;
import pl.salonea.entities.Employee;
import pl.salonea.entities.Provider;
import pl.salonea.enums.ClientType;
import pl.salonea.enums.Gender;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.RESTDateTime;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michzio on 12/10/2015.
 */
public class ClientBeanParam extends PaginationBeanParam {

    private @QueryParam("firstName") String firstName;
    private @QueryParam("lastName") String lastName;
    private @QueryParam("firmName") String firmName;
    private @QueryParam("name") String name;
    private @QueryParam("description") String description;
    private @QueryParam("clientType") List<ClientType> clientTypes;
    private @QueryParam("oldestBirthDate") RESTDateTime oldestBirthDate;
    private @QueryParam("youngestBirthDate") RESTDateTime youngestBirthDate;
    private @QueryParam("oldestAge") Integer oldestAge;
    private @QueryParam("youngestAge") Integer youngestAge;
    // location address
    private @QueryParam("city") String city;
    private @QueryParam("state") String state;
    private @QueryParam("country") String country;
    private @QueryParam("street") String street;
    private @QueryParam("zipCode") String zipCode;
    private @QueryParam("flatNumber") String flatNumber;
    private @QueryParam("houseNumber") String houseNumber;
    // delivery address
    private @QueryParam("deliveryCity") String deliveryCity;
    private @QueryParam("deliveryState") String deliveryState;
    private @QueryParam("deliveryCountry") String deliveryCountry;
    private @QueryParam("deliveryStreet") String deliveryStreet;
    private @QueryParam("deliveryZipCode") String deliveryZipCode;
    private @QueryParam("deliveryFlatNumber") String deliveryFlatNumber;
    private @QueryParam("deliveryHouseNumber") String deliveryHouseNumber;

    private @QueryParam("gender") Gender gender;
    private @QueryParam("providerId") List<Long> providerIds;
    private @QueryParam("employeeId") List<Long> employeeIds;

    @Inject
    private ProviderFacade providerFacade;

    @Inject
    private EmployeeFacade employeeFacade;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ClientType> getClientTypes() {
        return clientTypes;
    }

    public void setClientTypes(List<ClientType> clientTypes) {
        this.clientTypes = clientTypes;
    }

    public RESTDateTime getOldestBirthDate() {
        return oldestBirthDate;
    }

    public void setOldestBirthDate(RESTDateTime oldestBirthDate) {
        this.oldestBirthDate = oldestBirthDate;
    }

    public RESTDateTime getYoungestBirthDate() {
        return youngestBirthDate;
    }

    public void setYoungestBirthDate(RESTDateTime youngestBirthDate) {
        this.youngestBirthDate = youngestBirthDate;
    }

    public Integer getOldestAge() {
        return oldestAge;
    }

    public void setOldestAge(Integer oldestAge) {
        this.oldestAge = oldestAge;
    }

    public Integer getYoungestAge() {
        return youngestAge;
    }

    public void setYoungestAge(Integer youngestAge) {
        this.youngestAge = youngestAge;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public void setFlatNumber(String flatNumber) {
        this.flatNumber = flatNumber;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getDeliveryCity() {
        return deliveryCity;
    }

    public void setDeliveryCity(String deliveryCity) {
        this.deliveryCity = deliveryCity;
    }

    public String getDeliveryState() {
        return deliveryState;
    }

    public void setDeliveryState(String deliveryState) {
        this.deliveryState = deliveryState;
    }

    public String getDeliveryCountry() {
        return deliveryCountry;
    }

    public void setDeliveryCountry(String deliveryCountry) {
        this.deliveryCountry = deliveryCountry;
    }

    public String getDeliveryStreet() {
        return deliveryStreet;
    }

    public void setDeliveryStreet(String deliveryStreet) {
        this.deliveryStreet = deliveryStreet;
    }

    public String getDeliveryZipCode() {
        return deliveryZipCode;
    }

    public void setDeliveryZipCode(String deliveryZipCode) {
        this.deliveryZipCode = deliveryZipCode;
    }

    public String getDeliveryFlatNumber() {
        return deliveryFlatNumber;
    }

    public void setDeliveryFlatNumber(String deliveryFlatNumber) {
        this.deliveryFlatNumber = deliveryFlatNumber;
    }

    public String getDeliveryHouseNumber() {
        return deliveryHouseNumber;
    }

    public void setDeliveryHouseNumber(String deliveryHouseNumber) {
        this.deliveryHouseNumber = deliveryHouseNumber;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public List<Long> getProviderIds() {
        return providerIds;
    }

    public void setProviderIds(List<Long> providerIds) {
        this.providerIds = providerIds;
    }

    public List<Provider> getRatedProviders() throws NotFoundException {
        if(getProviderIds() != null && getProviderIds().size() > 0) {
            final List<Provider> ratedProviders = providerFacade.find( new ArrayList<>(getProviderIds()) );
            if(ratedProviders.size() != getProviderIds().size()) throw new NotFoundException("Could not find rated providers for all provided ids.");
            return ratedProviders;
        }
        return null;
    }

    public List<Long> getEmployeeIds() {
        return employeeIds;
    }

    public void setEmployeeIds(List<Long> employeeIds) {
        this.employeeIds = employeeIds;
    }

    public List<Employee> getRatedEmployees() throws NotFoundException {
        if(getEmployeeIds() != null && getEmployeeIds().size() > 0) {
            final List<Employee> ratedEmployees = employeeFacade.find( new ArrayList<>(getEmployeeIds()) );
            if(ratedEmployees.size() != getEmployeeIds().size()) throw new NotFoundException("Could not find rated employees for all provided ids.");
            return ratedEmployees;
        }
        return null;
    }
}
