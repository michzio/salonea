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
import pl.salonea.ejb.stateless.ProviderFacade;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.Provider;
import pl.salonea.entities.ProviderService;
import pl.salonea.entities.Service;
import pl.salonea.enums.ProviderType;

import javax.inject.Inject;
import java.io.File;
import java.util.logging.Logger;

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

    // @Inject
    // private ServiceFacadeInterface.Local serviceFacade;
    // TODO implement ProviderServiceFacade Integration Tests

    @Test
    public void shouldCreateNewProviderService() {

        // create instance of Provider entity
        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider = new Provider("firma2@allegro.pl", "allegro2", "aAle2@", "Allegro 2 Ltd.",
                "2234567890", "2234567890", address, "Allegro Polska", ProviderType.SIMPLE);

        // create instance of Service entity
        Service service = new Service("Haircut");

        // create instance of ProviderService entity
        ProviderService providerService = new ProviderService(provider, service, 1800000L /* 30 min */);


    }

}
