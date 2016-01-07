package pl.salonea.jaxrs.wrappers;

import pl.salonea.entities.Employee;
import pl.salonea.entities.Skill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by michzio on 07/01/2016.
 */
@XmlRootElement(name = "skill")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class SkillWrapper {

    private Skill skill;
    private Set<Employee> skilledEmployees;

    // default no-args constructor
    public SkillWrapper() { }

    public SkillWrapper(Skill skill) {
        this.skill = skill;
        this.skilledEmployees = skill.getSkilledEmployees();
    }

    public static List<SkillWrapper> wrap( List<Skill> skills ) {

        List<SkillWrapper> wrappedSkills = new ArrayList<>();

        for(Skill skill : skills)
            wrappedSkills.add(new SkillWrapper(skill));

        return wrappedSkills;
    }

    @XmlElement(name = "entity", nillable = true)
    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    @XmlElement(name = "skilledEmployees", nillable = true)
    public Set<Employee> getSkilledEmployees() {
        return skilledEmployees;
    }

    public void setSkilledEmployees(Set<Employee> skilledEmployees) {
        this.skilledEmployees = skilledEmployees;
    }
}
