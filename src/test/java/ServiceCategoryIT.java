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
import javax.validation.ConstraintViolationException;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * ServiceCategory Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jun 4, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class ServiceCategoryIT {

    private static final Logger logger = Logger.getLogger(ServiceCategoryIT.class.getName());

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
    public void shouldCreateNewServiceCategory() {

        // create instance of ServiceCategory entity
        ServiceCategory category = new ServiceCategory("Service category name");

        transaction.begin();
        em.persist(category);
        transaction.commit();

        assertNotNull("Service category id can not be null.", category.getCategoryId());

        transaction.begin();
        em.remove(category);
        transaction.commit();
    }

    @Test
    public void shouldCreateSubCategory() {

        // create instance of Service Category entity
        ServiceCategory category = new ServiceCategory("Category name");

        // create instances of sub category
        ServiceCategory subCategory1 = new ServiceCategory(category, "Sub Category 1");
        ServiceCategory subCategory2 = new ServiceCategory("Sub Category 2");
        subCategory2.setSuperCategory(category);
        ServiceCategory subCategory3  = new ServiceCategory(category, "Sub Category 3");

        transaction.begin();
        em.persist(category);
        em.persist(subCategory1);
        em.persist(subCategory2);
        em.persist(subCategory3);
        em.refresh(category);
        transaction.commit();

        assertNotNull("Category should contain subcategories.", category.getSubCategories());
        assertTrue(category.getSubCategories().contains(subCategory1));
        assertTrue(category.getSubCategories().contains(subCategory2));
        assertTrue(category.getSubCategories().contains(subCategory3));

        transaction.begin();
        em.remove(subCategory3);
        em.remove(subCategory2);
        em.remove(subCategory1);
        em.remove(category);
        transaction.commit();
    }

   /* @Test(expected = ConstraintViolationException.class)
    public void shouldRaiseExceptionRelatedToCategoryName() {

        // create instance of ServiceCategory entity
        ServiceCategory category = new ServiceCategory("wrong category name");

        transaction.begin();
        em.persist(category);
        transaction.commit();

        assertNotNull("Service category id can not be null.", category.getCategoryId());

        transaction.begin();
        em.remove(category);
        transaction.commit();
    } */

}
