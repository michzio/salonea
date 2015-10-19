package pl.salonea.jaxrs.wrappers;

import pl.salonea.entities.Corporation;
import pl.salonea.entities.Provider;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by michzio on 19/10/2015.
 */
@XmlRootElement(name = "corporation")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class CorporationWrapper {

    private Corporation corporation;
    private Set<Provider> providers;

    // default no-args constructor
    public CorporationWrapper() { }

    public CorporationWrapper(Corporation corporation) {
        this.corporation = corporation;
        this.providers = corporation.getProviders();
    }

    public static List<CorporationWrapper> wrap(List<Corporation> corporations) {

        List<CorporationWrapper> wrappedCorporations = new ArrayList<>();

        for(Corporation corporation : corporations)
            wrappedCorporations.add(new CorporationWrapper(corporation));

        return wrappedCorporations;
    }

    @XmlElement(name = "entity", nillable = true)
    public Corporation getCorporation() {
        return corporation;
    }

    public void setCorporation(Corporation corporation) {
        this.corporation = corporation;
    }

    @XmlElement(name = "providers", nillable = true)
    public Set<Provider> getProviders() {
        return providers;
    }

    public void setProviders(Set<Provider> providers) {
        this.providers = providers;
    }
}
