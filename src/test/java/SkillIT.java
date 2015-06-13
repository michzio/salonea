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
import pl.salonea.entities.Employee;
import pl.salonea.entities.Skill;
import pl.salonea.enums.Gender;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Skill Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jun 12, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class SkillIT {

    private static final Logger logger = Logger.getLogger(SkillIT.class.getName());

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
    public void shouldCreateNewSkill() {

        // Create instance of Skill entity
        Skill skill = new Skill("painting");

        transaction.begin();
        em.persist(skill);
        transaction.commit();

        assertNotNull("Skill id can not be null.", skill.getSkillId());

        transaction.begin();
        em.remove(skill);
        transaction.commit();
    }

    @Test
    public void shouldAssignSkillToEmployee() {

        // create instance of Employee entity
        Employee employee = new Employee("michzio@hotmail.com", "michzio", "pAs12#", "Michał", "Ziobro", (short) 20, Gender.male, "assistant");

        // Create instance of Skill entity
        Skill skill = new Skill("painting");

        transaction.begin();
        em.persist(employee);
        em.persist(skill);
        Set<Skill> skills = new HashSet<>();
        skills.add(skill);
        employee.setSkills(skills);
        em.refresh(skill);
        transaction.commit();

        assertNotNull("Employee id can not be null.", employee.getUserId());
        assertNotNull("Skill id can not be null.", skill.getSkillId());
        assertTrue("Employee should contain association to given skill.", employee.getSkills().contains(skill));
        assertTrue("Skill should contain association to given employee.", skill.getSkilledEmployees().contains(employee));

        transaction.begin();
        em.remove(skill);
        em.remove(employee);
        transaction.commit();
    }
}