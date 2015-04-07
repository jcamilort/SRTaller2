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
public class Attribute{
	
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
	
	public String name;


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
	
	public void addChildren(Attribute attribute)
	{
		encloses.add(attribute);
	}
}
