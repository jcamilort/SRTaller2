/**
 * 
 */
package models;

import javax.persistence.*;

import play.db.ebean.Model;

/**
 * @author juancamiloortiz
 *
 */
@Entity
public class Category extends Model{

	/**
	 * Default Serial Version
	 */
	private static final long serialVersionUID = 1L;

    @Id
	public int category_id;
	
	public String name;
	
	public Category()
	{
		
	}
	
	public void setName(String nameP)
	{
		this.name = nameP;
	}
	public void setID(int idP)
	{
		this.category_id = idP;
	}
	
	public int getID()
	{
		return category_id;
	}
	
	public String getName()
	{
		return name;
	}
}
