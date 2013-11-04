package uk.ac.brookes.tederiksson.pointsofinterest;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;


//Temporary Class: Placeholder for augmented reality
public class ViewPoint extends Activity {
	
	TextView id, loading, name, lng, lat, message;
	int pointid;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
	    setContentView(R.layout.activity_view_point);
	    
	    loading = (TextView) findViewById(R.id.loading);
	    id = (TextView) findViewById(R.id.pointid);
	    name = (TextView) findViewById(R.id.pointName);
	    lng = (TextView) findViewById(R.id.pointLng);
	    lat = (TextView) findViewById(R.id.pointLat);
	    message = (TextView) findViewById(R.id.pointMsg);
	    
	    Intent intent = getIntent();
	    pointid = intent.getIntExtra("id", 0);
	    id.setText(Integer.toString(pointid));
	    GetPoint getPointTask = new GetPoint();
	    getPointTask.execute(pointid);
	    
	}
	
	private class GetPoint extends AsyncTask<Integer, Void, Point> {

		@Override
		protected void onPostExecute(Point result) {
			super.onPostExecute(result);
			name.setText(result.getName());
			message.setText(result.getMessage());
			lng.setText(Double.toString(result.getLatLng().longitude));
			lat.setText(Double.toString(result.getLatLng().latitude));
			
			loading.setText("Done!");
		}

		@Override
		protected Point doInBackground(Integer... params) {
			loading.setText("Loading..");
			PointParser parser = new PointParser();
		    return parser.getPointsById(pointid);
		}
		
	}

}
