import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.interfaces.ProviderFacadeInterface;
import pl.salonea.ejb.interfaces.ServicePointFacadeInterface;
import pl.salonea.ejb.interfaces.ServicePointPhotoFacadeInterface;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.Provider;
import pl.salonea.entities.ServicePoint;
import pl.salonea.entities.ServicePointPhoto;
import pl.salonea.enums.ProviderType;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import java.io.File;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;

/**
 * ServicePointPhotoFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Aug 6, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class ServicePointPhotoFacadeIT {

    private static final Logger logger = Logger.getLogger(ServicePointPhotoFacadeIT.class.getName());

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
    private ServicePointPhotoFacadeInterface.Local photoFacade;

    @Inject
    private ProviderFacadeInterface.Local providerFacade;

    @Inject
    private ServicePointFacadeInterface.Local pointFacade;

    @Inject
    private UserTransaction utx;

    @Test
    public void shouldCreateNewPhoto() throws Exception {

        // create instance of Provider entity
        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider = new Provider("firma@medical24.pl", "medical24", "aAle2@", "Medical 24 Sp. z o.o.",
                "2234567890", "2234567890", address, "Medical 24 Poland", ProviderType.SIMPLE);

        // create instance of ServicePoint entity
        ServicePoint servicePoint = new ServicePoint(provider, 1, address);

        // create instance of ServicePointPhoto entity
        ServicePointPhoto photo = new ServicePointPhoto("medical_point", servicePoint);

        utx.begin();
        providerFacade.create(provider);
        pointFacade.create(servicePoint);
        photoFacade.create(photo);
        utx.commit();

        assertNotNull("Service Point Photo ID should not be null.", photo.getPhotoId());

        photoFacade.remove(photo);
        providerFacade.remove(provider);

    }



}
