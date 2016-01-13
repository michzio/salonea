package pl.salonea.ejb.interfaces;

import pl.salonea.entities.Education;
import pl.salonea.entities.Employee;

import java.util.List;

/**
 * Created by michzio on 15/08/2015.
 */
public interface EducationFacadeInterface extends AbstractFacadeInterface<Education> {

    // concrete interface
    Education update(Education education, Boolean retainTransientFields);

    List<Education> findAllEagerly();
    List<Education> findAllEagerly(Integer start, Integer limit);
    Education findByIdEagerly(Long educationId);

    List<Education> findByDegree(String degree);
    List<Education> findByDegree(String degree, Integer start, Integer limit);
    List<Education> findByFaculty(String faculty);
    List<Education> findByFaculty(String faculty, Integer start, Integer limit);
    List<Education> findBySchool(String school);
    List<Education> findBySchool(String school, Integer start, Integer limit);
    List<Education> findByDegreeAndSchool(String degree, String school);
    List<Education> findByDegreeAndSchool(String degree, String school, Integer start, Integer limit);
    List<Education> findByFacultyAndSchool(String faculty, String school);
    List<Education> findByFacultyAndSchool(String faculty, String school, Integer start, Integer limit);
    List<Education> findByKeyword(String keyword);
    List<Education> findByKeyword(String keyword, Integer start, Integer limit);

    List<Education> findByEmployee(Employee employee);
    List<Education> findByEmployee(Employee employee, Integer start, Integer limit);
    List<Education> findByEmployeeEagerly(Employee employee);
    List<Education> findByEmployeeEagerly(Employee employee, Integer start, Integer limit);
    List<Education> findByEmployeeAndKeyword(Employee employee, String keyword);
    List<Education> findByEmployeeAndKeyword(Employee employee, String keyword, Integer start, Integer limit);

    Long countByEmployee(Employee employee);

    Integer deleteByDegree(String degree);
    Integer deleteByFaculty(String faculty);
    Integer deleteBySchool(String school);
    Integer deleteByDegreeAndSchool(String degree, String school);
    Integer deleteByFacultyAndSchool(String faculty, String school);
    Integer deleteByEmployee(Employee employee);
    Integer deleteByEducations(List<Education> educations);

    List<Education> findByMultipleCriteria(List<String> degrees, List<String> faculties, List<String> schools, List<Employee> employees);
    List<Education> findByMultipleCriteria(List<String> degrees, List<String> faculties, List<String> schools, List<Employee> employees, Integer start, Integer limit);
    List<Education> findByMultipleCriteria(List<String> keywords, List<Employee> employees);
    List<Education> findByMultipleCriteria(List<String> keywords, List<Employee> employees, Integer start, Integer limit);

    List<Education> findByMultipleCriteriaEagerly(List<String> degrees, List<String> faculties, List<String> schools, List<Employee> employees);
    List<Education> findByMultipleCriteriaEagerly(List<String> degrees, List<String> faculties, List<String> schools, List<Employee> employees, Integer start, Integer limit);
    List<Education> findByMultipleCriteriaEagerly(List<String> keywords, List<Employee> employees);
    List<Education> findByMultipleCriteriaEagerly(List<String> keywords, List<Employee> employees, Integer start, Integer limit);

    @javax.ejb.Local
    interface Local extends EducationFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends EducationFacadeInterface { }
}
