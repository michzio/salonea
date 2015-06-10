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
import pl.salonea.entities.Provider;
import pl.salonea.entities.ServicePoint;
import pl.salonea.entities.ServicePointPhoto;
import pl.salonea.entities.Tag;
import pl.salonea.entities.idclass.ServicePointId;
import pl.salonea.enums.ProviderType;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * ServicePointPhoto Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jun 4, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class ServicePointPhotoIT {

    private static final Logger logger = Logger.getLogger(ServicePointPhotoIT.class.getName());

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
    public void shouldCreateNewServicePointWithPhoto() {

        // Create instance of provider entity
        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider = new Provider("firma2@allegro.pl", "allegro2", "aAle2@", "Allegro 2 Ltd.",
                "2234567890", "2234567890", address, "Allegro Polska", ProviderType.SIMPLE);

        transaction.begin();

        em.persist(provider);

        // Create instance of service point entity
        ServicePoint servicePoint = new ServicePoint(provider, 1, address);
        em.persist(servicePoint);

        // Create instance of service point photo entity
        ServicePointPhoto servicePointPhoto = new ServicePointPhoto("service_point_1.png", servicePoint);
        em.persist(servicePointPhoto);

        transaction.commit();

        assertNotNull("Service point photo id can not be null", servicePointPhoto.getPhotoId());

        transaction.begin();
        ServicePointPhoto photo = em.find(ServicePointPhoto.class, servicePointPhoto.getPhotoId());
        em.refresh(servicePoint);
        Set<ServicePointPhoto> servicePointPhotos = servicePoint.getPhotos();
        transaction.commit();

        assertTrue("Service point photo collection must include added photo.", servicePointPhotos.contains(photo));

        transaction.begin();
        em.remove(photo);
        em.remove(servicePoint);
        em.remove(provider);
        transaction.commit();

    }

    @Test
    public void shouldDefineTagsForProvider() {

        // Create instance of provider entity
        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider = new Provider("firma2@allegro.pl", "allegro2", "aAle2@", "Allegro 2 Ltd.",
                "2234567890", "2234567890", address, "Allegro Polska", ProviderType.SIMPLE);

        transaction.begin();

        em.persist(provider);

        // Create instance of service point entity
        ServicePoint servicePoint = new ServicePoint(provider, 1, address);
        em.persist(servicePoint);

        // Create instance of service point photo entity
        ServicePointPhoto servicePointPhoto = new ServicePointPhoto("service_point_1.png", servicePoint);
        em.persist(servicePointPhoto);

        transaction.commit();

        assertNotNull("Service point photo id can not be null", servicePointPhoto.getPhotoId());

        // Create instance of tag
        Tag tag = new Tag("some tag");

        transaction.begin();
        em.persist(tag);
        transaction.commit();

        Set<Tag> tags = servicePointPhoto.getTags();
        if( tags == null) {
            tags = new HashSet<>();
        }
        tags.add(tag);
        servicePointPhoto.setTags(tags);

        transaction.begin();
        em.persist(servicePointPhoto);
        em.refresh(tag);
        transaction.commit();

        assertTrue("Photo should be contained in collection for given tag.",
                tag.getTaggedPhotos().contains(servicePointPhoto));

        transaction.begin();
        em.remove(tag);
        em.remove(servicePointPhoto);
        em.remove(servicePoint);
        em.remove(provider);
        transaction.commit();

    }
}
