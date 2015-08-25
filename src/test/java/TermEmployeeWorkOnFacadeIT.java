import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.interfaces.*;
import pl.salonea.ejb.stateless.ServicePointFacade;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.*;
import pl.salonea.entities.idclass.ServicePointId;
import pl.salonea.entities.idclass.TermEmployeeId;
import pl.salonea.entities.idclass.WorkStationId;
import pl.salonea.enums.Gender;
import pl.salonea.enums.ProviderType;
import pl.salonea.enums.WorkStationType;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * TermEmployeeWorkOnFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Aug 18, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class TermEmployeeWorkOnFacadeIT {

    private static final Logger logger = Logger.getLogger(TermEmployeeWorkOnFacadeIT.class.getName());

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
    private TermEmployeeWorkOnFacadeInterface.Local employeeTermFacade;

    @Inject
    private TermFacadeInterface.Local termFacade;

    @Inject
    private WorkStationFacadeInterface.Local workStationFacade;

    @Inject
    private EmployeeFacadeInterface.Local employeeFacade;

    @Inject
    private ProviderFacadeInterface.Local providerFacade;

    @Inject
    private ServicePointFacadeInterface.Local pointFacade;

    @Inject
    private UserTransaction utx;

    @Test
    public void shouldCreateNewTermEmployeeWorkOn() throws Exception {

        // get opening and closing datetimes
        Calendar calendar = new GregorianCalendar(2016, 1, 12, 8, 00);
        Date openingTime = calendar.getTime();
        calendar.add(Calendar.HOUR_OF_DAY, 8);
        Date closingTime = calendar.getTime();

        // create instance of Term entity
        Term term = new Term(openingTime, closingTime);

        Date dateOfBirth = new GregorianCalendar(1988, Calendar.OCTOBER, 3).getTime();

        // create instance of Employee entity
        Employee employee =  new Employee("michzio@hotmail.com", "michzio", "pAs12#", "Michał", "Ziobro", dateOfBirth, Gender.male, "assistant");

        // create instance of Provider entity
        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider = new Provider("firma2@allegro.pl", "allegro2", "aAle2@", "Allegro 2 Ltd.",
                "2234567890", "2234567890", address, "Allegro Polska", ProviderType.SIMPLE);

        // create instance of service point entity
        ServicePoint servicePoint = new ServicePoint(provider, 1, address);

        // create instance of work station entity
        WorkStation workStation = new WorkStation(servicePoint, 1, WorkStationType.OTHER);

        // create ternary association between Term, Employee and WorkStation
        TermEmployeeWorkOn termEmployeeWorkOn = new TermEmployeeWorkOn(employee, term, workStation);

        utx.begin();
        providerFacade.create(provider);
        pointFacade.create(servicePoint);
        workStationFacade.create(workStation);
        employeeFacade.create(employee);
        termFacade.create(term);
        employeeTermFacade.create(termEmployeeWorkOn);
        utx.commit();

        assertTrue("There should be one TermEmployeeWorkOn entity in database.", employeeTermFacade.count() == 1);
        assertNotNull("TermEmployeeWorkOn ID can not be null.");

        TermEmployeeWorkOn foundTermEmployeeWorkOn = employeeTermFacade.find(
                new TermEmployeeId(termEmployeeWorkOn.getTerm().getTermId(), termEmployeeWorkOn.getEmployee().getUserId()) );
        assertEquals("TermEmployeeWorkOn persisted and found should be the same.", termEmployeeWorkOn, foundTermEmployeeWorkOn);

        // remove TermEmployeeWorkOn entity from database
        utx.begin();
        employee = employeeFacade.find(employee.getUserId());
        provider = providerFacade.find(provider.getUserId());
        servicePoint = pointFacade.find( new ServicePointId(provider.getUserId(), servicePoint.getServicePointNumber()) );
        workStation = workStationFacade.find(
                new WorkStationId(provider.getUserId(), servicePoint.getServicePointNumber(), workStation.getWorkStationNumber()) );
        termEmployeeWorkOn = employeeTermFacade.find( new TermEmployeeId(termEmployeeWorkOn.getTerm().getTermId(), termEmployeeWorkOn.getEmployee().getUserId()) );
        employeeTermFacade.remove(termEmployeeWorkOn);
        termFacade.remove(term);
        employeeFacade.remove(employee);
        pointFacade.remove(servicePoint);
        providerFacade.remove(provider);
        utx.commit();
        assertTrue("There should not be any TermEmployeeWorkOn entity in database.", employeeTermFacade.count() == 0);
    }

}