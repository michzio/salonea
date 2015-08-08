package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.TagFacadeInterface;
import pl.salonea.entities.ServicePointPhoto;
import pl.salonea.entities.Tag;
import pl.salonea.entities.Tag_;
import pl.salonea.entities.VirtualTour;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
    public List<Tag> findByTagName(String tagName) {
        return findByTagName(tagName, null, null);
    }

    @Override
    public List<Tag> findByTagName(String tagName, Integer start, Integer offset) {

        TypedQuery<Tag> query = getEntityManager().createNamedQuery(Tag.FIND_BY_TAG_NAME, Tag.class);
        query.setParameter("tag_name", "%" + tagName + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Tag> findByServicePointPhoto(ServicePointPhoto servicePointPhoto) {
        return findByServicePointPhoto(servicePointPhoto, null, null);
    }

    @Override
    public List<Tag> findByServicePointPhoto(ServicePointPhoto servicePointPhoto, Integer start, Integer offset) {

        TypedQuery<Tag> query = getEntityManager().createNamedQuery(Tag.FIND_BY_SERVICE_POINT_PHOTO, Tag.class);
        query.setParameter("service_point_photo", servicePointPhoto);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Tag> findByServicePointPhotoAndTagName(ServicePointPhoto servicePointPhoto, String tagName) {
        return findByServicePointPhotoAndTagName(servicePointPhoto, tagName, null, null);
    }

    @Override
    public List<Tag> findByServicePointPhotoAndTagName(ServicePointPhoto servicePointPhoto, String tagName, Integer start, Integer offset) {

        TypedQuery<Tag> query = getEntityManager().createNamedQuery(Tag.FIND_BY_SERVICE_POINT_PHOTO_AND_TAG_NAME, Tag.class);
        query.setParameter("service_point_photo", servicePointPhoto);
        query.setParameter("tag_name", "%" + tagName + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Tag> findByVirtualTour(VirtualTour virtualTour) {
        return findByVirtualTour(virtualTour, null, null);
    }

    @Override
    public List<Tag> findByVirtualTour(VirtualTour virtualTour, Integer start, Integer offset) {

        TypedQuery<Tag> query = getEntityManager().createNamedQuery(Tag.FIND_BY_VIRTUAL_TOUR, Tag.class);
        query.setParameter("virtual_tour", virtualTour);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Tag> findByVirtualTourAndTagName(VirtualTour virtualTour, String tagName) {

        return findByVirtualTourAndTagName(virtualTour, tagName, null, null);
    }

    @Override
    public List<Tag> findByVirtualTourAndTagName(VirtualTour virtualTour, String tagName, Integer start, Integer offset) {

        TypedQuery<Tag> query = getEntityManager().createNamedQuery(Tag.FIND_BY_VIRTUAL_TOUR_AND_TAG_NAME, Tag.class);
        query.setParameter("virtual_tour", virtualTour);
        query.setParameter("tag_name", "%" + tagName + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Tag> findByMultipleCriteria(List<String> tagNames, List<ServicePointPhoto> pointPhotos, List<VirtualTour> virtualTours) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Tag> criteriaQuery = criteriaBuilder.createQuery(Tag.class);
        // FROM
        Root<Tag> tag = criteriaQuery.from(Tag.class);
        // SELECT
        criteriaQuery.select(tag);

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

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] { }));

        TypedQuery<Tag> query = getEntityManager().createQuery(criteriaQuery);
        return query.getResultList();
    }
}
