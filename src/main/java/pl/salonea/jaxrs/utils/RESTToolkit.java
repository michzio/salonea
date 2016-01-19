package pl.salonea.jaxrs.utils;

import pl.salonea.jaxrs.bean_params.DateBetweenBeanParam;
import pl.salonea.jaxrs.bean_params.GenericBeanParam;
import pl.salonea.jaxrs.bean_params.PaginationBeanParam;
import pl.salonea.jaxrs.exceptions.BadRequestException;
import pl.salonea.jaxrs.exceptions.ForbiddenException;

import java.util.Collection;

/**
 * Created by michzio on 04/11/2015.
 */
public class RESTToolkit {

    /**
     * Method calculates number of filter query params of the request
     * based on PaginationBeanParam object. It counts all query params using
     * UriInfo object and then subtract potentially set offset and limit pagination query params.
     */
    public static Integer calculateNumberOfFilterQueryParams(PaginationBeanParam params) {

        Integer noOfParams = params.getUriInfo().getQueryParameters().size();
        if(params.getOffset() != null) noOfParams -= 1;
        if(params.getLimit() != null) noOfParams -= 1;

        return noOfParams;
    }

    /**
     * Method checks whether access to web service is authorized.
     */
    public static void authorizeAccessToWebService(GenericBeanParam params) throws ForbiddenException {

        if(params.getAuthToken() == null)
            throw new ForbiddenException("Unauthorized access to web service.");
    }

    public static <T> Boolean isSet(Collection<T> collection) {
        return collection != null && collection.size() > 0;
    }

    public static void validateDateRange(DateBetweenBeanParam params) throws BadRequestException {

        // check correctness of date query params
        if(params.getStartDate() == null || params.getEndDate() == null)
            throw new BadRequestException("Start date or end date query param not specified for request.");

        if(params.getStartDate().after(params.getEndDate()))
            throw new BadRequestException("Start date is after end date.");
    }
}
