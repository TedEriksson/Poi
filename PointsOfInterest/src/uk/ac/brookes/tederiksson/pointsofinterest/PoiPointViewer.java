package uk.ac.brookes.tederiksson.pointsofinterest;

import java.util.ArrayList;

import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.MetaioSDK;
import com.metaio.sdk.jni.TrackingValues;
import com.metaio.sdk.jni.TrackingValuesVector;
import com.metaio.sdk.jni.Vector3d;
import com.metaio.tools.io.AssetsManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateBeamUrisCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PoiPointViewer extends ARViewActivity {
	
	private Point point;
	
	private PoiPartDialog dialog;
	
	private ArrayList<IGeometry> mPointModels;
	
	private TextView name, message;
	
	private MetaioSDKCallbackHandler mCallbackHandler;
	
	private RelativeLayout fitinbox, fitin;
	
	private boolean sdkReady = false;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		
		mCallbackHandler = new MetaioSDKCallbackHandler();
	}

	@Override
	protected int getGUILayout() {
		return R.layout.viewer_gui;
	}

	@Override
	protected void loadContents() {
		Intent intent = getIntent();
		point = (Point) intent.getExtras().getSerializable("point");
		
		Log.d("Metaio", point.toString());
		try
		{
			AssetsManager.extractAllAssets(getApplicationContext(), true);
			// Getting a file path for tracking configuration XML file
			String trackingConfigFile = AssetsManager.getAssetPath("TrackingData_MarkerlessFast.xml");
			
			// Assigning tracking configuration
			boolean result = metaioSDK.setTrackingConfiguration(trackingConfigFile); 
			MetaioDebug.log("Tracking data loaded: " + result); 
	        
			mPointModels = new ArrayList<IGeometry>();
			// Getting a file path for a 3D geometry
			String poiLogo = AssetsManager.getAssetPath("poi.png");			
			if (poiLogo != null) 
			{
				
				ArrayList<PoiPart> parts = point.getParts();
				
				for (int i = 0; i < parts.size(); i++) {
					mPointModels.add(metaioSDK.createGeometryFromImage(poiLogo, true));
					if (mPointModels.get(i) != null) {
						// Set geometry properties
						mPointModels.get(i).setScale(1);
						mPointModels.get(i).setName(Integer.toString(i));

						mPointModels.get(i).setTranslation(new Vector3d(parts.get(i).x, parts.get(i).y, parts.get(i).z));
					}
					else
						MetaioDebug.log(Log.ERROR, "Error loading geometry: "+poiLogo);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
 
	@Override
	protected void onGeometryTouched(IGeometry geometry) {
		
		final int partID = Integer.parseInt(geometry.getName());

		runOnUiThread(new Runnable() {
			@Override
			public void run(){
				dialog.setMessage(point.getParts().get(partID).getMessage());
				dialog.show(getFragmentManager(),"part");
			}
		});
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
	@Override
	protected IMetaioSDKCallback getMetaioSDKCallbackHandler() 
	{
		return mCallbackHandler;
	}
	 
	final class MetaioSDKCallbackHandler extends IMetaioSDKCallback 
	{ 

		@Override
		public void onTrackingEvent(TrackingValuesVector trackingValues) {
			// TODO Auto-generated method stub
			super.onTrackingEvent(trackingValues);
			
			for (int i=0;i<trackingValues.size();i++) {
				final TrackingValues v = trackingValues.get(i);
				if(v.isTrackingState()) {
					runOnUiThread(new Runnable() {
						public void run() {
							fitin.setVisibility(View.INVISIBLE);
							fitinbox.setVisibility(View.INVISIBLE);
						}
					});
					
				} else {
					runOnUiThread(new Runnable() {
						public void run() {
							if(sdkReady) {
								fitin.setVisibility(View.VISIBLE);
								fitinbox.setVisibility(View.VISIBLE);
							}
							
						}
					});
					
				}
			}
			
		}

		@Override
		public void onSDKReady() 
		{
			// show GUI
			runOnUiThread(new Runnable() 
			{
				@Override
				public void run() 
				{
					name = (TextView) findViewById(R.id.textViewName);
				    message = (TextView) findViewById(R.id.textViewMessage);
				    
				    fitin = (RelativeLayout) findViewById(R.id.fitin);
				    fitinbox = (RelativeLayout) findViewById(R.id.fitinbox);
				    
				    name.setText(point.getName());
					message.setText(point.getMessage());
					mGUIView.setVisibility(View.VISIBLE);
					
					dialog = new PoiPartDialog();
					
					sdkReady = true;
				}
			});
		}
	}
}
