import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.interfaces.HistoricalTransactionFacadeInterface;

import javax.inject.Inject;
import java.io.File;
import java.util.logging.Logger;

/**
 * HistoricalTransactionFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Aug 25, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class HistoricalTransactionFacadeIT {

    private static final Logger logger = Logger.getLogger(HistoricalTransactionFacadeIT.class.getName());

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
    private HistoricalTransactionFacadeInterface.Local transactionFacade;

    @Test
    public void shouldCreateNewHistoricalTransaction() {
        // TODO implement integration test that creates new Historical Transaction entity and test its basic behaviour
    }

    @Test
    public void shouldFindHistoricalTransactionByClient() {
        // TODO integration test that finds Historical Transaction entities by Client and other criteria
    }

    @Test
    public void shouldFindHistoricalTransactionByTime() {
        // TODO integration test that finds Historical Transaction entities for given period of time both transaction and booked time
    }

    @Test
    public void shouldFindHistoricalTransactionByPriceAndCurrency() {
        // TODO integration test that finds Historical Transaction entities by price range and currency code combinations or payment methods
    }

    @Test
    public void shouldFindHistoricalTransactionByProviderAndService() {
        // TODO integration test that finds Historical Transaction entities by providers, service points, work stations and services
    }

    @Test
    public void shouldFindHistoricalTransactionByMultipleCriteria() {
        // TODO integration test that finds Historical Transaction entities by multiple criteria
    }

    @Test
    public void shouldFindHistoricalTransactionByCompletionStatus() {
        // TODO integration test that finds Historical Transaction entities by completion status
    }

    @Test
    public void shouldFindHistoricalTransactionByRatings() {
        // TODO integration test that finds Historical Transaction entities by client rating or provider (employee) rating
    }

}
