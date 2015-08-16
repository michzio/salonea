package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.EducationFacadeInterface;
import pl.salonea.entities.Education;
import pl.salonea.entities.Employee;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
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
    public List<Education> findByDegree(String degree) {
        return findByDegree(degree, null, null);
    }

    @Override
    public List<Education> findByDegree(String degree, Integer start, Integer offset) {

        TypedQuery<Education> query = getEntityManager().createNamedQuery(Education.FIND_BY_DEGREE, Education.class);
        query.setParameter("degree", "%" + degree + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Education> findBySchool(String school) {
        return findBySchool(school, null, null);
    }

    @Override
    public List<Education> findBySchool(String school, Integer start, Integer offset) {

        TypedQuery<Education> query = getEntityManager().createNamedQuery(Education.FIND_BY_SCHOOL, Education.class);
        query.setParameter("school", "%" + school + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Education> findByDegreeAndSchool(String degree, String school) {
        return findByDegreeAndSchool(degree, school, null, null);
    }

    @Override
    public List<Education> findByDegreeAndSchool(String degree, String school, Integer start, Integer offset) {

        TypedQuery<Education> query = getEntityManager().createNamedQuery(Education.FIND_BY_DEGREE_AND_SCHOOL, Education.class);
        query.setParameter("degree", "%" + degree + "%");
        query.setParameter("school", "%" + school + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Education> findByKeyword(String keyword) {
        return findByKeyword(keyword, null, null);
    }

    @Override
    public List<Education> findByKeyword(String keyword, Integer start, Integer offset) {

        TypedQuery<Education> query = getEntityManager().createNamedQuery(Education.FIND_BY_KEYWORD, Education.class);
        query.setParameter("keyword", "%" + keyword + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Education> findByEmployee(Employee employee) {
        return findByEmployee(employee, null, null);
    }

    @Override
    public List<Education> findByEmployee(Employee employee, Integer start, Integer offset) {

        TypedQuery<Education> query = getEntityManager().createNamedQuery(Education.FIND_BY_EMPLOYEE, Education.class);
        query.setParameter("employee", employee);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Education> findByEmployeeAndKeyword(Employee employee, String keyword) {
        return findByEmployeeAndKeyword(employee, keyword, null, null);
    }

    @Override
    public List<Education> findByEmployeeAndKeyword(Employee employee, String keyword, Integer start, Integer offset) {

        TypedQuery<Education> query = getEntityManager().createNamedQuery(Education.FIND_BY_EMPLOYEE_AND_KEYWORD, Education.class);
        query.setParameter("employee", employee);
        query.setParameter("keyword", "%" + keyword + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public Integer deleteByDegree(String degree) {

        Query query = getEntityManager().createNamedQuery(Education.DELETE_BY_DEGREE);
        query.setParameter("degree", degree);
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
    public Integer deleteByEmployee(Employee employee) {

        // select educations for given employee that will be deleted in the following query
        List<Education> educations = findByEmployee(employee);

        return deleteByEducations(educations);
    }

    @Override
    public Integer deleteByEducations(List<Education> educations) {

        Query query = getEntityManager().createNamedQuery(Education.DELETE_BY_EDUCATIONS);
        query.setParameter("educations", educations);
        return query.executeUpdate();
    }
}
