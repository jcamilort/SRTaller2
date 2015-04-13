package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.semantria.SentimentAnalizer;
import play.mvc.Controller;
import play.mvc.Result;
import recommender.CollaborativeRecommender;
import recommender.ContentRecommender;
import recommender.DataLoader;
import recommender.EvaluationResult;
import views.html.evaluation;
import views.html.index;

import java.util.List;

public class EvaluationController extends Controller {


    public static Result evaluateContent() {

//        ContentRecommender cr=new ContentRecommender();
//        EvaluationResult res = cr.evaluateCR(false, 500, 0.5);
//        
        
        EvaluationResult resCollab = CollaborativeRecommender.evaluate(50, 100, CollaborativeRecommender.EUCLIDEAN, 0.5);

        return ok(evaluation.render());
    }


    /**
     * Those who have review more
     * @param nusers
     * @return
     */
    public static String[] findPopularUsers(int nusers) {
    	
        String query="select user_id,count(user_id) cc from review group by user_id order by cc desc limit "+nusers;

        List<SqlRow> rows = Ebean.createSqlQuery(query).findList();
        String[] ansa=new String[rows.size()];
        for (int i = 0; i < ansa.length; i++) {
            ansa[i]=rows.get(i).getString("user_id");
        }
        return ansa;
    }

}
