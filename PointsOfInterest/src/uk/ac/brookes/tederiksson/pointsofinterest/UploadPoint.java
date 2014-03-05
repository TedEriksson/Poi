package uk.ac.brookes.tederiksson.pointsofinterest;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.plus.Plus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UploadPoint extends Activity {
	
	private Point point;
	private ProgressDialog dia;
	
	private TextView uploadStatus;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.upload_point);
	    
	    uploadStatus = (TextView) findViewById(R.id.textViewUploadStatus);
	    
	    Intent intent = getIntent();
	    point = (Point) intent.getSerializableExtra("point");
	    
	    //Toast.makeText(this, point.toJSON().toString(), Toast.LENGTH_LONG).show();
	    dia = new ProgressDialog(this);
	    dia.setMessage("Uploading Point...");
	    dia.setCancelable(false);
	    dia.setCanceledOnTouchOutside(false);
	    dia.show();
	    Upload upload = new Upload();
	    upload.execute(point);
	}
	
	class Upload extends AsyncTask<Point, Void, Boolean> {

		@Override
		protected void onPostExecute(Boolean result) {
			dia.dismiss();
			if(result) {
				uploadStatus.setText("Upload Successful");
			} else {
				uploadStatus.setText("Upload Failed");
			}
		}

		@Override
		protected Boolean doInBackground(Point... arg0) {
			return PointParser.insertPoint(point, getSharedPreferences("poiprefs", Activity.MODE_PRIVATE).getString("auth", null));
		}
		
	}

	
}
