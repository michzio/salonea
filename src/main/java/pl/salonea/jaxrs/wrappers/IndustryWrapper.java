package pl.salonea.jaxrs.wrappers;

import pl.salonea.entities.Industry;
import pl.salonea.entities.Provider;

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
@XmlRootElement(name = "industry")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class IndustryWrapper {

    private Industry industry;
    private Set<Provider> providers;

    // default no-args constructor
    public IndustryWrapper() { }

    public IndustryWrapper(Industry industry) {
        this.industry = industry;
        this.providers = industry.getProviders();
    }

    public static List<IndustryWrapper> wrap(List<Industry> industries) {

        List<IndustryWrapper> wrappedIndustries = new ArrayList<>();

        for (Industry industry : industries)
            wrappedIndustries.add(new IndustryWrapper(industry));

        return wrappedIndustries;
    }

    @XmlElement(name = "entity", nillable = true)
    public Industry getIndustry() {
        return industry;
    }

    public void setIndustry(Industry industry) {
        this.industry = industry;
    }

    @XmlElement(name = "providers", nillable = true)
    public Set<Provider> getProviders() {
        return providers;
    }

    public void setProviders(Set<Provider> providers) {
        this.providers = providers;
    }
}
