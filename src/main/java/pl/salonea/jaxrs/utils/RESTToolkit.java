package pl.salonea.jaxrs.utils;

import pl.salonea.jaxrs.bean_params.*;
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

    public static void validateDateRange(DateRangeBeanParam params) throws BadRequestException {

        // check correctness of date query params
        if(params.getStartDate() == null || params.getEndDate() == null)
            throw new BadRequestException("Start date or end date query param not specified for request.");

        if(params.getStartDate().after(params.getEndDate()))
            throw new BadRequestException("Start date is after end date.");
    }

    public static void validatePriceRange(PriceRangeBeanParam params) throws BadRequestException {

        // check correctness of price query params
        if(params.getMinPrice() == null)
            throw new BadRequestException("Min price query param cannot be null.");
        if(params.getMaxPrice() == null)
            throw new BadRequestException("Max price query param cannot be null.");
        if(params.getMinPrice() < 0 || params.getMaxPrice() < 0)
            throw new BadRequestException("Price query params cannot be less than 0.");
        if(params.getMaxPrice() < params.getMinPrice())
            throw new BadRequestException("Max price cannot be less than min price.");
    }

    public static void validateDiscountRange(DiscountRangeBeanParam params) throws BadRequestException {

        // check correctness of discount query params
        if (params.getMinDiscount() == null)
            throw new BadRequestException("Min discount query param cannot be null.");
        else if (params.getMinDiscount() < 0 || params.getMinDiscount() > 100)
            throw new BadRequestException("Min discount value should be between 0 and 100.");

        if (params.getMaxDiscount() == null)
            throw new BadRequestException("Max discount query param cannot be null.");
        else if (params.getMaxDiscount() < 0 || params.getMaxDiscount() > 100)
            throw new BadRequestException("Max discount value should be between 0 and 100.");

        if (params.getMaxDiscount() < params.getMinDiscount())
            throw new BadRequestException("Max discount cannot be less than min discount.");
    }
}
