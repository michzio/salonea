import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.interfaces.*;
import pl.salonea.ejb.stateless.VirtualTourFacade;
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
 * VirtualTourFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Aug 8, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class VirtualTourFacadeIT {

    private static final Logger logger = Logger.getLogger(VirtualTourFacadeIT.class.getName());

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
    private VirtualTourFacadeInterface.Local virtualTourFacade;

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
    public void shouldCreateNewVirtualTour() throws Exception {

        // create instance of Provider entity
        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider = new Provider("firma@medical24.pl", "medical24", "aAle2@", "Medical 24 Sp. z o.o.",
                "2234567890", "2234567890", address, "Medical 24 Poland", ProviderType.SIMPLE);

        // create instance of ServicePoint entity
        ServicePoint servicePoint = new ServicePoint(provider, 1, address);

        // create instance of VirtualTour entity
        VirtualTour virtualTour = new VirtualTour("medical_point_tour.swf", servicePoint);

        utx.begin();
        providerFacade.create(provider);
        pointFacade.create(servicePoint);
        virtualTourFacade.create(virtualTour);
        utx.commit();

        assertTrue("There should be one Virtual Tour persisted in database.", virtualTourFacade.count() == 1);
        assertNotNull("Virtual Tour ID should not be null.", virtualTour.getTourId());

        assertEquals("Virtual Tour persisted and found should be the same.", virtualTourFacade.find(virtualTour.getTourId()) , virtualTour);

        virtualTour.setServicePoint(null);
        virtualTourFacade.remove(virtualTour);
        providerFacade.remove(provider);

        assertTrue("There should not be any Virtual Tour in database.", virtualTourFacade.count() == 0);

    }

    @Test
    public void shouldFindVirtualToursByKeywords() throws Exception {

        // create instance of Provider entity
        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider = new Provider("firma@medical24.pl", "medical24", "aAle2@", "Medical 24 Sp. z o.o.",
                "2234567890", "2234567890", address, "Medical 24 Poland", ProviderType.SIMPLE);

        // create instance of ServicePoint entity
        ServicePoint servicePoint = new ServicePoint(provider, 1, address);

        // create some instances of VirtualTour entity
        VirtualTour tour1 = new VirtualTour("medical_point.swf", servicePoint);
                    tour1.setDescription("Overall view of Medical service point.");
        VirtualTour tour2 = new VirtualTour("medical_point_front.swf", servicePoint);
                    tour2.setDescription("Frontal view of Medical service point.");
        VirtualTour tour3 = new VirtualTour("medical_point_back.swf", servicePoint);
                    tour3.setDescription("Back view of Medical service point.");
        VirtualTour tour4 = new VirtualTour("medical_point_view.swf", servicePoint);
                    tour4.setDescription("Bird's eye view of medical point.");

        utx.begin();
        providerFacade.create(provider);
        pointFacade.create(servicePoint);
        virtualTourFacade.create(tour1);
        virtualTourFacade.create(tour2);
        virtualTourFacade.create(tour3);
        virtualTourFacade.create(tour4);
        utx.commit();

        assertTrue("There should be four Virtual Tour entities persisted in database.", virtualTourFacade.count() == 4);

        List<VirtualTour> swfTours = virtualTourFacade.findByFileName(".swf");
        List<VirtualTour> frontTours = virtualTourFacade.findByFileName("front");

        assertTrue("There should be four swf Virtual Tours in database.", swfTours.size() == 4);
        assertTrue(swfTours.contains(tour1) && swfTours.contains(tour2) &&
                   swfTours.contains(tour3) && swfTours.contains(tour4));

        assertTrue("There should be one front Virtual Tour in database.", frontTours.size() == 1);
        assertTrue(frontTours.contains(tour2));

        List<VirtualTour> birdTours = virtualTourFacade.findByFileNameAndDescription(".swf", "bird");
        assertTrue("There should be only one swf Virtual Tour with 'bird' keyword in description.", birdTours.size() == 1);
        assertTrue(birdTours.contains(tour4));

        List<VirtualTour> descTours = virtualTourFacade.findByDescription("service");
        assertTrue("There should be found three Virtual Tours by specified description.", descTours.size() == 3);
        assertTrue(descTours.contains(tour1) && descTours.contains(tour2) && descTours.contains(tour3));

        List<VirtualTour> keyTours = virtualTourFacade.findByKeyword("view");
        assertTrue("There should be found four Virtual Tours with 'view' keyword in name or description.", keyTours.size() == 4);

        List<String> keywords = new ArrayList<>(); keywords.add("service");
        List<VirtualTour> multipleTours = virtualTourFacade.findByMultipleCriteria(keywords, null, null, null, null);
        assertTrue("There should be found three Virtual Tours by specified keywords.", multipleTours.size() == 3);

        tour1.setServicePoint(null);
        tour2.setServicePoint(null);
        tour3.setServicePoint(null);
        tour4.setServicePoint(null);
        virtualTourFacade.remove(tour4);
        virtualTourFacade.remove(tour3);
        virtualTourFacade.remove(tour2);
        virtualTourFacade.remove(tour1);
        providerFacade.remove(provider);

        assertTrue("There should not be any Virtual Tour in database.", virtualTourFacade.count() == 0);
    }

    @Test
    public void shouldFindVirtualToursByTags() throws Exception {

        // create instance of Provider entity
        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider = new Provider("firma@medical24.pl", "medical24", "aAle2@", "Medical 24 Sp. z o.o.",
                "2234567890", "2234567890", address, "Medical 24 Poland", ProviderType.SIMPLE);

        // create instance of ServicePoint entity
        ServicePoint servicePoint = new ServicePoint(provider, 1, address);

        // create some instances of VirtualTour entity
        VirtualTour tour1 = new VirtualTour("medical_point.swf", servicePoint);
                    tour1.setDescription("Overall view of Medical service point.");
        VirtualTour tour2 = new VirtualTour("medical_point_front.swf", servicePoint);
                    tour2.setDescription("Frontal view of Medical service point.");
        VirtualTour tour3 = new VirtualTour("medical_point_back.swf", servicePoint);
                    tour3.setDescription("Back view of Medical service point.");
        VirtualTour tour4 = new VirtualTour("medical_point_view.swf", servicePoint);
                    tour4.setDescription("Bird's eye view of medical point.");

        // create some instances of Tag entity
        Tag overallTag = new Tag("overall");
        Tag frontTag = new Tag("front");
        Tag backTag = new Tag("back");
        Tag viewTag = new Tag("view");
        Tag birdTag = new Tag("bird");

        // wire Virtual Tours with Tags
        tour1.getTags().add(overallTag);
        tour1.getTags().add(viewTag);
        tour2.getTags().add(frontTag);
        tour3.getTags().add(backTag);
        tour4.getTags().add(birdTag);
        tour4.getTags().add(viewTag);

        overallTag.getTaggedVirtualTours().add(tour1);
        viewTag.getTaggedVirtualTours().add(tour1);
        frontTag.getTaggedVirtualTours().add(tour2);
        backTag.getTaggedVirtualTours().add(tour3);
        birdTag.getTaggedVirtualTours().add(tour4);
        viewTag.getTaggedVirtualTours().add(tour4);

        utx.begin();
        providerFacade.create(provider);
        pointFacade.create(servicePoint);
        virtualTourFacade.create(tour1);
        virtualTourFacade.create(tour2);
        virtualTourFacade.create(tour3);
        virtualTourFacade.create(tour4);
        tagFacade.create(overallTag);
        tagFacade.create(frontTag);
        tagFacade.create(backTag);
        tagFacade.create(birdTag);
        tagFacade.create(viewTag);
        utx.commit();

        assertTrue("There should be four Virtual Tours persisted in database.", virtualTourFacade.count() == 4);
        assertTrue("There should be five Tags persisted in database.", tagFacade.count() == 5);

        List<VirtualTour> vTagTours = virtualTourFacade.findByTagName("v");
        assertTrue("There should be two Virtual Tours with tag name containing 'v' symbol.", vTagTours.size() == 2);
        assertTrue(vTagTours.contains(tour1) && vTagTours.contains(tour4));

        List<String> tagNames = new ArrayList<>();
        tagNames.add(overallTag.getTagName()); tagNames.add(viewTag.getTagName());

        List<VirtualTour> anyTagTours = virtualTourFacade.findByAnyTagNames(tagNames);
        assertTrue("There should be two Virtual Tours with some of specified tag names.", anyTagTours.size() == 2);
        assertTrue(anyTagTours.contains(tour1) && anyTagTours.contains(tour4));

        List<VirtualTour> allTagTours = virtualTourFacade.findByAllTagNames(tagNames);
        assertTrue("There should be one Virtual Tour with all of specified tag names.", allTagTours.size() == 1);
        assertTrue(allTagTours.contains(tour1));

        List<Tag> tags = new ArrayList<>();
        tags.add(overallTag); tags.add(viewTag);
        allTagTours = virtualTourFacade.findByAllTags(tags);
        assertTrue("There should be one Virtual Tour with all of specified tags.", allTagTours.size() == 1);
        assertTrue(allTagTours.contains(tour1));

        assertTrue(virtualTourFacade.findByKeywordIncludingTags("front").size() == 1);

        providerFacade.remove(provider);
        viewTag.setTaggedVirtualTours(null); tagFacade.remove(viewTag);
        birdTag.setTaggedVirtualTours(null); tagFacade.remove(birdTag);
        backTag.setTaggedVirtualTours(null); tagFacade.remove(backTag);
        frontTag.setTaggedVirtualTours(null); tagFacade.remove(frontTag);
        overallTag.setTaggedVirtualTours(null); tagFacade.remove(overallTag);

        assertTrue("There should not be any Virtual Tour in database.", virtualTourFacade.count() == 0);
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
        ServicePoint point11 = new ServicePoint(provider1, 1, address11);
        ServicePoint point12 = new ServicePoint(provider1, 2, address12);
        ServicePoint point13 = new ServicePoint(provider1, 3, address13);
        ServicePoint point21 = new ServicePoint(provider2, 1, address21);
        ServicePoint point22 = new ServicePoint(provider2, 2, address22);
        ServicePoint point31 = new ServicePoint(provider3, 1, address31);
        ServicePoint point32 = new ServicePoint(provider3, 2, address32);

        // create some instances of Virtual Tours
        VirtualTour tour111 = new VirtualTour("tour111.swf", point11);
        VirtualTour tour112 = new VirtualTour("tour112.swf", point11);
        VirtualTour tour121 = new VirtualTour("tour121.swf", point12);
        VirtualTour tour131 = new VirtualTour("tour131.swf", point13);
        VirtualTour tour211 = new VirtualTour("tour211.swf", point21);
        VirtualTour tour212 = new VirtualTour("tour212.swf", point21);
        VirtualTour tour221 = new VirtualTour("tour221.swf", point22);
        VirtualTour tour311 = new VirtualTour("tour311.swf", point31);
        VirtualTour tour321 = new VirtualTour("tour321.swf", point32);
        VirtualTour tour322 = new VirtualTour("tour322.swf", point32);

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

        virtualTourFacade.create(tour111);
        virtualTourFacade.create(tour112);
        virtualTourFacade.create(tour121);
        virtualTourFacade.create(tour131);
        virtualTourFacade.create(tour211);
        virtualTourFacade.create(tour212);
        virtualTourFacade.create(tour221);
        virtualTourFacade.create(tour311);
        virtualTourFacade.create(tour321);
        virtualTourFacade.create(tour322);
        utx.commit();

        // by corporation
        List<VirtualTour> corpoTours = virtualTourFacade.findByCorporation(corporation);
        assertTrue("There should be three Virtual Tours assigned to service points associated with given corporation.", corpoTours.size() == 3);
        assertTrue(corpoTours.contains(tour211) && corpoTours.contains(tour212) && corpoTours.contains(tour221));

        // by provider
        List<VirtualTour> providerTours = virtualTourFacade.findByProvider(provider1);
        assertTrue("There should be four Virtual Tours assigned to service points associated with given provider.", providerTours.size() == 4);
        assertTrue(providerTours.contains(tour111) && providerTours.contains(tour112) && providerTours.contains(tour121) && providerTours.contains(tour131));

        // by service point
        utx.begin();
        point32 = pointFacade.find(new ServicePointId(point32.getProvider().getUserId(), point32.getServicePointNumber()));
        List<VirtualTour> pointTours = virtualTourFacade.findByServicePoint(point32);
        utx.commit();
        assertTrue("There should be two Virtual Tours assigned to given service point.", pointTours.size() == 2);

        // by multiple criteria
        utx.begin();
        List<ServicePoint> servicePoints = new ArrayList<>();
        servicePoints.add( pointFacade.find(new ServicePointId(point11.getProvider().getUserId(), point11.getServicePointNumber()))  );
        servicePoints.add( pointFacade.find(new ServicePointId(point12.getProvider().getUserId(), point12.getServicePointNumber())) );
        servicePoints.add( pointFacade.find(new ServicePointId(point13.getProvider().getUserId(), point13.getServicePointNumber())) );
        List<Provider> providers = new ArrayList<>();
        providers.add(provider1);
        providers.add(provider2);
        List<String> keywords = new ArrayList<>();
        keywords.add("swf");
        List<VirtualTour> multiTours = virtualTourFacade.findByMultipleCriteria(keywords,  null,  servicePoints, providers, null, null);
        utx.commit();

        assertTrue("There should be four Virtual Tours satisfying given criteria.", multiTours.size() == 4);
        assertTrue(multiTours.contains(tour111) && multiTours.contains(tour112) && multiTours.contains(tour121) && multiTours.contains(tour131));

        // removing entities from database
        provider1.setCorporation(null);
        provider2.setCorporation(null);
        provider3.setCorporation(null);
        providerFacade.remove(provider1);
        providerFacade.remove(provider2);
        providerFacade.remove(provider3);

        corporation.setProviders(null);
        corporationFacade.remove(corporation);

        assertTrue("There should not be any Virtual Tour in database.", virtualTourFacade.count() == 0);
        assertTrue("There should not be any Service Point in database.", pointFacade.count() == 0);
        assertTrue("There should not be any Provider in database.", providerFacade.count() == 0);
        assertTrue("There should not be any Corporation in database.", corporationFacade.count() == 0);

    }

}
