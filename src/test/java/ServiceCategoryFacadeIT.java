import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.interfaces.ServiceCategoryFacadeInterface;
import pl.salonea.entities.ServiceCategory;

import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * ServiceCategoryFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Aug 1, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class ServiceCategoryFacadeIT {

    private static final Logger logger = Logger.getLogger(ServiceCategoryFacadeIT.class.getName());

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
    private ServiceCategoryFacadeInterface.Local categoryFacade;

    @Test
    public void shouldCreateNewServiceCategory() {

        // create instance of ServiceCategory entity
        ServiceCategory serviceCategory = new ServiceCategory("Beauty and Cosmetics");
        categoryFacade.create(serviceCategory);

        assertNotNull("Service Category ID should not be null.", serviceCategory.getCategoryId());
        assertTrue("There should be one service category entity in database.", categoryFacade.count() == 1);

        // find Service Category by ID
        ServiceCategory foundServiceCategory = categoryFacade.find(serviceCategory.getCategoryId());

        assertEquals("Persisted and found service category entity should be the same.", serviceCategory, foundServiceCategory);

        // remove found Service Category entity from database
        categoryFacade.remove(foundServiceCategory);

        assertTrue("There should not be any Service Category entity in database.", categoryFacade.count() == 0);
    }

    @Test
    public void shouldFindByKeywordAndSuperCategory() {

        // create some instances of ServiceCategory entity
        ServiceCategory beautyCategory = new ServiceCategory("Beauty and Cosmetics");
            beautyCategory.setDescription("Beauty is the quality of being pleasing, especially to look at, or someone or something that gives great pleasure, especially when looking at it.");
        ServiceCategory dentalCategory = new ServiceCategory("Dental Services");
            dentalCategory.setDescription("Dental surgery is any of a number of medical procedures that involve artificially modifying dentition; in other words, surgery of the teeth and jaw bones.");
        ServiceCategory hairCareCategory = new ServiceCategory("Hair Care");
            hairCareCategory.setDescription("Hair care is an overall term for parts of hygiene and cosmetology involving the hair on the human head.");
        ServiceCategory beautyTreatmentsCategory = new ServiceCategory("Beauty Treatments");
            beautyTreatmentsCategory.setDescription("Massage for the body is a popular beauty treatment, with various techniques offering benefits to the skin.");
        ServiceCategory medicalCategory = new ServiceCategory("Medical Services");
            medicalCategory.setDescription("Health care or healthcare is the diagnosis, treatment, and prevention of disease, illness, injury, and other physical and mental impairments in human beings.");
        ServiceCategory outpatientTestingCategory = new ServiceCategory("Outpatient Testing");
            outpatientTestingCategory.setDescription("Outpatient services are medical procedures or tests that can be done in a medical center without an overnight stay. Many procedures and tests can be done in a few hours.");

        // wiring subcategories with super-categories (both sides)
        hairCareCategory.setSuperCategory(beautyCategory);
        beautyTreatmentsCategory.setSuperCategory(beautyCategory);
        dentalCategory.setSuperCategory(medicalCategory);
        outpatientTestingCategory.setSuperCategory(medicalCategory);

        beautyCategory.getSubCategories().add(hairCareCategory);
        beautyCategory.getSubCategories().add(beautyTreatmentsCategory);
        medicalCategory.getSubCategories().add(dentalCategory);
        medicalCategory.getSubCategories().add(outpatientTestingCategory);

        // persist Service Categories in database
        categoryFacade.create(beautyCategory);
        categoryFacade.create(medicalCategory);
        categoryFacade.create(hairCareCategory);
        categoryFacade.create(beautyTreatmentsCategory);
        categoryFacade.create(dentalCategory);
        categoryFacade.create(outpatientTestingCategory);

        assertTrue("There should be six Service Category entities.", categoryFacade.count() == 6);

        // search by name
        List<ServiceCategory> serviceCategories = categoryFacade.findByName("services");
        assertTrue("There should be two categories with 'services' keyword in name.", serviceCategories.size() == 2);
        assertTrue(serviceCategories.contains(medicalCategory));
        assertTrue(serviceCategories.contains(dentalCategory));

        // search by description
        List<ServiceCategory> careCategories = categoryFacade.findByDescription("care");
        assertTrue("There should be two categories with 'care' keyword in description.", careCategories.size() == 2);
        assertTrue(careCategories.contains(hairCareCategory));
        assertTrue(careCategories.contains(medicalCategory));

        // search by keyword
        List<ServiceCategory> keyCategories = categoryFacade.findByKeyword("services");
        assertTrue("There should be three categories with 'services' keyword.", keyCategories.size() == 3);
        assertTrue(keyCategories.contains(medicalCategory));
        assertTrue(keyCategories.contains(dentalCategory));
        assertTrue(keyCategories.contains(outpatientTestingCategory));

        // find by super-category
        List<ServiceCategory> medicalCategories = categoryFacade.findBySuperCategory(medicalCategory);
        assertTrue("There should be two subcategories in medical category.", medicalCategories.size() == 2);
        assertTrue(medicalCategories.contains(dentalCategory));
        assertTrue(medicalCategories.contains(outpatientTestingCategory));

        // search by keyword in given super-category
        List<ServiceCategory> keyInBeautyCategories = categoryFacade.findBySuperCategoryAndKeyword(beautyCategory, "care");
        assertTrue("There should be only one subcategory in beauty category for given keyword.", keyInBeautyCategories.size() == 1);
        assertTrue(keyInBeautyCategories.contains(hairCareCategory));

        // remove Service Categories from database (cascade removing subcategories)
        categoryFacade.remove(medicalCategory);
        categoryFacade.remove(beautyCategory);

        assertTrue("There should not be any Service Category entity.", categoryFacade.count() == 0);

    }

    @Test
    public void shouldDeleteByNameAndSuperCategory() {

        // create some instances of ServiceCategory entity
        ServiceCategory beautyCategory = new ServiceCategory("Beauty and Cosmetics");
        ServiceCategory dentalCategory = new ServiceCategory("Dental Services");
        ServiceCategory hairCareCategory = new ServiceCategory("Hair Care");
        ServiceCategory beautyTreatmentsCategory = new ServiceCategory("Beauty Treatments");
        ServiceCategory medicalCategory = new ServiceCategory("Medical Services");
        ServiceCategory outpatientTestingCategory = new ServiceCategory("Outpatient Testing");

        // wiring subcategories with super-categories (both sides)
        hairCareCategory.setSuperCategory(beautyCategory);
        beautyTreatmentsCategory.setSuperCategory(beautyCategory);
        dentalCategory.setSuperCategory(medicalCategory);
        outpatientTestingCategory.setSuperCategory(medicalCategory);

        beautyCategory.getSubCategories().add(hairCareCategory);
        beautyCategory.getSubCategories().add(beautyTreatmentsCategory);
        medicalCategory.getSubCategories().add(dentalCategory);
        medicalCategory.getSubCategories().add(outpatientTestingCategory);

        // persist Service Categories in database
        categoryFacade.create(beautyCategory);
        categoryFacade.create(medicalCategory);
        categoryFacade.create(hairCareCategory);
        categoryFacade.create(beautyTreatmentsCategory);
        categoryFacade.create(dentalCategory);
        categoryFacade.create(outpatientTestingCategory);

        assertTrue("There should be six Service Category entities.", categoryFacade.count() == 6);

        categoryFacade.deleteByName("Dental Services");
        assertTrue("There should remain five Service Category entities.", categoryFacade.count() == 5);
        assertFalse(categoryFacade.findAll().contains(dentalCategory));

        categoryFacade.deleteByName("Outpatient Testing");
        assertTrue("There should remain four Service Category entities.", categoryFacade.count() == 4);
        assertFalse(categoryFacade.findAll().contains(outpatientTestingCategory));

        categoryFacade.deleteByName("Medical Services");
        assertTrue("There should remain three Service Category entities.", categoryFacade.count() == 3);
        assertFalse(categoryFacade.findAll().contains(medicalCategory));
        assertFalse(categoryFacade.findAll().contains(outpatientTestingCategory));

        categoryFacade.deleteBySuperCategory(beautyCategory);
        assertTrue("There should remain only one Service Category entity.", categoryFacade.count() == 1);
        assertFalse(categoryFacade.findAll().contains(hairCareCategory));
        assertFalse(categoryFacade.findAll().contains(beautyTreatmentsCategory));

        categoryFacade.deleteByName("Beauty and Cosmetics");
        assertTrue("There should not remain any Service Category entity.", categoryFacade.count() == 0);
        assertFalse(categoryFacade.findAll().contains(beautyCategory));
    }
}
