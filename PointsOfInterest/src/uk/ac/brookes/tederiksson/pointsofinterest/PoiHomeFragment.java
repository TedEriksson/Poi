package uk.ac.brookes.tederiksson.pointsofinterest;

import java.util.ArrayList;
import java.util.zip.Inflater;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PoiHomeFragment extends Fragment {
	
	private LinearLayout nearYou;
	private SharedPreferences prefs = null;
	private LoadNearby loadNearby;

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
		loadNearby = new LoadNearby();
		prefs = getActivity().getSharedPreferences("poiprefs", Activity.MODE_PRIVATE);
		loadNearby.execute(prefs.getInt("radius", 10));
	}
	
	
	@Override
	public void onPause() {
		super.onPause();
		if(loadNearby != null) loadNearby.cancel(true);
	}



	private class LoadNearby extends AsyncTask<Integer, Void, ArrayList<Point>> {

		@Override
		protected ArrayList<Point> doInBackground(Integer... radius) {
			return PointParser.getPointsByLocation(((MainActivity)getActivity()).getGPSLocation(), radius[0]);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			nearYou.removeAllViews();
			Context context = getActivity();
			NearbyView loading = new NearbyView(context);
			loading.setNearbyName("Loading Points");
			nearYou.addView(loading);
			
		}

		@Override
		protected void onPostExecute(ArrayList<Point> results) {
			super.onPostExecute(results);
			Context context = getActivity();
			
			nearYou.removeAllViews();
		
			if(results == null) {
				NearbyView fail = new NearbyView(context);
				fail.setNearbyName("Failed to get Points. Tap to try again.");
				nearYou.addView(fail);
				fail.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						loadNearby = new LoadNearby();
						if(prefs != null) loadNearby.execute(prefs.getInt("radius", 10));
					}
				});
			} else {
				if(results.size() == 0) {
					NearbyView fail = new NearbyView(context);
					fail.setNearbyName("No Points Near by. Tap to Increase the search radius.");
					nearYou.addView(fail);
					fail.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							MapSearchDialog dialog = new MapSearchDialog() {

								@Override
								public void onPosClick() {
									super.onPosClick();
									loadNearby = new LoadNearby();
									if(prefs != null) loadNearby.execute(prefs.getInt("radius", 10));
								}

							};
							dialog.show(getFragmentManager(), "mapdialog");
						}
					});
				} else {
					for(final Point result : results) {
						NearbyView slot = new NearbyView(context);
						slot.setNearbyName(result.getName());
						slot.setNearbyDistance(String.format("%.2f",distance(((MainActivity)getActivity()).getGPSLocation(), result.getLatLng())/1000)+"km");
						nearYou.addView(slot);
						slideView(slot,nearYou.getBottom());
						slot.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								((MainActivity)getActivity()).setLastLocation(result.getLatLng());
								((MainActivity)getActivity()).selectItem(1);
							}
						});
					}
				}
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
	
	public void slideView(final View view,int toY){
		TranslateAnimation animate = new TranslateAnimation(0,0,-toY,0);
		animate.setDuration(500);
		animate.setFillAfter(true);
		view.startAnimation(animate);
	}

}
