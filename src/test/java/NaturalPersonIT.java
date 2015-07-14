

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.*;
import org.junit.runner.RunWith;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.NaturalPerson;
import pl.salonea.enums.Gender;
import pl.salonea.qualifiers.CountryQualifierLiteral;
import pl.salonea.utils.zip_codes.ZipCodeChecker;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.persistence.*;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/** 
* NaturalPerson Tester. 
* 
* @author Michal Ziobro
* @since <pre>Jun 4, 2015</pre> 
* @version 1.0 
*/
@RunWith(Arquillian.class)
public class NaturalPersonIT {

    private static final Logger logger = Logger.getLogger(NaturalPersonIT.class.getName());

    private static EntityManagerFactory emf;
    // @PersistenceContext(unitName = "LocalServicesMySQL")
    private EntityManager em;
    private EntityTransaction transaction;

    @Inject @Any
    private Instance<ZipCodeChecker> zipCodeCheckerInstance;

    @Deployment
    public static WebArchive createDeployment() {

        File[] dependencies = Maven.resolver().resolve(
                "org.slf4j:slf4j-simple:1.7.7"
        )
                .withoutTransitivity().asFile();

       /*  WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war")
                .addClass(ZipCodeChecker.class)
                .addClass(ZipCodeCheckerFactory.class)
                .addClass(CountryZipCodeChecker.class)
                .addPackage(NaturalPerson.class.getPackage())
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsManifestResource("META-INF/persistence.xml","persistence.xml");
        */

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
    public void shouldCreateNewNaturalPerson() throws Exception {

        Date dateOfBirth = new GregorianCalendar(1988, Calendar.OCTOBER, 3).getTime();

        // create instance of natural person entity
        NaturalPerson naturalPerson = new NaturalPerson("michzio@hotmail.com", "michzio", "pP123#", "Michał", "Ziobro", dateOfBirth , Gender.male);

        // persist the user account to the database
        transaction.begin();
        em.persist(naturalPerson);

        assertNotNull("Natural person should be set.", naturalPerson.getUserId());

        em.remove(naturalPerson);

        transaction.commit();

    }

    @Test
    public void shouldSetAddressOnNaturalPerson() throws Exception {

        Date dateOfBirth = new GregorianCalendar(1988, Calendar.OCTOBER, 3).getTime();

        // create instance of natural person entity
        NaturalPerson naturalPerson = new NaturalPerson("michzio@hotmail.com", "michzio", "pP123#", "Michał", "Ziobro", dateOfBirth, Gender.male);

        naturalPerson.setHomeAddress(new Address("Wolica Piaskowa", "156", null, "39-120", "Sędziszów Małopolski", "podkarpackie", "Poland"));
        naturalPerson.setDeliveryAsHome(true);

        // persist the user account to the database
        transaction.begin();
        em.persist(naturalPerson);

        assertNotNull("Wolica Piaskowa.", naturalPerson.getHomeAddress().getStreet());

        em.remove(naturalPerson);

        transaction.commit();
    }

    @Test
    public void shouldCheckZipCodeFormat() {

        ZipCodeChecker zipCodeChecker = zipCodeCheckerInstance.select(new CountryQualifierLiteral("Poland")).get();

        assertTrue(zipCodeChecker.isFormatValid("39-120"));

        assertFalse(zipCodeChecker.isFormatValid("560901"));
    }


} 
