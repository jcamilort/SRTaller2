package evaluator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.MemoryIDMigrator;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class CollaborativeEvaluator {

	protected static final int neighbourhoodSize = 100;
	public static RecommenderEvaluator evaluador1;
	public static RecommenderEvaluator evaluador2 = new AverageAbsoluteDifferenceRecommenderEvaluator();
	private static String rutaReviewInfo = "./data/reviews_info.txt";
	
	private static GenericDataModel dataModel;
	/**
	 * An MemoryIDMigrator which is able to create for every string a long
	 * representation. Further it can store the string which were put in and it
	 * is possible to do the mapping back.
	 */
	private static MemoryIDMigrator thing2long = new MemoryIDMigrator();

	public static void main(String[] args) {
		evaluador1 = new RMSRecommenderEvaluator();
		createDataModel();
		evaluateUserBased(dataModel);
	}

	private static void createDataModel() {
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
			System.out.println("DataModel Created");
			System.out.println("Numero de Usuarios: " + dataModel.getNumUsers()
					+ "Numero de Items: " + dataModel.getNumItems());
		} catch (IOException e) {
			System.out.println("Exception: " + e.getClass() + ": "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	public static void evaluateUserBased(DataModel dataModel) {
		try {
			System.out.println("Going to Evaluate the recommender...");
			RecommenderBuilder userSimRecBuilder = new RecommenderBuilder() {

				@Override
				public Recommender buildRecommender(DataModel model)
						throws TasteException {
					// The Similarity algorithms used in your recommender
					UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(model);

					/*
					 * The Neighborhood algorithms used in your recommender not
					 * required if you are evaluating your item based
					 * recommendations
					 */
					UserNeighborhood neighborhood = new NearestNUserNeighborhood(neighbourhoodSize, userSimilarity, model);

					// Recommender used in your real time implementation
					Recommender recommender = new GenericUserBasedRecommender(model, neighborhood, userSimilarity);
					return recommender;
				}
			};
			System.out.println("Before Evaluation...");
			double userSimEval2 = evaluador2.evaluate(userSimRecBuilder, null, dataModel, 0.7, 1);
			System.out.println("User Similarity Evaluation score 2: "+ userSimEval2);
			double userSimEvaluationScore = evaluador1.evaluate(userSimRecBuilder,	null, dataModel, 0.7, 1.0);
			System.out.println("User Similarity Evaluation score : "+ userSimEvaluationScore);
		} catch (TasteException e) {
			System.out.println("Exception: " + e.getClass() + ": "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

}
