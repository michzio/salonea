package pl.salonea.entities;


import pl.salonea.constraints.*;
import pl.salonea.embeddables.Address;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
@Table(name="firm")
@DiscriminatorValue("firm")
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = Firm.FIND_BY_ADDRESS, query = "SELECT f FROM Firm f WHERE f.address.city LIKE :city AND f.address.state LIKE :state " +
             "AND f.address.country LIKE :country AND f.address.street LIKE :street AND f.address.zipCode LIKE :zip_code"),
        @NamedQuery(name = Firm.FIND_BY_NAME, query = "SELECT f FROM Firm f WHERE f.name LIKE :firm_name"),
        @NamedQuery(name = Firm.FIND_BY_VATIN, query = "SELECT f FROM Firm f WHERE f.vatin = :vatin"),
        @NamedQuery(name = Firm.FIND_BY_COMPANY_NUMBER, query = "SELECT f FROM Firm f WHERE f.companyNumber = :company_number"),
        @NamedQuery(name = Firm.DELETE_WITH_VATIN, query = "DELETE FROM Firm f WHERE f.vatin = :vatin"),
        @NamedQuery(name = Firm.DELETE_WITH_COMPANY_NUMBER, query = "DELETE FROM Firm f WHERE f.companyNumber = :company_number")
})
@VATIN // check country specific VAT identification number (e.g. poland NIP)
@CompanyNumber // check country specific comany number (e.g. poland KRS)
@StatisticNumber // check country specific statistic number (e.g. poland REGON)
public class Firm extends UserAccount {

    public static final String FIND_BY_ADDRESS = "Firm.findByAddress";
    public static final String FIND_BY_NAME = "Firm.findByName";
    public static final String FIND_BY_VATIN = "Firm.findByVATIN";
    public static final String FIND_BY_COMPANY_NUMBER = "Firm.findByCompanyNumber";
    public static final String DELETE_WITH_VATIN = "Firm.deleteWithVATIN";
    public static final String DELETE_WITH_COMPANY_NUMBER = "Firm.deleteWithCompanyNumber";

    private String vatin; // in Poland -> NIP, VAT-EU number
    private String name;
    private String companyNumber; //in Poland -> KRS
    private String statisticNumber; // probably only in Poland -> REGON

    private Address address;

    private String phoneNumber;
    private String skypeName;

    private Client client;

    public Firm() {}

    public Firm(String email, String login, String password, String name) {
        super(email, login, password);
        this.name = name;
    }

    public Firm(String email, String login, String password, String vatin, String name, String companyNumber) {
        super(email, login, password);
        this.vatin = vatin;
        this.name = name;
        this.companyNumber = companyNumber;
    }

    public Firm(String email, String login, String password, String vatin, String name, String companyNumber, Address address) {
        super(email, login, password);
        this.vatin = vatin;
        this.name = name;
        this.companyNumber = companyNumber;
        this.address = address;
    }

    /* Getter and setter */

    @NotNull // additional check on class level for country specific format
    @Column(name="vatin", unique = true, nullable = false, columnDefinition = "CHAR(10)")
    public String getVatin() {
        return vatin;
    }

    /* TODO: internationalize VAT Identification column */

    public void setVatin(String vatin) {
        this.vatin = vatin;
    }

    @NotNull(message = "Firm name can not be null.")
    @Size(min = 2, max = 45)
    @Column(name ="name", nullable = false, length = 45)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @NotNull // additional check on class level for country specific format
    @Column(name="company_number", nullable = false, unique = true, columnDefinition = "CHAR(10)")
    public String getCompanyNumber() {
        return companyNumber;
    }

    /* TODO: internationalize Company Number (polish KRS) */

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    // additional check on class level for country specific format
    @Column(name = "statistic_number", columnDefinition = "CHAR(9)")
    public String getStatisticNumber() {
        return statisticNumber;
    }

    /* TODO: internationalize Statistic Number(poland REGON) */

    public void setStatisticNumber(String statisticNumber) {
        this.statisticNumber = statisticNumber;
    }

    @Valid
    @NotNull
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street",
                    column = @Column(name = "address_street", nullable = false, columnDefinition = "VARCHAR(255)")),
            @AttributeOverride(name = "houseNumber",
                    column = @Column(name = "address_building_no", nullable = false, columnDefinition = "VARCHAR(6)")),
            @AttributeOverride(name = "flatNumber",
                    column = @Column(name = "address_office_no")),
            @AttributeOverride(name = "zipCode",
                    column = @Column(name = "address_zip_code", nullable = false, columnDefinition = "VARCHAR(10)")),
            @AttributeOverride(name = "city",
                    column = @Column(name = "address_city", nullable = false, columnDefinition = "VARCHAR(255)")),
            @AttributeOverride(name = "state",
                    column = @Column(name = "address_state", nullable = false, columnDefinition = "VARCHAR(255)")),
            @AttributeOverride(name = "country",
                    column = @Column(name = "address_country", nullable = false, columnDefinition = "VARCHAR(45)"))
    })
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @PhoneNumber
    @Column(name = "phone_number", length = 20, columnDefinition = "VARCHAR(20) DEFAULT NULL")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @SkypeName
    @Column(name = "skype_name", length = 32, columnDefinition = "VARCHAR(32) DEFAULT NULL")
    public String getSkypeName() {
        return skypeName;
    }

    public void setSkypeName(String skypeName) {
        this.skypeName = skypeName;
    }

    /* relationship one-to-one with Client */
    // TODO @NotNull - shouldn't be possible to create firm that isnt Client
    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    @JoinColumn(name = "client_id", unique = true, columnDefinition = "BIGINT UNSIGNED default NULL",
            foreignKey = @ForeignKey(name="fk_firm_client"))
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
