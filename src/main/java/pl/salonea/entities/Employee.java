package pl.salonea.entities;

import pl.salonea.enums.Gender;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "employee")
@PrimaryKeyJoinColumn(name = "employee_id",
        foreignKey = @ForeignKey(name = "fk_employee_natural_person"))
@DiscriminatorValue("employee")
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = Employee.FIND_BY_DESCRIPTION, query = "SELECT e FROM Employee e WHERE e.description LIKE :description"),
        @NamedQuery(name = Employee.FIND_BY_JOB_POSITION, query = "SELECT e FROM Employee e WHERE e.jobPosition = :job_position"),
        @NamedQuery(name = Employee.FIND_BY_SKILL, query = "SELECT e FROM Employee e WHERE :skill MEMBER OF e.skills"),
        @NamedQuery(name = Employee.FIND_BY_EDUCATION, query = "SELECT e FROM Employee e WHERE :education MEMBER OF e.educations"),
        @NamedQuery(name = Employee.FIND_BY_SERVICE, query = "SELECT DISTINCT e FROM Employee e INNER JOIN e.suppliedServices ps WHERE ps.service = :service"),
        @NamedQuery(name = Employee.FIND_BY_PROVIDER_SERVICE, query = "SELECT DISTINCT e FROM Employee e INNER JOIN e.suppliedServices ps WHERE ps = :provider_service"),
        @NamedQuery(name = Employee.FIND_BY_SERVICE_POINT, query = "SELECT DISTINCT e FROM Employee e INNER JOIN e.termsOnWorkStation empl_term INNER JOIN empl_term.workStation ws WHERE ws.servicePoint = :service_point"),
        @NamedQuery(name = Employee.FIND_BY_SERVICE_POINT_AND_TERM, query = "SELECT DISTINCT e FROM Employee e INNER JOIN e.termsOnWorkStation empl_term INNER JOIN empl_term.workStation ws INNER JOIN empl_term.term term WHERE ws.servicePoint = :service_point AND term.openingTime < :end_time AND term.closingTime > :start_time"), // constraint: openingTime < closingTime
        @NamedQuery(name = Employee.FIND_BY_SERVICE_POINT_AND_TERM_STRICT, query = "SELECT DISTINCT e FROM Employee e INNER JOIN e.termsOnWorkStation empl_term INNER JOIN empl_term.workStation ws INNER JOIN  empl_term.term term WHERE ws.servicePoint = :service_point AND term.openingTime <= :start_time AND term.closingTime >= :end_time"),
        @NamedQuery(name = Employee.FIND_BY_WORK_STATION, query = "SELECT DISTINCT e FROM Employee e INNER JOIN e.termsOnWorkStation empl_term WHERE empl_term.workStation = :work_station"),
        @NamedQuery(name = Employee.FIND_BY_WORK_STATION_AND_TERM, query = "SELECT DISTINCT e FROM Employee e INNER JOIN e.termsOnWorkStation empl_term INNER JOIN empl_term.term term WHERE empl_term.workStation = :work_station AND term.openingTime < :end_time AND term.closingTime > :start_time"), // constraint: openingTime < closingTime
        @NamedQuery(name = Employee.FIND_BY_WORK_STATION_AND_TERM_STRICT, query = "SELECT DISTINCT e FROM Employee e INNER JOIN e.termsOnWorkStation empl_term INNER JOIN empl_term.term term WHERE empl_term.workStation = :work_station AND term.openingTime <= :start_time AND term.closingTime >= :end_time"),
})
public class Employee extends NaturalPerson {

    public static final String FIND_BY_DESCRIPTION = "Employee.findByDescription";
    public static final String FIND_BY_JOB_POSITION = "Employee.findByJobPosition";
    public static final String FIND_BY_SKILL = "Employee.findBySkill";
    public static final String FIND_BY_EDUCATION = "Employee.findByEducation";
    public static final String FIND_BY_SERVICE = "Employee.findByService";
    public static final String FIND_BY_PROVIDER_SERVICE = "Employee.findByProviderService";
    public static final String FIND_BY_SERVICE_POINT = "Employee.findByServicePoint";
    public static final String FIND_BY_SERVICE_POINT_AND_TERM = "Employee.findByServicePointAndTerm";
    public static final String FIND_BY_SERVICE_POINT_AND_TERM_STRICT = "Employee.findByServicePointAndTermStrict";
    public static final String FIND_BY_WORK_STATION = "Employee.findByWorkStation";
    public static final String FIND_BY_WORK_STATION_AND_TERM = "Employee.findByWorkStationAndTerm";
    public static final String FIND_BY_WORK_STATION_AND_TERM_STRICT = "Employee.findByWorkStationAndTermStrict";

    private String jobPosition;
    private String description;

    private Set<Education> educations = new HashSet<>();
    private Set<Skill> skills = new HashSet<>();

    /* one-to-many relationships */
    private Set<TermEmployeeWorkOn> termsOnWorkStation = new HashSet<>();
    private Set<ProviderService> suppliedServices = new HashSet<>();
    private Set<EmployeeRating> receivedRatings = new HashSet<>();

    /* constructors */
    public Employee() { }

    public Employee(String email, String login, String password, String firstName, String lastName, String jobPosition) {
        super(email, login, password, firstName, lastName);
        this.jobPosition = jobPosition;
    }

    public Employee(String email, String login, String password, String firstName, String lastName, Date birthDate, Gender gender, String jobPosition) {
        super(email, login, password, firstName, lastName, birthDate, gender);
        this.jobPosition = jobPosition;
    }

    /* getters and setters */

    @NotNull
    @Size(min=2, max=45)
    @Column(name = "job_position", nullable = false, length = 45)
    // TODO limit job positions to closed selection list
    public String getJobPosition() {
        return jobPosition;
    }

    public void setJobPosition(String jobPosition) {
        this.jobPosition = jobPosition;
    }

    @Lob
    @Column(name = "description", columnDefinition = "LONGTEXT DEFAULT NULL")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /* many-to-many bidirectional relationships */

    @ManyToMany
    @JoinTable(name = "employee_education",
        joinColumns = @JoinColumn(name = "employee_id"),
        inverseJoinColumns = @JoinColumn(name = "education_id")
    )
    public Set<Education> getEducations() {
        return educations;
    }

    public void setEducations(Set<Education> educations) {
        this.educations = educations;
    }

    @ManyToMany
    @JoinTable(name = "employee_skill",
        joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    public Set<Skill> getSkills() {
        return skills;
    }

    public void setSkills(Set<Skill> skills) {
        this.skills = skills;
    }

    /* one-to-many relationships */

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    public Set<TermEmployeeWorkOn> getTermsOnWorkStation() {
        return termsOnWorkStation;
    }

    public void setTermsOnWorkStation(Set<TermEmployeeWorkOn> termsOnWorkStation) {
        this.termsOnWorkStation = termsOnWorkStation;
    }

    @ManyToMany(mappedBy = "supplyingEmployees", fetch = FetchType.LAZY)
    public Set<ProviderService> getSuppliedServices() {
        return suppliedServices;
    }

    public void setSuppliedServices(Set<ProviderService> suppliedServices) {
        this.suppliedServices = suppliedServices;
    }

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    public Set<EmployeeRating> getReceivedRatings() {
        return receivedRatings;
    }

    public void setReceivedRatings(Set<EmployeeRating> receivedRatings) {
        this.receivedRatings = receivedRatings;
    }
}
