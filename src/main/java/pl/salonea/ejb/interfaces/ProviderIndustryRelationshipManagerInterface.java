package pl.salonea.ejb.interfaces;

/**
 * Created by michzio on 15/11/2015.
 */
public interface ProviderIndustryRelationshipManagerInterface {

    void addProviderToIndustry(Long providerId, Long industryId);
    void removeProviderFromIndustry(Long providerId, Long industryId);

    @javax.ejb.Remote
    interface Remote extends ProviderIndustryRelationshipManagerInterface { }

    @javax.ejb.Local
    interface Local extends ProviderIndustryRelationshipManagerInterface { }
}
