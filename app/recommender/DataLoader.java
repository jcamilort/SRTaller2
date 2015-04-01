package recommender;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import models.User;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DataLoader {

	public static final String rutaUsuarios = "./data/yelp_academic_dataset_user.json";
	public static final String rutaUsuariosPrueba = "./data/yelp_academic_dataset_user_test.json";
	public static EntityCollector colector;

	public static void main(String[] args0) {
		cargarNegocios();
		cargarUsuarios();
		colector = EntityCollector.getInstance();
	}

	private static void cargarUsuarios() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					rutaUsuarios));
			System.out.println("Lee el archivo");
			String line = "";
			User usuario;
			while ((line = br.readLine().replace("\\", "")) != null) {
				
				System.out.println("Nueva linea");
				System.out.println(line);

				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObject = (JSONObject) jsonParser.parse(line);
				System.out.println(jsonObject.toJSONString());

				//TODO terminar de extraer compliments y votos
				// get a String from the JSON object
				String name = (String) jsonObject.get("name");
				System.out.println("User's name is: " + name);
				String user_id = (String) jsonObject.get("user_id");
				int review_count= (Integer) jsonObject.get("review_count");
				int fans = (Integer)jsonObject.get("fans");
				double average_stars = (Double)jsonObject.get("average_stars");
				
				// get an array from the JSON object
				JSONArray friends = (JSONArray) jsonObject.get("friends");

				String[] amigos = new String [ friends.size()];
				// take the elements of the json array
				for (int i = 0; i < friends.size(); i++) {
					System.out.println("The " + i + " element of the array: "
							+ friends.get(i));
					amigos[i]= (String) friends.get(i);
				}
				
				//TODO terminar de asignar todo
				usuario = new User();
				usuario.setUser_id(user_id);
				usuario.setName(name);
				usuario.setReview_count(review_count);
				usuario.setAverage_stars(average_stars);
				usuario.setFans(fans);
				usuario.setFriends(amigos);
				
				colector.addUser(usuario);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Error loading users: file not found.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO Exception: " + e.getMessage());
			e.printStackTrace();
		} catch (ParseException e) {
			System.out.println("Parse Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void cargarNegocios() {
		// TODO Auto-generated method stub

	}

}
