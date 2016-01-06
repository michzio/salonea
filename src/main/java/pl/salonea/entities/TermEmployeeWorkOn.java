package pl.salonea.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pl.salonea.entities.idclass.TermEmployeeId;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

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
@IdClass(TermEmployeeId.class)
@NamedQueries({
        @NamedQuery(name = TermEmployeeWorkOn.DELETE_FOR_EMPLOYEES, query = "DELETE FROM TermEmployeeWorkOn empl_term WHERE empl_term.employee IN :employees"),
        @NamedQuery(name = TermEmployeeWorkOn.DELETE_FOR_WORK_STATIONS, query = "DELETE FROM TermEmployeeWorkOn empl_term WHERE empl_term.workStation IN :work_stations"),
        @NamedQuery(name = TermEmployeeWorkOn.DELETE_FOR_TERMS, query = "DELETE FROM TermEmployeeWorkOn empl_term WHERE empl_term.term IN :terms"),
        @NamedQuery(name = TermEmployeeWorkOn.DELETE_FOR_EMPLOYEES_AND_WORK_STATIONS, query = "DELETE FROM TermEmployeeWorkOn empl_term WHERE empl_term.employee IN :employees AND empl_term.workStation IN :work_stations"),
        @NamedQuery(name = TermEmployeeWorkOn.DELETE_FOR_EMPLOYEES_AND_TERMS, query = "DELETE FROM TermEmployeeWorkOn empl_term WHERE empl_term.employee IN :employees AND empl_term.term IN :terms"),
        @NamedQuery(name = TermEmployeeWorkOn.DELETE_FOR_WORK_STATIONS_AND_TERMS, query = "DELETE FROM TermEmployeeWorkOn empl_term WHERE empl_term.workStation IN :work_stations AND empl_term.term IN :terms")
})
public class TermEmployeeWorkOn implements Serializable {

    public static final String DELETE_FOR_EMPLOYEES = "TermEmployeeWorkOn.deleteForEmployees";
    public static final String DELETE_FOR_WORK_STATIONS = "TermEmployeeWorkOn.deleteForWorkStations";
    public static final String DELETE_FOR_TERMS = "TermEmployeeWorkOn.deleteForTerms";
    public static final String DELETE_FOR_EMPLOYEES_AND_WORK_STATIONS = "TermEmployeeWorkOn.deleteForEmployeesAndWorkStations";
    public static final String DELETE_FOR_EMPLOYEES_AND_TERMS = "TermEmployeeWorkOn.deleteForEmployeesAndTerms";
    public static final String DELETE_FOR_WORK_STATIONS_AND_TERMS = "TermEmployeeWorkOn.deleteForWorkStationsAndTerms";

    private Term term; // PK, FK
    private Employee employee; // PK, FK
    private WorkStation workStation; // FK

    /* constructors */

    public TermEmployeeWorkOn() { }

    public TermEmployeeWorkOn(Employee employee, Term term, WorkStation workStation) {
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
        if (obj == null || !(obj instanceof TermEmployeeWorkOn))
            return false;
        if (obj == this)
            return true;

        TermEmployeeWorkOn rhs = (TermEmployeeWorkOn) obj;
        return new EqualsBuilder()
                // if deriving: appendSuper(super.equals(obj)).
                .append(getTerm(), rhs.getTerm())
                .append(getEmployee(),rhs.getEmployee())
                .isEquals();
    }
}
