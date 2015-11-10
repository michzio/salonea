package pl.salonea.jaxrs;

import pl.salonea.ejb.stateless.CreditCardFacade;
import pl.salonea.entities.CreditCard;
import pl.salonea.jaxrs.bean_params.CreditCardBeanParam;
import pl.salonea.jaxrs.exceptions.ForbiddenException;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.RESTToolkit;
import pl.salonea.jaxrs.utils.ResourceList;

import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.ws.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by michzio on 10/11/2015.
 */
@Path("/credit-cards")
public class CreditCardResource {

    private static final Logger logger = Logger.getLogger(CreditCardResource.class.getName());

    @Inject
    private CreditCardFacade creditCardFacade;

    /**
     * Method returns all Credit Card resources
     * They can be additionally filtered or paginated by @QueryParams
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCreditCards( @BeanParam CreditCardBeanParam params ) throws ForbiddenException, NotFoundException {

        RESTToolkit.authorizeAccessToWebService(params);
        logger.log(Level.INFO, "returning all Credit Cards by executing CreditCardResource.getCreditCards() method of REST API");

        Integer noOfParams = RESTToolkit.calculateNumberOfFilterQueryParams(params);

        ResourceList<CreditCard> creditCards = null;

        if(noOfParams > 0) {
            logger.log(Level.INFO, "There is at least one filter query param in HTTP request.");

            // get credit cards filtered by criteria provided in query params

        } else {
            logger.log(Level.INFO, "There isn't any filter query param in HTTP request.");

            // get all credit cards without filtering (eventually paginated)
        }

        return null;
    }

}
