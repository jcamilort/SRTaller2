package models;

/**
 * Created by carol on 8/04/15.
 */
public class Recommendation {

    public Business business;
    public double estimatedRating;

    public Recommendation()
    {}
    public Recommendation(Business b,double r)
    {
        business=b;
        estimatedRating =r;
    }
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
