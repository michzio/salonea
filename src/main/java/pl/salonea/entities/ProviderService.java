package pl.salonea.entities;

import pl.salonea.constraints.MutualProvider;
import pl.salonea.entities.idclass.ProviderServiceId;
import pl.salonea.enums.CurrencyCode;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.DoubleSummaryStatistics;
import java.util.Set;

@Entity
@Table(name = "provider_service")
@Access(AccessType.PROPERTY)
@IdClass(ProviderServiceId.class)
@MutualProvider
public class ProviderService {

    private Provider provider; // PK, FK
    private Service service; // PK, FK

    private String description;
    private Long serviceDuration; // [ms]
    private Double price;
    private CurrencyCode priceCurrencyCode;
    private Short discount; // [%]

    /* many-to-many relationships */
    private Set<Employee> supplyingEmployees;
    private Set<WorkStation> workStations;

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
    @Column(name = "price_currency_code", columnDefinition = "CHAR(3) DEFAULT NULL")
    public CurrencyCode getPriceCurrencyCode() {
        return priceCurrencyCode;
    }

    public void setPriceCurrencyCode(CurrencyCode priceCurrencyCode) {
        this.priceCurrencyCode = priceCurrencyCode;
    }

    @Min(0) @Max(99)
    @Column(name = "discount", columnDefinition = "TINYINT(2) UNSIGNED DEFAULT NULL")
    public Short getDiscount() {
        return discount;
    }

    public void setDiscount(Short discount) {
        this.discount = discount;
    }

    /* one-to-many relationships */

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
}
