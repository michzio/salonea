import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.interfaces.PaymentMethodFacadeInterface;
import pl.salonea.ejb.interfaces.ProviderFacadeInterface;
import pl.salonea.ejb.stateless.ProviderFacade;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.PaymentMethod;
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
 * PaymentMethodFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jul 24, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class PaymentMethodFacadeIT {

    private static final Logger logger = Logger.getLogger(PaymentMethodFacadeIT.class.getName());

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
    private PaymentMethodFacadeInterface.Local paymentFacade;

    @Inject
    private ProviderFacadeInterface.Local providerFacade;

    @Inject
    private UserTransaction utx;

    @Test
    public void shouldCreateNewPaymentMethod() {

        // create instance of PaymentMethod entity
        PaymentMethod paymentMethod = new PaymentMethod("Credit Card", true);
        paymentFacade.create(paymentMethod);

        assertNotNull("Payment Method ID should not be null.", paymentMethod.getId());
        assertTrue("There should be persisted only one payment method.", paymentFacade.count() == 1);

        PaymentMethod foundPaymentMethod = paymentFacade.find(paymentMethod.getId());
        assertEquals("The found entity instance should be the same.", foundPaymentMethod, paymentMethod);

        paymentFacade.remove(foundPaymentMethod);
        assertTrue("There should not be persisted any payment method.", paymentFacade.count() == 0);

    }

    @Test
    public void shouldFindPaymentMethodByName() {

        // create some instances of PaymentMethod entity
        PaymentMethod paymentMethod1 = new PaymentMethod("Credit Card", true);
        PaymentMethod paymentMethod2 = new PaymentMethod("Cash", false);
        PaymentMethod paymentMethod3 = new PaymentMethod("Bank Transfer", true);

        paymentFacade.create(paymentMethod1);
        paymentFacade.create(paymentMethod2);
        paymentFacade.create(paymentMethod3);

        assertTrue("There should be three payment methods persisted.", paymentFacade.count() == 3);

        assertEquals(paymentFacade.findForName("Credit Card"), paymentMethod1);
        assertEquals(paymentFacade.findForName("Cash"), paymentMethod2);
        assertEquals(paymentFacade.findForName("Bank Transfer"), paymentMethod3);

        List<PaymentMethod> methods1 = paymentFacade.findByName("Ca");
        List<PaymentMethod> methods2 = paymentFacade.findByName("Transfer");

        assertTrue("There should be two payment methods containing given name.", methods1.size() == 2);
        assertTrue(methods1.contains(paymentMethod1));
        assertTrue(methods1.contains(paymentMethod2));
        assertTrue("There should be only one payment method with given name.", methods2.size() == 1);
        assertTrue(methods2.contains(paymentMethod3));

        paymentFacade.remove(paymentMethod3);
        paymentFacade.remove(paymentMethod2);
        paymentFacade.remove(paymentMethod1);
    }

    @Test
    public void shouldFindPaymentMethodByInAdvance() {

        // create some instances of PaymentMethod entity
        PaymentMethod paymentMethod1 = new PaymentMethod("Credit Card", true);
        PaymentMethod paymentMethod2 = new PaymentMethod("Cash", false);
        PaymentMethod paymentMethod3 = new PaymentMethod("Bank Transfer", true);

        paymentFacade.create(paymentMethod1);
        paymentFacade.create(paymentMethod2);
        paymentFacade.create(paymentMethod3);

        assertTrue("There should be three payment methods persisted.", paymentFacade.count() == 3);

        List<PaymentMethod> inAdvanceMethods = paymentFacade.findInAdvance(true);
        List<PaymentMethod> notInAdvanceMethods = paymentFacade.findInAdvance(false);

        assertTrue("There should be two payment methods with in advance payment.", inAdvanceMethods.size() == 2);
        assertTrue(inAdvanceMethods.contains(paymentMethod3));
        assertTrue(inAdvanceMethods.contains(paymentMethod1));

        assertTrue("There should be only one payment method without advance payment.", notInAdvanceMethods.size() == 1);
        assertTrue(notInAdvanceMethods.contains(paymentMethod2));

        assertTrue(paymentFacade.findByNameAndInAdvance("Ca", true).contains(paymentMethod1));
        assertTrue(paymentFacade.findByNameAndInAdvance("Ca", false).contains(paymentMethod2));

        paymentFacade.remove(paymentMethod3);
        paymentFacade.remove(paymentMethod2);
        paymentFacade.remove(paymentMethod1);
    }

    @Test
    public void findPaymentMethodByProvider() throws Exception {

        // create some instances of PaymentMethod entity
        PaymentMethod paymentMethod1 = new PaymentMethod("Credit Card", true);
        PaymentMethod paymentMethod2 = new PaymentMethod("Cash", false);
        PaymentMethod paymentMethod3 = new PaymentMethod("Bank Transfer", true);

        // create some Provider instances
        // create some instances of Provider entity
        Address address1 =  new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider1 = new Provider("firma@allegro.pl", "allegro", "aAle2@_", "Allegro Ltd.",
                "2234567890", "2234567890", address1, "Allegro Polska", ProviderType.SIMPLE);

        Address address2 = new Address("Wrocławska", "45", "10-140", "Szczecin", "Zachodnio Pomorskie", "Poland");
        Provider provider2 = new Provider("firma@tieto.pl", "tieto", "tIe%13?", "Tieto Sp. z o.o.",
                "6593878688", "6510029930", address2, "Tieto Poland", ProviderType.SIMPLE);

        provider1.getAcceptedPaymentMethods().add(paymentMethod1);
        provider1.getAcceptedPaymentMethods().add(paymentMethod3);
        provider2.getAcceptedPaymentMethods().add(paymentMethod2);
        provider2.getAcceptedPaymentMethods().add(paymentMethod3);
        paymentMethod1.getAcceptingProviders().add(provider1);
        paymentMethod2.getAcceptingProviders().add(provider2);
        paymentMethod3.getAcceptingProviders().add(provider1);
        paymentMethod3.getAcceptingProviders().add(provider2);

        paymentFacade.create(paymentMethod1);
        paymentFacade.create(paymentMethod2);
        paymentFacade.create(paymentMethod3);
        providerFacade.create(provider1);
        providerFacade.create(provider2);

        List<PaymentMethod> for1stProvider = paymentFacade.findByProvider(provider1);
        List<PaymentMethod> for2ndProvider = paymentFacade.findByProvider(provider2);

        assertTrue("There should be defined two payment methods for first provider.", for1stProvider.size() == 2);
        assertTrue(for1stProvider.contains(paymentMethod1) && for1stProvider.contains(paymentMethod3));
        assertTrue("There should be defined two payment methods for second provider.", for2ndProvider.size() == 2);
        assertTrue(for2ndProvider.contains(paymentMethod2) && for2ndProvider.contains(paymentMethod3));

        // removing providers and payment methods
        utx.begin();
        providerFacade.remove(provider2);
        providerFacade.remove(provider1);
        paymentFacade.remove(paymentMethod3);
        paymentFacade.remove(paymentMethod2);
        paymentFacade.remove(paymentMethod1);
        utx.commit();

    }

}
