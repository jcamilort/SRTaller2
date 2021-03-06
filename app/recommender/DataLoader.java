package recommender;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import models.*;

import org.apache.mahout.cf.taste.impl.model.MemoryIDMigrator;
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
	private static String rutaCategorias = "./data/categories.txt";
    private static String rutaAtributos= "./data/attributes.txt";
    private static ArrayList<Attribute> arbol=new ArrayList<Attribute>();
    private static ArrayList<String> attr_n0 = new ArrayList<String>();
    private static ArrayList<String> attr_n1 = new ArrayList<String>();

    public static void main(String[] args0) {
		//colector = EntityCollector.getInstance();

		//buildCategories();
		//buildAttributes();

		//cargarNegocios();
		//cargarUsuarios();
		//cargarCheckins();
		cargarReviews();
		//cargarTips();

		System.out.println("Carga Completa");
	}

    public static void loadAllDB()
    {
        cargarCategorias();
        cargarAtributos();
        cargarNegociosDB();
        cargarUsuarios();
        cargarTips();
        cargarReviews();
        generateContentModel();
    }

    private static void generateContentModel() {
        try {
            List<SqlRow> q = Ebean.createSqlQuery("select count(*) as count from item_content").findList();
            SqlQuery qtemp = Ebean
                    .createSqlQuery(" select * from businesscategories");
            qtemp.setMaxRows(200000);

            List<SqlRow> q2 = qtemp
                    .findList();
            System.out.println("\nATTENTION... THERE ARE "+q2.size()+" ROWS IN BUSINESSCATEGORIES....\n");

            if(q.get(0).getInteger("count")>0)
                return;
        }
        catch(Exception e)
        {
            //does not exist
        }

        SqlQuery sqlQuery = Ebean.createSqlQuery(" select * from businesscategories");
        sqlQuery.setMaxRows(3000000);

        List<SqlRow> q2 = sqlQuery
                .findList();

        MemoryIDMigrator thing2long = new MemoryIDMigrator();

        for (SqlRow row:q2)
        {
            try{
                String sid=row.getString("business_business_id");
                ItemContent ic=new ItemContent(sid,thing2long.toLongID(sid),row.getInteger("category_category_id"),1);
                ic.save();
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }

    }

    private static void cargarAtributos() {
        try {
            if (AttributeDB.find.all().size() > 0)
                return;
        }
        catch(Exception e)
        {
            //does not exist
        }
        try {
            BufferedReader br=new BufferedReader(new FileReader(rutaAtributos));
            String line = "";
            while ((line = br.readLine()) != null) {
                AttributeDB at=new AttributeDB();
                at.setName(line);
                at.save();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void cargarCategorias() {
        try {
            if(Category.finder.all().size()>0)
                return;
        }
        catch(Exception e)
        {
            //does not exist
        }

        try {
            BufferedReader br=new BufferedReader(new FileReader(rutaCategorias));
            String line = "";
            while ((line = br.readLine()) != null) {
                Category cat=new Category();
                cat.setName(line);
                cat.save();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getAtrs(JSONObject atributos)
    {
        try{
        ArrayList<Attribute> atsBusiness = new ArrayList<Attribute>();
        // System.out.println("Nueva linea");
        // System.out.println(line);



        // Attributes Key Set Extraction
        Set keySet = atributos.keySet();
        Iterator<?> keys = keySet.iterator();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            System.out.println(atributos.get(key).getClass());
            if (atributos.get(key).getClass().equals(Boolean.class)) {
                System.out.println("Atr n0");
                if (!attr_n0.contains(key)) {
                    attr_n0.add(key);

                    // Agrega el atributo al primer nivel del arbol
                    Attribute atributo = new Attribute();
                    atributo.setName(key);
                    atributo.setEnclosure(false);

                    arbol.add(atributo);
                    if(Boolean.parseBoolean((String)atributos.get(key)))
                        atsBusiness.add(atributo);
                }
            } else if (atributos.get(key).getClass()
                    .equals(Long.class)) {
                Long value = (Long) atributos.get(key);
                System.out.println("Atr n1");
                if (!attr_n1.contains(value.toString())) {
                    attr_n1.add(value.toString());

                    // Si el atributo no existe debe agregar el key
                    // al primer nivel del arbol
                    // Y el valor al segundo nivel
                    Attribute atributo = new Attribute();
                    atributo.setName(value.toString());
                    atributo.setEnclosure(false);

                    Attribute padre = null;
                    if (!attr_n0.contains(key)) {
                        attr_n0.add(key);

                        // El atributo padre no existe, lo debe
                        // agregar al primer nivel del arbol

                        padre = new Attribute();
                        padre.setName(key);
                        padre.setEnclosure(true);
                        padre.addChildren(atributo);
                        atributo.setName2(padre.getName()+"_"+atributo.getName());


                        arbol.add(padre);
                    } else {
                        // El atributo padre existe, debe agregar al
                        // hijo en el segundo nivel
                        boolean termino = false;
                        for (int i = 0; i < arbol.size()
                                && !termino; i++) {
                            padre = arbol.get(i);
                            if (padre.getName().equals(key)) {
                                termino = true;
                            }
                        }
                        if (termino) {
                            padre.addChildren(atributo);
                            atributo.setName2(padre.getName()+"_"+atributo.getName());
                        }
                    }
                    atsBusiness.add(atributo);
                }
            } else if (atributos.get(key).getClass()
                    .equals(String.class)) {
                String value = (String) atributos.get(key);
                System.out.println("Atr n1");

                if (!attr_n1.contains(value)) {

                    Attribute atributo = new Attribute();
                    atributo.setName(value);
                    atributo.setEnclosure(false);

                    attr_n1.add(value);

                    Attribute padre = null;
                    if (!attr_n0.contains(key)) {
                        attr_n0.add(key);

                        padre = new Attribute();
                        padre.setName(key);
                        padre.setEnclosure(true);
                        padre.addChildren(atributo);
                        atributo.setName2(padre.getName()+"_"+atributo.getName());

                        arbol.add(padre);
                    } else {
                        boolean termino = false;
                        for (int i = 0; i < arbol.size()
                                && !termino; i++) {
                            padre = arbol.get(i);
                            if (padre.getName().equals(key)) {
                                termino = true;
                            }
                        }
                        if (termino) {
                            padre.addChildren(atributo);
                            atributo.setName2(padre.getName()+"_"+atributo.getName());
                        }
                    }
                    atsBusiness.add(atributo);
                }
            } else if (atributos.get(key).getClass()
                    .equals(JSONObject.class)) {
                System.out.println("Atr n2");
                JSONObject subatributos = (JSONObject) atributos
                        .get(key);
                if (subatributos == null) {
                    if (!attr_n1.contains(key)) {
                        attr_n1.add(key);
                    }
                } else {
                    Set keySet2 = subatributos.keySet();
                    Iterator<?> itern2 = keySet2.iterator();
                    while (itern2.hasNext()) {
                        String atn2Val = (String) itern2.next();
                        if (!attr_n1.contains(atn2Val)) {

                            Attribute atributo = new Attribute();
                            atributo.setName(atn2Val);
                            atributo.setEnclosure(false);

                            attr_n1.add(atn2Val);

                            Attribute padre = null;
                            if (!attr_n0.contains(key)) {

                                attr_n0.add(key);
                                padre = new Attribute();
                                padre.setName(key);
                                padre.setEnclosure(true);
                                padre.addChildren(atributo);
                                atributo.setName2(padre.getName()+"_"+atributo.getName());

                                arbol.add(padre);
                            } else {
                                boolean termino = false;
                                for (int i = 0; i < arbol.size()
                                        && !termino; i++) {
                                    padre = arbol.get(i);
                                    if (padre.getName().equals(key)) {
                                        termino = true;
                                    }
                                }
                                if (termino) {
                                    padre.addChildren(atributo);
                                    atributo.setName2(padre.getName()+"_"+atributo.getName());
                                }
                            }

                            if(!subatributos.get(atn2Val).getClass().equals(Boolean.class)||Boolean.parseBoolean((String)subatributos.get(atn2Val)))
                                atsBusiness.add(atributo);
                        }
                    }
                }

            }

        }

        String stringAtrs="";
        for (Attribute myat:atsBusiness)
        {
            stringAtrs+= "," + myat.toString();
        }
        stringAtrs=stringAtrs==""?"":stringAtrs.substring(1);
            return stringAtrs;
    } catch (Exception e) {
        System.out.println(e.getClass() + " :: " + e.getMessage());
        //e.printStackTrace();
            return "";
    }

    }
    private static void buildAttributes() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(rutaNegocios));




			System.out.println("Vamos a extraer los atributos");
			String line = "";
			while ((line = br.readLine()) != null) {
				try {

                    line = line.replace("\\", "");


                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(line);

                    JSONObject hours_structure = (JSONObject) jsonObject
                            .get("hours");

                    // TODO terminar de extraer horas y atributos
                    JSONObject atributos = (JSONObject) jsonObject
                            .get("attributes");

                    getAtrs(atributos);

                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }

			System.out.println("Attributes:");
			for (int i = 0; i < arbol.size(); i++) {
				if(!arbol.get(i).enclosure){
					System.out.println(arbol.get(i).getName());
				}
				else{
					ArrayList<Attribute> subattributes = arbol.get(i).encloses;
					for(int j = 0; j<subattributes.size();j++)
					{
						System.out.println(arbol.get(i).getName()+"_"+subattributes.get(j).getName());
					}
				}
			}


			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error loading users: file not found.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void buildCategories() {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(rutaCategorias));
			System.out.println("Construccion de categorias");
			String line = "";

			Category category;
			int count = 0;
			while ((line = br.readLine()) != null) {
				category = new Category();
				category.setID(count);
				category.setName(line);

				System.out.println(count + ". " + category.getName());

				colector.addCategory(category);
				count++;
			}
		} catch (FileNotFoundException e) {
			System.out.println("Error loading users: file not found.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	private static void cargarReviews() {
        try {
            List<SqlRow> q = Ebean.createSqlQuery("select count(*) as count from review").findList();
            if(q.get(0).getInteger("count")>0)
                return;
        }
        catch(Exception e)
        {
            //does not exist
        }
		try {
			BufferedReader br = new BufferedReader(new FileReader(rutaReviews));
			System.out.println("Lee el archivo");
			String line = "";

			PrintWriter writer = new PrintWriter(rutaArchivoDataModel, "UTF-8");

			while ((line = br.readLine()) != null) {

				try {
//					Review review = new Review();
				//	line = line.replace("\\", "");
				//	System.out.println("Nueva linea");

					JSONParser jsonParser = new JSONParser();
					JSONObject jsonObject = (JSONObject) jsonParser.parse(line);

					String business_id = (String) jsonObject.get("business_id");
					String user_id = (String) jsonObject.get("user_id");
					long stars_long = (Long) jsonObject.get("stars");
					double stars = (double) stars_long;
//					String text = (String) jsonObject.get("text");
//                    Date d=new Date();
//                    try{
//                        d = (new SimpleDateFormat("yyyy-MM-dd"))
//                                .parse(jsonObject.get("date").toString());
//                    }
//                    catch(Exception ex)
//                    {
//
//                    }

//
//					// Vote Extraction
//					JSONObject structure = (JSONObject) jsonObject.get("votes");
//
//					Long cool_long = (Long) structure.get("cool");
//					if (cool_long == null) {
//						cool_long = (long) 0;
//					}
//					int cool = cool_long.intValue();
//
//					Long funny_long = (Long) structure.get("funny");
//					if (funny_long == null) {
//						funny_long = (long) 0;
//					}
//					int funny = funny_long.intValue();
//
//					Long useful_long = (Long) structure.get("useful");
//					if (useful_long == null) {
//						useful_long = (long) 0;
//					}
//					int useful = useful_long.intValue();
//
//					ArrayList<Integer> votes = new ArrayList<Integer>(3);
//					for (int i = 0; i < 3; i++) {
//						votes.add(0);
//					}
//					votes.add(0, cool);
//					votes.add(1, funny);
//					votes.add(2, useful);
//
//					review.setBusiness_id(business_id);
//					review.setVotes(votes);
//					review.setDate(d);
//					review.setStars(stars);
//                    review.setText(text.substring(0, Math.min(text.length(), 250)));
//					review.setUser_id(user_id);
//					review.save();
					System.out.println(user_id + ";" + business_id + ";" + stars);
					writer.println(user_id + ";" + business_id + ";" + stars);

				} catch (Exception e) {
					System.out.println("EXCEPTION THROWN: " + e.getClass()
							+ " ::: " + e.getMessage());
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

	public static void cargarTips() {
        try {

            if(Tip.find.all().size()>0)
                return;
        }
        catch(Exception e)
        {
            //does not exist
        }
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

                    t.setBusiness(Business.find.byId(bi));
                    t.setUser(User.find.byId(ui));
					t.setLikes(likes);
					t.setDate(d);
					t.setText(te.substring(0, Math.min(te.length(), 250)));
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
			BufferedReader br = new BufferedReader(new FileReader(rutaCheckins));
			// System.out.println("Lee el archivo");
			String line = "";
			while ((line = br.readLine()) != null) {
				Checkin checkin = new Checkin();

				line = line.replace("\\", "");
				// System.out.println("Nueva linea");
				// System.out.println(line);

				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObject = (JSONObject) jsonParser.parse(line);
				// System.out.println(jsonObject.toJSONString());

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
            if (User.find.all().size() > 0)
                return;
        }
        catch(Exception e)
        {
            //does not exist
        }
		try {

			// ArrayList<String> vote_collector = new ArrayList<String>();
			// ArrayList<String> compliment_collector = new ArrayList<String>();

			BufferedReader br = new BufferedReader(new FileReader(
					rutaUsuarios));
			System.out.println("Lee el archivo");
			String line = "";

			while ((line = br.readLine()) != null) {

				line = line.replace("\\", "");
                try {
                    User usuario=new User();


                    // System.out.println("Nueva linea");
                    // System.out.println(line);

                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(line);
                    // System.out.println(jsonObject.toJSONString());

                    // Vote Extraction
                    JSONObject structure = (JSONObject) jsonObject.get("votes");

                    // // Vote Key Set Extraction
                    // Set keySet = structure.keySet();
                    // System.out.println(keySet.toString());
                    // Iterator<?> keys = keySet.iterator();
                    // int count = 0;
                    // while (keys.hasNext()) {
                    // String key = (String) keys.next(); // Cool funny useful
                    // System.out.println("_____Votes tag " + count + ": " + key);
                    // if (!vote_collector.contains(key)) {
                    // vote_collector.add(key);
                    // }
                    // count++;
                    // }

                    Long cool_long = (Long) structure.get("cool");
                    if (cool_long == null) {
                        cool_long = (long) 0;
                    }
                    int cool = cool_long.intValue();

                    Long funny_long = (Long) structure.get("funny");
                    if (funny_long == null) {
                        funny_long = (long) 0;
                    }
                    int funny = funny_long.intValue();

                    Long useful_long = (Long) structure.get("useful");
                    if (useful_long == null) {
                        useful_long = (long) 0;
                    }
                    int useful = useful_long.intValue();

                    ArrayList<Integer> votes = new ArrayList<Integer>(3);
                    for (int i = 0; i < 3; i++) {
                        votes.add(0);
                    }
                    votes.add(0, cool);
                    votes.add(1, funny);
                    votes.add(2, useful);

                    JSONObject structure_compliments = (JSONObject) jsonObject
                            .get("compliments");

                    Long cool_long_compl = (Long) structure_compliments.get("cool");
                    if (cool_long_compl == null) {
                        cool_long_compl = (long) 0;
                    }
                    int cool_compl = cool_long_compl.intValue();

                    Long photos_long = (Long) structure_compliments.get("photos");
                    if (photos_long == null) {
                        photos_long = (long) 0;
                    }
                    int photos = photos_long.intValue();

                    Long more_long = (Long) structure_compliments.get("more");
                    if (more_long == null) {
                        more_long = (long) 0;
                    }
                    int more = more_long.intValue();

                    Long funny_long_compl = (Long) structure_compliments
                            .get("funny");
                    if (funny_long_compl == null) {
                        funny_long_compl = (long) 0;
                    }
                    int funny_compl = funny_long_compl.intValue();

                    Long hot_long = (Long) structure_compliments.get("hot");
                    if (hot_long == null) {
                        hot_long = (long) 0;
                    }
                    int hot = hot_long.intValue();

                    Long writer_long = (Long) structure_compliments.get("writer");
                    if (writer_long == null) {
                        writer_long = (long) 0;
                    }
                    int writer = writer_long.intValue();

                    Long plain_long = (Long) structure_compliments.get("plain");
                    if (plain_long == null) {
                        plain_long = (long) 0;
                    }
                    int plain = plain_long.intValue();

                    Long cute_long = (Long) structure_compliments.get("cute");
                    if (cute_long == null) {
                        cute_long = (long) 0;
                    }
                    int cute = cute_long.intValue();

                    Long note_long = (Long) structure_compliments.get("note");
                    if (note_long == null) {
                        note_long = (long) 0;
                    }
                    int note = note_long.intValue();

                    Long profile_long = (Long) structure_compliments.get("profile");
                    if (profile_long == null) {
                        profile_long = (long) 0;
                    }
                    int profile = profile_long.intValue();

                    Long list_long = (Long) structure_compliments.get("list");
                    if (list_long == null) {
                        list_long = (long) 0;
                    }
                    int list = list_long.intValue();

                    ArrayList<Integer> compliments = new ArrayList<Integer>(
                            Compliments.COMPLIMENT_ATTRIBUTES);
                    for (int i = 0; i < Compliments.COMPLIMENT_ATTRIBUTES; i++) {
                        compliments.add(0);
                    }
                    compliments.add(Compliments.cool, cool);
                    compliments.add(Compliments.cute, cute);
                    compliments.add(Compliments.funny, funny_compl);
                    compliments.add(Compliments.hot, hot);
                    compliments.add(Compliments.list, list);
                    compliments.add(Compliments.more, more);
                    compliments.add(Compliments.note, note);
                    compliments.add(Compliments.photos, photos);
                    compliments.add(Compliments.plain, plain);
                    compliments.add(Compliments.profile, profile);
                    compliments.add(Compliments.writer, writer);

                    /**
                     * Set keySet_compliments = structure_compliments.keySet();
                     * System.out.println(keySet_compliments.toString());
                     * Iterator<?> keys_compl = keySet_compliments.iterator(); int
                     * count_compliments = 0; while (keys_compl.hasNext()) { String
                     * key = (String) keys_compl.next(); if
                     * (!compliment_collector.contains(key)) {
                     * compliment_collector.add(key); } count_compliments++; }
                     */

                    String name = (String) jsonObject.get("name");
                    // System.out.println("User's name is: " + name);
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
                        // System.out.println("The " + i +
                        // " element of the array: "+ friends.get(i));
                        amigos.add((String) friends.get(i));
                    }

                    usuario.setVotes(votes);
                    usuario.setCompliments(compliments);

                    usuario.setUser_id(user_id);
                    usuario.setName(name);
                    usuario.setReview_count(review_count);
                    usuario.setAverage_stars(average_stars);
                    usuario.setFans(fans_int);
                    usuario.setFriends(amigos);
                    usuario.save();
                }
                catch(Exception e)
                {
                    System.err.println("bad user");
                }

				//colector.addUser(usuario);
			}

			/**
			 * System.out.println("Votes categories:"); for (int i = 0; i <
			 * vote_collector.size(); i++) {
			 * System.out.println(vote_collector.get(i)); }
			 * 
			 * System.out.println("Compliments categories:"); for (int i = 0; i
			 * < compliment_collector.size(); i++) {
			 * System.out.println(compliment_collector.get(i)); }
			 */
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error loading users: file not found.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

    private static void cargarNegociosDB() {
        try {
            if (Business.find.all().size() > 0)
                return;
        }
        catch(Exception e)
        {
            //does not exist
        }
        try {
            ArrayList<String> category_collector = new ArrayList<String>();
            ArrayList<String> attribute_collector = new ArrayList<String>();

            BufferedReader br = new BufferedReader(new FileReader(rutaNegocios));
            System.out.println("Lee el archivo");
            String line = "";


            while ((line = br.readLine()) != null) {
                try {
                    Business negocio = new Business();
                    line = line.replace("\\", "");

                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(line);

                    JSONObject ats=(JSONObject)jsonObject.get("attributes");
                    String atrStr=getAtrs(ats);

                    JSONArray categorias = (JSONArray) jsonObject.get("categories");
                    ArrayList<Category> categorias_negocio = new ArrayList<Category>();

                    for (int i = 0; i < categorias.size();i++)
                    {
                        try {
                            String ct = categorias.get(i).toString();
                            categorias_negocio.add(Category.finder.where().eq("name", ct).findList().get(0));
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    try {
                        ArrayList<ArrayList<Integer>> opentimes = getOpenTimes(jsonObject);
                        negocio.setOpenTimes(opentimes);
                    }
                    catch(Exception e)
                    {
                        System.err.println("without opentimes");
                    }
/*
                    Iterator<Category> iter = colector.getCategories()
                            .iterator();
                    while (iter.hasNext()) {
                        Category actual = iter.next();
                        boolean encontro = false;
                        for (int i = 0; i < categorias.size() && !encontro; i++) {
                            if (actual.name
                                    .equals(categorias.get(i).toString())) {
                                categorias_negocio.add(actual);
                                System.out.println(actual.name);
                                categorias.remove(i);
                                encontro = true;
                            }
                        }
                    }
                    */
                    // get a String from the JSON object
                    String business_id = (String) jsonObject.get("business_id");
                    String name = (String) jsonObject.get("name");
                    String full_address = (String) jsonObject
                            .get("full_address");
                    String city = (String) jsonObject.get("city");
                    String state = (String) jsonObject.get("state");
                    double latitude = (Double) jsonObject.get("latitude");
                    double longitude = (Double) jsonObject.get("longitude");
                    double stars = (Double) jsonObject.get("stars");
                    long review_count = (Long) jsonObject.get("review_count");
                    int review_count_int = (int) review_count;
                    // get an array from the JSON object
                    JSONArray hoods = (JSONArray) jsonObject
                            .get("neighborhoods");

                    // String[] amigos = new String [ friends.size()];
                    ArrayList<String> vecindarios = new ArrayList<String>();
                    // take the elements of the json array
                    for (int i = 0; i < hoods.size(); i++) {
                        // System.out.println("The " + i +
                        // " element of the array: "+ hoods.get(i));
                        vecindarios.add((String) hoods.get(i));
                    }

                    // System.out.println("Business name is: " + name);

                    // TODO terminar de asignar atributos y horas

                    System.out.println("n categs:"+categorias_negocio!=null?categorias_negocio.size():"0");
                    ArrayList<AttributeDB> alist = getAttributesList(atrStr);
                    System.out.println("n attrs:"+alist!=null?alist.size():"0");
                    negocio.setCategories(categorias_negocio);
                    negocio.setAttributes(alist);
                    negocio.setName(name);
                    negocio.setFull_address(full_address);
                    negocio.setLatitude(latitude);
                    negocio.setLongitude(longitude);
                    negocio.setBusiness_id(business_id);
                    negocio.setCity(city);
                    negocio.setState(state);
                    negocio.setStars(stars);
                    negocio.setReview_count(review_count_int);

                    //negocio.setAttributesString(atrStr);

                    negocio.save();
                    // colector.addBusiness(negocio);

                } catch (Exception e) {
                    System.out.println(e.getClass() + " :: " + e.getMessage());
                }
            }

            /*
            for (int i = 0; i < attribute_collector.size(); i++) {
                System.out.println(attribute_collector.get(i));
            }

            for (int i = 0; i < category_collector.size(); i++) {
                System.out.println(category_collector.get(i));
            }
            */
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error loading businesses: file not found.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static ArrayList<AttributeDB> getAttributesList(String atrStr) {

        ArrayList<AttributeDB> resp=new ArrayList<AttributeDB>();
        String[] all=atrStr.split(",");
        for (String a:all)
        {
            try{
                resp.add(AttributeDB.find.where().eq("name",a).findList()
                        .get(0));

            }
            catch(Exception e){
                System.err.println("Error with attribute "+a);
                System.err.println("ewa: "+e.getMessage());
        }
        }
        return resp;
    }

    private static void cargarNegocios() {
		try {
			ArrayList<String> category_collector = new ArrayList<String>();
			ArrayList<String> attribute_collector = new ArrayList<String>();

			BufferedReader br = new BufferedReader(new FileReader(rutaNegocios));
			System.out.println("Lee el archivo");
			String line = "";
			Business negocio;
			while ((line = br.readLine()) != null) {
				try {

					line = line.replace("\\", "");

					// System.out.println("Nueva linea");
					// System.out.println(line);

					JSONParser jsonParser = new JSONParser();
					JSONObject jsonObject = (JSONObject) jsonParser.parse(line);

					// TODO terminar de extraer horas y atributos
					JSONObject atributos = (JSONObject) jsonObject
							.get("attributes");
					// TODO terminar de extraer horas
					JSONObject hours_structure = (JSONObject)jsonObject.get("hours");
					
					JSONObject monday = (JSONObject)hours_structure.get("Monday");
					ArrayList<Integer> monday_array = new ArrayList<Integer>(2);
					
					JSONObject tuesday = (JSONObject)hours_structure.get("Tuesday");
					ArrayList<Integer> tuesday_array = new ArrayList<Integer>(2);
					
					JSONObject wednesday = (JSONObject)hours_structure.get("Wednesday");
					ArrayList<Integer> wednesday_array = new ArrayList<Integer>(2);
					
					JSONObject thursday = (JSONObject)hours_structure.get("Thursday");
					ArrayList<Integer> thursday_array = new ArrayList<Integer>(2);
					
					JSONObject friday = (JSONObject)hours_structure.get("Friday");
					ArrayList<Integer> friday_array = new ArrayList<Integer>(2);
					
					JSONObject saturday = (JSONObject)hours_structure.get("Saturday");
					ArrayList<Integer> saturday_array = new ArrayList<Integer>(2);
					
					JSONObject sunday = (JSONObject)hours_structure.get("Sunday");
					ArrayList<Integer> sunday_array = new ArrayList<Integer>(2);
					
					
					ArrayList<ArrayList<Integer>> openTimes = new ArrayList<ArrayList<Integer>>();
					
					if(monday==null)
					{
						monday_array.add(0,0);
						monday_array.add(1,0);
					}
					else{
						
						//TODO
						String open  = (String) monday.get("open");
						String close = (String) monday.get("close");
						monday_array.add(0,0);
						monday_array.add(1,0 );
					}
					if(tuesday==null)
					{
						tuesday_array.add(0,0);
						tuesday_array.add(1,0);
					}
					else{
						tuesday_array.add(0,(Integer) tuesday.get("open"));
						tuesday_array.add(1,(Integer) tuesday.get("close"));
					}
					if(wednesday==null)
					{
						wednesday_array.add(0,0);
						wednesday_array.add(1,0);
					}
					else{
						wednesday_array.add(0,(Integer) wednesday.get("open"));
						wednesday_array.add(1,(Integer) wednesday.get("close"));
					}
					if(thursday==null)
					{
						thursday_array.add(0,0);
						thursday_array.add(1,0);
					}
					else{
						thursday_array.add(0,(Integer) thursday.get("open"));
						thursday_array.add(1,(Integer) thursday.get("close"));
					}
					if(friday==null)
					{
						friday_array.add(0,0);
						friday_array.add(1,0);
					}
					else{
						friday_array.add(0,(Integer) friday.get("open"));
						friday_array.add(1,(Integer) friday.get("close"));
					}
					if(saturday==null)
					{
						saturday_array.add(0,0);
						saturday_array.add(1,0);
					}
					else{
						saturday_array.add(0,(Integer) saturday.get("open"));
						saturday_array.add(1,(Integer) saturday.get("close"));
					}
					if(sunday==null)
					{
						sunday_array.add(0,0);
						sunday_array.add(1,0);
					}
					else{
						sunday_array.add(0,(Integer) sunday.get("open"));
						sunday_array.add(1,(Integer) sunday.get("close"));
					}
					
					openTimes.add(0,sunday_array);
					openTimes.add(1,monday_array);
					openTimes.add(2,tuesday_array);
					openTimes.add(3,wednesday_array);
					openTimes.add(4,thursday_array);
					openTimes.add(5,friday_array);
					openTimes.add(6,saturday_array);
					
//					// Attributes Key Set Extraction
//					Set keySet = atributos.keySet();
//					System.out.println(keySet.toString());
//					Iterator<?> keys = keySet.iterator();
//					int count = 0;
//					while (keys.hasNext()) {
//						String key = (String) keys.next(); // Cool funny useful
//						System.out.println("_____Attribute tag " + count + ": "
//								+ key);
//						if (!attribute_collector.contains(key)) {
//							attribute_collector.add(key);
//						}
//						count++;
//					}

					JSONArray categorias = (JSONArray) jsonObject
							.get("categories");
					ArrayList<Category> categorias_negocio = new ArrayList<Category>();
					Iterator<Category> iter = colector.getCategories()
							.iterator();
					while (iter.hasNext()) {
						Category actual = iter.next();
						boolean encontro = false;
						for (int i = 0; i < categorias.size() && !encontro; i++) {
							if (actual.name
									.equals(categorias.get(i).toString())) {
								categorias_negocio.add(actual);
								System.out.println(actual.name);
								categorias.remove(i);
								encontro = true;
							}
						}
					}
					// get a String from the JSON object
					String business_id = (String) jsonObject.get("business_id");
					String name = (String) jsonObject.get("name");
					String full_address = (String) jsonObject
							.get("full_address");
					String city = (String) jsonObject.get("city");
					String state = (String) jsonObject.get("state");
					double latitude = (Double) jsonObject.get("latitude");
					double longitude = (Double) jsonObject.get("longitude");
                    double stars = (Double) jsonObject.get("stars");
					long review_count = (Long) jsonObject.get("review_count");
					int review_count_int = (int) review_count;
                    // get an array from the JSON object
					JSONArray hoods = (JSONArray) jsonObject
							.get("neighborhoods");

					// String[] amigos = new String [ friends.size()];
					ArrayList<String> vecindarios = new ArrayList<String>();
					// take the elements of the json array
					for (int i = 0; i < hoods.size(); i++) {
						// System.out.println("The " + i +
						// " element of the array: "+ hoods.get(i));
						vecindarios.add((String) hoods.get(i));
					}

					// System.out.println("Business name is: " + name);

					// TODO terminar de asignar atributos y horas
					negocio = new Business();

					negocio.setCategories(categorias_negocio);
					negocio.setName(name);
					negocio.setFull_address(full_address);
					negocio.setLatitude(latitude);
					negocio.setLongitude(longitude);
					negocio.setBusiness_id(business_id);
					negocio.setCity(city);
					negocio.setState(state);
					negocio.setStars(stars);
					negocio.setReview_count(review_count_int);
					negocio.setOpenTimes(openTimes);

					// negocio.save();
					// colector.addBusiness(negocio);

				} catch (Exception e) {
					System.out.println(e.getClass() + " :: " + e.getMessage());
				}
			}

			for (int i = 0; i < attribute_collector.size(); i++) {
				System.out.println(attribute_collector.get(i));
			}
			for (int i = 0; i < category_collector.size(); i++) {
				System.out.println(category_collector.get(i));
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error loading users: file not found.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

    public static ArrayList<ArrayList<Integer>> getOpenTimes(JSONObject jsonObject) {
        //horas:

        // TODO terminar de extraer horas
        JSONObject hours_structure = (JSONObject)jsonObject.get("hours");

        JSONObject monday = (JSONObject)hours_structure.get("Monday");
        ArrayList<Integer> monday_array = new ArrayList<Integer>(2);

        JSONObject tuesday = (JSONObject)hours_structure.get("Tuesday");
        ArrayList<Integer> tuesday_array = new ArrayList<Integer>(2);

        JSONObject wednesday = (JSONObject)hours_structure.get("Wednesday");
        ArrayList<Integer> wednesday_array = new ArrayList<Integer>(2);

        JSONObject thursday = (JSONObject)hours_structure.get("Thursday");
        ArrayList<Integer> thursday_array = new ArrayList<Integer>(2);

        JSONObject friday = (JSONObject)hours_structure.get("Friday");
        ArrayList<Integer> friday_array = new ArrayList<Integer>(2);

        JSONObject saturday = (JSONObject)hours_structure.get("Saturday");
        ArrayList<Integer> saturday_array = new ArrayList<Integer>(2);

        JSONObject sunday = (JSONObject)hours_structure.get("Sunday");
        ArrayList<Integer> sunday_array = new ArrayList<Integer>(2);


        ArrayList<ArrayList<Integer>> openTimes = new ArrayList<ArrayList<Integer>>();

        if(monday==null)
        {
            monday_array.add(0,0);
            monday_array.add(1,0);
        }
        else{

            //TODO
            String open  = (String) monday.get("open");
            String close = (String) monday.get("close");
            monday_array.add(0,0);
            monday_array.add(1,0 );
        }
        if(tuesday==null)
        {
            tuesday_array.add(0,0);
            tuesday_array.add(1,0);
        }
        else{
            tuesday_array.add(0,(Integer) tuesday.get("open"));
            tuesday_array.add(1,(Integer) tuesday.get("close"));
        }
        if(wednesday==null)
        {
            wednesday_array.add(0,0);
            wednesday_array.add(1,0);
        }
        else{
            wednesday_array.add(0,(Integer) wednesday.get("open"));
            wednesday_array.add(1,(Integer) wednesday.get("close"));
        }
        if(thursday==null)
        {
            thursday_array.add(0,0);
            thursday_array.add(1,0);
        }
        else{
            thursday_array.add(0,(Integer) thursday.get("open"));
            thursday_array.add(1,(Integer) thursday.get("close"));
        }
        if(friday==null)
        {
            friday_array.add(0,0);
            friday_array.add(1,0);
        }
        else{
            friday_array.add(0,(Integer) friday.get("open"));
            friday_array.add(1,(Integer) friday.get("close"));
        }
        if(saturday==null)
        {
            saturday_array.add(0,0);
            saturday_array.add(1,0);
        }
        else{
            saturday_array.add(0,(Integer) saturday.get("open"));
            saturday_array.add(1,(Integer) saturday.get("close"));
        }
        if(sunday==null)
        {
            sunday_array.add(0,0);
            sunday_array.add(1,0);
        }
        else{
            sunday_array.add(0,(Integer) sunday.get("open"));
            sunday_array.add(1,(Integer) sunday.get("close"));
        }

        openTimes.add(0,sunday_array);
        openTimes.add(1,monday_array);
        openTimes.add(2,tuesday_array);
        openTimes.add(3,wednesday_array);
        openTimes.add(4,thursday_array);
        openTimes.add(5,friday_array);
        openTimes.add(6,saturday_array);
        return openTimes;
    }
}
