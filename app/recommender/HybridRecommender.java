package recommender;

import models.Business;
import models.Recommendation;
import models.User;

import java.util.ArrayList;

/**
 * Created by carol on 8/04/15.
 */
public class HybridRecommender {

    private static HybridRecommender instance;
    private static CollaborativeRecommender colaborativo;
    private static ContentRecommender contenido;
    
    public static void main(String [] args)
    {

   	 	User user = new User();
   	 	user.setUser_id("uMKK1Ans4DrUsxlliIH_xA");

   	 	recommend(null, null, user, null, null);
    }
    
    public HybridRecommender()
    {
    	colaborativo = CollaborativeRecommender.getInstance();
    	//contenido =  ContentRecommender.getInstance();
    }

    public static ArrayList<Recommendation> recommend(double[] latlong,String hour, User user,String[] categories,String[] attributes)
    {
    	//TODO filtrar por posicion y horas
    	ArrayList<Business> cercanos = LocationFilter.obtenerSitiosCercanos(latlong[0], latlong[1], 2000);
//    	for(Business b:cercanos){
//    		if(b!=null)
//    		System.out.println(b.getBusiness_id()+" :: "+b.getName());
//    	}
    	
    	//TODO generate the new dataModels
    	ArrayList<Recommendation> collabRecs = getCollaborativeRecommendations( latlong,hour,  user.user_id, categories,attributes);
    	ArrayList<Recommendation> contentRecs = getContentRecommendations( latlong,hour,  user, categories,attributes);
        
    	ArrayList<Recommendation> finalRecs = new ArrayList<Recommendation>();
    	for( int i = 0 ; i<40 ; i++)
    	{
    		finalRecs.add(null);
    	}
    	
    	int posIntro;
    	for( int i = 0 ; i < collabRecs.size(); i ++)
    	{
    		Recommendation collab = collabRecs.get(i);
    		for( int j = 0 ; j<contentRecs.size() ; j ++){
    			Recommendation content = contentRecs.get(j);
    			if(collab.getBusiness().business_id.equals(content.getBusiness().business_id)){
    				posIntro = (i+j)/2;
    				boolean intro = introducirAntes(collab, posIntro, finalRecs);
    				if(!intro){
    					introducirDespues(collab, posIntro, finalRecs);
    				}
    			}
    		}
    	}
    	for(int i=finalRecs.size()-1 ; i>=0; i--){
    		if(finalRecs.get(i)==null){
    			finalRecs.remove(i);
    			System.out.println("NULL RECOMMENDATION REMOVED");
    		}
    	}
    	for(int i = 0; i< finalRecs.size() ; i++){
    		if(finalRecs.get(i)==null)
    			System.out.println("NULO!!! "+i);
    	}
        return finalRecs;
    }


	private static boolean introducirAntes(Recommendation rec, int posIntro, ArrayList<Recommendation> finalRecs) {
		boolean termino = false;
		boolean respuesta = false;
		
    	if(finalRecs.get(posIntro)==null){
    		finalRecs.remove(posIntro);
			finalRecs.set(posIntro, rec);
			respuesta = true;
		}
    	else if(posIntro<0)
    		respuesta = false;
		else{
			termino = introducirAntes(finalRecs.get(posIntro), posIntro-1, finalRecs);
			if(termino){
				finalRecs.set(posIntro, rec);
				respuesta = true;
			}
			else
				respuesta = false;
		}
    	return respuesta;
	}
    
    private static boolean introducirDespues(Recommendation rec, int posIntro, ArrayList<Recommendation> finalRecs) {
		boolean respuesta = false;
		boolean termino = false;
		
		finalRecs.add(posIntro, rec);
		for( int i = posIntro ; i< finalRecs.size() && !termino; i++){
			if(finalRecs.get(posIntro)==null){
				finalRecs.remove(posIntro);
				termino = true;
				respuesta = true;
			}
		}
		if(!termino)
			respuesta = false;
		
    	return respuesta;
	}

	private static ArrayList<Recommendation> getContentRecommendations(double[] latlong, String hour, User user, String[] categories,String[] attributes) {
		ArrayList<Recommendation> returned = new ArrayList<Recommendation>();
    	
        Business bt=new Business();
        bt.setName("un business");
        bt.setBusiness_id("id1");
        returned.add(new Recommendation(bt,4.8));

        bt=new Business();
        bt.setName("otro business");
        bt.setBusiness_id("id2");
        returned.add(new Recommendation(bt,4.8));

        bt=new Business();
        bt.setName("tercer business");
        bt.setBusiness_id("id3");
        returned.add(new Recommendation(bt,4.8));
    	
    	//returned = contenido.recommend(latlong,hour,user,categories,attributes);
    	return returned;
	}

	private static ArrayList<Recommendation> getCollaborativeRecommendations(
			double[] latlong, String hour, String user_id, String[] categories,
			String[] attributes) {
		int neighbors = 10;
		int similarityMethod = CollaborativeRecommender.EUCLIDEAN;

		return colaborativo.executeRecommender(user_id, 20, neighbors, similarityMethod);
		//return colaborativo.executeRecommender("6TPxhpHqFedjMvBuw6pF3w", 20, neighbors, similarityMethod);
	}

	public static EvaluationResult evaluate (double radioLoc,String hour, double trainingPercentage,int evalMethod)
    {

        //TODO
        return new EvaluationResult();
    }

    public static HybridRecommender getInstance() {
        if(instance==null)
            instance=new HybridRecommender();
        return instance;
    }
}
