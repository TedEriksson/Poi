package uk.ac.brookes.tederiksson.pointsofinterest;

import java.util.ArrayList;

import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.MetaioSDK;
import com.metaio.sdk.jni.Vector3d;
import com.metaio.tools.io.AssetsManager;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateBeamUrisCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

public class PoiPointViewer extends ARViewActivity {
	
	private Point point;
	
	private ArrayList<IGeometry> mPointModels;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    // TODO Auto-generated method stub
	}

	@Override
	protected int getGUILayout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected IMetaioSDKCallback getMetaioSDKCallbackHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void loadContents() {
		Intent intent = getIntent();
		point = (Point) intent.getExtras().getSerializable("point");
		Log.d("Metaio", point.toString());
		try
		{
			AssetsManager.extractAllAssets(getApplicationContext(), false);
			// Getting a file path for tracking configuration XML file
			String trackingConfigFile = AssetsManager.getAssetPath("TrackingData_MarkerlessFast.xml");
			
			// Assigning tracking configuration
			boolean result = metaioSDK.setTrackingConfiguration(trackingConfigFile); 
			MetaioDebug.log("Tracking data loaded: " + result); 
	        
			mPointModels = new ArrayList<IGeometry>();
			// Getting a file path for a 3D geometry
			String poi1 = AssetsManager.getAssetPath("poi1.png");			
			if (poi1 != null) 
			{
				// Loading 3D geometry
				mPointModels.add(metaioSDK.createGeometryFromImage(poi1, true));
				if (mPointModels.get(0) != null) 
				{
					// Set geometry properties
					mPointModels.get(0).setScale(new Vector3d(1.0f, 1.0f, 1.0f));
					ArrayList<PoiPart> parts = point.getParts();
					mPointModels.get(0).setTranslation(new Vector3d(parts.get(0).x, parts.get(0).y, parts.get(0).z));
				}
				else
					MetaioDebug.log(Log.ERROR, "Error loading geometry: "+poi1);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void onGeometryTouched(IGeometry geometry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

}
