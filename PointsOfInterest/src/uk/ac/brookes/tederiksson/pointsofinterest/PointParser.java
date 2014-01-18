package uk.ac.brookes.tederiksson.pointsofinterest;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.util.Log;

public class PointParser {

	protected PointParser() {
		
	}
	
	private static String getDataString(String selection) {
		HttpClient httpClient = new DefaultHttpClient();
		Log.d("Point Parser", PoiAPIHelper.API_BASE_NAME+PoiAPIHelper.POINTS+selection);
		HttpGet get = new HttpGet(PoiAPIHelper.API_BASE_NAME+PoiAPIHelper.POINTS+selection);

		HttpResponse response;
		String data = null;
		try {
			response = httpClient.execute(get);
			data = EntityUtils.toString(response.getEntity());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return data;
	}
	
	public static Point getPointsById(int id) {
		String data = getDataString(Integer.toString(id));
		if(data==null) return null;
		try {
			JSONObject pointsJSONObject = new JSONObject(data);
			
			JSONObject jsonObject = pointsJSONObject.getJSONArray("points").getJSONObject(0);
			JSONArray jsonParts = jsonObject.getJSONArray(PoiAPIHelper.POINTS_PARTS);
			ArrayList<PoiPart> parts = jsonArrayToPoiPart(jsonParts);
			Log.d("Point Parser", data);
			return new Point(1,jsonObject.getString(PoiAPIHelper.POINTS_NAME),jsonObject.getString(PoiAPIHelper.POINTS_MESSAGE),
					Double.parseDouble(jsonObject.getString(PoiAPIHelper.POINTS_LNG)),Double.parseDouble(jsonObject.getString(PoiAPIHelper.POINTS_LAT)),parts);
		} catch(JSONException ex) {
			ex.printStackTrace();
			Log.e("Point Parser", "Json object failed");
		}
		return null;
		
	}
	
	public static ArrayList<PoiPart> jsonArrayToPoiPart(JSONArray jsonParts) {
		ArrayList<PoiPart> parts = new ArrayList<PoiPart>();
		for (int i = 0; i < jsonParts.length(); i++) {
			try {
				JSONObject jsonPart = jsonParts.getJSONObject(i);
				parts.add(new PoiPart(jsonPart.getInt(PoiAPIHelper.POINTS_PARTS_ID), jsonPart.getString(PoiAPIHelper.POINTS_PARTS_MESSAGE),
						jsonPart.getDouble(PoiAPIHelper.POINTS_PARTS_X), jsonPart.getDouble(PoiAPIHelper.POINTS_PARTS_Y), 
						jsonPart.getDouble(PoiAPIHelper.POINTS_PARTS_Z)));
			} catch (JSONException e) {
				e.printStackTrace();
				Log.e("Point Parser", "Json object (parts) failed");
			}
		}
		return parts;
	}
	
	public static ArrayList<Point> getPointsByLocation(LatLng latLng, int radius) {
		String data = getDataString("?clat="+latLng.latitude+"&clng="+latLng.longitude+"&rad="+Integer.toString(radius));
		if(data==null) return null;
		Log.d("returned", data);
		ArrayList<Point> points = new ArrayList<Point>();
		try {
			JSONObject pointsJSONObject = new JSONObject(data);
			JSONArray jsonArray = pointsJSONObject.getJSONArray("points");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				points.add(new Point(jsonObject.getInt(PoiAPIHelper.POINTS_ID),jsonObject.getString(PoiAPIHelper.POINTS_NAME),jsonObject.getString(PoiAPIHelper.POINTS_MESSAGE),
						Double.parseDouble(jsonObject.getString(PoiAPIHelper.POINTS_LNG)),Double.parseDouble(jsonObject.getString(PoiAPIHelper.POINTS_LAT)),null));
			}
		} catch(JSONException ex) {
			Log.e("Point Parser", "Json object failed");
		}
		return points;
		
	}

}