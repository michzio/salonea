package pl.salonea.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pl.salonea.entities.idclass.EmployeeRatingId;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@IdClass(EmployeeRatingId.class)
@Table(name = "employee_rating")
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = EmployeeRating.FIND_BY_CLIENT, query = "SELECT er FROM EmployeeRating er WHERE er.client = :client"),
        @NamedQuery(name = EmployeeRating.FIND_BY_EMPLOYEE, query = "SELECT er FROM EmployeeRating er WHERE er.employee = :employee"),
        @NamedQuery(name = EmployeeRating.FIND_FOR_EMPLOYEE_BY_RATING, query = "SELECT er FROM EmployeeRating er WHERE er.employee = :employee AND er.clientRating = :rating"),
        @NamedQuery(name = EmployeeRating.FIND_FOR_EMPLOYEE_ABOVE_RATING, query = "SELECT er FROM EmployeeRating er WHERE er.employee = :employee AND er.clientRating >= :min_rating"),
        @NamedQuery(name = EmployeeRating.FIND_FOR_EMPLOYEE_BELOW_RATING, query = "SELECT er FROM EmployeeRating er WHERE er.employee = :employee AND er.clientRating <= :max_rating"),
        @NamedQuery(name = EmployeeRating.FIND_FROM_CLIENT_BY_RATING, query = "SELECT er FROM EmployeeRating er WHERE er.client = :client AND er.clientRating = :rating"),
        @NamedQuery(name = EmployeeRating.FIND_FROM_CLIENT_ABOVE_RATING, query = "SELECT er FROM EmployeeRating er WHERE er.client = :client AND er.clientRating >= :min_rating"),
        @NamedQuery(name = EmployeeRating.FIND_FROM_CLIENT_BELOW_RATING, query = "SELECT er FROM EmployeeRating er WHERE er.client = :client AND er.clientRating <= :max_rating"),
        @NamedQuery(name = EmployeeRating.FIND_EMPLOYEE_AVG_RATING, query = "SELECT AVG(er.clientRating) FROM EmployeeRating er WHERE er.employee = :employee"),
        @NamedQuery(name = EmployeeRating.COUNT_EMPLOYEE_RATINGS, query = "SELECT COUNT(er) FROM EmployeeRating er WHERE er.employee = :employee"),
        @NamedQuery(name = EmployeeRating.COUNT_CLIENT_RATINGS, query = "SELECT COUNT(er) FROM EmployeeRating er WHERE er.client = :client"),
        @NamedQuery(name = EmployeeRating.DELETE_BY_CLIENT, query = "DELETE FROM EmployeeRating er WHERE er.client = :client"),
        @NamedQuery(name = EmployeeRating.DELETE_BY_EMPLOYEE, query = "DELETE FROM EmployeeRating er WHERE er.employee = :employee"),
})
public class EmployeeRating {

    public static final String FIND_BY_CLIENT = "EmployeeRating.findByClient";
    public static final String FIND_BY_EMPLOYEE = "EmployeeRating.findByEmployee";
    public static final String FIND_FOR_EMPLOYEE_BY_RATING = "EmployeeRating.findForEmployeeByRating";
    public static final String FIND_FOR_EMPLOYEE_ABOVE_RATING = "EmployeeRating.findForEmployeeAboveRating";
    public static final String FIND_FOR_EMPLOYEE_BELOW_RATING = "EmployeeRating.findForEmployeeBelowRating";
    public static final String FIND_FROM_CLIENT_BY_RATING = "EmployeeRating.findFromClientByRating";
    public static final String FIND_FROM_CLIENT_ABOVE_RATING = "EmployeeRating.findFromClientAboveRating";
    public static final String FIND_FROM_CLIENT_BELOW_RATING = "EmployeeRating.findFromClientBelowRating";
    public static final String FIND_EMPLOYEE_AVG_RATING = "EmployeeRating.findEmployeeAvgRating";
    public static final String COUNT_EMPLOYEE_RATINGS = "EmployeeRating.countEmployeeRatings";
    public static final String COUNT_CLIENT_RATINGS = "EmployeeRating.countClientRatings";
    public static final String DELETE_BY_CLIENT = "EmployeeRating.deleteByClient";
    public static final String DELETE_BY_EMPLOYEE = "EmployeeRating.deleteByEmployee";


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

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 31) // two randomly chosen prime numbers
                // if deriving: .appendSuper(super.hashCode())
                .append(getClient())
                .append(getEmployee())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof EmployeeRating))
            return false;
        if (obj == this)
            return true;

        EmployeeRating rhs = (EmployeeRating) obj;
        return new EqualsBuilder()
                // if deriving: .appendSuper(super.equals(obj))
                .append(getClient(), rhs.getClient())
                .append(getEmployee(), rhs.getEmployee())
                .isEquals();
    }
}