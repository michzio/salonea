package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.SkillFacadeInterface;
import pl.salonea.entities.Employee;
import pl.salonea.entities.Skill;
import pl.salonea.entities.Skill_;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.ArrayList;
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
    public Skill update(Skill skill, Boolean retainTransientFields) {

        if(retainTransientFields) {
            // keep current collection attributes of resource (and other marked @XmlTransient)
            Skill currentSkill = findByIdEagerly(skill.getSkillId());
            if(currentSkill != null) {
                skill.setSkilledEmployees(currentSkill.getSkilledEmployees());
            }
        }
        return update(skill);
    }

    @Override
    public List<Skill> findAllEagerly() {
        return findAllEagerly(null, null);
    }

    @Override
    public List<Skill> findAllEagerly(Integer start, Integer limit) {

        TypedQuery<Skill> query = getEntityManager().createNamedQuery(Skill.FIND_ALL_EAGERLY, Skill.class);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Skill findByIdEagerly(Integer skillId) {

        TypedQuery<Skill> query = getEntityManager().createNamedQuery(Skill.FIND_BY_ID_EAGERLY, Skill.class);
        query.setParameter("skillId", skillId);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
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
    public List<Skill> findByEmployeeEagerly(Employee employee) {
        return findByEmployeeEagerly(employee, null, null);
    }

    @Override
    public List<Skill> findByEmployeeEagerly(Employee employee, Integer start, Integer limit) {

        TypedQuery<Skill> query = getEntityManager().createNamedQuery(Skill.FIND_BY_EMPLOYEE_EAGERLY, Skill.class);
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
    public Long countByEmployee(Employee employee) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Skill.COUNT_BY_EMPLOYEE, Long.class);
        query.setParameter("employee", employee);
        return query.getSingleResult();
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

    @Override
    public List<Skill> findByMultipleCriteria(List<String> skillNames, List<String> descriptions, List<Employee> employees) {
        return findByMultipleCriteria(skillNames, descriptions, employees, null, null);
    }

    @Override
    public List<Skill> findByMultipleCriteria(List<String> skillNames, List<String> descriptions, List<Employee> employees, Integer start, Integer limit) {
        return findByMultipleCriteria(false, skillNames, false, descriptions, employees, false, start, limit);
    }

    @Override
    public List<Skill> findByMultipleCriteria(List<String> keywords, List<Employee> employees) {
        return findByMultipleCriteria(keywords, employees, null, null);
    }

    @Override
    public List<Skill> findByMultipleCriteria(List<String> keywords, List<Employee> employees, Integer start, Integer limit) {
        return findByMultipleCriteria(true, keywords, true, keywords, employees, false, start, limit);
    }

    @Override
    public List<Skill> findByMultipleCriteriaEagerly(List<String> skillNames, List<String> descriptions, List<Employee> employees) {
        return findByMultipleCriteriaEagerly(skillNames, descriptions, employees, null, null);
    }

    @Override
    public List<Skill> findByMultipleCriteriaEagerly(List<String> skillNames, List<String> descriptions, List<Employee> employees, Integer start, Integer limit) {
        return findByMultipleCriteria(false, skillNames, false, descriptions, employees, true, start, limit);
    }

    @Override
    public List<Skill> findByMultipleCriteriaEagerly(List<String> keywords, List<Employee> employees) {
        return findByMultipleCriteriaEagerly(keywords, employees, null, null);
    }

    @Override
    public List<Skill> findByMultipleCriteriaEagerly(List<String> keywords, List<Employee> employees, Integer start, Integer limit) {
        return findByMultipleCriteria(true, keywords, true, keywords, employees, true, start, limit);
    }


    private List<Skill> findByMultipleCriteria(Boolean orWithSkillNames, List<String> skillNames,
                                               Boolean orWithDescriptions, List<String> descriptions,
                                               List<Employee> employees, Boolean eagerly, Integer start, Integer limit) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Skill> criteriaQuery = criteriaBuilder.createQuery(Skill.class);
        // FROM
        Root<Skill> skill = criteriaQuery.from(Skill.class);
        // SELECT
        criteriaQuery.select(skill).distinct(true);

        // INNER JOIN-s
        Join<Skill, Employee> employee = null;

        // WHERE PREDICATES
        List<Predicate> predicates = new ArrayList<>();
        List<Predicate> orPredicates = new ArrayList<>();

        if(skillNames != null && skillNames.size() > 0) {

            List<Predicate> orSkillNamePredicates = new ArrayList<>();

            for(String skillName : skillNames) {
                orSkillNamePredicates.add( criteriaBuilder.like(skill.get(Skill_.skillName), "%" + skillName + "%") );
            }

            if(orWithSkillNames) {
                orPredicates.add( criteriaBuilder.or(orSkillNamePredicates.toArray(new Predicate[] {})) );
            } else {
                predicates.add( criteriaBuilder.or(orSkillNamePredicates.toArray(new Predicate[] {})) );
            }
        }

        if(descriptions != null && descriptions.size() > 0) {

            List<Predicate> orDescriptionPredicates = new ArrayList<>();

            for(String description : descriptions) {
                orDescriptionPredicates.add( criteriaBuilder.like(skill.get(Skill_.description), "%" + description + "%") );
            }

            if(orWithDescriptions) {
                orPredicates.add( criteriaBuilder.or(orDescriptionPredicates.toArray(new Predicate[] {})) );
            } else {
                predicates.add( criteriaBuilder.or(orDescriptionPredicates.toArray(new Predicate[] {})) );
            }
        }

        if(orPredicates.size() > 0)
            predicates.add( criteriaBuilder.or(orPredicates.toArray(new Predicate[]{})) );

        if(employees != null && employees.size() > 0) {

            if(employee == null) employee = skill.join(Skill_.skilledEmployees);

            predicates.add( employee.in(employees) );

            if(eagerly) {
                // then fetch associated collection of entities
                skill.fetch("skilledEmployees", JoinType.INNER);
            }
        } else if(eagerly) {
            // then left fetch associated collection of entities
            skill.fetch("skilledEmployees", JoinType.LEFT);
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[]{}));

        TypedQuery<Skill> query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }
}