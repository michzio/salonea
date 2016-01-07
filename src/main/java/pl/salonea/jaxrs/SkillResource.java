package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.EmployeeFacade;
import pl.salonea.ejb.stateless.SkillFacade;
import pl.salonea.entities.Employee;
import pl.salonea.entities.Skill;
import pl.salonea.jaxrs.bean_params.EmployeeBeanParam;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.wrappers.SkillWrapper;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.logging.Logger;

/**
 * Created by michzio on 07/01/2016.
 */
@Path("/skills")
public class SkillResource {

    private static final Logger logger = Logger.getLogger(SkillResource.class.getName());

    @Inject
    private SkillFacade skillFacade;
    @Inject
    private EmployeeFacade employeeFacade;

    /**
     * related subresources (through relationships)
     */

    @Path("/{skillId : \\d+}/employees")
    public EmployeeResource getEmployeeResource() {
        return new EmployeeResource();
    }

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList skills, UriInfo uriInfo, Integer offset, Integer limit) {
        // TODO
    }

    /**
     * This method enables to populate each individual resource wrapper with hypermedia links
     */
    public static void populateWithHATEOASLinks(SkillWrapper skillWrapper, UriInfo uriInfo) {

        SkillResource.populateWithHATEOASLinks(skillWrapper.getSkill(), uriInfo);

        for(Employee employee : skillWrapper.getSkilledEmployees())
            pl.salonea.jaxrs.EmployeeResource.populateWithHATEOASLinks(employee, uriInfo);

    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(Skill skill, UriInfo uriInfo) {

        // TODO
    }

    public class EmployeeResource {

        public EmployeeResource() { }

        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response getSkilledEmployees( @PathParam("skillId") Integer skillId,
                                             @BeanParam EmployeeBeanParam params ) throws ForbiddenException, NotFoundException {

            // TODO
            return null;
        }
    }

}
