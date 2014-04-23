package uk.ac.brookes.tederiksson.pointsofinterest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
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
		HttpGet get = new HttpGet(PoiAPIHelper.API_BASE_NAME+selection);

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
		String data = getDataString(PoiAPIHelper.POINTS+Integer.toString(id));
		if(data==null) return null;
		try {
			JSONObject pointsJSONObject = new JSONObject(data);
			
			JSONObject jsonObject = pointsJSONObject.getJSONArray("points").getJSONObject(0);
			JSONArray jsonParts = jsonObject.getJSONArray(PoiAPIHelper.POINTS_PARTS);
			ArrayList<PoiPart> parts = jsonArrayToPoiPart(jsonParts);
			Log.d("Point Parser", data);
			return new Point(1,jsonObject.getString(PoiAPIHelper.POINTS_NAME),jsonObject.getString(PoiAPIHelper.POINTS_MESSAGE),
					Float.parseFloat(jsonObject.getString(PoiAPIHelper.POINTS_LNG)),Float.parseFloat(jsonObject.getString(PoiAPIHelper.POINTS_LAT)),parts,PoiAPIHelper.POINTS_OWNER_ID);
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
						(float) jsonPart.getDouble(PoiAPIHelper.POINTS_PARTS_X), (float) jsonPart.getDouble(PoiAPIHelper.POINTS_PARTS_Y), 
						(float) jsonPart.getDouble(PoiAPIHelper.POINTS_PARTS_Z)));
			} catch (JSONException e) {
				e.printStackTrace();
				Log.e("Point Parser", "Json object (parts) failed");
			}
		}
		return parts;
	}
	
	public static ArrayList<Point> getPointsByUser(String user) {
		String data = getDataString(PoiAPIHelper.USERS+user+"/");
		if(data==null) return null;
		Log.d("returned", data);
		ArrayList<Point> points = new ArrayList<Point>();
		try {
			JSONObject pointsJSONObject = new JSONObject(data);
			JSONArray jsonArray = pointsJSONObject.getJSONArray("points");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				points.add(new Point(jsonObject.getInt(PoiAPIHelper.POINTS_ID),jsonObject.getString(PoiAPIHelper.POINTS_NAME),jsonObject.getString(PoiAPIHelper.POINTS_MESSAGE),
						Float.parseFloat(jsonObject.getString(PoiAPIHelper.POINTS_LNG)),Float.parseFloat(jsonObject.getString(PoiAPIHelper.POINTS_LAT)),null,PoiAPIHelper.POINTS_OWNER_ID));
			}
		} catch(JSONException ex) {
			Log.e("Point Parser", "Json object failed");
		}
		return points;
	}
	
	public static ArrayList<Point> getPointsByLocation(LatLng latLng, int radius) {
		String data = getDataString(PoiAPIHelper.POINTS+"?latitude="+latLng.latitude+"&longitude="+latLng.longitude+"&radius="+Integer.toString(radius));
		if(data==null) return null;
		Log.d("returned", data);
		ArrayList<Point> points = new ArrayList<Point>();
		try {
			JSONObject pointsJSONObject = new JSONObject(data);
			JSONArray jsonArray = pointsJSONObject.getJSONArray("points");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				points.add(new Point(jsonObject.getInt(PoiAPIHelper.POINTS_ID),jsonObject.getString(PoiAPIHelper.POINTS_NAME),jsonObject.getString(PoiAPIHelper.POINTS_MESSAGE),
						Float.parseFloat(jsonObject.getString(PoiAPIHelper.POINTS_LNG)),Float.parseFloat(jsonObject.getString(PoiAPIHelper.POINTS_LAT)),null,PoiAPIHelper.POINTS_OWNER_ID));
			}
		} catch(JSONException ex) {
			Log.e("Point Parser", "Json object failed");
		}
		return points;
		
	}
	
	public static JSONObject encapsulatePoints(ArrayList<Point> points) {
		JSONObject mainObject = new JSONObject();
		JSONArray pointsArray = new JSONArray();
		
		return mainObject;
	}
	
	public static boolean insertPoint(Point point, String token) {
		if (token == null) return false;
		JSONObject json = point.toJSON();
		try {
			json.put("access_token", token);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		json.remove("point_id");

		Log.d("POINT OUT", json.toString());
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(PoiAPIHelper.API_BASE_NAME+PoiAPIHelper.POINTS);
		try {
			StringEntity se = new StringEntity(json.toString());
			httpPost.setEntity(se);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	    httpPost.setHeader("Content-type", "application/json");
	    try {
			HttpResponse response = httpclient.execute(httpPost);
			if(response.getStatusLine().getStatusCode() == 200) {
				return true;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return false;
	}

}