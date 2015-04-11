package recommender;

import models.Business;
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
        ArrayList<Recommendation> returned = new ArrayList<Recommendation>();

        //
        /*
        Business bt=new Business();
        bt.setName("un business");
        bt.setBusiness_id("id1");
        returned.add(new Recommendation(bt,4.8));

        bt=new Business();
        bt.setName("otro business");
        bt.setBusiness_id("id2");
        returned.add(new Recommendation(bt,4.8));

        bt=new Business();
        bt.setName("tercer business");
        bt.setBusiness_id("id3");
        returned.add(new Recommendation(bt,4.8));
        */
        ContentRecommender crec=new ContentRecommender();

        crec.recommend(null,"",null,categories,null);
        return returned;

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
