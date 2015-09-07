import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pl.salonea.entities.Client;
import pl.salonea.entities.CreditCard;
import pl.salonea.enums.CreditCardType;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * CreditCard Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jun 10, 2015</pre>
 * @version 1.0
 */
public class CreditCardIT {

    private static final Logger logger = Logger.getLogger(CreditCard.class.getName());

    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction transaction;

    @Before
    public void before() throws Exception {
        emf = Persistence.createEntityManagerFactory("LocalServicesMySQL");
        em = emf.createEntityManager();
        transaction = em.getTransaction();
        logger.log(Level.INFO, "Creating entity manager and entity transaction.");
    }

    @After
    public void after() throws Exception {
        if(em != null) em.close();
        if(emf != null) emf.close();
    }

    @Test
    public void shouldCreateNewCreditCard() {

        // create instance of Client entity
        Client client = new Client("Credit card owner.");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);

        // create instance of CreditCard entity
        CreditCard creditCard = new CreditCard(client, "4539345259115377", cal.getTime(), "Michał Ziobro", CreditCardType.VISA);
        client.getCreditCards().add(creditCard);

        transaction.begin();
        em.persist(client);
        em.persist(creditCard);
        transaction.commit();

        assertNotNull(client.getCreditCards());
        assertTrue(client.getCreditCards().iterator().next().getCreditCardNumber().equals(creditCard.getCreditCardNumber()));
        assertTrue(creditCard.getExpirationDate().after(new Date()));
        assertTrue(creditCard.getClient().equals(client));

        transaction.begin();
        em.remove(creditCard);
        em.remove(client);
        transaction.commit();

    }
}
