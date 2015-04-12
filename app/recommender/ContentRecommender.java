package recommender;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.SqlUpdate;
import models.Business;
import models.Category;
import models.Recommendation;
import models.User;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.PlusAnonymousConcurrentUserDataModel;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import play.Play;

import play.db.*;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carol on 8/04/15.
 */
public class ContentRecommender {

    private final GenericItemBasedRecommender recommender;
    private final PlusAnonymousConcurrentUserDataModel plusDataModel;
    private DataModel datamodel;
    public ContentRecommender()
    {
        DataSource ds = DB.getDataSource();
        datamodel=new MySQLJDBCDataModel(ds,"item_content","itemlong_id","feature_id","rating",null);

        ItemSimilarity similarity = new TanimotoCoefficientSimilarity(datamodel);


        plusDataModel = new PlusAnonymousConcurrentUserDataModel(datamodel,100);
        //plusDataModel=(PlusAnonymousConcurrentUserDataModel)recommender.getDataModel();
        recommender=new GenericItemBasedRecommender(datamodel,similarity);
    }


    public ArrayList<Recommendation> recommend(double[] latlong,String hour, User user,String[] categories,String[] attributes)
    {
        long t1=System.currentTimeMillis();
        Category[] cs = new Category[0];
        if(categories==null||categories.length==0) {
            if (user != null) {
                user.updateCategories();
                cs = user.categories.toArray(new Category[user.categories.size()]);
            }
        }
        else
        {
            cs=new Category[categories.length];
            for (int i = 0; i < cs.length; i++) {
                try{
                    cs[i]=Category.finder.where().eq("name",categories[i]).findList().get(0);
                }
                catch (Exception ex)
                {

                }
            }
        }

        ArrayList<Recommendation> returned = new ArrayList<Recommendation>();
        if(cs.length>0)
        {
            Long au = plusDataModel.takeAvailableUser();
            plusDataModel.setTempPrefs(getPreferenceArray(cs,au),au);

            List<RecommendedItem> recommendations = null;
            try {
                recommendations = recommender.recommend(au, 30);


                for (int i = 0; i < recommendations.size(); i++) {
                    try {
                        RecommendedItem recommendation = recommendations.get(i);
                        List<SqlRow> q = Ebean.createSqlQuery("select item_id from item_content where itemlong_id=" + recommendation.getItemID()).findList();
                        String bid = q.get(0).getString("item_id");
                        Business rec = Business.find.byId(bid);
                        returned.add(new Recommendation(rec, recommendation.getValue()));
                    }
                    catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            } catch (TasteException e) {
                e.printStackTrace();
            }

            plusDataModel.releaseUser(au);

            SqlUpdate down = Ebean.createSqlUpdate("DELETE FROM item_content WHERE itemlong_id = "+au);
            down.execute();
        }
        System.out.println("\n\n!!!HIGH ATTENTION HERE... RECOMMENDING BY CONTENT TAKED "+(System.currentTimeMillis()-t1)+"ms!!!\n\n");
        return returned;

    }

    private PreferenceArray getPreferenceArray(Category[] cs,Long utemp) {
        PreferenceArray preferenceArray = new GenericUserPreferenceArray(cs.length);
        preferenceArray.setUserID(0, utemp);
        for (int i = 0; i < cs.length; i++) {
            preferenceArray.setItemID(i, cs[i].getID());
            preferenceArray.setValue(i, 1);

            SqlUpdate insert = Ebean.createSqlUpdate("INSERT INTO item_content (itemlong_id, feature_id,rating) VALUES ("+utemp+","+cs[i].getID()+","+1+")");
            insert.execute();
        }

        return preferenceArray;
    }

    public EvaluationResult evaluate (double radioLoc,String hour, double trainingPercentage,int evalMethod)
    {
        //TODO
        return new EvaluationResult();

    }

}
