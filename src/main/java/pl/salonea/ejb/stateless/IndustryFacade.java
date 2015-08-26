package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.IndustryFacadeInterface;
import pl.salonea.entities.Industry;
import pl.salonea.entities.Provider;

import javax.ejb.*;
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
@TransactionManagement(TransactionManagementType.CONTAINER) // default
@TransactionAttribute(TransactionAttributeType.REQUIRED) // default
public class IndustryFacade extends AbstractFacade<Industry> implements IndustryFacadeInterface.Local, IndustryFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public IndustryFacade() {
        super(Industry.class);
    }

    @Override
    public Industry findForName(String name) {

        TypedQuery<Industry> query = getEntityManager().createNamedQuery(Industry.FIND_FOR_NAME, Industry.class);
        query.setParameter("name", name);
        return query.getSingleResult();
    }

    @Override
    public List<Industry> findByName(String name) {
        return findByName(name, null, null);
    }

    @Override
    public List<Industry> findByName(String name, Integer start, Integer limit) {

        TypedQuery<Industry> query = getEntityManager().createNamedQuery(Industry.FIND_BY_NAME, Industry.class);
        query.setParameter("name", "%" + name + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Industry> findByProvider(Provider provider) {
        return findByProvider(provider, null, null);
    }

    @Override
    public List<Industry> findByProvider(Provider provider, Integer start, Integer limit) {

        TypedQuery<Industry> query = getEntityManager().createNamedQuery(Industry.FIND_BY_PROVIDER, Industry.class);
        query.setParameter("provider", provider);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }
}
