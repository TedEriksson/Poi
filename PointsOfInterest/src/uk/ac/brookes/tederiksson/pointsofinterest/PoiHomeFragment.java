package uk.ac.brookes.tederiksson.pointsofinterest;

import java.util.ArrayList;
import java.util.zip.Inflater;

import com.google.android.gms.maps.model.LatLng;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PoiHomeFragment extends Fragment {
	
	private LinearLayout nearYou;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().setTitle(getResources().getStringArray(R.array.fragment_titles)[0]);
		return inflater.inflate(R.layout.fragment_home, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		nearYou = (LinearLayout) getActivity().findViewById(R.id.linearLayoutNearYou);
		LoadNearby loadNearby = new LoadNearby();
		loadNearby.execute(100000);
	}
	
	private class LoadNearby extends AsyncTask<Integer, Void, ArrayList<Point>> {

		@Override
		protected ArrayList<Point> doInBackground(Integer... radius) {
			return PointParser.getPointsByLocation(((MainActivity)getActivity()).getGPSLocation(), radius[0]);
		}

		@Override
		protected void onPostExecute(ArrayList<Point> results) {
			super.onPostExecute(results);
			Context context = getActivity();
			
			nearYou.removeAllViews();
			
			for(Point result : results) {
				
				NearbyView slot = new NearbyView(context);
				slot.setNearbyName(result.getName());
				slot.setNearbyDistance(String.format("%.2f",distance(((MainActivity)getActivity()).getGPSLocation(), result.getLatLng())/1000)+"km");
				nearYou.addView(slot);
			}
		}
		
	}
	
	private class NearbyView extends RelativeLayout {

		public NearbyView(Context context) {
			super(context);
			initView();
		}
		
		private void initView() {
			View view = inflate(getContext(), R.layout.nearby_item, null);
			addView(view);
		}
		
		public void setNearbyName(String text) {
			TextView name = (TextView) findViewById(R.id.textViewNearbyName);
			name.setText(text);
		}
		
		public void setNearbyDistance(String text) {
			TextView distance = (TextView) findViewById(R.id.textViewNearbyDistance);
			distance.setText(text);
		}
		
	}
	
	public static double distance(LatLng StartP, LatLng EndP) {
	    double lat1 = StartP.latitude;
	    double lat2 = EndP.latitude;
	    double lon1 = StartP.longitude;
	    double lon2 = EndP.longitude;
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLon = Math.toRadians(lon2-lon1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	    Math.sin(dLon/2) * Math.sin(dLon/2);
	    double c = 2 * Math.asin(Math.sqrt(a));
	    return 6366000 * c;
	}

}
