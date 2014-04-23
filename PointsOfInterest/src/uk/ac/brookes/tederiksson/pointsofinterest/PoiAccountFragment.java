package uk.ac.brookes.tederiksson.pointsofinterest;

import java.io.IOException;
import java.util.ArrayList;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.fa;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.plus.Plus;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class PoiAccountFragment extends Fragment implements
		ConnectionCallbacks, OnConnectionFailedListener {

	private Activity activity;

	private RelativeLayout mAccountBase;
	private SignInButton mSignInButton;
	private ScrollView mLoggedin;
	private LinearLayout mUsersPoints;

	private TextView mUsername;
	private Button mButtonLogout, mButtonCreatePoint, mButtonRevoke;

	private Point mDisplaySize;

	private boolean mAllowAnimate = false;

	private GetUsersPoints loadPoints;

	/* Google Api Login Vars */
	private static final int RC_SIGN_IN = 0, REQUEST_AUTH = 1;
	private GoogleApiClient mGoogleApiClient;
	private boolean mIntentInProgress;
	private boolean mSignInClicked;

	private ConnectionResult mConnectionResult;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_account, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = getActivity();
		mGoogleApiClient = new GoogleApiClient.Builder(activity)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API, null)
				.addScope(Plus.SCOPE_PLUS_LOGIN)
				.addScope(Plus.SCOPE_PLUS_PROFILE).build();
	}

	@Override
	public void onStart() {
		super.onStart();
		mDisplaySize = new Point();
		activity.getWindowManager().getDefaultDisplay().getSize(mDisplaySize);
		mSignInButton = (SignInButton) activity
				.findViewById(R.id.sign_in_button);
		mLoggedin = (ScrollView) activity.findViewById(R.id.loggedin);
		mButtonLogout = (Button) activity.findViewById(R.id.buttonLogout);
		mButtonRevoke = (Button) activity.findViewById(R.id.buttonRevoke);
		mButtonCreatePoint = (Button) activity
				.findViewById(R.id.buttonCreatePoint);
		mUsername = (TextView) activity.findViewById(R.id.textViewUserName);

		mSignInButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAllowAnimate = true;
				try {
					if (mConnectionResult != null)
						mConnectionResult.startResolutionForResult(activity,
								RC_SIGN_IN);
					else
						Toast.makeText(
								activity,
								"Please Check you are connected to the Internet and try again.",
								Toast.LENGTH_SHORT).show();

				} catch (IntentSender.SendIntentException e) {
					mGoogleApiClient.connect();
				}
			}
		});

		mButtonCreatePoint.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CalcToken cal = new CalcToken();
				cal.execute();
			}
		});

		mButtonLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAllowAnimate = true;
				if (mGoogleApiClient.isConnected()) {
					activity.getSharedPreferences("poiprefs",
							Activity.MODE_PRIVATE).edit().remove("auth")
							.commit();
					Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
					mGoogleApiClient.disconnect();
					mGoogleApiClient.connect();
				}
			}
		});

		mButtonRevoke.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAllowAnimate = true;
				Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
				activity.getSharedPreferences("poiprefs",
						Activity.MODE_PRIVATE).edit().remove("auth")
						.commit();
				Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
						.setResultCallback(new ResultCallback<Status>() {

							@Override
							public void onResult(Status result) {
								Toast.makeText(activity, "Revoked", Toast.LENGTH_SHORT).show();
								((MainActivity)activity).selectItem(3);
							}
						});
				updateView(false);
			}
		});

		mUsersPoints = (LinearLayout) activity
				.findViewById(R.id.yourPointsList);

		mLoggedin.setVisibility(View.GONE);

		mGoogleApiClient.connect();
	}

	@Override
	public void onStop() {
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
		super.onStop();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RC_SIGN_IN) {
			if (resultCode == Activity.RESULT_OK
					&& !mGoogleApiClient.isConnected()
					&& !mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		} else if (requestCode == REQUEST_AUTH) {
			CalcToken cal = new CalcToken();
			cal.execute();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		mConnectionResult = result;
		updateView(false);
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		try {
			mUsername.setText(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient)
					.getDisplayName());
			loadPoints = new GetUsersPoints();
			loadPoints.execute(Plus.PeopleApi
					.getCurrentPerson(mGoogleApiClient).getId());

			updateView(true);
		} catch (NullPointerException e) {
			updateView(false);
			if (mGoogleApiClient.isConnected()) {
				Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
				mGoogleApiClient.disconnect();
				mGoogleApiClient.connect();
			}
			// Toast.makeText(
			// activity,
			// "Please Check you are connected to the Internet and try again.",
			// Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onConnectionSuspended(int cause) {
		updateView(false);
		mGoogleApiClient.connect();
	}

	public void updateView(boolean isSignedIn) {
		if (isSignedIn) {
			mSignInButton.setVisibility(View.GONE);
			mLoggedin.setVisibility(View.VISIBLE);
			mButtonLogout.setEnabled(true);
			mButtonCreatePoint.setEnabled(true);
			mButtonRevoke.setEnabled(true);
			if (mAllowAnimate)
				animateToLoggedIn();
		} else {
			if (mConnectionResult == null) {
				mSignInButton.setVisibility(View.GONE);
			} else {
				mSignInButton.setVisibility(View.VISIBLE);
			}
			mLoggedin.setVisibility(View.GONE);
			mButtonLogout.setEnabled(false);
			mButtonCreatePoint.setEnabled(false);
			mButtonRevoke.setEnabled(false);
			if (mAllowAnimate)
				animateToSignIn();
		}
		mAllowAnimate = false;
	}

	private void animateToSignIn() {
		TranslateAnimation animateSignIn = new TranslateAnimation(
				-mDisplaySize.x, 0, 0, 0);
		animateSignIn.setDuration(500);
		animateSignIn.setFillAfter(true);
		mSignInButton.startAnimation(animateSignIn);
		TranslateAnimation animateLoggedIn = new TranslateAnimation(0,
				mDisplaySize.x, 0, 0);
		animateLoggedIn.setDuration(500);
		animateLoggedIn.setFillBefore(true);
		animateLoggedIn.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				mLoggedin.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mLoggedin.setVisibility(View.GONE);
			}
		});
		mLoggedin.startAnimation(animateLoggedIn);
	}

	private void animateToLoggedIn() {
		TranslateAnimation animateLoggedIn = new TranslateAnimation(
				mDisplaySize.x, 0, 0, 0);
		animateLoggedIn.setDuration(500);
		animateLoggedIn.setFillAfter(true);
		mLoggedin.startAnimation(animateLoggedIn);
		TranslateAnimation animateSignIn = new TranslateAnimation(0,
				-mDisplaySize.x, 0, 0);
		animateSignIn.setDuration(500);
		animateSignIn.setFillBefore(true);
		animateSignIn.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				mSignInButton.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mSignInButton.setVisibility(View.GONE);
			}
		});
		mSignInButton.startAnimation(animateSignIn);
	}

	private class PointView extends RelativeLayout {

		public PointView(Context context) {
			super(context);
			initView();
		}

		private void initView() {
			View view = inflate(getContext(), R.layout.nearby_item, null);
			addView(view);
		}

		public void setName(String text) {
			TextView name = (TextView) findViewById(R.id.textViewNearbyName);
			name.setText(text);
		}
		
		public void setID(String text) {
			TextView pointid = (TextView) findViewById(R.id.textViewNearbyDistance);
			pointid.setText(text);
		}
	}

	public void slideView(final View view, int toY) {
		TranslateAnimation animate = new TranslateAnimation(0, 0, -toY, 0);
		animate.setDuration(500);
		animate.setFillAfter(true);
		view.startAnimation(animate);
	}

	class CalcToken extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				activity.getSharedPreferences("poiprefs", Activity.MODE_PRIVATE)
						.edit().putString("auth", result).commit();
				Intent intent = new Intent(activity, CreatePoint.class);
				intent.putExtra("owner_id",
						Plus.PeopleApi.getCurrentPerson(mGoogleApiClient)
								.getId());
				startActivity(intent);
			} else {
				activity.getSharedPreferences("poiprefs", Activity.MODE_PRIVATE)
				.edit().remove("auth").commit();
				Toast.makeText(activity,
						"Please accept the Permissions to continue",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPreExecute() {
			// Toast.makeText(activity, "Starting", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String accessToken = null;
			try {
				accessToken = GoogleAuthUtil.getToken(activity,
						Plus.AccountApi.getAccountName(mGoogleApiClient),
						"oauth2:profile");
				Log.d("access token", accessToken);
			} catch (IOException transientEx) {
				// network or server error, the call is expected to succeed if
				// you try again later.
				// Don't attempt to call again immediately - the request is
				// likely to
				// fail, you'll hit quotas or back-off.
				Log.e("Network", "server error");
				return null;
			} catch (UserRecoverableAuthException e) {
				// Recover
				e.printStackTrace();
				accessToken = null;
				startActivityForResult(e.getIntent(), REQUEST_AUTH);
			} catch (GoogleAuthException authEx) {
				authEx.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return accessToken;
		}

	}

	private class GetUsersPoints
			extends
			AsyncTask<String, Void, ArrayList<uk.ac.brookes.tederiksson.pointsofinterest.Point>> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mUsersPoints.removeAllViews();
			Context context = getActivity();
			PointView loading = new PointView(context);
			loading.setName("Loading Points");
			mUsersPoints.addView(loading);
		}

		@Override
		protected ArrayList<uk.ac.brookes.tederiksson.pointsofinterest.Point> doInBackground(
				String... id) {
			return PointParser.getPointsByUser(id[0]);
		}

		@Override
		protected void onPostExecute(
				ArrayList<uk.ac.brookes.tederiksson.pointsofinterest.Point> results) {
			super.onPostExecute(results);

			mUsersPoints.removeAllViews();

			if (results == null) {
				PointView fail = new PointView(activity);
				fail.setName("Failed to get Points. Tap to try again.");
				mUsersPoints.addView(fail);
				fail.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						loadPoints = new GetUsersPoints();
						loadPoints.execute(Plus.PeopleApi.getCurrentPerson(
								mGoogleApiClient).getId());
					}
				});
			} else {
				if (results.size() == 0) {
					PointView fail = new PointView(activity);
					fail.setName("You don't have any points.");
					mUsersPoints.addView(fail);

				} else {
					for (final uk.ac.brookes.tederiksson.pointsofinterest.Point result : results) {
						PointView slot = new PointView(activity);
						slot.setName(result.getName());
						slot.setID(Integer.toString(result.getId()));
						mUsersPoints.addView(slot);
						slideView(slot, mUsersPoints.getBottom());
						slot.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								((MainActivity) getActivity())
										.setLastLocation(result.getLatLng());
								((MainActivity) getActivity()).selectItem(1);
							}
						});
					}
				}
			}
		}

	}
}
