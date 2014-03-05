package uk.ac.brookes.tederiksson.pointsofinterest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public class Point implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1567931240566650009L;
	private int id;
	private String name, message, ownerId;
	private transient LatLng latLng;
	private ArrayList<PoiPart> parts;
	
	public Point(int id, String name, String message, double lng, double lat, ArrayList<PoiPart> parts, String ownerId) {
		this.id = id;
		this.name = name;
		this.message = message;
		this.ownerId = ownerId;
		this.latLng = new LatLng(lat, lng);
		this.parts = parts;
	}
	
	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeDouble(latLng.latitude);
        out.writeDouble(latLng.longitude);
    }
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        latLng = new LatLng(in.readDouble(), in.readDouble());
    }

	public ArrayList<PoiPart> getParts() {
		return parts;
	}

	public void setParts(ArrayList<PoiPart> parts) {
		this.parts = parts;
	}

	public LatLng getLatLng() {
		return latLng;
	}

	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		String string = "ID: "+id+", Name: "+ name+", Message: "+message+", LatLng: ("+latLng.toString()+"), Parts: (";
		for(PoiPart part : parts) {
			string += " " + part.toString();
		}
		string +=")";
		return string;
	}
	
	public JSONObject toJSON() {
		JSONObject pointObject = new JSONObject();
		
		try {
			pointObject.put(PoiAPIHelper.POINTS_ID, id);
			pointObject.put(PoiAPIHelper.POINTS_NAME, name);
			pointObject.put(PoiAPIHelper.POINTS_MESSAGE, message);
			pointObject.put(PoiAPIHelper.POINTS_LAT, latLng.latitude);
			pointObject.put(PoiAPIHelper.POINTS_LNG, latLng.longitude);
			pointObject.put(PoiAPIHelper.POINTS_OWNER_ID, ownerId);
			JSONArray partsArray = new JSONArray();
			if (parts != null) {
				for(PoiPart part : parts) {
					partsArray.put(part.toJSON());
				}
			}
			pointObject.put(PoiAPIHelper.POINTS_PARTS, partsArray);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return pointObject;
	}
}
