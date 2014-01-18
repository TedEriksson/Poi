package uk.ac.brookes.tederiksson.pointsofinterest;

import java.io.Serializable;

public class PoiPart implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4862453600761412990L;
	private int id;
	private String message;
	public Double x,y,z;
	
	public PoiPart(int id, String message, Double x, Double y, Double z) {
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
	
}
