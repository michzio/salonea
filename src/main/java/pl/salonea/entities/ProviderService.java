package pl.salonea.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pl.salonea.constraints.MutualProvider;
import pl.salonea.constraints.PriceNeedType;
import pl.salonea.constraints.PriceTypeDependentDuration;
import pl.salonea.entities.idclass.ProviderServiceId;
import pl.salonea.enums.CurrencyCode;
import pl.salonea.enums.PriceType;
import pl.salonea.jaxrs.utils.hateoas.Link;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.util.*;

@XmlRootElement(name = "provider-service")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"provider", "service", "description", "serviceDuration", "price", "priceType", "priceCurrencyCode", "discount", "links" })

@Entity
@IdClass(ProviderServiceId.class)
@Table(name = "provider_service")
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = ProviderService.FIND_ALL_EAGERLY, query = "SELECT ps FROM ProviderService ps LEFT JOIN FETCH ps.supplyingEmployees e LEFT JOIN FETCH ps.workStations ws"),
        @NamedQuery(name = ProviderService.FIND_BY_ID_EAGERLY, query = "SELECT ps FROM ProviderService ps LEFT JOIN FETCH ps.supplyingEmployees e LEFT JOIN FETCH  ps.workStations ws WHERE ps.provider.userId = :userId AND ps.service.serviceId = :serviceId"),
        @NamedQuery(name = ProviderService.FIND_BY_PROVIDER, query = "SELECT ps FROM ProviderService ps WHERE ps.provider = :provider"),
        @NamedQuery(name = ProviderService.FIND_BY_PROVIDER_EAGERLY, query = "SELECT DISTINCT ps FROM ProviderService ps LEFT JOIN FETCH ps.supplyingEmployees e LEFT JOIN FETCH ps.workStations ws WHERE ps.provider = :provider"),
        @NamedQuery(name = ProviderService.FIND_BY_PROVIDER_AND_DESCRIPTION, query = "SELECT ps FROM ProviderService ps WHERE ps.provider = :provider AND ps.description LIKE :description"),
        @NamedQuery(name = ProviderService.FIND_BY_PROVIDER_AND_DISCOUNT, query = "SELECT ps FROM ProviderService ps WHERE ps.provider = :provider AND ps.discount >= :min_discount AND ps.discount <= :max_discount"),
        @NamedQuery(name = ProviderService.FIND_BY_PROVIDER_AND_SERVICE_CATEGORY, query = "SELECT ps FROM ProviderService ps INNER JOIN ps.service s WHERE ps.provider = :provider AND s.serviceCategory = :service_category"),
        @NamedQuery(name = ProviderService.FIND_BY_PROVIDER_AND_EMPLOYEE, query = "SELECT ps FROM ProviderService ps WHERE ps.provider = :provider AND :employee MEMBER OF ps.supplyingEmployees"),
        @NamedQuery(name = ProviderService.FIND_BY_SERVICE, query = "SELECT ps FROM ProviderService ps WHERE ps.service = :service"),
        @NamedQuery(name = ProviderService.FIND_BY_SERVICE_EAGERLY, query =  "SELECT DISTINCT ps FROM ProviderService ps LEFT JOIN FETCH ps.supplyingEmployees e LEFT JOIN FETCH ps.workStations ws WHERE ps.service = :service"),
        @NamedQuery(name = ProviderService.FIND_BY_SERVICE_CATEGORY, query = "SELECT ps FROM ProviderService ps INNER JOIN ps.service s WHERE s.serviceCategory = :service_category"),
        @NamedQuery(name = ProviderService.FIND_BY_SERVICE_CATEGORY_EAGERLY, query = "SELECT DISTINCT ps FROM ProviderService ps LEFT JOIN FETCH ps.supplyingEmployees e LEFT JOIN FETCH ps.workStations ws INNER JOIN ps.service s WHERE s.serviceCategory = :service_category"),
        @NamedQuery(name = ProviderService.FIND_BY_SERVICE_AND_DESCRIPTION, query = "SELECT ps FROM ProviderService ps WHERE ps.service = :service  AND ps.description LIKE :description"),
        @NamedQuery(name = ProviderService.FIND_BY_SERVICE_AND_PRICE, query = "SELECT ps FROM ProviderService ps WHERE ps.service = :service AND ps.price >= :min_price AND ps.price <= :max_price"),
        @NamedQuery(name = ProviderService.FIND_BY_SERVICE_AND_DISCOUNTED_PRICE, query = "SELECT ps FROM ProviderService ps WHERE ps.service = :service AND ps.price*(100.0-ps.discount)/100.0 >= :min_price AND ps.price*(100.0-ps.discount)/100.0 <= :max_price"),
        @NamedQuery(name = ProviderService.FIND_BY_SERVICE_AND_DISCOUNT, query = "SELECT ps FROM ProviderService ps WHERE ps.service = :service AND ps.discount >= :min_discount AND ps.discount <= :max_discount"),
        @NamedQuery(name = ProviderService.FIND_BY_WORK_STATION, query = "SELECT ps FROM ProviderService ps WHERE :work_station MEMBER OF ps.workStations"),
        @NamedQuery(name = ProviderService.FIND_BY_WORK_STATION_EAGERLY, query = "SELECT DISTINCT ps FROM ProviderService ps LEFT JOIN FETCH ps.supplyingEmployees e INNER JOIN FETCH ps.workStations ws WHERE ws = :work_station"),
        @NamedQuery(name = ProviderService.FIND_BY_SERVICE_POINT, query = "SELECT ps FROM ProviderService ps INNER JOIN ps.workStations ws WHERE ws.servicePoint = :service_point"),
        @NamedQuery(name = ProviderService.FIND_BY_SERVICE_POINT_EAGERLY, query = "SELECT DISTINCT ps FROM ProviderService ps LEFT JOIN FETCH ps.supplyingEmployees e INNER JOIN FETCH ps.workStations ws WHERE ws.servicePoint = :service_point"),
        @NamedQuery(name = ProviderService.FIND_BY_EMPLOYEE, query = "SELECT ps FROM ProviderService ps WHERE :employee MEMBER OF ps.supplyingEmployees"),
        @NamedQuery(name = ProviderService.FIND_BY_EMPLOYEE_EAGERLY, query = "SELECT DISTINCT ps FROM ProviderService ps INNER JOIN FETCH ps.supplyingEmployees e LEFT JOIN FETCH ps.workStations ws WHERE e = :employee"),
        @NamedQuery(name = ProviderService.FIND_BY_EMPLOYEE_TERM, query = "SELECT DISTINCT ps FROM ProviderService ps INNER JOIN ps.supplyingEmployees e INNER JOIN ps.workStations ws " +
                "INNER JOIN e.termsOnWorkStation empl_term INNER JOIN ws.termsEmployeesWorkOn ws_empl_term WHERE empl_term = ws_empl_term AND empl_term = :employee_term"),
        @NamedQuery(name = ProviderService.FIND_BY_EMPLOYEE_TERM_EAGERLY, query = "SELECT DISTINCT ps FROM ProviderService ps INNER JOIN FETCH ps.supplyingEmployees e INNER JOIN FETCH ps.workStations ws " +
                "INNER JOIN e.termsOnWorkStation empl_term INNER JOIN ws.termsEmployeesWorkOn ws_empl_term WHERE empl_term = ws_empl_term AND empl_term = :employee_term"),
        @NamedQuery(name = ProviderService.FIND_BY_TERM, query = "SELECT DISTINCT ps FROM ProviderService ps INNER JOIN ps.supplyingEmployees e INNER JOIN ps.workStations ws " +
                "INNER JOIN e.termsOnWorkStation empl_term INNER JOIN ws.termsEmployeesWorkOn ws_empl_term WHERE empl_term = ws_empl_term AND empl_term.term = :term"),
        @NamedQuery(name = ProviderService.FIND_BY_TERM_EAGERLY, query = "SELECT DISTINCT ps FROM ProviderService ps INNER JOIN FETCH ps.supplyingEmployees e INNER JOIN FETCH ps.workStations ws " +
                "INNER JOIN e.termsOnWorkStation empl_term INNER JOIN ws.termsEmployeesWorkOn ws_empl_term WHERE empl_term = ws_empl_term AND empl_term.term = :term"),
        @NamedQuery(name = ProviderService.UPDATE_DISCOUNT_FOR_PROVIDER, query = "UPDATE ProviderService ps SET ps.discount = :new_discount WHERE ps.provider = :provider"),
        @NamedQuery(name = ProviderService.UPDATE_DISCOUNT_FOR_PROVIDER_AND_SERVICE_CATEGORY, query = "UPDATE ProviderService ps SET ps.discount = :new_discount WHERE ps.provider = :provider AND ps.service IN (SELECT s FROM Service s WHERE s.serviceCategory = :service_category)"),
        @NamedQuery(name = ProviderService.UPDATE_DISCOUNT_FOR_PROVIDER_AND_EMPLOYEE, query = "UPDATE ProviderService ps SET ps.discount = :new_discount WHERE ps.provider = :provider AND :employee MEMBER OF ps.supplyingEmployees"),
        @NamedQuery(name = ProviderService.UPDATE_DISCOUNT_FOR_PROVIDER_AND_SERVICE_CATEGORY_AND_EMPLOYEE, query = "UPDATE ProviderService ps SET ps.discount = :new_discount WHERE ps.provider = :provider AND" +
                " ps.service IN (SELECT s FROM Service s WHERE s.serviceCategory = :service_category) AND :employee MEMBER OF ps.supplyingEmployees"),
        @NamedQuery(name = ProviderService.DELETE_BY_ID, query = "DELETE FROM ProviderService ps WHERE ps.provider.userId = :userId AND ps.service.serviceId = :serviceId"),
        @NamedQuery(name = ProviderService.DELETE_FOR_ONLY_WORK_STATION, query = "DELETE FROM ProviderService ps WHERE :work_station MEMBER OF ps.workStations AND SIZE(ps.workStations) = 1"),
        @NamedQuery(name = ProviderService.DELETE_FOR_PROVIDER_AND_ONLY_EMPLOYEE, query = "DELETE FROM ProviderService ps WHERE ps.provider = :provider AND :employee MEMBER OF ps.supplyingEmployees AND SIZE(ps.supplyingEmployees)= 1"),
        @NamedQuery(name = ProviderService.DELETE_FOR_PROVIDER_AND_SERVICE_CATEGORY, query = "DELETE FROM ProviderService ps WHERE ps.provider = :provider AND ps.service IN (SELECT s FROM Service s WHERE s.serviceCategory = :service_category)"),
        @NamedQuery(name = ProviderService.DELETE_FOR_PROVIDER_AND_SERVICE_CATEGORY_AND_ONLY_EMPLOYEE, query = "DELETE FROM ProviderService ps WHERE ps.provider = :provider AND" +
                " ps.service IN (SELECT s FROM Service s WHERE s.serviceCategory = :service_category) AND :employee MEMBER OF ps.supplyingEmployees AND SIZE(ps.supplyingEmployees)= 1"),
        @NamedQuery(name = ProviderService.DELETE_FOR_PROVIDER, query = "DELETE FROM ProviderService ps WHERE ps.provider = :provider"),
        @NamedQuery(name = ProviderService.DELETE_FOR_SERVICE, query = "DELETE FROM ProviderService ps WHERE ps.service = :service"),
        @NamedQuery(name = ProviderService.COUNT_BY_PROVIDER, query = "SELECT COUNT(ps) FROM ProviderService ps WHERE ps.provider = :provider"),
        @NamedQuery(name = ProviderService.COUNT_BY_SERVICE, query = "SELECT COUNT(ps) FROM ProviderService ps WHERE ps.service = :service"),
        @NamedQuery(name = ProviderService.COUNT_BY_SERVICE_CATEGORY, query = "SELECT COUNT(ps) FROM ProviderService ps INNER JOIN ps.service s WHERE s.serviceCategory = :service_category"),
        @NamedQuery(name = ProviderService.COUNT_BY_WORK_STATION, query = "SELECT COUNT(ps) FROM ProviderService ps WHERE :work_station MEMBER OF ps.workStations"),
        @NamedQuery(name = ProviderService.COUNT_BY_SERVICE_POINT, query = "SELECT COUNT(DISTINCT ps) FROM ProviderService ps INNER JOIN ps.workStations ws WHERE ws.servicePoint = :service_point"),
        @NamedQuery(name = ProviderService.COUNT_BY_EMPLOYEE, query = "SELECT COUNT(ps) FROM ProviderService ps WHERE :employee MEMBER OF ps.supplyingEmployees"),
        @NamedQuery(name = ProviderService.COUNT_BY_EMPLOYEE_TERM, query = "SELECT COUNT(DISTINCT ps) FROM ProviderService ps INNER JOIN ps.supplyingEmployees e INNER JOIN ps.workStations ws " +
                "INNER JOIN e.termsOnWorkStation empl_term INNER JOIN ws.termsEmployeesWorkOn ws_empl_term WHERE empl_term = ws_empl_term AND empl_term = :employee_term"),
        @NamedQuery(name = ProviderService.COUNT_BY_TERM, query = "SELECT COUNT(DISTINCT ps) FROM ProviderService ps INNER JOIN ps.supplyingEmployees e INNER JOIN ps.workStations ws " +
                "INNER JOIN e.termsOnWorkStation empl_term INNER JOIN ws.termsEmployeesWorkOn ws_empl_term WHERE empl_term = ws_empl_term AND empl_term.term = :term")
})
@MutualProvider
@PriceNeedType
@PriceTypeDependentDuration
public class ProviderService {

    public static final String FIND_ALL_EAGERLY = "ProviderService.findAllEagerly";
    public static final String FIND_BY_ID_EAGERLY = "ProviderService.findByIdEagerly";
    public static final String FIND_BY_DESCRIPTION = "ProviderService.findByDescription";

    public static final String FIND_BY_PROVIDER = "ProviderService.findByProvider";
    public static final String FIND_BY_PROVIDER_EAGERLY = "ProviderService.findByProviderEagerly";
    public static final String FIND_BY_PROVIDER_AND_DESCRIPTION = "ProviderService.findByProviderAndDescription";
    public static final String FIND_BY_PROVIDER_AND_DISCOUNT = "ProviderService.findByProviderAndDiscount";
    public static final String FIND_BY_PROVIDER_AND_EMPLOYEE = "ProviderService.findByProviderAndEmployee";
    public static final String FIND_BY_PROVIDER_AND_SERVICE_CATEGORY = "ProviderService.findByProviderAndServiceCategory";

    public static final String FIND_BY_SERVICE = "ProviderService.findByService";
    public static final String FIND_BY_SERVICE_EAGERLY = "ProviderService.findByServiceEagerly";
    public static final String FIND_BY_SERVICE_AND_DESCRIPTION = "ProviderService.findByServiceAndDescription";
    public static final String FIND_BY_SERVICE_AND_PRICE = "ProviderService.findByServiceAndPrice";
    public static final String FIND_BY_SERVICE_AND_DISCOUNTED_PRICE = "ProviderService.findByServiceAndDiscountedPrice";
    public static final String FIND_BY_SERVICE_AND_DISCOUNT = "ProviderService.findByServiceAndDiscount";

    public static final String FIND_BY_SERVICE_CATEGORY = "ProviderService.findByServiceCategory";
    public static final String FIND_BY_SERVICE_CATEGORY_EAGERLY = "ProviderService.findByServiceCategoryEagerly";

    public static final String FIND_BY_WORK_STATION = "ProviderService.findByWorkStation";
    public static final String FIND_BY_WORK_STATION_EAGERLY = "ProviderService.findByWorkStationEagerly";

    public static final String FIND_BY_SERVICE_POINT = "ProviderService.findByServicePoint";
    public static final String FIND_BY_SERVICE_POINT_EAGERLY = "ProviderService.findByServicePointEagerly";

    public static final String FIND_BY_EMPLOYEE = "ProviderService.findByEmployee";
    public static final String FIND_BY_EMPLOYEE_EAGERLY = "ProviderService.findByEmployeeEagerly";

    public static final String FIND_BY_EMPLOYEE_TERM = "ProviderService.findByEmployeeTerm";
    public static final String FIND_BY_EMPLOYEE_TERM_EAGERLY = "ProviderService.findByEmployeeTermEagerly";

    public static final String FIND_BY_TERM = "ProviderService.findByTerm";
    public static final String FIND_BY_TERM_EAGERLY = "ProviderService.findByTermEagerly";

    public static final String UPDATE_DISCOUNT_FOR_PROVIDER = "ProviderService.updateDiscountForProvider";
    public static final String UPDATE_DISCOUNT_FOR_PROVIDER_AND_SERVICE_CATEGORY = "ProviderService.updateDiscountForProviderAndServiceCategory";
    public static final String UPDATE_DISCOUNT_FOR_PROVIDER_AND_EMPLOYEE = "ProviderService.updateDiscountForProviderAndEmployee";
    public static final String UPDATE_DISCOUNT_FOR_PROVIDER_AND_SERVICE_CATEGORY_AND_EMPLOYEE = "ProviderService.updateDiscountForProviderAndServiceCategoryAndEmployee";

    public static final String DELETE_BY_ID = "ProviderService.deleteById";
    public static final String DELETE_FOR_ONLY_WORK_STATION = "ProviderService.deleteForOnlyWorkStation";
    public static final String DELETE_FOR_PROVIDER_AND_ONLY_EMPLOYEE = "ProviderService.deleteForProviderAndOnlyEmployee";
    public static final String DELETE_FOR_PROVIDER_AND_SERVICE_CATEGORY = "ProviderService.deleteForProviderAndServiceCategory";
    public static final String DELETE_FOR_PROVIDER_AND_SERVICE_CATEGORY_AND_ONLY_EMPLOYEE = "ProviderService.deleteForProviderAndServiceCategoryAndOnlyEmployee";
    public static final String DELETE_FOR_PROVIDER = "ProviderService.deleteForProvider";
    public static final String DELETE_FOR_SERVICE = "ProviderService.deleteForService";

    public static final String COUNT_BY_PROVIDER = "ProviderService.countByProvider";
    public static final String COUNT_BY_SERVICE = "ProviderService.countByService";
    public static final String COUNT_BY_SERVICE_CATEGORY = "ProviderService.countByServiceCategory";
    public static final String COUNT_BY_WORK_STATION = "ProviderService.countByWorkStation";
    public static final String COUNT_BY_SERVICE_POINT = "ProviderService.countByServicePoint";
    public static final String COUNT_BY_EMPLOYEE = "ProviderService.countByEmployee";
    public static final String COUNT_BY_EMPLOYEE_TERM = "ProviderService.countByEmployeeTerm";
    public static final String COUNT_BY_TERM = "ProviderService.countByTerm";

    private Provider provider; // PK, FK
    private Service service; // PK, FK

    private String description;
    private Long serviceDuration; // [ms]
    private Double price;
    private PriceType priceType; // per hour, per service, per day
    private CurrencyCode priceCurrencyCode;
    private Short discount = 0; // [%]

    /* many-to-many relationships */
    private Set<Employee> supplyingEmployees = new HashSet<>();
    private Set<WorkStation> workStations = new HashSet<>();

    // HATEOAS support for RESTFul web service in JAX-RS
    private LinkedHashSet<Link> links = new LinkedHashSet<>();

    /* constructors */

    public ProviderService() { }

    public ProviderService(Provider provider, Service service) {
        this.provider = provider;
        this.service = service;
    }

    public ProviderService(Provider provider, Service service, Long serviceDuration) {
        this.provider = provider;
        this.service = service;
        this.serviceDuration = serviceDuration;
    }

    /* PK getters and setters */

    @Id
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "provider_id", referencedColumnName = "provider_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    @Id
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id", referencedColumnName = "service_id", nullable = false, columnDefinition = "INT UNSIGNED")
    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    /* other getters and setters */

    @Lob
    @Column(name = "description", columnDefinition = "LONGTEXT DEFAULT NULL")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NotNull
    @Min(0)
    @Column(name = "service_duration", nullable = false, columnDefinition = "BIGINT UNSIGNED DEFAULT 0")
    public Long getServiceDuration() {
        return serviceDuration;
    }

    public void setServiceDuration(Long serviceDuration) {
        this.serviceDuration = serviceDuration;
    }

    @Digits(integer = 8, fraction = 2)
    @Column(name = "price", columnDefinition = "NUMERIC(10,2) DEFAULT NULL")
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "price_type", columnDefinition = "ENUM('PER_HOUR', 'PER_DAY', 'PER_SERVICE') DEFAULT NULL")
    public PriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "price_currency_code", columnDefinition = "CHAR(3) DEFAULT NULL")
    public CurrencyCode getPriceCurrencyCode() {
        return priceCurrencyCode;
    }

    public void setPriceCurrencyCode(CurrencyCode priceCurrencyCode) {
        this.priceCurrencyCode = priceCurrencyCode;
    }

    @Min(0) @Max(99)
    @Column(name = "discount", nullable = false, columnDefinition = "TINYINT(2) UNSIGNED DEFAULT 0")
    public Short getDiscount() {
        return discount;
    }

    public void setDiscount(Short discount) {
        this.discount = discount;
    }

    /* many-to-many relationships */

    @XmlTransient
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "service_supply",
            joinColumns = {
                    @JoinColumn(name = "provider_id", referencedColumnName = "provider_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"),
                    @JoinColumn(name = "service_id", referencedColumnName = "service_id", nullable = false, columnDefinition = "INT UNSIGNED")
            },
            inverseJoinColumns = @JoinColumn(name = "employee_id", referencedColumnName = "employee_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    )
    public Set<Employee> getSupplyingEmployees() {
        return supplyingEmployees;
    }

    public void setSupplyingEmployees(Set<Employee> supplyingEmployees) {
        this.supplyingEmployees = supplyingEmployees;
    }

    @XmlTransient
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "service_provided_on",
            joinColumns = {
                    @JoinColumn(name = "provider_id", referencedColumnName = "provider_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"),
                    @JoinColumn(name = "service_id", referencedColumnName = "service_id", nullable = false, columnDefinition = "INT UNSIGNED")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "work_station_provider_id", referencedColumnName = "provider_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"),
                    @JoinColumn(name = "service_point_no", referencedColumnName = "service_point_no", nullable = false, columnDefinition = "INT UNSIGNED"),
                    @JoinColumn(name = "work_station_no", referencedColumnName = "work_station_no", nullable = false, columnDefinition = "INT UNSIGNED")
            }
    )
    public Set<WorkStation> getWorkStations() {
        return workStations;
    }

    public void setWorkStations(Set<WorkStation> workStations) {
        this.workStations = workStations;
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 31) // two randomly chosen prime numbers
                // if deriving: .appendSuper(super.hashCode()).
                .append(getProvider())
                .append(getService())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ProviderService))
            return false;
        if (obj == this)
            return true;

        ProviderService rhs = (ProviderService) obj;
        return new EqualsBuilder()
                // if deriving: .appendSuper(super.equals(obj)).
                .append(getProvider(), rhs.getProvider())
                .append(getService(), rhs.getService())
                .isEquals();
    }

    @Transient
    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    public LinkedHashSet<Link> getLinks() {
        return links;
    }

    public void setLinks(LinkedHashSet<Link> links) {
        this.links = links;
    }
}
