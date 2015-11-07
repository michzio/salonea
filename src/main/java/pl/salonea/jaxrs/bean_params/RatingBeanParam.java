package pl.salonea.jaxrs.bean_params;

import javax.ws.rs.QueryParam;

/**
 * Created by michzio on 07/11/2015.
 */
public class RatingBeanParam extends PaginationBeanParam {

    private @QueryParam("minRating") Short minRating;
    private @QueryParam("maxRating") Short maxRating;
    private @QueryParam("rating") Short exactRating;

    public Short getMinRating() {
        return minRating;
    }

    public void setMinRating(Short minRating) {
        this.minRating = minRating;
    }

    public Short getMaxRating() {
        return maxRating;
    }

    public void setMaxRating(Short maxRating) {
        this.maxRating = maxRating;
    }

    public Short getExactRating() {
        return exactRating;
    }

    public void setExactRating(Short exactRating) {
        this.exactRating = exactRating;
    }
}
