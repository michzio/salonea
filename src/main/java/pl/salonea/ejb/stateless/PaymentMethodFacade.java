package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.PaymentMethodFacadeInterface;
import pl.salonea.entities.PaymentMethod;
import pl.salonea.entities.Provider;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by michzio on 24/07/2015.
 */
@Stateless
@LocalBean
public class PaymentMethodFacade extends AbstractFacade<PaymentMethod> implements PaymentMethodFacadeInterface.Local, PaymentMethodFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public PaymentMethodFacade() {
        super(PaymentMethod.class);
    }


    @Override
    public PaymentMethod findForName(String name) {

        TypedQuery<PaymentMethod> query = getEntityManager().createNamedQuery(PaymentMethod.FIND_FOR_NAME, PaymentMethod.class);
        query.setParameter("name", name);
        return query.getSingleResult();
    }

    @Override
    public List<PaymentMethod> findByName(String name) {
        return findByName(name, null, null);
    }

    @Override
    public List<PaymentMethod> findByName(String name, Integer start, Integer offset) {

        TypedQuery<PaymentMethod> query = getEntityManager().createNamedQuery(PaymentMethod.FIND_BY_NAME, PaymentMethod.class);
        query.setParameter("name", "%" + name + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<PaymentMethod> findInAdvance(Boolean inAdvance) {
        return findInAdvance(inAdvance, null, null);
    }

    @Override
    public List<PaymentMethod> findInAdvance(Boolean inAdvance, Integer start, Integer offset) {

        TypedQuery<PaymentMethod> query = getEntityManager().createNamedQuery(PaymentMethod.FIND_IN_ADVANCE, PaymentMethod.class);
        query.setParameter("in_advance", inAdvance);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<PaymentMethod> findByNameAndInAdvance(String name, Boolean inAdvance) {
        return findByNameAndInAdvance(name, inAdvance, null, null);
    }

    @Override
    public List<PaymentMethod> findByNameAndInAdvance(String name, Boolean inAdvance, Integer start, Integer offset) {

        TypedQuery<PaymentMethod> query = getEntityManager().createNamedQuery(PaymentMethod.FIND_BY_NAME_AND_IN_ADVANCE, PaymentMethod.class);
        query.setParameter("name", "%" + name + "%");
        query.setParameter("in_advance", inAdvance);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<PaymentMethod> findByProvider(Provider provider) {
        return findByProvider(provider, null, null);
    }

    @Override
    public List<PaymentMethod> findByProvider(Provider provider, Integer start, Integer offset) {

        TypedQuery<PaymentMethod> query = getEntityManager().createNamedQuery(PaymentMethod.FIND_BY_PROVIDER, PaymentMethod.class);
        query.setParameter("provider", provider);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }
}
