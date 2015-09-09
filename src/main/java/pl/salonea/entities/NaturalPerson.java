package pl.salonea.entities;

import pl.salonea.constraints.BirthDate;
import pl.salonea.constraints.DeliveryAddressFlagMatch;
import pl.salonea.constraints.PhoneNumber;
import pl.salonea.constraints.SkypeName;
import pl.salonea.embeddables.Address;
import pl.salonea.enums.Gender;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.*;
import java.util.Calendar;
import java.util.Date;;

@XmlRootElement(name = "natural-person")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"firstName", "lastName", "birthDate", "gender", "homePhoneNumber", "workPhoneNumber",
                        "skypeName", "homeAddress", "deliveryAsHome", "deliveryAddress", "age", "client"})

@Entity
@DiscriminatorValue("natural_person")
@Table(name="natural_person")
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = NaturalPerson.FIND_BY_FIRST_NAME, query = "SELECT np FROM NaturalPerson np WHERE np.firstName LIKE :fname"),
        @NamedQuery(name = NaturalPerson.FIND_BY_LAST_NAME, query = "SELECT np FROM NaturalPerson np WHERE np.lastName LIKE :lname"),
        @NamedQuery(name = NaturalPerson.FIND_BY_NAMES, query = "SELECT np FROM NaturalPerson np WHERE np.firstName LIKE :fname AND np.lastName LIKE :lname"),
        @NamedQuery(name = NaturalPerson.FIND_BORN_AFTER, query = "SELECT np FROM NaturalPerson np WHERE np.birthDate >= :date"),
        @NamedQuery(name = NaturalPerson.FIND_BORN_BEFORE, query = "SELECT np FROM NaturalPerson np WHERE np.birthDate <= :date"),
        @NamedQuery(name = NaturalPerson.FIND_BORN_BETWEEN, query = "SELECT np FROM NaturalPerson np WHERE np.birthDate >= :start_date AND np.birthDate <= :end_date"),
        @NamedQuery(name = NaturalPerson.FIND_BY_LOCATION, query = "SELECT np FROM NaturalPerson np WHERE np.homeAddress.city LIKE :city AND np.homeAddress.state LIKE :state " +
                "AND np.homeAddress.country LIKE :country AND np.homeAddress.street LIKE :street AND np.homeAddress.zipCode LIKE :zip_code"),
        @NamedQuery(name = NaturalPerson.FIND_BY_DELIVERY, query = "SELECT np FROM NaturalPerson np WHERE np.deliveryAddress.city LIKE :city AND np.deliveryAddress.state LIKE :state " +
                "AND np.deliveryAddress.country LIKE :country AND np.deliveryAddress.street LIKE :street AND np.deliveryAddress.zipCode LIKE :zip_code"),
        @NamedQuery(name = NaturalPerson.FIND_BY_GENDER, query = "SELECT np FROM NaturalPerson np WHERE np.gender = :gender")
})
@DeliveryAddressFlagMatch
public class NaturalPerson extends UserAccount {

    public static final String FIND_BY_FIRST_NAME = "NaturalPerson.findByFirstName";
    public static final String FIND_BY_LAST_NAME = "NaturalPerson.findByLastName";
    public static final String FIND_BY_NAMES = "NaturalPerson.findByNames";
    public static final String FIND_BORN_AFTER = "NaturalPerson.findBornAfter";
    public final static String FIND_BORN_BEFORE = "NaturalPerson.findBornBefore";
    public final static String FIND_BORN_BETWEEN = "NaturalPerson.findBornBetween";
    public final static String FIND_BY_LOCATION = "NaturalPerson.findByLocation";
    public final static String FIND_BY_DELIVERY = "NaturalPerson.findByDelivery";
    public final static String FIND_BY_GENDER = "NaturalPerson.findByGender";

    private String firstName;
    private String lastName;
    private Date birthDate;
    private Gender gender;

    private String homePhoneNumber;
    private String workPhoneNumber;
    private String skypeName;

    private Address homeAddress;
    private boolean deliveryAsHome;
    private Address deliveryAddress;

    private Short age;

    private Client client;

    /* constructors */

    public NaturalPerson() {}

    public NaturalPerson(String email, String login, String password, String firstName, String lastName) {
        super(email, login, password);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public NaturalPerson(String email, String login, String password, String firstName, String lastName, Date birthDate, Gender gender) {
        super(email, login, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
    }


    /* Getters and setters */

    @NotNull(message = "First Name can not be null.")
    @Size(min=2, max=45)
    @Column(name="first_name", nullable = false, length = 45)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @NotNull(message = "Last Name can not be null.")
    @Size(min=2, max=45)
    @Column(name="last_name", nullable = false, length = 45)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @NotNull(message = "The birth date can not be null.")
    @Past
    @BirthDate(minAge = 13, maxAge = 150)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "birth_date", nullable = false, columnDefinition = "DATETIME")
    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    @Transient
    @NotNull(message = "The age can not be null.", groups = { NaturalPerson.class })
    @Digits(integer = 3, fraction = 0, message = "The value of age can not be more than 3 digits", groups = { NaturalPerson.class })
    @Min(value = 13, message = "The minimum age should be 13", groups = { NaturalPerson.class})
    @Max(value = 150, message = "The maximum age can not more than be 150", groups = { NaturalPerson.class })
    public Short getAge() {
        return age;
    }

    public void setAge(Short age) {
        this.age = age;
    }

    @NotNull(message = "The gender can not be null.")
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, columnDefinition = "ENUM('male','female') DEFAULT 'male'")
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @PhoneNumber
    @Column(name = "home_phone_no", length = 20, columnDefinition = "VARCHAR(20) DEFAULT NULL")
    public String getHomePhoneNumber() {
        return homePhoneNumber;
    }

    public void setHomePhoneNumber(String homePhoneNumber) {
        this.homePhoneNumber = homePhoneNumber;
    }

    @PhoneNumber
    @Column(name = "work_phone_no", length = 20, columnDefinition = "VARCHAR(20) DEFAULT NULL")
    public String getWorkPhoneNumber() {
        return workPhoneNumber;
    }

    public void setWorkPhoneNumber(String workPhoneNumber) {
        this.workPhoneNumber = workPhoneNumber;
    }

    @SkypeName
    @Column(name="skype", length = 32, columnDefinition = "VARCHAR(32) DEFAULT NULL")
    public String getSkypeName() {
        return skypeName;
    }

    public void setSkypeName(String skypeName) {
        this.skypeName = skypeName;
    }

    @Valid
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street",
                             column = @Column(name="home_address_street")),
            @AttributeOverride(name = "houseNumber",
                             column = @Column(name="home_address_house_no")),
            @AttributeOverride(name = "flatNumber",
                             column = @Column(name="home_address_flat_no")),
            @AttributeOverride(name = "zipCode",
                             column = @Column(name="home_address_zip_code")),
            @AttributeOverride(name = "city",
                             column = @Column(name="home_address_city")),
            @AttributeOverride(name = "state",
                             column = @Column(name="home_address_state")),
            @AttributeOverride(name = "country",
                             column = @Column(name="home_address_country"))
    })
    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    @Column(name="delivery_as_home", nullable = false, columnDefinition = "BOOL DEFAULT 0")
    public boolean isDeliveryAsHome() {
        return deliveryAsHome;
    }

    public void setDeliveryAsHome(boolean deliveryAsHome) {
        this.deliveryAsHome = deliveryAsHome;
    }

    @Valid
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street",
                    column = @Column(name="delivery_address_street")),
            @AttributeOverride(name = "houseNumber",
                    column = @Column(name="delivery_address_house_no")),
            @AttributeOverride(name = "flatNumber",
                    column = @Column(name="delivery_address_flat_no")),
            @AttributeOverride(name = "zipCode",
                    column = @Column(name="delivery_address_zip_code")),
            @AttributeOverride(name = "city",
                    column = @Column(name="delivery_address_city")),
            @AttributeOverride(name = "state",
                    column = @Column(name="delivery_address_state")),
            @AttributeOverride(name = "country",
                    column = @Column(name="delivery_address_country"))
    })
    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
        // setting flag
        if(this.deliveryAddress != null)
            setDeliveryAsHome(false);
        else
            setDeliveryAsHome(true);
    }

    /* relationship one-to-one with Client */
    /* TODO @NotNull shouldn't be possible to create NaturalPerson that isn't Client */
    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    @JoinColumn(name="client_id", unique = true, columnDefinition = "BIGINT UNSIGNED default NULL",
                foreignKey = @ForeignKey(name="fk_natural_person_client"))
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    /* entity life cycle callback methods */
    @PostPersist
    @PostLoad
    @PostUpdate
    public void onManagedEntityLoaded() {
        calculateAge();
    }

    private void calculateAge() {

        if(birthDate == null) {
            age = null;
            return;
        }

        Calendar birth = Calendar.getInstance();
        birth.setTime(birthDate);
        Calendar today = Calendar.getInstance();
        age = (short) (today.get(Calendar.YEAR) - birth.get(Calendar.YEAR));
        if(today.get(Calendar.MONTH) < birth.get(Calendar.MONTH)) {
            age--;
        } else if (today.get(Calendar.MONTH) == birth.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) < birth.get(Calendar.DAY_OF_MONTH)) {
            age--;
        }
    }

}
