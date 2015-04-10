package recommender;

import models.Recommendation;
import models.User;

import java.util.ArrayList;

/**
 * Created by carol on 8/04/15.
 */
public class HybridRecommender {

    private static HybridRecommender instance;

    public static ArrayList<Recommendation> recommend(double[] latlong,String hour, User user,String[] categories,String[] attributes)
    {
        //TODO
        return new ArrayList<Recommendation>();

    }

    public static EvaluationResult evaluate (double radioLoc,String hour, double trainingPercentage,int evalMethod)
    {

        //TODO
        return new EvaluationResult();

    }

    public static HybridRecommender getInstance() {
        if(instance==null)
            instance=new HybridRecommender();
        return instance;
    }
}
