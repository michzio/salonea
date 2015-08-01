
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
import pl.salonea.embeddables.Address;
import pl.salonea.entities.Client;
import pl.salonea.entities.Firm;
import pl.salonea.entities.NaturalPerson;
import pl.salonea.enums.Gender;

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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


/**
 * Client Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jun 10, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class ClientIT {

    private static final Logger logger = Logger.getLogger(ClientIT.class.getName());

    private static EntityManagerFactory emf;
    // @PersistenceContext(unitName = "LocalServicesMySQL")
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
    public void shouldCreateNewClient() {

        // Create instance of Client not binded to neither NaturalPerson nor Firm
        Client client = new Client();

        transaction.begin();
        em.persist(client);
        transaction.commit();

        assertNotNull("Client id can not be null.", client.getClientId());

        transaction.begin();
        em.remove(client);
        transaction.commit();
    }

    @Test
    public void shouldCreateNewFirmClient() {

        // Create instance of Firm entity
        Firm firm = new Firm("firma@allegro.pl", "allegro", "aAle2@", "Allegro Ltd.");

        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        firm.setAddress(address);

        firm.setVatin("1234567890");
        firm.setCompanyNumber("1234567890");

        // Create instance of Client entity
        Client client = new Client("Nowy klient biznesowy.");

        transaction.begin();
        em.persist(firm);
        em.persist(client);
        firm.setClient(client);
        client.setFirm(firm);
        transaction.commit();

        assertTrue(client.getFirm().equals(firm));
        assertTrue(firm.getClient().equals(client));

        transaction.begin();
        em.remove(client);
        em.remove(firm);
        transaction.commit();
    }

    @Test
    public void shouldCreateNewPersonClient() {

        // Create instance of NaturalPerson entity
        Date dateOfBirth = new GregorianCalendar(1988, Calendar.OCTOBER, 3).getTime();
        NaturalPerson person = new NaturalPerson("michzio@hotmail.com", "michzio", "pP12@o", "Michał", "Ziobro");
        person.setBirthDate(dateOfBirth);
        person.setGender(Gender.male);

        // Create instance of Client entity
        Client client = new Client("Personal client.");

        transaction.begin();
        em.persist(person);
        em.persist(client);
        person.setClient(client);
        client.setNaturalPerson(person);
        transaction.commit();

        assertTrue(client.getNaturalPerson().equals(person));
        assertTrue(person.getClient().equals(client));
        assertNull(client.getFirm());

        transaction.begin();
        em.remove(client);
        em.remove(person);
        transaction.commit();
    }

}
