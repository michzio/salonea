import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.interfaces.ProviderFacadeInterface;
import pl.salonea.ejb.interfaces.ServicePointFacadeInterface;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.Provider;
import pl.salonea.entities.Service;
import pl.salonea.entities.ServicePoint;
import pl.salonea.entities.idclass.ServicePointId;
import pl.salonea.enums.ProviderType;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * ServicePointFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Aug 4, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class ServicePointFacadeIT {

    private static final Logger logger = Logger.getLogger(ServicePointFacadeIT.class.getName());

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
    private ServicePointFacadeInterface.Local pointFacade;

    @Inject
    private ProviderFacadeInterface.Local providerFacade;

    @Inject
    UserTransaction utx;

    @Test
    public void shouldCreateNewServicePoint() throws Exception {

        // create instance of Provider entity
        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider = new Provider("firma2@allegro.pl", "allegro2", "aAle2@", "Allegro 2 Ltd.",
                "2234567890", "2234567890", address, "Allegro Polska", ProviderType.SIMPLE);

        // create instance of ServicePoint entity
        ServicePoint servicePoint = new ServicePoint(provider, 1, address);

        // persist both entities
        utx.begin();
        providerFacade.create(provider);
        pointFacade.create(servicePoint);
        utx.commit();

        assertTrue("There should be one persisted Service Point entity in database.", pointFacade.count() == 1);

        // find Service Point by ID
        utx.begin();
        ServicePoint foundServicePoint = pointFacade.find(new ServicePointId(provider.getUserId(), servicePoint.getServicePointNumber()));

        assertEquals("Both persisted and found Service Point entity should be the same.", servicePoint, foundServicePoint);

        // remove Service Point
        pointFacade.remove(foundServicePoint);
        utx.commit();

        providerFacade.remove(provider);

        assertTrue("There should not be any Service Point entity in database.", pointFacade.count() == 0);
        assertTrue("There should not be any Provider entity in database.", providerFacade.count() == 0);
    }

    @Test
    public void shouldFindServicePointByProvider() throws Exception {

        // create some instances of Provider entity
        Address address1 =  new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider1 = new Provider("firma@fryzjer.pl", "fryzjer_pl", "aAle2@_", "FryzjerPl Sp. z o.o..",
                "2234567890", "2234567890", address1, "FryzjerPl", ProviderType.SIMPLE);

        Address address2 = new Address("Wrocławska", "45", "10-140", "Szczecin", "Zachodnio Pomorskie", "Poland");
        Provider provider2 = new Provider("firma@medical.pl", "medical", "tIe%13?", "Medical Sp. z o.o.",
                "6593878688", "6510029930", address2, "Medical Poland", ProviderType.SIMPLE);

        // create some instances of Service Point entity
        ServicePoint point11  = new ServicePoint(provider1, 1, address1);
        ServicePoint point12 = new ServicePoint(provider1, 2, address1);
        ServicePoint point13 = new ServicePoint(provider1, 3, address1);

        ServicePoint point21 = new ServicePoint(provider2, 1, address2);
        ServicePoint point22 = new ServicePoint(provider2, 2, address2);

        // persist entities in database
        utx.begin();
        providerFacade.create(provider1);
        providerFacade.create(provider2);

        pointFacade.create(point11);
        pointFacade.create(point12);
        pointFacade.create(point13);
        pointFacade.create(point21);
        pointFacade.create(point22);
        utx.commit();

        assertTrue("There should be persisted five Service Points.", pointFacade.count() == 5);
        assertTrue("There should be persisted two Providers.", providerFacade.count() == 2);

        List<ServicePoint> points1 = pointFacade.findByProvider(provider1);
        List<ServicePoint> points2 = pointFacade.findByProvider(provider2);

        assertTrue("There should be three Service Points for first Provider.", points1.size() == 3);
        assertTrue(points1.contains(point11) && points1.contains(point12) && points1.contains(point13));
        assertTrue("There should be two Service Points for second Provider.", points2.size() == 2);
        assertTrue(points2.contains(point21) && points2.contains(point22));

        List<Provider> providers = new ArrayList<>();
        providers.add(provider1);
        providers.add(provider2);
        List<ServicePoint> points = pointFacade.findByMultipleCriteria(providers, null, null, null, null, null);
        assertTrue("There should be five Service Points for both Providers.", points.size() == 5);

        pointFacade.deleteByProvider(provider1);
        assertTrue("There should left only two Service Point entities in database.", pointFacade.count() == 2);
        pointFacade.deleteByProvider(provider2);
        assertTrue("There should not remain any Service Point entity in database.", pointFacade.count() == 0);

        providerFacade.remove(provider1);
        providerFacade.remove(provider2);
    }

    @Test
    public void shouldFindServicePointByCoordinates() throws Exception {

        // create some instances of Provider entity
        Address address11 =  new Address("Poznanska", "25", "29-100", "Poznan", "Wielkopolska", "Poland");
        Address address12 = new Address("Wielkopolska", "15", "29-100", "Poznan", "Wielkopolska", "Poland");
        Address address13 = new Address("Warszawska", "50", "61-066", "Poznan", "Wielkopolska", "Poland");
        Provider provider1 = new Provider("firma@fryzjer.pl", "fryzjer_pl", "aAle2@_", "FryzjerPl Sp. z o.o..",
                "2234567890", "2234567890", address11, "FryzjerPl", ProviderType.SIMPLE);

        Address address21 = new Address("Wroclawska", "45", "10-140", "Szczecin", "Zachodnio Pomorskie", "Poland");
        Address address22 = new Address("Pomorska", "150", "10-140", "Szczecin", "Zachodnio Pomorskie", "Poland");
        Provider provider2 = new Provider("firma@medical.pl", "medical", "tIe%13?", "Medical Sp. z o.o.",
                "6593878688", "6510029930", address21, "Medical Poland", ProviderType.SIMPLE);

        // create some instances of Service Point entity
        ServicePoint point11  = new ServicePoint(provider1, 1, address11);
                     point11.setLongitudeWGS84(16.908212f);
                     point11.setLatitudeWGS84(52.413730f);
        ServicePoint point12 = new ServicePoint(provider1, 2, address12);
                     point12.setLongitudeWGS84(16.916990f);
                     point12.setLatitudeWGS84(52.417640f);
        ServicePoint point13 = new ServicePoint(provider1, 3, address13);
                     point13.setLongitudeWGS84(16.968580f);
                     point13.setLatitudeWGS84(52.409369f);

        ServicePoint point21 = new ServicePoint(provider2, 1, address21);
                     point21.setLongitudeWGS84(14.491800f);
                     point21.setLatitudeWGS84(53.416719f);
        ServicePoint point22 = new ServicePoint(provider2, 2, address22);
                     point22.setLatitudeWGS84(14.672827f);
                     point22.setLongitudeWGS84(53.394454f);

        // persist entities in database
        utx.begin();
        providerFacade.create(provider1);
        providerFacade.create(provider2);

        pointFacade.create(point11);
        pointFacade.create(point12);
        pointFacade.create(point13);
        pointFacade.create(point21);
        pointFacade.create(point22);
        utx.commit();

        assertTrue("There should be persisted five Service Points.", pointFacade.count() == 5);
        assertTrue("There should be persisted two Providers.", providerFacade.count() == 2);

        // find by address details
        List<ServicePoint> points = pointFacade.findByAddress("Poznan", null, null, null, null);
        assertTrue(points.size() == 3);
        assertTrue(pointFacade.findByAddress(null, null, "Poland", null, null).size() == 5);
        assertTrue(pointFacade.findByAddress(null, null, null, "Po", null).size() == 2);
        assertTrue(pointFacade.findByAddress(null, null, null, null, "10").size() == 4);


        pointFacade.deleteByProvider(provider1);
        assertTrue("There should left only two Service Point entities in database.", pointFacade.count() == 2);
        pointFacade.deleteByProvider(provider2);
        assertTrue("There should not remain any Service Point entity in database.", pointFacade.count() == 0);

        providerFacade.remove(provider1);
        providerFacade.remove(provider2);

    }

}
