package pl.salonea.ejb.interfaces;

import pl.salonea.entities.ServicePointPhoto;
import pl.salonea.entities.Tag;
import pl.salonea.entities.VirtualTour;

import java.util.List;

/**
 * Created by michzio on 06/08/2015.
 */
public interface TagFacadeInterface extends AbstractFacadeInterface<Tag> {

    // concrete interface
    List<Tag> findByTagName(Tag tagName);
    List<Tag> findByTagName(Tag tagName, Integer start, Integer offset);
    List<Tag> findByServicePointPhoto(ServicePointPhoto servicePointPhoto);
    List<Tag> findByServicePointPhoto(ServicePointPhoto servicePointPhoto, Integer start, Integer offset);
    List<Tag> findByServicePointPhotoAndTagName(ServicePointPhoto servicePointPhoto, String tagName);
    List<Tag> findByServicePointPhotoAndTagName(ServicePointPhoto servicePointPhoto, String tagName, Integer start, Integer offset);
    List<Tag> findByVirtualTour(VirtualTour virtualTour);
    List<Tag> findByVirtualTour(VirtualTour virtualTour, Integer start, Integer offset);
    List<Tag> findByVirtualTourAndTagName(VirtualTour virtualTour, String tagName);
    List<Tag> findByVirtualTourAndTagName(VirtualTour virtualTour, String tagName, Integer start, Integer offset);
    List<Tag> findByMultipleCriteria(List<String> tagNames, List<ServicePointPhoto> pointPhotos, List<VirtualTour> virtualTours);

    @javax.ejb.Local
    interface Local extends TagFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends TagFacadeInterface { }
}
