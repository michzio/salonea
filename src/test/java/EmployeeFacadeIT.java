import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.interfaces.*;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.*;
import pl.salonea.entities.idclass.ProviderServiceId;
import pl.salonea.enums.Gender;
import pl.salonea.enums.PriceType;
import pl.salonea.enums.ProviderType;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import java.io.File;
import java.util.*;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * EmployeeFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Aug 14, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class EmployeeFacadeIT {

    private static final Logger logger = Logger.getLogger(EmployeeFacadeIT.class.getName());

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
    private EmployeeFacadeInterface.Local employeeFacade;

    @Inject
    private ProviderFacadeInterface.Local providerFacade;

    @Inject
    private ServiceFacadeInterface.Local serviceFacade;

    @Inject
    private ProviderServiceFacadeInterface.Local providerServiceFacade;

    @Inject
    private EducationFacadeInterface.Local educationFacade;

    @Inject
    private SkillFacadeInterface.Local skillFacade;

    @Inject
    private UserTransaction utx;

    @Test
    public void shouldCreateNewEmployee() {

        Date dateOfBirth = new GregorianCalendar(1988, Calendar.OCTOBER, 3).getTime();

        // create instance of Employee
        Employee employee = new Employee("michzio@hotmail.com", "michzio", "pAs12#", "Michał", "Ziobro", dateOfBirth, Gender.male, "assistant");

        // persist employee in database
        employeeFacade.create(employee);
        assertNotNull("Employee ID should not be null", employee.getUserId());
        assertTrue("There should be one Employee entity persisted in database.", employeeFacade.count() == 1);

        Employee foundEmployee = employeeFacade.find(employee.getUserId());
        assertEquals("The found and persisted employee entity should be the same.", foundEmployee, employee);

        // remove employee from database
        employeeFacade.remove(foundEmployee);
        assertTrue("There should not be any employee persisted in database.", employeeFacade.count() == 0);
    }

   /* @Test
    public void shouldFindEmployeeByDescriptionOrJobPosition() {

        Date dateOfBirth1 = new GregorianCalendar(1988, Calendar.OCTOBER, 3).getTime();
        Date dateOfBirth2 = new GregorianCalendar(1985, Calendar.NOVEMBER, 10).getTime();
        Date dateOfBirth3 = new GregorianCalendar(1975, Calendar.APRIL, 6).getTime();

        // create some instances of Employee
        Employee employee1 = new Employee("michzio@hotmail.com", "michzio", "pAs12#", "Michał", "Ziobro", dateOfBirth1, Gender.male, "assistant");
                 employee1.setDescription("Assistant");
        Employee employee2 = new Employee("nowak.adam@gmail.com", "nowak.adam", "nOw12#", "Adam", "Nowak", dateOfBirth2, Gender.male, "consultant");
                 employee2.setDescription("Consultant");
        Employee employee3 = new Employee("maria.kwiatkowska@gmail.com", "m.kwiatkowska", "kWO29*", "Maria", "Kwiatkowska", dateOfBirth3, Gender.female, "dentist");
                 employee3.setDescription("Dentist");

        // persist employees
        employeeFacade.create(employee1);
        employeeFacade.create(employee2);
        employeeFacade.create(employee3);

        assertTrue("There should be persisted three employees in database.", employeeFacade.count() == 3);

        List<Employee> descEmployees = employeeFacade.findByDescription("dentist");
        List<Employee> consultants = employeeFacade.findByJobPosition("consultant");

        assertTrue("There should be one employee described as dentist.", descEmployees.size() == 1);
        assertTrue(descEmployees.contains(employee3));
        assertTrue("There should be one employee marked as consultant.", consultants.size() == 1);
        assertTrue(consultants.contains(employee2));

        List<String> jobPositions = new ArrayList<>();
        jobPositions.add("consultant");
        jobPositions.add("dentist");
        List<Employee> multiEmployees = employeeFacade.findByMultipleCriteria("tant", jobPositions, null, null, null, null, null, null, null, null);
        assertTrue("There should be one employee matching specified multiple criteria.", multiEmployees.size() == 1);
        assertTrue(multiEmployees.contains(employee2));

        // remove all employees
        employeeFacade.remove(employee3);
        employeeFacade.remove(employee2);
        employeeFacade.remove(employee1);

        assertTrue("There should not be any employee persisted in database.", employeeFacade.count() == 0);
    } */

    @Test
    public void shouldFindEmployeeBySkillsAndEducation() throws Exception {

        // create some instances of Employee entity
        Date dateOfBirth1 = new GregorianCalendar(1988, Calendar.OCTOBER, 3).getTime();
        Date dateOfBirth2 = new GregorianCalendar(1985, Calendar.NOVEMBER, 10).getTime();
        Date dateOfBirth3 = new GregorianCalendar(1975, Calendar.APRIL, 6).getTime();

        Employee employee1 = new Employee("michzio@hotmail.com", "michzio", "pAs12#", "Michał", "Ziobro", dateOfBirth1, Gender.male, "consultant");
                 employee1.setDescription("Consultant");
        Employee employee2 = new Employee("nowak.adam@gmail.com", "nowak.adam", "nOw12#", "Adam", "Nowak", dateOfBirth2, Gender.male, "consultant");
                 employee2.setDescription("Developer");
        Employee employee3 = new Employee("maria.kwiatkowska@gmail.com", "m.kwiatkowska", "kWO29*", "Maria", "Kwiatkowska", dateOfBirth3, Gender.female, "developer");
                 employee3.setDescription("Developer");

        // create some instances of Education entity
        Education stanfordUniversity = new Education("Stanford University", "Bachelor of Computer Science");
        Education massachusettsInstituteOfTechnology = new Education("Massachusetts Institute of Technology", "Bachelor of Computer Science");
        Education santaClaraUniversity = new Education("Santa Clara University", "Master of Computer Science");

        // create some instances of Skill entity
        Skill painting = new Skill("painting");
              painting.setDescription("The action or skill of using paint, either in a picture or as decoration.");
        Skill hairdressing = new Skill("hairdressing");
              hairdressing.setDescription("Hairdressers cut, style, colour, straighten and permanently wave hair and provide clients with hair and scalp treatments.");
        Skill hairdying = new Skill("hairdying");
        Skill cavityFilling = new Skill("cavity filling");
              cavityFilling.setDescription("Fillings are also used to repair cracked or broken teeth and teeth that have been worn down from misuse.");

        // wire up both Employee and Education entities
        employee1.getEducations().add(santaClaraUniversity);
        employee1.getEducations().add(stanfordUniversity);
        employee2.getEducations().add(massachusettsInstituteOfTechnology);
        employee3.getEducations().add(massachusettsInstituteOfTechnology);

        santaClaraUniversity.getEducatedEmployees().add(employee1);
        stanfordUniversity.getEducatedEmployees().add(employee1);
        massachusettsInstituteOfTechnology.getEducatedEmployees().add(employee2);
        massachusettsInstituteOfTechnology.getEducatedEmployees().add(employee3);

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
        utx.begin();
        educationFacade.create(stanfordUniversity);
        educationFacade.create(santaClaraUniversity);
        educationFacade.create(massachusettsInstituteOfTechnology);

        skillFacade.create(painting);
        skillFacade.create(hairdressing);
        skillFacade.create(hairdying);
        skillFacade.create(cavityFilling);

        employeeFacade.create(employee1);
        employeeFacade.create(employee2);
        employeeFacade.create(employee3);
        utx.commit();

        assertTrue("There should be three employees in database.", employeeFacade.count() == 3);
        assertTrue("There should be three skills in database.", skillFacade.count() == 4);
        assertTrue("There should be three educations in database.", educationFacade.count() == 3);

        List<Employee> mitEmployees = employeeFacade.findByEducation(massachusettsInstituteOfTechnology);
        assertTrue("There should be two MIT educated employees in database.", mitEmployees.size() == 2);
        assertTrue(mitEmployees.contains(employee2) && mitEmployees.contains(employee3));

        List<Employee> paintableEmployees = employeeFacade.findBySkill(painting);
        assertTrue("There should be two paintable employees in database.", paintableEmployees.size() == 2);
        assertTrue(paintableEmployees.contains(employee1) && paintableEmployees.contains(employee3));

        List<Skill> desiredSkills = new ArrayList<>();
                    desiredSkills.add(cavityFilling);
        List<Employee> educatedAndSkilledEmployees = employeeFacade.findByEducationAndSkills(massachusettsInstituteOfTechnology, desiredSkills);
        assertTrue("There should be only one employee with given education and skills.", educatedAndSkilledEmployees.size() == 1);
        assertTrue(educatedAndSkilledEmployees.contains(employee3));

        List<Education> desiredEducations = new ArrayList<>();
                        desiredEducations.add(massachusettsInstituteOfTechnology);
                        desiredEducations.add(stanfordUniversity);
        List<Employee> multiEmployees = employeeFacade.findByMultipleCriteria("Developer", null, desiredSkills, desiredEducations, null, null, null, null, null, null, null, null, null, null);
        assertTrue("There should be only one employee matching given criteria.", multiEmployees.size() == 1);
        assertTrue(multiEmployees.contains(employee3));

        // remove educations, skills and employees
        employee3.setSkills(null);
        employee2.setSkills(null);
        employee1.setSkills(null);
        employee3.setEducations(null);
        employee2.setEducations(null);
        employee1.setEducations(null);
        employeeFacade.remove(employee3);
        employeeFacade.remove(employee2);
        employeeFacade.remove(employee1);

        painting.setSkilledEmployees(null);
        hairdying.setSkilledEmployees(null);
        hairdressing.setSkilledEmployees(null);
        cavityFilling.setSkilledEmployees(null);
        skillFacade.remove(painting);
        skillFacade.remove(hairdying);
        skillFacade.remove(hairdressing);
        skillFacade.remove(cavityFilling);

        stanfordUniversity.setEducatedEmployees(null);
        massachusettsInstituteOfTechnology.setEducatedEmployees(null);
        santaClaraUniversity.setEducatedEmployees(null);
        educationFacade.remove(stanfordUniversity);
        educationFacade.remove(massachusettsInstituteOfTechnology);
        educationFacade.remove(santaClaraUniversity);

        assertTrue("There should not be any employee in database.", employeeFacade.count() == 0);
        assertTrue("There should not be any skill in database.", skillFacade.count() == 0);
        assertTrue("There should not be any education in database.", educationFacade.count() == 0);
    }

   /* @Test
    public void shouldFindEmployeeByProvidedServices() throws Exception {

        // create some instances of Provider entity
        Address address1 =  new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider1 = new Provider("firma@allegro.pl", "allegro", "aAle2@_", "Allegro Ltd.",
                "2234567890", "2234567890", address1, "Allegro Polska", ProviderType.SIMPLE);

        Address address2 = new Address("Wrocławska", "45", "10-140", "Szczecin", "Zachodnio Pomorskie", "Poland");
        Provider provider2 = new Provider("firma@tieto.pl", "tieto", "tIe%13?", "Tieto Sp. z o.o.",
                "6593878688", "6510029930", address2, "Tieto Poland", ProviderType.SIMPLE);

        Address address3 = new Address("Kijowska", "09", "20-160", "Lublin", "Lubelskie", "Poland");
        Provider provider3 = new Provider("firma@fryzjerka.pl", "fryzjerka_pl", "fRyZU123?", "Fryzjerka Sp. z o.o.",
                "1910020030", "1930040050", address3, "Fryzjerka Polska", ProviderType.SIMPLE);

        Address address4 = new Address("Pomorska", "12", "99-200", "Gdańsk", "Pomorze", "Poland");
        Provider provider4 = new Provider("kontakt@przeprowadzki24.pl", "przeprowadzki24", "prZEP_M24%", "Przeprowadzki24 Sp. z o.o.",
                "4530040050", "4530040050", address4, "Przeprowadzki24 Pomorze", ProviderType.SIMPLE);

        // create some instances of Service entity
        Service haircut = new Service("Haircut");
        Service fillingCavities = new Service("Filling Cavities");
        Service hairDyeing = new Service("Hair Dyeing");
        Service removals = new Service("Removals");

        // create instances of ProviderService entity
        ProviderService provider1Filling = new ProviderService(provider1, fillingCavities, 1800000L); // 30 min
                        provider1Filling.setPrice(100.0);
                        provider1Filling.setPriceType(PriceType.PER_SERVICE);
                        provider1Filling.setDescription("A dental restoration or dental filling is a dental restorative material used to restore the function, integrity and morphology of missing tooth structure.");
        ProviderService provider1Removals = new ProviderService(provider1, removals, 60*60*1000L); // 1h
                        provider1Removals.setPrice(60.0);
                        provider1Removals.setPriceType(PriceType.PER_HOUR);
                        provider1Removals.setDescription("A moving company, van line is a company that helps people and businesses move their goods from one place to another.");
        ProviderService provider2Filling = new ProviderService(provider2, fillingCavities, 1800000L); // 30 min
                        provider2Filling.setPrice(150.0);
                        provider2Filling.setPriceType(PriceType.PER_SERVICE);
                        provider2Filling.setDiscount((short) 10); // [%]
                        provider2Filling.setDescription("A dental restoration or dental filling is a dental restorative material used to restore the function, integrity and morphology of missing tooth structure.");
        ProviderService provider3Haircut = new ProviderService(provider3, haircut, 1800000L); // 30 min
                        provider3Haircut.setPrice(50.0);
                        provider3Haircut.setPriceType(PriceType.PER_SERVICE);
                        provider3Haircut.setDescription("A hairstyle, hairdo, or haircut refers to the styling of hair, usually on the human scalp.");
        ProviderService provider3Dyeing = new ProviderService(provider3, hairDyeing, 1800000L); // 30 min
                        provider3Dyeing.setPrice(80.0);
                        provider3Dyeing.setPriceType(PriceType.PER_SERVICE);
                        provider3Dyeing.setDiscount((short) 50); // [%]
                        provider3Dyeing.setDescription("Hair coloring is the practice of changing the color of hair. ");
        ProviderService provider4Removals = new ProviderService(provider4, removals, 1800000L); // 30 min
                        provider4Removals.setPrice(75.0);
                        provider4Removals.setPriceType(PriceType.PER_SERVICE);
                        provider4Removals.setDescription("A moving company, removalist is a company that helps people and businesses move their goods from one place to another.");

        // create some instances of Employee
        Date dateOfBirth1 = new GregorianCalendar(1988, Calendar.OCTOBER, 3).getTime();
        Date dateOfBirth2 = new GregorianCalendar(1985, Calendar.NOVEMBER, 10).getTime();
        Date dateOfBirth3 = new GregorianCalendar(1975, Calendar.APRIL, 6).getTime();

        Employee employee1 = new Employee("michzio@hotmail.com", "michzio", "pAs12#", "Michał", "Ziobro", dateOfBirth1, Gender.male, "assistant");
                 employee1.setDescription("Assistant");
        Employee employee2 = new Employee("maria.kwiatkowska@gmail.com", "m.kwiatkowska", "kWO29*", "Maria", "Kwiatkowska", dateOfBirth2, Gender.female, "dentist");
                 employee2.setDescription("Dentist");
        Employee employee3 = new Employee("wiktoria.nowak@gmail.com", "wiki.nowak", "wIkI23^", "Wiktoria", "Nowak", dateOfBirth3, Gender.female, "hairdresser");
                 employee3.setDescription("Hairdresser");

        // wire Employees with Provider Services
        employee1.getSuppliedServices().add(provider1Removals);
        employee1.getSuppliedServices().add(provider4Removals);
        employee2.getSuppliedServices().add(provider1Filling);
        employee2.getSuppliedServices().add(provider2Filling);
        employee3.getSuppliedServices().add(provider3Dyeing);
        employee3.getSuppliedServices().add(provider3Haircut);

        provider1Removals.getSupplyingEmployees().add(employee1);
        provider1Filling.getSupplyingEmployees().add(employee2);
        provider2Filling.getSupplyingEmployees().add(employee2);
        provider3Dyeing.getSupplyingEmployees().add(employee3);
        provider3Haircut.getSupplyingEmployees().add(employee3);
        provider4Removals.getSupplyingEmployees().add(employee1);

        utx.begin();
        providerFacade.create(provider1);
        providerFacade.create(provider2);
        providerFacade.create(provider3);
        providerFacade.create(provider4);

        serviceFacade.create(haircut);
        serviceFacade.create(fillingCavities);
        serviceFacade.create(hairDyeing);
        serviceFacade.create(removals);

        providerServiceFacade.create(provider1Filling);
        providerServiceFacade.create(provider1Removals);
        providerServiceFacade.create(provider2Filling);
        providerServiceFacade.create(provider3Haircut);
        providerServiceFacade.create(provider3Dyeing);
        providerServiceFacade.create(provider4Removals);

        employeeFacade.create(employee1);
        employeeFacade.create(employee2);
        employeeFacade.create(employee3);
        utx.commit();

        assertTrue(serviceFacade.count() == 4);
        assertTrue(providerServiceFacade.count() == 6);
        assertTrue(employeeFacade.count() == 3);

        List<Employee> hairdressers = employeeFacade.findByService(haircut);
        assertTrue("There should be one hairdresser in database.", hairdressers.size() == 1);
        assertTrue(hairdressers.contains(employee3));

        List<Employee> hairdressers2 = employeeFacade.findByService(hairDyeing);
        assertEquals("The same hairdressers should provide both services.", hairdressers, hairdressers2);

        utx.begin();
        provider1Filling = providerServiceFacade.find(new ProviderServiceId(provider1Filling.getProvider().getUserId(), provider1Filling.getService().getServiceId()));
        provider2Filling = providerServiceFacade.find(new ProviderServiceId(provider2Filling.getProvider().getUserId(), provider2Filling.getService().getServiceId()));
        List<Employee> dentists = employeeFacade.findByProviderService(provider2Filling);
        List<Employee> dentists2 = employeeFacade.findByProviderService(provider1Filling);
        utx.commit();

        assertTrue("There should be one dentist in database.", dentists.size() == 1);
        assertTrue(dentists.contains(employee2));
        assertEquals("The same dentists should provide both services.", dentists, dentists2);

        assertTrue(providerServiceFacade.deleteForProvider(provider1) == 2);
        assertTrue(providerServiceFacade.deleteForProvider(provider2) == 1);
        assertTrue(providerServiceFacade.deleteForProvider(provider3) == 2);
        assertTrue(providerServiceFacade.deleteForProvider(provider4) == 1);

        assertTrue(providerServiceFacade.count() == 0);

        employee3.setSuppliedServices(null);
        employee2.setSuppliedServices(null);
        employee1.setSuppliedServices(null);
        employeeFacade.remove(employee3);
        employeeFacade.remove(employee2);
        employeeFacade.remove(employee1);

        assertTrue(employeeFacade.count() == 0);

        providerFacade.remove(provider4);
        providerFacade.remove(provider3);
        providerFacade.remove(provider2);
        providerFacade.remove(provider1);

        assertTrue(providerFacade.count() == 0);

        serviceFacade.remove(removals);
        serviceFacade.remove(hairDyeing);
        serviceFacade.remove(fillingCavities);
        serviceFacade.remove(haircut);

        assertTrue(serviceFacade.count() == 0);
    }
    */

    @Test
    public void shouldFindEmployeeByServicePointAndWorkStation() {
        // TODO Implement finding employee by service point and work station
    }

    @Test
    public void shouldFindEmployeeByTerm() {
        // TODO Implement finding employee by terms
    }
}
