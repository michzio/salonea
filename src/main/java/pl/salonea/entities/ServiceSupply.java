package pl.salonea.entities;

import pl.salonea.entities.idclass.ServiceSupplyId;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "service_supply")
@Access(AccessType.PROPERTY)
@IdClass(ServiceSupplyId.class)
public class ServiceSupply {

    private Service service; // PK, FK
    private Employee employee; // PK, FK
    private Term term; // PK, FK

    /* constructors */

    public ServiceSupply() { }

    public ServiceSupply(Service service, Employee employee, Term term) {
        this.service = service;
        this.employee = employee;
        this.term = term;
    }

    /* PK getters and setters */

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
}
