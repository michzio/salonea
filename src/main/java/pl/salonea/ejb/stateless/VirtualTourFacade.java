package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.VirtualTourFacadeInterface;
import pl.salonea.entities.*;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michzio on 08/08/2015.
 */
@Stateless
@LocalBean
public class VirtualTourFacade extends AbstractFacade<VirtualTour> implements VirtualTourFacadeInterface.Local, VirtualTourFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public VirtualTourFacade() {
        super(VirtualTour.class);
    }


    @Override
    public List<VirtualTour> findByFileName(String fileName) {
        return findByFileName(fileName, null, null);
    }

    @Override
    public List<VirtualTour> findByFileName(String fileName, Integer start, Integer offset) {

        TypedQuery<VirtualTour> query = getEntityManager().createNamedQuery(VirtualTour.FIND_BY_FILE_NAME, VirtualTour.class);
        query.setParameter("file_name", "%" + fileName + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<VirtualTour> findByDescription(String description) {
        return findByDescription(description, null, null);
    }

    @Override
    public List<VirtualTour> findByDescription(String description, Integer start, Integer offset) {

        TypedQuery<VirtualTour> query = getEntityManager().createNamedQuery(VirtualTour.FIND_BY_DESCRIPTION, VirtualTour.class);
        query.setParameter("description", "%" + description + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<VirtualTour> findByFileNameAndDescription(String fileName, String description) {
        return findByFileNameAndDescription(fileName, description, null, null);
    }

    @Override
    public List<VirtualTour> findByFileNameAndDescription(String fileName, String description, Integer start, Integer offset) {

        TypedQuery<VirtualTour> query = getEntityManager().createNamedQuery(VirtualTour.FIND_BY_FILE_NAME_AND_DESCRIPTION, VirtualTour.class);
        query.setParameter("file_name", "%" + fileName + "%");
        query.setParameter("description", "%" + description + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<VirtualTour> findByKeyword(String keyword) {
        return findByKeyword(keyword, null, null);
    }

    @Override
    public List<VirtualTour> findByKeyword(String keyword, Integer start, Integer offset) {

        TypedQuery<VirtualTour> query = getEntityManager().createNamedQuery(VirtualTour.FIND_BY_KEYWORD, VirtualTour.class);
        query.setParameter("keyword", "%" + keyword + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<VirtualTour> findByTagName(String tagName) {
        return findByTagName(tagName, null, null);
    }

    @Override
    public List<VirtualTour> findByTagName(String tagName, Integer start, Integer offset) {

        TypedQuery<VirtualTour> query = getEntityManager().createNamedQuery(VirtualTour.FIND_BY_TAG_NAME, VirtualTour.class);
        query.setParameter("tag_name", "%" + tagName + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<VirtualTour> findByAnyTagNames(List<String> tagNames) {
        return findByAnyTagNames(tagNames, null, null);
    }

    @Override
    public List<VirtualTour> findByAnyTagNames(List<String> tagNames, Integer start, Integer offset) {

        TypedQuery<VirtualTour> query = getEntityManager().createNamedQuery(VirtualTour.FIND_BY_ANY_TAG_NAMES, VirtualTour.class);
        query.setParameter("tag_names", tagNames);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<VirtualTour> findByAllTags(List<Tag> tags) {
        return findByAllTags(tags, null, null);
    }

    @Override
    public List<VirtualTour> findByAllTags(List<Tag> tags, Integer start, Integer offset) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<VirtualTour> criteriaQuery = criteriaBuilder.createQuery(VirtualTour.class);
        // FROM
        Root<VirtualTour> virtualTour = criteriaQuery.from(VirtualTour.class);
        // SELECT
        criteriaQuery.select(virtualTour);

        List<Predicate> predicates = new ArrayList<>();

        for(Tag tag : tags) {

            predicates.add( criteriaBuilder.isMember( tag,  virtualTour.get(VirtualTour_.tags)) );
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] { }));

        TypedQuery<VirtualTour> query = getEntityManager().createQuery(criteriaQuery);
        return query.getResultList();
    }

    @Override
    public List<VirtualTour> findByAllTagNames(List<String> tagNames) {
        return findByAllTagNames(tagNames, null, null);
    }

    @Override
    public List<VirtualTour> findByAllTagNames(List<String> tagNames, Integer start, Integer offset) {

        TypedQuery<VirtualTour> query = getEntityManager().createNamedQuery(VirtualTour.FIND_BY_ALL_TAG_NAMES, VirtualTour.class);
        query.setParameter("tag_names", tagNames);
        query.setParameter("tag_count",  Long.valueOf(tagNames.size()));
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<VirtualTour> findByKeywordIncludingTags(String keyword) {
        return findByKeywordIncludingTags(keyword, null, null);
    }

    @Override
    public List<VirtualTour> findByKeywordIncludingTags(String keyword, Integer start, Integer offset) {

        TypedQuery<VirtualTour> query = getEntityManager().createNamedQuery(VirtualTour.FIND_BY_KEYWORD_INCLUDING_TAGS, VirtualTour.class);
        query.setParameter("keyword", keyword);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<VirtualTour> findByServicePoint(ServicePoint servicePoint) {
        return findByServicePoint(servicePoint, null, null);
    }

    @Override
    public List<VirtualTour> findByServicePoint(ServicePoint servicePoint, Integer start, Integer offset) {

        TypedQuery<VirtualTour> query = getEntityManager().createNamedQuery(VirtualTour.FIND_BY_SERVICE_POINT, VirtualTour.class);
        query.setParameter("service_point", servicePoint);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<VirtualTour> findByProvider(Provider provider) {
        return findByProvider(provider, null, null);
    }

    @Override
    public List<VirtualTour> findByProvider(Provider provider, Integer start, Integer offset) {

        TypedQuery<VirtualTour> query = getEntityManager().createNamedQuery(VirtualTour.FIND_BY_PROVIDER, VirtualTour.class);
        query.setParameter("provider", provider);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<VirtualTour> findByCorporation(Corporation corporation) {
        return findByCorporation(corporation, null, null);
    }

    @Override
    public List<VirtualTour> findByCorporation(Corporation corporation, Integer start, Integer offset) {

        TypedQuery<VirtualTour> query = getEntityManager().createNamedQuery(VirtualTour.FIND_BY_CORPORATION, VirtualTour.class);
        query.setParameter("corporation", corporation);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<VirtualTour> findByMultipleCriteria(List<String> keywords, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations) {
        return findByMultipleCriteria(true, keywords, true,  keywords, true, keywords,  servicePoints, providers, corporations);
    }

    @Override
    public List<VirtualTour> findByMultipleCriteria(List<String> keywords, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations) {
        return findByMultipleCriteria(true, keywords, true,  keywords, false,  tagNames, servicePoints, providers, corporations);
    }

    @Override
    public List<VirtualTour> findByMultipleCriteria(List<String> fileNames, List<String> descriptions, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations) {
        return findByMultipleCriteria(false, fileNames, false, descriptions, false, tagNames, servicePoints, providers, corporations);
    }

    private List<VirtualTour> findByMultipleCriteria(Boolean orWithFileNames, List<String> fileNames, Boolean orWithDescriptions,
                                                           List<String> descriptions, Boolean orWithTagNames,
                                                           List<String> tagNames, List<ServicePoint> servicePoints,
                                                           List<Provider> providers, List<Corporation> corporations) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<VirtualTour> criteriaQuery = criteriaBuilder.createQuery(VirtualTour.class);
        // FROM
        Root<VirtualTour> virtualTour = criteriaQuery.from(VirtualTour.class);
        // SELECT
        criteriaQuery.select(virtualTour).distinct(true);

        // INNER JOIN-s
        Join<VirtualTour, Tag> tag = null;
        Join<VirtualTour, ServicePoint> servicePoint = null;
        Join<ServicePoint, Provider> provider = null;

        // WHERE PREDICATES
        List<Predicate> predicates = new ArrayList<>();
        List<Predicate> orPredicates = new ArrayList<>();

        if(fileNames != null && fileNames.size() > 0) {

            List<Predicate> orFileNamePredicates = new ArrayList<>();

            for(String fileName : fileNames) {
                orFileNamePredicates.add( criteriaBuilder.like(virtualTour.get(VirtualTour_.fileName), "%" + fileName + "%") );
            }

            if(orWithFileNames) {
                orPredicates.add( criteriaBuilder.or(orFileNamePredicates.toArray(new Predicate[] {})) );
            } else {
                predicates.add( criteriaBuilder.or(orFileNamePredicates.toArray(new Predicate[]{})) );
            }
        }

        if(descriptions != null && descriptions.size() > 0) {

            List<Predicate> orDescriptionPredicates = new ArrayList<>();

            for(String description : descriptions) {
                orDescriptionPredicates.add( criteriaBuilder.like(virtualTour.get(VirtualTour_.description), "%" + description + "%") );
            }

            if(orWithDescriptions) {
                orPredicates.add( criteriaBuilder.or(orDescriptionPredicates.toArray(new Predicate[] {})) );
            } else {
                predicates.add( criteriaBuilder.or(orDescriptionPredicates.toArray(new Predicate[] {})) );
            }
        }

        if(tagNames != null && tagNames.size() > 0) {

            if(tag == null) tag = virtualTour.join(VirtualTour_.tags);

            // it will match virtualTours with ANY specified tag name
            List<Predicate> orTagNamePredicates = new ArrayList<>();

            for(String tagName : tagNames) {
                orTagNamePredicates.add( criteriaBuilder.like(tag.get(Tag_.tagName), "%" + tagName + "%") );
            }

            if(orWithTagNames) {
                orPredicates.add( criteriaBuilder.or(orTagNamePredicates.toArray(new Predicate[] {})) );
            } else {
                predicates.add( criteriaBuilder.or(orTagNamePredicates.toArray(new Predicate[] {})) );
            }
        }

        if(orPredicates.size() > 0)
            predicates.add( criteriaBuilder.or(orPredicates.toArray(new Predicate[]{})) );

        if(servicePoints != null && servicePoints.size() > 0) {

            predicates.add( virtualTour.get(VirtualTour_.servicePoint).in(servicePoints) );
        }

        if(providers != null && providers.size() > 0) {

            if(servicePoint == null) servicePoint = virtualTour.join(VirtualTour_.servicePoint);
            if(provider == null) provider = servicePoint.join(ServicePoint_.provider);

            predicates.add( provider.in(providers) );
        }

        if(corporations != null && corporations.size() > 0) {

            if(servicePoint == null) servicePoint = virtualTour.join(VirtualTour_.servicePoint);
            if(provider == null) provider = servicePoint.join(ServicePoint_.provider);

            predicates.add( provider.get(Provider_.corporation).in(corporations) );
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] { }));

        TypedQuery<VirtualTour> query = getEntityManager().createQuery(criteriaQuery);
        return query.getResultList();
    }
}
