package recommender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import models.User;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.MemoryIDMigrator;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
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
	private static DataModel dataModel;

	private static GenericBooleanPrefItemBasedRecommender recommenderGBIR;

	/**
	 * // * This function will init the recommender // * it will load the CSV
	 * file from the resource folder, // * parse it and create the necessary
	 * data structures // * to create a recommender. // * The //
	 */
	// @PostConstruct
	// public void initRecommender() {
	//
	// try {
	//
	// } catch (FileNotFoundException e) {
	// System.out.println(DATA_FILE_NAME+" was not found"+ e.getMessage());
	// e.printStackTrace();
	// } catch (IOException e) {
	// System.out.println("Error during reading line of file"+ e.getMessage());
	// e.printStackTrace();
	// }
	// }

	public static final int PEARSON = 1;
	public static final int SPEARMAN = 2;
	public static final int EUCLIDEAN = 3;

	
	 public static final long user_id_test = 5;
	
	 public static void main(String[] args)
	 {
	 User user = new User();
	 user.setUser_id("6TPxhpHqFedjMvBuw6pF3w");
	 executeRecommender(user, 100, 100, PEARSON);
	 }
	 
	public static List<RecommendedItem> executeRecommender(User user,
			int numberOfRecommendations, int neighbors,
			int similarityMethod) {
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
				float preference = Float.parseFloat(parts[2].trim());

				// other lines contained but not used
				// String category = line[2];
				// String id = line[3];
				// String created_time = line[4];

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
				userPrefList.add(new GenericPreference(userLong, itemLong, preference));
				System.out.println("Adding " + person + "(" + userLong+ ") to " + likeName + "(" + itemLong + ") with preference "+preference);
			}

			// create the corresponding mahout data structure from the map
			FastByIDMap<PreferenceArray> preferecesOfUsersFastMap = new FastByIDMap<PreferenceArray>();
			for (Entry<Long, List<Preference>> entry : preferecesOfUsers.entrySet()) {
				preferecesOfUsersFastMap.put(entry.getKey(),new GenericUserPreferenceArray(entry.getValue()));
			}

			// create a data model
			dataModel = new GenericDataModel(preferecesOfUsersFastMap);
			System.out.println("Numero de Usuarios: "+dataModel.getNumUsers()+"Numero de Items: "+dataModel.getNumItems());
			
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

			recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
			
			recommenderGBIR = new GenericBooleanPrefItemBasedRecommender(dataModel, new LogLikelihoodSimilarity(dataModel));

			System.out.println("Getting the recommendations...");
			
			List<String> recommendations = recommendThings(user.user_id, numberOfRecommendations);

			List<String> recommendationsBooleanPref = booleanRecommendThings(user.user_id, numberOfRecommendations);
			
			System.out.println("USER-BASED");
			for( String rec : recommendations){
				System.out.println(rec);
			}

			System.out.println("BOOLEAN PREFERENCES");
			
			for( String rec : recommendationsBooleanPref){
				System.out.println(rec);
			}
			
			
		} catch (TasteException e) {
			System.out.println("Exception: " + e.getClass() + ": "
					+ e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
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
	public static List<String> recommendThings(String user_id, int numberOfRecommendations) throws TasteException {
		List<String> recommendations = new ArrayList<String>();
		try {
			List<RecommendedItem> items = recommender.recommend(thing2long.toLongID(user_id), numberOfRecommendations);
			for (RecommendedItem item : items) {
				recommendations.add(thing2long.toStringID(item.getItemID()));
			}
		} catch (TasteException e) {
			throw e;
		}
		return recommendations;
	}
	
	public static List<String> booleanRecommendThings(String user_id, int numberOfRecommendations) throws TasteException {
		List<String> recommendations = new ArrayList<String>();
		try {
			List<RecommendedItem> items = recommenderGBIR.recommend(thing2long.toLongID(user_id), numberOfRecommendations);
			for (RecommendedItem item : items) {
				recommendations.add(thing2long.toStringID(item.getItemID()));
			}
		} catch (TasteException e) {
			throw e;
		}
		return recommendations;
	}
}
