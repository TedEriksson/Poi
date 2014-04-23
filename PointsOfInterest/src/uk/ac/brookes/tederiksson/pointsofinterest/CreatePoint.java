package uk.ac.brookes.tederiksson.pointsofinterest;

import com.google.android.gms.plus.Plus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class CreatePoint extends Activity {

	private EditText name, lat, lng, message;
	private String ownerId, accessToken;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_create_point);
		name = (EditText) findViewById(R.id.editTextCreatePointName);
		lat = (EditText) findViewById(R.id.editTextCreatePointLat);
		lng = (EditText) findViewById(R.id.editTextCreatePointLng);
		message = (EditText) findViewById(R.id.editTextCreatePointMessage);
		Intent intent = getIntent();
		ownerId = intent.getStringExtra("owner_id");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.createpoint, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event

		switch (item.getItemId()) {
		case R.id.action_create_point:
			if (!name.getText().toString().equals("")
					&& !lat.getText().toString().equals("")
					&& !lng.getText().toString().equals("")
					&& !message.getText().toString().equals("")) {
				Point point = new Point(-1, name.getText().toString(), message
						.getText().toString(), Double.parseDouble(lng.getText()
						.toString()), Double.parseDouble(lat.getText()
						.toString()), null, ownerId);
				Intent intent = new Intent(this, PoiPointViewer.class);
				Bundle bundle = new Bundle();
				bundle.putBoolean(PoiPointViewer.SET_MODE_CREATE, true);
				bundle.putSerializable("point", point);
				intent.putExtras(bundle);
				startActivity(intent);
			} else {
				Toast.makeText(this,
						"Please make sure all fields are filled and valid",
						Toast.LENGTH_SHORT).show();
			}
		}
		return super.onOptionsItemSelected(item);
	}
}
