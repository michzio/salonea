package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.EducationFacadeInterface;
import pl.salonea.entities.Education;
import pl.salonea.entities.Education_;
import pl.salonea.entities.Employee;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michzio on 15/08/2015.
 */
@Stateless
@LocalBean
public class EducationFacade extends AbstractFacade<Education> implements EducationFacadeInterface.Local, EducationFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public EducationFacade() {
        super(Education.class);
    }


    @Override
    public Education update(Education education, Boolean retainTransientFields) {

        if(retainTransientFields) {
            // keep current collection attributes of resource (and other marked @XmlTransient)
            Education currentEducation = findByIdEagerly(education.getEducationId());
            if(currentEducation != null) {
                education.setEducatedEmployees(currentEducation.getEducatedEmployees());
            }
        }
        return update(education);
    }

    @Override
    public List<Education> findAllEagerly() {
        return findAllEagerly(null, null);
    }

    @Override
    public List<Education> findAllEagerly(Integer start, Integer limit) {

        TypedQuery<Education> query = getEntityManager().createNamedQuery(Education.FIND_ALL_EAGERLY, Education.class);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Education findByIdEagerly(Long educationId) {

        TypedQuery<Education> query = getEntityManager().createNamedQuery(Education.FIND_BY_ID_EAGERLY, Education.class);
        query.setParameter("educationId", educationId);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    @Override
    public List<Education> findByDegree(String degree) {
        return findByDegree(degree, null, null);
    }

    @Override
    public List<Education> findByDegree(String degree, Integer start, Integer limit) {

        TypedQuery<Education> query = getEntityManager().createNamedQuery(Education.FIND_BY_DEGREE, Education.class);
        query.setParameter("degree", "%" + degree + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Education> findByFaculty(String faculty) {
        return findByFaculty(faculty, null, null);
    }

    @Override
    public List<Education> findByFaculty(String faculty, Integer start, Integer limit) {

        TypedQuery<Education> query = getEntityManager().createNamedQuery(Education.FIND_BY_FACULTY, Education.class);
        query.setParameter("faculty", "%" + faculty + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Education> findBySchool(String school) {
        return findBySchool(school, null, null);
    }

    @Override
    public List<Education> findBySchool(String school, Integer start, Integer limit) {

        TypedQuery<Education> query = getEntityManager().createNamedQuery(Education.FIND_BY_SCHOOL, Education.class);
        query.setParameter("school", "%" + school + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Education> findByDegreeAndSchool(String degree, String school) {
        return findByDegreeAndSchool(degree, school, null, null);
    }

    @Override
    public List<Education> findByDegreeAndSchool(String degree, String school, Integer start, Integer limit) {

        TypedQuery<Education> query = getEntityManager().createNamedQuery(Education.FIND_BY_DEGREE_AND_SCHOOL, Education.class);
        query.setParameter("degree", "%" + degree + "%");
        query.setParameter("school", "%" + school + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Education> findByFacultyAndSchool(String faculty, String school) {
        return findByFacultyAndSchool(faculty, school, null, null);
    }

    @Override
    public List<Education> findByFacultyAndSchool(String faculty, String school, Integer start, Integer limit) {

        TypedQuery<Education> query = getEntityManager().createNamedQuery(Education.FIND_BY_FACULTY_AND_SCHOOL, Education.class);
        query.setParameter("faculty", "%" + faculty + "%");
        query.setParameter("school", "%" + school + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Education> findByKeyword(String keyword) {
        return findByKeyword(keyword, null, null);
    }

    @Override
    public List<Education> findByKeyword(String keyword, Integer start, Integer limit) {

        TypedQuery<Education> query = getEntityManager().createNamedQuery(Education.FIND_BY_KEYWORD, Education.class);
        query.setParameter("keyword", "%" + keyword + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Education> findByEmployee(Employee employee) {
        return findByEmployee(employee, null, null);
    }

    @Override
    public List<Education> findByEmployee(Employee employee, Integer start, Integer limit) {

        TypedQuery<Education> query = getEntityManager().createNamedQuery(Education.FIND_BY_EMPLOYEE, Education.class);
        query.setParameter("employee", employee);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Education> findByEmployeeEagerly(Employee employee) {
        return findByEmployeeEagerly(employee, null, null);
    }

    @Override
    public List<Education> findByEmployeeEagerly(Employee employee, Integer start, Integer limit) {

        TypedQuery<Education> query = getEntityManager().createNamedQuery(Education.FIND_BY_EMPLOYEE_EAGERLY, Education.class);
        query.setParameter("employee", employee);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Education> findByEmployeeAndKeyword(Employee employee, String keyword) {
        return findByEmployeeAndKeyword(employee, keyword, null, null);
    }

    @Override
    public List<Education> findByEmployeeAndKeyword(Employee employee, String keyword, Integer start, Integer limit) {

        TypedQuery<Education> query = getEntityManager().createNamedQuery(Education.FIND_BY_EMPLOYEE_AND_KEYWORD, Education.class);
        query.setParameter("employee", employee);
        query.setParameter("keyword", "%" + keyword + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Long countByEmployee(Employee employee) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Education.COUNT_BY_EMPLOYEE, Long.class);
        query.setParameter("employee", employee);
        return query.getSingleResult();
    }

    @Override
    public Integer deleteByDegree(String degree) {

        Query query = getEntityManager().createNamedQuery(Education.DELETE_BY_DEGREE);
        query.setParameter("degree", degree);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteByFaculty(String faculty) {

        Query query = getEntityManager().createNamedQuery(Education.DELETE_BY_FACULTY);
        query.setParameter("faculty", faculty);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteBySchool(String school) {

        Query query = getEntityManager().createNamedQuery(Education.DELETE_BY_SCHOOL);
        query.setParameter("school", school);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteByDegreeAndSchool(String degree, String school) {

        Query query = getEntityManager().createNamedQuery(Education.DELETE_BY_DEGREE_AND_SCHOOL);
        query.setParameter("degree", degree);
        query.setParameter("school", school);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteByFacultyAndSchool(String faculty, String school) {

        Query query = getEntityManager().createNamedQuery(Education.DELETE_BY_FACULTY_AND_SCHOOL);
        query.setParameter("faculty", faculty);
        query.setParameter("school", school);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteByEmployee(Employee employee) {

        // select educations for given employee that will be deleted in the following query
        List<Education> educations = findByEmployee(employee);

        return deleteByEducations(educations);
    }

    @Override
    public Integer deleteByEducations(List<Education> educations) {

        Integer deletedCount = 0;
        for(Education education : educations) {
            getEntityManager().remove(education);
            deletedCount++;
        }
        return deletedCount;

        /* Query query = getEntityManager().createNamedQuery(Education.DELETE_BY_EDUCATIONS);
            query.setParameter("educations", educations);
            return query.executeUpdate();
         */
    }

    @Override
    public List<Education> findByMultipleCriteria(List<String> degrees, List<String> faculties, List<String> schools, List<Employee> employees) {
        return findByMultipleCriteria(degrees, faculties, schools, employees, null, null);
    }

    @Override
    public List<Education> findByMultipleCriteria(List<String> degrees, List<String> faculties, List<String> schools, List<Employee> employees, Integer start, Integer limit) {
        return findByMultipleCriteria(false, degrees, false, faculties, false, schools, employees, false, start, limit);
    }

    @Override
    public List<Education> findByMultipleCriteria(List<String> keywords, List<Employee> employees) {
        return findByMultipleCriteria(keywords, employees, null, null);
    }

    @Override
    public List<Education> findByMultipleCriteria(List<String> keywords, List<Employee> employees, Integer start, Integer limit) {
        return findByMultipleCriteria(true, keywords, true, keywords, true, keywords, employees, false, start, limit);
    }

    @Override
    public List<Education> findByMultipleCriteriaEagerly(List<String> degrees, List<String> faculties, List<String> schools, List<Employee> employees) {
        return findByMultipleCriteriaEagerly(degrees, faculties, schools, employees, null, null);
    }

    @Override
    public List<Education> findByMultipleCriteriaEagerly(List<String> degrees, List<String> faculties, List<String> schools, List<Employee> employees, Integer start, Integer limit) {
        return findByMultipleCriteria(false, degrees, false, faculties, false, schools, employees, true, start, limit);
    }

    @Override
    public List<Education> findByMultipleCriteriaEagerly(List<String> keywords, List<Employee> employees) {
        return findByMultipleCriteriaEagerly(keywords, employees, null, null);
    }

    @Override
    public List<Education> findByMultipleCriteriaEagerly(List<String> keywords, List<Employee> employees, Integer start, Integer limit) {
        return findByMultipleCriteria(true, keywords, true, keywords, true, keywords, employees, true, start, limit);
    }

    private List<Education> findByMultipleCriteria(Boolean orWithDegrees, List<String> degrees,
                                                   Boolean orWithFaculties, List<String> faculties,
                                                   Boolean orWithSchools, List<String> schools,
                                                   List<Employee> employees, Boolean eagerly, Integer start, Integer limit ) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Education> criteriaQuery = criteriaBuilder.createQuery(Education.class);
        // FROM
        Root<Education> education = criteriaQuery.from(Education.class);
        // SELECT
        criteriaQuery.select(education).distinct(true);

        // INNER JOIN-s
        Join<Education, Employee> employee = null;

        // WHERE PREDICATES
        List<Predicate> predicates = new ArrayList<>();
        List<Predicate> orPredicates = new ArrayList<>();

        if(degrees != null && degrees.size() > 0) {

            List<Predicate> orDegreePredicates = new ArrayList<>();

            for(String degree : degrees) {
                orDegreePredicates.add( criteriaBuilder.like(education.get(Education_.degree), "%" + degree + "%") );
            }

            if(orWithDegrees) {
                orPredicates.add( criteriaBuilder.or(orDegreePredicates.toArray(new Predicate[] {})) );
            } else {
                predicates.add( criteriaBuilder.or(orDegreePredicates.toArray(new Predicate[] {})) );
            }
        }

        if(faculties != null && faculties.size() > 0) {

            List<Predicate> orFacultyPredicates = new ArrayList<>();

            for(String faculty : faculties) {
                orFacultyPredicates.add( criteriaBuilder.like(education.get(Education_.faculty), "%" + faculty + "%") );
            }

            if(orWithFaculties) {
                orPredicates.add( criteriaBuilder.or(orFacultyPredicates.toArray(new Predicate[] {})) );
            } else {
                predicates.add( criteriaBuilder.or(orFacultyPredicates.toArray(new Predicate[] {})) );
            }
        }

        if(schools != null && schools.size() > 0) {

            List<Predicate> orSchoolPredicates = new ArrayList<>();

            for(String school : schools) {
                orSchoolPredicates.add( criteriaBuilder.like(education.get(Education_.school), "%" + school + "%") );
            }

            if(orWithSchools) {
                orPredicates.add( criteriaBuilder.or(orSchoolPredicates.toArray(new Predicate[] {})) );
            } else {
                predicates.add( criteriaBuilder.or(orSchoolPredicates.toArray(new Predicate[] {})) );
            }
        }

        if(orPredicates.size() > 0)
            predicates.add( criteriaBuilder.or(orPredicates.toArray(new Predicate[] {})) );

        if(employees != null && employees.size() > 0) {

            if(employee == null) employee = education.join(Education_.educatedEmployees);

            predicates.add( employee.in(employees) );

            if(eagerly) {
                // then fetch associated collection of entities
                education.fetch("educatedEmployees", JoinType.INNER);
            }
        } else if(eagerly) {
            // then left fetch associated collection of entities
            education.fetch("educatedEmployees", JoinType.LEFT);
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[]{}));

        TypedQuery<Education> query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }
}