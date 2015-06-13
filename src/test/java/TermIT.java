
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.entities.Term;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Term Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jun 13, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class TermIT {

    private static final Logger logger = Logger.getLogger(TermIT.class.getName());

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

    @Test
    public void shouldCreateNewTerm() {

        // get opening and closing datetimes
        Calendar calendar = new GregorianCalendar(2016, 1, 12, 8, 00);
        Date openingTime = calendar.getTime();
        calendar.add(Calendar.HOUR_OF_DAY, 8);
        Date closingTime = calendar.getTime();

        // create instance of Term entity
        Term term = new Term(openingTime, closingTime);

        transaction.begin();
        em.persist(term);
        transaction.commit();

        assertNotNull("Term id can not be null.", term.getTermId());
        assertTrue(term.getOpeningTime().before(term.getClosingTime()));

        transaction.begin();
        em.remove(term);
        transaction.commit();
    }
}
