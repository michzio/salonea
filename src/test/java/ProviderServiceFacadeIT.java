import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.interfaces.ProviderFacadeInterface;
import pl.salonea.ejb.interfaces.ProviderServiceFacadeInterface;
import pl.salonea.ejb.interfaces.ServiceFacadeInterface;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.Provider;
import pl.salonea.entities.ProviderService;
import pl.salonea.entities.Service;
import pl.salonea.entities.idclass.ProviderServiceId;
import pl.salonea.enums.ProviderType;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * ProviderServiceFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jul 26, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class ProviderServiceFacadeIT {

    private static final Logger logger = Logger.getLogger(ProviderServiceFacadeIT.class.getName());

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
    private ProviderServiceFacadeInterface.Local providerServiceFacade;

    @Inject
    private ProviderFacadeInterface.Local providerFacade;

    @Inject
    private ServiceFacadeInterface.Local serviceFacade;

    @Inject
    private UserTransaction utx;

    // TODO implement ProviderServiceFacade Integration Tests

    @Test
    public void shouldCreateNewProviderService() throws Exception {

        // create instance of Provider entity
        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider = new Provider("firma2@allegro.pl", "allegro2", "aAle2@", "Allegro 2 Ltd.",
                "2234567890", "2234567890", address, "Allegro Polska", ProviderType.SIMPLE);

        // create instance of Service entity
        Service service = new Service("Haircut");

        // create instance of ProviderService entity
        ProviderService providerService = new ProviderService(provider, service, 1800000L /* 30 min */);

        utx.begin();
        providerFacade.create(provider);
        serviceFacade.create(service);
        providerServiceFacade.create(providerService);
        utx.commit();

        assertNotNull("Provider ID should not be null.", provider.getUserId());
        assertNotNull("Service ID should not be null.", service.getServiceId());

        assertTrue(providerServiceFacade.count() == 1);
        assertEquals(providerService.getProvider(), provider);
        assertEquals(providerService.getService(), service);

        utx.begin();
        ProviderService foundProviderService =
                providerServiceFacade.find(new ProviderServiceId(provider.getUserId(), service.getServiceId()));
        assertEquals("Persisted and found provider service should be the same.", foundProviderService, providerService);

        providerServiceFacade.remove(foundProviderService);
        utx.commit();

        providerFacade.remove(provider);
        serviceFacade.remove(service);

        assertTrue(providerServiceFacade.count() == 0);

    }

    @Test
    public void shouldFindByProviderOrService() throws Exception {

        // create some instances of Provider entity
        Address address1 =  new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider1 = new Provider("firma@allegro.pl", "allegro", "aAle2@_", "Allegro Ltd.",
                "2234567890", "2234567890", address1, "Allegro Polska", ProviderType.SIMPLE);

        Address address2 = new Address("Wrocławska", "45", "10-140", "Szczecin", "Zachodnio Pomorskie", "Poland");
        Provider provider2 = new Provider("firma@tieto.pl", "tieto", "tIe%13?", "Tieto Sp. z o.o.",
                "6593878688", "6510029930", address2, "Tieto Poland", ProviderType.SIMPLE);

        Address address3 = new Address("Kijowska", "09", "20-160", "Lublin", "Lubelskie", "Poland");
        Provider provider3 = new Provider("firma@fryzjerka.pl", "fryzjerka_pl", "fRyZU123?", "Fryzjerka Sp. z o.o.",
                "1910020030", "1930040050", address3, "Fryzjerka Polska", ProviderType.SIMPLE);

        Address address4 = new Address("Pomorska", "12", "99-200", "Gdańsk", "Pomorze", "Poland");
        Provider provider4 = new Provider("kontakt@przeprowadzki24.pl", "przeprowadzki24", "prZEP_M24%", "Przeprowadzki24 Sp. z o.o.",
                "4530040050", "4530040050", address4, "Przeprowadzki24 Pomorze", ProviderType.SIMPLE);

        // create some instances of Service entity
        Service haircut = new Service("Haircut");
        Service fillingCavities = new Service("Filling Cavities");
        Service hairDyeing = new Service("Hair Dyeing");
        Service removals = new Service("Removals");

        // create instances of ProviderService entity
        ProviderService provider1Filling = new ProviderService(provider1, fillingCavities, 1800000L /* 30 min */);
        provider1Filling.setPrice(100.0);
        provider1Filling.setDescription("A dental restoration or dental filling is a dental restorative material used to restore the function, integrity and morphology of missing tooth structure.");
            ProviderService provider1Removals = new ProviderService(provider1, removals, 1800000L /* 30 min */);
            provider1Removals.setPrice(60.0);
            provider1Removals.setDescription("A moving company, van line is a company that helps people and businesses move their goods from one place to another.");
        ProviderService provider2Filling = new ProviderService(provider2, fillingCavities, 1800000L /* 30 min */);
        provider2Filling.setPrice(150.0);
        provider2Filling.setDiscount((short) 10 /* [%] */);
        provider2Filling.setDescription("A dental restoration or dental filling is a dental restorative material used to restore the function, integrity and morphology of missing tooth structure.");
            ProviderService provider3Haircut = new ProviderService(provider3, haircut, 1800000L /* 30 min */);
            provider3Haircut.setPrice(50.0);
            provider3Haircut.setDescription("A hairstyle, hairdo, or haircut refers to the styling of hair, usually on the human scalp.");
        ProviderService provider3Dyeing = new ProviderService(provider3, hairDyeing, 1800000L /* 30 min */);
        provider3Dyeing.setPrice(80.0);
        provider3Dyeing.setDiscount((short) 50 /* [%] */);
        provider3Dyeing.setDescription("Hair coloring is the practice of changing the color of hair. ");
            ProviderService provider4Removals = new ProviderService(provider4, removals, 1800000L /* 30 min */);
            provider4Removals.setPrice(75.0);
            provider4Removals.setDescription("A moving company, removalist is a company that helps people and businesses move their goods from one place to another.");

        utx.begin();
        providerFacade.create(provider1);
        providerFacade.create(provider2);
        providerFacade.create(provider3);
        providerFacade.create(provider4);

        serviceFacade.create(haircut);
        serviceFacade.create(fillingCavities);
        serviceFacade.create(hairDyeing);
        serviceFacade.create(removals);

        providerServiceFacade.create(provider1Filling);
        providerServiceFacade.create(provider1Removals);
        providerServiceFacade.create(provider2Filling);
        providerServiceFacade.create(provider3Haircut);
        providerServiceFacade.create(provider3Dyeing);
        providerServiceFacade.create(provider4Removals);
        utx.commit();

        assertTrue(providerServiceFacade.count() == 6);

        List<ProviderService> provider1Services =  providerServiceFacade.findByProvider(provider1);
        assertTrue("There should be two provider services for given provider.", provider1Services.size() == 2);
        assertTrue(provider1Services.contains(provider1Filling));
        assertTrue(provider1Services.contains(provider1Removals));

        List<ProviderService> provider2Services = providerServiceFacade.findByProvider(provider2);
        assertTrue("There should be only one provider service for given provider.", provider2Services.size() == 1);
        assertTrue(provider2Services.contains(provider2Filling));

        List<ProviderService> haircutOffsers = providerServiceFacade.findByService(haircut);
        assertTrue("There should be only one haircut offer.", haircutOffsers.size() == 1);
        assertTrue(haircutOffsers.contains(provider3Haircut));

        List<ProviderService> removalsOffers = providerServiceFacade.findByService(removals);
        assertTrue("There should be two removals offers.", removalsOffers.size() == 2);
        assertTrue(removalsOffers.contains(provider1Removals));
        assertTrue(removalsOffers.contains(provider4Removals));

        List<ProviderService> hairRelatedServices = providerServiceFacade.findByDescription("hair");
        assertTrue("There should be two hair related provider services.", hairRelatedServices.size() == 2);
        assertTrue(hairRelatedServices.contains(provider3Dyeing));
        assertTrue(hairRelatedServices.contains(provider3Haircut));

        List<ProviderService> colorChangingOffers = providerServiceFacade.findByProviderAndDescription(provider3, "changing the color");
        assertTrue(colorChangingOffers.contains(provider3Dyeing));

        List<ProviderService> removalistOffers = providerServiceFacade.findByServiceAndDescription(removals, "removalist");
        assertTrue(removalistOffers.contains(provider4Removals));

        List<ProviderService> cheapDentalFillings = providerServiceFacade.findByServiceAndPrice(fillingCavities, 90.0, 140.0);
        List<ProviderService> priceyDentalFillings = providerServiceFacade.findByServiceAndPrice(fillingCavities, 140.0, Double.MAX_VALUE);
        assertTrue(cheapDentalFillings.contains(provider1Filling));
        assertTrue(priceyDentalFillings.contains(provider2Filling));

        List<ProviderService> cheapDentalFillingsDiscounted = providerServiceFacade.findByServiceAndDiscountedPrice(fillingCavities, 90.0, 140.0);
        List<ProviderService> priceyDentalFillingsDiscounted = providerServiceFacade.findByServiceAndDiscountedPrice(fillingCavities, 140.0, Double.MAX_VALUE);
        assertTrue(cheapDentalFillingsDiscounted.size() == 2);
        assertTrue(priceyDentalFillingsDiscounted.size() == 0);
        assertTrue(cheapDentalFillingsDiscounted.contains(provider1Filling));
        assertTrue(cheapDentalFillingsDiscounted.contains(provider2Filling));

        List<ProviderService> discountedDentalFillingOffers = providerServiceFacade.findByServiceAndDiscount(fillingCavities, (short) 1, (short) 99);
        assertTrue(discountedDentalFillingOffers.size() == 1);
        assertTrue(discountedDentalFillingOffers.contains(provider2Filling));

        List<ProviderService> discountedProvider3Offers = providerServiceFacade.findByProviderAndDiscount(provider3, (short) 1, (short) 99);
        assertTrue(discountedProvider3Offers.contains(provider3Dyeing));
        assertFalse(discountedProvider3Offers.contains(provider3Haircut));

        assertTrue(providerServiceFacade.deleteForProvider(provider1) == 2);
        assertTrue(providerServiceFacade.deleteForProvider(provider2) == 1);
        assertTrue(providerServiceFacade.deleteForProvider(provider3) == 2);
        assertTrue(providerServiceFacade.deleteForProvider(provider4) == 1);

        assertTrue(providerServiceFacade.count() == 0);

        providerFacade.remove(provider4);
        providerFacade.remove(provider3);
        providerFacade.remove(provider2);
        providerFacade.remove(provider1);

        assertTrue(providerFacade.count() == 0);

        serviceFacade.remove(removals);
        serviceFacade.remove(hairDyeing);
        serviceFacade.remove(fillingCavities);
        serviceFacade.remove(haircut);

        assertTrue(serviceFacade.count() == 0);

    }

    @Test
    public void shouldFindByMultipleCriteria() throws Exception {

        // create some instances of Provider entity
        Address address1 =  new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider1 = new Provider("firma@allegro.pl", "allegro", "aAle2@_", "Allegro Ltd.",
                "2234567890", "2234567890", address1, "Allegro Polska", ProviderType.SIMPLE);

        Address address2 = new Address("Wrocławska", "45", "10-140", "Szczecin", "Zachodnio Pomorskie", "Poland");
        Provider provider2 = new Provider("firma@tieto.pl", "tieto", "tIe%13?", "Tieto Sp. z o.o.",
                "6593878688", "6510029930", address2, "Tieto Poland", ProviderType.SIMPLE);

        Address address3 = new Address("Kijowska", "09", "20-160", "Lublin", "Lubelskie", "Poland");
        Provider provider3 = new Provider("firma@fryzjerka.pl", "fryzjerka_pl", "fRyZU123?", "Fryzjerka Sp. z o.o.",
                "1910020030", "1930040050", address3, "Fryzjerka Polska", ProviderType.SIMPLE);

        Address address4 = new Address("Pomorska", "12", "99-200", "Gdańsk", "Pomorze", "Poland");
        Provider provider4 = new Provider("kontakt@przeprowadzki24.pl", "przeprowadzki24", "prZEP_M24%", "Przeprowadzki24 Sp. z o.o.",
                "4530040050", "4530040050", address4, "Przeprowadzki24 Pomorze", ProviderType.SIMPLE);

        // create some instances of Service entity
        Service haircut = new Service("Haircut");
        Service fillingCavities = new Service("Filling Cavities");
        Service hairDyeing = new Service("Hair Dyeing");
        Service removals = new Service("Removals");

        // create instances of ProviderService entity
        ProviderService provider1Filling = new ProviderService(provider1, fillingCavities, 1800000L /* 30 min */);
        provider1Filling.setPrice(100.0);
        provider1Filling.setDescription("A dental restoration or dental filling is a dental restorative material used to restore the function, integrity and morphology of missing tooth structure.");
        ProviderService provider1Removals = new ProviderService(provider1, removals, 1800000L /* 30 min */);
        provider1Removals.setPrice(60.0);
        provider1Removals.setDescription("A moving company, van line is a company that helps people and businesses move their goods from one place to another.");
        ProviderService provider2Filling = new ProviderService(provider2, fillingCavities, 1800000L /* 30 min */);
        provider2Filling.setPrice(150.0);
        provider2Filling.setDiscount((short) 10 /* [%] */);
        provider2Filling.setDescription("A dental restoration or dental filling is a dental restorative material used to restore the function, integrity and morphology of missing tooth structure.");
        ProviderService provider3Haircut = new ProviderService(provider3, haircut, 1800000L /* 30 min */);
        provider3Haircut.setPrice(50.0);
        provider3Haircut.setDescription("A hairstyle, hairdo, or haircut refers to the styling of hair, usually on the human scalp.");
        ProviderService provider3Dyeing = new ProviderService(provider3, hairDyeing, 1800000L /* 30 min */);
        provider3Dyeing.setPrice(80.0);
        provider3Dyeing.setDiscount((short) 50 /* [%] */);
        provider3Dyeing.setDescription("Hair coloring is the practice of changing the color of hair.");
        ProviderService provider4Removals = new ProviderService(provider4, removals, 1800000L /* 30 min */);
        provider4Removals.setPrice(75.0);
        provider4Removals.setDescription("A moving company, removalist is a company that helps people and businesses move their goods from one place to another.");

        utx.begin();
        providerFacade.create(provider1);
        providerFacade.create(provider2);
        providerFacade.create(provider3);
        providerFacade.create(provider4);

        serviceFacade.create(haircut);
        serviceFacade.create(fillingCavities);
        serviceFacade.create(hairDyeing);
        serviceFacade.create(removals);

        providerServiceFacade.create(provider1Filling);
        providerServiceFacade.create(provider1Removals);
        providerServiceFacade.create(provider2Filling);
        providerServiceFacade.create(provider3Haircut);
        providerServiceFacade.create(provider3Dyeing);
        providerServiceFacade.create(provider4Removals);
        utx.commit();

        assertTrue(providerServiceFacade.count() == 6);

        List<Provider> providers = new ArrayList<>();
        providers.add(provider1);
        providers.add(provider3);
        List<Service> services = new ArrayList<>();
        services.add(fillingCavities);
        services.add(removals);
        services.add(haircut);

        List<ProviderService> matchingOffers = providerServiceFacade.findByMultipleCriteria(providers, services, null, null, null, null, false, null, null, null, null);
        assertTrue("There should be three offers matching given combination of providers and services",
                matchingOffers.size() == 3);
        assertTrue(matchingOffers.contains(provider1Filling));
        assertTrue(matchingOffers.contains(provider1Removals));
        assertTrue(matchingOffers.contains(provider3Haircut));
        assertFalse(matchingOffers.contains(provider3Dyeing));

        List<Service> hairServices = new ArrayList<>();
        hairServices.add(haircut);
        hairServices.add(hairDyeing);

        List<ProviderService> cheapHairOffers = providerServiceFacade.findByMultipleCriteria(null, hairServices, null, "hair", 40.0, 70.0, false, null, null, null, null );
        assertTrue(cheapHairOffers.size() == 1);
        assertTrue(cheapHairOffers.contains(provider3Haircut));
        assertFalse(cheapHairOffers.contains(provider3Dyeing));

        List<ProviderService> cheapDiscountedHairOffers = providerServiceFacade.findByMultipleCriteria(null, hairServices, null, "hair", 40.0, 70.0, true, null, null, null, null);
        assertTrue(cheapDiscountedHairOffers.size() == 2);
        assertTrue(cheapDiscountedHairOffers.contains(provider3Haircut));
        assertTrue(cheapDiscountedHairOffers.contains(provider3Dyeing));

        providers.add(provider2);
        List<ProviderService> discountedOffers = providerServiceFacade.findByMultipleCriteria(providers, null, null, null, null, null, false, (short) 10, (short) 60, null, null);
        assertTrue(discountedOffers.size() == 2);
        assertTrue(discountedOffers.contains(provider2Filling));
        assertTrue(discountedOffers.contains(provider3Dyeing));

        assertTrue(providerServiceFacade.deleteForProvider(provider1) == 2);
        assertTrue(providerServiceFacade.deleteForProvider(provider2) == 1);
        assertTrue(providerServiceFacade.deleteForProvider(provider3) == 2);
        assertTrue(providerServiceFacade.deleteForProvider(provider4) == 1);

        assertTrue(providerServiceFacade.count() == 0);

        providerFacade.remove(provider4);
        providerFacade.remove(provider3);
        providerFacade.remove(provider2);
        providerFacade.remove(provider1);

        assertTrue(providerFacade.count() == 0);

        serviceFacade.remove(removals);
        serviceFacade.remove(hairDyeing);
        serviceFacade.remove(fillingCavities);
        serviceFacade.remove(haircut);

        assertTrue(serviceFacade.count() == 0);
    }
}