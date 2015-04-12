package recommender;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.SqlUpdate;
import models.*;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.PlusAnonymousConcurrentUserDataModel;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import play.db.*;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by carol on 8/04/15.
 */
public class ContentRecommender {

    private static final int MAX_REVIEWED = 2000;
    private final TanimotoCoefficientSimilarity similarity;
    private final MySQLJDBCDataModel datamodel2;
    private final PlusAnonymousConcurrentUserDataModel plusDataModel2;
    //private final GenericItemBasedRecommender recommenderCol;
    private GenericItemBasedRecommender recommenderCol;
    private final PlusAnonymousConcurrentUserDataModel plusDataModel;
    private NearestNUserNeighborhood neighborhood;
    private DataModel datamodel;
    private int maxRec;

    public ContentRecommender()
    {
        DataSource ds = DB.getDataSource();
        datamodel=new MySQLJDBCDataModel(ds,"item_content","itemlong_id","feature_id","rating",null);
        datamodel2=new MySQLJDBCDataModel(ds,"itemlong_id","item_content","feature_id","rating",null);

        //ItemSimilarity similarity = new TanimotoCoefficientSimilarity(datamodel);
        similarity = new TanimotoCoefficientSimilarity(datamodel);
        TanimotoCoefficientSimilarity similarity2 = new TanimotoCoefficientSimilarity(datamodel2);


        plusDataModel = new PlusAnonymousConcurrentUserDataModel(datamodel,100);
        plusDataModel2 = new PlusAnonymousConcurrentUserDataModel(datamodel2,100);
        //plusDataModel=(PlusAnonymousConcurrentUserDataModel)recommenderCol.getDataModel();
        recommenderCol = new GenericItemBasedRecommender(datamodel,similarity);

        try {
            neighborhood=new NearestNUserNeighborhood(20,similarity,datamodel);
        } catch (TasteException e) {
            e.printStackTrace();
        }
        maxRec=20;
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
            String[] bids=getAllPosibleSimilarBusiness(user==null?null:user.getUser_id(),cs);
            Double[][] similB=new Double[bids.length][2];
            int i=0;
            for (i = 0; i < similB.length&&i<MAX_REVIEWED; i++)
            {
                List<Category> clist = Business.find.byId(bids[i]).categories;
                Category[] cl2 = clist.toArray(new Category[clist.size()]);
                similB[i][1]=getJaccardSimilarity(cl2,cs);
                similB[i][0]=(double)i;
            }
            for (; i < similB.length; i++)
            {
                similB[i][1]=new Double(0);
                similB[i][0]=(double)i;
            }
            Double[][] ordered=orderAll(similB);

            for (int j = 0; i < maxRec&&i<bids.length; i++) {
                Recommendation r = new Recommendation();
                int index=ordered[i][0].intValue();
                r.setBusiness(Business.find.byId(bids[index]));
                r.setEstimatedRating(ordered[i][1]);
                returned.add(r);
            }
        }
        System.out.println("\n\n!!!HIGH ATTENTION HERE... RECOMMENDING BY CONTENT TAKED "+(System.currentTimeMillis()-t1)+"ms!!!\n\n");
        return returned;
    }

    private Double[][] orderAll(Double[][] similB) {
        Comparator<Double[]> arrayComparator = new Comparator<Double[]>() {
            @Override
            public int compare(Double[] o1, Double[] o2) {
                return o1[0].compareTo(o2[0]);
            }
        };
        Arrays.sort(similB, arrayComparator);
        return similB;
    }

    public double getJaccardSimilarity(long[] c1,long[] c2)
    {
        String theQuery="";
        int co1=0,co2=0,cot=0;
        if(c1.length==0|| c2.length==0)
        {
            return 0;
        }
        String completeQuery="select count(*) as co from category where ";
        if(c1.length>0)
        {
            theQuery="select count(*) as co from category where ";
            String tqWhere="";
            for (int i = 0; i < c1.length; i++) {
                tqWhere+= " or category_id=" + c1[i];
            }
            theQuery+=tqWhere.substring(3);
            completeQuery+=tqWhere.substring(3);
            co1 = Ebean.createSqlQuery(theQuery).findList().get(0).getInteger("co");
        }
        if(c2.length>0)
        {
            theQuery="select count(*) as co from category where ";
            String tqWhere="";
            for (int i = 0; i < c2.length; i++) {
                tqWhere+=" or category_id="+c2[i];
            }
            theQuery+=tqWhere.substring(3);
            completeQuery+=tqWhere.substring(3);
            co2 = Ebean.createSqlQuery(theQuery).findList().get(0).getInteger("co");
        }

        cot = Ebean.createSqlQuery(theQuery).findList().get(0).getInteger("co");

        return (double)cot/(double)(co1+co2);
    }
    public double getJaccardSimilarity(Category[] c1,Category[]  c2)
    {
        long[] cs1=new long[c1.length];
        for (int i = 0; i < cs1.length; i++) {
            cs1[i]=c1[i].getID();
        }
        long[] cs2=new long[c2.length];
        for (int i = 0; i < cs2.length; i++) {
            cs2[i]=c2[i].getID();
        }
        return getJaccardSimilarity(cs1,cs2);
    }
    public String[] getAllPosibleSimilarBusiness(String uid,Category[] cs)
    {
        String theQuery="";
        if(cs.length>0)
        {
            theQuery="select distinct business_business_id from businesscategories where ";
            String tqWhere="";
            for (int i = 0; i < cs.length; i++) {
                tqWhere+=" or businesscategories.category_category_id="+cs[i].getID();
            }
            theQuery+=tqWhere.substring(3);
        }
        else{
            theQuery="select distinct business_business_id from usercategories join businesscategories on businesscategories.category_category_id=usercategories.category_category_id where user_user_id=\""+uid+"\"";
        }
        List<SqlRow> re = Ebean.createSqlQuery(theQuery).findList();
        String[] returned=new String[re.size()];

        for (int i = 0; i < returned.length; i++) {
            returned[i]=re.get(i).getString("business_business_id");
        }

        return returned;
    }





    public ArrayList<Recommendation> recommend2(double[] latlong,String hour, User user,String[] categories,String[] attributes)
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
            try {
                long[] myneigh = neighborhood.getUserNeighborhood(au);

                for (int i = 0; i < myneigh.length; i++) {
                    try {
                        //RecommendedItem recommendation = recommendations.get(i);
                        List<SqlRow> q = Ebean.createSqlQuery("select item_id from item_content where itemlong_id=" + myneigh[i]).findList();
                        String bid = q.get(0).getString("item_id");
                        Business rec = Business.find.byId(bid);
                        returned.add(new Recommendation(rec, similarity.userSimilarity(au, myneigh[i])));
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

    public Object getContentModel() {
//        Map<Long, List<Preference>> preferecesOfUsers = new HashMap<Long, List<Preference>>();
//
//        List<Preference> userPrefList;
//
//        // if we already have a userPrefList use it
//        // otherwise create a new one.
//        if ((userPrefList = preferecesOfUsers.get(userLong)) == null) {
//            userPrefList = new ArrayList<Preference>();
//            preferecesOfUsers.put(userLong, userPrefList);
//        }
//        // add the like that we just found to this user
//        userPrefList.add(new GenericPreference(userLong, itemLong, preference));
//        System.out.println("Adding " + person + "(" + userLong+ ") to " + likeName + "(" + itemLong + ") with preference "+preference);
        return null;
    }
}
