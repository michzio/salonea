package pl.salonea.jaxrs.bean_params;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Created by michzio on 10/11/2015.
 */
public class CreditCardBeanParam extends PaginationBeanParam {

    private @QueryParam("clientId") List<Long> clientIds;

}
