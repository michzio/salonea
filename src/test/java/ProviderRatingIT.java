import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.Client;
import pl.salonea.entities.Provider;
import pl.salonea.entities.ProviderRating;
import pl.salonea.enums.ProviderType;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * ProviderRating Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jun 14, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class ProviderRatingIT {

    private static final Logger logger = Logger.getLogger(ProviderRatingIT.class.getName());

    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction transaction;

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

    @Before
    public void before() throws Exception {
        emf = Persistence.createEntityManagerFactory("LocalServicesMySQL");
        em = emf.createEntityManager();
        transaction = em.getTransaction();
        logger.log(Level.INFO, "Creating entity manager and entity transaction.");
    }

    @After
    public void after() throws Exception {
        if(em != null) em.close();
        if(emf != null) emf.close();
    }

    @Test
    public void shouldCreateNewProviderRating() {

        // create instance of Client entity
        Client client = new Client();

        // create instance of Provider entity
        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider = new Provider("firma2@allegro.pl", "allegro2", "aAle2@", "Allegro 2 Ltd.",
                "2234567890", "2234567890", address, "Allegro Polska", ProviderType.SIMPLE);

        // create instance of ProviderRating entity
        ProviderRating providerRating = new ProviderRating(provider, client, (short) 9);

        transaction.begin();
        em.persist(client);
        em.persist(provider);
        em.persist(providerRating);
        transaction.commit();

        assertEquals(providerRating.getProvider(), provider);
        assertEquals(providerRating.getClient(), client);

        transaction.begin();
        em.refresh(client);
        em.refresh(provider);
        transaction.commit();

        assertTrue(client.getProviderRatings().contains(providerRating));
        assertTrue(provider.getReceivedRatings().contains(providerRating));

        transaction.begin();
        em.remove(providerRating);
        em.remove(provider);
        em.remove(client);
        transaction.commit();
    }
}