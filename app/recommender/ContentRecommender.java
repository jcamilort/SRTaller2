package recommender;

import models.Category;
import models.Recommendation;
import models.User;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.PlusAnonymousConcurrentUserDataModel;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import play.Play;
import play.api.Application;
import play.api.db.DB;

import javax.sql.DataSource;
import java.util.ArrayList;

/**
 * Created by carol on 8/04/15.
 */
public class ContentRecommender {

    private final GenericItemBasedRecommender recommender;
    private final PlusAnonymousConcurrentUserDataModel plusDataModel;
    private DataModel datamodel;
    public ContentRecommender()
    {
        DataSource ds = DB.getDataSource("default", (Application) Play.application());
        //TODO create view with null timestamp and value=1;
        datamodel=new MySQLJDBCDataModel(ds,"businesscategories_view","business_business_id","category_category_id","view_val","view_stamp");

        ItemSimilarity similarity = new TanimotoCoefficientSimilarity(datamodel);
        recommender=new GenericItemBasedRecommender(datamodel,similarity);

        plusDataModel = new PlusAnonymousConcurrentUserDataModel(datamodel,
                100);
    }


    public ArrayList<Recommendation> recommend(double[] latlong,String hour, User user,String[] categories,String[] attributes)
    {
        Category[] cs = new Category[0];
        if(categories==null||categories.length==0)
            if(user!=null) cs=user.categories.toArray(new Category[user.categories.size()]);
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

        if(cs.length>0)
        {
            Long au = plusDataModel.takeAvailableUser();
            plusDataModel.setTempPrefs(getPreferenceArray(cs,au),au);
        }


        //TODO
        return new ArrayList<Recommendation>();

    }

    private PreferenceArray getPreferenceArray(Category[] cs,Long utemp) {
        PreferenceArray preferenceArray = new GenericUserPreferenceArray(cs.length);
        for (int i = 0; i < cs.length; i++) {
            preferenceArray.setUserID(i, utemp);
            preferenceArray.setItemID(i, cs[i].getID());
            preferenceArray.setValue(i, 1);
        }

        return preferenceArray;
    }

    public EvaluationResult evaluate (double radioLoc,String hour, double trainingPercentage,int evalMethod)
    {
        //TODO
        return new EvaluationResult();

    }

}
