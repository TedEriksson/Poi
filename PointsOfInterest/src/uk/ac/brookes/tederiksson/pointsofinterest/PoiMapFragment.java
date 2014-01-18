package uk.ac.brookes.tederiksson.pointsofinterest;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class PoiMapFragment extends MapFragment {
	private GoogleMap map;
	private LatLng myLocation;
	private int radius = 10000;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private ProgressDialog progressDialog;
	private HashMap<String, Integer> hashMap;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().setTitle(getResources().getStringArray(R.array.fragment_titles)[1]);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onResume() {
		
		super.onResume();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		hashMap = new HashMap<String, Integer>();
		
		setUpMapIfNeeded();
		
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setTitle("Finding your location");
		progressDialog.show();
		
		locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		
		locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		    	myLocation = new LatLng(location.getLatitude(), location.getLongitude());
		    	loadMarkers();
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		  };

		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			
			@Override
			public void onInfoWindowClick(Marker marker) {
				showToast(Integer.toString(hashMap.get(marker.getId())));
			}
		});
		
	}
	
	public void loadMarkers() {
		radius = getActivity().getSharedPreferences("poiprefs", Activity.MODE_PRIVATE).getInt("radius", 10);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
		AddMarkers addMarkers = new AddMarkers();
		addMarkers.execute();
		locationManager.removeUpdates(locationListener);
	}

	private void setUpMapIfNeeded() {
		if(map==null) {	
			map = getMap();
			map.setMyLocationEnabled(true);
		}
	}
	
	public void showToast(String text) {
		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
	}
	
	private class AddMarkers extends AsyncTask<Void, Void, ArrayList<Point>> {

		@Override
		protected ArrayList<Point> doInBackground(Void... params) {
			return PointParser.getPointsByLocation(myLocation, radius);
		}

		@Override
		protected void onPostExecute(ArrayList<Point> result) {
			super.onPostExecute(result);
			for (Point point : result) {
				MarkerOptions mO = new MarkerOptions();
				mO	.position(point.getLatLng())
					.title(point.getName())
					.snippet(point.getMessage());
				Marker m = map.addMarker(mO);
				hashMap.put(m.getId(), point.getId());
			}
			progressDialog.dismiss();
		}
		
	}
	
}
