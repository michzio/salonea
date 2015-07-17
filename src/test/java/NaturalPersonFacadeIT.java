import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.stateless.NaturalPersonFacade;
import pl.salonea.entities.NaturalPerson;
import pl.salonea.enums.Gender;

import javax.inject.Inject;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * NaturalPersonFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Jul 16, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class NaturalPersonFacadeIT {

    private static final Logger logger = Logger.getLogger(NaturalPersonFacadeIT.class.getName());

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
    private NaturalPersonFacade naturalPersonFacade;

    @Test
    public void shouldCreateNaturalPerson() {

        Date dateOfBirth = new GregorianCalendar(1988, Calendar.OCTOBER, 3).getTime();

        // create instance of NaturalPerson
        NaturalPerson naturalPerson = new NaturalPerson("michzio@hotmail.com", "michzio", "pP123#", "Michał", "Ziobro", dateOfBirth , Gender.male);

        // persist the NaturalPerson in the database
        naturalPersonFacade.create(naturalPerson);

        assertNotNull("The Natural Person should got ID.", naturalPerson.getUserId());

        naturalPerson.setSkypeName("michzio");
        naturalPersonFacade.update(naturalPerson);

        naturalPersonFacade.getEntityManager().detach(naturalPerson);

        NaturalPerson foundPerson = naturalPersonFacade.find(naturalPerson.getUserId());
        assertEquals("Now, the natural person should have set 'michzio' skype name.", naturalPerson.getSkypeName(), "michzio");

        // remove the NaturalPerson
        naturalPersonFacade.remove(foundPerson);
    }

    @Test
    public void shouldFindByAge() {

        Date dateOfBirth;

        // create some instances of NaturalPerson
        dateOfBirth = new GregorianCalendar(1988, Calendar.OCTOBER, 3).getTime();
        NaturalPerson person1 = new NaturalPerson("michzio@hotmail.com", "michzio", "pP123#", "Michał", "Ziobro", dateOfBirth , Gender.male);

        dateOfBirth = new GregorianCalendar(1975, Calendar.FEBRUARY, 23).getTime();
        NaturalPerson person2 = new NaturalPerson("jan.nowak@gmail.com", "nowak_j", "pP123$", "Jan", "Nowak", dateOfBirth, Gender.male);

        dateOfBirth = new GregorianCalendar(1990, Calendar.SEPTEMBER, 10).getTime();
        NaturalPerson person3 = new NaturalPerson("wik.kwiatkowska@gmail.com", "wiki_kwiatkowska", "pP123#", "Wiktoria", "Kwiatkowska", dateOfBirth, Gender.female);

        naturalPersonFacade.create(person1);
        naturalPersonFacade.create(person2);
        naturalPersonFacade.create(person3);

        List<NaturalPerson> youngPersons = naturalPersonFacade.findYoungerThan(28);
        assertTrue("There should be 2 persons younger than 28.", youngPersons.size() == 2);
        assertTrue(youngPersons.contains(person1));
        assertTrue(youngPersons.contains(person3));

        List<NaturalPerson> oldPersons = naturalPersonFacade.findOlderThan(28);
        assertTrue("There should be 1 person older than 28.", oldPersons.size() == 1);
        assertTrue(oldPersons.contains(person2));

        // remove all instances of NaturalPerson
        naturalPersonFacade.remove(person3);
        naturalPersonFacade.remove(person2);
        naturalPersonFacade.remove(person1);
    }

    @Test
    public void shouldFindByGender() {

        Date dateOfBirth;

        // create some instances of NaturalPerson
        dateOfBirth = new GregorianCalendar(1988, Calendar.OCTOBER, 3).getTime();
        NaturalPerson person1 = new NaturalPerson("michzio@hotmail.com", "michzio", "pP123#", "Michał", "Ziobro", dateOfBirth , Gender.male);

        dateOfBirth = new GregorianCalendar(1975, Calendar.FEBRUARY, 23).getTime();
        NaturalPerson person2 = new NaturalPerson("jan.nowak@gmail.com", "nowak_j", "pP123$", "Jan", "Nowak", dateOfBirth, Gender.male);

        dateOfBirth = new GregorianCalendar(1990, Calendar.SEPTEMBER, 10).getTime();
        NaturalPerson person3 = new NaturalPerson("wik.kwiatkowska@gmail.com", "wiki_kwiatkowska", "pP123#", "Wiktoria", "Kwiatkowska", dateOfBirth, Gender.female);

        naturalPersonFacade.create(person1);
        naturalPersonFacade.create(person2);
        naturalPersonFacade.create(person3);

        List<NaturalPerson> males = naturalPersonFacade.findByGender(Gender.male);
        assertTrue("There should be 2 males.", males.size() == 2);
        assertTrue(males.contains(person1));
        assertTrue(males.contains(person2));

        List<NaturalPerson> females = naturalPersonFacade.findByGender(Gender.female);
        assertTrue("There should be 1 female.", females.size() == 1);
        assertTrue(females.contains(person3));

        // remove all instances of NaturalPerson
        naturalPersonFacade.remove(person3);
        naturalPersonFacade.remove(person2);
        naturalPersonFacade.remove(person1);
    }
}
