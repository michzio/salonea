import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.interfaces.ClientFacadeInterface;
import pl.salonea.ejb.interfaces.ProviderRatingFacadeInterface;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.Client;
import pl.salonea.entities.Provider;
import pl.salonea.entities.ProviderRating;
import pl.salonea.enums.ProviderType;

import javax.inject.Inject;
import java.io.File;
import java.util.logging.Logger;

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


    @Test
    private void shouldCreateProviderRating() {

        // create instance of Client entity
        Client client = new Client();

        // create instance of Provider entity
        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider = new Provider("firma@allegro.pl", "allegro", "aAle2@_", "Allegro Ltd.",
                       "2234567890", "2234567890", address, "Allegro Polska", ProviderType.SIMPLE);

        // create instance of ProviderRating entity
        ProviderRating rating = new ProviderRating(provider, client, (short) 5);

        clientFacade.create(client);
        // TODO: not implemented ProviderFacade yet
    }
}
