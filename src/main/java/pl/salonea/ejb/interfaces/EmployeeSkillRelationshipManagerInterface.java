package pl.salonea.ejb.interfaces;

/**
 * Created by michzio on 12/01/2016.
 */
public interface EmployeeSkillRelationshipManagerInterface {

    void addSkillToEmployee(Integer skillId, Long employeeId);
    void removeSkillFromEmployee(Integer skillId, Long employeeId);

    @javax.ejb.Remote
    interface Remote extends EmployeeSkillRelationshipManagerInterface { }

    @javax.ejb.Local
    interface Local extends EmployeeSkillRelationshipManagerInterface { }
}
