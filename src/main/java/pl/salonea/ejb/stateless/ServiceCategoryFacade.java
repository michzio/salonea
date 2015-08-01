package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.ServiceCategoryFacadeInterface;
import pl.salonea.entities.ServiceCategory;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by michzio on 31/07/2015.
 */
@Stateless
@LocalBean
public class ServiceCategoryFacade extends AbstractFacade<ServiceCategory> implements ServiceCategoryFacadeInterface.Local, ServiceCategoryFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public ServiceCategoryFacade() {
        super(ServiceCategory.class);
    }

    @Override
    public List<ServiceCategory> findByName(String name) {
        return findByName(name, null, null);
    }

    @Override
    public List<ServiceCategory> findByName(String name, Integer start, Integer offset) {

        TypedQuery<ServiceCategory> query = getEntityManager().createNamedQuery(ServiceCategory.FIND_BY_NAME, ServiceCategory.class);
        query.setParameter("name", "%" + name + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ServiceCategory> findByDescription(String description) {
        return findByDescription(description, null, null);
    }

    @Override
    public List<ServiceCategory> findByDescription(String description, Integer start, Integer offset) {

        TypedQuery<ServiceCategory> query = getEntityManager().createNamedQuery(ServiceCategory.FIND_BY_DESCRIPTION, ServiceCategory.class);
        query.setParameter("description", "%" + description + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ServiceCategory> findByKeyword(String keyword) {
        return findByKeyword(keyword, null, null);
    }

    @Override
    public List<ServiceCategory> findByKeyword(String keyword, Integer start, Integer offset) {

        TypedQuery<ServiceCategory> query = getEntityManager().createNamedQuery(ServiceCategory.FIND_BY_KEYWORD, ServiceCategory.class);
        query.setParameter("keyword", "%" + keyword + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ServiceCategory> findBySuperCategory(ServiceCategory superCategory) {
        return findBySuperCategory(superCategory, null, null);
    }

    @Override
    public List<ServiceCategory> findBySuperCategory(ServiceCategory superCategory, Integer start, Integer offset) {

        TypedQuery<ServiceCategory> query = getEntityManager().createNamedQuery(ServiceCategory.FIND_BY_SUPER_CATEGORY, ServiceCategory.class);
        query.setParameter("super_category", superCategory);
        if(start != null && offset != null)  {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ServiceCategory> findByKeywordInCategory(ServiceCategory superCategory, String keyword) {
        return findByKeywordInCategory(superCategory, keyword, null, null);
    }

    @Override
    public List<ServiceCategory> findByKeywordInCategory(ServiceCategory superCategory, String keyword, Integer start, Integer offset) {

        TypedQuery<ServiceCategory> query = getEntityManager().createNamedQuery(ServiceCategory.FIND_BY_KEYWORD_IN_CATEGORY, ServiceCategory.class);
        query.setParameter("keyword", "%" + keyword + "%");
        query.setParameter("super_category", superCategory);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public Integer deleteByName(String name) {

        Query query = getEntityManager().createNamedQuery(ServiceCategory.DELETE_BY_NAME);
        query.setParameter("name", name);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteBySuperCategory(ServiceCategory superCategory) {

        Query query = getEntityManager().createNamedQuery(ServiceCategory.DELETE_BY_SUPER_CATEGORY);
        query.setParameter("super_category", superCategory);
        return query.executeUpdate();
    }
}
