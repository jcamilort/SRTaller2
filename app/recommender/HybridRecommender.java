package recommender;

import models.Business;
import models.Recommendation;
import models.User;

import java.util.ArrayList;

/**
 * Created by carol on 8/04/15.
 */
public class HybridRecommender {

    private static final int RADIO_FILTRO = 25000;
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
    	contenido =  ContentRecommender.getInstance();
    }

    public static ArrayList<Recommendation> recommend(double[] latlong,String hour, User user,String[] categories,String[] attributes)
    {
    	//TODO filtrar por posicion y horas
    	ArrayList<Business> cercanos = LocationFilter.obtenerSitiosCercanos(latlong[0], latlong[1], RADIO_FILTRO);
    	//colaborativo.generateDataModelPositionBased(cercanos);
    	colaborativo.generateDataModel();
    	
//    	contenido.setFilteredBusinessGeo(cercanos);
    	
    	//TODO generate the new dataModels
    	ArrayList<Recommendation> collabRecs = getCollaborativeRecommendations( latlong,hour,  user.user_id, categories,attributes);
    	ArrayList<Recommendation> contentRecs = getContentRecommendations( latlong,hour,  user, categories,attributes);
        
    	ArrayList<Recommendation> finalRecs = new ArrayList<Recommendation>();
    	int ultimaPos=collabRecs.size()+contentRecs.size();
    	
    	//Los llena de nulos para 'reservar' el espacio y poder acceder a las posiciones especificas
    	for( int i = 0 ; i<ultimaPos ; i++)
    	{
    		finalRecs.add(null);
    	}
    	
    	int posIntro;
    	for( int i = 0 ; i < collabRecs.size(); i ++)
    	{
    		Recommendation collab = collabRecs.get(i);
    		
    		boolean termino = false;
    		for( int j = 0 ; j<contentRecs.size() && !termino ; j ++){
    			Recommendation content = contentRecs.get(j);
    			
    			if(collab.getBusiness().business_id.equals(content.getBusiness().business_id)){
    				//Promedio de posicion entre las dos listas
    				posIntro = (i+j)/2;
    				boolean intro = introducirAntes(collab, posIntro, finalRecs);
    				if(!intro){
    					introducirDespues(collab, posIntro, finalRecs);
    				}
    				termino = true;
    			}
    		}
    		if(!termino)
    			finalRecs.add(collab);
    	}
    	for(int i=finalRecs.size()-1 ; i>=0; i--){
    		if(finalRecs.get(i)==null){
    			finalRecs.remove(i);
    			System.out.println("NULL RECOMMENDATION REMOVED");
    		}
    	}
    	for(int i = 0; i< finalRecs.size() ; i++){
    		System.out.println(finalRecs.get(i).business.name);
    	}
    	return finalRecs;
        //return collabRecs;
    }


	private static boolean introducirAntes(Recommendation rec, int posIntro, ArrayList<Recommendation> finalRecs) {
		boolean termino = false;
		boolean respuesta = false;
		
		if(posIntro<0)
			respuesta = false;
		else if(finalRecs.get(posIntro)==null){
			finalRecs.set(posIntro, rec);
			System.out.println("Added Antes: "+rec.business.name+" en "+posIntro);
			respuesta = true;
		}
		else{
			termino = introducirAntes(finalRecs.get(posIntro), posIntro-1, finalRecs);
			if(termino){
				finalRecs.set(posIntro, rec);
				System.out.println("Added Antes 2: "+rec.business.name+" en "+posIntro);
				respuesta = true;
			}
			else
				respuesta = false;
		}
    	return respuesta;
	}
    
    private static boolean introducirDespues(Recommendation rec, int posIntro, ArrayList<Recommendation> finalRecs) {
		try{
			finalRecs.add(posIntro, rec);
			System.out.println("Added Despues: "+rec.business.name+" en "+posIntro);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
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
