package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.SkillFacadeInterface;
import pl.salonea.entities.Employee;
import pl.salonea.entities.Skill;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
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
        return null;
    }

    @Override
    public List<Skill> findByName(String skillName, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Skill> findByDescription(String description) {
        return null;
    }

    @Override
    public List<Skill> findByDescription(String description, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Skill> findByKeyword(String keyword) {
        return null;
    }

    @Override
    public List<Skill> findByKeyword(String keyword, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Skill> findByEmployee(Employee employee) {
        return null;
    }

    @Override
    public List<Skill> findByEmployee(Employee employee, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Skill> findByEmployeeAndKeyword(Employee employee, String keyword) {
        return null;
    }

    @Override
    public List<Skill> findByEmployeeAndKeyword(Employee employee, String keyword, Integer start, Integer offset) {
        return null;
    }

    @Override
    public Integer deleteByName(String skillName) {
        return null;
    }

    @Override
    public Integer deleteByEmployee(Employee employee) {
        return null;
    }
}
