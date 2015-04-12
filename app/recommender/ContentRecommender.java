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

import play.db.*;

import javax.sql.DataSource;
import java.util.ArrayList;

/**
 * Created by carol on 8/04/15.
 */
public class ContentRecommender {

    private static ContentRecommender instance;
    
	private final GenericItemBasedRecommender recommender;
    private final PlusAnonymousConcurrentUserDataModel plusDataModel;
    private DataModel datamodel;
    public ContentRecommender()
    {
        DataSource ds = DB.getDataSource();
        datamodel=new MySQLJDBCDataModel(ds,"itemcontent","itemlong_id","feature_id","rating",null);

        ItemSimilarity similarity = new TanimotoCoefficientSimilarity(datamodel);
        recommender=new GenericItemBasedRecommender(datamodel,similarity);

        plusDataModel = new PlusAnonymousConcurrentUserDataModel(datamodel,
                100);
    }


    public ArrayList<Recommendation> recommend(double[] latlong,String hour, User user,String[] categories,String[] attributes)
    {
        Category[] cs = new Category[0];
        if(categories==null||categories.length==0)
            if(user!=null) {
                user.updateCategories();
                cs=user.categories.toArray(new Category[user.categories.size()]);
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
    

	public static ContentRecommender getInstance() {
		if(instance == null){
			instance = new ContentRecommender();
		}
		return instance;
	}
}
