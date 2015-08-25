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
import pl.salonea.enums.WorkStationType;

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
 * Transaction Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jun 14, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class TransactionIT {

    private static final Logger logger = Logger.getLogger(TransactionIT.class.getName());

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
    public void shouldCreateNewTransaction() {

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

        // create instance of ServicePoint entity
        ServicePoint servicePoint = new ServicePoint(provider, 1, address);

        // create instance of WorkStation entity
        WorkStation workStation = new WorkStation(servicePoint, 1, WorkStationType.OFFICE);

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
                false, service, workStation, paymentMethod, term);

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
        em.persist(servicePoint);
        em.persist(workStation);
        em.persist(paymentMethod);
        em.persist(term);
        em.persist(employee);
        em.persist(myTransaction);
        transaction.commit();

        transaction.begin();
        em.refresh(myTransaction);
        transaction.commit();

        assertNotNull(myTransaction);
        assertEquals(myTransaction.getClient(), client);
        assertEquals(myTransaction.getProvider(), provider);
        assertEquals(myTransaction.getService(), service);
        assertEquals(myTransaction.getProviderService(), providerService);
        assertEquals(myTransaction.getServicePoint(), servicePoint);
        assertEquals(myTransaction.getWorkStation(), workStation);
        assertEquals(myTransaction.getBookedTime().getTime(), bookedTime.getTime());
        assertEquals(myTransaction.getPaid(), false);
        assertEquals(myTransaction.getPaymentMethod(), paymentMethod);
        assertEquals(myTransaction.getPriceCurrencyCode(), CurrencyCode.EUR);
        assertEquals(myTransaction.getTerm(), term);
        assertTrue(myTransaction.getEmployees().contains(employee));
        assertEquals(myTransaction.getProviderService().getProvider(), myTransaction.getProvider());
        assertEquals(myTransaction.getProviderService().getService(), myTransaction.getService());

        transaction.begin();
        em.remove(myTransaction);
        em.remove(employee);
        em.remove(term);
        em.remove(paymentMethod);
        em.remove(workStation);
        em.remove(servicePoint);
        em.remove(providerService);
        em.remove(service);
        em.remove(provider);
        em.remove(client);
        transaction.commit();
    }

}
