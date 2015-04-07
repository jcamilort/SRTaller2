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
    @GeneratedValue
	public long category_id;
	
	public String name;


    public static Finder<Long,Category> find = new Finder<Long,Category>(
            Long.class, Category.class
    );

	public Category()
	{
		
	}
	
	public void setName(String nameP)
	{
		this.name = nameP;
	}
	public void setID(long idP)
	{
		this.category_id = idP;
	}
	
	public long getID()
	{
		return category_id;
	}
	
	public String getName()
	{
		return name;
	}
}
