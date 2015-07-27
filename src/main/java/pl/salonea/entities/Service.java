package pl.salonea.entities;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "service")
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = Service.FIND_BY_NAME, query = "SELECT s FROM Service s WHERE s.serviceName = :name"),
        @NamedQuery(name = Service.FIND_BY_DESCRIPTION, query = "SELECT s FROM Service s WHERE s.description = :description"),
        @NamedQuery(name = Service.FIND_BY_CATEGORY, query = "SELECT s FROM Service s WHERE s.serviceCategory = :service_category"),
        @NamedQuery(name = Service.FIND_BY_PROVIDER, query = "SELECT s FROM Service s INNER JOIN s.providedServiceOffers ps WHERE ps.provider = :provider"),
        @NamedQuery(name = Service.FIND_BY_EMPLOYEE, query = "SELECT s FROM Service s INNER JOIN s.providedServiceOffers ps WHERE :employee MEMBER OF ps.supplyingEmployees")
})
public class Service {

    public static final String FIND_BY_NAME = "Service.findByName";
    public static final String FIND_BY_DESCRIPTION = "Service.findByDescription";
    public static final String FIND_BY_CATEGORY = "Service.findByCategory";
    public static final String FIND_BY_PROVIDER = "Service.findByProvider";
    public static final String FIND_BY_EMPLOYEE = "Service.findByEmployee";

    private Integer serviceId;
    private String serviceName; // business key
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

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 31) // two randomly chosen prime numbers
                // if deriving: .appendSuper(super.hashCode())
                .append(getServiceName())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Service))
            return false;
        if (obj == this)
            return true;

        Service rhs = (Service) obj;
        return new EqualsBuilder()
                // if deriving: .appendSuper(super.equals(obj)).
                .append(getServiceName(), rhs.getServiceName())
                .isEquals();
    }

}
