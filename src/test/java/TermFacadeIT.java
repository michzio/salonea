import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.interfaces.TermFacadeInterface;
import pl.salonea.entities.Term;

import javax.inject.Inject;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * TermFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Aug 18, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class TermFacadeIT {

    private static final Logger logger = Logger.getLogger(TermFacadeIT.class.getName());

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
    private TermFacadeInterface.Local termFacade;

    @Test
    public void shouldCreateNewTerm() {

        // get opening and closing datetimes
        Calendar calendar = new GregorianCalendar(2018, 1, 12, 8, 00);
        Date openingTime = calendar.getTime();
        calendar.add(Calendar.HOUR_OF_DAY, 8);
        Date closingTime = calendar.getTime();

        // create instance of Term entity
        Term term = new Term(openingTime, closingTime);

        // persist Term entity
        termFacade.create(term);

        assertTrue("There should be one Term entity in database.", termFacade.count() == 1);
        assertNotNull("Term ID can not be null.", term.getTermId());

        Term foundTerm = termFacade.find(term.getTermId());
        assertEquals("Term persisted and found should be the same.", foundTerm, term);

        // remove found Term
        termFacade.remove(foundTerm);
        assertTrue("There should not be any Term entity in database.", termFacade.count() == 0);
    }

    @Test
    public void shouldFindTermByPeriodOrTime() {
        // TODO finding terms by given period or before/after given time
        // TODO deleting terms older than
    }

    @Test
    public void shouldFindTermByEmployee() {
        // TODO finding terms by employee
    }

    @Test
    public void shouldFindTermByProviderService() {
        // TODO finding terms by service or provider service
    }

    @Test
    public void shouldFindTermByWorkStation() {
        // TODO finding terms by work station
    }

    @Test
    public void shouldFindTermByServicePoint() {
        // TODO finding terms by service point
    }

}