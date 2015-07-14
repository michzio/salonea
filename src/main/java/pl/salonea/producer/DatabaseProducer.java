package pl.salonea.producer;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by michzio on 14/07/2015.
 */
public class DatabaseProducer {

    @Produces
    @PersistenceContext(unitName = "LocalServicesMySQL_JTA")
    private EntityManager entityManager;
}
