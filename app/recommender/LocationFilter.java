package recommender;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;


import models.Business;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LocationFilter {

	public final static String URL_BASE_MAP_GPS_ST_INTERSECTS = "https://www.googleapis.com/fusiontables/v1/query?sql=SELECT%20business_id,name%20FROM%201WtuOzZqOYiiMQ24qapSyzz3AHaYl6hfoh3J7oLKn%20WHERE%20ST_INTERSECTS(Location,CIRCLE(LATLNG(";
	public final static String MAP_GPS_KEY = "&key=AIzaSyCGl5NEl5viaCeaUR5lIK2hgJCYDaXtbo4";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		obtenerSitiosCercanos(40.35762, -80.05998, 2000);
	}

	public static ArrayList<Business> obtenerSitiosCercanos(double latitud,
			double longitud, int radioM) {
        long t1=System.currentTimeMillis();
		try {
			ArrayList<Business> id_collection = new ArrayList<Business>();
			String url = URL_BASE_MAP_GPS_ST_INTERSECTS + latitud + "," + longitud + "),"
					+ radioM + "))"+MAP_GPS_KEY;
			JSONObject json = receiveJSON(url);

			String rta = null;
			Business agregar;
			JSONArray ids = json.getJSONArray("rows");
			for (int i = 0; i < ids.length(); i++) {
				JSONArray rtaArray = ids.getJSONArray(i);
				rta = rtaArray.getString(0);
				rta = rta.replace("[", "");
				rta = rta.replace("]", "");
				rta = rta.replace("\"", "");
				//String[] campos = rta.split(",");
				
				agregar = new Business();
				agregar.setBusiness_id(rta);
				agregar.setName(rtaArray.getString(1));
				id_collection.add(agregar);
				
				//System.out.println(rta);
			}
            System.out.println("\n ATTENTION!! getting geolocation filter taked "+(System.currentTimeMillis()-t1)+"ms\n\n");

			return id_collection;
		} catch (JSONException e) {
			System.out.println("Error in JSON response " + e.getClass()
					+ e.getMessage());
			e.printStackTrace();
			return new ArrayList<Business>();
		}
    }

	public static JSONObject receiveJSON(String url) {
		BufferedReader in = null;
		String textv = "";

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			URI website = new URI(url);
			request.setURI(website);
			HttpResponse response = httpclient.execute(request);

			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));

			String line;

			while ((line = in.readLine()) != null) {
				textv += line;
			}
			//System.out.println(textv);
			return new JSONObject(textv);
		} catch (Exception e) {
			System.out.println("Error in http connection " + e.getClass()
					+ e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
}
