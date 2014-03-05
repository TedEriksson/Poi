package uk.ac.brookes.tederiksson.pointsofinterest;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class PoiPart implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4862453600761412990L;
	private int id;
	private String message;
	public float x,y,z;
	
	public PoiPart(int id, String message, float x, float y, float z) {
		super();
		this.id = id;
		this.message = message;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return "ID: "+id+", Message: "+message+", X: "+x+", Y: "+y+", Z: "+z;
	}
	
	public JSONObject toJSON() {
		JSONObject obj = new JSONObject();
		try {
			obj.put(PoiAPIHelper.POINTS_PARTS_ID, id);
			obj.put(PoiAPIHelper.POINTS_PARTS_MESSAGE, message);
			obj.put(PoiAPIHelper.POINTS_PARTS_X, x);
			obj.put(PoiAPIHelper.POINTS_PARTS_Y, y);
			obj.put(PoiAPIHelper.POINTS_PARTS_Z, z);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}
	
}
