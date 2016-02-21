package pl.salonea.jaxrs.bean_params;

import javax.ws.rs.QueryParam;

/**
 * Created by michzio on 21/02/2016.
 */
public class PriceRangeBeanParam extends PaginationBeanParam {

    private @QueryParam("minPrice") Double minPrice;
    private @QueryParam("maxPrice") Double maxPrice;

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }
}
