package pl.salonea.jaxrs.utils;

import pl.salonea.jaxrs.bean_params.PaginationBeanParam;

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
}
