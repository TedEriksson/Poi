package uk.ac.brookes.tederiksson.pointsofinterest;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class PointParser {

	public PointParser() {
		// TODO Auto-generated constructor stub
	}
	
	public Point getPointsById(int id) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet get = new HttpGet(PoiAPIHelper.API_BASE_NAME+PoiAPIHelper.POINTS+id);

		HttpResponse response;
		String data = null;
		try {
			response = httpClient.execute(get);
			data = EntityUtils.toString(response.getEntity());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			JSONArray jsonArray = new JSONArray(data);
			JSONObject jsonObject = jsonArray.getJSONObject(0);
			return new Point(1,jsonObject.getString(PoiAPIHelper.POINTS_NAME),jsonObject.getString(PoiAPIHelper.POINTS_MESSAGE),
					Float.parseFloat(jsonObject.getString(PoiAPIHelper.POINTS_LNG)),Float.parseFloat(jsonObject.getString(PoiAPIHelper.POINTS_LAT)));
		} catch(JSONException ex) {
			Log.e("Point Parser", "Json object failed");
		}
		return null;
		
	}

}