package pl.salonea.utils;

/**
 * Created by michzio on 04/08/2015.
 */
public class CoordinatesCircle {

    private Float longitudeWGS84, latitudeWGS84;
    private Double radius;

    public CoordinatesCircle(Float longitudeWGS84, Float latitudeWGS84, Double radius) {
        this.longitudeWGS84 = longitudeWGS84;
        this.latitudeWGS84 = latitudeWGS84;
        this.radius = radius;
    }

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
