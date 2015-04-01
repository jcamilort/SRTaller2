package recommender;

import java.util.ArrayList;

import models.User;

public class EntityCollector {
	public ArrayList<User> usuarios;
	
	public static EntityCollector sInstance = null;
	
	public EntityCollector()
	{
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

}
