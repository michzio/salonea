package pl.salonea.entities;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pl.salonea.jaxrs.utils.hateoas.Link;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import java.util.*;

@XmlRootElement(name = "service")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"serviceId", "serviceName", "description", "serviceCategory", "links"})

@Entity
@Table(name = "service")
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = Service.FIND_ALL_EAGERLY, query = "SELECT DISTINCT s FROM Service s LEFT JOIN FETCH s.providedServiceOffers"),
        @NamedQuery(name = Service.FIND_BY_ID_EAGERLY, query = "SELECT s FROM Service s LEFT JOIN FETCH s.providedServiceOffers WHERE s.serviceId = :serviceId"),
        @NamedQuery(name = Service.FIND_BY_NAME, query = "SELECT s FROM Service s WHERE s.serviceName LIKE :name"),
        @NamedQuery(name = Service.FIND_BY_DESCRIPTION, query = "SELECT s FROM Service s WHERE s.description LIKE :description"),
        @NamedQuery(name = Service.FIND_BY_KEYWORD, query = "SELECT s FROM Service s WHERE s.serviceName LIKE :keyword OR s.description LIKE :keyword"),
        @NamedQuery(name = Service.FIND_BY_CATEGORY, query = "SELECT s FROM Service s WHERE s.serviceCategory = :service_category"),
        @NamedQuery(name = Service.FIND_BY_CATEGORY_EAGERLY, query = "SELECT DISTINCT s FROM Service s LEFT JOIN FETCH s.providedServiceOffers WHERE s.serviceCategory = :service_category"),
        @NamedQuery(name = Service.FIND_BY_CATEGORY_AND_NAME, query = "SELECT s FROM Service s WHERE s.serviceCategory = :service_category AND s.serviceName LIKE :name"),
        @NamedQuery(name = Service.FIND_BY_CATEGORY_AND_DESCRIPTION, query = "SELECT s FROM Service s WHERE s.serviceCategory = :service_category AND s.description LIKE :description"),
        @NamedQuery(name = Service.FIND_BY_CATEGORY_AND_KEYWORD, query = "SELECT s FROM Service s WHERE s.serviceCategory = :service_category AND (s.serviceName LIKE :keyword OR s.description LIKE :keyword)"),
        @NamedQuery(name = Service.FIND_BY_PROVIDER, query = "SELECT s FROM Service s INNER JOIN s.providedServiceOffers ps WHERE ps.provider = :provider"),
        @NamedQuery(name = Service.FIND_BY_PROVIDER_EAGERLY, query = "SELECT DISTINCT s FROM Service s INNER JOIN FETCH s.providedServiceOffers ps WHERE ps.provider = :provider"),
        @NamedQuery(name = Service.FIND_BY_EMPLOYEE, query = "SELECT DISTINCT s FROM Service s INNER JOIN s.providedServiceOffers ps WHERE :employee MEMBER OF ps.supplyingEmployees"),
        @NamedQuery(name = Service.FIND_BY_EMPLOYEE_EAGERLY, query = "SELECT DISTINCT s FROM Service s INNER JOIN FETCH s.providedServiceOffers ps WHERE :employee MEMBER OF ps.supplyingEmployees"),
        @NamedQuery(name = Service.FIND_BY_WORK_STATION, query = "SELECT DISTINCT s FROM Service s INNER JOIN s.providedServiceOffers ps WHERE :work_station MEMBER OF ps.workStations"),
        @NamedQuery(name = Service.FIND_BY_WORK_STATION_EAGERLY, query = "SELECT DISTINCT s FROM Service s INNER JOIN FETCH s.providedServiceOffers ps WHERE :work_station MEMBER OF ps.workStations"),
        @NamedQuery(name = Service.FIND_BY_SERVICE_POINT, query = "SELECT DISTINCT s FROM Service s INNER JOIN s.providedServiceOffers ps INNER JOIN ps.workStations ws WHERE ws.servicePoint = :service_point"),
        @NamedQuery(name = Service.FIND_BY_SERVICE_POINT_EAGERLY, query = "SELECT DISTINCT s FROM Service s INNER JOIN FETCH s.providedServiceOffers ps INNER JOIN ps.workStations ws WHERE ws.servicePoint = :service_point"),
        @NamedQuery(name = Service.FIND_BY_EMPLOYEE_TERM, query = "SELECT DISTINCT s FROM Service s INNER JOIN s.providedServiceOffers ps INNER JOIN ps.supplyingEmployees e INNER JOIN ps.workStations ws " +
                "INNER JOIN e.termsOnWorkStation empl_term INNER JOIN ws.termsEmployeesWorkOn ws_empl_term WHERE empl_term = ws_empl_term AND empl_term = :employee_term"),
        @NamedQuery(name = Service.FIND_BY_EMPLOYEE_TERM_EAGERLY, query = "SELECT DISTINCT s FROM Service s INNER JOIN FETCH s.providedServiceOffers ps INNER JOIN ps.supplyingEmployees e INNER JOIN ps.workStations ws " +
                "INNER JOIN e.termsOnWorkStation empl_term INNER JOIN ws.termsEmployeesWorkOn ws_empl_term WHERE empl_term = ws_empl_term AND empl_term = :employee_term"),
        @NamedQuery(name = Service.FIND_BY_TERM, query = "SELECT DISTINCT s FROM Service s INNER JOIN s.providedServiceOffers ps INNER JOIN ps.supplyingEmployees e INNER JOIN ps.workStations ws " +
                "INNER JOIN e.termsOnWorkStation empl_term INNER JOIN ws.termsEmployeesWorkOn ws_empl_term WHERE empl_term = ws_empl_term AND empl_term.term = :term"),
        @NamedQuery(name = Service.FIND_BY_TERM_EAGERLY, query = "SELECT DISTINCT s FROM Service s INNER JOIN FETCH s.providedServiceOffers ps INNER JOIN ps.supplyingEmployees e INNER JOIN ps.workStations ws " +
                "INNER JOIN e.termsOnWorkStation empl_term INNER JOIN ws.termsEmployeesWorkOn ws_empl_term WHERE empl_term = ws_empl_term AND empl_term.term = :term"),
        @NamedQuery(name = Service.COUNT_BY_CATEGORY, query = "SELECT COUNT(s) FROM Service s WHERE s.serviceCategory = :service_category"),
        @NamedQuery(name = Service.COUNT_BY_PROVIDER, query = "SELECT COUNT(s) FROM Service s INNER JOIN s.providedServiceOffers ps WHERE ps.provider = :provider"),
        @NamedQuery(name = Service.COUNT_BY_EMPLOYEE, query = "SELECT COUNT(DISTINCT s) FROM Service s INNER JOIN s.providedServiceOffers ps WHERE :employee MEMBER OF ps.supplyingEmployees"),
        @NamedQuery(name = Service.COUNT_BY_WORK_STATION, query = "SELECT COUNT(DISTINCT s) FROM Service s INNER JOIN s.providedServiceOffers ps WHERE :work_station MEMBER OF ps.workStations"),
        @NamedQuery(name = Service.COUNT_BY_SERVICE_POINT, query = "SELECT COUNT(DISTINCT s) FROM Service s INNER JOIN s.providedServiceOffers ps INNER JOIN ps.workStations ws WHERE ws.servicePoint = :service_point"),
        @NamedQuery(name = Service.COUNT_BY_EMPLOYEE_TERM, query = "SELECT COUNT(DISTINCT s) FROM Service s INNER JOIN s.providedServiceOffers ps INNER JOIN ps.supplyingEmployees e INNER JOIN ps.workStations ws " +
                "INNER JOIN e.termsOnWorkStation empl_term INNER JOIN ws.termsEmployeesWorkOn ws_empl_term WHERE empl_term = ws_empl_term AND empl_term = :employee_term"),
        @NamedQuery(name = Service.COUNT_BY_TERM, query = "SELECT COUNT(DISTINCT s) FROM Service s INNER JOIN s.providedServiceOffers ps INNER JOIN ps.supplyingEmployees e INNER JOIN ps.workStations ws " +
                "INNER JOIN e.termsOnWorkStation empl_term INNER JOIN ws.termsEmployeesWorkOn ws_empl_term WHERE empl_term = ws_empl_term AND empl_term.term = :term"),
        @NamedQuery(name = Service.DELETE_BY_NAME, query = "DELETE FROM Service s WHERE s.serviceName = :name"),
        @NamedQuery(name = Service.DELETE_BY_CATEGORY, query = "DELETE FROM Service s WHERE s.serviceCategory = :service_category"),

})
public class Service {

    public static final String FIND_ALL_EAGERLY = "Service.findAllEagerly";
    public static final String FIND_BY_ID_EAGERLY = "Service.findByIdEagerly";
    public static final String FIND_BY_NAME = "Service.findByName";
    public static final String FIND_BY_DESCRIPTION = "Service.findByDescription";
    public static final String FIND_BY_KEYWORD = "Service.findByKeyword";
    public static final String FIND_BY_CATEGORY = "Service.findByCategory";
    public static final String FIND_BY_CATEGORY_EAGERLY = "Service.findByCategoryEagerly";
    public static final String FIND_BY_CATEGORY_AND_NAME = "Service.findByCategoryAndName";
    public static final String FIND_BY_CATEGORY_AND_DESCRIPTION = "Service.findByCategoryAndDescription";
    public static final String FIND_BY_CATEGORY_AND_KEYWORD = "Service.findByCategoryAndKeyword";
    public static final String FIND_BY_PROVIDER = "Service.findByProvider";
    public static final String FIND_BY_PROVIDER_EAGERLY = "Service.findByProviderEagerly";
    public static final String FIND_BY_EMPLOYEE = "Service.findByEmployee";
    public static final String FIND_BY_EMPLOYEE_EAGERLY = "Service.findByEmployeeEagerly";
    public static final String FIND_BY_WORK_STATION = "Service.findByWorkStation";
    public static final String FIND_BY_WORK_STATION_EAGERLY = "Service.findByWorkStationEagerly";
    public static final String FIND_BY_SERVICE_POINT = "Service.findByServicePoint";
    public static final String FIND_BY_SERVICE_POINT_EAGERLY = "Service.findByServicePointEagerly";
    public static final String FIND_BY_EMPLOYEE_TERM = "Service.findByEmployeeTerm";
    public static final String FIND_BY_EMPLOYEE_TERM_EAGERLY = "Service.findByEmployeeTermEagerly";
    public static final String FIND_BY_TERM = "Service.findByTerm";
    public static final String FIND_BY_TERM_EAGERLY = "Service.findByTermEagerly";
    public static final String COUNT_BY_CATEGORY = "Service.countByCategory";
    public static final String COUNT_BY_PROVIDER = "Service.countByProvider";
    public static final String COUNT_BY_EMPLOYEE = "Service.countByEmployee";
    public static final String COUNT_BY_WORK_STATION = "Service.countByWorkStation";
    public static final String COUNT_BY_SERVICE_POINT = "Service.countByServicePoint";
    public static final String COUNT_BY_EMPLOYEE_TERM = "Service.countByEmployeeTerm";
    public static final String COUNT_BY_TERM = "Service.countByTerm";
    public static final String DELETE_BY_NAME = "Service.deleteByName";
    public static final String DELETE_BY_CATEGORY = "Service.deleteByCategory";

    private Integer serviceId;
    private String serviceName; // business key
    private String description;

    /* many-to-one relationship */
    private ServiceCategory serviceCategory;

    /* one-to-many relationship */
    private Set<ProviderService> providedServiceOffers;

    // HATEOAS support for RESTFul web service in JAX-RS
    private LinkedHashSet<Link> links = new LinkedHashSet<>();

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
    @Column(name = "service_id", nullable = false /*, columnDefinition = "INT UNSIGNED" */)
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

    @XmlTransient
    @OneToMany(mappedBy = "service", fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE })
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

    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    @Transient
    public LinkedHashSet<Link> getLinks() {
        return links;
    }

    public void setLinks(LinkedHashSet<Link> links) {
        this.links = links;
    }
}