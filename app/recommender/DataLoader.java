package recommender;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import models.Business;
import models.Checkin;
import models.Review;
import models.Tip;
import models.User;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DataLoader {

	public static final String rutaUsuarios = "./data/yelp_academic_dataset_user.json";
	public static final String rutaTips = "./data/yelp_academic_dataset_tip.json";
	public static final String rutaNegocios = "./data/yelp_academic_dataset_business.json";
	public static final String rutaCheckins = "./data/yelp_academic_dataset_checkin.json";
	public static final String rutaReviews = "./data/yelp_academic_dataset_review.json";
	
	public static final String rutaNegociosTest = "./data/yelp_academic_dataset_business_test.json";
	public static final String rutaCheckinsTest = "./data/yelp_academic_dataset_checkins_test.json";
	public static final String rutaUsuariosTest = "./data/yelp_academic_dataset_user_test.json";
	public static final String rutaReviewsTest = "./data/yelp_academic_dataset_reviews_test.json";
	
	public static final String rutaArchivoDataModel = "./data/reviews_info.txt";
	
	public static EntityCollector colector;
	public static void main(String[] args0) {
		colector = EntityCollector.getInstance();
		cargarNegocios();
		cargarUsuarios();
		cargarCheckins();
		cargarReviews();
		//cargarTips();

		System.out.println("Carga Completa");
	}

	private static void cargarReviews() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					rutaReviews));
			System.out.println("Lee el archivo");
			String line = "";
			
			PrintWriter writer = new PrintWriter(rutaArchivoDataModel, "UTF-8");
			
			while ((line = br.readLine()) != null) {

				try {
					Review review = new Review();
					line = line.replace("\\", "");
					System.out.println("Nueva linea");
					System.out.println(line);

					JSONParser jsonParser = new JSONParser();
					JSONObject jsonObject = (JSONObject) jsonParser.parse(line);
					System.out.println(jsonObject.toJSONString());

					String business_id = (String) jsonObject.get("business_id");
					String user_id = (String) jsonObject.get("user_id");
					long stars_long = (Long) jsonObject.get("stars");
					double stars = (double) stars_long;
					String text = (String) jsonObject.get("text");
					Date d = (new SimpleDateFormat("yyyy-MM-dd"))
							.parse(jsonObject.get("date").toString());
					
					// TODO extraer votes

					review.setBusiness_id(business_id);
					review.setDate(d);
					review.setStars(stars);
					review.setText(text);
					review.setUser_id(user_id);
					//review.save();
					
					writer.println(business_id+"	"+user_id+"		"+stars);

				} catch (Exception e) {
					System.out.println("EXCEPTION THROWN: "+e.getClass()+" ::: "+e.getMessage());
					System.out.println("Ignoring Line...");
					e.printStackTrace();
				}
			}
			
			writer.close();
            br.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Error loading users: file not found.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void cargarTips() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(rutaTips));
			System.out.println("Lee el archivo");
			String line = "";
			JSONParser jsonParser = new JSONParser();
			int badParsed = 0;
			while ((line = br.readLine()) != null) {

				line = line.replace("\\", "");

				System.out.println("nl_tips");
				try {
					JSONObject jsonObject = (JSONObject) jsonParser.parse(line);
					Tip t = new Tip();
					int likes = Integer.parseInt(jsonObject.get("likes")
							.toString());
					Date d = (new SimpleDateFormat("yyyy-MM-dd"))
							.parse(jsonObject.get("date").toString());
					String te = jsonObject.get("text").toString();
					String ui = jsonObject.get("user_id").toString();
					String bi = jsonObject.get("business_id").toString();

					t.setBusinees_id(bi.toString());
					t.setUserID(ui);
					t.setLikes(likes);
					t.setDate(d);
					t.setText(te);
					t.save();
				} catch (ParseException e) {
					e.printStackTrace();
					badParsed++;
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Bad parsed: " + badParsed);
			br.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void cargarCheckins() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					rutaCheckins));
			System.out.println("Lee el archivo");
			String line = "";
			while ((line = br.readLine()) != null) {
				Checkin checkin = new Checkin();

				line = line.replace("\\", "");
				System.out.println("Nueva linea");
				System.out.println(line);

				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObject = (JSONObject) jsonParser.parse(line);
				System.out.println(jsonObject.toJSONString());

				JSONObject structure = (JSONObject) jsonObject
						.get("checkin_info");
				String business_id = (String) jsonObject.get("business_id");

				checkin.setBusiness_id(business_id);

				// Extracts checkins
				for (int i = 0; i < 24; i++) {
					ArrayList<Integer> byDays = new ArrayList<Integer>(7);
					for (int j = 0; j < 7; j++) {
						Long checkin_numb = (Long) structure.get(i + "-" + j);

						if (checkin_numb == null) {
							checkin_numb = (long) 0;
						}
						int checkin_numb_int = checkin_numb.intValue();
						byDays.add(j, checkin_numb_int);
					}
					checkin.checkin_info.add(i, byDays);

				}

			}
            br.close();
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

	private static void cargarUsuarios() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					rutaUsuariosTest));
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
				long review_count_long = (Long) jsonObject.get("review_count");
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
				//usuario.save();

				colector.addUser(usuario);
			}
            br.close();
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

                //negocio.save();
				colector.addBusiness(negocio);
			}
            br.close();
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
