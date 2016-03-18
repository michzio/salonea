package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.TermFacade;
import pl.salonea.jaxrs.utils.ResourceList;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriInfo;
import java.util.logging.Logger;

/**
 * Created by michzio on 18/03/2016.
 */
@Path("/terms")
public class TermResource {

    private static final Logger logger = Logger.getLogger(TermResource.class.getName());

    @Inject
    private UserTransaction utx;

    @Inject
    private TermFacade termFacade;

    /**
     * This method enables to populate list of resources and each individual resource on list with hypermedia links
     */
    public static void populateWithHATEOASLinks(ResourceList terms, UriInfo uriInfo, Integer offset, Integer limit) {

    }

}
