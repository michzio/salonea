package pl.salonea.ejb.interfaces;

import pl.salonea.entities.Employee;
import pl.salonea.entities.Skill;

import java.util.List;

/**
 * Created by michzio on 14/08/2015.
 */
public interface SkillFacadeInterface extends AbstractFacadeInterface<Skill> {

    // concrete interface
    Skill update(Skill skill, Boolean retainTransientFields);

    List<Skill> findAllEagerly();
    List<Skill> findAllEagerly(Integer start, Integer limit);
    Skill findByIdEagerly(Integer skillId);

    List<Skill> findByName(String skillName);
    List<Skill> findByName(String skillName, Integer start, Integer limit);
    List<Skill> findByDescription(String description);
    List<Skill> findByDescription(String description, Integer start, Integer limit);
    List<Skill> findByKeyword(String keyword);
    List<Skill> findByKeyword(String keyword, Integer start, Integer limit);

    List<Skill> findByEmployee(Employee employee);
    List<Skill> findByEmployee(Employee employee, Integer start, Integer limit);
    List<Skill> findByEmployeeEagerly(Employee employee);
    List<Skill> findByEmployeeEagerly(Employee employee, Integer start, Integer limit);
    List<Skill> findByEmployeeAndKeyword(Employee employee, String keyword);
    List<Skill> findByEmployeeAndKeyword(Employee employee, String keyword, Integer start, Integer limit);

    Integer countByEmployee(Employee employee);

    Integer deleteByName(String skillName);
    Integer deleteBySkills(List<Skill> skills);
    Integer deleteByEmployee(Employee employee);

    List<Skill> findByMultipleCriteria(List<String> skillNames, List<String> descriptions, List<Employee> employees);
    List<Skill> findByMultipleCriteria(List<String> skillNames, List<String> descriptions, List<Employee> employees, Integer start, Integer limit);
    List<Skill> findByMultipleCriteria(List<String> keywords, List<Employee> employees);
    List<Skill> findByMultipleCriteria(List<String> keywords, List<Employee> employees, Integer start, Integer limit);

    List<Skill> findByMultipleCriteriaEagerly(List<String> skillNames, List<String> descriptions, List<Employee> employees);
    List<Skill> findByMultipleCriteriaEagerly(List<String> skillNames, List<String> descriptions, List<Employee> employees, Integer start, Integer limit);
    List<Skill> findByMultipleCriteriaEagerly(List<String> keywords, List<Employee> employees);
    List<Skill> findByMultipleCriteriaEagerly(List<String> keywords, List<Employee> employees, Integer start, Integer limit);

    @javax.ejb.Local
    interface Local extends SkillFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends SkillFacadeInterface { }
}
