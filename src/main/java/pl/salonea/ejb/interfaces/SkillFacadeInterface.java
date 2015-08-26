package pl.salonea.ejb.interfaces;

import pl.salonea.entities.Employee;
import pl.salonea.entities.Skill;

import java.util.List;

/**
 * Created by michzio on 14/08/2015.
 */
public interface SkillFacadeInterface extends AbstractFacadeInterface<Skill> {

    // concrete interface
    List<Skill> findByName(String skillName);
    List<Skill> findByName(String skillName, Integer start, Integer limit);
    List<Skill> findByDescription(String description);
    List<Skill> findByDescription(String description, Integer start, Integer limit);
    List<Skill> findByKeyword(String keyword);
    List<Skill> findByKeyword(String keyword, Integer start, Integer limit);
    List<Skill> findByEmployee(Employee employee);
    List<Skill> findByEmployee(Employee employee, Integer start, Integer limit);
    List<Skill> findByEmployeeAndKeyword(Employee employee, String keyword);
    List<Skill> findByEmployeeAndKeyword(Employee employee, String keyword, Integer start, Integer limit);
    Integer deleteByName(String skillName);
    Integer deleteByEmployee(Employee employee);
    Integer deleteBySkills(List<Skill> skills);

    @javax.ejb.Local
    interface Local extends SkillFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends SkillFacadeInterface { }
}
