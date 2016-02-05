
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
import pl.salonea.ejb.interfaces.ServiceCategoryFacadeInterface;
import pl.salonea.ejb.interfaces.ServiceFacadeInterface;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.Provider;
import pl.salonea.entities.ProviderService;
import pl.salonea.entities.Service;
import pl.salonea.entities.ServiceCategory;
import pl.salonea.enums.PriceType;
import pl.salonea.enums.ProviderType;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * ServiceFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jul 31, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class ServiceFacadeIT {

    private static final Logger logger = Logger.getLogger(ServiceFacadeIT.class.getName());

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
    private ServiceFacadeInterface.Local serviceFacade;

    @Inject
    private ProviderFacadeInterface.Local providerFacade;

    @Inject
    private ProviderServiceFacadeInterface.Local providerServiceFacade;

    @Inject
    private ServiceCategoryFacadeInterface.Local categoryFacade;

    @Inject
    private UserTransaction utx;

    // TODO implement ServiceFacade Integration Tests

    @Test
    public void shouldCreateNewService() {

        // create instance of Service entity
        Service service = new Service("Hair washing");
        serviceFacade.create(service);

        assertNotNull("Service ID should not be null.", service.getServiceId());
        assertTrue("There should be one service entity in database.", serviceFacade.count() == 1);

        // find Service by ID
        Service foundService = serviceFacade.find(service.getServiceId());

        assertEquals("Persisted and found service entity should be the same.", foundService, service);

        // remove found Service entity from database
        serviceFacade.remove(foundService);

        assertTrue("There should not be any Service entity in database.", serviceFacade.count() == 0);

    }

    @Test
    public void shouldFindByKeyword() {

        // create some instances of Service entity
        Service hairWashing = new Service("Hair washing");
        hairWashing.setDescription("Hair washing is the cosmetic act of keeping hair clean by washing it with shampoo or other detergent products and water. Hair conditioner may also be used to improve hair's texture and manageability.");
            Service haircut = new Service("Haircut");
            haircut.setDescription("A hairstyle, hairdo, or haircut refers to the styling of hair, usually on the human scalp.");
        Service dentalFillings = new Service("Dental fillings");
        dentalFillings.setDescription("A dental restoration or dental filling is a dental restorative material used to restore the function, integrity and morphology of missing tooth structure.");
            Service hairDyeing = new Service("Hair dyeing");
            hairDyeing.setDescription("Hair coloring is the practice of changing the color of hair.");
        Service removals = new Service("Removals");
        removals.setDescription("A moving company, removalist is a company that helps people and businesses move their goods from one place to another.");

        serviceFacade.create(hairWashing);
        serviceFacade.create(haircut);
        serviceFacade.create(dentalFillings);
        serviceFacade.create(hairDyeing);
        serviceFacade.create(removals);

        assertTrue("There should be five Service entities.", serviceFacade.count() == 5);

        List<Service> hairServices = serviceFacade.findByName("hair");
        assertTrue("There should be three hair related Services.", hairServices.size() == 3);
        assertTrue(hairServices.contains(hairWashing));
        assertTrue(hairServices.contains(haircut));
        assertTrue(hairServices.contains(hairDyeing));

        List<Service> ofServices = serviceFacade.findByDescription("of");
        assertTrue("There should be three Services which description contains 'of' keyword.", ofServices.size() == 4);
        assertTrue(ofServices.contains(hairWashing));
        assertTrue(ofServices.contains(haircut));
        assertTrue(ofServices.contains(hairDyeing));
        assertTrue(ofServices.contains(dentalFillings));

        List<Service> ovServices = serviceFacade.findByKeyword("ov");
        assertTrue("There should be two Services which description contains 'ov' keyword", ovServices.size() == 2);
        assertTrue(ovServices.contains(hairWashing));
        assertTrue(ovServices.contains(removals));

        serviceFacade.deleteByName("Hair washing");
        serviceFacade.deleteByName("Haircut");
        serviceFacade.remove(dentalFillings);
        serviceFacade.remove(hairDyeing);
        serviceFacade.remove(removals);

        assertTrue(serviceFacade.count() == 0);
    }

    @Test
    public void shouldFindServicesByProvider() throws Exception {

        // create some instances of Provider entity
        Address address1 =  new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider1 = new Provider("firma@allegro.pl", "allegro", "aAle2@_", "Allegro Ltd.",
                "2234567890", "2234567890", address1, "Allegro Polska", ProviderType.SIMPLE);

        Address address2 = new Address("Wrocławska", "45", "10-140", "Szczecin", "Zachodnio Pomorskie", "Poland");
        Provider provider2 = new Provider("firma@tieto.pl", "tieto", "tIe%13?", "Tieto Sp. z o.o.",
                "6593878688", "6510029930", address2, "Tieto Poland", ProviderType.SIMPLE);

        // create some instances of Service entity
        Service haircut = new Service("Haircut");
        Service fillingCavities = new Service("Filling Cavities");
        Service hairDyeing = new Service("Hair Dyeing");
        Service removals = new Service("Removals");

        // create instances of ProviderService entity
        ProviderService provider1Filling = new ProviderService(provider1, fillingCavities, 1800000L /* 30 min */);
        provider1Filling.setPrice(100.0);
        provider1Filling.setPriceType(PriceType.PER_SERVICE);
        provider1Filling.setDescription("A dental restoration or dental filling is a dental restorative material used to restore the function, integrity and morphology of missing tooth structure.");
        ProviderService provider1Removals = new ProviderService(provider1, removals, 60*60*1000L /* 1h */);
        provider1Removals.setPrice(60.0);
        provider1Removals.setPriceType(PriceType.PER_HOUR);
        provider1Removals.setDescription("A moving company, van line is a company that helps people and businesses move their goods from one place to another.");
        ProviderService provider2Filling = new ProviderService(provider2, fillingCavities, 1800000L /* 30 min */);
        provider2Filling.setPrice(150.0);
        provider2Filling.setPriceType(PriceType.PER_SERVICE);
        provider2Filling.setDiscount((short) 10 /* [%] */);
        provider2Filling.setDescription("A dental restoration or dental filling is a dental restorative material used to restore the function, integrity and morphology of missing tooth structure.");
        ProviderService provider2Haircut = new ProviderService(provider2, haircut, 1800000L /* 30 min */);
        provider2Haircut.setPrice(50.0);
        provider2Haircut.setPriceType(PriceType.PER_SERVICE);
        provider2Haircut.setDescription("A hairstyle, hairdo, or haircut refers to the styling of hair, usually on the human scalp.");
        ProviderService provider2Dyeing = new ProviderService(provider2, hairDyeing, 1800000L /* 30 min */);
        provider2Dyeing.setPrice(80.0);
        provider2Dyeing.setPriceType(PriceType.PER_SERVICE);
        provider2Dyeing.setDiscount((short) 50 /* [%] */);
        provider2Dyeing.setDescription("Hair coloring is the practice of changing the color of hair.");

        utx.begin();
        providerFacade.create(provider1);
        providerFacade.create(provider2);

        serviceFacade.create(haircut);
        serviceFacade.create(fillingCavities);
        serviceFacade.create(hairDyeing);
        serviceFacade.create(removals);

        providerServiceFacade.create(provider1Filling);
        providerServiceFacade.create(provider1Removals);
        providerServiceFacade.create(provider2Filling);
        providerServiceFacade.create(provider2Haircut);
        providerServiceFacade.create(provider2Dyeing);
        utx.commit();

        assertTrue(providerServiceFacade.count() == 5);

        List<Service> provider1Services = serviceFacade.findByProvider(provider1);
        assertTrue(provider1Services.size() == 2);
        assertTrue(provider1Services.contains(fillingCavities));
        assertTrue(provider1Services.contains(removals));

        List<Service> provider2Services = serviceFacade.findByProvider(provider2);
        assertTrue(provider2Services.size() == 3);
        assertTrue(provider2Services.contains(fillingCavities));
        assertTrue(provider2Services.contains(haircut));
        assertTrue(provider2Services.contains(hairDyeing));

        assertTrue(providerServiceFacade.deleteForProvider(provider1) == 2);
        assertTrue(providerServiceFacade.deleteForProvider(provider2) == 3);

        assertTrue(providerServiceFacade.count() == 0);

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
        haircut.setDescription("A hairstyle, hairdo, or haircut refers to the styling of hair, usually on the human scalp.");
            Service fillingCavities = new Service("Filling Cavities");
            fillingCavities.setDescription("A dental restoration or dental filling is a dental restorative material used to restore the function, integrity and morphology of missing tooth structure.");
        Service hairDyeing = new Service("Hair Dyeing");
        hairDyeing.setDescription("Hair coloring is the practice of changing the color of hair. ");
            Service removals = new Service("Removals");
            removals.setDescription("A moving company, van line is a company that helps people and businesses move their goods from one place to another.");

        // create instances of ProviderService entity
        ProviderService provider1Filling = new ProviderService(provider1, fillingCavities, 1800000L /* 30 min */);
        provider1Filling.setPrice(100.0);
        provider1Filling.setPriceType(PriceType.PER_SERVICE);
        ProviderService provider1Removals = new ProviderService(provider1, removals, 60*60*1000L /* 1h */);
        provider1Removals.setPrice(60.0);
        provider1Removals.setPriceType(PriceType.PER_HOUR);
        ProviderService provider2Filling = new ProviderService(provider2, fillingCavities, 1800000L /* 30 min */);
        provider2Filling.setPrice(150.0);
        provider2Filling.setPriceType(PriceType.PER_SERVICE);
        provider2Filling.setDiscount((short) 10 /* [%] */);
        ProviderService provider3Haircut = new ProviderService(provider3, haircut, 1800000L /* 30 min */);
        provider3Haircut.setPrice(50.0);
        provider3Haircut.setPriceType(PriceType.PER_SERVICE);
        ProviderService provider3Dyeing = new ProviderService(provider3, hairDyeing, 1800000L /* 30 min */);
        provider3Dyeing.setPrice(80.0);
        provider3Dyeing.setPriceType(PriceType.PER_SERVICE);
        provider3Dyeing.setDiscount((short) 50 /* [%] */);
        ProviderService provider4Removals = new ProviderService(provider4, removals, 60*60*1000L /* 1h */);
        provider4Removals.setPrice(75.0);
        provider4Removals.setPriceType(PriceType.PER_HOUR);

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

        // Integration Test BEGIN

        List<Provider> providers = new ArrayList<>();
        providers.add(provider3);
        providers.add(provider4);
        List<String> keywords = new ArrayList<>();
        keywords.add("is");
        List<Service> foundServices = serviceFacade.findByMultipleCriteria(keywords, null, providers, null, null, null);

        assertTrue("There should be defined offers for two services by given providers.", foundServices.size() == 2);
        assertTrue(foundServices.contains(hairDyeing));
        assertTrue(foundServices.contains(removals));

        // Integration Test END

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
    public void shouldFindServiceByCategory() {

        // create some instances of ServiceCategory entity
        ServiceCategory beautyCategory = new ServiceCategory("Beauty and Cosmetics");
        ServiceCategory dentalCategory = new ServiceCategory("Dental Services");

        // create some instances of Service entity
        Service hairWashing = new Service("Hair washing");
            hairWashing.setDescription("Hair washing is the cosmetic act of keeping hair clean by washing it with shampoo or other detergent products and water. Hair conditioner may also be used to improve hair's texture and manageability.");
        Service haircut = new Service("Haircut");
            haircut.setDescription("A hairstyle, hairdo, or haircut refers to the styling of hair, usually on the human scalp.");
        Service dentalFillings = new Service("Dental fillings");
            dentalFillings.setDescription("A dental restoration or dental filling is a dental restorative material used to restore the function, integrity and morphology of missing tooth structure.");
        Service hairDyeing = new Service("Hair dyeing");
            hairDyeing.setDescription("Hair coloring is the practice of changing the color of hair.");

        // wiring Services to Service Categories (both sides)
        hairWashing.setServiceCategory(beautyCategory);
        haircut.setServiceCategory(beautyCategory);
        hairDyeing.setServiceCategory(beautyCategory);
        dentalFillings.setServiceCategory(dentalCategory);

        beautyCategory.getServices().add(hairWashing);
        beautyCategory.getServices().add(haircut);
        beautyCategory.getServices().add(hairDyeing);
        dentalCategory.getServices().add(dentalFillings);

        // persisting Services and Service Categories
        categoryFacade.create(beautyCategory);
        categoryFacade.create(dentalCategory);

        serviceFacade.create(hairWashing);
        serviceFacade.create(haircut);
        serviceFacade.create(hairDyeing);
        serviceFacade.create(dentalFillings);

        assertTrue("There should be four Service entities in database.", serviceFacade.count() == 4);
        assertTrue("There should be two Service Category entities in database.", categoryFacade.count() == 2);


        List<Service> dentalServices = serviceFacade.findByCategory(dentalCategory);
        assertTrue("There should be only one dental Service entity in database.", dentalServices.size() == 1);
        assertTrue(dentalServices.contains(dentalFillings));

        List<Service> hairServices = serviceFacade.findByCategoryAndKeyword(beautyCategory, "co");
        assertTrue("There should be two hair related Service entities with 'co' keyword.", hairServices.size() == 2);
        assertTrue(hairServices.contains(hairDyeing));
        assertTrue(hairServices.contains(hairWashing));
        assertFalse(hairServices.contains(haircut));

        // remove Services by Service Categories
        serviceFacade.deleteByCategory(beautyCategory);
        assertTrue("There should remain only one Service in database.", serviceFacade.count() == 1);
        assertFalse(serviceFacade.findAll().contains(hairWashing));
        assertFalse(serviceFacade.findAll().contains(haircut));
        assertFalse(serviceFacade.findAll().contains(hairDyeing));
        assertTrue(serviceFacade.findAll().contains(dentalFillings));

        serviceFacade.deleteByCategory(dentalCategory);
        assertTrue("There should not remain any Service in database.", serviceFacade.count() == 0);
        assertFalse(serviceFacade.findAll().contains(dentalFillings));

    }
}