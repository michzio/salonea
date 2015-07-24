import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.interfaces.IndustryFacadeInterface;
import pl.salonea.ejb.interfaces.ProviderFacadeInterface;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.Industry;
import pl.salonea.entities.Provider;
import pl.salonea.enums.ProviderType;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * IndustryFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jul 24, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class IndustryFacadeIT {

    private static final Logger logger = Logger.getLogger(IndustryFacadeIT.class.getName());

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
    private IndustryFacadeInterface.Local industryFacade;

    @Inject
    private ProviderFacadeInterface.Local providerFacade;

    @Inject
    private UserTransaction utx;

    @Test
    public void shouldCreateNewIndustry() {

        // create instance of Industry
        Industry industry = new Industry("Cosmetics");
        industryFacade.create(industry);

        assertNotNull("Industry ID should not be null.", industry.getIndustryId());

        Industry foundIndustry = industryFacade.find(industry.getIndustryId());
        assertEquals("Should find the same industry entity.", industry, foundIndustry);
        assertTrue(industryFacade.findAll().contains(industry));

        assertTrue("There should be only on industry entity.", industryFacade.count() == 1);

        // removing Industry from persistent storage
        industryFacade.remove(foundIndustry);

        assertTrue("There should not be any industry entity.", industryFacade.count() == 0);

    }

    @Test
    public void shouldFindIndustryByName() {

        // create some instances of Industry
        Industry industry1 = new Industry("Cosmetics");
        Industry industry2 = new Industry("Hairdressing");
        Industry industry3 = new Industry("Automotive");
        Industry industry4 = new Industry("Dental");

        // persist in database
        industryFacade.create(industry1);
        industryFacade.create(industry2);
        industryFacade.create(industry3);
        industryFacade.create(industry4);

        assertTrue("There should be four industries persisted.", industryFacade.count() == 4);

        assertTrue(industryFacade.findByName("Dental").contains(industry4));
        assertTrue(industryFacade.findByName("Automotive").contains(industry3));
        assertTrue(industryFacade.findByName("Hairdressing").contains(industry2));
        assertTrue(industryFacade.findByName("Cosmetics").contains(industry1));

        assertEquals(industryFacade.findForName("Dental"), industry4);
        assertEquals(industryFacade.findForName("Automotive"), industry3);
        assertEquals(industryFacade.findForName("Hairdressing"), industry2);
        assertEquals(industryFacade.findForName("Cosmetics"), industry1);

        // removing industries
        industryFacade.remove(industry4);
        industryFacade.remove(industry3);
        industryFacade.remove(industry2);
        industryFacade.remove(industry1);

    }

    @Test
    public void shouldFindByProvider() throws Exception {

        // create some instances of Industry entity
        Industry industry1 = new Industry("Cosmetics");
        Industry industry2 = new Industry("Hairdressing");
        Industry industry3 = new Industry("Automotive");
        Industry industry4 = new Industry("Dental");

        // create some instances of Provider entity
        Address address1 =  new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider1 = new Provider("firma@allegro.pl", "allegro", "aAle2@_", "Allegro Ltd.",
                "2234567890", "2234567890", address1, "Allegro Polska", ProviderType.SIMPLE);

        Address address2 = new Address("Wrocławska", "45", "10-140", "Szczecin", "Zachodnio Pomorskie", "Poland");
        Provider provider2 = new Provider("firma@tieto.pl", "tieto", "tIe%13?", "Tieto Sp. z o.o.",
                "6593878688", "6510029930", address2, "Tieto Poland", ProviderType.SIMPLE);

        industry1.getProviders().add(provider1);
        industry1.getProviders().add(provider2);
        industry2.getProviders().add(provider1);
        industry3.getProviders().add(provider2);
        industry4.getProviders().add(provider2);
        provider1.getIndustries().add(industry1);
        provider1.getIndustries().add(industry2);
        provider2.getIndustries().add(industry1);
        provider2.getIndustries().add(industry3);
        provider2.getIndustries().add(industry4);

        // persist providers and industries
        providerFacade.create(provider1);
        providerFacade.create(provider2);
        industryFacade.create(industry1);
        industryFacade.create(industry2);
        industryFacade.create(industry3);
        industryFacade.create(industry4);

        List<Industry> industries1 = industryFacade.findByProvider(provider1);
        List<Industry> industries2 = industryFacade.findByProvider(provider2);

        assertTrue("The first provider should function in two industries.", industries1.size() == 2);
        assertTrue(industries1.contains(industry1) && industries1.contains(industry2));

        assertTrue("The second provider should function in three industries.", industries2.size() == 3);
        assertTrue(industries2.contains(industry1) && industries2.contains(industry3) && industries2.contains(industry4));

        // removing providers and industries
        utx.begin();
        providerFacade.remove(provider2);
        providerFacade.remove(provider1);
        industryFacade.remove(industry4);
        industryFacade.remove(industry3);
        industryFacade.remove(industry2);
        industryFacade.remove(industry1);
        utx.commit();

    }
}