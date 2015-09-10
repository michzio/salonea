package pl.salonea.jaxrs.bean_params;

import javax.ws.rs.QueryParam;

/**
 * Created by michzio on 09/09/2015.
 */
public class FirmBeanParam extends PaginationBeanParam {

    private @QueryParam("name") String name;
    private @QueryParam("vatin") String vatin;
    private @QueryParam("companyNumber") String companyNumber;
    private @QueryParam("statisticNumber") String statisticNumber;
    private @QueryParam("phoneNumber") String phoneNumber;
    private @QueryParam("skypeName") String skypeName;
    private @QueryParam("city") String city;
    private @QueryParam("state") String state;
    private @QueryParam("country") String country;
    private @QueryParam("street") String street;
    private @QueryParam("zipCode") String zipCode;
    private @QueryParam("flatNumber") String flatNumber;
    private @QueryParam("houseNumber") String houseNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVatin() {
        return vatin;
    }

    public void setVatin(String vatin) {
        this.vatin = vatin;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getStatisticNumber() {
        return statisticNumber;
    }

    public void setStatisticNumber(String statisticNumber) {
        this.statisticNumber = statisticNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSkypeName() {
        return skypeName;
    }

    public void setSkypeName(String skypeName) {
        this.skypeName = skypeName;
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
}
