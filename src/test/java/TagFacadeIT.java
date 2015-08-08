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
import pl.salonea.ejb.interfaces.TagFacadeInterface;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.Provider;
import pl.salonea.entities.ServicePoint;
import pl.salonea.entities.ServicePointPhoto;
import pl.salonea.entities.Tag;
import pl.salonea.enums.ProviderType;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * TagFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Aug 7, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class TagFacadeIT {

    private static final Logger logger = Logger.getLogger(TagFacadeIT.class.getName());

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
    private TagFacadeInterface.Local tagFacade;

    @Inject
    private UserTransaction utx;

    @Test
    public void shouldCreateNewTag() {

        // create instance of Tag entity
        Tag tag = new Tag("tag name");

        // persist Tag entity in database
        tagFacade.create(tag);

        assertNotNull("Tag ID should not be null.", tag.getTagId());
        assertTrue("There should be one Tag persisted in database.", tagFacade.count() == 1);

        assertEquals("Both persisted and found Tag entity should be the same.", tagFacade.find(tag.getTagId()), tag);

        tagFacade.remove(tag);
        assertTrue("There should not be any Tag entity in database.", tagFacade.count() == 0);
    }

    @Test
    public void shouldFindTagByName() {

        // create some instances of Tag entity
        Tag removals = new Tag("removals");
        Tag hair = new Tag("hair");
        Tag dental = new Tag("dental");
        Tag medical = new Tag("medical");

        // persist Tag entities in database
        tagFacade.create(removals);
        tagFacade.create(hair);
        tagFacade.create(dental);
        tagFacade.create(medical);

        assertTrue("There should be four Tag entities in database.", tagFacade.count() == 4);

        List<Tag> foundTags = tagFacade.findByTagName("al");
        assertTrue("There should be found three Tag entities for given name.", foundTags.size() == 3);
        assertTrue(foundTags.contains(removals) && foundTags.contains(dental) && foundTags.contains(medical));

        List<String> tagNames = new ArrayList<>();
        tagNames.add("hair");
        tagNames.add("dental");
        List<Tag> multiTags = tagFacade.findByMultipleCriteria(tagNames, null, null);
        assertTrue("There should be found two Tag entities for given names.", multiTags.size() == 2);
        assertTrue(multiTags.contains(hair) && multiTags.contains(dental));

        // remove Tag entities
        tagFacade.remove(medical);
        tagFacade.remove(dental);
        tagFacade.remove(hair);
        tagFacade.remove(removals);
    }

    @Test
    public void shouldFindTagByPhotos() throws Exception {

        // create some instances of Tag entity
        Tag doctor = new Tag("doctor");
        Tag vaccination = new Tag("vaccination");
        Tag dental = new Tag("dental");
        Tag medical = new Tag("medical");

        // create instance of Provider entity
        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider = new Provider("firma@medical24.pl", "medical24", "aAle2@", "Medical 24 Sp. z o.o.",
                "2234567890", "2234567890", address, "Medical 24 Poland", ProviderType.SIMPLE);

        // create instance of ServicePoint entity
        ServicePoint servicePoint = new ServicePoint(provider, 1, address);

        // create some instances of ServicePointPhoto entity
        ServicePointPhoto photo1 = new ServicePointPhoto("photo1.png", servicePoint);
        ServicePointPhoto photo2 = new ServicePointPhoto("photo2.png", servicePoint);

        photo1.getTags().add(doctor); photo1.getTags().add(vaccination);
        photo2.getTags().add(dental); photo2.getTags().add(medical);

        doctor.getTaggedPhotos().add(photo1);
        vaccination.getTaggedPhotos().add(photo1);
        dental.getTaggedPhotos().add(photo2);
        medical.getTaggedPhotos().add(photo2);

        tagFacade.create(doctor);
        tagFacade.create(vaccination);
        tagFacade.create(dental);
        tagFacade.create(medical);

        utx.begin();
        providerFacade.create(provider);
        pointFacade.create(servicePoint);
        photoFacade.create(photo1);
        photoFacade.create(photo2);
        utx.commit();

        assertTrue("There should be four Tag entities in database.", tagFacade.count() == 4);
        assertTrue("There should be two Photo entities in database.", photoFacade.count() == 2);
        assertTrue("There should be one Service Point in database.", pointFacade.count() == 1);
        assertTrue("There should be one Provider entity in database.", providerFacade.count() == 1);

        List<Tag> tags = tagFacade.findByServicePointPhoto(photo1);
        assertTrue("There should be two Tags for given photo.", tags.size() == 2);
        assertTrue(tags.contains(doctor) && tags.contains(vaccination));

        List<Tag> namedTags = tagFacade.findByServicePointPhotoAndTagName(photo2, "med");
        assertTrue("There should be only one Tag defined for given photo and name.", namedTags.size() == 1);
        assertTrue(namedTags.contains(medical));

        List<ServicePointPhoto> photos = new ArrayList<>();
        photos.add(photo1);
        photos.add(photo2);
        List<String> tagNames = new ArrayList<>();
        tagNames.add(vaccination.getTagName());
        tagNames.add(medical.getTagName());
        List<Tag> multiTags = tagFacade.findByMultipleCriteria(tagNames,photos, null);
        assertTrue("There should be found two Tags for given photos and names.", multiTags.size() == 2);
        assertTrue(multiTags.contains(vaccination) && multiTags.contains(medical));

        // remove Provider and ServicePoint and Photos entities
        utx.begin();
        provider = providerFacade.find(provider.getUserId());
        providerFacade.remove(provider);
        utx.commit();

        assertTrue("There should not be any Photo entity in database.", photoFacade.count() == 0);
        assertTrue("There should not be any Service Point in database.", pointFacade.count() == 0);
        assertTrue("There should not be any Provider entity in database.", providerFacade.count() == 0);

        // remove Tag entities
        medical.setTaggedPhotos(null);
        dental.setTaggedPhotos(null);
        vaccination.setTaggedPhotos(null);
        doctor.setTaggedPhotos(null);
        tagFacade.remove(medical);
        tagFacade.remove(dental);
        tagFacade.remove(vaccination);
        tagFacade.remove(doctor);

        assertTrue("There should not be any Tag entity in database.", tagFacade.count() == 0);

    }

}
