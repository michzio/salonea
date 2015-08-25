import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.interfaces.ClientFacadeInterface;
import pl.salonea.ejb.interfaces.EmployeeFacadeInterface;
import pl.salonea.ejb.interfaces.EmployeeRatingFacadeInterface;
import pl.salonea.entities.Client;
import pl.salonea.entities.Employee;
import pl.salonea.entities.EmployeeRating;
import pl.salonea.entities.idclass.EmployeeRatingId;
import pl.salonea.enums.Gender;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * EmployeeRatingFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Aug 23, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class EmployeeRatingFacadeIT {

    private static final Logger logger = Logger.getLogger(EmployeeRatingFacadeIT.class.getName());

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
    private EmployeeRatingFacadeInterface.Local employeeRatingFacade;

    @Inject
    private ClientFacadeInterface.Local clientFacade;

    @Inject
    private EmployeeFacadeInterface.Local employeeFacade;

    @Inject
    private UserTransaction utx;

    @Test
    public void shouldCreateNewEmployeeRating() throws Exception {

        // create instance of Client entity
        Client client = new Client();

        Date dateOfBirth = new GregorianCalendar(1988, Calendar.OCTOBER, 3).getTime();

        // create instance of Employee entity
        Employee employee = new Employee("michzio@hotmail.com", "michzio", "pAs12#", "Michał", "Ziobro", dateOfBirth, Gender.male, "assistant");

        // create instance of EmployeeRating entity
        EmployeeRating employeeRating = new EmployeeRating(employee, client, (short) 9);

        utx.begin();
        clientFacade.create(client);
        employeeFacade.create(employee);
        employeeRatingFacade.create(employeeRating);
        utx.commit();

        assertTrue("There should be persisted one Employee Rating entity in database.", employeeRatingFacade.count() == 1);

        utx.begin();
        EmployeeRating foundEmployeeRating = employeeRatingFacade.find(new EmployeeRatingId(employee.getUserId(), client.getClientId()));
        assertEquals("The Employee Rating found and persisted should be the same.", foundEmployeeRating, employeeRating);

        // removing Employee Rating
        employeeRatingFacade.remove(foundEmployeeRating);
        utx.commit();

        employeeFacade.remove(employee);
        clientFacade.remove(client);

        assertTrue("There should not be any Employee Rating entity in database.", employeeRatingFacade.count() == 0);
    }

    @Test
    public void shouldFindRatingsByConditions() throws Exception {

        // TODO  Test finding employee ratings by combination of Client, Employee and Rating
    }

}
