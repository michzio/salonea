package pl.salonea.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "skill")
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = Skill.FIND_BY_NAME, query = "SELECT s FROM Skill s WHERE s.skillName LIKE :skill_name"),
        @NamedQuery(name = Skill.FIND_BY_DESCRIPTION, query = "SELECT s FROM Skill s WHERE s.description LIKE :description"),
        @NamedQuery(name = Skill.FIND_BY_KEYWORD, query = "SELECT s FROM Skill s WHERE s.skillName LIKE :keyword OR s.description LIKE :keyword"),
        @NamedQuery(name = Skill.FIND_BY_EMPLOYEE, query = "SELECT s FROM Skill s WHERE :employee MEMBER OF s.skilledEmployees"),
        @NamedQuery(name = Skill.FIND_BY_EMPLOYEE_AND_KEYWORD, query = "SELECT s FROM Skill s WHERE :employee MEMBER OF s.skilledEmployees " +
                                                                        "AND (s.skillName LIKE :keyword OR s.description LIKE :keyword)"),
        @NamedQuery(name = Skill.DELETE_BY_NAME, query = "DELETE FROM Skill s WHERE s.skillName = :skill_name"),
        @NamedQuery(name = Skill.DELETE_BY_EMPLOYEE, query = "DELETE FROM Skill s WHERE :employee MEMBER OF s.skilledEmployees"),
})
public class Skill implements Serializable {

    public static final String FIND_BY_NAME = "Skill.findByName";
    public static final String FIND_BY_DESCRIPTION = "Skill.findByDescription";
    public static final String FIND_BY_KEYWORD = "Skill.findByKeyword";
    public static final String FIND_BY_EMPLOYEE = "Skill.findByEmployee";
    public static final String FIND_BY_EMPLOYEE_AND_KEYWORD = "Skill.findByEmployeeAndKeyword";
    public static final String DELETE_BY_NAME = "Skill.deleteByName";
    public static final String DELETE_BY_EMPLOYEE = "Skill.deleteByEmployee";

    private Integer skillId; // PK
    private String skillName; // business key
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

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 31) // two randomly chosen prime numbers
                // if deriving: .appendSuper(super.hashCode())
                .append(getSkillName())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Skill))
            return false;
        if (obj == this)
            return true;

        Skill rhs = (Skill) obj;
        return new EqualsBuilder()
                // if deriving: .appendSuper(super.equals(obj)).
                .append(getSkillName(), rhs.getSkillName())
                .isEquals();
    }
}
