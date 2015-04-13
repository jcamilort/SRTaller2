package evaluator;

import java.util.Iterator;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.DataModelBuilder;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.IDRescorer;

public class HandMadeEvaluator implements RecommenderIRStatsEvaluator{
	public int numItems;
	public double precision;
	public double recall;
	
	
	public  HandMadeEvaluator(DataModel dataModel, double threshold, double porcentajeEval, int maxPrefs){
		try {
			numItems = dataModel.getNumItems();
			Iterator<Long> iter = dataModel.getUserIDs();
			while(iter.hasNext()){
				if (Math.random() < porcentajeEval) {
					Long user_id = iter.next();
					
					FastIDSet prefsID = getRelevantItemsIDs(user_id, maxPrefs, threshold, dataModel);
					Iterator<Long> otherIter = dataModel.getUserIDs();
					while(otherIter.hasNext()){
						
					}
					Iterator<Long> iterPrefs = prefsID.iterator();
				}
			iter.next();
			}
			
		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public FastIDSet getRelevantItemsIDs(long userID,
	                                       int at,
	                                       double relevanceThreshold,
	                                       DataModel dataModel) throws TasteException {
		
	    PreferenceArray prefs = dataModel.getPreferencesFromUser(userID);
	    FastIDSet relevantItemIDs = new FastIDSet(at);
	    prefs.sortByValueReversed();
	    for (int i = 0; i < prefs.length() && relevantItemIDs.size() < at; i++) {
	      if (prefs.getValue(i) >= relevanceThreshold) {
	        relevantItemIDs.add(prefs.getItemID(i));
	      }
	    }
	    return relevantItemIDs;
	  }


	@Override
	public IRStatistics evaluate(RecommenderBuilder arg0,
			DataModelBuilder arg1, DataModel arg2, IDRescorer arg3, int arg4,
			double arg5, double arg6) throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}
}
