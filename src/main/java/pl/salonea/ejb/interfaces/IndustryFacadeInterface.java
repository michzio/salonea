package pl.salonea.ejb.interfaces;

import pl.salonea.entities.Industry;
import pl.salonea.entities.Provider;

import java.util.List;

/**
 * Created by michzio on 24/07/2015.
 */
public interface IndustryFacadeInterface extends AbstractFacadeInterface<Industry> {

    // concrete interface
    Industry update(Industry industry, Boolean retainTransientFields);
    List<Industry> findAllEagerly();
    List<Industry> findAllEagerly(Integer start, Integer limit);
    Industry findByIdEagerly(Long industryId);
    Industry findForName(String name);
    List<Industry> findByName(String name);
    List<Industry> findByName(String name, Integer start, Integer limit);
    List<Industry> findByDescription(String description);
    List<Industry> findByDescription(String description, Integer start, Integer limit);
    List<Industry> findByKeyword(String keyword);
    List<Industry> findByKeyword(String keyword, Integer start, Integer limit);
    List<Industry> findByProvider(Provider provider);
    List<Industry> findByProvider(Provider provider, Integer start, Integer limit);
    List<Industry> findByProviderEagerly(Provider provider);
    List<Industry> findByProviderEagerly(Provider provider, Integer start, Integer limit);

    Long countByProvider(Provider provider);

    List<Industry> findByMultipleCriteria(List<Provider> providers, String name, String description);
    List<Industry> findByMultipleCriteria(List<Provider> providers, String name, String description, Integer start, Integer limit);
    List<Industry> findByMultipleCriteriaEagerly(List<Provider> providers, String name, String description);
    List<Industry> findByMultipleCriteriaEagerly(List<Provider> providers, String name, String description, Integer start, Integer limit);

    @javax.ejb.Remote
    interface Remote extends IndustryFacadeInterface { }

    @javax.ejb.Local
    interface Local extends IndustryFacadeInterface { }
}
