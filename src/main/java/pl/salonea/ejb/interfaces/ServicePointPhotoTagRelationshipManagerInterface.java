package pl.salonea.ejb.interfaces;

/**
 * Created by michzio on 25/02/2016.
 */
public interface ServicePointPhotoTagRelationshipManagerInterface {

    void addTagToServicePointPhoto(Long tagId, Long photoId);
    void removeTagFromServicePointPhoto(Long tagId, Long photoId);

    @javax.ejb.Remote
    interface Remote extends ServicePointPhotoTagRelationshipManagerInterface { }

    @javax.ejb.Local
    interface Local extends ServicePointPhotoTagRelationshipManagerInterface { }
}
