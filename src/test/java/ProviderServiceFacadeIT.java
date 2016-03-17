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
import pl.salonea.entities.idclass.ProviderServiceId;
import pl.salonea.enums.PriceType;
import pl.salonea.enums.ProviderType;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
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
    private ServiceCategoryFacadeInterface.Local categoryFacade;

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
            ProviderService provider3Haircut = new ProviderService(provider3, haircut, 1800000L /* 30 min */);
            provider3Haircut.setPrice(50.0);
            provider3Haircut.setPriceType(PriceType.PER_SERVICE);
            provider3Haircut.setDescription("A hairstyle, hairdo, or haircut refers to the styling of hair, usually on the human scalp.");
        ProviderService provider3Dyeing = new ProviderService(provider3, hairDyeing, 1800000L /* 30 min */);
        provider3Dyeing.setPrice(80.0);
        provider3Dyeing.setPriceType(PriceType.PER_SERVICE);
        provider3Dyeing.setDiscount((short) 50 /* [%] */);
        provider3Dyeing.setDescription("Hair coloring is the practice of changing the color of hair. ");
            ProviderService provider4Removals = new ProviderService(provider4, removals, 1800000L /* 30 min */);
            provider4Removals.setPrice(75.0);
            provider4Removals.setPriceType(PriceType.PER_SERVICE);
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
        ProviderService provider3Haircut = new ProviderService(provider3, haircut, 1800000L /* 30 min */);
        provider3Haircut.setPrice(50.0);
        provider3Haircut.setPriceType(PriceType.PER_SERVICE);
        provider3Haircut.setDescription("A hairstyle, hairdo, or haircut refers to the styling of hair, usually on the human scalp.");
        ProviderService provider3Dyeing = new ProviderService(provider3, hairDyeing, 1800000L /* 30 min */);
        provider3Dyeing.setPrice(80.0);
        provider3Dyeing.setPriceType(PriceType.PER_SERVICE);
        provider3Dyeing.setDiscount((short) 50 /* [%] */);
        provider3Dyeing.setDescription("Hair coloring is the practice of changing the color of hair.");
        ProviderService provider4Removals = new ProviderService(provider4, removals, 60*60*1000L /* 1h */);
        provider4Removals.setPrice(75.0);
        provider4Removals.setPriceType(PriceType.PER_HOUR);
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

        List<ProviderService> matchingOffers = providerServiceFacade.findByMultipleCriteria(providers, services, null, null, null, null, false, null, null, null, null, null, null, null, null, null);
        assertTrue("There should be three offers matching given combination of providers and services",
                matchingOffers.size() == 3);
        assertTrue(matchingOffers.contains(provider1Filling));
        assertTrue(matchingOffers.contains(provider1Removals));
        assertTrue(matchingOffers.contains(provider3Haircut));
        assertFalse(matchingOffers.contains(provider3Dyeing));

        List<Service> hairServices = new ArrayList<>();
        hairServices.add(haircut);
        hairServices.add(hairDyeing);

        List<String> descriptions = new ArrayList<>();
        descriptions.add("hair");

        List<ProviderService> cheapHairOffers = providerServiceFacade.findByMultipleCriteria(null, hairServices, null, descriptions, 40.0, 70.0, false, null, null, null, null, null, null, null, null, null);
        assertTrue(cheapHairOffers.size() == 1);
        assertTrue(cheapHairOffers.contains(provider3Haircut));
        assertFalse(cheapHairOffers.contains(provider3Dyeing));

        List<ProviderService> cheapDiscountedHairOffers = providerServiceFacade.findByMultipleCriteria(null, hairServices, null, descriptions, 40.0, 70.0, true, null, null, null, null, null, null, null, null, null);
        assertTrue(cheapDiscountedHairOffers.size() == 2);
        assertTrue(cheapDiscountedHairOffers.contains(provider3Haircut));
        assertTrue(cheapDiscountedHairOffers.contains(provider3Dyeing));

        providers.add(provider2);
        List<ProviderService> discountedOffers = providerServiceFacade.findByMultipleCriteria(providers, null, null, null, null, null, false, (short) 10, (short) 60, null, null, null, null, null, null, null);
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

    @Test
    public void findByServiceCategory() throws Exception {

        // create some instances of Service entity
        Service haircut = new Service("Haircut");
        Service hairDyeing = new Service("Hair Dyeing");
        Service hairWashing = new Service("Hair Washing");
        Service dentalFilling = new Service("Dental Filling");
        Service medicalSurgery = new Service("Medical Surgery");
        Service neurologicalConsultation = new Service("Neurological Consultation");
        Service removals = new Service("Removals");

        // create some instances of Provider entity
        Address address1 =  new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider1 = new Provider("firma@fryzjer.pl", "fryzjer_pl", "aAle2@_", "FryzjerPl Sp. z o.o..",
                "2234567890", "2234567890", address1, "FryzjerPl", ProviderType.SIMPLE);

        Address address2 = new Address("Wrocławska", "45", "10-140", "Szczecin", "Zachodnio Pomorskie", "Poland");
        Provider provider2 = new Provider("firma@medical.pl", "medical", "tIe%13?", "Medical Sp. z o.o.",
                "6593878688", "6510029930", address2, "Medical Poland", ProviderType.SIMPLE);

        Address address3 = new Address("Kijowska", "09", "20-160", "Lublin", "Lubelskie", "Poland");
        Provider provider3 = new Provider("firma@dentystka.pl", "dentyska_pl", "fRyZU123?", "DentystkaPl Sp. z o.o.",
                "1910020030", "1930040050", address3, "DentystkaPl Lublin", ProviderType.SIMPLE);

        Address address4 = new Address("Pomorska", "12", "99-200", "Gdańsk", "Pomorze", "Poland");
        Provider provider4 = new Provider("kontakt@przeprowadzki24.pl", "przeprowadzki24", "prZEP_M24%", "Przeprowadzki24 Sp. z o.o.",
                "4530040050", "4530040050", address4, "Przeprowadzki24 Pomorze", ProviderType.SIMPLE);

        // create some instances of ServiceCategory entity
        ServiceCategory hairCareCategory = new ServiceCategory("Hair Care");
        ServiceCategory medicalCategory = new ServiceCategory("Medical Services");
        ServiceCategory dentalCategory = new ServiceCategory("Dental Services");
        ServiceCategory transportCategory = new ServiceCategory("Transport Services");

        // wiring Services to Service Categories (both sides)
        haircut.setServiceCategory(hairCareCategory);
        hairDyeing.setServiceCategory(hairCareCategory);
        hairWashing.setServiceCategory(hairCareCategory);
        dentalFilling.setServiceCategory(dentalCategory);
        medicalSurgery.setServiceCategory(medicalCategory);
        neurologicalConsultation.setServiceCategory(medicalCategory);
        removals.setServiceCategory(transportCategory);

        hairCareCategory.getServices().add(haircut);
        hairCareCategory.getServices().add(hairDyeing);
        hairCareCategory.getServices().add(hairWashing);
        dentalCategory.getServices().add(dentalFilling);
        medicalCategory.getServices().add(medicalSurgery);
        medicalCategory.getServices().add(neurologicalConsultation);
        transportCategory.getServices().add(removals);

        // create instances of ProviderService entity
        ProviderService provider1Haircut = new ProviderService(provider1, haircut, 1800000L /* 30 min */);
        provider1Haircut.setPrice(50.0);
        provider1Haircut.setPriceType(PriceType.PER_SERVICE);
        provider1Haircut.setDescription("A hairstyle, hairdo, or haircut refers to the styling of hair, usually on the human scalp.");

        ProviderService provider1HairDyeing  = new ProviderService(provider1, hairDyeing , 1800000L /* 30 min */);
        provider1HairDyeing.setPrice(80.0);
        provider1HairDyeing.setPriceType(PriceType.PER_SERVICE);
        provider1HairDyeing.setDiscount((short) 50 /* [%] */);
        provider1HairDyeing.setDescription("Hair coloring is the practice of changing the color of hair.");

        ProviderService provider1HairWashing = new ProviderService(provider1, hairWashing, 900000L /* 15 min */);
        provider1HairWashing.setPrice(30.0);
        provider1HairWashing.setPriceType(PriceType.PER_SERVICE);
        provider1HairWashing.setDescription("Hair washing is the cosmetic act of keeping hair clean by washing it with shampoo or other detergent products and water.");

        ProviderService provider2MedicalSurgery = new ProviderService(provider2, medicalSurgery, 7200000L /* 2h */);
        provider2MedicalSurgery.setPrice(5000.0);
        provider2MedicalSurgery.setPriceType(PriceType.PER_SERVICE);
        provider2MedicalSurgery.setDescription("Surgery is a technology consisting of a physical intervention on tissues, and muscle.");

        ProviderService provider2Neurological = new ProviderService(provider2, neurologicalConsultation, 1800000L /* 30 min */);
        provider2Neurological.setPrice(150.0);
        provider2Neurological.setPriceType(PriceType.PER_SERVICE);
        provider2Neurological.setDiscount( (short) 15 /* [%] */);
        provider2Neurological.setDescription("A neurologist is a physician specializing in neurology and trained to investigate, or diagnose and treat neurological disorders.");

        ProviderService provider3DentalFilling = new ProviderService(provider3, dentalFilling, 1800000L /* 30 min */);
        provider3DentalFilling.setPrice(150.0);
        provider3DentalFilling.setPriceType(PriceType.PER_SERVICE);
        provider3DentalFilling.setDiscount((short) 10 /* [%] */);
        provider3DentalFilling.setDescription("A dental restoration or dental filling is a dental restorative material used to restore the function, integrity and morphology of missing tooth structure.");

        ProviderService provider4Removals = new ProviderService(provider4, removals, 60*60*1000L /* 1h */);
        provider4Removals.setPrice(75.0);
        provider4Removals.setPriceType(PriceType.PER_HOUR);
        provider4Removals.setDescription("A moving company, removalist is a company that helps people and businesses move their goods from one place to another.");

        utx.begin();

        // persist Providers
        providerFacade.create(provider1);
        providerFacade.create(provider2);
        providerFacade.create(provider3);
        providerFacade.create(provider4);

        // persist Service Categories and Services
        categoryFacade.create(hairCareCategory);
        categoryFacade.create(medicalCategory);
        categoryFacade.create(dentalCategory);
        categoryFacade.create(transportCategory);

        serviceFacade.create(haircut);
        serviceFacade.create(hairDyeing);
        serviceFacade.create(hairWashing);
        serviceFacade.create(dentalFilling);
        serviceFacade.create(medicalSurgery);
        serviceFacade.create(neurologicalConsultation);
        serviceFacade.create(removals);

        // persist Provider Services
        providerServiceFacade.create(provider1Haircut);
        providerServiceFacade.create(provider1HairDyeing);
        providerServiceFacade.create(provider1HairWashing);
        providerServiceFacade.create(provider2MedicalSurgery);
        providerServiceFacade.create(provider2Neurological);
        providerServiceFacade.create(provider3DentalFilling);
        providerServiceFacade.create(provider4Removals);

        utx.commit();

        // MAIN TEST BEGIN

        assertTrue(providerServiceFacade.updateDiscountForProviderAndServiceCategory(provider1, hairCareCategory, (short) 15) == 3);
        provider1Haircut = providerServiceFacade.find( new ProviderServiceId(provider1.getUserId(), haircut.getServiceId()) );
        provider1HairDyeing = providerServiceFacade.find( new ProviderServiceId(provider1.getUserId(), hairDyeing.getServiceId()) );
        provider1HairWashing = providerServiceFacade.find( new ProviderServiceId(provider1.getUserId(), hairWashing.getServiceId()) );

        assertTrue( provider1Haircut.getDiscount() == (short) 15);
        assertTrue( provider1HairDyeing.getDiscount() == (short) 15 );
        assertTrue( provider1HairWashing.getDiscount() == (short) 15 );

        List<ProviderService> hairCareOffers = providerServiceFacade.findByServiceCategory(hairCareCategory);
        assertTrue("There should be three hair care offers.", hairCareOffers.size() == 3);
        assertTrue(hairCareOffers.contains(provider1Haircut));
        assertTrue(hairCareOffers.contains(provider1HairDyeing));
        assertTrue(hairCareOffers.contains(provider1HairWashing));

        List<ProviderService> dentalOffers = providerServiceFacade.findByServiceCategory(dentalCategory);
        assertTrue("There should be one dental offer.", dentalOffers.size() == 1);
        assertTrue(dentalOffers.contains(provider3DentalFilling));

        List<ProviderService> medicalOffers = providerServiceFacade.findByProviderAndServiceCategory(provider2, medicalCategory);
        assertTrue("There should be two medical offers for given provider.", medicalOffers.size() == 2);
        assertTrue(medicalOffers.contains(provider2MedicalSurgery));
        assertTrue(medicalOffers.contains(provider2Neurological));

        medicalOffers = providerServiceFacade.findByProviderAndServiceCategory(provider3, medicalCategory);
        assertTrue(medicalOffers.size() == 0);

        // MAIN TEST END

        // remove Provider Services
        assertTrue(providerServiceFacade.deleteForProviderAndServiceCategory(provider1, hairCareCategory) == 3);
        assertTrue(providerServiceFacade.deleteForProviderAndServiceCategory(provider2, medicalCategory) == 2);
        assertTrue(providerServiceFacade.deleteForProviderAndServiceCategory(provider3, dentalCategory) == 1);
        assertTrue(providerServiceFacade.deleteForProviderAndServiceCategory(provider4, transportCategory) == 1);

        assertTrue(providerServiceFacade.count() == 0);

        // remove Services
        serviceFacade.remove(removals);
        serviceFacade.remove(neurologicalConsultation);
        serviceFacade.remove(medicalSurgery);
        serviceFacade.remove(dentalFilling);
        serviceFacade.remove(hairWashing);
        serviceFacade.remove(hairDyeing);
        serviceFacade.remove(haircut);

        assertTrue(serviceFacade.count() == 0);

        transportCategory.setServices(new HashSet<>());
        dentalCategory.setServices(new HashSet<>());
        medicalCategory.setServices(new HashSet<>());
        hairCareCategory.setServices(new HashSet<>());
        categoryFacade.remove(transportCategory);
        categoryFacade.remove(dentalCategory);
        categoryFacade.remove(medicalCategory);
        categoryFacade.remove(hairCareCategory);

        assertTrue(categoryFacade.count() == 0);

        providerFacade.remove(provider4);
        providerFacade.remove(provider3);
        providerFacade.remove(provider2);
        providerFacade.remove(provider1);

        assertTrue(providerFacade.count() == 0);

    }

}