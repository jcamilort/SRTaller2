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
public class Attribute extends Model{
	
	/**
	 * Default Serial Version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Indica si este atributo encierra subatributos
	 */
	public boolean enclosure;
	/**
	 * Contenedora de los atributos que almacena
	 */
	public ArrayList<Attribute> encloses;
	
	public int attribute_id; 
	
	public Attribute()
	{
		encloses = new ArrayList<Attribute>();
	}
	
	public boolean getEnclosure()
	{
		return enclosure;
	}
	public int getID()
	{
		return attribute_id;
	}
	
	public void setEnclosure(boolean enclosureP)
	{
		this.enclosure = enclosureP;
	}
	
	public void setID(int idP)
	{
		this.attribute_id = idP;
	}
	
	public void addEnclosed(Attribute attID)
	{
		encloses.add(attID);
	}
}
