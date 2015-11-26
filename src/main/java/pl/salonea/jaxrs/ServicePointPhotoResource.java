package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.ServicePointPhotoFacade;
import pl.salonea.entities.ServicePointPhoto;
import pl.salonea.jaxrs.bean_params.ServicePointPhotoBeanParam;
import pl.salonea.jaxrs.exceptions.BadRequestException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;

import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by michzio on 26/11/2015.
 */
@Path("service-point-photos")
public class ServicePointPhotoResource {

    private static final Logger logger = Logger.getLogger(ServicePointPhotoResource.class.getName());

    @Inject
    private ServicePointPhotoFacade servicePointPhotoFacade;

    /**
     * Method returns all Service Point Photo resources
     * They can be additionally filtered or paginated by @QueryParams
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPhotos( @BeanParam ServicePointPhotoBeanParam params ) throws ForbiddenException, BadRequestException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Service Point Photos by executing ServicePointPhotoResource.getPhotos() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<ServicePointPhoto> photos = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            if(params.getKeywords() != null) {
                if(params.getFileNames() != null || params.getDescriptions() != null)
                    throw new BadRequestException("Query params cannot include keywords and fileNames or descriptions at the same time.");

                if(params.getTagNames() != null) {
                    // find by keywords and tag names
                    photos = new ResourceList<>(
                            servicePointPhotoFacade.findByMultipleCriteria(params.getKeywords(), params.getTagNames(), params.getServicePoints(),
                                    params.getProviders(), params.getCorporations(), params.getOffset(), params.getLimit())
                    );
                } else {
                    // find only by keywords
                    photos = new ResourceList<>(
                            servicePointPhotoFacade.findByMultipleCriteria(params.getKeywords(),params.getServicePoints(), params.getProviders(),
                                    params.getCorporations(), params.getOffset(), params.getLimit())
                    );
                }
            } else {
                // find by firstNames, descriptions or tagNames
                photos = new ResourceList<>(
                        servicePointPhotoFacade.findByMultipleCriteria(params.getFileNames(), params.getDescriptions(), params.getTagNames(),
                                params.getServicePoints(), params.getProviders(), params.getCorporations(), params.getOffset(), params.getLimit())
                );
            }

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all photos without filtering (eventually paginated)
            photos = new ResourceList<>( servicePointPhotoFacade.findAll(params.getOffset(), params.getLimit()) );
        }

        // result resources need to be populated with hypermedia links to enable resource discovery
        ServicePointPhotoResource.populateWithHATEOASLinks(photos, params.getUriInfo(), params.getOffset(), params.getLimit());

        return Response.status(Status.OK).entity(photos).build();
    }

    public static void populateWithHATEOASLinks(ResourceList photos, UriInfo uriInfo, Integer offset, Integer limit) {


    }
}
