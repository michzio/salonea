
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.interfaces.ClientFacadeInterface;
import pl.salonea.ejb.interfaces.NaturalPersonFacadeInterface;
import pl.salonea.entities.Client;
import pl.salonea.entities.NaturalPerson;
import pl.salonea.enums.Gender;

import javax.inject.Inject;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * ClientFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jul 14, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class ClientFacadeIT {

    private static final Logger logger = Logger.getLogger(ClientFacadeIT.class.getName());

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
    private ClientFacadeInterface.Local clientFacade;

    @Inject
    private NaturalPersonFacadeInterface.Local naturalPersonFacade;

    @Test
    public void shouldCreateAClient() {

        // create instance of Client
        Client client = new Client();

        // persists the Client in the database
        client = clientFacade.create(client);

        assertNotNull("Client ID should not be null.", client.getClientId());

        assertTrue("Number of clients should be one.", clientFacade.count() == 1);

        Client foundClient = clientFacade.find(client.getClientId());

        assertEquals(client.getClientId(), foundClient.getClientId());

        clientFacade.remove(foundClient);

        assertTrue("There should not be any client in database.", clientFacade.count() == 0);

    }

    @Test
    public void shouldFindClientsByGender() {

        Date dateOfBirth;

        // create some instances of Client
        dateOfBirth = new GregorianCalendar(1988, Calendar.OCTOBER, 3).getTime();
        NaturalPerson person1 = new NaturalPerson("michzio@hotmail.com", "michzio", "pP123#", "Michał", "Ziobro", dateOfBirth , Gender.male);
        Client client1 = new Client();
        person1.setClient(client1);

        dateOfBirth = new GregorianCalendar(1975, Calendar.FEBRUARY, 23).getTime();
        NaturalPerson person2 = new NaturalPerson("jan.nowak@gmail.com", "nowak_j", "pP123$", "Jan", "Nowak", dateOfBirth, Gender.male);
        Client client2 = new Client();
        person2.setClient(client2);

        dateOfBirth = new GregorianCalendar(1990, Calendar.SEPTEMBER, 10).getTime();
        NaturalPerson person3 = new NaturalPerson("wik.kwiatkowska@gmail.com", "wiki_kwiatkowska", "pP123#", "Wiktoria", "Kwiatkowska", dateOfBirth, Gender.female);
        Client client3 = new Client();
        person3.setClient(client3);

        naturalPersonFacade.create(person1);
        naturalPersonFacade.create(person2);
        naturalPersonFacade.create(person3);

        List<Client> males = clientFacade.findByGender(Gender.male);
        assertTrue("There should be 2 male clients.", males.size() == 2);
        assertTrue(males.contains(client1));
        assertTrue(males.contains(client2));

        List<Client> females = clientFacade.findByGender(Gender.female);
        assertTrue("There should be 1 female client.", females.size() == 1);
        assertTrue(females.contains(client3));

        // remove all instances of Clients
        naturalPersonFacade.remove(person3);
        naturalPersonFacade.remove(person2);
        naturalPersonFacade.remove(person1);
    }


}