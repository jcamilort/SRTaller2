package recommender;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import models.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by carol on 8/04/15.
 */
public class ContentRecommender {

    private static final int MAX_REVIEWED = 1000;

    public void setMaxRecommendations(int maxRecommendations) {
        this.maxRecommendations = maxRecommendations;
    }

    private int maxRecommendations;
    private static ContentRecommender instance;
    private boolean geoFiltered;

    private ArrayList<Business> filteredBusinessGeo;

    public ContentRecommender()
    {
        filteredBusinessGeo=new ArrayList<Business>();
        maxRecommendations =20;
    }

    public static ContentRecommender getInstance() {
        if(instance==null)
            instance=new ContentRecommender();
        return instance;
    }

    public ArrayList<Recommendation> recommend(double[] latlong,String hour, User user,String[] categories,String[] attributes)
    {
        if(latlong!=null&&latlong.length>0)
        {
            geoFiltered=true;
            double[] geoFilter = latlong;
        }
        else geoFiltered=false;


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

            for (int j = 0; j < maxRecommendations &&j<bids.length; j++) {
                Recommendation r = new Recommendation();
                int index=ordered[j][0].intValue();
                r.setBusiness(Business.find.byId(bids[index]));
                r.setEstimatedRating(ordered[j][1]);
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
                return o2[1].compareTo(o1[1]);
            }
        };
        Arrays.sort(similB, arrayComparator);
        return similB;
    }

    public double getJaccardSimilarity(long[] c1,long[] c2)
    {
        int cojoin=0,cot=0;
        if(c1.length==0|| c2.length==0)
        {
            return 0;
        }
        String completeQuery="select count(*) as co from category where ";
        String where1= "where category_id=3 or category_id=4";
        String where2="where category_id=3 or category_id=4";

        if(c1.length>0)
        {
            where1=" where ";
            String tqWhere="";
            for (int i = 0; i < c1.length; i++) {
                tqWhere+= " or category_id=" + c1[i];
            }
            where1+=tqWhere.substring(3);

            completeQuery+=tqWhere.substring(3);
        }
        if(c2.length>0)
        {
            where2=" ";
            String tqWhere="";
            for (int i = 0; i < c2.length; i++) {
                tqWhere+=" or category_id="+c2[i];
            }
            where2+=tqWhere.substring(3);
            completeQuery+=tqWhere.substring(3);
        }
        //TODO revisar query
        String queryJoin="select count(*) co from (select category_id cid from category"+where1+") c2 join category on cid=category_id "+where2;

        cot = Ebean.createSqlQuery(completeQuery).findList().get(0).getInteger("co");
        cojoin= Ebean.createSqlQuery(queryJoin).findList().get(0).getInteger("co");

        return (double)cojoin/(double)cot;
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
        String joinstatement="";

        if(geoFiltered)
        {
            joinstatement=calculateJoinGeoFiltered();
        }

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
            theQuery="select distinct business_business_id from usercategories join businesscategories on businesscategories.category_category_id=usercategories.category_category_id " +joinstatement+
                    " where user_user_id=\""+uid+"\" order by rand() limit "+MAX_REVIEWED;
        }
        List<SqlRow> re = Ebean.createSqlQuery(theQuery).findList();
        String[] returned=new String[re.size()];

        for (int i = 0; i < returned.length; i++) {
            returned[i]=re.get(i).getString("business_business_id");
        }

        return returned;
    }

    private String calculateJoinGeoFiltered() {

        if(filteredBusinessGeo.size()>0)
        {
            String where="";

            for (Business b:filteredBusinessGeo)
            {
                where+="or business_id=\""+b.business_id+"\" ";
            }
            where=where.substring(3);

            String join="join (select businness_id from business where "+where+") bfilter on bfilter.business_id=business_business_id";
            return join;
        }
        return "";
    }

    public EvaluationResult evaluate (double radioLoc,String hour, double trainingPercentage,int evalMethod)
    {
        //TODO
        return new EvaluationResult();

    }

    public void setFilteredBusinessGeo(ArrayList<Business> filteredBusinessGeo) {
        this.filteredBusinessGeo = filteredBusinessGeo;
    }
}
