/**
 * 
 */
package models;

import java.util.ArrayList;

import javax.persistence.Entity;

import play.db.ebean.Model;

/**
 * @author juancamiloortiz
 *
 */
@Entity
public class User extends Model{

	/**
	 * Default Serial Version ID
	 */
	private static final long serialVersionUID = 1L;
	
	public String user_id;
	public String name;
	public int review_count;
	public double average_stars;
	public int[] votes;
	public int[] compliments;
	public ArrayList<User> friends;
	public int[] elite;
	public String yelping_since;
	public int fans;
	public ArrayList<Review> reviews;
	
	public User()
	{
		votes = new int[VoteTypes.VOTE_VALUES_QUANTITY];
		compliments = new int[VoteTypes.VOTE_VALUES_QUANTITY];
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
	public ArrayList<User> getFriends()
	{
		return friends;
	}
	/**
	 * @return the votes
	 */
	public int[] getVotes() {
		return votes;
	}
	/**
	 * @return the compliments
	 */
	public int[] getCompliments() {
		return compliments;
	}
	/**
	 * @return the elite
	 */
	public int[] getElite() {
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
	public void setVotes(int[] votes) {
		this.votes = votes;
	}
	/**
	 * @param compliments the compliments to set
	 */
	public void setCompliments(int[] compliments) {
		this.compliments = compliments;
	}
	/**
	 * @param elite the elite to set
	 */
	public void setElite(int[] elite) {
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
	public void setFriends(ArrayList<User> friends) {
		this.friends = friends;
	}
	/**
	 * @param reviews the reviews to set
	 */
	public void setReviews(ArrayList<Review> reviews) {
		this.reviews = reviews;
	}
	

}
