package pl.salonea.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pl.salonea.entities.idclass.WorkStationId;
import pl.salonea.enums.WorkStationType;
import pl.salonea.jaxrs.utils.hateoas.Link;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.*;

@XmlRootElement(name = "work-station")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"workStationNumber", "servicePoint", "workStationType", "links"})

@Entity
@Table(name = "work_station")
@Access(AccessType.PROPERTY)
@IdClass(WorkStationId.class)
@NamedQueries({
        @NamedQuery(name = WorkStation.FIND_ALL_EAGERLY, query = "SELECT DISTINCT ws FROM WorkStation ws LEFT JOIN FETCH ws.providedServices LEFT JOIN FETCH ws.termsEmployeesWorkOn"),
        @NamedQuery(name = WorkStation.FIND_BY_ID_EAGERLY, query = "SELECT ws FROM WorkStation ws LEFT JOIN FETCH ws.providedServices LEFT JOIN FETCH ws.termsEmployeesWorkOn " +
                "WHERE ws.servicePoint.provider.userId = :userId AND ws.servicePoint.servicePointNumber = :servicePointNumber AND ws.workStationNumber = :workStationNumber"),
        @NamedQuery(name = WorkStation.FIND_BY_TYPE, query = "SELECT ws FROM WorkStation ws WHERE ws.workStationType = :work_station_type"),
        @NamedQuery(name = WorkStation.FIND_BY_PERIOD, query = "SELECT DISTINCT ws FROM WorkStation ws INNER JOIN ws.termsEmployeesWorkOn empl_term INNER JOIN empl_term.term term WHERE term.openingTime < :end_time AND term.closingTime > :start_time"), // constraint: openingTime < closingTime
        @NamedQuery(name = WorkStation.FIND_BY_PERIOD_STRICT, query = "SELECT DISTINCT ws FROM WorkStation ws INNER JOIN ws.termsEmployeesWorkOn empl_term INNER JOIN empl_term.term term WHERE term.openingTime <= :start_time AND term.closingTime >= :end_time"),
        @NamedQuery(name = WorkStation.FIND_BY_TERM, query = "SELECT DISTINCT ws FROM WorkStation ws INNER JOIN ws.termsEmployeesWorkOn empl_term WHERE empl_term.term = :term"),
        @NamedQuery(name = WorkStation.FIND_BY_TERM_EAGERLY, query = "SELECT DISTINCT ws FROM WorkStation ws LEFT JOIN FETCH ws.providedServices INNER JOIN FETCH ws.termsEmployeesWorkOn empl_term WHERE empl_term.term = :term"),
        @NamedQuery(name = WorkStation.FIND_BY_SERVICE_POINT, query = "SELECT ws FROM WorkStation ws WHERE ws.servicePoint = :service_point"),
        @NamedQuery(name = WorkStation.FIND_BY_SERVICE_POINT_EAGERLY, query = "SELECT DISTINCT ws FROM WorkStation ws LEFT JOIN FETCH ws.providedServices LEFT JOIN FETCH ws.termsEmployeesWorkOn WHERE ws.servicePoint = :service_point"),
        @NamedQuery(name = WorkStation.FIND_BY_SERVICE_POINT_AND_TYPE, query = "SELECT ws FROM WorkStation ws WHERE ws.servicePoint = :service_point AND ws.workStationType = :work_station_type"),
        @NamedQuery(name = WorkStation.FIND_BY_SERVICE_POINT_AND_TERM, query = "SELECT DISTINCT ws FROM WorkStation ws INNER JOIN ws.termsEmployeesWorkOn empl_term INNER JOIN empl_term.term term WHERE ws.servicePoint = :service_point AND term.openingTime < :end_time AND term.closingTime > :start_time"), // constraint: openingTime < closingTime
        @NamedQuery(name = WorkStation.FIND_BY_SERVICE_POINT_AND_TERM_STRICT, query = "SELECT DISTINCT ws FROM WorkStation ws INNER JOIN ws.termsEmployeesWorkOn empl_term INNER JOIN empl_term.term term WHERE ws.servicePoint = :service_point AND term.openingTime <= :start_time AND term.closingTime >= :end_time"),
        @NamedQuery(name = WorkStation.FIND_BY_SERVICE, query = "SELECT ws FROM WorkStation ws INNER JOIN ws.providedServices ps WHERE ps.service = :service"),
        @NamedQuery(name = WorkStation.FIND_BY_SERVICE_EAGERLY, query = "SELECT DISTINCT ws FROM WorkStation ws INNER JOIN FETCH ws.providedServices ps LEFT JOIN FETCH ws.termsEmployeesWorkOn WHERE ps.service = :service"),
        @NamedQuery(name = WorkStation.FIND_BY_SERVICE_AND_TERM, query = "SELECT DISTINCT ws FROM WorkStation ws INNER JOIN ws.providedServices ps INNER JOIN ws.termsEmployeesWorkOn empl_term INNER JOIN empl_term.employee e " +
                "INNER JOIN e.suppliedServices empl_ps INNER JOIN empl_term.term term WHERE ps.service = :service AND ps = empl_ps AND term.openingTime < :end_time AND term.closingTime > :start_time"), // constraint: openingTime < closingTime
        @NamedQuery(name = WorkStation.FIND_BY_SERVICE_AND_TERM_STRICT, query = "SELECT DISTINCT ws FROM WorkStation ws INNER JOIN ws.providedServices ps INNER JOIN ws.termsEmployeesWorkOn empl_term INNER JOIN empl_term.employee e " +
                "INNER JOIN e.suppliedServices empl_ps INNER JOIN empl_term.term term WHERE ps.service = :service AND ps = empl_ps AND term.openingTime <= :start_time AND term.closingTime >= :end_time"),
        @NamedQuery(name = WorkStation.FIND_BY_SERVICE_AND_SERVICE_POINT, query = "SELECT ws FROM WorkStation ws INNER JOIN ws.providedServices ps WHERE ws.servicePoint = :service_point AND ps.service = :service"),
        @NamedQuery(name = WorkStation.FIND_BY_SERVICE_AND_SERVICE_POINT_AND_TERM, query = "SELECT DISTINCT ws FROM WorkStation ws INNER JOIN ws.providedServices ps INNER JOIN ws.termsEmployeesWorkOn empl_term INNER JOIN empl_term.employee e " +
                "INNER JOIN e.suppliedServices empl_ps INNER JOIN empl_term.term term WHERE ws.servicePoint = :service_point AND ps.service = :service AND ps = empl_ps AND term.openingTime < :end_time AND term.closingTime > :start_time"),  // constraint: openingTime < closingTime
        @NamedQuery(name = WorkStation.FIND_BY_SERVICE_AND_SERVICE_POINT_AND_TERM_STRICT, query = "SELECT DISTINCT ws FROM WorkStation ws INNER JOIN ws.providedServices ps INNER JOIN ws.termsEmployeesWorkOn empl_term INNER JOIN empl_term.employee e " +
                "INNER JOIN e.suppliedServices empl_ps INNER JOIN empl_term.term term WHERE ws.servicePoint = :service_point AND ps.service = :service AND ps = empl_ps AND term.openingTime <= :start_time AND term.closingTime >= :end_time"),
        @NamedQuery(name = WorkStation.FIND_BY_PROVIDER_SERVICE, query = "SELECT ws FROM WorkStation ws WHERE :provider_service MEMBER OF ws.providedServices"),
        @NamedQuery(name = WorkStation.FIND_BY_PROVIDER_SERVICE_EAGERLY, query = "SELECT DISTINCT ws FROM WorkStation ws INNER JOIN FETCH ws.providedServices ps LEFT JOIN FETCH ws.termsEmployeesWorkOn WHERE ps = :provider_service"),
        @NamedQuery(name = WorkStation.FIND_BY_PROVIDER_SERVICE_AND_TERM, query = "SELECT DISTINCT ws FROM WorkStation ws INNER JOIN ws.providedServices ps INNER JOIN ws.termsEmployeesWorkOn empl_term INNER JOIN empl_term.employee e " +
                "INNER JOIN e.suppliedServices empl_ps INNER JOIN empl_term.term term WHERE ps = empl_ps AND ps = :provider_service AND term.openingTime < :end_time AND term.closingTime > :start_time"), // constraint: openingTime < closingTime
        @NamedQuery(name = WorkStation.FIND_BY_PROVIDER_SERVICE_AND_TERM_STRICT, query = "SELECT DISTINCT ws FROM WorkStation ws INNER JOIN ws.providedServices ps INNER JOIN ws.termsEmployeesWorkOn empl_term INNER JOIN empl_term.employee e " +
                "INNER JOIN e.suppliedServices empl_ps INNER JOIN empl_term.term term WHERE ps = empl_ps AND ps = :provider_service AND term.openingTime <= :start_time AND term.closingTime >= :end_time"),
        @NamedQuery(name = WorkStation.FIND_BY_PROVIDER_SERVICE_AND_SERVICE_POINT, query = "SELECT ws FROM WorkStation ws WHERE ws.servicePoint = :service_point AND :provider_service MEMBER OF ws.providedServices"),
        @NamedQuery(name = WorkStation.FIND_BY_PROVIDER_SERVICE_AND_SERVICE_POINT_AND_TERM, query = "SELECT DISTINCT ws FROM WorkStation ws INNER JOIN ws.providedServices ps INNER JOIN ws.termsEmployeesWorkOn empl_term INNER JOIN empl_term.employee e " +
                "INNER JOIN e.suppliedServices empl_ps INNER JOIN empl_term.term term WHERE ps = empl_ps AND ps = :provider_service AND ws.servicePoint = :service_point AND term.openingTime < :end_time AND term.closingTime > :start_time"), // constraint: openingTime < closingTime
        @NamedQuery(name = WorkStation.FIND_BY_PROVIDER_SERVICE_AND_SERVICE_POINT_AND_TERM_STRICT, query = "SELECT DISTINCT ws FROM WorkStation ws INNER JOIN ws.providedServices ps INNER JOIN ws.termsEmployeesWorkOn empl_term INNER JOIN empl_term.employee e " +
                "INNER JOIN e.suppliedServices empl_ps INNER JOIN empl_term.term term WHERE ps = empl_ps AND ps = :provider_service AND ws.servicePoint = :service_point AND term.openingTime <= :start_time AND term.closingTime >= :end_time"),
        @NamedQuery(name = WorkStation.FIND_BY_EMPLOYEE, query = "SELECT DISTINCT ws FROM WorkStation ws INNER JOIN ws.termsEmployeesWorkOn empl_term WHERE empl_term.employee = :employee"),
        @NamedQuery(name = WorkStation.FIND_BY_EMPLOYEE_EAGERLY, query = "SELECT DISTINCT ws FROM WorkStation ws LEFT JOIN FETCH ws.providedServices INNER JOIN FETCH ws.termsEmployeesWorkOn empl_term WHERE empl_term.employee = :employee"),
        @NamedQuery(name = WorkStation.FIND_BY_EMPLOYEE_AND_TERM, query = "SELECT DISTINCT ws FROM WorkStation ws INNER JOIN ws.termsEmployeesWorkOn empl_term INNER JOIN empl_term.term term WHERE empl_term.employee = :employee AND term.openingTime < :end_time AND term.closingTime > :start_time"), // constraint: openingTime < closingTime
        @NamedQuery(name = WorkStation.FIND_BY_EMPLOYEE_AND_TERM_STRICT, query = "SELECT DISTINCT ws FROM WorkStation ws INNER JOIN ws.termsEmployeesWorkOn empl_term INNER JOIN empl_term.term term WHERE empl_term.employee = :employee AND term.openingTime <= :start_time AND term.closingTime >= :end_time"),
        @NamedQuery(name = WorkStation.FIND_BY_EMPLOYEE_AND_SERVICE_POINT, query = "SELECT DISTINCT ws FROM WorkStation ws INNER JOIN ws.termsEmployeesWorkOn empl_term WHERE empl_term.employee = :employee AND ws.servicePoint = :service_point"),
        @NamedQuery(name = WorkStation.FIND_BY_EMPLOYEE_AND_SERVICE_POINT_AND_TERM, query = "SELECT DISTINCT ws FROM WorkStation ws INNER JOIN ws.termsEmployeesWorkOn empl_term INNER JOIN empl_term.term term WHERE empl_term.employee = :employee AND ws.servicePoint = :service_point AND term.openingTime < :end_time AND term.closingTime > :start_time"), // constraint: openingTime < closingTime
        @NamedQuery(name = WorkStation.FIND_BY_EMPLOYEE_AND_SERVICE_POINT_AND_TERM_STRICT, query = "SELECT DISTINCT ws FROM WorkStation ws INNER JOIN ws.termsEmployeesWorkOn empl_term INNER JOIN empl_term.term term WHERE empl_term.employee = :employee AND ws.servicePoint = :service_point AND term.openingTime <= :start_time AND term.closingTime >= :end_time"),
        @NamedQuery(name = WorkStation.FIND_BY_EMPLOYEE_AND_SERVICE, query = "SELECT DISTINCT ws FROM WorkStation ws INNER JOIN ws.providedServices ps INNER JOIN ws.termsEmployeesWorkOn empl_term INNER JOIN empl_term.employee e " +
                "INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND e = :employee AND ps.service = :service"),
        @NamedQuery(name = WorkStation.FIND_BY_EMPLOYEE_AND_SERVICE_AND_TERM, query = "SELECT DISTINCT ws FROM WorkStation ws INNER JOIN ws.providedServices ps INNER JOIN ws.termsEmployeesWorkOn empl_term INNER JOIN empl_term.employee e " +
                "INNER JOIN e.suppliedServices empl_ps INNER JOIN empl_term.term term WHERE ps = empl_ps AND e = :employee AND ps.service = :service AND term.openingTime < :end_time AND term.closingTime > :start_time"), // constraint: openingTime < closingTime
        @NamedQuery(name = WorkStation.FIND_BY_EMPLOYEE_AND_SERVICE_AND_TERM_STRICT, query = "SELECT DISTINCT ws FROM WorkStation ws INNER JOIN ws.providedServices ps INNER JOIN ws.termsEmployeesWorkOn empl_term INNER JOIN empl_term.employee e " +
                "INNER JOIN e.suppliedServices empl_ps INNER JOIN empl_term.term term WHERE ps = empl_ps AND e = :employee AND ps.service = :service AND term.openingTime <= :start_time AND term.closingTime >= :end_time"),
        @NamedQuery(name = WorkStation.DELETE_BY_SERVICE_POINT, query = "DELETE FROM WorkStation ws WHERE ws.servicePoint = :service_point"),
        @NamedQuery(name = WorkStation.DELETE_BY_ID, query = "DELETE FROM WorkStation ws WHERE ws.servicePoint = :servicePoint AND ws.workStationNumber = :workStationNumber"),
        @NamedQuery(name = WorkStation.COUNT_BY_SERVICE_POINT, query = "SELECT COUNT(ws) FROM WorkStation ws WHERE ws.servicePoint = :service_point"),
        @NamedQuery(name = WorkStation.COUNT_BY_SERVICE, query = "SELECT COUNT(ws) FROM WorkStation ws INNER JOIN ws.providedServices ps WHERE ps.service = :service"),
        @NamedQuery(name = WorkStation.COUNT_BY_PROVIDER_SERVICE, query = "SELECT COUNT(ws) FROM WorkStation ws WHERE :provider_service MEMBER OF ws.providedServices"),
        @NamedQuery(name = WorkStation.COUNT_BY_EMPLOYEE, query = "SELECT COUNT(DISTINCT ws) FROM WorkStation ws INNER JOIN ws.termsEmployeesWorkOn empl_term WHERE empl_term.employee = :employee"),
        @NamedQuery(name = WorkStation.COUNT_BY_TERM, query = "SELECT COUNT(DISTINCT ws) FROM WorkStation ws INNER JOIN ws.termsEmployeesWorkOn empl_term WHERE empl_term.term = :term"),
})
public class WorkStation implements Serializable{

    public static final String FIND_ALL_EAGERLY = "WorkStation.findAllEagerly";
    public static final String FIND_BY_ID_EAGERLY = "WorkStation.findByIdEagerly";
    public static final String FIND_BY_TYPE = "WorkStation.findByType";
    public static final String FIND_BY_PERIOD = "WorkStation.findByPeriod";
    public static final String FIND_BY_PERIOD_STRICT = "WorkStation.findByPeriodStrict";
    public static final String FIND_BY_TERM = "WorkStation.findByTerm";
    public static final String FIND_BY_TERM_EAGERLY = "WorkStation.findByTermEagerly";
    public static final String FIND_BY_SERVICE_POINT = "WorkStation.findByServicePoint";
    public static final String FIND_BY_SERVICE_POINT_EAGERLY = "WorkStation.findByServicePointEagerly";
    public static final String FIND_BY_SERVICE_POINT_AND_TYPE = "WorkStation.findByServicePointAndType";
    public static final String FIND_BY_SERVICE_POINT_AND_TERM = "WorkStation.findByServicePointAndTerm";
    public static final String FIND_BY_SERVICE_POINT_AND_TERM_STRICT = "WorkStation.findByServicePointAndTermStrict";
    public static final String FIND_BY_SERVICE = "WorkStation.findByService";
    public static final String FIND_BY_SERVICE_EAGERLY = "WorkStation.findByServiceEagerly";
    public static final String FIND_BY_SERVICE_AND_TERM = "WorkStation.findByServiceAndTerm";
    public static final String FIND_BY_SERVICE_AND_TERM_STRICT = "WorkStation.findByServiceAndTermStrict";
    public static final String FIND_BY_SERVICE_AND_SERVICE_POINT = "WorkStation.findByServiceAndServicePoint";
    public static final String FIND_BY_SERVICE_AND_SERVICE_POINT_AND_TERM = "WorkStation.findByServiceAndServicePointAndTerm";
    public static final String FIND_BY_SERVICE_AND_SERVICE_POINT_AND_TERM_STRICT = "WorkStation.findByServiceAndServicePointAndTermStrict";
    public static final String FIND_BY_PROVIDER_SERVICE = "WorkStation.findByProviderService";
    public static final String FIND_BY_PROVIDER_SERVICE_EAGERLY = "WorkStation.findByProviderServiceEagerly";
    public static final String FIND_BY_PROVIDER_SERVICE_AND_TERM = "WorkStation.findByProviderServiceAndTerm";
    public static final String FIND_BY_PROVIDER_SERVICE_AND_TERM_STRICT = "WorkStation.findByProviderServiceAndTermStrict";
    public static final String FIND_BY_PROVIDER_SERVICE_AND_SERVICE_POINT = "WorkStation.findByProviderServiceAndServicePoint";
    public static final String FIND_BY_PROVIDER_SERVICE_AND_SERVICE_POINT_AND_TERM = "WorkStation.findByProviderServiceAndServicePointAndTerm";
    public static final String FIND_BY_PROVIDER_SERVICE_AND_SERVICE_POINT_AND_TERM_STRICT = "WorkStation.findByProviderServiceAndServicePointAndTermStrict";
    public static final String FIND_BY_EMPLOYEE = "WorkStation.findByEmployee";
    public static final String FIND_BY_EMPLOYEE_EAGERLY = "WorkStation.findByEmployeeEagerly";
    public static final String FIND_BY_EMPLOYEE_AND_TERM = "WorkStation.findByEmployeeAndTerm";
    public static final String FIND_BY_EMPLOYEE_AND_TERM_STRICT = "WorkStation.findByEmployeeAndTermStrict";
    public static final String FIND_BY_EMPLOYEE_AND_SERVICE_POINT = "WorkStation.findByEmployeeAndServicePoint";
    public static final String FIND_BY_EMPLOYEE_AND_SERVICE_POINT_AND_TERM = "WorkStation.findByEmployeeAndServicePointAndTerm";
    public static final String FIND_BY_EMPLOYEE_AND_SERVICE_POINT_AND_TERM_STRICT = "WorkStation.findByEmployeeAndServicePointAndTermStrict";
    public static final String FIND_BY_EMPLOYEE_AND_SERVICE = "WorkStation.findByEmployeeAndService";
    public static final String FIND_BY_EMPLOYEE_AND_SERVICE_AND_TERM = "WorkStation.findByEmployeeAndServiceAndTerm";
    public static final String FIND_BY_EMPLOYEE_AND_SERVICE_AND_TERM_STRICT = "WorkStation.findByEmployeeAndServiceAndTermStrict";
    public static final String DELETE_BY_SERVICE_POINT = "WorkStation.deleteByServicePoint";
    public static final String DELETE_BY_ID = "WorkStation.deleteById";
    public static final String COUNT_BY_SERVICE_POINT = "WorkStation.countByServicePoint";
    public static final String COUNT_BY_SERVICE = "WorkStation.countByService";
    public static final String COUNT_BY_PROVIDER_SERVICE = "WorkStation.countByProviderService";
    public static final String COUNT_BY_EMPLOYEE = "WorkStation.countByEmployee";
    public static final String COUNT_BY_TERM = "WorkStation.countByTerm";

    private Integer workStationNumber; // PK
    private ServicePoint servicePoint; // composite PK, composite FK

    private WorkStationType workStationType;

    /* one-to-many relationship */
    private Set<EmployeeTerm> termsEmployeesWorkOn = new HashSet<>();

    /* many-to-many relationship */
    private Set<ProviderService> providedServices = new HashSet<>();

    // HATEOAS support for RESTFul web service in JAX-RS
    private LinkedHashSet<Link> links = new LinkedHashSet<>();

    /* constructors */

    public  WorkStation() { }

    public WorkStation(ServicePoint servicePoint, Integer workStationNumber) {
        this.servicePoint = servicePoint;
        this.workStationNumber = workStationNumber;
    }

    public WorkStation(ServicePoint servicePoint, Integer workStationNumber, WorkStationType workStationType) {
        this.servicePoint = servicePoint;
        this.workStationNumber = workStationNumber;
        this.workStationType = workStationType;
    }

    /* PK getters and setters */

    @Id
    @Basic(optional = false)
    @Column(name = "work_station_no", nullable = false, columnDefinition = "INT UNSIGNED")
    public Integer getWorkStationNumber() {
        return workStationNumber;
    }

    public void setWorkStationNumber(Integer workStationNumber) {
        this.workStationNumber = workStationNumber;
    }

    @Id
    @NotNull
    @JoinColumns(value = {
            @JoinColumn(name = "provider_id", referencedColumnName = "provider_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"),
            @JoinColumn(name = "service_point_no", referencedColumnName = "service_point_no", nullable = false, columnDefinition = "INT UNSIGNED"),
    })
    @ManyToOne(fetch = FetchType.EAGER)
    public ServicePoint getServicePoint() {
        return servicePoint;
    }

    public void setServicePoint(ServicePoint servicePoint) {
        this.servicePoint = servicePoint;
    }

    /* other getters and setters */

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "work_station_type", nullable = false, columnDefinition = "ENUM('ROOM', 'CHAIR', 'OFFICE', 'GARAGE', 'OTHER') DEFAULT 'OTHER'")
    public WorkStationType getWorkStationType() {
        return workStationType;
    }

    public void setWorkStationType(WorkStationType workStationType) {
        this.workStationType = workStationType;
    }

    /* one-to-many relationship */
    @XmlTransient
    @OneToMany(mappedBy = "workStation", fetch = FetchType.LAZY)
    public Set<EmployeeTerm> getTermsEmployeesWorkOn() {
        return termsEmployeesWorkOn;
    }

    public void setTermsEmployeesWorkOn(Set<EmployeeTerm> termsEmployeesWorkOn) {
        this.termsEmployeesWorkOn = termsEmployeesWorkOn;
    }

    /* many-to-many relationship */
    @XmlTransient
    @ManyToMany(mappedBy = "workStations", fetch = FetchType.LAZY)
    public Set<ProviderService> getProvidedServices() {
        return providedServices;
    }

    public void setProvidedServices(Set<ProviderService> providedServices) {
        this.providedServices = providedServices;
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 31) // two randomly chosen prime numbers
                // if deriving: .appendSuper(super.hashCode()).
                .append(getServicePoint())
                .append(getWorkStationNumber())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof WorkStation))
            return false;
        if (obj == this)
            return true;

        WorkStation rhs = (WorkStation) obj;
        return new EqualsBuilder()
                // if deriving: .appendSuper(super.equals(obj)).
                .append(getServicePoint(), rhs.getServicePoint())
                .append(getWorkStationNumber(), rhs.getWorkStationNumber())
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
