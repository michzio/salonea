package pl.salonea.ejb.stateless;

import pl.salonea.entities.NaturalPerson;

import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * Created by michzio on 14/07/2015.
 */
public class NaturalPersonFacade extends AbstractFacade<NaturalPerson> {

    @Inject
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NaturalPersonFacade() {
        super(NaturalPerson.class);
    }



}
