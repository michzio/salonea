import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.interfaces.*;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.*;
import pl.salonea.entities.idclass.ServicePointId;
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
    private TagFacadeInterface.Local tagFacade;

    @Inject
    private CorporationFacadeInterface.Local corporationFacade;

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
        ServicePointPhoto photo = new ServicePointPhoto("medical_point.png", servicePoint);

        utx.begin();
        providerFacade.create(provider);
        pointFacade.create(servicePoint);
        photoFacade.create(photo);
        utx.commit();

        assertTrue("There should be one Service Point Photo persisted in database.", photoFacade.count() == 1);
        assertNotNull("Service Point Photo ID should not be null.", photo.getPhotoId());

        assertEquals("Service Point Photo persisted and found should be the same.", photoFacade.find(photo.getPhotoId()) , photo);

        photo.setServicePoint(null);
        photoFacade.remove(photo);
        providerFacade.remove(provider);

        assertTrue("There should not be any Service Point Photo in database.", photoFacade.count() == 0);

    }

    @Test
    public void shouldFindPhotoByKeywords() throws Exception {

        // create instance of Provider entity
        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider = new Provider("firma@medical24.pl", "medical24", "aAle2@", "Medical 24 Sp. z o.o.",
                "2234567890", "2234567890", address, "Medical 24 Poland", ProviderType.SIMPLE);

        // create instance of ServicePoint entity
        ServicePoint servicePoint = new ServicePoint(provider, 1, address);

        // create some instances of ServicePointPhoto entity
        ServicePointPhoto photo1 = new ServicePointPhoto("medical_point.gif", servicePoint);
                          photo1.setDescription("Overall view of Medical service point.");
        ServicePointPhoto photo2 = new ServicePointPhoto("medical_point_front.png", servicePoint);
                          photo2.setDescription("Frontal view of Medical service point.");
        ServicePointPhoto photo3 = new ServicePointPhoto("medical_point_back.gif", servicePoint);
                          photo3.setDescription("Back view of Medical service point.");
        ServicePointPhoto photo4 = new ServicePointPhoto("medical_point_view.png", servicePoint);
                          photo4.setDescription("Bird's eye view of medical point.");

        utx.begin();
        providerFacade.create(provider);
        pointFacade.create(servicePoint);
        photoFacade.create(photo1);
        photoFacade.create(photo2);
        photoFacade.create(photo3);
        photoFacade.create(photo4);
        utx.commit();

        assertTrue("There should be four Service Point Photo entities persisted in database.", photoFacade.count() == 4);

        List<ServicePointPhoto> gifPhotos = photoFacade.findByFileName(".gif");
        List<ServicePointPhoto> pngPhotos = photoFacade.findByFileName(".png");

        assertTrue("There should be two GIF Photos in database.", gifPhotos.size() == 2);
        assertTrue(gifPhotos.contains(photo1) && gifPhotos.contains(photo3));

        assertTrue("There should be two PNG Photos in database.", pngPhotos.size() == 2);
        assertTrue(pngPhotos.contains(photo2) && pngPhotos.contains(photo4));

        List<ServicePointPhoto> birdPhotos = photoFacade.findByFileNameAndDescription(".png", "bird");
        assertTrue("There should be only one PNG Photo with 'bird' keyword in description.", birdPhotos.size() == 1);
        assertTrue(birdPhotos.contains(photo4));

        List<ServicePointPhoto> descPhotos = photoFacade.findByDescription("service");
        assertTrue("There should be found three Photos by specified description.", descPhotos.size() == 3);
        assertTrue(descPhotos.contains(photo1) && descPhotos.contains(photo2) && descPhotos.contains(photo3));

        List<ServicePointPhoto> keyPhotos = photoFacade.findByKeyword("view");
        assertTrue("There should be found four Photos with 'view' keyword in name or description.", keyPhotos.size() == 4);

        List<String> keywords = new ArrayList<>(); keywords.add("service");
        List<ServicePointPhoto> multiplePhotos = photoFacade.findByMultipleCriteria(keywords, null, null, null, null);
        assertTrue("There should be found three Photos by specified keywords.", multiplePhotos.size() == 3);

        photo1.setServicePoint(null);
        photo2.setServicePoint(null);
        photo3.setServicePoint(null);
        photo4.setServicePoint(null);
        photoFacade.remove(photo4);
        photoFacade.remove(photo3);
        photoFacade.remove(photo2);
        photoFacade.remove(photo1);
        providerFacade.remove(provider);

        assertTrue("There should not be any Service Point Photo in database.", photoFacade.count() == 0);
    }

    @Test
    public void shouldFindPhotoByTags() throws Exception {

        // create instance of Provider entity
        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider = new Provider("firma@medical24.pl", "medical24", "aAle2@", "Medical 24 Sp. z o.o.",
                "2234567890", "2234567890", address, "Medical 24 Poland", ProviderType.SIMPLE);

        // create instance of ServicePoint entity
        ServicePoint servicePoint = new ServicePoint(provider, 1, address);

        // create some instances of ServicePointPhoto entity
        ServicePointPhoto photo1 = new ServicePointPhoto("medical_point.gif", servicePoint);
        photo1.setDescription("Overall view of Medical service point.");
        ServicePointPhoto photo2 = new ServicePointPhoto("medical_point_front.png", servicePoint);
        photo2.setDescription("Frontal view of Medical service point.");
        ServicePointPhoto photo3 = new ServicePointPhoto("medical_point_back.gif", servicePoint);
        photo3.setDescription("Back view of Medical service point.");
        ServicePointPhoto photo4 = new ServicePointPhoto("medical_point_view.png", servicePoint);
        photo4.setDescription("Bird's eye view of medical point.");

        // create some instances of Tag entity
        Tag overallTag = new Tag("overall");
        Tag frontTag = new Tag("front");
        Tag backTag = new Tag("back");
        Tag viewTag = new Tag("view");
        Tag birdTag = new Tag("bird");

        // wire Photos with Tags
        photo1.getTags().add(overallTag);
        photo1.getTags().add(viewTag);
        photo2.getTags().add(frontTag);
        photo3.getTags().add(backTag);
        photo4.getTags().add(birdTag);
        photo4.getTags().add(viewTag);

        overallTag.getTaggedPhotos().add(photo1);
        viewTag.getTaggedPhotos().add(photo1);
        frontTag.getTaggedPhotos().add(photo2);
        backTag.getTaggedPhotos().add(photo3);
        birdTag.getTaggedPhotos().add(photo4);
        viewTag.getTaggedPhotos().add(photo4);

        utx.begin();
        providerFacade.create(provider);
        pointFacade.create(servicePoint);
        photoFacade.create(photo1);
        photoFacade.create(photo2);
        photoFacade.create(photo3);
        photoFacade.create(photo4);
        tagFacade.create(overallTag);
        tagFacade.create(frontTag);
        tagFacade.create(backTag);
        tagFacade.create(birdTag);
        tagFacade.create(viewTag);
        utx.commit();

        assertTrue("There should be four Photos persisted in database.", photoFacade.count() == 4);
        assertTrue("There should be five Tags persisted in database.", tagFacade.count() == 5);

        List<ServicePointPhoto> vTagPhotos = photoFacade.findByTagName("v");
        assertTrue("There should be two Photos with tag name containing 'v' symbol.", vTagPhotos.size() == 2);
        assertTrue(vTagPhotos.contains(photo1) && vTagPhotos.contains(photo4));

        List<String> tagNames = new ArrayList<>();
        tagNames.add(overallTag.getTagName()); tagNames.add(viewTag.getTagName());

        List<ServicePointPhoto> anyTagPhotos = photoFacade.findByAnyTagNames(tagNames);
        assertTrue("There should be two Photos with some of specified tag names.", anyTagPhotos.size() == 2);
        assertTrue(anyTagPhotos.contains(photo1) && anyTagPhotos.contains(photo4));

        List<ServicePointPhoto> allTagPhotos = photoFacade.findByAllTagNames(tagNames);
        assertTrue("There should be one Photo with all of specified tag names.", allTagPhotos.size() == 1);
        assertTrue(allTagPhotos.contains(photo1));

        List<Tag> tags = new ArrayList<>();
        tags.add(overallTag); tags.add(viewTag);
        allTagPhotos = photoFacade.findByAllTags(tags);
        assertTrue("There should be one Photo with all of specified tags.", allTagPhotos.size() == 1);
        assertTrue(allTagPhotos.contains(photo1));

        assertTrue(photoFacade.findByKeywordIncludingTags("front").size() == 1);

        providerFacade.remove(provider);
        viewTag.setTaggedPhotos(null); tagFacade.remove(viewTag);
        birdTag.setTaggedPhotos(null); tagFacade.remove(birdTag);
        backTag.setTaggedPhotos(null); tagFacade.remove(backTag);
        frontTag.setTaggedPhotos(null); tagFacade.remove(frontTag);
        overallTag.setTaggedPhotos(null); tagFacade.remove(overallTag);

        assertTrue("There should not be any Photo in database.", photoFacade.count() == 0);
        assertTrue("There should not be any Tag in database.", tagFacade.count() == 0);

    }

    @Test
    public void shouldFindByOwners() throws Exception {

        // create some instances of Corporation
        Address address1 = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Corporation corporation = new Corporation("Medical24", "medical24_logo.png", address1);

        // create some instances of Provider
        Address address11 =  new Address("Poznanska", "25", "29-100", "Poznan", "Wielkopolska", "Poland");
        Address address12 = new Address("Wielkopolska", "15", "29-100", "Poznan", "Wielkopolska", "Poland");
        Address address13 = new Address("Warszawska", "50", "61-066", "Poznan", "Wielkopolska", "Poland");
        Provider provider1 = new Provider("firma@fryzjer.pl", "fryzjer_pl", "aAle2@_", "FryzjerPl Sp. z o.o..",
                "2234567890", "2234567890", address11, "FryzjerPl", ProviderType.SIMPLE);

        Address address21 = new Address("Wroclawska", "45", "10-140", "Szczecin", "Zachodnio Pomorskie", "Poland");
        Address address22 = new Address("Pomorska", "150", "10-140", "Szczecin", "Zachodnio Pomorskie", "Poland");
        Provider provider2 = new Provider("firma@medical.pl", "medical", "tIe%13?", "Medical Sp. z o.o.",
                "6593878688", "6510029930", address21, "Medical Poland", ProviderType.CORPORATE);
        // wiring corporation to provider
        provider2.setCorporation(corporation);
        corporation.getProviders().add(provider2);

        Address address31 = new Address("Szczecinska", "25", "15-200", "Stargard Szczecinski", "Zachodnio Pomorskie", "Poland");
        Address address32 = new Address("Ludzi Morza", "20", "25-100", "Swinoujscie", "Zachodnio Pomorskie", "Poland");
        Provider provider3 = new Provider("firma@dentystka.pl", "dentyska_pl", "fRyZU123?", "DentystkaPl Sp. z o.o.",
                "1910020030", "1930040050", address31, "DentystkaPl Stargard Szczecinski", ProviderType.SIMPLE);

        // create some instances of ServicePoint
        ServicePoint point11  = new ServicePoint(provider1, 1, address11);
        ServicePoint point12 = new ServicePoint(provider1, 2, address12);
        ServicePoint point13 = new ServicePoint(provider1, 3, address13);
        ServicePoint point21 = new ServicePoint(provider2, 1, address21);
        ServicePoint point22 = new ServicePoint(provider2, 2, address22);
        ServicePoint point31 = new ServicePoint(provider3, 1, address31);
        ServicePoint point32 = new ServicePoint(provider3, 2, address32);

        // create some instances of Service Point Photo
        ServicePointPhoto photo111 = new ServicePointPhoto("photo111.png", point11);
        ServicePointPhoto photo112 = new ServicePointPhoto("photo112.gif", point11);
        ServicePointPhoto photo121 = new ServicePointPhoto("photo121.jpeg", point12);
        ServicePointPhoto photo131 = new ServicePointPhoto("photo131.png", point13);
        ServicePointPhoto photo211 = new ServicePointPhoto("photo211.png", point21);
        ServicePointPhoto photo212 = new ServicePointPhoto("photo212.png", point21);
        ServicePointPhoto photo221 = new ServicePointPhoto("photo221.gif", point22);
        ServicePointPhoto photo311 = new ServicePointPhoto("photo311.png", point31);
        ServicePointPhoto photo321 = new ServicePointPhoto("photo321.gif", point32);
        ServicePointPhoto photo322 = new ServicePointPhoto("photo322.gif", point32);

        // persist entities in database
        utx.begin();
        corporationFacade.create(corporation);

        providerFacade.create(provider1);
        providerFacade.create(provider2);
        providerFacade.create(provider3);

        pointFacade.create(point11);
        pointFacade.create(point12);
        pointFacade.create(point13);
        pointFacade.create(point21);
        pointFacade.create(point22);
        pointFacade.create(point31);
        pointFacade.create(point32);

        photoFacade.create(photo111);
        photoFacade.create(photo112);
        photoFacade.create(photo121);
        photoFacade.create(photo131);
        photoFacade.create(photo211);
        photoFacade.create(photo212);
        photoFacade.create(photo221);
        photoFacade.create(photo311);
        photoFacade.create(photo321);
        photoFacade.create(photo322);
        utx.commit();

        List<ServicePointPhoto> corpoPhotos = photoFacade.findByCorporation(corporation);
        assertTrue("There should be three Photos assigned to service points associated with given corporation.", corpoPhotos.size() == 3);
        assertTrue(corpoPhotos.contains(photo211) && corpoPhotos.contains(photo212) && corpoPhotos.contains(photo221));

        List<ServicePointPhoto> providerPhotos = photoFacade.findByProvider(provider1);
        assertTrue("There should be four Photos assigned to service points associated with given provider.", providerPhotos.size() == 4);
        assertTrue(providerPhotos.contains(photo111) && providerPhotos.contains(photo112) && providerPhotos.contains(photo121) && providerPhotos.contains(photo131));

        utx.begin();
        point32 = pointFacade.find(new ServicePointId(point32.getProvider().getUserId(), point32.getServicePointNumber()));
        List<ServicePointPhoto> pointPhotos = photoFacade.findByServicePoint(point32);
        utx.commit();
        assertTrue("There should be two Photos assigned to given service point.", pointPhotos.size() == 2);


        utx.begin();
        List<ServicePoint> servicePoints = new ArrayList<>();
        servicePoints.add( pointFacade.find(new ServicePointId(point11.getProvider().getUserId(), point11.getServicePointNumber())) );
        servicePoints.add( pointFacade.find(new ServicePointId(point12.getProvider().getUserId(), point12.getServicePointNumber())) );
        servicePoints.add( pointFacade.find(new ServicePointId(point13.getProvider().getUserId(), point13.getServicePointNumber())) );
        List<Provider> providers = new ArrayList<>();
        providers.add(provider1);
        providers.add(provider2);
        List<String> keywords = new ArrayList<>();
        keywords.add("png");
        List<ServicePointPhoto> multiPhotos = photoFacade.findByMultipleCriteria(keywords,  null,  servicePoints, providers, null);
        utx.commit();

        assertTrue("There should be two Photos satisfying given criteria.", multiPhotos.size() == 2);
        assertTrue(multiPhotos.contains(photo111) && multiPhotos.contains(photo131));

        provider1.setCorporation(null);
        provider2.setCorporation(null);
        provider3.setCorporation(null);
        providerFacade.remove(provider1);
        providerFacade.remove(provider2);
        providerFacade.remove(provider3);

        corporation.setProviders(null);
        corporationFacade.remove(corporation);

        assertTrue("There should not be any Photo in database.", photoFacade.count() == 0);
        assertTrue("There should not be any Corporation in database.", corporationFacade.count() == 0);

    }
}