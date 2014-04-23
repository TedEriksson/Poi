package uk.ac.brookes.tederiksson.pointsofinterest;

import java.util.ArrayList;

import com.google.android.gms.internal.cu;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PoiPointViewer extends ARViewActivity {

	public static final String SET_MODE_CREATE = "create_mode";

	public static final int MODE_VIEW = 1, MODE_CREATE = 2;

	private int mode;

	private Point point;

	private PoiPartDialog dialog;

	private ArrayList<IGeometry> mPointModels;

	private TextView name, message;
	
	private EditText mPartMessage;

	private MetaioSDKCallbackHandler mCallbackHandler;

	private RelativeLayout pointCreator, pointViewer, fitinbox, controls;
	private Button toggleControls, nextPoint, previousPoint;

	private boolean sdkReady = false;

	// Create point specific variables
	private int currentPart = -1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mCallbackHandler = new MetaioSDKCallbackHandler();
	}

	public void toggleControls(View v) {
		if (controls.getVisibility() == View.VISIBLE)
			controls.setVisibility(View.GONE);
		else
			controls.setVisibility(View.VISIBLE);
	}

	@Override
	protected int getGUILayout() {
		return R.layout.viewer_gui;
	}

	@Override
	protected void loadContents() {
		Intent intent = getIntent();
		if (intent.getExtras().getBoolean(SET_MODE_CREATE, false))
			mode = MODE_CREATE;
		else
			mode = MODE_VIEW;
		point = (Point) intent.getExtras().getSerializable("point");

		Log.d("Metaio", point.toString());
		try {
			AssetsManager.extractAllAssets(getApplicationContext(), true);
			// Getting a file path for tracking configuration XML file
			String trackingConfigFile = AssetsManager
					.getAssetPath("TrackingData_Marker.xml");

			// Assigning tracking configuration
			boolean result = metaioSDK
					.setTrackingConfiguration(trackingConfigFile);
			MetaioDebug.log("Tracking data loaded: " + result);

			mPointModels = new ArrayList<IGeometry>();
			// Getting a file path for a 3D geometry
			String poiLogo = AssetsManager.getAssetPath("poi.png");
			if (poiLogo != null) {

				if (mode == MODE_CREATE && point.getPartsSize() == 0) {
					for (int i = 0; i < 10; i++) {
						mPointModels.add(metaioSDK.createGeometryFromImage(
								poiLogo, true));
						if (mPointModels.get(i) != null) {
							// Set geometry properties
							mPointModels.get(i).setScale(1);
							mPointModels.get(i).setName(Integer.toString(i));
							mPointModels.get(i).setTranslation(
									new Vector3d(0, 0, 0));
							mPointModels.get(i).setVisible(false);
						} else
							MetaioDebug.log(Log.ERROR,
									"Error loading geometry: " + poiLogo);
					}
				} else {
					ArrayList<PoiPart> parts = point.getParts();

					for (int i = 0; i < parts.size(); i++) {
						mPointModels.add(metaioSDK.createGeometryFromImage(
								poiLogo, true));
						if (mPointModels.get(i) != null) {
							// Set geometry properties
							mPointModels.get(i).setScale(1);
							mPointModels.get(i).setName(Integer.toString(i));
							mPointModels.get(i).setTranslation(
									new Vector3d(parts.get(i).x,
											parts.get(i).y, parts.get(i).z));
							if (mode == MODE_CREATE)
								mPointModels.get(i).setVisible(false);
						} else
							MetaioDebug.log(Log.ERROR,
									"Error loading geometry: " + poiLogo);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onGeometryTouched(IGeometry geometry) {

		final int partID = Integer.parseInt(geometry.getName());

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				dialog.setMessage(point.getParts().get(partID).getMessage());
				dialog.show(getFragmentManager(), "part");
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
	protected IMetaioSDKCallback getMetaioSDKCallbackHandler() {
		return mCallbackHandler;
	}

	private void createNewPart() {
		PoiPart part = new PoiPart(-1, null, 0, 0, 0);
		point.addPart(part);
		currentPart = point.getPartsSize() - 1;
		updateViews();
	}

	private void updateViews() {
		if (currentPart == point.getPartsSize() - 1)
			nextPoint.setText("New");
		else
			nextPoint.setText("Next");
		if (currentPart <= 0)
			previousPoint.setEnabled(false);
		else
			previousPoint.setEnabled(true);
		
		for(int i = 0; i< point.getPartsSize();i++) {
			ArrayList<PoiPart> parts = point.getParts();
			mPointModels.get(i).setTranslation(new Vector3d(parts.get(i).x, parts.get(i).y, parts.get(i).z), false);
		}
		
		for (int i = 0; i < mPointModels.size(); i++)
			mPointModels.get(i).setVisible(false);
		if (currentPart >= 0 && currentPart < point.getPartsSize())
			mPointModels.get(currentPart).setVisible(true);
		
		if(mPartMessage != null && currentPart > -1)
			mPartMessage.setText(point.getParts().get(currentPart).getMessage());
	}
	
	public void addX(View v) {
		if(currentPart >= 0) {
			point.getParts().get(currentPart).x += 10;
			
		}
		updateViews();
	}
	
	public void subtractX(View v) {
		if(currentPart >= 0) {
			point.getParts().get(currentPart).x -= 10;
			
		}
		updateViews();
	}
	
	public void addY(View v) {
		if(currentPart >= 0) {
			point.getParts().get(currentPart).y += 10;
			
		}
		updateViews();
	}
	
	public void subtractY(View v) {
		if(currentPart >= 0) {
			point.getParts().get(currentPart).y -= 10;
			
		}
		updateViews();
	}
	
	public void addZ(View v) {
		if(currentPart >= 0) {
			point.getParts().get(currentPart).z += 10;
			
		}
		updateViews();
	}
	
	public void subtractZ(View v) {
		if(currentPart >= 0) {
			point.getParts().get(currentPart).z -= 10;
			
		}
		updateViews();
	}
	
	public void submitPoint() {
		Intent intent = new Intent(this, UploadPoint.class);
		intent.putExtra("point", point);
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		if(mode == MODE_CREATE) inflater.inflate(R.menu.createpoint, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event

		switch (item.getItemId()) {
		case R.id.action_create_point:
			submitPoint();
		}
		return super.onOptionsItemSelected(item);
	}

	final class MetaioSDKCallbackHandler extends IMetaioSDKCallback {

		@Override
		public void onTrackingEvent(TrackingValuesVector trackingValues) {
			// TODO Auto-generated method stub
			super.onTrackingEvent(trackingValues);

			for (int i = 0; i < trackingValues.size(); i++) {
				final TrackingValues v = trackingValues.get(i);
				if (v.isTrackingState()) {
					runOnUiThread(new Runnable() {
						public void run() {
							fitinbox.setVisibility(View.INVISIBLE);
						}
					});

				} else {
					runOnUiThread(new Runnable() {
						public void run() {
							if (sdkReady) {
								fitinbox.setVisibility(View.VISIBLE);
							}

						}
					});

				}
			}

		}

		@Override
		public void onSDKReady() {
			// show GUI
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
 
					pointViewer = (RelativeLayout) findViewById(R.id.pointViewer);
					pointCreator = (RelativeLayout) findViewById(R.id.pointViewCreator);

					name = (TextView) findViewById(R.id.textViewName);
					message = (TextView) findViewById(R.id.textViewMessage);

					fitinbox = (RelativeLayout) findViewById(R.id.fitinbox);

					controls = (RelativeLayout) findViewById(R.id.pointControls);
					toggleControls = (Button) findViewById(R.id.buttonControlsToggle);

					nextPoint = (Button) findViewById(R.id.buttonPointNext);
					nextPoint.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if(currentPart >= 9) {
								Toast.makeText(getApplicationContext(), "Reached Max Parts", Toast.LENGTH_SHORT).show();
								return;
							}
							if (currentPart >= point.getPartsSize() - 1) {
								// New Point
								createNewPart();
							} else {
								// Next Point
								currentPart++;
								updateViews();
							}
						}
					});
					
					previousPoint = (Button) findViewById(R.id.buttonPointBack);
					previousPoint.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							if(currentPart > 0) currentPart--;
							updateViews();
						}
					});
					
					mPartMessage = (EditText) findViewById(R.id.partMessage);
					mPartMessage.addTextChangedListener(new TextWatcher() {
						
						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void beforeTextChanged(CharSequence s, int start, int count,
								int after) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void afterTextChanged(Editable s) {
							if(mode == MODE_CREATE && currentPart > -1)
								point.getParts().get(currentPart).setMessage(s.toString());
						}
					});

					name.setText(point.getName());
					message.setText(point.getMessage());

					switch (mode) {
					case MODE_CREATE:
						pointCreator.setVisibility(View.VISIBLE);
						pointViewer.setVisibility(View.GONE);
						updateViews();
						break;

					default:
						pointCreator.setVisibility(View.GONE);
						pointViewer.setVisibility(View.VISIBLE);
						break;
					}

					mGUIView.setVisibility(View.VISIBLE);

					dialog = new PoiPartDialog();
					invalidateOptionsMenu();
					sdkReady = true;

				}
			});
		}
	}
}
