package recommender;

import java.util.ArrayList;

import models.Business;
import models.Category;
import models.User;

public class EntityCollector {
	public ArrayList<User> usuarios;

	private ArrayList<Business> negocios;
	private ArrayList<Category> categorias;
	
	public static EntityCollector sInstance = null;
	
	public EntityCollector()
	{
		negocios = new ArrayList<Business>();
		usuarios = new ArrayList<User>();
		categorias = new ArrayList<Category>();
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
	public void addCategory(Category category)
	{
		categorias.add(category);
	}
	public ArrayList<Category> getCategories()
	{
		return categorias;
	}
}
