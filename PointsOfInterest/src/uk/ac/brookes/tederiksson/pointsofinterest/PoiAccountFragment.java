package uk.ac.brookes.tederiksson.pointsofinterest;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.graphics.Point;
import android.os.Bundle;
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

	private TextView mUsername;
	private Button mButtonLogout;

	private Point mDisplaySize;
	
	private boolean mAllowAnimate = false;

	/* Google Api Login Vars */
	private static final int RC_SIGN_IN = 0;
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
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();
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
		mUsername = (TextView) activity.findViewById(R.id.textViewUserName);

		mSignInButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAllowAnimate = true;
				try {
					mConnectionResult.startResolutionForResult(activity,
							RC_SIGN_IN);
				} catch (IntentSender.SendIntentException e) {
					mGoogleApiClient.connect();
				}
			}
		});

		mButtonLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAllowAnimate = true;
				if (mGoogleApiClient.isConnected()) {
					Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
					mGoogleApiClient.disconnect();
					mGoogleApiClient.connect();
				}
			}
		});
		
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
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		mConnectionResult = result;
		updateView(false);
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mUsername.setText(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getDisplayName());
		updateView(true);
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
			if (mAllowAnimate) animateToLoggedIn();
		} else {
			if (mConnectionResult == null) {
				mSignInButton.setVisibility(View.GONE);
			} else {
				mSignInButton.setVisibility(View.VISIBLE);
			}
			mLoggedin.setVisibility(View.GONE);
			mButtonLogout.setEnabled(false);
			if (mAllowAnimate) animateToSignIn();
		}
		mAllowAnimate = false;
	}
	
	private void animateToSignIn() {
		TranslateAnimation animateSignIn = new TranslateAnimation(-mDisplaySize.x, 0, 0, 0);
		animateSignIn.setDuration(500);
		animateSignIn.setFillAfter(true);
		mSignInButton.startAnimation(animateSignIn);
		TranslateAnimation animateLoggedIn = new TranslateAnimation(0, mDisplaySize.x, 0, 0);
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
		TranslateAnimation animateLoggedIn = new TranslateAnimation(mDisplaySize.x, 0, 0, 0);
		animateLoggedIn.setDuration(500);
		animateLoggedIn.setFillAfter(true);
		mLoggedin.startAnimation(animateLoggedIn);
		TranslateAnimation animateSignIn = new TranslateAnimation(0, -mDisplaySize.x, 0, 0);
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
}
