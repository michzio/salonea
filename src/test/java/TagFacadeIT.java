import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.interfaces.ServicePointPhotoFacadeInterface;
import pl.salonea.ejb.interfaces.TagFacadeInterface;
import pl.salonea.entities.Tag;

import javax.inject.Inject;
import java.io.File;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * TagFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Aug 7, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class TagFacadeIT {

    private static final Logger logger = Logger.getLogger(TagFacadeIT.class.getName());

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
    private ServicePointPhotoFacadeInterface.Local photoFacade;

    @Inject
    private TagFacadeInterface.Local tagFacade;

    @Test
    public void shouldCreateNewTag() {

        // create instance of Tag entity
        Tag tag = new Tag("tag name");

        // persist Tag entity in database
        tagFacade.create(tag);

        assertNotNull("Tag ID should not be null.", tag.getTagId());
        assertTrue("There should be one Tag persisted in database.", tagFacade.count() == 1);

        assertEquals("Both persisted and found Tag entity should be the same.", tagFacade.find(tag.getTagId()), tag);

        tagFacade.remove(tag);
        assertTrue("There should not be any Tag entity in database.", tagFacade.count() == 0);
    }


}
