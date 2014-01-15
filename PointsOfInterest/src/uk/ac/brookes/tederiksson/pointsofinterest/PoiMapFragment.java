package uk.ac.brookes.tederiksson.pointsofinterest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PoiMapFragment extends MapFragment {
	GoogleMap map;
	
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
		setUpMapIfNeeded();
		map.addMarker(new MarkerOptions().position(new LatLng(55, 3)));
	}

	private void setUpMapIfNeeded() {
		if(map==null) {	
			map = getMap();
		} else {
			showToast("AHH No Map!");
		}
	}
	
	public void showToast(String text) {
		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
	}
	
}
