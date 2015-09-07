import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.stateless.ClientFacade;
import pl.salonea.ejb.stateless.CreditCardFacade;
import pl.salonea.entities.Client;
import pl.salonea.entities.CreditCard;
import pl.salonea.entities.idclass.CreditCardId;
import pl.salonea.enums.CreditCardType;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;
import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * CreditCardFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jul 18, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class CreditCardFacadeIT {

    private static final Logger logger = Logger.getLogger(CreditCardFacadeIT.class.getName());

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
    private CreditCardFacade cardFacade;

    @Inject
    private ClientFacade clientFacade;

    @Inject
    private UserTransaction utx;

    @Test
    public void shouldCreateClientWithCreditCard() throws Exception {

        // create instance of Client
        Client client = new Client("Credit card owner.");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);

        // create instance of CreditCard
        CreditCard creditCard = new CreditCard(client, "4539345259115377", cal.getTime(), "Michał Ziobro", CreditCardType.VISA );
        client.getCreditCards().add(creditCard);

        utx.begin();
        clientFacade.create(client);
        cardFacade.create(creditCard);
        utx.commit();

        assertTrue("There should be one CreditCard persisted in database.", cardFacade.count() == 1);
        assertTrue("There should be one Client persisted in database.", clientFacade.count() == 1);
        assertNotNull("Should assign new Credit Card to existing Client.", client.getCreditCards());
        assertTrue(client.getCreditCards().contains(creditCard));

        utx.begin();
        creditCard = cardFacade.find(new CreditCardId(client.getClientId(), creditCard.getCreditCardNumber(), creditCard.getExpirationDate()));
        cardFacade.remove(creditCard);
        utx.commit();

        assertTrue(cardFacade.count() == 0);

        client.getCreditCards().remove(creditCard);
        utx.begin();
        client = clientFacade.find(client.getClientId());
        clientFacade.remove(client);
        utx.commit();
    }

    @Test
    public void shouldManageClientCreditCards() throws Exception {

        // create some instances of Client entity
        Client client1 = new Client("Client 1");
        Client client2 = new Client("Client 2");
        Client client3 = new Client("Client 3");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);

        // create Credit Card instances and assign them to Clients
        CreditCard card1 = new CreditCard(client1, "4916254147698730", cal.getTime(), "Michał Ziobro", CreditCardType.VISA);
        cal.add(Calendar.YEAR, 1);
        CreditCard card2 = new CreditCard(client1, "4485249741691678", cal.getTime(), "Michał Ziobro", CreditCardType.MASTERCARD);
        cal.add(Calendar.YEAR, -1);
        CreditCard card3 = new CreditCard(client2, "5478623012821451", cal.getTime(), "Jan Nowak", CreditCardType.MASTERCARD);
        cal.add(Calendar.YEAR, 2);
        CreditCard card4 = new CreditCard(client2, "4532661830539670", cal.getTime(), "Jan Nowak", CreditCardType.AMERICAN_EXPRESS);
        CreditCard card5 = new CreditCard(client3, "5266864683957747", cal.getTime(), "Anna Kwiatkowska", CreditCardType.VISA);
        cal.add(Calendar.MONTH, -10);
        CreditCard card6 = new CreditCard(client3, "4445800819641026", cal.getTime(), "Anna Kwiatkowska", CreditCardType.VISA);

        // setting other side of bidirectional relationship
        client1.getCreditCards().add(card1);
        client1.getCreditCards().add(card2);
        client2.getCreditCards().add(card3);
        client2.getCreditCards().add(card4);
        client3.getCreditCards().add(card5);
        client3.getCreditCards().add(card6);

        utx.begin();
        clientFacade.create(client1);
        clientFacade.create(client2);
        clientFacade.create(client3);
        cardFacade.create(card1);
        cardFacade.create(card2);
        cardFacade.create(card3);
        cardFacade.create(card4);
        cardFacade.create(card5);
        cardFacade.create(card6);
        utx.commit();

        cal.add(Calendar.YEAR, -1);
        List<CreditCard> cards = cardFacade.findExpirationDateAfter(cal.getTime());
        assertTrue("There should be four cards expiring after " + cal.getTime(), cards.size() == 4);

        cards = cardFacade.findExpirationDateBefore(cal.getTime());
        assertTrue("There should be two cards expiring before " + cal.getTime(), cards.size() == 2);

        cards = cardFacade.findNotExpired();
        assertTrue("All cards should be not expired.", cards.size() == 6);

        cards = cardFacade.findByType(CreditCardType.VISA);
        assertTrue("There should be three VISA cards.", cards.size() == 3);

        cards = cardFacade.findByType(CreditCardType.MASTERCARD);
        assertTrue("There should be two MASTERCARD cards.", cards.size() == 2);

        cardFacade.deleteWithExpirationDateAfter(cal.getTime());
        assertTrue("There should left only two cards.", cardFacade.count() == 2);

        // refreshing client entities to enforce CascadeType.REMOVE to work correctly
        utx.begin();
        client1 = clientFacade.find(client1.getClientId());
        client2 = clientFacade.find(client2.getClientId());
        client3 = clientFacade.find(client3.getClientId());
        clientFacade.remove(client1);
        clientFacade.remove(client2);
        clientFacade.remove(client3);
        utx.commit();

        assertTrue("There should not remain any Credit Card persisted in database.", cardFacade.count() == 0);

    }

}
