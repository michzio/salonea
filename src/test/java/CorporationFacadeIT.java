import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.interfaces.CorporationFacadeInterface;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.Corporation;

import javax.inject.Inject;
import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * CorporationFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jul 22, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class CorporationFacadeIT {

    private static final Logger logger = Logger.getLogger(CorporationFacadeIT.class.getName());

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
    private CorporationFacadeInterface.Local corporationFacade;

    @Test
    public void shouldCreateCorporation() {

        // create instance of Corporation
        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Corporation corporation = new Corporation("Mc'Donalds", "mcdonalds.png", address);

        corporation = corporationFacade.create(corporation);

        assertNotNull("Corporation ID should not be null.", corporation.getCorporationId());

        Corporation foundCorporation = corporationFacade.find(corporation.getCorporationId());

        assertEquals(corporation, foundCorporation);

        corporationFacade.remove(foundCorporation);

        assertTrue("There should not be any Corporation.", corporationFacade.count() == 0);
    }

    @Test
    public void shouldFindCorporationByName() {

        // create some instances of Corporation
        Address address1 = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Corporation corporation1 = new Corporation("Allegro", "allegro_logo.png", address1);
        Address address2 = new Address("Wrocławska", "25", "29-100", "Poznań", "Wielkopolska", "Poland");
        Corporation corporation2 = new Corporation("Lego", "lego_logo.png", address2);

        corporationFacade.create(corporation1);
        corporationFacade.create(corporation2);

        List<Corporation> corpos = corporationFacade.findByName("leg");

        assertTrue("There should be two corporations.", corpos.size() == 2);

        assertTrue(corpos.contains(corporation1));
        assertTrue(corpos.contains(corporation2));

        corporationFacade.remove(corporation2);
        corporationFacade.remove(corporation1);
    }

    @Test
    public void shouldFindByOpeningDate() {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -20);

        // create some instances of Corporation
        Address address1 = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Corporation corporation1 = new Corporation("Allegro", "allegro_logo.png", address1);
        corporation1.setOpeningDate(cal.getTime());

        Address address2 = new Address("Wrocławska", "25", "29-100", "Poznań", "Wielkopolska", "Poland");
        Corporation corporation2 = new Corporation("Lego", "lego_logo.png", address2);
        cal.add(Calendar.YEAR, -50);
        corporation2.setOpeningDate(cal.getTime());

        Address address3 = new Address("Szwedzka", "50", "30-150", "Gdańsk", "Pomorze", "Poland");
        Corporation corporation3 = new Corporation("Amazon", "amazon_logo.png", address3);
        cal.add(Calendar.YEAR, + 60);
        corporation3.setOpeningDate(cal.getTime());

        corporationFacade.create(corporation1);
        corporationFacade.create(corporation2);
        corporationFacade.create(corporation3);

        cal.add(Calendar.YEAR, -15);
        List<Corporation> oldCorpos = corporationFacade.findOpenBefore(cal.getTime());
        List<Corporation> newCorpos = corporationFacade.findOpenAfter(cal.getTime());

        assertTrue("There should be only one legacy corporation.", oldCorpos.size() == 1);
        assertTrue("There should be two new corporations.", newCorpos.size() == 2);
        assertTrue(oldCorpos.contains(corporation2));
        assertTrue(newCorpos.contains(corporation1));
        assertTrue(newCorpos.contains(corporation3));

        corporationFacade.remove(corporation3);
        corporationFacade.remove(corporation2);
        corporationFacade.remove(corporation1);

    }

    @Test
    public void shouldFindByAddress() {

        // create some instances of Corporation
        Address address1 = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Corporation corporation1 = new Corporation("Allegro", "allegro_logo.png", address1);

        Address address2 = new Address("Wrocławska", "25", "29-100", "Poznań", "Wielkopolska", "Poland");
        Corporation corporation2 = new Corporation("Lego", "lego_logo.png", address2);

        Address address3 = new Address("Szwedzka", "50", "30-150", "Gdańsk", "Pomorze", "Poland");
        Corporation corporation3 = new Corporation("Amazon", "amazon_logo.png", address3);

        corporationFacade.create(corporation1);
        corporationFacade.create(corporation2);
        corporationFacade.create(corporation3);

        List<Corporation> corposByCity = corporationFacade.findByAddress("Poznań", null, null, null, null);
        assertTrue("There should be two corporations in given city.", corposByCity.size() == 2);

        List<Corporation> corposByState = corporationFacade.findByAddress(null, "Wielkopolska", null, null, null);
        assertTrue("There should be two corporations in given state.", corposByState.size() == 2);

        List<Corporation> corposByZipCode = corporationFacade.findByAddress(null, null, null, null, "29-");
        assertTrue("There should be two corporations for given zip code.", corposByZipCode.size() == 2);

        List<Corporation> corposByStreet = corporationFacade.findByAddress("Gdańsk", null, null, "Szwedzka", null);
        assertTrue("There should be only one corporation for given city and street combination", corposByStreet.size() == 1);

        corporationFacade.remove(corporation3);
        corporationFacade.remove(corporation2);
        corporationFacade.remove(corporation1);

    }

}
