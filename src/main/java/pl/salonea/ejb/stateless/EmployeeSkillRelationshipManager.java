package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.EmployeeSkillRelationshipManagerInterface;
import pl.salonea.entities.Employee;
import pl.salonea.entities.Skill;
import pl.salonea.jaxrs.exceptions.NotFoundException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Created by michzio on 12/01/2016.
 */
@Stateless
@LocalBean
public class EmployeeSkillRelationshipManager
        implements EmployeeSkillRelationshipManagerInterface.Local, EmployeeSkillRelationshipManagerInterface.Remote {

    @Inject
    private EmployeeFacade employeeFacade;
    @Inject
    private SkillFacade skillFacade;


    @Override
    public void addSkillToEmployee(Integer skillId, Long employeeId) throws NotFoundException {

        Employee employee = employeeFacade.find(employeeId);
        if(employee == null)
            throw new NotFoundException("Employee entity could not be found for id " + employeeId);
        Skill skill = skillFacade.find(skillId);
        if(skill == null)
            throw new NotFoundException("Skill entity could not be found for id " + skillId);

        employee.getSkills().add(skill);
        skill.getSkilledEmployees().add(employee);
    }

    @Override
    public void removeSkillFromEmployee(Integer skillId, Long employeeId) throws NotFoundException {

        Employee employee = employeeFacade.find(employeeId);
        if(employee == null)
            throw new NotFoundException("Employee entity could not be found for id " + employeeId);
        Skill skill = skillFacade.find(skillId);
        if(skill == null)
            throw new NotFoundException("Skill entity could not be found for id " + skillId);

        employee.getSkills().remove(skill);
        skill.getSkilledEmployees().remove(employee);
    }
}
