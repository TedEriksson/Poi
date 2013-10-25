package uk.ac.brookes.tederiksson.pointsofinterest;

public class Point {
	private int id;
	private String name, message;
	private float lng, lat;
	
	public Point(int id, String name, String message, float lng, float lat) {
		this.id = id;
		this.name = name;
		this.message = message;
		this.lng = lng;
		this.lat = lat;
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

	public float getLng() {
		return lng;
	}

	public void setLng(float lng) {
		this.lng = lng;
	}

	public float getLat() {
		return lat;
	}

	public void setLat(float lat) {
		this.lat = lat;
	}

	public int getId() {
		return id;
	}
	
	
}
