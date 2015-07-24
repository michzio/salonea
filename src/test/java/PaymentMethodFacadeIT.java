import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.interfaces.PaymentMethodFacadeInterface;
import pl.salonea.entities.PaymentMethod;

import javax.inject.Inject;
import java.io.File;
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

}
