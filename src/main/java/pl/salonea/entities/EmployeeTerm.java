package pl.salonea.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pl.salonea.entities.idclass.EmployeeTermId;
import pl.salonea.jaxrs.utils.hateoas.Link;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.LinkedHashSet;

/**
 * This entity is intermediary for ternary relationship
 * between Term, Employee, WorkStation. It defines
 * associations that tells which work station employee
 * in given term works on.
 */
@XmlRootElement(name = "employee-term")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"term", "employee", "workStation"})

@Entity
@Table(name = "work_on")
@Access(AccessType.PROPERTY)
@IdClass(EmployeeTermId.class)
@NamedQueries({
        @NamedQuery(name = EmployeeTerm.FIND_BY_PERIOD, query = "SELECT empl_term FROM EmployeeTerm empl_term INNER JOIN empl_term.term t WHERE t.openingTime < :end_time AND t.closingTime > :start_time"), // constraint: openingTime < closingTime
        @NamedQuery(name = EmployeeTerm.FIND_BY_PERIOD_STRICT, query = "SELECT empl_term FROM EmployeeTerm empl_term INNER JOIN empl_term.term t WHERE t.openingTime <= :start_time AND t.closingTime >= :end_time"),
        @NamedQuery(name = EmployeeTerm.FIND_AFTER, query = "SELECT empl_term FROM EmployeeTerm empl_term INNER JOIN empl_term.term t WHERE t.closingTime > :time"),
        @NamedQuery(name = EmployeeTerm.FIND_AFTER_STRICT, query = "SELECT empl_term FROM EmployeeTerm empl_term INNER JOIN empl_term.term t WHERE t.openingTime >= :time"),
        @NamedQuery(name = EmployeeTerm.FIND_BEFORE, query = "SELECT empl_term FROM EmployeeTerm empl_term INNER JOIN empl_term.term t WHERE t.openingTime < :time"),
        @NamedQuery(name = EmployeeTerm.FIND_BEFORE_STRICT, query = "SELECT empl_term FROM EmployeeTerm empl_term INNER JOIN empl_term.term t WHERE t.closingTime <= :time"),
        @NamedQuery(name = EmployeeTerm.FIND_BY_EMPLOYEE, query = "SELECT empl_term FROM EmployeeTerm empl_term WHERE empl_term.employee = :employee"),
        @NamedQuery(name = EmployeeTerm.FIND_BY_TERM, query = "SELECT empl_term FROM EmployeeTerm empl_term WHERE empl_term.term = :term"),
        @NamedQuery(name = EmployeeTerm.FIND_BY_WORK_STATION, query = "SELECT empl_term FROM EmployeeTerm empl_term WHERE empl_term.workStation = :work_station"),
        @NamedQuery(name = EmployeeTerm.FIND_BY_SERVICE, query = "SELECT empl_term FROM EmployeeTerm empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps.service = :service"),
        @NamedQuery(name = EmployeeTerm.FIND_BY_PROVIDER_SERVICE, query = "SELECT empl_term FROM EmployeeTerm empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps = :provider_service"),
        @NamedQuery(name = EmployeeTerm.FIND_BY_SERVICE_POINT, query = "SELECT empl_term FROM EmployeeTerm empl_term INNER JOIN empl_term.workStation ws WHERE ws.servicePoint = :service_point"),
        @NamedQuery(name = EmployeeTerm.COUNT_BY_EMPLOYEE, query = "SELECT COUNT(empl_term) FROM EmployeeTerm empl_term WHERE empl_term.employee = :employee"),
        @NamedQuery(name = EmployeeTerm.COUNT_BY_TERM, query = "SELECT COUNT(empl_term) FROM EmployeeTerm empl_term WHERE empl_term.term = :term"),
        @NamedQuery(name = EmployeeTerm.COUNT_BY_WORK_STATION, query = "SELECT COUNT(empl_term) FROM EmployeeTerm empl_term WHERE empl_term.workStation = :work_station"),
        @NamedQuery(name = EmployeeTerm.COUNT_BY_SERVICE, query = "SELECT COUNT(empl_term) FROM EmployeeTerm empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps.service = :service"),
        @NamedQuery(name = EmployeeTerm.COUNT_BY_PROVIDER_SERVICE, query = "SELECT COUNT(empl_term) FROM EmployeeTerm empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps = :provider_service"),
        @NamedQuery(name = EmployeeTerm.COUNT_BY_SERVICE_POINT, query = "SELECT COUNT(empl_term) FROM EmployeeTerm empl_term INNER JOIN empl_term.workStation ws WHERE ws.servicePoint = :service_point"),
        @NamedQuery(name = EmployeeTerm.DELETE_BY_ID, query = "DELETE FROM EmployeeTerm empl_term WHERE empl_term.employee.userId = :employeeId AND empl_term.term.termId = :termId"),
        @NamedQuery(name = EmployeeTerm.DELETE_FOR_EMPLOYEES, query = "DELETE FROM EmployeeTerm empl_term WHERE empl_term.employee IN :employees"),
        @NamedQuery(name = EmployeeTerm.DELETE_FOR_WORK_STATIONS, query = "DELETE FROM EmployeeTerm empl_term WHERE empl_term.workStation IN :work_stations"),
        @NamedQuery(name = EmployeeTerm.DELETE_FOR_TERMS, query = "DELETE FROM EmployeeTerm empl_term WHERE empl_term.term IN :terms"),
        @NamedQuery(name = EmployeeTerm.DELETE_FOR_EMPLOYEES_AND_WORK_STATIONS, query = "DELETE FROM EmployeeTerm empl_term WHERE empl_term.employee IN :employees AND empl_term.workStation IN :work_stations"),
        @NamedQuery(name = EmployeeTerm.DELETE_FOR_EMPLOYEES_AND_TERMS, query = "DELETE FROM EmployeeTerm empl_term WHERE empl_term.employee IN :employees AND empl_term.term IN :terms"),
        @NamedQuery(name = EmployeeTerm.DELETE_FOR_WORK_STATIONS_AND_TERMS, query = "DELETE FROM EmployeeTerm empl_term WHERE empl_term.workStation IN :work_stations AND empl_term.term IN :terms")
})
public class EmployeeTerm implements Serializable {

    public static final String FIND_BY_PERIOD = "EmployeeTerm.findByPeriod";
    public static final String FIND_BY_PERIOD_STRICT = "EmployeeTerm.findByPeriodStrict";
    public static final String FIND_AFTER = "EmployeeTerm.findAfter";
    public static final String FIND_AFTER_STRICT = "EmployeeTerm.findAfterStrict";
    public static final String FIND_BEFORE = "EmployeeTerm.findBefore";
    public static final String FIND_BEFORE_STRICT = "EmployeeTerm.findBeforeStrict";
    public static final String FIND_BY_EMPLOYEE = "EmployeeTerm.findByEmployee";
    public static final String FIND_BY_TERM = "EmployeeTerm.findByTerm";
    public static final String FIND_BY_WORK_STATION = "EmployeeTerm.findByWorkStation";
    public static final String FIND_BY_SERVICE = "EmployeeTerm.findByService";
    public static final String FIND_BY_PROVIDER_SERVICE = "EmployeeTerm.findByProviderService";
    public static final String FIND_BY_SERVICE_POINT = "EmployeeTerm.findByServicePoint";
    public static final String COUNT_BY_EMPLOYEE = "EmployeeTerm.countByEmployee";
    public static final String COUNT_BY_TERM = "EmployeeTerm.countByTerm";
    public static final String COUNT_BY_WORK_STATION = "EmployeeTerm.countByWorkStation";
    public static final String COUNT_BY_SERVICE = "EmployeeTerm.countByService";
    public static final String COUNT_BY_PROVIDER_SERVICE = "EmployeeTerm.countByProviderService";
    public static final String COUNT_BY_SERVICE_POINT = "EmployeeTerm.countByServicePoint";
    public static final String DELETE_BY_ID = "EmployeeTerm.deleteById";
    public static final String DELETE_FOR_EMPLOYEES = "EmployeeTerm.deleteForEmployees";
    public static final String DELETE_FOR_WORK_STATIONS = "EmployeeTerm.deleteForWorkStations";
    public static final String DELETE_FOR_TERMS = "EmployeeTerm.deleteForTerms";
    public static final String DELETE_FOR_EMPLOYEES_AND_WORK_STATIONS = "EmployeeTerm.deleteForEmployeesAndWorkStations";
    public static final String DELETE_FOR_EMPLOYEES_AND_TERMS = "EmployeeTerm.deleteForEmployeesAndTerms";
    public static final String DELETE_FOR_WORK_STATIONS_AND_TERMS = "EmployeeTerm.deleteForWorkStationsAndTerms";

    private Term term; // PK, FK
    private Employee employee; // PK, FK
    private WorkStation workStation; // FK

    // HATEOAS support for RESTFul web service in JAX-RS
    private LinkedHashSet<Link> links = new LinkedHashSet<>();

    /* constructors */

    public EmployeeTerm() { }

    public EmployeeTerm(Employee employee, Term term, WorkStation workStation) {
        this.employee = employee;
        this.term = term;
        this.workStation = workStation;
    }

    /* PK getters and setters */

    @Id
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "term_id", referencedColumnName = "term_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    @Id
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    /* other getters and setters */

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "provider_id", referencedColumnName = "provider_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"),
            @JoinColumn(name = "service_point_no", referencedColumnName = "service_point_no", nullable = false, columnDefinition = "INT UNSIGNED"),
            @JoinColumn(name = "work_station_no", referencedColumnName = "work_station_no", nullable = false, columnDefinition = "INT UNSIGNED")
    })
    public WorkStation getWorkStation() {
        return workStation;
    }

    public void setWorkStation(WorkStation workStation) {
        this.workStation = workStation;
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

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 31) // two randomly chosen prime numbers
                // if deriving: .appendSuper(super.hashCode())
                .append(getTerm())
                .append(getEmployee())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof EmployeeTerm))
            return false;
        if (obj == this)
            return true;

        EmployeeTerm rhs = (EmployeeTerm) obj;
        return new EqualsBuilder()
                // if deriving: appendSuper(super.equals(obj)).
                .append(getTerm(), rhs.getTerm())
                .append(getEmployee(),rhs.getEmployee())
                .isEquals();
    }
}
