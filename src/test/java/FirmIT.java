import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.*;
import org.junit.runner.RunWith;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.Firm;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.validation.ConstraintViolationException;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;

/**
 * Firm Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jun 4, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class FirmIT {

    private static final Logger logger = Logger.getLogger(FirmIT.class.getName());

    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction transaction;

    @Deployment
    public static WebArchive createDeployment() {

        File[] dependencies = Maven.resolver().resolve(
                "org.slf4j:slf4j-simple:1.7.7"
        )
                .withoutTransitivity().asFile();

        WebArchive war = ShrinkWrap.create(ZipImporter.class, "salonea-1.0.war")
                .importFrom(new File("./out/artifacts/salonea/salonea-1.0.war"))
                .as(WebArchive.class);

        war.addAsLibraries(dependencies);

        return war;

    }


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

    /*
   @Test(expected = ConstraintViolationException.class) // not set required fields
    public void shouldNotCreateNewFirm() {

        // Create instance of firm entity
        Firm firm = new Firm("firma@allegro.pl", "allegro", "aAle2@", "Allegro Ltd.");

        // persist the firm to the database
        transaction.begin();
        em.persist(firm);

        em.remove(firm);

        transaction.commit();
    }*/

    @Test
    public void shouldCreateNewFirm() {

        // Create instance of firm entity
        Firm firm = new Firm("firma@allegro.pl", "allegro", "aAle2@", "Allegro Ltd.");

        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        firm.setAddress(address);

        firm.setVatin("1234567890");
        firm.setCompanyNumber("1234567890");

        // persist the firm to the database
        transaction.begin();
        em.persist(firm);
        transaction.commit();

        assertNotNull("User id should be set.", firm.getUserId());

        transaction.begin();

        em.remove(firm);

        transaction.commit();
    }


}