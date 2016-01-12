package pl.salonea.ejb.interfaces;

/**
 * Created by michzio on 12/01/2016.
 */
public interface EmployeeEducationRelationshipManagerInterface {

    void addEducationToEmployee(Long educationId, Long employeeId);
    void removeEducationFromEmployee(Long educationId, Long employeeId);

    @javax.ejb.Remote
    interface Remote extends EmployeeEducationRelationshipManagerInterface { }

    @javax.ejb.Local
    interface Local extends EmployeeEducationRelationshipManagerInterface { }
}
