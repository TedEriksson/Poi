package uk.ac.brookes.tederiksson.pointsofinterest;

import com.google.android.gms.maps.model.LatLng;

public class Point {
	private int id;
	private String name, message;
	private LatLng latLng;
	
	public Point(int id, String name, String message, double lng, double lat) {
		this.id = id;
		this.name = name;
		this.message = message;
		this.latLng = new LatLng(lat, lng);
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
	
	
}
