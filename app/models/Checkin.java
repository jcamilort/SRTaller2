/**
 * 
 */
package models;

import javax.persistence.*;

import play.db.ebean.*;

import java.util.ArrayList;

/**
 * @author juancamiloortiz
 *
 */
@Entity
public class Checkin extends Model {

	/**
	 * Default Serial Version ID
	 */
	private static final long serialVersionUID = 1L;
	public String business_id;
	/**
	 * Modela los checkins, las filas contienen las 24 horas del dia
	 * Las columnas contienen los dias de la semana
	 * El numero de checkins en el rango de una hora por dias se encuentra en la interseccion dia-hora
	 */

    @Transient
	public ArrayList<ArrayList<Integer>> checkin_info;
	
	public Checkin()
	{
		//checkin_info = new int [24][7];
	}

	/**
	 * @return the business_id
	 */
	public String getBusiness_id() {
		return business_id;
	}

	/**
	 * @return the checkin_info
	 */
	public ArrayList<ArrayList<Integer>> getCheckin_info() {
		return checkin_info;
	}
	/**
	 * @param business_id the business_id to set
	 */
	public void setBusiness_id(String business_id) {
		this.business_id = business_id;
	}

	/**
	 * @param checkin_info the checkin_info to set
	 */
	public void setCheckin_info(ArrayList<ArrayList<Integer>> checkin_info) {
		this.checkin_info = checkin_info;
	}

}
