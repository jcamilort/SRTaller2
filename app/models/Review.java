/**
 * 
 */
package models;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import play.db.ebean.Model;

/**
 * @author juancamiloortiz
 *
 */
@Entity
public class Review extends Model{

	/**
	 * Default Serial Version ID
	 */
	private static final long serialVersionUID = 1L;
	
	public String business_id;
	public String user_id;
	public double stars;
	public String text;
	public Date date;

    public static Finder<Long,Review> finder = new Finder<Long,Review>(
            Long.class, Review.class
    );
	
	/**
	 * Modela los votos sobre el review de las caracteristicas en el arreglo
	 */

    @Transient
	public ArrayList<Integer> votes;

    /**
     * Tip sentiment
     * -2: undefined
     * -1 : negative
     * 0 : neutral
     * 1: positive
     */
    public int sentiment;
	
	public Review()
	{
		//votes = new int[VoteTypes.VOTE_VALUES_QUANTITY];
        votes = new ArrayList<Integer>();
        sentiment=-2;
	}
	/**
	 * @return the business_id
	 */
	public String getBusiness_id() {
		return business_id;
	}
	/**
	 * @return the user_id
	 */
	public String getUser_id() {
		return user_id;
	}
	/**
	 * @return the stars
	 */
	public double getStars() {
		return stars;
	}
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * @return the votes
	 */

	public ArrayList<Integer> getVotes() {
		return votes;
	}
	/**
	 * @param business_id the business_id to set
	 */
	public void setBusiness_id(String business_id) {
		this.business_id = business_id;
	}
	/**
	 * @param user_id the user_id to set
	 */
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	/**
	 * @param stars the stars to set
	 */
	public void setStars(double stars) {
		this.stars = stars;
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	/**
	 * @param votes the votes to set
	 */
	public void setVotes(ArrayList<Integer> votes) {
		this.votes = votes;
	}

    /**
     * @return the sentiment
     */
    public int getSentiment() {
        return sentiment;
    }

    /**
     * @param sentiment the sentiment to set
     */
    public void setSentiment(int sentiment) {
        this.sentiment = sentiment;
    }
    @Override
    public String toString()
    {
        return text;
    }
}

