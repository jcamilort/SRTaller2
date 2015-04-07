/**
 * 
 */
package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;

/**
 * @author juancamiloortiz
 *
 */
@Entity
public class AttributeDB extends Model{

	/**
	 * Default Serial Version
	 */
	private static final long serialVersionUID = 1L;

	public String name;


    public static Finder<Long,AttributeDB> find = new Finder<Long,AttributeDB>(
            Long.class, AttributeDB.class
    );

    /**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

    @Id
    @GeneratedValue
	public long attribute_id;

	public AttributeDB()
	{
	}


	public long getID()
	{
		return attribute_id;
	}

	
	public void setID(long idP)
	{
		this.attribute_id = idP;
	}
}
