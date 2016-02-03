package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.ServiceCategoryFacadeInterface;
import pl.salonea.entities.ServiceCategory;
import pl.salonea.entities.ServiceCategory_;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.ArrayList;
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
    public ServiceCategory update(ServiceCategory serviceCategory, Boolean retainTransientFields) {

        if(retainTransientFields) {
            // keep current collection attributes of resource (and other marked @XmlTransient)
            ServiceCategory currentServiceCategory = findByIdEagerly(serviceCategory.getCategoryId());
            if(currentServiceCategory != null) {
                serviceCategory.setSubCategories(currentServiceCategory.getSubCategories());
                serviceCategory.setServices(currentServiceCategory.getServices());
            }
        }
        return update(serviceCategory);
    }

    @Override
    public List<ServiceCategory> findAllEagerly() {
        return findAllEagerly(null, null);
    }

    @Override
    public List<ServiceCategory> findAllEagerly(Integer start, Integer limit) {

        TypedQuery<ServiceCategory> query = getEntityManager().createNamedQuery(ServiceCategory.FIND_ALL_EAGERLY, ServiceCategory.class);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public ServiceCategory findByIdEagerly(Integer categoryId) {

        TypedQuery<ServiceCategory> query = getEntityManager().createNamedQuery(ServiceCategory.FIND_BY_ID_EAGERLY, ServiceCategory.class);
        query.setParameter("categoryId", categoryId);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    @Override
    public List<ServiceCategory> findByName(String name) {
        return findByName(name, null, null);
    }

    @Override
    public List<ServiceCategory> findByName(String name, Integer start, Integer limit) {

        TypedQuery<ServiceCategory> query = getEntityManager().createNamedQuery(ServiceCategory.FIND_BY_NAME, ServiceCategory.class);
        query.setParameter("name", "%" + name + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServiceCategory> findByDescription(String description) {
        return findByDescription(description, null, null);
    }

    @Override
    public List<ServiceCategory> findByDescription(String description, Integer start, Integer limit) {

        TypedQuery<ServiceCategory> query = getEntityManager().createNamedQuery(ServiceCategory.FIND_BY_DESCRIPTION, ServiceCategory.class);
        query.setParameter("description", "%" + description + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServiceCategory> findByKeyword(String keyword) {
        return findByKeyword(keyword, null, null);
    }

    @Override
    public List<ServiceCategory> findByKeyword(String keyword, Integer start, Integer limit) {

        TypedQuery<ServiceCategory> query = getEntityManager().createNamedQuery(ServiceCategory.FIND_BY_KEYWORD, ServiceCategory.class);
        query.setParameter("keyword", "%" + keyword + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServiceCategory> findBySuperCategory(ServiceCategory superCategory) {
        return findBySuperCategory(superCategory, null, null);
    }

    @Override
    public List<ServiceCategory> findBySuperCategory(ServiceCategory superCategory, Integer start, Integer limit) {

        TypedQuery<ServiceCategory> query = getEntityManager().createNamedQuery(ServiceCategory.FIND_BY_SUPER_CATEGORY, ServiceCategory.class);
        query.setParameter("super_category", superCategory);
        if(start != null && limit != null)  {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServiceCategory> findBySuperCategoryEagerly(ServiceCategory superCategory) {
        return findBySuperCategoryEagerly(superCategory, null, null);
    }

    @Override
    public List<ServiceCategory> findBySuperCategoryEagerly(ServiceCategory superCategory, Integer start, Integer limit) {

        TypedQuery<ServiceCategory> query = getEntityManager().createNamedQuery(ServiceCategory.FIND_BY_SUPER_CATEGORY_EAGERLY, ServiceCategory.class);
        query.setParameter("super_category", superCategory);
        if(start != null && limit != null)  {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServiceCategory> findByNameInSuperCategory(ServiceCategory superCategory, String name) {
        return findByNameInSuperCategory(superCategory, name, null, null);
    }

    @Override
    public List<ServiceCategory> findByNameInSuperCategory(ServiceCategory superCategory, String name, Integer start, Integer limit) {

        TypedQuery<ServiceCategory> query = getEntityManager().createNamedQuery(ServiceCategory.FIND_BY_NAME_IN_SUPER_CATEGORY, ServiceCategory.class);
        query.setParameter("name", "%" + name + "%");
        query.setParameter("super_category", superCategory);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServiceCategory> findByDescriptionInSuperCategory(ServiceCategory superCategory, String description) {
        return findByDescriptionInSuperCategory(superCategory, description, null, null);
    }

    @Override
    public List<ServiceCategory> findByDescriptionInSuperCategory(ServiceCategory superCategory, String description, Integer start, Integer limit) {

        TypedQuery<ServiceCategory> query = getEntityManager().createNamedQuery(ServiceCategory.FIND_BY_DESCRIPTION_IN_SUPER_CATEGORY, ServiceCategory.class);
        query.setParameter("description", "%" + description + "%");
        query.setParameter("super_category", superCategory);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServiceCategory> findByKeywordInSuperCategory(ServiceCategory superCategory, String keyword) {
        return findByKeywordInSuperCategory(superCategory, keyword, null, null);
    }

    @Override
    public List<ServiceCategory> findByKeywordInSuperCategory(ServiceCategory superCategory, String keyword, Integer start, Integer limit) {

        TypedQuery<ServiceCategory> query = getEntityManager().createNamedQuery(ServiceCategory.FIND_BY_KEYWORD_IN_SUPER_CATEGORY, ServiceCategory.class);
        query.setParameter("keyword", "%" + keyword + "%");
        query.setParameter("super_category", superCategory);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Long countBySuperCategory(ServiceCategory superCategory) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(ServiceCategory.COUNT_BY_SUPER_CATEGORY, Long.class);
        query.setParameter("super_category", superCategory);
        return query.getSingleResult();
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

    @Override
    public List<ServiceCategory> findByMultipleCriteria(List<String> names, List<String> descriptions, List<ServiceCategory> superCategories) {
        return findByMultipleCriteria(names, descriptions, superCategories, null, null);
    }

    @Override
    public List<ServiceCategory> findByMultipleCriteria(List<String> names, List<String> descriptions, List<ServiceCategory> superCategories, Integer start, Integer limit) {
        return findByMultipleCriteria(false, names, false, descriptions, superCategories, false, start, limit );
    }

    @Override
    public List<ServiceCategory> findByMultipleCriteria(List<String> keywords, List<ServiceCategory> superCategories) {
        return findByMultipleCriteria(keywords, superCategories, null, null);
    }

    @Override
    public List<ServiceCategory> findByMultipleCriteria(List<String> keywords, List<ServiceCategory> superCategories, Integer start, Integer limit) {
        return findByMultipleCriteria(true, keywords, true, keywords, superCategories, false, start, limit);
    }

    @Override
    public List<ServiceCategory> findByMultipleCriteriaEagerly(List<String> names, List<String> descriptions, List<ServiceCategory> superCategories) {
        return findByMultipleCriteriaEagerly(names, descriptions, superCategories, null, null);
    }

    @Override
    public List<ServiceCategory> findByMultipleCriteriaEagerly(List<String> names, List<String> descriptions, List<ServiceCategory> superCategories, Integer start, Integer limit) {
        return findByMultipleCriteria(false, names, false, descriptions, superCategories, true, start, limit);
    }

    @Override
    public List<ServiceCategory> findByMultipleCriteriaEagerly(List<String> keywords, List<ServiceCategory> superCategories) {
        return findByMultipleCriteriaEagerly(keywords, superCategories, null, null);
    }

    @Override
    public List<ServiceCategory> findByMultipleCriteriaEagerly(List<String> keywords, List<ServiceCategory> superCategories, Integer start, Integer limit) {
        return findByMultipleCriteria(true, keywords, true, keywords, superCategories, true, start, limit);
    }

    private List<ServiceCategory> findByMultipleCriteria(Boolean orWithNames, List<String> names,
                                                         Boolean orWithDescriptions, List<String> descriptions,
                                                         List<ServiceCategory> superCategories, Boolean eagerly,
                                                         Integer start, Integer limit) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ServiceCategory> criteriaQuery = criteriaBuilder.createQuery(ServiceCategory.class);
        // FROM
        Root<ServiceCategory> serviceCategory = criteriaQuery.from(ServiceCategory.class);
        // SELECT
        criteriaQuery.select(serviceCategory);

        // INNER JOIN-s
        // ------------

        // WHERE PREDICATES
        List<Predicate> predicates = new ArrayList<>();
        List<Predicate> orPredicates = new ArrayList<>();

        if(names != null && names.size() > 0) {

            List<Predicate> orNamePredicates = new ArrayList<>();

            for(String name : names) {
                orNamePredicates.add( criteriaBuilder.like(serviceCategory.get(ServiceCategory_.categoryName), "%" + name + "%") );
            }

            if(orWithNames) {
                orPredicates.add( criteriaBuilder.or(orNamePredicates.toArray(new Predicate[] {})) );
            } else {
                predicates.add( criteriaBuilder.or(orNamePredicates.toArray(new Predicate[] {})) );
            }
        }

        if(descriptions != null && descriptions.size() > 0) {

            List<Predicate> orDescriptionPredicates = new ArrayList<>();

            for(String description : descriptions) {
                orDescriptionPredicates.add( criteriaBuilder.like(serviceCategory.get(ServiceCategory_.description), "%" + description + "%") );
            }

            if(orWithDescriptions) {
                orPredicates.add( criteriaBuilder.or(orDescriptionPredicates.toArray(new Predicate[] {})) );
            } else {
                predicates.add( criteriaBuilder.or(orDescriptionPredicates.toArray(new Predicate[] {})) );
            }
        }

        if(orPredicates.size() > 0)
            predicates.add( criteriaBuilder.or(orPredicates.toArray(new Predicate[] {})) );

        if(superCategories != null && superCategories.size() > 0) {

            predicates.add( serviceCategory.get(ServiceCategory_.superCategory).in(superCategories) );
        }

        if(eagerly) {
            serviceCategory.fetch("subCategories", JoinType.LEFT);
            serviceCategory.fetch("services", JoinType.LEFT);
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] {}));

        TypedQuery<ServiceCategory> query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }
}
