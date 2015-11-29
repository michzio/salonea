package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.ServicePointPhotoFacadeInterface;
import pl.salonea.entities.*;
import pl.salonea.entities.idclass.ServicePointId;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michzio on 05/08/2015.
 */
@Stateless
@LocalBean
public class ServicePointPhotoFacade extends AbstractFacade<ServicePointPhoto>
        implements ServicePointPhotoFacadeInterface.Local, ServicePointPhotoFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public ServicePointPhotoFacade() {
        super(ServicePointPhoto.class);
    }

    @Override
    public ServicePointPhoto createForServicePoint(ServicePointId servicePointId, ServicePointPhoto photo) {

        ServicePoint foundServicePoint = getEntityManager().find(ServicePoint.class, servicePointId);
        photo.setServicePoint(foundServicePoint);

        return create(photo);
    }

    @Override
    public ServicePointPhoto updateWithServicePoint(ServicePointId servicePointId, ServicePointPhoto photo) {

        ServicePoint foundServicePoint = getEntityManager().find(ServicePoint.class, servicePointId);
        photo.setServicePoint(foundServicePoint);

        return update(photo);
    }

    @Override
    public List<ServicePointPhoto> findAllEagerly() {
        return findAllEagerly(null, null);
    }

    @Override
    public List<ServicePointPhoto> findAllEagerly(Integer start, Integer limit) {

        TypedQuery<ServicePointPhoto> query = getEntityManager().createNamedQuery(ServicePointPhoto.FIND_ALL_EAGERLY, ServicePointPhoto.class);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public ServicePointPhoto findByIdEagerly(Long photoId) {

        TypedQuery<ServicePointPhoto> query = getEntityManager().createNamedQuery(ServicePointPhoto.FIND_BY_ID_EAGERLY, ServicePointPhoto.class);
        query.setParameter("photo_id", photoId);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    @Override
    public List<ServicePointPhoto> findByFileName(String fileName) {
        return findByFileName(fileName, null, null);
    }

    @Override
    public List<ServicePointPhoto> findByFileName(String fileName, Integer start, Integer limit) {

        TypedQuery<ServicePointPhoto> query = getEntityManager().createNamedQuery(ServicePointPhoto.FIND_BY_FILE_NAME, ServicePointPhoto.class);
        query.setParameter("file_name", "%" + fileName + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePointPhoto> findByDescription(String description) {
        return findByDescription(description, null, null);
    }

    @Override
    public List<ServicePointPhoto> findByDescription(String description, Integer start, Integer limit) {

        TypedQuery<ServicePointPhoto> query = getEntityManager().createNamedQuery(ServicePointPhoto.FIND_BY_DESCRIPTION, ServicePointPhoto.class);
        query.setParameter("description", "%" + description + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePointPhoto> findByFileNameAndDescription(String fileName, String description) {
        return findByFileNameAndDescription(fileName, description, null, null);
    }

    @Override
    public List<ServicePointPhoto> findByFileNameAndDescription(String fileName, String description, Integer start, Integer limit) {

        TypedQuery<ServicePointPhoto> query = getEntityManager().createNamedQuery(ServicePointPhoto.FIND_BY_FILE_NAME_AND_DESCRIPTION, ServicePointPhoto.class);
        query.setParameter("file_name", "%" + fileName + "%");
        query.setParameter("description", "%" + description + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePointPhoto> findByKeyword(String keyword) {
        return findByKeyword(keyword, null, null);
    }

    @Override
    public List<ServicePointPhoto> findByKeyword(String keyword, Integer start, Integer limit) {

        TypedQuery<ServicePointPhoto> query = getEntityManager().createNamedQuery(ServicePointPhoto.FIND_BY_KEYWORD, ServicePointPhoto.class);
        query.setParameter("keyword", "%" + keyword + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePointPhoto> findByTagName(String tagName) {
        return findByTagName(tagName, null, null);
    }

    @Override
    public List<ServicePointPhoto> findByTagName(String tagName, Integer start, Integer limit) {

        TypedQuery<ServicePointPhoto> query = getEntityManager().createNamedQuery(ServicePointPhoto.FIND_BY_TAG_NAME, ServicePointPhoto.class);
        query.setParameter("tag_name", "%" + tagName + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePointPhoto> findByAnyTagNames(List<String> tagNames) {
        return findByAnyTagNames(tagNames, null, null);
    }

    @Override
    public List<ServicePointPhoto> findByAnyTagNames(List<String> tagNames, Integer start, Integer limit) {

        TypedQuery<ServicePointPhoto> query = getEntityManager().createNamedQuery(ServicePointPhoto.FIND_BY_ANY_TAG_NAMES, ServicePointPhoto.class);
        query.setParameter("tag_names", tagNames);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePointPhoto> findByAllTags(List<Tag> tags) {
        return findByAllTags(tags, null, null);
    }

    @Override
    public List<ServicePointPhoto> findByAllTags(List<Tag> tags, Integer start, Integer limit) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ServicePointPhoto> criteriaQuery = criteriaBuilder.createQuery(ServicePointPhoto.class);
        // FROM
        Root<ServicePointPhoto> photo = criteriaQuery.from(ServicePointPhoto.class);
        // SELECT
        criteriaQuery.select(photo);

        List<Predicate> predicates = new ArrayList<>();

        for(Tag tag : tags) {

            predicates.add( criteriaBuilder.isMember( tag,  photo.get(ServicePointPhoto_.tags)) );
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] { }));

        TypedQuery<ServicePointPhoto> query = getEntityManager().createQuery(criteriaQuery);
        return query.getResultList();
    }

    @Override
    public List<ServicePointPhoto> findByAllTagNames(List<String> tagNames) {
        return findByAllTagNames(tagNames, null, null);
    }

    @Override
    public List<ServicePointPhoto> findByAllTagNames(List<String> tagNames, Integer start, Integer limit) {

        TypedQuery<ServicePointPhoto> query = getEntityManager().createNamedQuery(ServicePointPhoto.FIND_BY_ALL_TAG_NAMES, ServicePointPhoto.class);
        query.setParameter("tag_names", tagNames);
        query.setParameter("tag_count",  Long.valueOf(tagNames.size()));
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePointPhoto> findByKeywordIncludingTags(String keyword) {
        return findByKeywordIncludingTags(keyword, null, null);
    }

    @Override
    public List<ServicePointPhoto> findByKeywordIncludingTags(String keyword, Integer start, Integer limit) {

        TypedQuery<ServicePointPhoto> query = getEntityManager().createNamedQuery(ServicePointPhoto.FIND_BY_KEYWORD_INCLUDING_TAGS, ServicePointPhoto.class);
        query.setParameter("keyword", "%" + keyword + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePointPhoto> findByServicePoint(ServicePoint servicePoint) {
        return findByServicePoint(servicePoint, null, null);
    }

    @Override
    public List<ServicePointPhoto> findByServicePoint(ServicePoint servicePoint, Integer start, Integer limit) {

        TypedQuery<ServicePointPhoto> query = getEntityManager().createNamedQuery(ServicePointPhoto.FIND_BY_SERVICE_POINT, ServicePointPhoto.class);
        query.setParameter("service_point", servicePoint);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePointPhoto> findByProvider(Provider provider) {
        return findByProvider(provider, null, null);
    }

    @Override
    public List<ServicePointPhoto> findByProvider(Provider provider, Integer start, Integer limit) {

        TypedQuery<ServicePointPhoto> query = getEntityManager().createNamedQuery(ServicePointPhoto.FIND_BY_PROVIDER, ServicePointPhoto.class);
        query.setParameter("provider", provider);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePointPhoto> findByCorporation(Corporation corporation) {
        return findByCorporation(corporation, null, null);
    }

    @Override
    public List<ServicePointPhoto> findByCorporation(Corporation corporation, Integer start, Integer limit) {

        TypedQuery<ServicePointPhoto> query = getEntityManager().createNamedQuery(ServicePointPhoto.FIND_BY_CORPORATION, ServicePointPhoto.class);
        query.setParameter("corporation", corporation);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePointPhoto> findByTag(Tag tag) {
        return findByTag(tag);
    }

    @Override
    public List<ServicePointPhoto> findByTag(Tag tag, Integer start, Integer limit) {

        TypedQuery<ServicePointPhoto> query = getEntityManager().createNamedQuery(ServicePointPhoto.FIND_BY_TAG, ServicePointPhoto.class);
        query.setParameter("tag", tag);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Long countByServicePoint(ServicePoint servicePoint) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(ServicePointPhoto.COUNT_BY_SERVICE_POINT, Long.class);
        query.setParameter("service_point", servicePoint);
        return query.getSingleResult();
    }

    @Override
    public Long countByProvider(Provider provider) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(ServicePointPhoto.COUNT_BY_PROVIDER, Long.class);
        query.setParameter("provider", provider);
        return query.getSingleResult();
    }

    @Override
    public Long countByCorporation(Corporation corporation) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(ServicePointPhoto.COUNT_BY_CORPORATION, Long.class);
        query.setParameter("corporation", corporation);
        return query.getSingleResult();
    }

    @Override
    public Long countByTag(Tag tag) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(ServicePointPhoto.COUNT_BY_TAG, Long.class);
        query.setParameter("tag", tag);
        return query.getSingleResult();
    }

    @Override
    public Integer deleteByServicePoint(ServicePoint servicePoint) {

        Query query = getEntityManager().createNamedQuery(ServicePointPhoto.DELETE_BY_SERVICE_POINT);
        query.setParameter("service_point", servicePoint);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteById(Long photoId) {

        Query query = getEntityManager().createNamedQuery(ServicePointPhoto.DELETE_BY_ID);
        query.setParameter("photo_id", photoId);
        return query.executeUpdate();
    }

    @Override
    public List<ServicePointPhoto> findByMultipleCriteria(List<String> keywords, List<ServicePoint> servicePoints,
                                                          List<Provider> providers, List<Corporation> corporations) {
        return findByMultipleCriteria(keywords, servicePoints, providers, corporations, null, null);
    }

    @Override
    public List<ServicePointPhoto> findByMultipleCriteria(List<String> keywords, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, Integer start, Integer limit) {

        return findByMultipleCriteria(true, keywords, true,  keywords, true, keywords,  servicePoints, providers, corporations, false, start, limit);
    }

    @Override
    public List<ServicePointPhoto> findByMultipleCriteria(List<String> keywords, List<String> tagNames,
                                                          List<ServicePoint> servicePoints, List<Provider> providers,
                                                          List<Corporation> corporations) {
        return findByMultipleCriteria(keywords, tagNames, servicePoints, providers, corporations, null, null);
    }

    @Override
    public List<ServicePointPhoto> findByMultipleCriteria(List<String> keywords, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, Integer start, Integer limit) {
        return findByMultipleCriteria(true, keywords, true,  keywords, false,  tagNames, servicePoints, providers, corporations, false, start, limit);
    }

    @Override
    public List<ServicePointPhoto> findByMultipleCriteria(List<String> fileNames, List<String> descriptions,
                                                          List<String> tagNames, List<ServicePoint> servicePoints,
                                                          List<Provider> providers, List<Corporation> corporations) {
        return findByMultipleCriteria(fileNames, descriptions, tagNames, servicePoints, providers, corporations, null, null);
    }

    @Override
    public List<ServicePointPhoto> findByMultipleCriteria(List<String> fileNames, List<String> descriptions, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, Integer start, Integer limit) {
        return findByMultipleCriteria(false, fileNames, false, descriptions, false, tagNames, servicePoints, providers, corporations, false, start, limit);
    }

    @Override
    public List<ServicePointPhoto> findByMultipleCriteriaEagerly(List<String> keywords, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations) {
        return findByMultipleCriteriaEagerly(keywords, servicePoints, providers, corporations, null, null);
    }

    @Override
    public List<ServicePointPhoto> findByMultipleCriteriaEagerly(List<String> keywords, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, Integer start, Integer limit) {
        return findByMultipleCriteria(true, keywords, true,  keywords, true, keywords,  servicePoints, providers, corporations, true, start, limit);
    }

    @Override
    public List<ServicePointPhoto> findByMultipleCriteriaEagerly(List<String> keywords, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations) {
        return findByMultipleCriteriaEagerly(keywords, tagNames, servicePoints, providers, corporations, null, null);
    }

    @Override
    public List<ServicePointPhoto> findByMultipleCriteriaEagerly(List<String> keywords, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, Integer start, Integer limit) {
        return findByMultipleCriteria(true, keywords, true,  keywords, false,  tagNames, servicePoints, providers, corporations, true, start, limit);
    }

    @Override
    public List<ServicePointPhoto> findByMultipleCriteriaEagerly(List<String> fileNames, List<String> descriptions, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations) {
        return findByMultipleCriteriaEagerly(fileNames, descriptions, tagNames, servicePoints, providers, corporations, null, null);
    }

    @Override
    public List<ServicePointPhoto> findByMultipleCriteriaEagerly(List<String> fileNames, List<String> descriptions, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, Integer start, Integer limit) {
        return findByMultipleCriteria(false, fileNames, false, descriptions, false, tagNames, servicePoints, providers, corporations, true, start, limit);
    }

    private List<ServicePointPhoto> findByMultipleCriteria(Boolean orWithFileNames, List<String> fileNames, Boolean orWithDescriptions,
                                                           List<String> descriptions, Boolean orWithTagNames,
                                                           List<String> tagNames, List<ServicePoint> servicePoints,
                                                           List<Provider> providers, List<Corporation> corporations,
                                                           Boolean eagerly, Integer start, Integer limit ) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ServicePointPhoto> criteriaQuery = criteriaBuilder.createQuery(ServicePointPhoto.class);
        // FROM
        Root<ServicePointPhoto> photo = criteriaQuery.from(ServicePointPhoto.class);
        // SELECT
        criteriaQuery.select(photo).distinct(true);

        // INNER JOIN-s
        Join<ServicePointPhoto, Tag> tag = null;
        Join<ServicePointPhoto, ServicePoint> servicePoint = null;
        Join<ServicePoint, Provider> provider = null;

        // WHERE PREDICATES
        List<Predicate> predicates = new ArrayList<>();
        List<Predicate> orPredicates = new ArrayList<>();

        if(fileNames != null && fileNames.size() > 0) {

            List<Predicate> orFileNamePredicates = new ArrayList<>();

            for(String fileName : fileNames) {
                orFileNamePredicates.add( criteriaBuilder.like(photo.get(ServicePointPhoto_.fileName), "%" + fileName + "%") );
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
                orDescriptionPredicates.add( criteriaBuilder.like(photo.get(ServicePointPhoto_.description), "%" + description + "%") );
            }

            if(orWithDescriptions) {
                orPredicates.add( criteriaBuilder.or(orDescriptionPredicates.toArray(new Predicate[] {})) );
            } else {
                predicates.add(criteriaBuilder.or(orDescriptionPredicates.toArray(new Predicate[]{})));
            }
        }

        if(tagNames != null && tagNames.size() > 0) {

            if(tag == null) tag = photo.join(ServicePointPhoto_.tags);

            // it will match photos with ANY specified tag name
            List<Predicate> orTagNamePredicates = new ArrayList<>();

            for(String tagName : tagNames) {
                orTagNamePredicates.add( criteriaBuilder.like(tag.get(Tag_.tagName), "%" + tagName + "%") );
            }

            if(orWithTagNames) {
                orPredicates.add( criteriaBuilder.or(orTagNamePredicates.toArray(new Predicate[] {})) );
            } else {
                predicates.add( criteriaBuilder.or(orTagNamePredicates.toArray(new Predicate[] {})) );
            }

            if(eagerly) {
                // then fetch associated collection of entities
                photo.fetch("tags", JoinType.INNER);
            }
        } else if(eagerly) {
            // then left fetch associated collection of entities
            photo.fetch("tags", JoinType.LEFT);
        }

        if(orPredicates.size() > 0)
            predicates.add( criteriaBuilder.or(orPredicates.toArray(new Predicate[]{})) );

        if(servicePoints != null && servicePoints.size() > 0) {

            predicates.add( photo.get(ServicePointPhoto_.servicePoint).in(servicePoints) );
        }

        if(providers != null && providers.size() > 0) {

            if(servicePoint == null) servicePoint = photo.join(ServicePointPhoto_.servicePoint);
            if(provider == null) provider = servicePoint.join(ServicePoint_.provider);

            predicates.add( provider.in(providers) );
        }

        if(corporations != null && corporations.size() > 0) {

            if(servicePoint == null) servicePoint = photo.join(ServicePointPhoto_.servicePoint);
            if(provider == null) provider = servicePoint.join(ServicePoint_.provider);

            predicates.add( provider.get(Provider_.corporation).in(corporations) );
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] { }));

        TypedQuery<ServicePointPhoto> query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

}
