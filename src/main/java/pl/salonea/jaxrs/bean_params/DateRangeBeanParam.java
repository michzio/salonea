package pl.salonea.jaxrs.bean_params;

import pl.salonea.jaxrs.utils.RESTDateTime;
import pl.salonea.utils.Period;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * Created by michzio on 02/09/2015.
 */
public class DateRangeBeanParam extends PaginationBeanParam {

    private @QueryParam("startDate") RESTDateTime startDate;
    private @QueryParam("endDate") RESTDateTime endDate;

    public RESTDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(RESTDateTime startDate) {
        this.startDate = startDate;
    }

    public RESTDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(RESTDateTime endDate) {
        this.endDate = endDate;
    }

    public Period getPeriod() {
        if(getStartDate() == null && getEndDate() == null)
            return null;

        return new Period(getStartDate(), getEndDate());
    }
}