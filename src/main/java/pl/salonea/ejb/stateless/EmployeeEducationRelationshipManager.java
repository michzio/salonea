package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.EmployeeEducationRelationshipManagerInterface;
import pl.salonea.entities.Education;
import pl.salonea.entities.Employee;
import pl.salonea.jaxrs.exceptions.NotFoundException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Created by michzio on 12/01/2016.
 */
@Stateless
@LocalBean
public class EmployeeEducationRelationshipManager
        implements EmployeeEducationRelationshipManagerInterface.Local, EmployeeEducationRelationshipManagerInterface.Remote {

    @Inject
    private EmployeeFacade employeeFacade;
    @Inject
    private EducationFacade educationFacade;


    @Override
    public void addEducationToEmployee(Long educationId, Long employeeId) {

        Employee employee = employeeFacade.find(employeeId);
        if(employee == null)
            throw new NotFoundException("Employee entity could not be found for id " + employeeId);
        Education education = educationFacade.find(educationId);
        if(education == null)
            throw new NotFoundException("Education entity could not be found for id " + educationId);

        employee.getEducations().add(education);
        education.getEducatedEmployees().add(employee);
    }

    @Override
    public void removeEducationFromEmployee(Long educationId, Long employeeId) {

        Employee employee = employeeFacade.find(employeeId);
        if(employee == null)
            throw new NotFoundException("Employee entity could not be found for id " + employeeId);
        Education education = educationFacade.find(educationId);
        if(education == null)
            throw new NotFoundException("Education entity could not be found for id " + educationId);

        employee.getEducations().remove(education);
        education.getEducatedEmployees().remove(employee);
    }
}
