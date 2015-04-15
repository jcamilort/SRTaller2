package recommender;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import controllers.EvaluationController;
import models.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by carol on 8/04/15.
 */
public class ContentRecommender {

    private int MAX_REVIEWED;
    private String[] limitedUsers;
    private ArrayList<String> specialBusinesses;

    public void setMaxRecommendations(int maxRecommendations) {
        this.maxRecommendations = maxRecommendations;
    }

    private int maxRecommendations;
    private static ContentRecommender instance;
    private boolean geoFiltered;

    private ArrayList<Business> filteredBusinessGeo;

    public void setMaxBusinessReviewed(int max)
    {
        MAX_REVIEWED=max;
    }
    public ContentRecommender()
    {
        filteredBusinessGeo=new ArrayList<Business>();
        maxRecommendations =20;
        MAX_REVIEWED=1500;
    }

    public static ContentRecommender getInstance() {
        if(instance==null)
            instance=new ContentRecommender();
        return instance;
    }

    public ArrayList<Recommendation> recommend(double[] latlong,String hour, User user,String[] categories,String[] attributes)
    {
        if(latlong!=null&&latlong.length>0&&latlong[0]!=0)
        {
            geoFiltered=true;
            if(filteredBusinessGeo==null||filteredBusinessGeo.isEmpty())
            {
                try{
                    filteredBusinessGeo = LocationFilter.obtenerSitiosCercanos(latlong[0], latlong[1], HybridRecommender.RADIO_FILTRO);
                }
                catch (Exception e)
                {
                    filteredBusinessGeo=new ArrayList<>();
                }

            }
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
            String[] bids;
            String[] bidsPrev=getAllPosibleSimilarBusiness(user ==null?null: user.getUser_id(),cs);
            if(specialBusinesses!=null&&!specialBusinesses.isEmpty())
            {
                bids=includeSpecial(bidsPrev);
            }
            else bids=bidsPrev;

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
                r.setEstimatedRating(ordered[j][1]*5);
                returned.add(r);
            }
        }
        System.out.println("\n\n!!!HIGH ATTENTION HERE... RECOMMENDING BY CONTENT TOOK "+(System.currentTimeMillis()-t1)+"ms!!!\n\n");
        return returned;
    }

    private String[] includeSpecial(String[] bidsPrev) {
        ArrayList<String> all=new ArrayList<String>();
        for (int i = 0; i < bidsPrev.length; i++) {
            all.add(bidsPrev[i]);
        }
        for (int i = 0; i < specialBusinesses.size(); i++) {
            if(!all.contains(specialBusinesses.get(i)))
                all.add(specialBusinesses.get(i));
        }
        return all.toArray(new String[all.size()]);
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
            where2=" where ";
            String tqWhere="";
            for (int i = 0; i < c2.length; i++) {
                tqWhere+=" or category_id="+c2[i];
            }

            where2+=tqWhere.substring(3);
            if(completeQuery.contains("where"))
                completeQuery+=tqWhere;
            else
                completeQuery+=tqWhere.substring(3);
        }
        String queryJoin="select count(*) co from (select category_id cid from category"+where1+") c2 join category on cid=category_id "+where2;

        cot = Ebean.createSqlQuery(completeQuery).findList().get(0).getInteger("co");
        cojoin= Ebean.createSqlQuery(queryJoin).findList().get(0).getInteger("co");

        return (double)cojoin/(double)cot;
    }
    public double getJaccardSimilarity(Category[] c1,Category[]  c2)
    {
        long[] cs1=new long[c1.length];
        for (int i = 0; i < cs1.length; i++) {
            if(c1[i]!=null)
                cs1[i]=c1[i].getID();
        }
        long[] cs2=new long[c2.length];
        for (int i = 0; i < cs2.length; i++) {
            if(c2[i]!=null)
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
            theQuery="select distinct business_business_id from businesscategories"+joinstatement+" where ";
            String tqWhere="";
            for (int i = 0; i < cs.length; i++) {
                if(cs[i]!=null)
                    tqWhere+=" or businesscategories.category_category_id="+cs[i].getID();
                else System.err.println("Trying use an unknown category...");
            }
            theQuery+=tqWhere.substring(3);
            theQuery+=" order by rand() limit "+MAX_REVIEWED;
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

            for (int i = 0; i < filteredBusinessGeo.size() && i < MAX_REVIEWED*5; i++) {

                where+="or business_id=\""+filteredBusinessGeo.get(i).business_id+"\" ";
            }
            where=where.substring(3);

            String join=" join (select business_id from business where "+where+") bfilter on bfilter.business_id=business_business_id";
            return join;
        }
        return "";
    }

    public EvaluationResult evaluate (boolean radio,int maxBusinessNeighb, double trainingPercentage, int nUsers,int maxrec)
    {

        EvaluationResult er=new EvaluationResult();
        er.description="Recomendador de contenido: {filtra por radio="+radio+
                "%,negocios revisados="+maxBusinessNeighb
                +", porcentaje entrenamiento= "+(int)(trainingPercentage*100)
                +", #usuarios prueba= "+nUsers
                +", numero de recomendaciones x usuario= "+maxrec+"}";
        maxRecommendations=maxrec;
        //1. Get users with most visited places (100?) - or with more stars??
        //2. split according to trainingPercentage (reviews)
        //3. in limitedRandomBusiness make sure to include the target business (set special business in recommender each time and use it)
        //4.recommend for all users
        //5. calculate precision and recall.
        //User.find.where()

        int totalUsers=nUsers;
        String[] uids= EvaluationController.findPopularUsers(totalUsers);
        setLimitedUsers(uids);
        setMaxBusinessReviewed(maxBusinessNeighb);
        int tpTotal=0;
        int esperadoTotal=0;

        long t1=0,averageTime=0;
        t1=System.currentTimeMillis();
        for (int i = 0; i < uids.length; i++) {
            User u=User.find.byId(uids[i]);
            String[] splitedCats = getSplitedCategories(u, trainingPercentage);
            t1=System.currentTimeMillis();
            //recommend based in splitted categories not in user:
            ArrayList<Recommendation> res = recommend(radio ? u.getMedianLocation() : null, null, null, splitedCats, null);
            averageTime+=System.currentTimeMillis()-t1;
            int[] result=getEval(res);//result[#esperado][#tp - intersección]
            esperadoTotal+=result[0];
            tpTotal+=result[1];
        }
        averageTime/=uids.length;

        er.precision=(double)(((double)tpTotal)/(double)(maxRecommendations*totalUsers));
        er.recall=(double)(((double)tpTotal)/(double)(esperadoTotal));
        er.time=averageTime;

        return er;
    }

    private String[] getSplitedCategories(User u, double trainingPercentage) {
        List<Business> bs = u.getVisitedBusiness();
        specialBusinesses = new ArrayList<String>();
        int toplimit= (int) (((double)bs.size())*trainingPercentage);
        ArrayList<String> trainingBusiness = new ArrayList<String>();
        ArrayList<Category> cats=new ArrayList<Category>();

        for (int i = 0; i < toplimit; i++) {
            Business b=bs.get(i);
            trainingBusiness.add(b.business_id);
            for (int j = 0; j < b.categories.size(); j++) {
                Category cate=b.categories.get(j);
                if(!cats.contains(cate))
                    cats.add(cate);
            }
        }
        for (int i = toplimit; i < bs.size(); i++) {
            specialBusinesses.add(bs.get(i).business_id);
        }


        String[] ansa=new String[cats.size()];
        for (int i = 0; i < cats.size(); i++) {
            ansa[i]=cats.get(i).name;
        }
        return ansa;
    }

    /**
     * //result[#esperado][#tp - intersección]
     * @param res
     * @return
     */
    private int[] getEval(ArrayList<Recommendation> res) {
        int[] result=new int[2];
        result[0]=specialBusinesses.size();
        result[1]=0;
        for (Recommendation r:res)
        {
            if(specialBusinesses.contains(r.getBusiness().getBusiness_id()))
                result[1]++;
        }
        return result;
    }


    public void setFilteredBusinessGeo(ArrayList<Business> filteredBusinessGeo) {
        geoFiltered=true;
        this.filteredBusinessGeo = filteredBusinessGeo;
    }


    public void setLimitedUsers(String[] limitedUsers) {
        this.limitedUsers = limitedUsers;
    }

    public String[] getLimitedUsers() {
        return limitedUsers;
    }
}
