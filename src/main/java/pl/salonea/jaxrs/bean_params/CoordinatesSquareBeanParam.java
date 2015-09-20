package pl.salonea.jaxrs.bean_params;

import javax.ws.rs.QueryParam;

/**
 * Created by michzio on 19/09/2015.
 */
public class CoordinatesSquareBeanParam extends PaginationBeanParam {

    private @QueryParam("minLongitudeWGS84") Float minLongitudeWGS84;
    private @QueryParam("minLatitudeWGS84") Float minLatitudeWGS84;
    private @QueryParam("maxLongitudeWGS84") Float maxLongitudeWGS84;
    private @QueryParam("maxLatitudeWGS84") Float maxLatitudeWGS84;

    public Float getMinLongitudeWGS84() {
        return minLongitudeWGS84;
    }

    public void setMinLongitudeWGS84(Float minLongitudeWGS84) {
        this.minLongitudeWGS84 = minLongitudeWGS84;
    }

    public Float getMinLatitudeWGS84() {
        return minLatitudeWGS84;
    }

    public void setMinLatitudeWGS84(Float minLatitudeWGS84) {
        this.minLatitudeWGS84 = minLatitudeWGS84;
    }

    public Float getMaxLongitudeWGS84() {
        return maxLongitudeWGS84;
    }

    public void setMaxLongitudeWGS84(Float maxLongitudeWGS84) {
        this.maxLongitudeWGS84 = maxLongitudeWGS84;
    }

    public Float getMaxLatitudeWGS84() {
        return maxLatitudeWGS84;
    }

    public void setMaxLatitudeWGS84(Float maxLatitudeWGS84) {
        this.maxLatitudeWGS84 = maxLatitudeWGS84;
    }
}
