/**
 * 
 */
package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.annotation.ConcurrencyMode;
import com.avaje.ebean.annotation.EntityConcurrencyMode;
import play.db.ebean.Model;

/**
 * @author juancamiloortiz
 *
 */
@EntityConcurrencyMode(ConcurrencyMode.NONE)
@Entity
public class User extends Model{

	/**
	 * Default Serial Version ID
	 */
	private static final long serialVersionUID = 1L;

    @Id
	public String user_id;
	public String name;
	public int review_count;
	public double average_stars;
    @ElementCollection
    public ArrayList<Integer> votes;
    @ElementCollection
    public ArrayList<Integer> compliments;
	/**
	 * Contenedor de ids de los amigos del usuario
	 */

    @ElementCollection
	public ArrayList<String> friends;
    @ElementCollection
    public ArrayList<Integer> elite;
	public String yelping_since;
	public int fans;


	public ArrayList<Review> reviews;

    @OneToMany(mappedBy = "user")
    public ArrayList<Tip> tips;


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "usercategories")
    public List<Category> categories;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "userattributes")
    public List<AttributeDB> attributes;

    public static Finder<String,User> find = new Finder<String,User>(
            String.class, User.class
    );

    @Transient
    private ArrayList<Business> visited;
    @Transient
    private double latitude;
    @Transient
    private double longitude;

    public User()
	{
		//votes = new int[VoteTypes.VOTE_VALUES_QUANTITY];
		//compliments = new int[VoteTypes.VOTE_VALUES_QUANTITY];
        votes=new ArrayList<Integer>();
        compliments=new ArrayList<Integer>();
        tips=new ArrayList<Tip>();
        reviews=new ArrayList<Review>();
        attributes=new ArrayList<AttributeDB>();
        categories=new ArrayList<Category>();
	}
	/**
	 * @return the reviews
	 */
	public ArrayList<Review> getReviews() {
		return reviews;
	}
	/**
	 * @return the user_id
	 */
	public String getUser_id() {
		return user_id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the review_count
	 */
	public int getReview_count() {
		return review_count;
	}
	/**
	 * @return the average_stars
	 */
	public double getAverage_stars() {
		return average_stars;
	}
	/**
	 * @return the list of friends
	 */
	public ArrayList<String> getFriends()
	{
		return friends;
	}
	/**
	 * @return the votes
	 */
	public ArrayList<Integer> getVotes() {
		return votes;
	}
	/**
	 * @return the compliments
	 */
    public ArrayList<Integer> getCompliments() {
		return compliments;
	}
	/**
	 * @return the elite
	 */
    public ArrayList<Integer> getElite() {
		return elite;
	}
	/**
	 * @return the yelping_since
	 */
	public String getYelping_since() {
		return yelping_since;
	}
	/**
	 * @return the fans
	 */
	public int getFans() {
		return fans;
	}
	/**
	 * @param user_id the user_id to set
	 */
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @param review_count the review_count to set
	 */
	public void setReview_count(int review_count) {
		this.review_count = review_count;
	}
	/**
	 * @param average_stars the average_stars to set
	 */
	public void setAverage_stars(double average_stars) {
		this.average_stars = average_stars;
	}
	/**
	 * @param votes the votes to set
	 */
	public void setVotes(ArrayList<Integer> votes) {
		this.votes = votes;
	}
	/**
	 * @param compliments the compliments to set
	 */
	public void setCompliments(ArrayList<Integer> compliments) {
		this.compliments = compliments;
	}
	/**
	 * @param elite the elite to set
	 */
	public void setElite(ArrayList<Integer> elite) {
		this.elite = elite;
	}
	/**
	 * @param yelping_since the yelping_since to set
	 */
	public void setYelping_since(String yelping_since) {
		this.yelping_since = yelping_since;
	}
	/**
	 * @param fans the fans to set
	 */
	public void setFans(int fans) {
		this.fans = fans;
	}
	/**
	 * @param friends the friends to set
	 */
	public void setFriends(ArrayList<String> friends) {
		this.friends = friends;
	}
	/**
	 * @param reviews the reviews to set
	 */
	public void setReviews(ArrayList<Review> reviews) {
		this.reviews = reviews;
	}

    public List<Business> getVisitedBusiness()
    {
     if(visited==null||visited.isEmpty()) {

         visited = new ArrayList<Business>();
         List<SqlRow> q = Ebean.createSqlQuery("select distinct business.business_id bid from business join review on review.business_id=business.business_id where user_id=\""+getUser_id()+"\"").findList();
         for (SqlRow r : q) {
             visited.add(Business.find.byId(r.getString("bid")));
         }
     }
        return visited;
    }
    public double getMedianLatitude()
    {
        if(latitude==0)
            getMedianLocation();
        return latitude;

    }
    public double getMedianLongitude()
    {
        if(latitude==0)
            getMedianLocation();
        return longitude;
    }
    public double[] getMedianLocation()
    {
        double[] ansa=new double[2];
        if(latitude!=0&&longitude!=0)
        {
            ansa[0]=latitude;
            ansa[1]=longitude;
            return ansa;
        }

        getVisitedBusiness();
        double[] latitudes=new double[visited.size()];
        double[] longitudes=new double[visited.size()];

        for (int i = 0; i < visited.size(); i++) {

            latitudes[i]=visited.get(i).latitude;
            longitudes[i]=visited.get(i).longitude;
        }
        Arrays.sort(latitudes);

        if (latitudes.length % 2 == 0)
            latitude = ((double)latitudes[latitudes.length/2] + (double)latitudes[latitudes.length/2 - 1])/2;
        else
            latitude = (double) latitudes[latitudes.length/2];

        Arrays.sort(longitudes);

        if (longitudes.length % 2 == 0)
            longitude = ((double)longitudes[longitudes.length/2] + (double)longitudes[longitudes.length/2 - 1])/2;
        else
            longitude = (double) longitudes[longitudes.length/2];

        ansa[0]=latitude;
        ansa[1]=longitude;
        return ansa;
    }


    public void updateCategories() {
        if(categories==null||categories.isEmpty())
        {
            categories=new ArrayList<Category>();

            List<SqlRow> q = Ebean
                    .createSqlQuery(" select distinct category_category_id as cid from review join businesscategories on businesscategories.business_business_id = review.business_id where user_id=\""+user_id+"\"")
                    .findList();
            for (SqlRow row:q)
            {
                try{
                    categories.add(Category.finder.byId(row.getLong("cid")));
                }
                catch(Exception ex){}

            }
            this.update();
        }

    }

    public String[] getCategoriesStr() {

        updateCategories();
        String ansa="";
        for (Category c:categories)
        {
            ansa+=","+c.getName();
        }
        return ansa.length()==0?new String[0]:ansa.substring(1).split(",");
    }
}
