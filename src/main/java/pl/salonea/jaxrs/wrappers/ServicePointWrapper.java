package pl.salonea.jaxrs.wrappers;

import pl.salonea.entities.ServicePoint;
import pl.salonea.entities.ServicePointPhoto;
import pl.salonea.entities.VirtualTour;
import pl.salonea.entities.WorkStation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by michzio on 21/09/2015.
 */
@XmlRootElement(name = "service-point")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ServicePointWrapper {

    private ServicePoint servicePoint;
    private Set<ServicePointPhoto> photos;
    private Set<VirtualTour> virtualTours;
    private Set<WorkStation> workStations;

    // default no-args constructor
    public ServicePointWrapper() { }

    public ServicePointWrapper(ServicePoint servicePoint) {
        this.servicePoint = servicePoint;
        this.photos = servicePoint.getPhotos();
        this.virtualTours = servicePoint.getVirtualTours();
        this.workStations = servicePoint.getWorkStations();
    }

    public static List<ServicePointWrapper> wrap(List<ServicePoint> servicePoints) {

        List<ServicePointWrapper> wrappedServicePoints = new ArrayList<>();

        for(ServicePoint servicePoint : servicePoints)
            wrappedServicePoints.add(new ServicePointWrapper(servicePoint));

        return wrappedServicePoints;
    }

    @XmlElement(name = "entity", nillable = true)
    public ServicePoint getServicePoint() {
        return servicePoint;
    }

    public void setServicePoint(ServicePoint servicePoint) {
        this.servicePoint = servicePoint;
    }

    @XmlElement(name = "service-point-photos", nillable = true)
    public Set<ServicePointPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(Set<ServicePointPhoto> photos) {
        this.photos = photos;
    }

    @XmlElement(name = "virtual-tours", nillable = true)
    public Set<VirtualTour> getVirtualTours() {
        return virtualTours;
    }

    public void setVirtualTours(Set<VirtualTour> virtualTours) {
        this.virtualTours = virtualTours;
    }

    @XmlElement(name = "work-stations", nillable = true)
    public Set<WorkStation> getWorkStations() {
        return workStations;
    }

    public void setWorkStations(Set<WorkStation> workStations) {
        this.workStations = workStations;
    }
}
