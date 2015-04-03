package recommender;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import models.Business;
import models.User;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DataLoader {

	public static final String rutaUsuarios = "./data/yelp_academic_dataset_user.json";
	public static final String rutaUsuariosTest = "./data/yelp_academic_dataset_user_test.json";
	public static EntityCollector colector;
	public static final String rutaNegociosTest = "./data/yelp_academic_dataset_business_test.json";

	public static void main(String[] args0) {
		colector = EntityCollector.getInstance();
		cargarNegocios();
		cargarUsuarios();
		
		System.out.println("Carga Completa");
	}

	private static void cargarUsuarios() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(rutaUsuariosTest));
			System.out.println("Lee el archivo");
			String line = "";
			User usuario;
			while ((line = br.readLine()) != null) {

				line = line.replace("\\", "");
				System.out.println("Nueva linea");
				System.out.println(line);

				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObject = (JSONObject) jsonParser.parse(line);
				System.out.println(jsonObject.toJSONString());

				// TODO terminar de extraer compliments y votos
				// get a String from the JSON object
				String name = (String) jsonObject.get("name");
				System.out.println("User's name is: " + name);
				String user_id = (String) jsonObject.get("user_id");
				long review_count_long = (Long)jsonObject.get("review_count");
				int review_count = (int) review_count_long;
				
				long fans = (Long) jsonObject.get("fans");
				int fans_int = (int) fans;
				double average_stars = (Double) jsonObject.get("average_stars");

				// get an array from the JSON object
				JSONArray friends = (JSONArray) jsonObject.get("friends");

				// String[] amigos = new String [ friends.size()];
				ArrayList<String> amigos = new ArrayList<String>();
				// take the elements of the json array
				for (int i = 0; i < friends.size(); i++) {
					System.out.println("The " + i + " element of the array: "
							+ friends.get(i));
					amigos.add((String) friends.get(i));
				}

				// TODO terminar de asignar todo
				usuario = new User();
				usuario.setUser_id(user_id);
				usuario.setName(name);
				usuario.setReview_count(review_count);
				usuario.setAverage_stars(average_stars);
				usuario.setFans(fans_int);
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
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					rutaNegociosTest));
			System.out.println("Lee el archivo");
			String line = "";
			Business negocio;
			while ((line = br.readLine()) != null) {

				line = line.replace("\\", "");

				System.out.println("Nueva linea");
				System.out.println(line);

				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObject = (JSONObject) jsonParser.parse(line);

				// TODO terminar de extraer categorias, horas y atributos
				// get a String from the JSON object
				String business_id = (String) jsonObject.get("business_id");
				String name = (String) jsonObject.get("name");
				String full_address = (String) jsonObject.get("full_address");
				String city = (String) jsonObject.get("city");
				String state = (String) jsonObject.get("state");
				double latitude = (Double) jsonObject.get("latitude");
				double longitude = (Double) jsonObject.get("longitude");
				double stars = (Double) jsonObject.get("stars");
				long review_count = (Long) jsonObject.get("review_count");
				int review_count_int = (int) review_count;
				// get an array from the JSON object
				JSONArray hoods = (JSONArray) jsonObject.get("neighborhoods");

				// String[] amigos = new String [ friends.size()];
				ArrayList<String> vecindarios = new ArrayList<String>();
				// take the elements of the json array
				for (int i = 0; i < hoods.size(); i++) {
					System.out.println("The " + i + " element of the array: "
							+ hoods.get(i));
					vecindarios.add((String) hoods.get(i));
				}

				System.out.println("Business name is: " + name);

				// TODO terminar de asignar atributos, categorias y horas
				negocio = new Business();
				negocio.setName(name);
				negocio.setFull_address(full_address);
				negocio.setLatitude(latitude);
				negocio.setLongitude(longitude);
				negocio.setBusiness_id(business_id);
				negocio.setCity(city);
				negocio.setState(state);
				negocio.setStars(stars);
				negocio.setReview_count(review_count_int);

				colector.addBusiness(negocio);
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

}
