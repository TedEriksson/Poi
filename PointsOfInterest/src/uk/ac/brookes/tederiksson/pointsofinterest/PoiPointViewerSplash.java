package uk.ac.brookes.tederiksson.pointsofinterest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;

public class PoiPointViewerSplash extends Activity {
	
	private ProgressDialog dialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.fragment_home);
	    dialog = new ProgressDialog(this);
	    dialog.setMessage("Loading Point");
	    dialog.show();
	    processIntent(); 
	}
	
	private void processIntent() {
		Intent intent = getIntent();
		int id = -1;
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
	        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
	                NfcAdapter.EXTRA_NDEF_MESSAGES);
	        // only one message sent during the beam
	        NdefMessage msg = (NdefMessage) rawMsgs[0];
	        // record 0 contains the MIME type, record 1 is the AAR, if present
	        String poiString = new String(msg.getRecords()[0].getPayload());
	        String[] poiStringSplit = poiString.split("pointsofinterest.info/points/", 2);
	        poiStringSplit = poiStringSplit[1].split("/",1);
	        id = Integer.parseInt(poiStringSplit[0]);
		} else {
			id = intent.getIntExtra("id", -1);
		} 
        if(id != -1) {
        	LoadPoint loadPoint = new LoadPoint();
			loadPoint.execute(id);
        } else {
        	showToast("Id is -1");
        }
	}
	
	public void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
	
	private class LoadPoint extends AsyncTask<Integer, Void, Point> {

		@Override
		protected void onPostExecute(Point result) {
			super.onPostExecute(result);
			Intent intent = new Intent(getApplicationContext(), PoiPointViewer.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("point", result);
			intent.putExtras(bundle);
			startActivity(intent);
			dialog.dismiss();
		}

		@Override
		protected Point doInBackground(Integer... params) {
			return PointParser.getPointsById(params[0]);
		}
		
	}

}
