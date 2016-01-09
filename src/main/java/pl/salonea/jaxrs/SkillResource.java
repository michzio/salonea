package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.EmployeeFacade;
import pl.salonea.ejb.stateless.SkillFacade;
import pl.salonea.entities.Employee;
import pl.salonea.entities.Skill;
import pl.salonea.jaxrs.bean_params.EmployeeBeanParam;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.bean_params.SkillBeanParam;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.SkillWrapper;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.exceptions.BadRequestException;

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
     * Method returns all Skill resources
     * They can be additionally filtered or paginated by @QueryParams
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getSkills( @BeanParam SkillBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Skills by executing SkillResource.getSkills() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<Skill> skills = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            // get all skills filtered by given query params

            if( RESTToolkit.isSet(params.getKeywords()) ) {
                if( RESTToolkit.isSet(params.getSkillNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                    throw new BadRequestException("Query params cannot include keywords and skillNames or descriptions at the same time.");

                // find only by keywords
                skills = new ResourceList<>(
                        skillFacade.findByMultipleCriteria(params.getKeywords(), params.getEmployees(), params.getOffset(), params.getLimit())
                );

            } else {
                // find by skillNames, descriptions
                skills = new ResourceList<>(
                        skillFacade.findByMultipleCriteria(params.getSkillNames(), params.getDescriptions(), params.getEmployees(), params.getOffset(), params.getLimit())
                );
            }
        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all skills without filtering (eventually paginated)
            skills = new ResourceList<>( skillFacade.findAll(params.getOffset(), params.getLimit()) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        SkillResource.populateWithHATEOASLinks(skills, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(skills).build();
    }

    @GET
    @Path("/eagerly")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getSkillsEagerly( @BeanParam SkillBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Skills eagerly by executing SkillResource.getSkillsEagerly() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<SkillWrapper> skills = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            // get all skills eagerly filtered by query params

            if( RESTToolkit.isSet(params.getKeywords()) ) {
                if( RESTToolkit.isSet(params.getSkillNames()) || RESTToolkit.isSet(params.getDescriptions()) )
                    throw new BadRequestException("Query params cannot include keywords and skillNames or descriptions at the same time.");

                // find only by keywords
                skills = new ResourceList<>(
                        SkillWrapper.wrap(
                                skillFacade.findByMultipleCriteriaEagerly(params.getKeywords(), params.getEmployees(), params.getOffset(), params.getLimit())
                        )
                );
            } else {
                // find by skillNames, descriptions
                skills = new ResourceList<>(
                        SkillWrapper.wrap(
                                skillFacade.findByMultipleCriteriaEagerly(params.getSkillNames(), params.getDescriptions(), params.getEmployees(), params.getOffset(), params.getLimit())
                        )
                );
            }
        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all skills eagerly without filtering (eventually paginated)
            skills = new ResourceList<>( SkillWrapper.wrap(skillFacade.findAllEagerly(params.getOffset(), params.getLimit())) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        SkillResource.populateWithHATEOASLinks(skills, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(skills).build();
    }



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

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(skills, uriInfo, offset, limit);

        try {
            // count resources hypermedia link
            Method countMethod = SkillResource.class.getMethod("countSkills", GenericBeanParam.class);
            skills.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(SkillResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            skills.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(SkillResource.class).build()).rel("skills").build() );

            // get all resources eagerly hypermedia link
            Method skillsEagerlyMethod = SkillResource.class.getMethod("getSkillsEagerly", SkillBeanParam.class);
            skills.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(SkillResource.class)
                    .path(skillsEagerlyMethod)
                    .build()).rel("skills-eagerly").build() );

            // get subset of resources hypermedia links
            // TODO

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for (Object object : skills.getResources()) {
            if(object instanceof Skill) {
                SkillResource.populateWithHATEOASLinks( (Skill) object, uriInfo );
            } else if(object instanceof SkillWrapper) {
                SkillResource.populateWithHATEOASLinks( (SkillWrapper) object, uriInfo );
            }
        }
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

        // self link with pattern: http://localhost:port/app/rest/{resources}/{id}
        skill.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                    .path(SkillResource.class)
                                                    .path(skill.getSkillId().toString())
                                                    .build())
                                    .rel("self").build());

        // collection link with pattern: http://localhost:port/app/rest/{resources}
        skill.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                    .path(SkillResource.class)
                                                    .build())
                                    .rel("skills").build() );

        try {
            // self eagerly link with pattern: http://localhost:port/app/rest/{resources}/{id}/eagerly
            Method skillEagerlyMethod = SkillResource.class.getMethod("getSkillEagerly", Integer.class, GenericBeanParam.class);
            skill.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(SkillResource.class)
                    .path(skillEagerlyMethod)
                    .resolveTemplate("skillId", skill.getSkillId().toString())
                    .build())
                    .rel("skill-eagerly").build() );

            // associated collections links with pattern: http://localhost:port/app/rest/{resources}/{id}/{relationship}

            /**
             * Employees associated with current Skill resource
             */

            // TODO

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
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
