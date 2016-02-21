package pl.salonea.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pl.salonea.constraints.Degree;
import pl.salonea.constraints.Faculty;
import pl.salonea.constraints.School;
import pl.salonea.jaxrs.utils.hateoas.Link;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.*;

@XmlRootElement(name = "education")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"educationId", "degree", "faculty", "school"})

@Entity
@Table(name = "education", uniqueConstraints = @UniqueConstraint(columnNames = {"degree", "faculty", "school"}))
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = Education.FIND_ALL_EAGERLY, query = "SELECT DISTINCT e FROM Education e LEFT JOIN FETCH e.educatedEmployees"),
        @NamedQuery(name = Education.FIND_BY_ID_EAGERLY, query = "SELECT e FROM Education e LEFT JOIN FETCH e.educatedEmployees WHERE e.educationId = :educationId"),
        @NamedQuery(name = Education.FIND_BY_DEGREE, query = "SELECT e FROM Education e WHERE e.degree LIKE :degree"),
        @NamedQuery(name = Education.FIND_BY_FACULTY, query = "SELECT e FROM Education e WHERE e.faculty LIKE :faculty"),
        @NamedQuery(name = Education.FIND_BY_SCHOOL, query = "SELECT e FROM Education e WHERE e.school LIKE :school"),
        @NamedQuery(name = Education.FIND_BY_DEGREE_AND_SCHOOL, query = "SELECT e FROM Education e WHERE e.degree LIKE :degree AND e.school LIKE :school"),
        @NamedQuery(name = Education.FIND_BY_FACULTY_AND_SCHOOL,query = "SELECT e FROM Education e WHERE e.faculty LIKE :faculty AND e.school LIKE :school"),
        @NamedQuery(name = Education.FIND_BY_KEYWORD, query = "SELECT e FROM Education e WHERE e.degree LIKE :keyword OR e.faculty LIKE :keyword OR e.school LIKE :keyword"),
        @NamedQuery(name = Education.FIND_BY_EMPLOYEE, query = "SELECT e FROM Education e WHERE :employee MEMBER OF e.educatedEmployees"),
        @NamedQuery(name = Education.FIND_BY_EMPLOYEE_EAGERLY, query = "SELECT DISTINCT e FROM Education e LEFT JOIN FETCH e.educatedEmployees WHERE :employee MEMBER OF e.educatedEmployees"),
        @NamedQuery(name = Education.FIND_BY_EMPLOYEE_AND_KEYWORD, query = "SELECT e FROM Education e WHERE :employee MEMBER OF e.educatedEmployees " +
                                                                            "AND (e.degree LIKE :keyword OR e.faculty LIKE :keyword OR e.school LIKE :keyword)"),
        @NamedQuery(name = Education.COUNT_BY_EMPLOYEE, query = "SELECT COUNT(e) FROM Education e WHERE :employee MEMBER OF e.educatedEmployees"),
        @NamedQuery(name = Education.DELETE_BY_DEGREE, query = "DELETE FROM Education e WHERE e.degree = :degree"),
        @NamedQuery(name = Education.DELETE_BY_FACULTY, query = "DELETE FROM Education e WHERE e.faculty = :faculty"),
        @NamedQuery(name = Education.DELETE_BY_SCHOOL, query = "DELETE FROM Education e WHERE e.school = :school"),
        @NamedQuery(name = Education.DELETE_BY_DEGREE_AND_SCHOOL, query = "DELETE FROM Education e WHERE e.degree = :degree AND e.school = :school"),
        @NamedQuery(name = Education.DELETE_BY_FACULTY_AND_SCHOOL, query = "DELETE FROM Education e WHERE e.faculty = :faculty AND e.school = :school"),
        @NamedQuery(name = Education.DELETE_BY_EMPLOYEE, query = "DELETE FROM Education e WHERE :employee MEMBER OF e.educatedEmployees"),
        // @NamedQuery(name = Education.DELETE_BY_EDUCATIONS, query = "DELETE FROM Education e WHERE e IN :educations"), deprecated -- don't work in EclipseLink
})
public class Education implements Serializable {

    public static final String FIND_ALL_EAGERLY = "Education.findAllEagerly";
    public static final String FIND_BY_ID_EAGERLY = "Education.findByIdEagerly";
    public static final String FIND_BY_DEGREE = "Education.findByDegree";
    public static final String FIND_BY_FACULTY = "Education.findByFaculty";
    public static final String FIND_BY_SCHOOL = "Education.findBySchool";
    public static final String FIND_BY_DEGREE_AND_SCHOOL = "Education.findByDegreeAndSchool";
    public static final String FIND_BY_FACULTY_AND_SCHOOL = "Education.findByFacultyAndSchool";
    public static final String FIND_BY_KEYWORD = "Education.findByKeyword";
    public static final String FIND_BY_EMPLOYEE = "Education.findByEmployee";
    public static final String FIND_BY_EMPLOYEE_EAGERLY = "Education.findByEmployeeEagerly";
    public static final String FIND_BY_EMPLOYEE_AND_KEYWORD = "Education.findByEmployeeAndKeyword";
    public static final String COUNT_BY_EMPLOYEE = "Education.countByEmployee";
    public static final String DELETE_BY_DEGREE = "Education.deleteByDegree";
    public static final String DELETE_BY_FACULTY = "Education.deleteByFaculty";
    public static final String DELETE_BY_SCHOOL = "Education.deleteBySchool";
    public static final String DELETE_BY_DEGREE_AND_SCHOOL = "Education.deleteByDegreeAndSchool";
    public static final String DELETE_BY_FACULTY_AND_SCHOOL = "Education.deleteByFacultyAndSchool";
    public static final String DELETE_BY_EMPLOYEE = "Education.deleteByEmployee";
    //public static final String DELETE_BY_EDUCATIONS = "Education.deleteByEducations"; deprecated -- don't work in EclipseLink

    private Long educationId;
    private String degree; // composite business key
    private String faculty; // composite business key
    private String school; // composite business key

    private Set<Employee> educatedEmployees = new HashSet<>();

    // HATEOAS support for RESTFul web service in JAX-RS
    private LinkedHashSet<Link> links = new LinkedHashSet<>();

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
    @Column(name = "education_id", nullable = false /*,  columnDefinition = "BIGINT UNSIGNED" */)
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

    @Size(min = 2, max = 255)
    @Faculty
    @Column(name = "faculty", nullable = true, length = 255)
    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
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

    @XmlTransient
    @ManyToMany(mappedBy = "educations", fetch = FetchType.LAZY)
    public Set<Employee> getEducatedEmployees() {
        return educatedEmployees;
    }

    public void setEducatedEmployees(Set<Employee> employees) {
        this.educatedEmployees = employees;
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
                .append(getDegree())
                .append(getFaculty())
                .append(getSchool())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Education))
            return false;
        if (obj == this)
            return true;

        Education rhs = (Education) obj;
        return new EqualsBuilder()
                // if deriving: .appendSuper(super.equals(obj)).
                .append(getDegree(), rhs.getDegree())
                .append(getFaculty(), rhs.getFaculty())
                .append(getSchool(), rhs.getSchool())
                .isEquals();
    }
}