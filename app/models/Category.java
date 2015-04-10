/**
 * 
 */
package models;

import javax.persistence.*;

import org.json.simple.JSONArray;
import play.db.ebean.Model;
import play.db.ebean.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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


    public static Finder<Long,Category> finder = new Finder<Long,Category>(
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


    public static List<String> getAll()
    {

        List<Category> r = Category.finder.select("name").findList();

        List<String> strings= new ArrayList<>();
        for (Category c:r)
        {
            strings.add(c.getName());
        }
        return strings;
    }
}
