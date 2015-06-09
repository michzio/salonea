import org.junit.*;
import pl.salonea.entities.UserAccount;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/** 
* UserAccount Tester. 
* 
* @author Michal Ziobro
* @since <pre>Jun 4, 2015</pre> 
* @version 1.0 
*/ 
public class UserAccountIT {

    private static final Logger logger = Logger.getLogger(UserAccountIT.class.getName());

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
        emf.close();
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
    public void shouldCreateNewUserAccount() throws Exception {

        // create instance of user account entity
        UserAccount userAccount = new UserAccount("michzio@hotmail.com", "michzio", "pP_ass12");

        // persist the user account to the database
        transaction.begin();
        em.persist(userAccount);


        assertNotNull("User id should be set.", userAccount.getUserId());

        em.remove(userAccount);

        transaction.commit();

    }

    @Test
    public void shouldUserAccountBeActivable() {

        // create user account instance
        UserAccount userAccount = new UserAccount("michzio@hotmail.com", "michzio", "pPass12.");

        // persist the user account to the database
        transaction.begin();
        em.persist(userAccount);


        assertNotNull("Activation code not generated for new user account.", userAccount.getActivationCode());

        userAccount.setActivationCode(null);
        transaction.commit();

        transaction.begin();
        userAccount = em.find(UserAccount.class, userAccount.getUserId());

        assertNull("User account not activated!", userAccount.getActivationCode());

        em.remove(userAccount);
        transaction.commit();

    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldRaiseConstraintViolationCauseWrongEmail() {

        // create user account instance with wrong email address
        UserAccount userAccount = new UserAccount("michzio-hotmail.com", "michzio", "pPass12$");

        // try persist the user account to the database
        transaction.begin();
        em.persist(userAccount);
        transaction.commit();
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldRaiseConstraintViolationCauseWeakPassword() {
        // create user account instance with weak password
        UserAccount userAccount = new UserAccount("michzio@hotmail.com", "michzio", "123456");

        // try persist the user account to the database
        transaction.begin();
        em.persist(userAccount);
        transaction.commit();
    }

}