import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.interfaces.ClientFacadeInterface;
import pl.salonea.ejb.interfaces.CorporationFacadeInterface;
import pl.salonea.ejb.interfaces.ProviderFacadeInterface;
import pl.salonea.ejb.stateless.ClientFacade;
import pl.salonea.ejb.stateless.ProviderRatingFacade;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.*;
import pl.salonea.enums.ProviderType;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * ProviderFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jul 21, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class ProviderFacadeIT {

    private static final Logger logger = Logger.getLogger(ProviderFacadeIT.class.getName());

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
    private ProviderFacadeInterface.Local providerFacade;

    @Inject
    private CorporationFacadeInterface.Local corporationFacade;

    @Inject
    private ClientFacadeInterface.Local clientFacade;

    @Inject
    private ProviderRatingFacade.Local ratingFacade;

    @Inject
    private UserTransaction utx;

    @Test
    public void shouldCreateProvider() {

        // create instance of Provider entity
        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider = new Provider("firma@allegro.pl", "allegro", "aAle2@_", "Allegro Ltd.",
                "2234567890", "2234567890", address, "Allegro Polska", ProviderType.SIMPLE);

        providerFacade.create(provider);

        assertNotNull("Provider ID should not be null.", provider.getUserId());
        assertTrue("There should be one provider.", providerFacade.count() == 1);

        Provider foundProvider = providerFacade.find(provider.getUserId());
        assertEquals(foundProvider, provider);

        providerFacade.remove(foundProvider);

        assertTrue("There should not be any provider in database.", providerFacade.count() == 0);
    }

    @Test
    public void shouldFindProviderByType() {

        // create some instances of Corporation
        Address corpo_address1 = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Corporation corporation1 = new Corporation("Allegro", "allegro_logo.png", corpo_address1);

        Address corpo_address2 =  new Address("Wrocławska", "45", "10-140", "Szczecin", "Zachodnio Pomorskie", "Poland");
        Corporation corporation2 = new Corporation("Tieto", "tieto_logo.png", corpo_address2);

        Address corpo_address3 = new Address("Pomorska", "12", "99-200", "Gdańsk", "Pomorze", "Poland");
        Corporation corporation3 = new Corporation("Przeprowadzki24", "przeprowadzki24_logo.gif", corpo_address3);

        // create some instances of Provider entity
        Address address1 =  new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider1 = new Provider("firma@allegro.pl", "allegro", "aAle2@_", "Allegro Ltd.",
                "2234567890", "2234567890", address1, "Allegro Polska", ProviderType.CORPORATE);
        provider1.setCorporation(corporation1);

        Address address2 = new Address("Wrocławska", "45", "10-140", "Szczecin", "Zachodnio Pomorskie", "Poland");
        Provider provider2 = new Provider("firma@tieto.pl", "tieto", "tIe%13?", "Tieto Sp. z o.o.",
                "6593878688", "6510029930", address2, "Tieto Poland", ProviderType.CORPORATE);
        provider2.setCorporation(corporation2);

        Address address3 = new Address("Kijowska", "09", "20-160", "Lublin", "Lubelskie", "Poland");
        Provider provider3 = new Provider("firma@fryzjerka.pl", "fryzjerka_pl", "fRyZU123?", "Fryzjerka Sp. z o.o.",
                "1910020030", "1930040050", address3, "Fryzjerka Polska", ProviderType.SIMPLE);

        Address address4 = new Address("Pomorska", "12", "99-200", "Gdańsk", "Pomorze", "Poland");
        Provider provider4 = new Provider("kontakt@przeprowadzki24.pl", "przeprowadzki24", "prZEP_M24%", "Przeprowadzki24 Sp. z o.o.",
                "4530040050", "4530040050", address4, "Przeprowadzki24 Pomorze", ProviderType.FRANCHISE);
        provider4.setCorporation(corporation3);

        corporationFacade.create(corporation1);
        providerFacade.create(provider1);
        corporationFacade.create(corporation2);
        providerFacade.create(provider2);
        providerFacade.create(provider3);
        corporationFacade.create(corporation3);
        providerFacade.create(provider4);

        assertTrue("There should be two Corporate providers.", providerFacade.findByType(ProviderType.CORPORATE).size() == 2);
        assertTrue("There should be one Simple provider.", providerFacade.findByType(ProviderType.SIMPLE).size() == 1);
        assertTrue("There should be one Franchise provider.", providerFacade.findByType(ProviderType.FRANCHISE).size() == 1);


        providerFacade.remove(provider4);
        providerFacade.remove(provider3);
        providerFacade.remove(provider2);
        providerFacade.remove(provider1);
        corporationFacade.remove(corporation3);
        corporationFacade.remove(corporation2);
        corporationFacade.remove(corporation1);

        assertTrue("There should not be any provider in database.", providerFacade.count() == 0);
    }

    @Test
    public void shouldFindProvidersByCorporation() throws Exception {

        // create some instances of Corporation
        Address corpo_address1 = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Corporation corporation1 = new Corporation("Allegro", "allegro_logo.png", corpo_address1);

        Address corpo_address2 =  new Address("Wrocławska", "45", "10-140", "Szczecin", "Zachodnio Pomorskie", "Poland");
        Corporation corporation2 = new Corporation("Tieto", "tieto_logo.png", corpo_address2);

        // create some instances of Provider entity
        Address address1 =  new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider1 = new Provider("firma@allegro.pl", "allegro", "aAle2@_", "Allegro Ltd.",
                "2234567890", "2234567890", address1, "Allegro Polska", ProviderType.CORPORATE);
        provider1.setCorporation(corporation1);

        Address address2 = new Address("Wrocławska", "45", "10-140", "Szczecin", "Zachodnio Pomorskie", "Poland");
        Provider provider2 = new Provider("firma@tieto.pl", "tieto", "tIe%13?", "Tieto Sp. z o.o.",
                "6593878688", "6510029930", address2, "Tieto Poland", ProviderType.CORPORATE);
        provider2.setCorporation(corporation2);

        Address address3 = new Address("Kijowska", "09", "20-160", "Lublin", "Lubelskie", "Poland");
        Provider provider3 = new Provider("firma@fryzjerka.pl", "fryzjerka_pl", "fRyZU123?", "Fryzjerka Sp. z o.o.",
                "1910020030", "1930040050", address3, "Fryzjerka Polska", ProviderType.CORPORATE);
        provider3.setCorporation(corporation1);

        Address address4 = new Address("Pomorska", "12", "99-200", "Gdańsk", "Pomorze", "Poland");
        Provider provider4 = new Provider("kontakt@przeprowadzki24.pl", "przeprowadzki24", "prZEP_M24%", "Przeprowadzki24 Sp. z o.o.",
                "4530040050", "4530040050", address4, "Przeprowadzki24 Pomorze", ProviderType.FRANCHISE);
        provider4.setCorporation(corporation2);

        corporationFacade.create(corporation1);
        corporationFacade.create(corporation2);
        providerFacade.create(provider1);
        providerFacade.create(provider2);
        providerFacade.create(provider3);
        providerFacade.create(provider4);

        List<Provider> providers1 = providerFacade.findByCorporation(corporation1);
        List<Provider> providers2 = providerFacade.findByCorporation(corporation2);

        assertTrue("There should be two providers belonging to first corporation.", providers1.size() == 2);
        assertTrue("There should be two providers belonging to second corporation.", providers2.size() == 2);

        assertTrue(providers1.contains(provider1) && providers1.contains(provider3));
        assertTrue(providers2.contains(provider2) && providers2.contains(provider4));

        providerFacade.remove(provider4);
        providerFacade.remove(provider3);
        providerFacade.remove(provider2);
        providerFacade.remove(provider1);
        corporationFacade.remove(corporation2);
        corporationFacade.remove(corporation1);
    }

    @Test
    public void shouldFindProviderByRating() throws Exception {

        // create some instances of Client entity
        Client client1 = new Client();
        Client client2 = new Client();
        Client client3 = new Client();
        Client client4 = new Client();

        // create some instances of Provider entity
        Address address1 = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
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

        // create instances of ProviderRating entiy
        ProviderRating rating11 = new ProviderRating(provider1, client1, (short) 5);
        ProviderRating rating12 = new ProviderRating(provider1, client2, (short) 3);
        ProviderRating rating13 = new ProviderRating(provider1, client3, (short) 2);
        ProviderRating rating14 = new ProviderRating(provider1, client4, (short) 6);
        ProviderRating rating21 = new ProviderRating(provider2, client1, (short) 1);
        ProviderRating rating22 = new ProviderRating(provider2, client2, (short) 4);
        ProviderRating rating23 = new ProviderRating(provider2, client3, (short) 2);
        ProviderRating rating24 = new ProviderRating(provider2, client4, (short) 4);
        ProviderRating rating31 = new ProviderRating(provider3, client1, (short) 1);
        ProviderRating rating32 = new ProviderRating(provider3, client2, (short) 6);
        ProviderRating rating33 = new ProviderRating(provider3, client3, (short) 2);
        ProviderRating rating34 = new ProviderRating(provider3, client4, (short) 1);

        utx.begin();
        clientFacade.create(client1);
        clientFacade.create(client2);
        clientFacade.create(client3);
        clientFacade.create(client4);
        providerFacade.create(provider1);
        providerFacade.create(provider2);
        providerFacade.create(provider3);
        providerFacade.create(provider4);
        ratingFacade.create(rating11);
        ratingFacade.create(rating12);
        ratingFacade.create(rating13);
        ratingFacade.create(rating14);
        ratingFacade.create(rating21);
        ratingFacade.create(rating22);
        ratingFacade.create(rating23);
        ratingFacade.create(rating24);
        ratingFacade.create(rating31);
        ratingFacade.create(rating32);
        ratingFacade.create(rating33);
        ratingFacade.create(rating34);
        utx.commit();

        assertTrue(ratingFacade.count() == 12);

        // MAIN TEST CASES

        List<Provider> ratedProviders = providerFacade.findRated();
        List<Provider> unratedProviders = providerFacade.findUnrated();

        assertTrue("There should be three rated providers.", ratedProviders.size() == 3);
        assertTrue("There should be only one unrated provider.", unratedProviders.size() == 1);
        assertTrue(ratedProviders.contains(provider1) && ratedProviders.contains(provider2) && ratedProviders.contains(provider3));
        assertTrue(unratedProviders.contains(provider4));

        List<Provider> ratedAboveProviders = providerFacade.findOnAvgRatedAbove(2.5);
        List<Provider> ratedBelowProviders = providerFacade.findOnAvgRatedBelow(2.5);

        // tests take into account providers that have been rated equal to given value
        assertTrue("There should be three providers rated above 2.5", ratedAboveProviders.size() == 3);
        assertTrue("There should be also one porvider rated below 2.5", ratedBelowProviders.size() == 1);

        List<Provider> ratedByClientProviders = providerFacade.findRatedByClient(client1);

        assertTrue("There are three providers rated by given client.", ratedByClientProviders.size() == 3);
        assertTrue(ratedByClientProviders.contains(provider1) && ratedByClientProviders.contains(provider2)
                   && ratedByClientProviders.contains(provider3));
        assertFalse(ratedByClientProviders.contains(provider4));


        // END MAIN TEST CASES

        assertTrue(ratingFacade.deleteByProvider(provider1) == 4);
        assertTrue(ratingFacade.deleteByProvider(provider2) == 4);
        assertTrue(ratingFacade.countProviderRatings(provider2) == 0);
        assertTrue(ratingFacade.deleteByProvider(provider3) == 4);
        assertTrue(ratingFacade.deleteByProvider(provider4) == 0);

        assertTrue(ratingFacade.count() == 0);

        clientFacade.remove(client4);
        clientFacade.remove(client3);
        clientFacade.remove(client2);
        clientFacade.remove(client1);

        assertTrue(clientFacade.count() == 0);

        providerFacade.remove(provider4);
        providerFacade.remove(provider3);
        providerFacade.remove(provider2);
        providerFacade.remove(provider1);

        assertTrue(providerFacade.count() == 0);


    }

    // TODO Other JPQL named queries tests
    /*@Test
    public void shouldFindProviderByPaymentMethod() {

        // create some instances of Corporation
        Address corpo_address1 = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Corporation corporation1 = new Corporation("Allegro", "allegro_logo.png", corpo_address1);

        Address corpo_address2 =  new Address("Wrocławska", "45", "10-140", "Szczecin", "Zachodnio Pomorskie", "Poland");
        Corporation corporation2 = new Corporation("Tieto", "tieto_logo.png", corpo_address2);

        Address corpo_address3 = new Address("Pomorska", "12", "99-200", "Gdańsk", "Pomorze", "Poland");
        Corporation corporation3 = new Corporation("Przeprowadzki24", "przeprowadzki24_logo.gif", corpo_address3);

        // create some instances of Provider entity
        Address address1 =  new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider1 = new Provider("firma@allegro.pl", "allegro", "aAle2@_", "Allegro Ltd.",
                "2234567890", "2234567890", address1, "Allegro Polska", ProviderType.CORPORATE);
        provider1.setCorporation(corporation1);
        provider1.getAcceptedPaymentMethods().add();
        provider1.getAcceptedPaymentMethods().add();

        Address address2 = new Address("Wrocławska", "45", "10-140", "Szczecin", "Zachodnio Pomorskie", "Poland");
        Provider provider2 = new Provider("firma@tieto.pl", "tieto", "tIe%13?", "Tieto Sp. z o.o.",
                "6593878688", "6510029930", address2, "Tieto Poland", ProviderType.CORPORATE);
        provider2.setCorporation(corporation2);
        provider2.getAcceptedPaymentMethods().add();
        provider2.getAcceptedPaymentMethods().add();

        Address address3 = new Address("Kijowska", "09", "20-160", "Lublin", "Lubelskie", "Poland");
        Provider provider3 = new Provider("firma@fryzjerka.pl", "fryzjerka_pl", "fRyZU123?", "Fryzjerka Sp. z o.o.",
                "1910020030", "1930040050", address3, "Fryzjerka Polska", ProviderType.SIMPLE);
        provider3.getAcceptedPaymentMethods().add();
        provider3.getAcceptedPaymentMethods().add();

        Address address4 = new Address("Pomorska", "12", "99-200", "Gdańsk", "Pomorze", "Poland");
        Provider provider4 = new Provider("kontakt@przeprowadzki24.pl", "przeprowadzki24", "prZEP_M24%", "Przeprowadzki24 Sp. z o.o.",
                "4530040050", "4530040050", address4, "Przeprowadzki24 Pomorze", ProviderType.FRANCHISE);
        provider4.getAcceptedPaymentMethods().add();
        provider4.getAcceptedPaymentMethods().add();

        corporationFacade.create(corporation1);
        providerFacade.create(provider1);
        corporationFacade.create(corporation2);
        providerFacade.create(provider2);
        providerFacade.create(provider3);
        corporationFacade.create(corporation3);
        providerFacade.create(provider4);



        providerFacade.remove(provider4);
        providerFacade.remove(provider3);
        providerFacade.remove(provider2);
        providerFacade.remove(provider1);
        corporationFacade.remove(corporation2);
        corporationFacade.remove(corporation1);

        assertTrue("There should not be any provider in database.", providerFacade.count() == 0);
        assertTrue("There should not be any corporation in database.", corporationFacade.count() == 0);
    }
    */

}
