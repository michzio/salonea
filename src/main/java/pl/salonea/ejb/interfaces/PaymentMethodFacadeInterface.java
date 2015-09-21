package pl.salonea.ejb.interfaces;

import pl.salonea.entities.PaymentMethod;
import pl.salonea.entities.Provider;

import java.util.List;

/**
 * Created by michzio on 24/07/2015.
 */
public interface PaymentMethodFacadeInterface extends AbstractFacadeInterface<PaymentMethod> {

    // concrete interface
    List<PaymentMethod> findAllEagerly();
    List<PaymentMethod> findAllEagerly(Integer start, Integer limit);
    PaymentMethod findByIdEagerly(Long paymentMethodId);
    PaymentMethod findForName(String name);
    List<PaymentMethod> findByName(String name);
    List<PaymentMethod> findByName(String name, Integer start, Integer limit);
    List<PaymentMethod> findInAdvance(Boolean inAdvance);
    List<PaymentMethod> findInAdvance(Boolean inAdvance, Integer start, Integer limit);
    List<PaymentMethod> findByNameAndInAdvance(String name, Boolean inAdvance);
    List<PaymentMethod> findByNameAndInAdvance(String name, Boolean inAdvance, Integer start, Integer limit);
    List<PaymentMethod> findByProvider(Provider provider);
    List<PaymentMethod> findByProvider(Provider provider, Integer start, Integer limit);
    List<PaymentMethod> findByMultipleCriteria(List<Provider> providers, String name, String description, Boolean inAdvance);
    List<PaymentMethod> findByMultipleCriteria(List<Provider> providers, String name, String description, Boolean inAdvance, Integer start, Integer limit);
    List<PaymentMethod> findByMultipleCriteriaEagerly(List<Provider> providers, String name, String description, Boolean inAdvance);
    List<PaymentMethod> findByMultipleCriteriaEagerly(List<Provider> providers, String name, String description, Boolean inAdvance, Integer start, Integer limit);

    @javax.ejb.Remote
    interface Remote extends PaymentMethodFacadeInterface { }

    @javax.ejb.Local
    interface Local extends PaymentMethodFacadeInterface { }
}
