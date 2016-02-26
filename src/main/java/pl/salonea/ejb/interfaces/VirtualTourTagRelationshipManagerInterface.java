package pl.salonea.ejb.interfaces;

/**
 * Created by michzio on 26/02/2016.
 */
public interface VirtualTourTagRelationshipManagerInterface {

    void addTagToVirtualTour(Long tagId, Long tourId);
    void removeTagFromVirtualTour(Long tagId, Long tourId);

    @javax.ejb.Remote
    interface Remote extends VirtualTourTagRelationshipManagerInterface { }

    @javax.ejb.Local
    interface Local extends VirtualTourTagRelationshipManagerInterface { }
}
