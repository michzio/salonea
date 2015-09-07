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
import pl.salonea.entities.Employee;
import pl.salonea.enums.Gender;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
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

    @Inject
    private UserTransaction utx;

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

       // create some instances of Education entity
       Education stanfordUniversity = new Education("Stanford University", "Bachelor of Computer Science");
       Education massachusettsInstituteOfTechnology = new Education("Massachusetts Institute of Technology", "Bachelor of Computer Science");
       Education santaClaraUniversity = new Education("Santa Clara University", "Master of Computer Science");

       // persist all Education entities
       educationFacade.create(stanfordUniversity);
       educationFacade.create(massachusettsInstituteOfTechnology);
       educationFacade.create(santaClaraUniversity);

        assertTrue("There should be three Education entities in database.", educationFacade.count() == 3);

       List<Education> bachelors =  educationFacade.findByDegree("Bachelor");
       assertTrue("There should be two educations with bachelor degree in database.", bachelors.size() == 2);
       assertTrue(bachelors.contains(stanfordUniversity) && bachelors.contains(massachusettsInstituteOfTechnology));

       List<Education> masters = educationFacade.findByDegree("Master");
       assertTrue("There should be only one education with master degree in database.", masters.size() == 1);
       assertTrue(masters.contains(santaClaraUniversity));

       List<Education> universities = educationFacade.findBySchool("University");
       assertTrue("There should be two university educations in database.", universities.size() == 2);
       assertTrue(universities.contains(stanfordUniversity) && universities.contains(santaClaraUniversity));

       List<Education> technologyInstitutes = educationFacade.findBySchool("Institute of Technology");
       assertTrue("There should be only one technology institute education in database.", technologyInstitutes.size() == 1);
       assertTrue(technologyInstitutes.contains(massachusettsInstituteOfTechnology));

       List<Education> universityBachelors = educationFacade.findByDegreeAndSchool("Bachelor", "University");
       assertTrue("There should be only one university bachelor education in database.", universityBachelors.size() == 1);
       assertTrue(universityBachelors.contains(stanfordUniversity));

       List<Education> keyEducations = educationFacade.findByKeyword("ma");
       assertTrue("There should be two educations with keyword 'ma' in degree or school.", keyEducations.size() == 2);
       assertTrue(keyEducations.contains(massachusettsInstituteOfTechnology) && keyEducations.contains(santaClaraUniversity));

       // remove all Education entities
       assertTrue(educationFacade.deleteByDegreeAndSchool("Master of Computer Science", "Santa Clara University") == 1);
       assertTrue("There should remain only two Education entities in database.", educationFacade.count() == 2);

       assertTrue(educationFacade.deleteBySchool("Stanford University") == 1);
       assertTrue("There should remain only one Education entity in database.", educationFacade.count() == 1);

       assertTrue(educationFacade.deleteByDegree("Bachelor of Computer Science") == 1);
       assertTrue("There should not remain any Education entity in database.", educationFacade.count() == 0);

    }

    @Test
    public void shouldFindEducationByEmployee() throws Exception {

        // create some instances of Employee entity
        Date dateOfBirth1 = new GregorianCalendar(1988, Calendar.OCTOBER, 3).getTime();
        Date dateOfBirth2 = new GregorianCalendar(1985, Calendar.NOVEMBER, 10).getTime();
        Date dateOfBirth3 = new GregorianCalendar(1975, Calendar.APRIL, 6).getTime();

        Employee employee1 = new Employee("michzio@hotmail.com", "michzio", "pAs12#", "Michał", "Ziobro", dateOfBirth1, Gender.male, "consultant");
                 employee1.setDescription("Consultant");
        Employee employee2 = new Employee("nowak.adam@gmail.com", "nowak.adam", "nOw12#", "Adam", "Nowak", dateOfBirth2, Gender.male, "consultant");
                 employee2.setDescription("Consultant");
        Employee employee3 = new Employee("maria.kwiatkowska@gmail.com", "m.kwiatkowska", "kWO29*", "Maria", "Kwiatkowska", dateOfBirth3, Gender.female, "developer");
                 employee3.setDescription("Developer");

        // create some instances of Education entity
        Education stanfordUniversity = new Education("Stanford University", "Bachelor of Computer Science");
        Education massachusettsInstituteOfTechnology = new Education("Massachusetts Institute of Technology", "Bachelor of Computer Science");
        Education santaClaraUniversity = new Education("Santa Clara University", "Master of Computer Science");

        // wire up both Employee and Education entities
        santaClaraUniversity.getEducatedEmployees().add(employee1);
        stanfordUniversity.getEducatedEmployees().add(employee1);
        massachusettsInstituteOfTechnology.getEducatedEmployees().add(employee2);
        massachusettsInstituteOfTechnology.getEducatedEmployees().add(employee3);

        employee1.getEducations().add(santaClaraUniversity);
        employee1.getEducations().add(stanfordUniversity);
        employee2.getEducations().add(massachusettsInstituteOfTechnology);
        employee3.getEducations().add(massachusettsInstituteOfTechnology);
        // persist employees and educations in database
        utx.begin();
        educationFacade.create(stanfordUniversity);
        educationFacade.create(santaClaraUniversity);
        educationFacade.create(massachusettsInstituteOfTechnology);

        employeeFacade.create(employee1);
        employeeFacade.create(employee2);
        employeeFacade.create(employee3);
        utx.commit();

        assertTrue("There should be three Employee entities in database.", employeeFacade.count() == 3);
        assertTrue("There should be three Education entities in database.", educationFacade.count() == 3);

        List<Education> firstEmployeeEdu = educationFacade.findByEmployee(employee1);
        assertTrue("There should be two Educations for first Employee.", firstEmployeeEdu.size() == 2);
        assertTrue(firstEmployeeEdu.contains(stanfordUniversity) && firstEmployeeEdu.contains(santaClaraUniversity));

        List<Education> employeeEdu = educationFacade.findByEmployeeAndKeyword(employee1, "Santa Clara");
        assertTrue("There should be one Education for first Employee and 'Santa Clara' keyword.", employeeEdu.size() == 1);
        assertTrue(employeeEdu.contains(santaClaraUniversity));

        // removing Education entities
        Integer deletedEducation = educationFacade.deleteByEmployee(employee1);
        assertTrue(deletedEducation == 2);
        assertTrue("There should remain only one Education entity in database.", educationFacade.count() == 1);

        assertTrue(educationFacade.deleteByEmployee(employee2) == 1);
        assertTrue("There should not remain any Education entity in database.", educationFacade.count() == 0);

        employee3.setEducations(null);
        employee2.setEducations(null);
        employee1.setEducations(null);
        employeeFacade.remove(employee3);
        employeeFacade.remove(employee2);
        employeeFacade.remove(employee1);
        assertTrue("There should not be any Employee entity in database.", employeeFacade.count() == 0);
    }
}