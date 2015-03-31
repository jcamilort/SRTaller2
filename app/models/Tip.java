/**
 * 
 */
package models;

import java.util.Date;

import javax.persistence.Entity;

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

	public String userID;
	public String businees_id;
	public String text;
	public Date date;
	public int likes;
	/**
	 * Tip sentiment
	 * -1 : negative
	 * 0 : neutral
	 * 1: positive
	 */
	public int sentiment;
	/**
	 * @return the userID
	 */
	public String getUserID() {
		return userID;
	}
	/**
	 * @return the businees_id
	 */
	public String getBusinees_id() {
		return businees_id;
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
	 * @param userID the userID to set
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}
	/**
	 * @param businees_id the businees_id to set
	 */
	public void setBusinees_id(String businees_id) {
		this.businees_id = businees_id;
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
	
}
