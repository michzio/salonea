package pl.salonea.jaxrs.wrappers;

import pl.salonea.entities.Education;
import pl.salonea.entities.Employee;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by michzio on 14/01/2016.
 */
@XmlRootElement(name = "education")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class EducationWrapper {

    private Education education;
    private Set<Employee> educatedEmployees;

    // default no-args constructor
    public EducationWrapper() { }

    public EducationWrapper(Education education) {
        this.education = education;
        this.educatedEmployees = education.getEducatedEmployees();
    }

    public static List<EducationWrapper> wrap( List<Education> educations ) {

        List<EducationWrapper> wrappedEducations = new ArrayList<>();

        for(Education education : educations)
            wrappedEducations.add(new EducationWrapper(education));

        return wrappedEducations;
    }

    @XmlElement(name = "entity", nillable = true)
    public Education getEducation() {
        return education;
    }

    public void setEducation(Education education) {
        this.education = education;
    }

    @XmlElement(name = "educatedEmployees", nillable = true)
    public Set<Employee> getEducatedEmployees() {
        return educatedEmployees;
    }

    public void setEducatedEmployees(Set<Employee> educatedEmployees) {
        this.educatedEmployees = educatedEmployees;
    }
}
