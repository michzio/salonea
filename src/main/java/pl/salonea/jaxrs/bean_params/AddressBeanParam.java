package pl.salonea.jaxrs.bean_params;

import javax.ws.rs.QueryParam;

/**
 * Created by michzio on 08/09/2015.
 */
public class AddressBeanParam extends PaginationBeanParam {

    private @QueryParam("city") String city;
    private @QueryParam("state") String state;
    private @QueryParam("country") String country;
    private @QueryParam("street") String street;
    private @QueryParam("zipCode") String zipCode;

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
}
