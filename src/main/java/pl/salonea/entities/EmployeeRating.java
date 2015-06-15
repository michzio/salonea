package pl.salonea.entities;

import pl.salonea.entities.idclass.EmployeeRatingId;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "employee_rating")
@Access(AccessType.PROPERTY)
@IdClass(EmployeeRatingId.class)
public class EmployeeRating {

    private Client client; // PK, FK
    private Employee employee; // PK, FK

    private Short clientRating;
    private String clientComment;
    private String employeeDementi;

     /* constructors */

    public EmployeeRating() { }

    public EmployeeRating(Employee employee, Client client) {
        this.client = client;
        this.employee = employee;
    }

    public EmployeeRating(Employee employee, Client client, Short clientRating) {
        this.client = client;
        this.employee = employee;
        this.clientRating = clientRating;
    }

    /* PK getters and setters */

    @Id
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", referencedColumnName = "client_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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
    @Min(0) @Max(10)
    @Column(name = "client_rating", nullable = false, columnDefinition = "TINYINT UNSIGNED DEFAULT 0")
    public Short getClientRating() {
        return clientRating;
    }

    public void setClientRating(Short clientRating) {
        this.clientRating = clientRating;
    }

    @Lob
    @Column(name = "client_comment", columnDefinition = "LONGTEXT DEFAULT NULL")
    public String getClientComment() {
        return clientComment;
    }

    public void setClientComment(String clientComment) {
        this.clientComment = clientComment;
    }

    @Lob
    @Column(name = "employee_dementi", columnDefinition = "LONGTEXT DEFAULT NULL")
    public String getEmployeeDementi() {
        return employeeDementi;
    }

    public void setEmployeeDementi(String employeeDementi) {
        this.employeeDementi = employeeDementi;
    }
}