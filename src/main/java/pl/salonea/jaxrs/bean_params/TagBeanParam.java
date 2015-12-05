package pl.salonea.jaxrs.bean_params;

import pl.salonea.ejb.interfaces.VirtualTourFacadeInterface;
import pl.salonea.ejb.stateless.ServicePointPhotoFacade;
import pl.salonea.ejb.stateless.VirtualTourFacade;
import pl.salonea.entities.ServicePointPhoto;
import pl.salonea.entities.VirtualTour;
import pl.salonea.jaxrs.exceptions.NotFoundException;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michzio on 05/12/2015.
 */
public class TagBeanParam extends PaginationBeanParam {

    private @QueryParam("tagName") List<String> tagNames;
    private @QueryParam("servicePointPhotoId") List<Long> servicePointPhotoIds;
    private @QueryParam("virtualTourId") List<Long> virtualTourIds;

    @Inject
    private ServicePointPhotoFacade servicePointPhotoFacade;
    @Inject
    private VirtualTourFacade virtualTourFacade;

    public List<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(List<String> tagNames) {
        this.tagNames = tagNames;
    }

    public List<Long> getServicePointPhotoIds() {
        return servicePointPhotoIds;
    }

    public void setServicePointPhotoIds(List<Long> servicePointPhotoIds) {
        this.servicePointPhotoIds = servicePointPhotoIds;
    }

    public List<ServicePointPhoto> getServicePointPhotos() throws NotFoundException {
        if(getServicePointPhotoIds() != null && getServicePointPhotoIds().size() > 0) {
            final List<ServicePointPhoto> photos = servicePointPhotoFacade.find( new ArrayList<>(getServicePointPhotoIds()) );
            if(photos.size() != getServicePointPhotoIds().size()) throw new NotFoundException("Could not find service point photos for all provided ids.");
            return photos;
        }
        return null;
    }

    public List<Long> getVirtualTourIds() {
        return virtualTourIds;
    }

    public void setVirtualTourIds(List<Long> virtualTourIds) {
        this.virtualTourIds = virtualTourIds;
    }

    public List<VirtualTour> getVirtualTours() throws NotFoundException {
        if(getVirtualTourIds() != null && getVirtualTourIds().size() > 0) {
            final List<VirtualTour> virtualTours = virtualTourFacade.find( new ArrayList<>(getVirtualTourIds()) );
            if(virtualTours.size() != getVirtualTourIds().size()) throw new NotFoundException("Could not find virtual tours for all provided ids.");
            return virtualTours;
        }
        return null;
    }
}
