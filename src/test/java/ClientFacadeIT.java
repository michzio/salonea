
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.interfaces.ClientFacadeInterface;
import pl.salonea.entities.Client;

import javax.inject.Inject;
import java.io.File;
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
    ClientFacadeInterface.Local clientFacade;

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
}