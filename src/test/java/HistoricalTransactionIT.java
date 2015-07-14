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
import pl.salonea.entities.*;
import pl.salonea.enums.CurrencyCode;
import pl.salonea.enums.Gender;
import pl.salonea.enums.ProviderType;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * HistoricalTransaction Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jun 15, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class HistoricalTransactionIT {

    private static final Logger logger = Logger.getLogger(HistoricalTransactionIT.class.getName());

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
    public void shouldCreateNewHistoricalTransaction() {

        // Create instance of Client entity
        Client client = new Client("Personal client.");

        // Create instance of Provider entity
        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider = new Provider("firma2@allegro.pl", "allegro2", "aAle2@", "Allegro 2 Ltd.",
                "2234567890", "2234567890", address, "Allegro Polska", ProviderType.SIMPLE);

        // Create instance of Service entity
        Service service = new Service("Service name");

        // Create instance of ProviderService entity
        ProviderService providerService = new ProviderService(provider, service, 1800000L);

        // Create instance of PaymentMethod entity
        PaymentMethod paymentMethod = new PaymentMethod("Cash", false);

        // get opening and closing datetimes
        Calendar calendar = new GregorianCalendar(2016, 1, 12, 8, 00);
        Date openingTime = calendar.getTime();
        calendar.add(Calendar.HOUR_OF_DAY, 8);
        Date closingTime = calendar.getTime();

        // Create instance of Term entity
        Term term = new Term(openingTime, closingTime);

        // get booked time in between opening and closing time of Term entity
        calendar.add(Calendar.HOUR_OF_DAY, -4);
        Date bookedTime = calendar.getTime();

        // create instance of Transaction entity
        Transaction myTransaction = new Transaction(client, 1, 10.00, CurrencyCode.EUR, new Date(), bookedTime,
                false, providerService, paymentMethod, term);

        Date dateOfBirth = new GregorianCalendar(1988, Calendar.OCTOBER, 3).getTime();

        // create instance of Employee entity
        Employee employee = new Employee("michzio@hotmail.com", "michzio", "pAs12#", "Michał", "Ziobro", dateOfBirth, Gender.male, "assistant");

        Set<Employee> employees = new HashSet<>();
        employees.add(employee);

        myTransaction.setEmployees(employees);

        transaction.begin();
        em.persist(client);
        em.persist(provider);
        em.persist(service);
        em.persist(providerService);
        em.persist(paymentMethod);
        em.persist(term);
        em.persist(employee);
        em.persist(myTransaction);
        transaction.commit();

        transaction.begin();
        em.refresh(myTransaction);
        transaction.commit();

        // creating instance of HistoricalTransaction based on created Transaction entity
        HistoricalTransaction historicalTransaction
                = new HistoricalTransaction(myTransaction.getClient(),
                myTransaction.getTransactionNumber(), myTransaction.getPrice(), myTransaction.getPriceCurrencyCode(),
                myTransaction.getTransactionTime(),myTransaction.getBookedTime(), true,
                myTransaction.getProviderService(), myTransaction.getPaymentMethod(), myTransaction.getTerm(), true);

        historicalTransaction.setEmployees(myTransaction.getEmployees());

        // moving completed transaction to historical_transaction
        transaction.begin();
        em.persist(historicalTransaction);
        em.remove(myTransaction);
        transaction.commit();

        transaction.begin();
        em.refresh(historicalTransaction);
        transaction.commit();

        assertNotNull(historicalTransaction);
        assertEquals(historicalTransaction.getClient(), client);
        assertEquals(historicalTransaction.getProvider(), provider);
        assertEquals(historicalTransaction.getService(), service);
        assertEquals(historicalTransaction.getProviderService(), providerService);
        assertEquals(historicalTransaction.getBookedTime().getTime(), bookedTime.getTime());
        assertEquals(historicalTransaction.getPaid(), true);
        assertEquals(historicalTransaction.getPaymentMethod(), paymentMethod);
        assertEquals(historicalTransaction.getPriceCurrencyCode(), CurrencyCode.EUR);
        assertEquals(historicalTransaction.getTerm(), term);
        assertTrue(historicalTransaction.getEmployees().contains(employee));
        assertEquals(historicalTransaction.getProviderService().getProvider(), historicalTransaction.getProvider());
        assertEquals(historicalTransaction.getProviderService().getService(), historicalTransaction.getService());
        assertEquals(historicalTransaction.getCompletionStatus(), true);

        transaction.begin();
        em.remove(historicalTransaction);
        em.remove(employee);
        em.remove(term);
        em.remove(paymentMethod);
        em.remove(providerService);
        em.remove(service);
        em.remove(provider);
        em.remove(client);
        transaction.commit();
    }

}
