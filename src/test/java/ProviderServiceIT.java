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
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * ProviderService Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jun 14, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class ProviderServiceIT {

    private static final Logger logger = Logger.getLogger(ProviderServiceIT.class.getName());

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
    public void shouldCreateNewProviderService() {

        // Create instance of Provider entity
        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider = new Provider("firma2@allegro.pl", "allegro2", "aAle2@", "Allegro 2 Ltd.",
                "2234567890", "2234567890", address, "Allegro Polska", ProviderType.SIMPLE);

        // Create instance of Service entity
        Service service = new Service("Service name");

        // Create instance of ProviderService entity
        ProviderService providerService = new ProviderService(provider, service, 1800000L);

        transaction.begin();
        em.persist(provider);
        em.persist(service);
        em.persist(providerService);
        transaction.commit();

        assertEquals(providerService.getService(), service);
        assertEquals(providerService.getProvider(), provider);

        transaction.begin();
        em.refresh(service);
        em.refresh(provider);
        transaction.commit();

        assertTrue(service.getProvidedServiceOffers().contains(providerService));
        assertTrue(provider.getSuppliedServiceOffers().contains(providerService));

        transaction.begin();
        em.remove(providerService);
        em.remove(service);
        em.remove(provider);
        transaction.commit();

    }

    @Test
    public void shouldAssignEmployeeToProviderService() {

        // Create instance of Provider entity
        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider = new Provider("firma2@allegro.pl", "allegro2", "aAle2@", "Allegro 2 Ltd.",
                "2234567890", "2234567890", address, "Allegro Polska", ProviderType.SIMPLE);

        // Create instance of Service entity
        Service service = new Service("Service name");

        // Create instance of ProviderService entity
        ProviderService providerService = new ProviderService(provider, service, 1800000L);

        // create instance of Employee entity
        Employee employee = new Employee("michzio@hotmail.com", "michzio", "pAs12#", "Michał", "Ziobro", (short) 20, Gender.male, "assistant");

        Set<Employee> employees = new HashSet<>();
        employees.add(employee);
        providerService.setSupplyingEmployees(employees);

        transaction.begin();
        em.persist(provider);
        em.persist(service);
        em.persist(employee);
        em.persist(providerService);
        transaction.commit();

        assertEquals(providerService.getProvider(), provider);
        assertEquals(providerService.getService(), service);
        assertTrue(providerService.getSupplyingEmployees().contains(employee));

        transaction.begin();
        em.refresh(employee);
        transaction.commit();

        assertTrue(employee.getSuppliedServices().contains(providerService));

        transaction.begin();
        em.remove(providerService);
        em.remove(employee);
        em.remove(service);
        em.remove(provider);
        transaction.commit();
    }

    @Test
    public void shouldAssignWorkStationToProviderService() {

        // Create instance of Provider entity
        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider = new Provider("firma2@allegro.pl", "allegro2", "aAle2@", "Allegro 2 Ltd.",
                "2234567890", "2234567890", address, "Allegro Polska", ProviderType.SIMPLE);

        // Create instance of Service entity
        Service service = new Service("Service name");

        // Create instance of ProviderService entity
        ProviderService providerService = new ProviderService(provider, service, 1800000L);

        // Create instance of ServicePoint entity
        ServicePoint servicePoint = new ServicePoint(provider, 1, address);

        // Create instance of WorkStation entity
        WorkStation workStation = new WorkStation(servicePoint, 1, WorkStationType.OFFICE);

        // Set workStation in association with providerService
        Set<WorkStation> workStations = new HashSet<>();
        workStations.add(workStation);

        providerService.setWorkStations(workStations);

        transaction.begin();
        em.persist(provider);
        em.persist(service);
        em.persist(servicePoint);
        em.persist(workStation);
        em.persist(providerService);
        transaction.commit();

        assertEquals(servicePoint.getProvider(), provider);
        assertEquals(workStation.getServicePoint(), servicePoint);
        assertEquals(providerService.getProvider(), provider);
        assertEquals(providerService.getService(), service);
        assertTrue(providerService.getWorkStations().contains(workStation));

        transaction.begin();
        em.refresh(workStation);
        transaction.commit();

        assertTrue(workStation.getProvidedServices().contains(providerService));

        assertEquals(providerService.getProvider().getUserId(), workStation.getServicePoint().getProvider().getUserId());

        transaction.begin();
        em.remove(providerService);
        em.remove(workStation);
        em.remove(servicePoint);
        em.remove(service);
        em.remove(provider);
        transaction.commit();

    }

  /*  @Test(expected = ConstraintViolationException.class)
    public void shouldRiseConstraintViolationWhenAssigningWorkStation() {

        // Create first instance of Provider entity
        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider1 = new Provider("firma@allegro.pl", "allegro", "aAle2@", "Allegro Ltd.",
                "2234567891", "2234567891", address, "Allegro Polska", ProviderType.SIMPLE);

        // Create second instance of Provider entity
        Provider provider2 = new Provider("firma2@allegro.pl", "allegro2", "aAle2@", "Allegro 2 Ltd.",
                "2234567890", "2234567890", address, "Allegro Polska", ProviderType.SIMPLE);

        // Create instance of Service entity
        Service service = new Service("Service name");

        // Create instance of ProviderService entity
        ProviderService providerService = new ProviderService(provider1, service, 1800000L);

        // Create instance of ServicePoint entity
        ServicePoint servicePoint = new ServicePoint(provider2, 1, address);

        // Create instance of WorkStation entity
        WorkStation workStation = new WorkStation(servicePoint, 1, WorkStationType.OFFICE);

        // Set workStation in association with providerService
        Set<WorkStation> workStations = new HashSet<>();
        workStations.add(workStation);

        providerService.setWorkStations(workStations);

        transaction.begin();
        em.persist(provider1);
        em.persist(provider2);
        em.persist(service);
        em.persist(servicePoint);
        em.persist(workStation);
        em.persist(providerService);
        transaction.commit();


        transaction.begin();
        em.remove(providerService);
        em.remove(workStation);
        em.remove(servicePoint);
        em.remove(service);
        em.remove(provider2);
        em.remove(provider1);
        transaction.commit();

    }
    */
}
