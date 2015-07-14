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
import pl.salonea.entities.Education;
import pl.salonea.entities.Employee;
import pl.salonea.enums.Gender;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Education Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jun 12, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class EducationIT {

    private static final Logger logger = Logger.getLogger(EducationIT.class.getName());

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
    public void shouldCreateNewEducationEntity() {

        // Create instance of Education entity
        Education education = new Education("Stanford University", "Master's Degree in Computer Science");

        transaction.begin();
        em.persist(education);
        transaction.commit();

        assertNotNull("Education id can not be null.", education.getEducationId());

        transaction.begin();
        em.remove(education);
        transaction.commit();

    }

    @Test
    public void shouldAssignEducationToEmployee() {

        Date dateOfBirth = new GregorianCalendar(1988, Calendar.OCTOBER, 3).getTime();

        // create instance of Employee entity
        Employee employee = new Employee("michzio@hotmail.com", "michzio", "pAs12#", "Michał", "Ziobro", dateOfBirth, Gender.male, "assistant");

        // create instance of Education entity
        Education education = new Education("Stanford University", "Master's Degree in Computer Science");

        transaction.begin();
        em.persist(employee);
        em.persist(education);
        Set<Education> educations = new HashSet<>();
        educations.add(education);
        employee.setEducations(educations);
        em.refresh(education);
        transaction.commit();

        assertNotNull("Employee id can not be null.", employee.getUserId());
        assertNotNull("Education id can not be null.", education.getEducationId());
        assertTrue("Employee should contain association to given education.", employee.getEducations().contains(education));
        assertTrue("Education should contain association to given employee.", education.getEducatedEmployees().contains(employee));

        transaction.begin();
        em.remove(education);
        em.remove(employee);
        transaction.commit();
    }
}
