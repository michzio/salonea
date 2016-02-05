package pl.salonea.jaxrs.wrappers;

import pl.salonea.entities.Service;
import pl.salonea.entities.ServiceCategory;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by michzio on 04/02/2016.
 */
@XmlRootElement(name = "service-category")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ServiceCategoryWrapper {

    private ServiceCategory serviceCategory;
    private Set<ServiceCategory> subCategories;
    private Set<Service> services;

    // default no-args constructor
    public ServiceCategoryWrapper() { }

    public ServiceCategoryWrapper(ServiceCategory serviceCategory) {
        this.serviceCategory = serviceCategory;
        this.subCategories = serviceCategory.getSubCategories();
        this.services = serviceCategory.getServices();
    }

    public static List<ServiceCategoryWrapper> wrap(List<ServiceCategory> serviceCategories) {

        List<ServiceCategoryWrapper> wrappedServiceCategories = new ArrayList<>();

        for(ServiceCategory serviceCategory : serviceCategories)
            wrappedServiceCategories.add(new ServiceCategoryWrapper(serviceCategory));

        return wrappedServiceCategories;
    }

    @XmlElement(name = "entity", nillable = true)
    public ServiceCategory getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(ServiceCategory serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    @XmlElement(name = "subcategories", nillable = true)
    public Set<ServiceCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(Set<ServiceCategory> subCategories) {
        this.subCategories = subCategories;
    }

    @XmlElement(name = "services", nillable = true)
    public Set<Service> getServices() {
        return services;
    }

    public void setServices(Set<Service> services) {
        this.services = services;
    }
}
