package recommender;

public class LocationFilter {
	
	public final static String URL_BASE_MAP_GPS = "https://www.googleapis.com/fusiontables/v1/query?sql=SELECT%20name%20FROM%201WtuOzZqOYiiMQ24qapSyzz3AHaYl6hfoh3J7oLKn%20WHERE%20ST_INTERSECTS%28geometry,CIRCLE%28LATLNG%28";
	public final static String MAP_GPS_KEY = "";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	
	}
	public static void obtnerSitiosCercanos(double latitud, double longitud)
	{

		String url = URL_BASE_MAP_GPS+latitud+","+longitud+MAP_GPS_KEY;
	}
}
