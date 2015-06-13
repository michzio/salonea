package pl.salonea.entities;

import pl.salonea.constraints.Degree;
import pl.salonea.constraints.School;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "education")
@Access(AccessType.PROPERTY)
public class Education implements Serializable {

    private Long educationId;
    private String degree;
    private String school;

    private Set<Employee> educatedEmployees;

    /* constructors */

    public Education() { }

    public Education(String school, String degree) {
        this.school = school;
        this.degree = degree;
    }

    /* getters and setters */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false) // runtime scoped not null, whereas nullable references only to table
    @Column(name = "education_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    public Long getEducationId() {
        return educationId;
    }

    public void setEducationId(Long educationId) {
        this.educationId = educationId;
    }

    @NotNull
    @Size(min = 2, max = 255)
    @Degree
    @Column(name = "degree", nullable = false, length = 255)
    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    @NotNull
    @Size(min = 2, max = 255)
    @School
    @Column(name = "school", nullable = false, length = 255)
    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    /* many-to-many bidirectional relationship */

    @ManyToMany(mappedBy = "educations")
    public Set<Employee> getEducatedEmployees() {
        return educatedEmployees;
    }

    public void setEducatedEmployees(Set<Employee> employees) {
        this.educatedEmployees = employees;
    }
}
