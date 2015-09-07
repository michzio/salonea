package pl.salonea.jaxrs.bean_params;

import pl.salonea.jaxrs.utils.RESTDateTime;

import javax.ws.rs.QueryParam;

/**
 * Created by michzio on 06/09/2015.
 */
public class OlderThanBeanParam extends PaginationBeanParam {

    private @QueryParam("olderThan") RESTDateTime date;

    public RESTDateTime getDate() {
        return date;
    }

    public void setDate(RESTDateTime date) {
        this.date = date;
    }
}
