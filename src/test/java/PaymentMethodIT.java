import org.junit.*;
import pl.salonea.entities.PaymentMethod;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

public class PaymentMethodIT {

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
    public void shouldCreateNewPaymentMethod() {

        // create new PaymentMethod instance
        PaymentMethod paymentMethod = new PaymentMethod("Cash", false);

        transaction.begin();
        em.persist(paymentMethod);
        transaction.commit();

        PaymentMethod paymentMethod2 = em.find(PaymentMethod.class, paymentMethod.getId());

        assertNotNull("Payment method id should be generated.", paymentMethod2.getId());
        assertTrue(paymentMethod2.getName().equals("Cash"));

        transaction.begin();
        em.remove(paymentMethod2);
        transaction.commit();
    }


}
