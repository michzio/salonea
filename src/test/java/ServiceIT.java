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
import pl.salonea.entities.Service;
import pl.salonea.entities.ServiceCategory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Service Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jun 4, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class ServiceIT {

    private static final Logger logger = Logger.getLogger(ServiceIT.class.getName());

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
    public void shouldCreateNewService() {

        // create instance of Service entity
        Service service = new Service("Hair washing");

        transaction.begin();
        em.persist(service);
        transaction.commit();

        assertNotNull("Service id can not be null.", service.getServiceId());

        transaction.begin();
        em.remove(service);
        transaction.commit();

    }

    @Test
    public void shouldCreateNewServiceAssignedToCategory() {

        // create instance of Category entity
        ServiceCategory category = new ServiceCategory("Category Name");

        // create instance of Service entity
        Service service = new Service("Service name");

        // assign Service instance to Category
        service.setServiceCategory(category);

        transaction.begin();
        em.persist(category);
        em.persist(service);
        em.refresh(category);
        transaction.commit();

        assertNotNull("Service id can not be null.", service.getServiceId());
        assertTrue(category.getServices().contains(service));
        assertEquals(service.getServiceCategory(), category);

        transaction.begin();
        em.remove(service);
        em.remove(category);
        transaction.commit();

    }
}