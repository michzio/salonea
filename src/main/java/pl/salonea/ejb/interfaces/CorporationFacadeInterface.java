package pl.salonea.ejb.interfaces;

import pl.salonea.embeddables.Address;
import pl.salonea.entities.Corporation;
import pl.salonea.entities.Provider;

import javax.ejb.Remote;
import java.util.Date;
import java.util.List;

/**
 * Created by michzio on 22/07/2015.
 */
public interface CorporationFacadeInterface extends AbstractFacadeInterface<Corporation> {

    // concrete interface
    Corporation update(Corporation corporation, Boolean retainTransientFields);
    List<Corporation> findAllEagerly();
    List<Corporation> findAllEagerly(Integer start, Integer limit);
    Corporation findByIdEagerly(Long corporationId);
    List<Corporation> findByAddress(String city, String state, String country, String street, String zipCode);
    List<Corporation> findByAddress(String city, String state, String country, String street, String zipCode, Integer start, Integer limit);
    List<Corporation> findByName(String name);
    List<Corporation> findByName(String name, Integer start, Integer limit);
    List<Corporation> findByDescription(String description);
    List<Corporation> findByDescription(String description, Integer start, Integer limit);
    List<Corporation> findByHistory(String history);
    List<Corporation> findByHistory(String history, Integer start, Integer limit);
    List<Corporation> findByKeyword(String keyword);
    List<Corporation> findByKeyword(String keyword, Integer start, Integer limit);
    List<Corporation> findOpenAfter(Date date);
    List<Corporation> findOpenAfter(Date date, Integer start, Integer limit);
    List<Corporation> findOpenBefore(Date date);
    List<Corporation> findOpenBefore(Date date, Integer start, Integer limit);
    List<Corporation> findOpenBetween(Date startDate, Date endDate);
    List<Corporation> findOpenBetween(Date startDate, Date endDate, Integer start, Integer limit);
    Corporation findForProvider(Provider provider);
    Corporation findForProviderEagerly(Provider provider);

    List<Corporation> findByMultipleCriteria(List<Provider> providers, String name, String description, String history, Date startOpeningDate, Date endOpeningDate, Address address);
    List<Corporation> findByMultipleCriteria(List<Provider> providers, String name, String description, String history, Date startOpeningDate, Date endOpeningDate, Address address, Integer start, Integer limit);
    List<Corporation> findByMultipleCriteriaEagerly(List<Provider> providers, String name, String description, String history, Date startOpeningDate, Date endOpeningDate, Address address);
    List<Corporation> findByMultipleCriteriaEagerly(List<Provider> providers, String name, String description, String history, Date startOpeningDate, Date endOpeningDate, Address address, Integer start, Integer limit);


    @javax.ejb.Remote
    interface Remote extends CorporationFacadeInterface { }

    @javax.ejb.Local
    interface Local extends CorporationFacadeInterface { }
}
