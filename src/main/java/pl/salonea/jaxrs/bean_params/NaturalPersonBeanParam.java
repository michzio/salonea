package pl.salonea.jaxrs.bean_params;

import pl.salonea.enums.Gender;
import pl.salonea.jaxrs.utils.RESTDateTime;

import javax.ws.rs.QueryParam;

/**
 * Created by michzio on 07/09/2015.
 */
public class NaturalPersonBeanParam extends PaginationBeanParam {

    private @QueryParam("firstName") String firstName;
    private @QueryParam("lastName") String lastName;
    private @QueryParam("gender") Gender gender;
    private @QueryParam("oldestBirthDate") RESTDateTime oldestBirthDate;
    private @QueryParam("youngestBirthDate") RESTDateTime youngestBirthDate;
    private @QueryParam("oldestAge") Integer oldestAge;
    private @QueryParam("youngestAge") Integer youngestAge;
    private @QueryParam("homeCity") String homeCity;
    private @QueryParam("homeState") String homeState;
    private @QueryParam("homeCountry") String homeCountry;
    private @QueryParam("homeStreet") String homeStreet;
    private @QueryParam("homeZipCode") String homeZipCode;
    private @QueryParam("homeFlatNumber") String homeFlatNumber;
    private @QueryParam("homeHouseNumber") String homeHouseNumber;
    private @QueryParam("deliveryCity") String deliveryCity;
    private @QueryParam("deliveryState") String deliveryState;
    private @QueryParam("deliveryCountry") String deliveryCountry;
    private @QueryParam("deliveryStreet") String deliveryStreet;
    private @QueryParam("deliveryZipCode") String deliveryZipCode;
    private @QueryParam("deliveryFlatNumber") String deliveryFlatNumber;
    private @QueryParam("deliveryHouseNumber") String deliveryHouseNumber;

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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
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

    public String getHomeCity() {
        return homeCity;
    }

    public void setHomeCity(String homeCity) {
        this.homeCity = homeCity;
    }

    public String getHomeState() {
        return homeState;
    }

    public void setHomeState(String homeState) {
        this.homeState = homeState;
    }

    public String getHomeCountry() {
        return homeCountry;
    }

    public void setHomeCountry(String homeCountry) {
        this.homeCountry = homeCountry;
    }

    public String getHomeStreet() {
        return homeStreet;
    }

    public void setHomeStreet(String homeStreet) {
        this.homeStreet = homeStreet;
    }

    public String getHomeZipCode() {
        return homeZipCode;
    }

    public void setHomeZipCode(String homeZipCode) {
        this.homeZipCode = homeZipCode;
    }

    public String getHomeFlatNumber() {
        return homeFlatNumber;
    }

    public void setHomeFlatNumber(String homeFlatNumber) {
        this.homeFlatNumber = homeFlatNumber;
    }

    public String getHomeHouseNumber() {
        return homeHouseNumber;
    }

    public void setHomeHouseNumber(String homeHouseNumber) {
        this.homeHouseNumber = homeHouseNumber;
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
}
