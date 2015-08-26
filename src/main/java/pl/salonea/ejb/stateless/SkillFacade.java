package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.SkillFacadeInterface;
import pl.salonea.entities.Employee;
import pl.salonea.entities.Skill;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by michzio on 14/08/2015.
 */
@Stateless
@LocalBean
public class SkillFacade extends AbstractFacade<Skill> implements SkillFacadeInterface.Local, SkillFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public SkillFacade() {
        super(Skill.class);
    }


    @Override
    public List<Skill> findByName(String skillName) {
        return findByName(skillName, null, null);
    }

    @Override
    public List<Skill> findByName(String skillName, Integer start, Integer limit) {

        TypedQuery<Skill> query = getEntityManager().createNamedQuery(Skill.FIND_BY_NAME, Skill.class);
        query.setParameter("skill_name", "%" + skillName + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Skill> findByDescription(String description) {
        return findByDescription(description, null, null);
    }

    @Override
    public List<Skill> findByDescription(String description, Integer start, Integer limit) {

        TypedQuery<Skill> query = getEntityManager().createNamedQuery(Skill.FIND_BY_DESCRIPTION, Skill.class);
        query.setParameter("description", "%" + description + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Skill> findByKeyword(String keyword) {
        return findByKeyword(keyword, null, null);
    }

    @Override
    public List<Skill> findByKeyword(String keyword, Integer start, Integer limit) {

        TypedQuery<Skill> query = getEntityManager().createNamedQuery(Skill.FIND_BY_KEYWORD, Skill.class);
        query.setParameter("keyword", "%" + keyword + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Skill> findByEmployee(Employee employee) {
        return findByEmployee(employee, null, null);
    }

    @Override
    public List<Skill> findByEmployee(Employee employee, Integer start, Integer limit) {

        TypedQuery<Skill> query = getEntityManager().createNamedQuery(Skill.FIND_BY_EMPLOYEE, Skill.class);
        query.setParameter("employee", employee);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Skill> findByEmployeeAndKeyword(Employee employee, String keyword) {
        return findByEmployeeAndKeyword(employee, keyword, null, null);
    }

    @Override
    public List<Skill> findByEmployeeAndKeyword(Employee employee, String keyword, Integer start, Integer limit) {

        TypedQuery<Skill> query = getEntityManager().createNamedQuery(Skill.FIND_BY_EMPLOYEE_AND_KEYWORD, Skill.class);
        query.setParameter("employee", employee);
        query.setParameter("keyword", "%" + keyword + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Integer deleteByName(String skillName) {

        Query query = getEntityManager().createNamedQuery(Skill.DELETE_BY_NAME);
        query.setParameter("skill_name", skillName);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteByEmployee(Employee employee) {

        // select skills for given employee that will be deleted in the following query
        List<Skill> skills = findByEmployee(employee);

        return deleteBySkills(skills);
    }

    @Override
    public Integer deleteBySkills(List<Skill> skills) {

        Query query = getEntityManager().createNamedQuery(Skill.DELETE_BY_SKILLS);
        query.setParameter("skills", skills);
        return query.executeUpdate();
    }
}
