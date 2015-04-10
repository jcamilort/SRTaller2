package models;

/**
 * Created by carol on 8/04/15.
 */
public class Recommendation {

    private Business business;
    private double estimatedRating;

    public double getEstimatedRating() {
        return estimatedRating;
    }

    public void setEstimatedRating(double estimatedRating) {
        this.estimatedRating = estimatedRating;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }
}
