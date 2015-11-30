package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.TagFacadeInterface;
import pl.salonea.entities.ServicePointPhoto;
import pl.salonea.entities.Tag;
import pl.salonea.entities.*;
import pl.salonea.entities.VirtualTour;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michzio on 06/08/2015.
 */
@Stateless
@LocalBean
public class TagFacade extends AbstractFacade<Tag> implements TagFacadeInterface.Local, TagFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public TagFacade() {
        super(Tag.class);
    }

    @Override
    public List<Tag> findAllEagerly() {
        return findAllEagerly(null, null);
    }

    @Override
    public List<Tag> findAllEagerly(Integer start, Integer limit) {

        TypedQuery<Tag> query = getEntityManager().createNamedQuery(Tag.FIND_ALL_EAGERLY, Tag.class);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Tag findByIdEagerly(Long tagId) {

        TypedQuery<Tag> query = getEntityManager().createNamedQuery(Tag.FIND_BY_ID_EAGERLY, Tag.class);
        query.setParameter("tag_id", tagId);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    @Override
    public List<Tag> findByTagName(String tagName) {
        return findByTagName(tagName, null, null);
    }

    @Override
    public List<Tag> findByTagName(String tagName, Integer start, Integer limit) {

        TypedQuery<Tag> query = getEntityManager().createNamedQuery(Tag.FIND_BY_TAG_NAME, Tag.class);
        query.setParameter("tag_name", "%" + tagName + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Tag> findByServicePointPhoto(ServicePointPhoto servicePointPhoto) {
        return findByServicePointPhoto(servicePointPhoto, null, null);
    }

    @Override
    public List<Tag> findByServicePointPhoto(ServicePointPhoto servicePointPhoto, Integer start, Integer limit) {

        TypedQuery<Tag> query = getEntityManager().createNamedQuery(Tag.FIND_BY_SERVICE_POINT_PHOTO, Tag.class);
        query.setParameter("service_point_photo", servicePointPhoto);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Tag> findByServicePointPhotoEagerly(ServicePointPhoto servicePointPhoto) {
        return findByServicePointPhotoEagerly(servicePointPhoto, null, null);
    }

    @Override
    public List<Tag> findByServicePointPhotoEagerly(ServicePointPhoto servicePointPhoto, Integer start, Integer limit) {

        TypedQuery<Tag> query = getEntityManager().createNamedQuery(Tag.FIND_BY_SERVICE_POINT_PHOTO_EAGERLY, Tag.class);
        query.setParameter("service_point_photo", servicePointPhoto);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Tag> findByServicePointPhotoAndTagName(ServicePointPhoto servicePointPhoto, String tagName) {
        return findByServicePointPhotoAndTagName(servicePointPhoto, tagName, null, null);
    }

    @Override
    public List<Tag> findByServicePointPhotoAndTagName(ServicePointPhoto servicePointPhoto, String tagName, Integer start, Integer limit) {

        TypedQuery<Tag> query = getEntityManager().createNamedQuery(Tag.FIND_BY_SERVICE_POINT_PHOTO_AND_TAG_NAME, Tag.class);
        query.setParameter("service_point_photo", servicePointPhoto);
        query.setParameter("tag_name", "%" + tagName + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Tag> findByVirtualTour(VirtualTour virtualTour) {
        return findByVirtualTour(virtualTour, null, null);
    }

    @Override
    public List<Tag> findByVirtualTour(VirtualTour virtualTour, Integer start, Integer limit) {

        TypedQuery<Tag> query = getEntityManager().createNamedQuery(Tag.FIND_BY_VIRTUAL_TOUR, Tag.class);
        query.setParameter("virtual_tour", virtualTour);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Tag> findByVirtualTourEagerly(VirtualTour virtualTour) {
        return findByVirtualTourEagerly(virtualTour, null, null);
    }

    @Override
    public List<Tag> findByVirtualTourEagerly(VirtualTour virtualTour, Integer start, Integer limit) {

        TypedQuery<Tag> query = getEntityManager().createNamedQuery(Tag.FIND_BY_VIRTUAL_TOUR_EAGERLY, Tag.class);
        query.setParameter("virtual_tour", virtualTour);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Tag> findByVirtualTourAndTagName(VirtualTour virtualTour, String tagName) {

        return findByVirtualTourAndTagName(virtualTour, tagName, null, null);
    }

    @Override
    public List<Tag> findByVirtualTourAndTagName(VirtualTour virtualTour, String tagName, Integer start, Integer limit) {

        TypedQuery<Tag> query = getEntityManager().createNamedQuery(Tag.FIND_BY_VIRTUAL_TOUR_AND_TAG_NAME, Tag.class);
        query.setParameter("virtual_tour", virtualTour);
        query.setParameter("tag_name", "%" + tagName + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Long countByServicePointPhoto(ServicePointPhoto servicePointPhoto) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Tag.COUNT_BY_SERVICE_POINT_PHOTO, Long.class);
        query.setParameter("service_point_photo", servicePointPhoto);
        return query.getSingleResult();
    }

    @Override
    public Long countByVirtualTour(VirtualTour virtualTour) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Tag.COUNT_BY_VIRTUAL_TOUR, Long.class);
        query.setParameter("virtual_tour", virtualTour);
        return query.getSingleResult();
    }

    @Override
    public List<Tag> findByMultipleCriteria(List<String> tagNames, List<ServicePointPhoto> pointPhotos, List<VirtualTour> virtualTours) {
        return findByMultipleCriteria(tagNames, pointPhotos, virtualTours, null, null);
    }

    @Override
    public List<Tag> findByMultipleCriteria(List<String> tagNames, List<ServicePointPhoto> pointPhotos, List<VirtualTour> virtualTours, Integer start, Integer limit) {
        return findByMultipleCriteria(tagNames, pointPhotos, virtualTours, false, start, limit);
    }

    @Override
    public List<Tag> findByMultipleCriteriaEagerly(List<String> tagNames, List<ServicePointPhoto> pointPhotos, List<VirtualTour> virtualTours) {
        return findByMultipleCriteriaEagerly(tagNames, pointPhotos, virtualTours, null, null);
    }

    @Override
    public List<Tag> findByMultipleCriteriaEagerly(List<String> tagNames, List<ServicePointPhoto> pointPhotos, List<VirtualTour> virtualTours, Integer start, Integer limit) {
        return findByMultipleCriteria(tagNames, pointPhotos, virtualTours, true, start, limit);
    }


    private List<Tag> findByMultipleCriteria(List<String> tagNames, List<ServicePointPhoto> pointPhotos, List<VirtualTour> virtualTours, Boolean eagerly, Integer start, Integer limit) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Tag> criteriaQuery = criteriaBuilder.createQuery(Tag.class);
        // FROM
        Root<Tag> tag = criteriaQuery.from(Tag.class);
        // SELECT
        criteriaQuery.select(tag).distinct(true);

        // WHERE PREDICATES
        List<Predicate> predicates = new ArrayList<>();

        if(tagNames != null && tagNames.size() > 0) {

            // it will match tags with ANY specified tag name
            List<Predicate> orTagNamePredicates = new ArrayList<>();

            for(String tagName : tagNames) {
                orTagNamePredicates.add( criteriaBuilder.like(tag.get(Tag_.tagName), "%" + tagName + "%") );
            }

            predicates.add( criteriaBuilder.or(orTagNamePredicates.toArray(new Predicate[] {})) );
        }

        if(pointPhotos != null && pointPhotos.size() > 0) {

            // it will match tags with ANY specified photo
            List<Predicate> orPhotoPredicates = new ArrayList<>();

            for(ServicePointPhoto photo : pointPhotos) {
                orPhotoPredicates.add( criteriaBuilder.isMember(photo, tag.get(Tag_.taggedPhotos)) );
            }

            predicates.add( criteriaBuilder.or(orPhotoPredicates.toArray(new Predicate[] {})) );
        }

        if(virtualTours != null && virtualTours.size() > 0) {

            // it will match tags with ANY specified virtual tour
            List<Predicate> orVirtualTourPredicates = new ArrayList<>();

            for(VirtualTour virtualTour : virtualTours) {
                orVirtualTourPredicates.add( criteriaBuilder.isMember(virtualTour, tag.get(Tag_.taggedVirtualTours)) );
            }

            predicates.add( criteriaBuilder.or(orVirtualTourPredicates.toArray(new Predicate[] {})) );
        }

        if(eagerly) {
            // then left fetch associated collections of entities
            tag.fetch("taggedPhotos", JoinType.LEFT);
            tag.fetch("taggedVirtualTours", JoinType.LEFT);
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] { }));

        TypedQuery<Tag> query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }
}
