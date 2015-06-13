package pl.salonea.entities;

import pl.salonea.entities.idclass.TermEmployeeId;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * This entity is intermediary for ternary relationship
 * between Term, Employee, WorkStation. It defines
 * associations that tells which work station employee
 * in given term works on.
 */
@Entity
@Table(name = "work_on")
@Access(AccessType.PROPERTY)
@IdClass(TermEmployeeId.class)
public class TermEmployeeWorkOn implements Serializable {

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
}
