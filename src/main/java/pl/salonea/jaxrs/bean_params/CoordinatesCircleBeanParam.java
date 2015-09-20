package pl.salonea.jaxrs.bean_params;

import javax.ws.rs.QueryParam;

/**
 * Created by michzio on 19/09/2015.
 */
public class CoordinatesCircleBeanParam extends PaginationBeanParam {

    private @QueryParam("longitudeWGS84") Float longitudeWGS84;
    private @QueryParam("latitudeWGS84") Float latitudeWGS84;
    private @QueryParam("radius") Double radius;

    public Float getLongitudeWGS84() {
        return longitudeWGS84;
    }

    public void setLongitudeWGS84(Float longitudeWGS84) {
        this.longitudeWGS84 = longitudeWGS84;
    }

    public Float getLatitudeWGS84() {
        return latitudeWGS84;
    }

    public void setLatitudeWGS84(Float latitudeWGS84) {
        this.latitudeWGS84 = latitudeWGS84;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }
}
