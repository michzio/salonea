
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.interfaces.UserAccountFacadeInterface;
import pl.salonea.entities.UserAccount;

import javax.inject.Inject;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * UserAccountFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jul 16, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class UserAccountFacadeIT {

    private static final Logger logger = Logger.getLogger(UserAccountFacadeIT.class.getName());

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

    @Inject
    private UserAccountFacadeInterface.Local userAccountFacade;

    @Test
    public void shouldCreateUserAccount() {

        // create instance of UserAccount
        UserAccount userAccount = new UserAccount("michzio@hotmail.com", "michzio", "pP_ass12");

        // persist the UserAccount in the database
        userAccount = userAccountFacade.create(userAccount);

        assertNotNull("User account ID should not be null.", userAccount.getUserId());

        UserAccount foundUserAccount = userAccountFacade.find(userAccount.getUserId());
        userAccountFacade.remove(foundUserAccount);

        assertTrue("There should not be any client in database.", userAccountFacade.count() == 0);
    }

    @Test
    public void shouldEnableToManageUserActivation() {

        // create some instances of UserAccount
        UserAccount user1 = new UserAccount("michzio@hotmail.com", "michzio1", "pP_asS12");
        UserAccount user2 = new UserAccount("24sell@gmail.com", "24sell", "pP_asS12");
        UserAccount user3 = new UserAccount("michal.ziobro@uj.edu.pl", "michzio_uj", "pP_asS12");

        userAccountFacade.create(user1);
        userAccountFacade.create(user2);
        userAccountFacade.create(user3);

        assertTrue("There should be 3 users in the database.", userAccountFacade.count() == 3);
        assertTrue("None of created users should be activated.", userAccountFacade.findAllActivated().size() == 0);
        assertTrue("All created users should not be activated.", userAccountFacade.findAllNotActivated().size() == 3);

        user2.setActivationCode(null);
        userAccountFacade.update(user2);

        assertTrue("Now, there should be 2 users that are not activated.", userAccountFacade.findAllNotActivated().size() == 2);
        assertTrue("There is also 1 user that has been activated.", userAccountFacade.findAllActivated().size() == 1);

        userAccountFacade.updateActivateAll();

        assertTrue("Now, all users are activated.", userAccountFacade.findAllNotActivated().size() == 0);
        assertTrue("Now, all users are activated.", userAccountFacade.findAllActivated().size() == 3);

        // remove all instances of UserAccount
        userAccountFacade.remove(user3);
        userAccountFacade.remove(user2);
        userAccountFacade.remove(user1);

    }

    @Test
    public void shouldFindByEmailOrLogin() {

        // create some instances of UserAccount
        UserAccount user1 = new UserAccount("michzio@hotmail.com", "michzio1", "pP_asS12");
        UserAccount user2 = new UserAccount("24sell@gmail.com", "24sell", "pP_asS12");
        UserAccount user3 = new UserAccount("michal.ziobro@uj.edu.pl", "michzio_uj", "pP_asS12");

        userAccountFacade.create(user1);
        userAccountFacade.create(user2);
        userAccountFacade.create(user3);

        List<UserAccount> users = userAccountFacade.findByEmail(".com");
        assertTrue("There should be 2 users in .com domain.", users.size() == 2);
        assertTrue(users.contains(user1) && users.contains(user2));

        List<UserAccount> users2 = userAccountFacade.findByEmail(".pl");
        assertTrue("There should be 1 user in .pl domain.", users2.size() == 1);
        assertTrue(users2.contains(user3));

        List<UserAccount> accounts = userAccountFacade.findByLogin("michzio");
        assertTrue("User 'michzio' should have 2 accounts in system.", accounts.size() == 2);
        assertTrue(accounts.contains(user1) && accounts.contains(user3));

        // remove all instances of UserAccount
        userAccountFacade.remove(user3);
        userAccountFacade.remove(user2);
        userAccountFacade.remove(user1);
    }

    @Test
    public void shouldFindByRegistrationDate() {

        // create some instances of UserAccount
        UserAccount user1 = new UserAccount("michzio@hotmail.com", "michzio1", "pP_asS12");
        UserAccount user2 = new UserAccount("24sell@gmail.com", "24sell", "pP_asS12");
        UserAccount user3 = new UserAccount("michal.ziobro@uj.edu.pl", "michzio_uj", "pP_asS12");

        Calendar c = Calendar.getInstance();

        c.add(Calendar.YEAR, -1);
        user1.setRegistrationDate(c.getTime());

        c.add(Calendar.YEAR, -2);
        user2.setRegistrationDate(c.getTime());

        c.add(Calendar.YEAR, -5);
        user3.setRegistrationDate(c.getTime());

        userAccountFacade.create(user1);
        userAccountFacade.create(user2);
        userAccountFacade.create(user3);

        c.add(Calendar.YEAR, 3);
        Date startDate = c.getTime();
        c.add(Calendar.YEAR, 3);
        Date endDate = c.getTime();
        List<UserAccount> users = userAccountFacade.findCreatedBetween(startDate, endDate);

        assertTrue("There should be one user registered between given dates.", users.size() == 1);
        assertTrue(users.contains(user2));
        assertFalse(users.contains(user1));
        assertFalse(users.contains(user3));

        // remove all instances of UserAccount
        userAccountFacade.remove(user3);
        userAccountFacade.remove(user2);
        userAccountFacade.remove(user1);
    }

}
