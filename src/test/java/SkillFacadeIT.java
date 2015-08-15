import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.interfaces.EmployeeFacadeInterface;
import pl.salonea.ejb.interfaces.SkillFacadeInterface;
import pl.salonea.entities.Employee;
import pl.salonea.entities.Skill;
import pl.salonea.enums.Gender;

import javax.inject.Inject;
import java.io.File;
import java.util.*;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * SkillFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Aug 15, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class SkillFacadeIT {

    private static final Logger logger = Logger.getLogger(SkillFacadeIT.class.getName());

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
    private SkillFacadeInterface.Local skillFacade;

    @Inject
    private EmployeeFacadeInterface.Local employeeFacade;

    @Test
    public void shouldCreateNewSkill() {

        // create instance of Skill entity
        Skill skill = new Skill("painting");

        // persist skill in database
        skillFacade.create(skill);

        assertNotNull("Skill ID should not be null.", skill.getSkillId());
        assertTrue("There should be persisted one skill in database.", skillFacade.count() == 1);

        // finding skill by id
        Skill foundSkill = skillFacade.find(skill.getSkillId());
        assertEquals("The found and persisted Skill entity should be the same.", foundSkill, skill);

        // removing skill from database
        skillFacade.remove(skill);
        assertTrue("There should not be any skill persisted in database.", skillFacade.count() == 0);
    }

    @Test
    public void shouldFindSkillByKeyword() {

        // create some instances of Skill entity
        Skill painting = new Skill("painting");
              painting.setDescription("The action or skill of using paint, either in a picture or as decoration.");
        Skill hairdressing = new Skill("hairdressing");
              hairdressing.setDescription("Hairdressers cut, style, colour, straighten and permanently wave hair and provide clients with hair and scalp treatments.");
        Skill hairdying = new Skill("hairdying");
        Skill cavityFilling = new Skill("cavity filling");
              cavityFilling.setDescription("Fillings are also used to repair cracked or broken teeth and teeth that have been worn down from misuse.");

        // persist all skills in database
        skillFacade.create(painting);
        skillFacade.create(hairdressing);
        skillFacade.create(hairdying);
        skillFacade.create(cavityFilling);

        assertTrue("There should be persisted four skills in database.", skillFacade.count() == 4);

        List<Skill> hairSkills = skillFacade.findByName("hair");
        assertTrue("There should be two hair related skills in database.", hairSkills.size() == 2);
        assertTrue(hairSkills.contains(hairdressing) && hairSkills.contains(hairdying));

        List<Skill> descSkills = skillFacade.findByDescription("or");
        assertTrue("There should be two skills containing 'or' in description.", descSkills.size() == 2);
        assertTrue(descSkills.contains(painting) && descSkills.contains(cavityFilling));

        List<Skill> keySkills = skillFacade.findByKeyword("re");
        assertTrue("There should be two skills containing 're' keyword.", keySkills.size() == 3);
        assertTrue(keySkills.contains(hairdressing) && keySkills.contains(cavityFilling) && keySkills.contains(painting));

        assertTrue("There should be deleted one skill from database by name.", skillFacade.deleteByName("hairdying") == 1);
        assertTrue("There should remain only three skills in database.", skillFacade.count() == 3);
        // removing all skills from database
        skillFacade.remove(painting);
        skillFacade.remove(hairdressing);
        skillFacade.remove(cavityFilling);

        assertTrue("There should not be any skills in database.", skillFacade.count() == 0);
    }

    @Test
    public void shouldFindSkillByEmployee() {

        // create some instances of Employee entity
        Date dateOfBirth1 = new GregorianCalendar(1988, Calendar.OCTOBER, 3).getTime();
        Date dateOfBirth2 = new GregorianCalendar(1985, Calendar.NOVEMBER, 10).getTime();
        Date dateOfBirth3 = new GregorianCalendar(1975, Calendar.APRIL, 6).getTime();

        Employee employee1 = new Employee("michzio@hotmail.com", "michzio", "pAs12#", "Michał", "Ziobro", dateOfBirth1, Gender.male, "assistant");
                 employee1.setDescription("Assistant");
        Employee employee2 = new Employee("nowak.adam@gmail.com", "nowak.adam", "nOw12#", "Adam", "Nowak", dateOfBirth2, Gender.male, "consultant");
                 employee2.setDescription("Consultant");
        Employee employee3 = new Employee("maria.kwiatkowska@gmail.com", "m.kwiatkowska", "kWO29*", "Maria", "Kwiatkowska", dateOfBirth3, Gender.female, "dentist");
                 employee3.setDescription("Dentist");

        // create some instances of Skill entity
        Skill painting = new Skill("painting");
              painting.setDescription("The action or skill of using paint, either in a picture or as decoration.");
        Skill hairdressing = new Skill("hairdressing");
              hairdressing.setDescription("Hairdressers cut, style, colour, straighten and permanently wave hair and provide clients with hair and scalp treatments.");
        Skill hairdying = new Skill("hairdying");
        Skill cavityFilling = new Skill("cavity filling");
              cavityFilling.setDescription("Fillings are also used to repair cracked or broken teeth and teeth that have been worn down from misuse.");

        // wire up employees and skills
        employee1.getSkills().add(painting);
        employee2.getSkills().add(hairdressing);
        employee2.getSkills().add(hairdying);
        employee3.getSkills().add(cavityFilling);
        employee3.getSkills().add(painting);

        painting.getSkilledEmployees().add(employee1);
        painting.getSkilledEmployees().add(employee3);
        hairdressing.getSkilledEmployees().add(employee2);
        hairdying.getSkilledEmployees().add(employee2);
        cavityFilling.getSkilledEmployees().add(employee3);

        // persist employees and skills in database
        skillFacade.create(painting);
        skillFacade.create(hairdressing);
        skillFacade.create(hairdying);
        skillFacade.create(cavityFilling);

        employeeFacade.create(employee1);
        employeeFacade.create(employee2);
        employeeFacade.create(employee3);

        assertTrue("There should be persisted three employees in database.", employeeFacade.count() == 3);
        assertTrue("There should be persisted four skills in database.", skillFacade.count() == 4);

        List<Skill> hairdresserSkills = skillFacade.findByEmployee(employee2);
        assertTrue("There should be two hairdresser skills persisted in database.", hairdresserSkills.size() == 2);
        assertTrue(hairdresserSkills.contains(hairdressing) && hairdresserSkills.contains(hairdying));

        List<Skill> employeeSkills = skillFacade.findByEmployeeAndKeyword(employee3, "teeth");
        assertTrue("There should be only one skill for given employee and keyword.", employeeSkills.size() == 1);
        assertTrue(employeeSkills.contains(cavityFilling));

        assertTrue("It should delete two skills for given employee.", skillFacade.deleteByEmployee(employee3) == 2);
        // assertTrue(skillFacade.deleteByName("cavity filling") == 1);
        // assertTrue(skillFacade.deleteByName("painting") == 1);

        // remove employees and skills
        employee3.setSkills(null);
        employee2.setSkills(null);
        employee1.setSkills(null);
        employeeFacade.remove(employee3);
        employeeFacade.remove(employee2);
        employeeFacade.remove(employee1);

        hairdying.setSkilledEmployees(null);
        hairdressing.setSkilledEmployees(null);

        skillFacade.remove(hairdying);
        skillFacade.remove(hairdressing);

        assertTrue("There should not be any employee in database.", employeeFacade.count() == 0);
        assertTrue("There should not be any skill in database.", skillFacade.count() == 0);

    }

}
