package recommender;

import java.util.ArrayList;

import models.Business;
import models.User;

public class EntityCollector {
	public ArrayList<User> usuarios;

	private ArrayList<Business> negocios;
	
	public static EntityCollector sInstance = null;
	
	public EntityCollector()
	{
		negocios = new ArrayList<Business>();
		usuarios = new ArrayList<User>();
	}
	
	public static EntityCollector getInstance()
	{
		if( sInstance == null){
			sInstance = new EntityCollector();
		}
		return sInstance;
	}
	
	public void addUser(User user)
	{
		usuarios.add(user);
	}
	public void addBusiness(Business business)
	{
		negocios.add(business);
	}
}
