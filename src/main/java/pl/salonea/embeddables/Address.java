package pl.salonea.embeddables;

import pl.salonea.constraints.CountryZipCode;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

@Embeddable
@Access(AccessType.PROPERTY)
// TO DO: @AddressValid - verifies street, zip code, city, country combination
@CountryZipCode(message = "Zip code doesn't match country specific zip code format.")
public class Address {

    private String street;
    private String houseNumber;
    private String flatNumber;
    private String zipCode;
    private String city;
    private String state; // federated state: state, province, land, canton, etc.
    private String country;

    /* Constructors */

    public Address() { }

    public Address(String street, String zipCode, String city, String country) {
        this.street = street;
        this.zipCode = zipCode;
        this.city = city;
        this.country = country;
    }

    public Address(String street, String houseNumber, String zipCode, String city, String state, String country) {
        this.street = street;
        this.zipCode = zipCode;
        this.city = city;
        this.country = country;
        this.state = state;
        this.houseNumber = houseNumber;
    }

    public Address(String street, String houseNumber, String flatNumber, String zipCode, String city, String state, String country) {
        this.street = street;
        this.houseNumber = houseNumber;
        this.flatNumber = flatNumber;
        this.zipCode = zipCode;
        this.city = city;
        this.state = state;
        this.country = country;
    }

    /* Getters and setters */

    @Column(name = "street", columnDefinition = "VARCHAR(255) DEFAULT NULL")
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Size(max=6)
    @Column(name = "house_no", columnDefinition = "VARCHAR(6) DEFAULT NULL")
    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    @Size(max=6)
    @Column(name = "flat_no", columnDefinition = "VARCHAR(6) DEFAULT NULL")
    public String getFlatNumber() {
        return flatNumber;
    }

    public void setFlatNumber(String flatNumber) {
        this.flatNumber = flatNumber;
    }

    @Size(max=10)
    @Column(name = "zip_code", columnDefinition = "VARCHAR(10) DEFAULT NULL")
    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Column(name="city", columnDefinition = "VARCHAR(255) DEFAULT NULL")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column(name="state", columnDefinition = "VARCHAR(255) DEFAULT NULL")
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Size(max=45)
    @Column(name="country", columnDefinition = "VARCHAR(45) DEFAULT NULL")
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
