package pl.salonea.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pl.salonea.constraints.ChronologicalDates;
import pl.salonea.jaxrs.utils.hateoas.Link;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@XmlRootElement(name = "term")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"termId", "openingTime", "closingTime", "links"})

@Entity
@Table(name = "term",
        uniqueConstraints = @UniqueConstraint(columnNames = { "opening_time", "closing_time" } )) // business key
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = Term.FIND_ALL_EAGERLY, query = "SELECT DISTINCT t FROM Term t LEFT JOIN FETCH t.employeeTerms LEFT JOIN FETCH t.transactions LEFT JOIN FETCH t.historicalTransactions"),
        @NamedQuery(name = Term.FIND_BY_ID_EAGERLY, query = "SELECT t FROM Term t LEFT JOIN FETCH t.employeeTerms LEFT JOIN FETCH t.transactions LEFT JOIN FETCH t.historicalTransactions WHERE t.termId = :termId"),
        @NamedQuery(name = Term.FIND_BY_PERIOD, query = "SELECT t FROM Term t WHERE t.openingTime < :end_time AND t.closingTime > :start_time"), // constraint: openingTime < closingTime
        @NamedQuery(name = Term.FIND_BY_PERIOD_STRICT, query = "SELECT t FROM Term t WHERE t.openingTime <= :start_time AND t.closingTime >= :end_time"),
        @NamedQuery(name = Term.FIND_AFTER, query = "SELECT t FROM Term t WHERE t.closingTime > :time"),
        @NamedQuery(name = Term.FIND_AFTER_STRICT, query = "SELECT t FROM Term t WHERE t.openingTime >= :time"),
        @NamedQuery(name = Term.FIND_BEFORE, query = "SELECT t FROM Term t WHERE t.openingTime < :time"),
        @NamedQuery(name = Term.FIND_BEFORE_STRICT, query = "SELECT t FROM Term t WHERE t.closingTime <= :time"),
        @NamedQuery(name = Term.FIND_BY_EMPLOYEE, query = "SELECT t FROM Term t INNER JOIN t.employeeTerms empl_term WHERE empl_term.employee = :employee"),
        @NamedQuery(name = Term.FIND_BY_EMPLOYEE_EAGERLY, query = "SELECT DISTINCT t FROM Term t INNER JOIN FETCH t.employeeTerms empl_term LEFT JOIN FETCH t.transactions LEFT JOIN FETCH t.historicalTransactions " +
                "WHERE empl_term.employee = :employee"),
        @NamedQuery(name = Term.FIND_BY_WORK_STATION, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeeTerms empl_term WHERE empl_term.workStation = :work_station"),
        @NamedQuery(name = Term.FIND_BY_WORK_STATION_EAGERLY, query = "SELECT DISTINCT t FROM Term t INNER JOIN FETCH t.employeeTerms empl_term LEFT JOIN FETCH t.transactions LEFT JOIN FETCH t.historicalTransactions " +
                "WHERE empl_term.workStation = :work_station"),
        @NamedQuery(name = Term.FIND_BY_SERVICE, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeeTerms empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps.service = :service"),
        @NamedQuery(name = Term.FIND_BY_SERVICE_EAGERLY, query = "SELECT DISTINCT t FROM Term t INNER JOIN FETCH t.employeeTerms empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps LEFT JOIN FETCH t.transactions LEFT JOIN FETCH t.historicalTransactions WHERE ps = empl_ps AND ps.service = :service"),
        @NamedQuery(name = Term.FIND_BY_PROVIDER_SERVICE, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeeTerms empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps = :provider_service"),
        @NamedQuery(name = Term.FIND_BY_PROVIDER_SERVICE_EAGERLY, query = "SELECT DISTINCT t FROM Term t INNER JOIN FETCH t.employeeTerms empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps LEFT JOIN FETCH t.transactions LEFT JOIN FETCH t.historicalTransactions WHERE ps = empl_ps AND ps = :provider_service"),
        @NamedQuery(name = Term.FIND_BY_SERVICE_AND_EMPLOYEE, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeeTerms empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps.service = :service AND e = :employee"),
        @NamedQuery(name = Term.FIND_BY_PROVIDER_SERVICE_AND_EMPLOYEE, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeeTerms empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps = :provider_service AND e = :employee"),
        @NamedQuery(name = Term.FIND_BY_WORK_STATION_AND_EMPLOYEE, query = "SELECT t FROM Term t INNER JOIN t.employeeTerms empl_term WHERE empl_term.workStation = :work_station AND empl_term.employee = :employee"),
        @NamedQuery(name = Term.FIND_BY_WORK_STATION_AND_SERVICE, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeeTerms empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps.service = :service AND ws = :work_station"),
        @NamedQuery(name = Term.FIND_BY_WORK_STATION_AND_PROVIDER_SERVICE, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeeTerms empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps = :provider_service AND ws = :work_station"),
        @NamedQuery(name = Term.FIND_BY_WORK_STATION_AND_SERVICE_AND_EMPLOYEE, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeeTerms empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps.service = :service AND ws = :work_station AND e = :employee"),
        @NamedQuery(name = Term.FIND_BY_WORK_STATION_AND_PROVIDER_SERVICE_AND_EMPLOYEE, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeeTerms empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps = :provider_service AND ws = :work_station AND e = :employee"),
        @NamedQuery(name = Term.FIND_BY_SERVICE_POINT, query = "SELECT t FROM Term t INNER JOIN t.employeeTerms empl_term INNER JOIN empl_term.workStation ws WHERE ws.servicePoint = :service_point"),
        @NamedQuery(name = Term.FIND_BY_SERVICE_POINT_EAGERLY, query = "SELECT DISTINCT t FROM Term t INNER JOIN FETCH t.employeeTerms empl_term INNER JOIN empl_term.workStation ws LEFT JOIN FETCH t.transactions LEFT JOIN FETCH t.historicalTransactions WHERE ws.servicePoint = :service_point"),
        @NamedQuery(name = Term.FIND_BY_SERVICE_POINT_AND_EMPLOYEE, query = "SELECT t FROM Term t INNER JOIN t.employeeTerms empl_term INNER JOIN empl_term.workStation ws WHERE ws.servicePoint = :service_point AND empl_term.employee = :employee"),
        @NamedQuery(name = Term.FIND_BY_SERVICE_POINT_AND_SERVICE, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeeTerms empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps.service = :service AND ws.servicePoint = :service_point"),
        @NamedQuery(name = Term.FIND_BY_SERVICE_POINT_AND_PROVIDER_SERVICE, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeeTerms empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps = :provider_service AND ws.servicePoint = :service_point"),
        @NamedQuery(name = Term.FIND_BY_SERVICE_POINT_AND_SERVICE_AND_EMPLOYEE, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeeTerms empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps.service = :service AND ws.servicePoint = :service_point AND e = :employee"),
        @NamedQuery(name = Term.FIND_BY_SERVICE_POINT_AND_PROVIDER_SERVICE_AND_EMPLOYEE, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeeTerms empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps = :provider_service AND ws.servicePoint = :service_point AND e = :employee"),
        @NamedQuery(name = Term.COUNT_BY_EMPLOYEE, query = "SELECT COUNT(t) FROM Term t INNER JOIN t.employeeTerms empl_term WHERE empl_term.employee = :employee"),
        @NamedQuery(name = Term.COUNT_BY_WORK_STATION, query = "SELECT COUNT(DISTINCT t) FROM Term t INNER JOIN t.employeeTerms empl_term WHERE empl_term.workStation = :work_station"),
        @NamedQuery(name = Term.COUNT_BY_SERVICE, query = "SELECT COUNT(DISTINCT t) FROM Term t INNER JOIN t.employeeTerms empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps.service = :service"),
        @NamedQuery(name = Term.COUNT_BY_PROVIDER_SERVICE, query = "SELECT COUNT(DISTINCT t) FROM Term t INNER JOIN t.employeeTerms empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps = :provider_service"),
        @NamedQuery(name = Term.COUNT_BY_SERVICE_POINT, query = "SELECT COUNT(t) FROM Term t INNER JOIN t.employeeTerms empl_term INNER JOIN empl_term.workStation ws WHERE ws.servicePoint = :service_point"),
        @NamedQuery(name = Term.DELETE_OLDER_THAN, query = "DELETE FROM Term t WHERE t.closingTime <= :time"),
})
@ChronologicalDates(dateAttributes = { "openingTime", "closingTime"}, order = ChronologicalDates.Order.ASCENDING)
public class Term  implements Serializable {

    public static final String FIND_ALL_EAGERLY = "Term.findAllEagerly";
    public static final String FIND_BY_ID_EAGERLY = "Term.findByIdEagerly";
    public static final String FIND_BY_PERIOD = "Term.findByPeriod";
    public static final String FIND_BY_PERIOD_STRICT = "Term.findByPeriodStrict";
    public static final String FIND_AFTER = "Term.findAfter";
    public static final String FIND_AFTER_STRICT = "Term.findAfterStrict";
    public static final String FIND_BEFORE = "Term.findBefore";
    public static final String FIND_BEFORE_STRICT = "Term.findBeforeStrict";
    public static final String FIND_BY_EMPLOYEE = "Term.findByEmployee";
    public static final String FIND_BY_EMPLOYEE_EAGERLY = "Term.findByEmployeeEagerly";
    public static final String FIND_BY_WORK_STATION = "Term.findByWorkStation";
    public static final String FIND_BY_WORK_STATION_EAGERLY = "Term.findByWorkStationEagerly";
    public static final String FIND_BY_SERVICE = "Term.findByService";
    public static final String FIND_BY_SERVICE_EAGERLY = "Term.findByServiceEagerly";
    public static final String FIND_BY_PROVIDER_SERVICE = "Term.findByProviderService";
    public static final String FIND_BY_PROVIDER_SERVICE_EAGERLY = "Term.findByProviderServiceEagerly";
    public static final String FIND_BY_SERVICE_AND_EMPLOYEE = "Term.findByServiceAndEmployee";
    public static final String FIND_BY_PROVIDER_SERVICE_AND_EMPLOYEE = "Term.findByProviderServiceAndEmployee";
    public static final String FIND_BY_WORK_STATION_AND_EMPLOYEE = "Term.findByWorkStationAndEmployee";
    public static final String FIND_BY_WORK_STATION_AND_SERVICE = "Term.findByWorkStationAndService";
    public static final String FIND_BY_WORK_STATION_AND_PROVIDER_SERVICE = "Term.findByWorkStationAndProviderService";
    public static final String FIND_BY_WORK_STATION_AND_SERVICE_AND_EMPLOYEE = "Term.findByWorkStationAndServiceAndEmployee";
    public static final String FIND_BY_WORK_STATION_AND_PROVIDER_SERVICE_AND_EMPLOYEE = "Term.findByWorkStationAndProviderServiceAndEmployee";
    public static final String FIND_BY_SERVICE_POINT = "Term.findByServicePoint";
    public static final String FIND_BY_SERVICE_POINT_EAGERLY = "Term.findByServicePointEagerly";
    public static final String FIND_BY_SERVICE_POINT_AND_EMPLOYEE = "Term.findByServicePointAndEmployee";
    public static final String FIND_BY_SERVICE_POINT_AND_SERVICE = "Term.findByServicePointAndService";
    public static final String FIND_BY_SERVICE_POINT_AND_PROVIDER_SERVICE = "Term.findByServicePointAndProviderService";
    public static final String FIND_BY_SERVICE_POINT_AND_SERVICE_AND_EMPLOYEE = "Term.findByServicePointAndServiceAndEmployee";
    public static final String FIND_BY_SERVICE_POINT_AND_PROVIDER_SERVICE_AND_EMPLOYEE = "Term.findByServicePointAndProviderServiceAndEmployee";
    public static final String COUNT_BY_EMPLOYEE = "Term.countByEmployee";
    public static final String COUNT_BY_WORK_STATION = "Term.countByWorkStation";
    public static final String COUNT_BY_SERVICE = "Term.countByService";
    public static final String COUNT_BY_PROVIDER_SERVICE = "Term.countByProviderService";
    public static final String COUNT_BY_SERVICE_POINT = "Term.countByServicePoint";
    public static final String DELETE_OLDER_THAN = "Term.deleteOlderThan";

    private Long termId;
    private Date openingTime; // business key
    private Date closingTime; // business key

    /* one-to-many relationships */
    private Set<EmployeeTerm> employeeTerms;
    private Set<Transaction> transactions;
    private Set<HistoricalTransaction> historicalTransactions;

    // HATEOAS support for RESTFul web service in JAX-RS
    private LinkedHashSet<Link> links = new LinkedHashSet<>();

    /* constructors */

    public Term() {
    }

    public Term(Date openingTime, Date closingTime) {
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    /* getters and setters */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)  // runtime scoped not null, whereas nullable references only to table
    @Column(name = "term_id", nullable = false /*, columnDefinition = "BIGINT UNSIGNED" */)
    public Long getTermId() {
        return termId;
    }

    public void setTermId(Long termId) {
        this.termId = termId;
    }

    @Future
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "opening_time", nullable = false, columnDefinition = "DATETIME")
    public Date getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(Date openingTime) {
        this.openingTime = openingTime;
    }

    @Future
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "closing_time", nullable = false, columnDefinition = "DATETIME")
    public Date getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(Date closingTime) {
        this.closingTime = closingTime;
    }

    /* one-to-many relationships */

    @XmlTransient
    @OneToMany(mappedBy = "term", fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE })
    public Set<EmployeeTerm> getEmployeeTerms() {
        return employeeTerms;
    }

    public void setEmployeeTerms(Set<EmployeeTerm> employeeTerms) {
        this.employeeTerms = employeeTerms;
    }

    @XmlTransient
    @OneToMany(mappedBy = "term", fetch = FetchType.LAZY)
    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    @XmlTransient
    @OneToMany(mappedBy = "term", fetch = FetchType.LAZY)
    public Set<HistoricalTransaction> getHistoricalTransactions() {
        return historicalTransactions;
    }

    public void setHistoricalTransactions(Set<HistoricalTransaction> historicalTransactions) {
        this.historicalTransactions = historicalTransactions;
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

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(getOpeningTime());
        calendar.set(Calendar.MILLISECOND, 0);
        Long openingTime = calendar.getTimeInMillis();

        calendar.setTime(getClosingTime());
        calendar.set(Calendar.MILLISECOND, 0);
        Long closingTime = calendar.getTimeInMillis();

        return new HashCodeBuilder(17, 31) // two randomly chosen prime numbers
                // if deriving: .appendSuper(super.hashCode())
                .append(openingTime)
                .append(closingTime)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Term))
            return false;
        if (obj == this)
            return true;

        Term rhs = (Term) obj;

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(getOpeningTime());
        calendar.set(Calendar.MILLISECOND, 0);
        Long openingTime = calendar.getTimeInMillis();

        calendar.setTime(rhs.getOpeningTime());
        calendar.set(Calendar.MILLISECOND, 0);
        Long rhs_openingTime = calendar.getTimeInMillis();

        calendar.setTime(getClosingTime());
        calendar.set(Calendar.MILLISECOND, 0);
        Long closingTime = calendar.getTimeInMillis();

        calendar.setTime(rhs.getClosingTime());
        calendar.set(Calendar.MILLISECOND, 0);
        Long rhs_closingTime = calendar.getTimeInMillis();

        return new EqualsBuilder()
                // if deriving: .appendSuper(super.equals(obj))
                .append(openingTime, rhs_openingTime)
                .append(closingTime, rhs_closingTime)
                .isEquals();
    }

}