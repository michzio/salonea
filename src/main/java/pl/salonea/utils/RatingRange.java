package pl.salonea.utils;

/**
 * Created by michzio on 25/08/2015.
 */
public class RatingRange {

    private Short minRating;
    private Short maxRating;

    public RatingRange(Short minRating, Short maxRating) {
        this.minRating = minRating;
        this.maxRating = maxRating;
    }

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
}
