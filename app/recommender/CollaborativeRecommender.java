package recommender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import models.Business;
import models.Recommendation;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.MemoryIDMigrator;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.SpearmanCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class CollaborativeRecommender {
	/**
	 * Recommender which will be hold by this session bean.
	 */
	private static UserBasedRecommender recommender = null;

	/**
	 * An MemoryIDMigrator which is able to create for every string a long
	 * representation. Further it can store the string which were put in and it
	 * is possible to do the mapping back.
	 */
	private static MemoryIDMigrator thing2long = new MemoryIDMigrator();

	/**
	 * The name of the file used for loading.
	 */
	private static String rutaReviewInfo = "./data/reviews_info.txt";

	/**
	 * A data model which is needed for the recommender implementation. It
	 * provides a standardized interface for using the recommender. The data
	 * model can be become quite memory consuming. In our case it will be around
	 * 2 mb.
	 */
	private static DataModel dataModel = null;

	private static GenericBooleanPrefItemBasedRecommender recommenderGBIR;

	private static CollaborativeRecommender instance;

	public static final int PEARSON = 1;
	public static final int SPEARMAN = 2;
	public static final int EUCLIDEAN = 3;

	public static final long user_id_test = 5;

	private static final String rutaReviewInfoLocationBased = null;

	public static void main(String[] args) {
		//generateDataModel();
		executeRecommender("uMKK1Ans4DrUsxlliIH_xA", 100, 10, EUCLIDEAN);
	}
	
	public static void generateDataModelPositionBased(ArrayList<Business> businesses){
		try {
			// create a file out of the resource
			File data = new File(rutaReviewInfo);

			// create a map for saving the preferences (likes) for
			// a certain person
			Map<Long, List<Preference>> preferecesOfUsers = new HashMap<Long, List<Preference>>();

			// use a CSV parser for reading the file
			// use UTF-8 as character set
			BufferedReader br = new BufferedReader(new FileReader(data));

			String line;
			String negocio_anterior = "";
			boolean contenido_anterior  = false;
			// go through every line
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(";");
				String likeName = parts[1].trim();
				
				if(negocio_anterior.equals(likeName)){
					if(contenido_anterior){
						String person = parts[0].trim();
						float preference = Float.parseFloat(parts[2]);
						
						// create a long from the person name
						long userLong = thing2long.toLongID(person);
						
						// store the mapping for the user
						thing2long.storeMapping(userLong, person);
						
						// create a long from the like name
						long itemLong = thing2long.toLongID(likeName);
						
						// store the mapping for the item
						thing2long.storeMapping(itemLong, likeName);
						
						List<Preference> userPrefList;
						
						// if we already have a userPrefList use it
						// otherwise create a new one.
						if ((userPrefList = preferecesOfUsers.get(userLong)) == null) {
							userPrefList = new ArrayList<Preference>();
							preferecesOfUsers.put(userLong, userPrefList);
						}
						// add the like that we just found to this user
						userPrefList.add(new GenericPreference(userLong, itemLong,
								preference));
						System.out.println("LocBased " + person + "(" + userLong
								+ ") to " + likeName + "(" + itemLong
								+ ") with preference " + preference);
					}
				}
				else{
					negocio_anterior = likeName;
					if(isContainedIn(likeName, businesses)){
						contenido_anterior=true;
						String person = parts[0].trim();
						float preference = Float.parseFloat(parts[2]);
						
						// create a long from the person name
						long userLong = thing2long.toLongID(person);
						
						// store the mapping for the user
						thing2long.storeMapping(userLong, person);
						
						// create a long from the like name
						long itemLong = thing2long.toLongID(likeName);
						
						// store the mapping for the item
						thing2long.storeMapping(itemLong, likeName);
						
						List<Preference> userPrefList;
						
						// if we already have a userPrefList use it
						// otherwise create a new one.
						if ((userPrefList = preferecesOfUsers.get(userLong)) == null) {
							userPrefList = new ArrayList<Preference>();
							preferecesOfUsers.put(userLong, userPrefList);
						}
						// add the like that we just found to this user
						userPrefList.add(new GenericPreference(userLong, itemLong,
								preference));
						System.out.println("LocBased " + person + "(" + userLong
								+ ") to " + likeName + "(" + itemLong
								+ ") with preference " + preference);
					}
					else{
						contenido_anterior = false;
					}			
					
				}
				

			}

			// create the corresponding mahout data structure from the map
			FastByIDMap<PreferenceArray> preferecesOfUsersFastMap = new FastByIDMap<PreferenceArray>();
			for (Entry<Long, List<Preference>> entry : preferecesOfUsers
					.entrySet()) {
				preferecesOfUsersFastMap.put(entry.getKey(),
						new GenericUserPreferenceArray(entry.getValue()));
			}

			// create a data model
			dataModel = new GenericDataModel(preferecesOfUsersFastMap);

			FileOutputStream fos = new FileOutputStream("dataModelLocation.model");
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(dataModel);
			System.out.println("DataModel Generated!");
			oos.close();

		} catch (IOException e) {
			System.out.println("Exception: " + e.getClass() + ": "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	private static boolean isContainedIn(String business_id, ArrayList<Business> businesses) {
		boolean termino = false;
		for (int i = 0 ; i < businesses.size() && !termino; i++)
		{
			if(businesses.get(i).business_id.equals(business_id)){
				return true;
			}
		}
		return false;
	}

	public static void generateDataModel() 
	{
		try {
			// create a file out of the resource
			File data = new File(rutaReviewInfo);

			// create a map for saving the preferences (likes) for
			// a certain person
			Map<Long, List<Preference>> preferecesOfUsers = new HashMap<Long, List<Preference>>();

			// use a CSV parser for reading the file
			// use UTF-8 as character set
			BufferedReader br = new BufferedReader(new FileReader(data));

			String line;

			// go through every line
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(";");
				String person = parts[0].trim();
				String likeName = parts[1].trim();
				float preference = Float.parseFloat(parts[2]);

				// create a long from the person name
				long userLong = thing2long.toLongID(person);

				// store the mapping for the user
				thing2long.storeMapping(userLong, person);

				// create a long from the like name
				long itemLong = thing2long.toLongID(likeName);

				// store the mapping for the item
				thing2long.storeMapping(itemLong, likeName);

				List<Preference> userPrefList;

				// if we already have a userPrefList use it
				// otherwise create a new one.
				if ((userPrefList = preferecesOfUsers.get(userLong)) == null) {
					userPrefList = new ArrayList<Preference>();
					preferecesOfUsers.put(userLong, userPrefList);
				}
				// add the like that we just found to this user
				userPrefList.add(new GenericPreference(userLong, itemLong,
						preference));
				System.out.println("Adding " + person + "(" + userLong
						+ ") to " + likeName + "(" + itemLong
						+ ") with preference " + preference);
			}

			// create the corresponding mahout data structure from the map
			FastByIDMap<PreferenceArray> preferecesOfUsersFastMap = new FastByIDMap<PreferenceArray>();
			for (Entry<Long, List<Preference>> entry : preferecesOfUsers
					.entrySet()) {
				preferecesOfUsersFastMap.put(entry.getKey(),
						new GenericUserPreferenceArray(entry.getValue()));
			}

			// create a data model
			dataModel = new GenericDataModel(preferecesOfUsersFastMap);

			FileOutputStream fos = new FileOutputStream("dataModel.model");
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(dataModel);
			System.out.println("DataModel Generated!");
			oos.close();

		} catch (IOException e) {
			System.out.println("Exception: " + e.getClass() + ": "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	public static ArrayList<Recommendation> executeRecommender(String userID,
			int numberOfRecommendations, int neighbors, int similarityMethod) {
		try {

			ArrayList<Recommendation> result = new ArrayList<Recommendation>();
			
			//FileInputStream fis = new FileInputStream("dataModel.model");
			FileInputStream fis = new FileInputStream("dataModelLocation.model");
			ObjectInputStream ois = new ObjectInputStream(fis);

			dataModel = (DataModel) ois.readObject();

			ois.close();

			System.out.println("Numero de Usuarios: " + dataModel.getNumUsers()
					+ "Numero de Items: " + dataModel.getNumItems());

			UserSimilarity similarity;
			if (similarityMethod == PEARSON) {
				// Pearson
				similarity = new PearsonCorrelationSimilarity(dataModel);
			} else if (similarityMethod == SPEARMAN) {
				similarity = new SpearmanCorrelationSimilarity(dataModel);
			} else {
				similarity = new EuclideanDistanceSimilarity(dataModel);
			}

			UserNeighborhood neighborhood = new NearestNUserNeighborhood(neighbors, similarity, dataModel);

			// Instantiate the recommender

			recommender = new GenericUserBasedRecommender(dataModel,neighborhood, similarity);

			//recommenderGBIR = new GenericBooleanPrefItemBasedRecommender(dataModel, new LogLikelihoodSimilarity(dataModel));

			System.out.println("Getting the recommendations...");

			List<RecommendedItem> recommendations = recommendThings(userID, numberOfRecommendations);
			
			if(recommendations.size()==0)
				System.out.println("No recommendations");

			// List<String> recommendationsBooleanPref =
			// booleanRecommendThings(user.user_id, numberOfRecommendations);

			System.out.println("USER-BASED");
			for (RecommendedItem rec : recommendations) {
				System.out.println("Got recommendations..."+recommendations.size());
				
				Business found = Business.find.byId(thing2long.toStringID(rec.getItemID()));
				if(found!=null){
					System.out.println("Found business..."+found.getName()+" "+found.getBusiness_id());
				
					Recommendation recom = new Recommendation(found, rec.getValue());
					result.add(recom);
				}
			}
			System.out.println("All collaborative recommendations added");
			return result;

		} catch (TasteException e) {
			System.out.println("Exception: " + e.getClass() + ": "
					+ e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Exception: " + e.getClass() + ": "
					+ e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Exception: " + e.getClass() + ": "
					+ e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns up to 10 recommendations for a certain person as a string array.
	 * If less then 10 things are found the array will contain less elements. If
	 * no recommendations are found the array will contain 0 elements.
	 * 
	 * @return a string array with recommendations
	 * @throws TasteException
	 *             If anything goes wrong a TasteException is thrown
	 */
	public static List<RecommendedItem> recommendThings(String user_id,int numberOfRecommendations) throws TasteException
	{
        long t1 = System.currentTimeMillis();
		// List<String> recommendations = new ArrayList<String>();
		List<RecommendedItem> items = null;
		try {
			items = recommender.recommend(thing2long.toLongID(user_id), numberOfRecommendations);
//			 for (RecommendedItem item : items) {
//				 recommendations.add(thing2long.toStringID(item.getItemID()));
//			 }
		} catch (TasteException e) {
			throw e;
		}
        System.out.println("\n ATTENTION!! getting geolocation filter taked "+(System.currentTimeMillis()-t1)+"ms\n\n");
		return items;
	}

	public static List<String> booleanRecommendThings(String user_id,
			int numberOfRecommendations) throws TasteException {
		List<String> recommendations = new ArrayList<String>();
		try {
			List<RecommendedItem> items = recommenderGBIR.recommend(
					thing2long.toLongID(user_id), numberOfRecommendations);
			for (RecommendedItem item : items) {
				recommendations.add(thing2long.toStringID(item.getItemID()));
			}
		} catch (TasteException e) {
			throw e;
		}
		return recommendations;
	}

	public static CollaborativeRecommender getInstance() {
		if (instance == null) {
			instance = new CollaborativeRecommender();
			//CollaborativeRecommender.generateDataModel();
		}
		return instance;
	}
}
