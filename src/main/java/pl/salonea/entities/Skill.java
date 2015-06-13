package pl.salonea.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "skill")
@Access(AccessType.PROPERTY)
public class Skill implements Serializable {

    private Integer skillId; // PK
    private String skillName;
    private String description;

    private Set<Employee> skilledEmployees;

    /* constructors */

    public Skill() { }

    public Skill(String skillName) {
        this.skillName = skillName;
    }

    /* getters and setters */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false) // runtime scoped not null, whereas nullable references only to table
    @Column(name = "skill_id", nullable = false, columnDefinition = "INT UNSIGNED")
    public Integer getSkillId() {
        return skillId;
    }

    public void setSkillId(Integer skillId) {
        this.skillId = skillId;
    }

    @NotNull
    @Size(min = 2, max = 255)
    @Column(name = "skill_name", nullable = false, unique = true, length = 255)
    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    @Lob
    @Column(name = "description", columnDefinition = "LONGTEXT DEFAULT NULL")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

     /* many-to-many bidirectional relationship */

    @ManyToMany(mappedBy = "skills")
    public Set<Employee> getSkilledEmployees() {
        return skilledEmployees;
    }

    public void setSkilledEmployees(Set<Employee> skilledEmployees) {
        this.skilledEmployees = skilledEmployees;
    }
}
