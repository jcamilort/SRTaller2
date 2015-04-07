

/**
 * 
 */
package models;

import java.util.Date;

import javax.persistence.*;

import play.db.ebean.Model;

/**
 * @author juancamiloortiz
 *
 */
@Entity
public class Tip extends Model{

	/**
	 * Default Serial Version ID
	 */
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    public Long id;


    @ManyToOne
	public User user;
    @ManyToOne
	public Business business;
	public String text;
	public Date date;
	public int likes;
	/**
	 * Tip sentiment
     * -2: undefined
	 * -1 : negative
	 * 0 : neutral
	 * 1: positive
	 */
	public int sentiment;

    public static Finder<Long,Tip> find = new Finder<Long,Tip>(
            Long.class, Tip.class
    );


    public Tip()
    {
        sentiment=-2;
    }
	/**
	 * @return the userID
	 */
	public User getUser() {
		return user;
	}
	/**
	 * @return the businees_id
	 */
	public Business getBusinees() {
		return business;
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
	 * @return the likes
	 */
	public int getLikes() {
		return likes;
	}
	/**
	 * @return the sentiment
	 */
	public int getSentiment() {
		return sentiment;
	}
	/**
	 * @param user the userID to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
	/**
	 * @param business the businees_id to set
	 */
	public void setBusiness(Business business) {
		this.business = business;
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
	 * @param likes the likes to set
	 */
	public void setLikes(int likes) {
		this.likes = likes;
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
