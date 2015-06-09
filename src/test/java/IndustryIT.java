import org.junit.*;
import pl.salonea.entities.Industry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import static org.junit.Assert.*;

public class IndustryIT {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction transaction;

    @BeforeClass
    public static void beforeClass() {
        emf = Persistence.createEntityManagerFactory("LocalServicesMySQL");
    }

    @AfterClass
    public static void afterClass() {
        if(emf != null) emf.close();
    }

    @Before
    public void before() throws Exception {
        em = emf.createEntityManager();
        transaction = em.getTransaction();
    }

    @After
    public void after() throws Exception {
        if(em != null) em.close();
    }

    @Test
    public void shouldCreateNewIndustry() {

        // create new Industry instance
        Industry industry = new Industry("Computers");

        transaction.begin();
        em.persist(industry);
        transaction.commit();

        Industry industry2 = em.find(Industry.class, industry.getIndustryId());

        assertNotNull("Industry id should be generated.", industry2.getIndustryId());
        assertTrue("Industry names should be the same.", industry2.getName().equals("Computers"));

        transaction.begin();
        em.remove(industry2);
        transaction.commit();
    }
}
