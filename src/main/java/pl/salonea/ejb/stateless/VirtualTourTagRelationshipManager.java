package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.VirtualTourTagRelationshipManagerInterface;
import pl.salonea.entities.Tag;
import pl.salonea.entities.VirtualTour;
import pl.salonea.jaxrs.exceptions.NotFoundException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Created by michzio on 26/02/2016.
 */
@Stateless
@LocalBean
public class VirtualTourTagRelationshipManager
        implements VirtualTourTagRelationshipManagerInterface.Local, VirtualTourTagRelationshipManagerInterface.Remote {

    @Inject
    private VirtualTourFacade virtualTourFacade;
    @Inject
    private TagFacade tagFacade;


    @Override
    public void addTagToVirtualTour(Long tagId, Long tourId) throws NotFoundException {

        VirtualTour virtualTour = virtualTourFacade.find(tourId);
        if(virtualTour == null)
            throw new NotFoundException("Virtual Tour entity could not be found for id " + tourId);
        Tag tag = tagFacade.find(tagId);
        if(tag == null)
            throw new NotFoundException("Tag entity could not be found for id " + tagId);

        virtualTour.getTags().add(tag);
        tag.getTaggedVirtualTours().add(virtualTour);
    }

    @Override
    public void removeTagFromVirtualTour(Long tagId, Long tourId) throws NotFoundException {

        VirtualTour virtualTour = virtualTourFacade.find(tourId);
        if(virtualTour == null)
            throw new NotFoundException("Virtual Tour entity could not be found for id " + tourId);
        Tag tag = tagFacade.find(tagId);
        if(tag == null)
            throw new NotFoundException("Tag entity could not be found for id " + tagId);

        virtualTour.getTags().remove(tag);
        tag.getTaggedVirtualTours().remove(virtualTour);
    }
}
