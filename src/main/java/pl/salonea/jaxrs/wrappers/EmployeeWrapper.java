package pl.salonea.jaxrs.wrappers;

import pl.salonea.entities.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by michzio on 31/10/2015.
 */
@XmlRootElement(name = "employee")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class EmployeeWrapper {

    private Employee employee;
    private Set<Education> educations;
    private Set<Skill> skills;
    private Set<ProviderService> suppliedServices;
    private Set<EmployeeTerm> termsOnWorkStation;
    private Set<EmployeeRating> receivedRatings;

    // default no-args constructor
    public EmployeeWrapper() { }

    public EmployeeWrapper(Employee employee) {
        this.employee = employee;
        this.educations = employee.getEducations();
        this.skills = employee.getSkills();
        this.suppliedServices = employee.getSuppliedServices();
        this.termsOnWorkStation = employee.getTermsOnWorkStation();
        this.receivedRatings = employee.getReceivedRatings();
    }

    public static List<EmployeeWrapper> wrap(List<Employee> employees) {

        List<EmployeeWrapper> wrappedEmployees = new ArrayList<>();

        for(Employee employee : employees)
            wrappedEmployees.add(new EmployeeWrapper(employee));

        return wrappedEmployees;
    }

    @XmlElement(name = "entity", nillable = true)
    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @XmlElement(name = "educations", nillable = true)
    public Set<Education> getEducations() {
        return educations;
    }

    public void setEducations(Set<Education> educations) {
        this.educations = educations;
    }

    @XmlElement(name = "skills", nillable = true)
    public Set<Skill> getSkills() {
        return skills;
    }

    public void setSkills(Set<Skill> skills) {
        this.skills = skills;
    }

    @XmlElement(name = "suppliedServices", nillable = true)
    public Set<ProviderService> getSuppliedServices() {
        return suppliedServices;
    }

    public void setSuppliedServices(Set<ProviderService> suppliedServices) {
        this.suppliedServices = suppliedServices;
    }

    @XmlElement(name = "termsOnWorkStation", nillable = true)
    public Set<EmployeeTerm> getTermsOnWorkStation() {
        return termsOnWorkStation;
    }

    public void setTermsOnWorkStation(Set<EmployeeTerm> termsOnWorkStation) {
        this.termsOnWorkStation = termsOnWorkStation;
    }

    @XmlElement(name = "receivedRatings", nillable = true)
    public Set<EmployeeRating> getReceivedRatings() {
        return receivedRatings;
    }

    public void setReceivedRatings(Set<EmployeeRating> receivedRatings) {
        this.receivedRatings = receivedRatings;
    }
}
