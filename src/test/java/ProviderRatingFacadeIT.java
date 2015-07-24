import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.interfaces.ClientFacadeInterface;
import pl.salonea.ejb.interfaces.ProviderFacadeInterface;
import pl.salonea.ejb.interfaces.ProviderRatingFacadeInterface;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.Client;
import pl.salonea.entities.Provider;
import pl.salonea.entities.ProviderRating;
import pl.salonea.entities.idclass.ProviderRatingId;
import pl.salonea.enums.ProviderType;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * ProviderRatingFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jul 20, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class ProviderRatingFacadeIT {

    private static final Logger logger = Logger.getLogger(ProviderRatingFacadeIT.class.getName());

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
    private ProviderRatingFacadeInterface.Local ratingFacade;

    @Inject
    private ClientFacadeInterface.Local clientFacade;

    @Inject
    private ProviderFacadeInterface.Local providerFacade;

    @Inject
    private UserTransaction utx;


    @Test
    public void shouldCreateProviderRating() throws Exception {

        // create instance of Client entity
        Client client = new Client();

        // create instance of Provider entity
        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider = new Provider("firma@allegro.pl", "allegro", "aAle2@_", "Allegro Ltd.",
                       "2234567890", "2234567890", address, "Allegro Polska", ProviderType.SIMPLE);

        // create instance of ProviderRating entity
        ProviderRating rating = new ProviderRating(provider, client, (short) 5);

        utx.begin();
        clientFacade.create(client);
        providerFacade.create(provider);
        ratingFacade.create(rating);
        utx.commit();

        assertNotNull("Client ID should not be null.", client.getClientId());
        assertNotNull("Provider ID should not be null.", provider.getUserId());

        utx.begin();
        ProviderRating foundRating = ratingFacade.find(new ProviderRatingId(provider.getUserId(), client.getClientId()));

        assertEquals("Persisted and found provider rating should be the same.", rating, foundRating);

        ratingFacade.remove(foundRating);
        utx.commit();

        providerFacade.remove(provider);
        clientFacade.remove(client);

        assertTrue(ratingFacade.count() == 0);
    }

    @Test
    public void shouldFindRatingsByConditions() throws Exception {

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
        ProviderRating rating41 = new ProviderRating(provider4, client1, (short) 3);
        ProviderRating rating42 = new ProviderRating(provider4, client2, (short) 2);
        ProviderRating rating43 = new ProviderRating(provider4, client3, (short) 5);
        ProviderRating rating44 = new ProviderRating(provider4, client4, (short) 1);

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
        ratingFacade.create(rating41);
        ratingFacade.create(rating42);
        ratingFacade.create(rating43);
        ratingFacade.create(rating44);
        utx.commit();

        assertTrue(ratingFacade.count() == 16);

        List<ProviderRating> providerRatings = ratingFacade.findByProvider(provider1);
        assertTrue("There should be four ratings for given provider.", providerRatings.size() == 4);
        assertTrue(providerRatings.contains(rating11));
        assertTrue(providerRatings.contains(rating12));
        assertTrue(providerRatings.contains(rating13));
        assertTrue(providerRatings.contains(rating14));

        List<ProviderRating> clientRatings = ratingFacade.findByClient(client3);
        assertTrue("There should be four ratings added by given client.", clientRatings.size() == 4);
        assertTrue(clientRatings.contains(rating13));
        assertTrue(clientRatings.contains(rating23));
        assertTrue(clientRatings.contains(rating33));
        assertTrue(clientRatings.contains(rating43));

        assertTrue(ratingFacade.findProviderAvgRating(provider1) == 4);
        assertTrue(ratingFacade.findProviderAvgRating(provider2) == 2.75);
        assertTrue(ratingFacade.findProviderAvgRating(provider3) == 2.5);
        assertTrue(ratingFacade.findProviderAvgRating(provider4) == 2.75);

        assertTrue(ratingFacade.countProviderRatings(provider1) == 4);
        assertTrue(ratingFacade.countClientRatings(client3) == 4);

        assertTrue(ratingFacade.findFromClientByRating(client4, (short) 1).size() == 2);
        assertTrue(ratingFacade.findForProviderByRating(provider2, (short) 4).size() == 2);
        assertTrue(ratingFacade.findForProviderByRating(provider3, (short) 6).contains(rating32));

        assertTrue(ratingFacade.findForProviderBelowRating(provider1, (short) 5).size() == 3);
        assertTrue(ratingFacade.findForProviderAboveRating(provider2, (short) 2).size() == 3);
        assertTrue(ratingFacade.findFromClientAboveRating(client3, (short) 3).size() == 1);
        assertTrue(ratingFacade.findFromClientBelowRating(client4, (short) 3).size() == 2);

        assertTrue(ratingFacade.deleteByClient(client1) == 4);
        assertTrue(ratingFacade.deleteByClient(client2) == 4);
        assertTrue(ratingFacade.countClientRatings(client2) == 0);
        assertTrue(ratingFacade.deleteByClient(client3) == 4);
        assertTrue(ratingFacade.deleteByClient(client4) == 4);

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
}
