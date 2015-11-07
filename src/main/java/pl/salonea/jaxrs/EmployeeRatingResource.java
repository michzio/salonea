package pl.salonea.jaxrs;

import pl.salonea.entities.EmployeeRating;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;
import pl.salonea.jaxrs.utils.hateoas.Link;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.ws.Response;
import java.lang.reflect.Method;

/**
 * Created by michzio on 07/11/2015.
 */
@Path("/employee-ratings")
public class EmployeeRatingResource {

    @GET
    @Path("/count")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response countEmployeeRatings( @BeanParam GenericBeanParam params ) throws ForbiddenException {

        RESTToolkit.authorizeAccessToWebService(params);

        return null;
    }

    /**
     * This method enable to populate list of resources and each individual resource with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList<EmployeeRating> employeeRatings, UriInfo uriInfo, Integer offset, Integer limit) {

        // navigation links through collection of resources
        ResourceList.generateNavigationLinks(employeeRatings, uriInfo, offset, limit);

        try {
            // count resources hypermedia link
            Method countMethod = EmployeeRatingResource.class.getMethod("countEmployeeRatings", GenericBeanParam.class);
            employeeRatings.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(EmployeeRatingResource.class).path(countMethod).build()).rel("count").build() );

            // get all resources hypermedia link
            employeeRatings.getLinks().add( Link.fromUri(uriInfo.getBaseUriBuilder().path(EmployeeRatingResource.class).build()).rel("employee-ratings").build() );

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for(EmployeeRating employeeRating : employeeRatings.getResources())
            EmployeeRatingResource.populateWithHATEOASLinks(employeeRating, uriInfo);
    }

    /**
     * This method enables to populate each individual resource with hypermedia links
     */
    private static void populateWithHATEOASLinks(EmployeeRating employeeRating, UriInfo uriInfo) {

        try {
            // self link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/{sub-id}
            Method clientEmployeeRatingsMethod = ClientResource.class.getMethod("getEmployeeRatingResource");
            employeeRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(clientEmployeeRatingsMethod)
                    .path(employeeRating.getEmployee().getUserId().toString())
                    .resolveTemplate("clientId", employeeRating.getClient().getClientId().toString())
                    .build())
                    .rel("self").build());

            // sub-collection link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}
            employeeRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(clientEmployeeRatingsMethod)
                    .resolveTemplate("clientId", employeeRating.getClient().getClientId().toString())
                    .build())
                    .rel("client-employee-ratings").build());

            Method employeeEmployeeRatingsMethod = EmployeeResource.class.getMethod("getEmployeeRatingResource");
            employeeRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(EmployeeResource.class)
                    .path(employeeEmployeeRatingsMethod)
                    .resolveTemplate("userId", employeeRating.getEmployee().getUserId().toString())
                    .build())
                    .rel("employee-employee-ratings").build());

            // sub-collection count link with pattern: http://localhost:port/app/rest/{resources}/{id}/{subresources}/count
            Method countByClientMethod = ClientResource.EmployeeRatingResource.class.getMethod("countClientEmployeeRatings", Long.class, GenericBeanParam.class);
            employeeRating.getLinks().add(Link.fromUri(uriInfo.getBaseUriBuilder()
                    .path(ClientResource.class)
                    .path(clientEmployeeRatingsMethod)
                    .path(countByClientMethod)
                    .resolveTemplate("clientId", employeeRating.getClient().getClientId().toString())
                    .build())
                    .rel("client-employee-ratings-count").build());

            // TODO

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
