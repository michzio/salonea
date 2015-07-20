import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.stateless.FirmFacade;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.Firm;

import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * FirmFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jul 17, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class FirmFacadeIT {

    private static final Logger logger = Logger.getLogger(FirmFacadeIT.class.getName());

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
    private FirmFacade firmFacade;

    @Test
    public void shouldCreateAFirm() {

        // create instance of Firm
        Firm firm = new Firm("firma@allegro.pl", "allegro", "aAle2@", "Allegro Ltd.");

        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        firm.setAddress(address);

        firm.setVatin("1234567890");
        firm.setCompanyNumber("1234567890");

        // persists the Firm in the database
        firm = firmFacade.create(firm);

        assertNotNull("Firm ID should not be null.", firm.getUserId());
        assertTrue("Number of firms should be one.", firmFacade.count() == 1);

        firmFacade.getEntityManager().detach(firm);
        Firm foundFirm = firmFacade.find(firm.getUserId());

        assertEquals(firm, foundFirm);

        firmFacade.remove(foundFirm);

        assertTrue("There should not be any firm in database.", firmFacade.count() == 0);
    }

    @Test
    public void shouldFindFirmByAddress() {

        // create instance of Firm
        Firm firm1 = new Firm("firma@allegro.pl", "allegro", "aAle2@", "Allegro Ltd.");

        Address address1 = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        firm1.setAddress(address1);

        firm1.setVatin("1234567890");
        firm1.setCompanyNumber("1234567890");

        Firm firm2 = new Firm("firma@onet.pl", "onet", "oNet2!", "Onet Gmbh");

        Address address2 = new Address("Wrocławska", "10", "29-110", "Poznań", "Wielkopolska", "Poland");
        firm2.setAddress(address2);

        firm2.setVatin("1234567891");
        firm2.setCompanyNumber("1234567891");

        // persist the Firm in the database
        firm1 = firmFacade.create(firm1);
        firm2 = firmFacade.create(firm2);

        assertNotNull("Firm 1 ID should not be null.", firm1.getUserId());
        assertNotNull("Firm 2 ID should not be null.", firm2.getUserId());

        assertTrue("Number of firms should be two.", firmFacade.count() == 2);

        List<Firm> firms = firmFacade.findByAddress("Pozn", "lkop", "Poland", "", "29");

        assertTrue("There should be 2 firms in Poznań.", firms.size() == 2);
        assertTrue(firms.contains(firm1));
        assertTrue(firms.contains(firm2));

        assertTrue(firmFacade.deleteWithCompanyNumber("1234567891"));

        assertTrue(firmFacade.count() == 1);
        assertTrue(firmFacade.findByName("onet").size() == 0);
        assertTrue(firmFacade.findByName("allegro").size() == 1);

        assertTrue(firmFacade.deleteWithCompanyNumber("1234567890"));

        assertTrue(firmFacade.count() == 0);

    }

}