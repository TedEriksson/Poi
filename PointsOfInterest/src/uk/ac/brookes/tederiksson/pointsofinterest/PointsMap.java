package uk.ac.brookes.tederiksson.pointsofinterest;

import java.util.ArrayList;
import java.util.Random;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

public class PointsMap extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener{
	private GoogleMap mMap;
	private Location myLocation;
	private LocationClient locationClient;
	private ArrayList<Point> points = new ArrayList<Point>();
	
	//For debug only
	private void populatePoints(LatLng myLocal, double radius) {
		Random random = new Random();
		for(int i =0;i<10;i++) {
			points.add(new Point(i, "Point"+i, "This is a message for point "+i,myLocal.longitude + (-radius/2)+(random.nextDouble()*radius) , myLocal.latitude + (-radius/2)+(random.nextDouble()*radius)));
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_map);
	    
	    mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	
	    setUpMapIfNeeded();
	    
	    GoogleMapOptions options = new GoogleMapOptions();
	    options.mapType(GoogleMap.MAP_TYPE_NORMAL)
	    .compassEnabled(true)
	    .rotateGesturesEnabled(false)
	    .tiltGesturesEnabled(false);
	   
	    
	    mMap.getUiSettings().setCompassEnabled(false);
	    mMap.getUiSettings().setRotateGesturesEnabled(false);
	    
	    mMap.setMyLocationEnabled(true);
	    locationClient = new LocationClient(this, this, this);
	    locationClient.connect();
	    
	}

	private void setUpMapIfNeeded() {
	    // Do a null check to confirm that we have not already instantiated the map.
	    if (mMap == null) {
	        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
	                            .getMap();
	        // Check if we were successful in obtaining the map.
	        if (mMap != null) {
	            
	        }
	    }
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		myLocation = locationClient.getLastLocation();
		if(myLocation != null) {
		    LatLng myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
		    populatePoints(myLatLng, 0.005);
		    for(Point point : points) {
		    	mMap.addMarker(new MarkerOptions().position(point.getLatLng()).title(point.getName()));
		    }
		    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,15));
		} else {
			Toast toast = Toast.makeText(getApplicationContext(), "Couldn't get location :(", Toast.LENGTH_LONG);
			toast.show();
		}
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
}
