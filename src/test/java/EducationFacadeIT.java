import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.interfaces.EducationFacadeInterface;
import pl.salonea.ejb.interfaces.EmployeeFacadeInterface;
import pl.salonea.entities.Education;

import javax.inject.Inject;
import java.io.File;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * EducationFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Aug 15, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class EducationFacadeIT {

    private static final Logger logger = Logger.getLogger(EducationFacadeIT.class.getName());

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
    private EducationFacadeInterface.Local educationFacade;

    @Inject
    private EmployeeFacadeInterface.Local employeeFacade;

    @Test
    public void shouldCreateNewEducation() {

        // create instance of Education entity
        Education education = new Education("Stanford University", "Bachelor of Computer Science");

        // persist education in database
        educationFacade.create(education);

        assertTrue("There should be persisted one Education entity in database.", educationFacade.count() == 1);
        assertNotNull("Education ID should not be null.", education.getEducationId());

        Education foundEducation = educationFacade.find(education.getEducationId());
        assertEquals("The found and persisted Education entity should be the same.", foundEducation, education);

        // remove education from database
        educationFacade.remove(education);

        assertTrue("There should not be any Education entity in database.", educationFacade.count() == 0);
    }

    @Test
    public void shouldFindEducationByDegreeAndSchool() {

        // TODO
    }

    @Test
    public void shouldFindEducationByEmployee() {
        // TODO
    }
}

