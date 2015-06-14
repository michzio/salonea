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
@ChronologicalDates(dateAttributes = { "openingTime", "closingTime"}, order = ChronologicalDates.Order.ASCENDING)
public class Term  implements Serializable {

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

    @OneToMany(mappedBy = "term", fetch = FetchType.LAZY)
    public Set<TermEmployeeWorkOn> getEmployeesWorkStation() {
        return employeesWorkStation;
    }

    public void setEmployeesWorkStation(Set<TermEmployeeWorkOn> employeesWorkStation) {
        this.employeesWorkStation = employeesWorkStation;
    }

}
