package pl.salonea.entities;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "service")
@Access(AccessType.PROPERTY)
public class Service {

    private Integer serviceId;
    private String serviceName;
    private String description;

    /* many-to-one relationship */
    private ServiceCategory serviceCategory;

    /* one-to-many relationship */
    private Set<ProviderService> providedServiceOffers;

    /* constructors */

    public Service() { }

    public Service(String serviceName) {
        this.serviceName = serviceName;
    }

    public Service(String serviceName, ServiceCategory serviceCategory) {
        this.serviceName = serviceName;
        this.serviceCategory = serviceCategory;
    }

    /* getters and setters */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false) // runtime scoped not null, whereas nullable references only to table
    @Column(name = "service_id", nullable = false, columnDefinition = "INT UNSIGNED")
    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    @NotNull
    @Size(min = 2, max = 255)
    @Column(name = "service_name", nullable = false, unique = true, length = 255)
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Lob
    @Column(name = "description", columnDefinition = "LONGTEXT DEFAULT NULL")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /* many-to-one relationship */

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id", columnDefinition = "INT UNSIGNED DEFAULT NULL")
    public ServiceCategory getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(ServiceCategory serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    /* one-to-many relationship */

    @OneToMany(mappedBy = "service", fetch = FetchType.LAZY)
    public Set<ProviderService> getProvidedServiceOffers() {
        return providedServiceOffers;
    }

    public void setProvidedServiceOffers(Set<ProviderService> providedServiceOffers) {
        this.providedServiceOffers = providedServiceOffers;
    }

}
