package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import play.mvc.Controller;
import play.mvc.Result;
import recommender.ContentRecommender;
import models.EvaluationResult;
import views.html.evaluation;

import java.util.ArrayList;
import java.util.List;

public class EvaluationController extends Controller {


    public static EvaluationResult evaluateContentRecommender() {

        ContentRecommender cr=new ContentRecommender();
        EvaluationResult res = cr.evaluate(false, 50, 0.5);
        return res;
    }


    public static Result evaluation() {
        ArrayList<EvaluationResult> evals=new ArrayList<EvaluationResult>();
        evals.add(evaluateContentRecommender());
        return ok(evaluation.render(evals));//"Hybrid recommender system"));
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
