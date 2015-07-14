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
import pl.salonea.enums.Gender;
import pl.salonea.enums.ProviderType;
import pl.salonea.enums.WorkStationType;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * TermEmployeeWorkOn Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jun 13, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class TermEmployeeWorkOnIT {

    private static final Logger logger = Logger.getLogger(TermEmployeeWorkOnIT.class.getName());


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
    public void shouldCreateNewTermEmployeeWorkStationAssociation() {

        // get opening and closing datetimes
        Calendar calendar = new GregorianCalendar(2016, 1, 12, 8, 00);
        Date openingTime = calendar.getTime();
        calendar.add(Calendar.HOUR_OF_DAY, 8);
        Date closingTime = calendar.getTime();

        // create instance of Term entity
        Term term = new Term(openingTime, closingTime);

        Date dateOfBirth = new GregorianCalendar(1988, Calendar.OCTOBER, 3).getTime();

        // create instance of Employee entity
        Employee employee =  new Employee("michzio@hotmail.com", "michzio", "pAs12#", "Michał", "Ziobro", dateOfBirth, Gender.male, "assistant");

        // create instance of Provider entity
        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider = new Provider("firma2@allegro.pl", "allegro2", "aAle2@", "Allegro 2 Ltd.",
                "2234567890", "2234567890", address, "Allegro Polska", ProviderType.SIMPLE);

        // create instance of service point entity
        ServicePoint servicePoint = new ServicePoint(provider, 1, address);

        // create instance of work station entity
        WorkStation workStation = new WorkStation(servicePoint, 1, WorkStationType.OTHER);

        // create ternary association between Term, Employee and WorkStation
        TermEmployeeWorkOn termEmployeeWorkOn = new TermEmployeeWorkOn(employee, term, workStation);

        transaction.begin();
        em.persist(term);
        em.persist(employee);
        em.persist(provider);
        em.persist(servicePoint);
        em.persist(workStation);
        em.persist(termEmployeeWorkOn);
        transaction.commit();


        assertEquals(termEmployeeWorkOn.getEmployee(), employee);
        assertEquals(termEmployeeWorkOn.getTerm(), term);
        assertEquals(termEmployeeWorkOn.getWorkStation(), workStation);

        transaction.begin();
        em.refresh(employee);
        em.refresh(workStation);
        em.refresh(term);
        transaction.commit();

        assertTrue(employee.getTermsOnWorkStation().contains(termEmployeeWorkOn));
        assertTrue(workStation.getTermsEmployeesWorkOn().contains(termEmployeeWorkOn));
        assertTrue(term.getEmployeesWorkStation().contains(termEmployeeWorkOn));

        transaction.begin();
        em.remove(termEmployeeWorkOn);
        em.remove(workStation);
        em.remove(servicePoint);
        em.remove(provider);
        em.remove(employee);
        em.remove(term);
        transaction.commit();
    }
}
