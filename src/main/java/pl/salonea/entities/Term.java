package pl.salonea.entities;

import pl.salonea.constraints.ChronologicalDates;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "term",
        uniqueConstraints = @UniqueConstraint(columnNames = { "opening_time", "closing_time" } ))
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = Term.FIND_BY_PERIOD, query = "SELECT t FROM Term t WHERE t.openingTime < :end_time AND t.closingTime > :start_time"), // constraint: openingTime < closingTime
        @NamedQuery(name = Term.FIND_BY_PERIOD_STRICT, query = "SELECT t FROM Term t WHERE t.openingTime <= :start_time AND t.closingTime >= :end_time"),
        @NamedQuery(name = Term.FIND_AFTER, query = "SELECT t FROM Term t WHERE t.closingTime > :time"),
        @NamedQuery(name = Term.FIND_AFTER_STRICT, query = "SELECT t FROM Term t WHERE t.openingTime >= :time"),
        @NamedQuery(name = Term.FIND_BEFORE, query = "SELECT t FROM Term t WHERE t.openingTime < :time"),
        @NamedQuery(name = Term.FIND_BEFORE_STRICT, query = "SELECT t FROM Term t WHERE t.closingTime <= :time"),
        @NamedQuery(name = Term.FIND_BY_EMPLOYEE, query = "SELECT t FROM Term t INNER JOIN t.employeesWorkStation empl_term WHERE empl_term.employee = :employee"),
        @NamedQuery(name = Term.FIND_BY_WORK_STATION, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeesWorkStation empl_term WHERE empl_term.workStation = :work_station"),
        @NamedQuery(name = Term.FIND_BY_SERVICE, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeesWorkStation empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps.service = :service"),
        @NamedQuery(name = Term.FIND_BY_PROVIDER_SERVICE, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeesWorkStation empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps = :provider_service"),
        @NamedQuery(name = Term.FIND_BY_SERVICE_AND_EMPLOYEE, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeesWorkStation empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps.service = :service AND e = :employee"),
        @NamedQuery(name = Term.FIND_BY_PROVIDER_SERVICE_AND_EMPLOYEE, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeesWorkStation empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps = :provider_service AND e = :employee"),
        @NamedQuery(name = Term.FIND_BY_WORK_STATION_AND_EMPLOYEE, query = "SELECT t FROM Term t INNER JOIN t.employeesWorkStation empl_term WHERE empl_term.workStation = :work_station AND empl_term.employee = :employee"),
        @NamedQuery(name = Term.FIND_BY_WORK_STATION_AND_SERVICE, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeesWorkStation empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps.service = :service AND ws = :work_station"),
        @NamedQuery(name = Term.FIND_BY_WORK_STATION_AND_PROVIDER_SERVICE, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeesWorkStation empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps = :provider_service AND ws = :work_station"),
        @NamedQuery(name = Term.FIND_BY_WORK_STATION_AND_SERVICE_AND_EMPLOYEE, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeesWorkStation empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps.service = :service AND ws = :work_station AND e = :employee"),
        @NamedQuery(name = Term.FIND_BY_WORK_STATION_AND_PROVIDER_SERVICE_AND_EMPLOYEE, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeesWorkStation empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps = :provider_service AND ws = :work_station AND e = :employee"),
        @NamedQuery(name = Term.FIND_BY_SERVICE_POINT, query = "SELECT t FROM Term t INNER JOIN t.employeesWorkStation empl_term INNER JOIN empl_term.workStation ws WHERE ws.servicePoint = :service_point"),
        @NamedQuery(name = Term.FIND_BY_SERVICE_POINT_AND_EMPLOYEE, query = "SELECT t FROM Term t INNER JOIN t.employeesWorkStation empl_term INNER JOIN empl_term.workStation ws WHERE ws.servicePoint = :service_point AND empl_term.employee = :employee"),
        @NamedQuery(name = Term.FIND_BY_SERVICE_POINT_AND_SERVICE, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeesWorkStation empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps.service = :service AND ws.servicePoint = :service_point"),
        @NamedQuery(name = Term.FIND_BY_SERVICE_POINT_AND_PROVIDER_SERVICE, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeesWorkStation empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps = :provider_service AND ws.servicePoint = :service_point"),
        @NamedQuery(name = Term.FIND_BY_SERVICE_POINT_AND_SERVICE_AND_EMPLOYEE, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeesWorkStation empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps.service = :service AND ws.servicePoint = :service_point AND e = :employee"),
        @NamedQuery(name = Term.FIND_BY_SERVICE_POINT_AND_PROVIDER_SERVICE_AND_EMPLOYEE, query = "SELECT DISTINCT t FROM Term t INNER JOIN t.employeesWorkStation empl_term INNER JOIN empl_term.workStation ws INNER JOIN ws.providedServices ps " +
                "INNER JOIN empl_term.employee e INNER JOIN e.suppliedServices empl_ps WHERE ps = empl_ps AND ps = :provider_service AND ws.servicePoint = :service_point AND e = :employee"),
        @NamedQuery(name = Term.DELETE_OLDER_THAN, query = "DELETE FROM Term t WHERE t.closingTime <= :time"),
})
@ChronologicalDates(dateAttributes = { "openingTime", "closingTime"}, order = ChronologicalDates.Order.ASCENDING)
public class Term  implements Serializable {

    public static final String FIND_BY_PERIOD = "Term.findByPeriod";
    public static final String FIND_BY_PERIOD_STRICT = "Term.findByPeriodStrict";
    public static final String FIND_AFTER = "Term.findAfter";
    public static final String FIND_AFTER_STRICT = "Term.findAfterStrict";
    public static final String FIND_BEFORE = "Term.findBefore";
    public static final String FIND_BEFORE_STRICT = "Term.findBeforeStrict";
    public static final String FIND_BY_EMPLOYEE = "Term.findByEmployee";
    public static final String FIND_BY_WORK_STATION = "Term.findByWorkStation";
    public static final String FIND_BY_SERVICE = "Term.findByService";
    public static final String FIND_BY_PROVIDER_SERVICE = "Term.findByProviderService";
    public static final String FIND_BY_SERVICE_AND_EMPLOYEE = "Term.findByServiceAndEmployee";
    public static final String FIND_BY_PROVIDER_SERVICE_AND_EMPLOYEE = "Term.findByProviderServiceAndEmployee";
    public static final String FIND_BY_WORK_STATION_AND_EMPLOYEE = "Term.findByWorkStationAndEmployee";
    public static final String FIND_BY_WORK_STATION_AND_SERVICE = "Term.findByWorkStationAndService";
    public static final String FIND_BY_WORK_STATION_AND_PROVIDER_SERVICE = "Term.findByWorkStationAndProviderService";
    public static final String FIND_BY_WORK_STATION_AND_SERVICE_AND_EMPLOYEE = "Term.findByWorkStationAndServiceAndEmployee";
    public static final String FIND_BY_WORK_STATION_AND_PROVIDER_SERVICE_AND_EMPLOYEE = "Term.findByWorkStationAndProviderServiceAndEmployee";
    public static final String FIND_BY_SERVICE_POINT = "Term.findByServicePoint";
    public static final String FIND_BY_SERVICE_POINT_AND_EMPLOYEE = "Term.findByServicePointAndEmployee";
    public static final String FIND_BY_SERVICE_POINT_AND_SERVICE = "Term.findByServicePointAndService";
    public static final String FIND_BY_SERVICE_POINT_AND_PROVIDER_SERVICE = "Term.findByServicePointAndProviderService";
    public static final String FIND_BY_SERVICE_POINT_AND_SERVICE_AND_EMPLOYEE = "Term.findByServicePointAndServiceAndEmployee";
    public static final String FIND_BY_SERVICE_POINT_AND_PROVIDER_SERVICE_AND_EMPLOYEE = "Term.findByServicePointAndProviderServiceAndEmployee";
    public static final String DELETE_OLDER_THAN = "Term.deleteOlderThan";

    private Long termId;
    private Date openingTime;
    private Date closingTime;

    /* one-to-many relationships */
    private Set<TermEmployeeWorkOn> employeesWorkStation;

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
    @Column(name = "term_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
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

    @OneToMany(mappedBy = "term", fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE })
    public Set<TermEmployeeWorkOn> getEmployeesWorkStation() {
        return employeesWorkStation;
    }

    public void setEmployeesWorkStation(Set<TermEmployeeWorkOn> employeesWorkStation) {
        this.employeesWorkStation = employeesWorkStation;
    }

}
