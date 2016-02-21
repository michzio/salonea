package pl.salonea.jaxrs.bean_params;

import javax.ws.rs.QueryParam;

/**
 * Created by michzio on 06/10/2015.
 */
public class DiscountRangeBeanParam extends PaginationBeanParam {

    private @QueryParam("minDiscount") Short minDiscount;
    private @QueryParam("maxDiscount") Short maxDiscount;

    public Short getMinDiscount() {
        return minDiscount;
    }

    public void setMinDiscount(Short minDiscount) {
        this.minDiscount = minDiscount;
    }

    public Short getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(Short maxDiscount) {
        this.maxDiscount = maxDiscount;
    }
}
