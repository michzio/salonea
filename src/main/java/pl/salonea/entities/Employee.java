package pl.salonea.entities;

import pl.salonea.enums.Gender;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "employee")
@DiscriminatorValue("employee")
@PrimaryKeyJoinColumn(name = "employee_id",
                foreignKey = @ForeignKey(name = "fk_employee_natural_person"))
@Access(AccessType.PROPERTY)
public class Employee extends NaturalPerson {

    private String jobPosition;
    private String description;

    private Set<Education> educations;
    private Set<Skill> skills;

    /* one-to-many relationships */
    private Set<TermEmployeeWorkOn> termsOnWorkStation;

    /* constructors */
    public Employee() { }

    public Employee(String email, String login, String password, String firstName, String lastName, String jobPosition) {
        super(email, login, password, firstName, lastName);
        this.jobPosition = jobPosition;
    }

    public Employee(String email, String login, String password, String firstName, String lastName, Short age, Gender gender, String jobPosition) {
        super(email, login, password, firstName, lastName, age, gender);
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
}
