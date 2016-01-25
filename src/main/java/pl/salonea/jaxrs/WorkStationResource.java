package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.EmployeeFacade;
import pl.salonea.ejb.stateless.WorkStationFacade;
import pl.salonea.entities.WorkStation;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.bean_params.WorkStationBeanParam;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.WorkStationWrapper;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * Created by michzio on 24/01/2016.
 */
@Path("/work-stations")
public class WorkStationResource {

    private static final Logger logger = Logger.getLogger(WorkStationResource.class.getName());

    @Inject
    private UserTransaction utx;

    @Inject
    private WorkStationFacade workStationFacade;
    @Inject
    private EmployeeFacade employeeFacade;

    /**
     * Alternative methods to access Work Station resource
     */

    // TODO

    /**
     * Method returns all Work Station entities.
     * They can be additionally filtered and paginated by @QueryParams.
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getWorkStations( @BeanParam WorkStationBeanParam params ) throws ForbiddenException {

        // TODO
        return null;
    }

    /**
     * Additional methods returning a subset of resources based on given criteria
     * you can achieve similar results by applying @QueryParams to generic method
     * returning all resources in order to filter and limit them.
     */

    /**
     * Method returns number of Work Station entities in database
     */
    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countWorkStations( @BeanParam GenericBeanParam params ) throws ForbiddenException {

        return null;
    }

    // TODO other methods

    /**
     * related subresources (through relationships)
     */
    @Path("/{providerId: \\d+}+{servicePointNumber: \\d+}+{workStationNumber: \\d+}/employees")
    public EmployeeResource getEmployeeResource() { return new EmployeeResource(); }

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList workStations, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(workStations, uriInfo, offset, limit);

        try {
            // count resources hypermedia link
            Method countMethod = WorkStationResource.class.getMethod("countWorkStations", GenericBeanParam.class);
            workStations.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(WorkStationResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            workStations.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(WorkStationResource.class).build()).rel("work-stations").build() );

            // get all resources eagerly hypermedia link
            Method workStationsEagerlyMethod = null;
            // TODO

            // get subset of resources hypermedia links


        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for(Object object : workStations.getResources()) {
            if(object instanceof WorkStation) {
                WorkStationResource.populateWithHATEOASLinks( (WorkStation) object, uriInfo);
            } else if(object instanceof WorkStationWrapper) {
                WorkStationResource.populateWithHATEOASLinks( (WorkStationWrapper) object, uriInfo);
            }
        }
    }

    /**
     * This method enables to populate each individual resource wrapper with hypermedia links
     */
    public static void populateWithHATEOASLinks(WorkStationWrapper workStationWrapper, UriInfo uriInfo) {

        WorkStationResource.populateWithHATEOASLinks(workStationWrapper.getWorkStation(), uriInfo);

        // TODO

    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(WorkStation workStation, UriInfo uriInfo) {

        // self link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/{sub-id}/{sub-subresources}/{sub-sub-id}

        // self eagerly link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/{sub-id}/{sub-subresources}/{sub-sub-id}/eagerly

        // collection link with pattern: http://localhost:port/app/rest/{resources}
        workStation.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder()
                                                        .path(WorkStationResource.class)
                                                        .build())
                                        .rel("work-stations").build() );

        // TODO
    }

}
