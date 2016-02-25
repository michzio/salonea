package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.ServicePointPhotoTagRelationshipManagerInterface;
import pl.salonea.entities.ServicePointPhoto;
import pl.salonea.entities.Tag;
import pl.salonea.jaxrs.exceptions.NotFoundException;

import javax.inject.Inject;

/**
 * Created by michzio on 25/02/2016.
 */
public class ServicePointPhotoTagRelationshipManager
        implements ServicePointPhotoTagRelationshipManagerInterface.Local, ServicePointPhotoTagRelationshipManagerInterface.Remote {

        @Inject
        private ServicePointPhotoFacade photoFacade;
        @Inject
        private TagFacade tagFacade;


        @Override
        public void addTagToServicePointPhoto(Long tagId, Long photoId) throws NotFoundException {

                ServicePointPhoto photo = photoFacade.find(photoId);
                if(photo == null)
                        throw new NotFoundException("Service Point Photo entity could not be found for id " + photoId);
                Tag tag = tagFacade.find(tagId);
                if(tag == null)
                        throw new NotFoundException("Tag entity could not be found for id " + tagId);

                photo.getTags().add(tag);
                tag.getTaggedPhotos().add(photo);
        }

        @Override
        public void removeTagFromServicePointPhoto(Long tagId, Long photoId) throws NotFoundException {

                ServicePointPhoto photo = photoFacade.find(photoId);
                if(photo == null)
                        throw new NotFoundException("Service Point Photo entity could not be found for id " + photoId);
                Tag tag = tagFacade.find(tagId);
                if(tag == null)
                        throw new NotFoundException("Tag entity could not be found for id " + tagId);

                photo.getTags().remove(tag);
                tag.getTaggedPhotos().remove(photo);
        }
}
