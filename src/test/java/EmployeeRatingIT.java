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
import pl.salonea.entities.Client;
import pl.salonea.entities.Employee;
import pl.salonea.entities.EmployeeRating;
import pl.salonea.enums.Gender;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * EmployeeRating Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jun 14, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class EmployeeRatingIT {

    private static final Logger logger = Logger.getLogger(EmployeeRatingIT.class.getName());

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
    public void shouldCreateNewEmployeeRating() {

        // create instance of Client entity
        Client client = new Client();

        // create instance of Employee entity
        Employee employee = new Employee("michzio@hotmail.com", "michzio", "pAs12#", "Michał", "Ziobro", (short) 20, Gender.male, "assistant");

        // create instance of EmployeeRating entity
        EmployeeRating employeeRating = new EmployeeRating(employee, client, (short) 9);

        transaction.begin();
        em.persist(client);
        em.persist(employee);
        em.persist(employeeRating);
        transaction.commit();

        assertEquals(employeeRating.getEmployee(), employee);
        assertEquals(employeeRating.getClient(), client);

        transaction.begin();
        em.refresh(client);
        em.refresh(employee);
        transaction.commit();

        assertTrue(client.getEmployeeRatings().contains(employeeRating));
        assertTrue(employee.getReceivedRatings().contains(employeeRating));

        transaction.begin();
        em.remove(employeeRating);
        em.remove(employee);
        em.remove(client);
        transaction.commit();
    }
}
