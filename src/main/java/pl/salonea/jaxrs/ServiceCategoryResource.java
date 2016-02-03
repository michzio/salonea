package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.ServiceCategoryFacade;
import pl.salonea.ejb.stateless.ServiceFacade;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import javax.ws.rs.Path;
import java.util.logging.Logger;

/**
 * Created by michzio on 03/02/2016.
 */
@Path("/service-categories")
public class ServiceCategoryResource {

    private static final Logger logger = Logger.getLogger(ServiceCategoryResource.class.getName());

    @Inject
    private UserTransaction utx;

    @Inject
    private ServiceCategoryFacade serviceCategoryFacade;
    @Inject
    private ServiceFacade serviceFacade;
}
