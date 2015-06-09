import org.junit.*;
import pl.salonea.entities.Tag;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;

/**
 * Tag Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jun 4, 2015</pre>
 * @version 1.0
 */
public class TagIT {

    private static final Logger logger = Logger.getLogger(TagIT.class.getName());

    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction transaction;

    @BeforeClass
    public static void beforeClass() {
        emf = Persistence.createEntityManagerFactory("LocalServicesMySQL");
        logger.log(Level.INFO, "Creating entity manager factory.");
    }

    @AfterClass
    public static void afterClass() {
        if(emf != null) emf.close();
        logger.log(Level.INFO, "Entity manager factory has been destroyed.");
    }

    @Before
    public void before() throws Exception {
        em = emf.createEntityManager();
        transaction = em.getTransaction();
        logger.log(Level.INFO, "Creating entity manager and entity transaction.");
    }

    @After
    public void after() throws Exception {
        if(em != null) em.close();
    }

    @Test
    public void shouldCreateNewTag() {

        // Create Tag instance
        Tag tag = new Tag("bananas");

        transaction.begin();
        em.persist(tag);
        transaction.commit();

        assertNotNull("Tag id should be set.", tag.getTagId());

        transaction.begin();
        em.remove(tag);
        transaction.commit();

    }

}
