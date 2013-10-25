package uk.ac.brookes.tederiksson.pointsofinterest;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class Home extends Activity {
	
	Button searchByIDButon;
	EditText idNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        searchByIDButon = (Button) findViewById(R.id.searchIDButton);
        idNumber = (EditText) findViewById(R.id.idNumber);
        
        searchByIDButon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(idNumber.getWindowToken(), 0);
				Intent intent = new Intent(getApplicationContext(), ViewPoint.class);
				intent.putExtra("id", Integer.parseInt(idNumber.getText().toString()));
				startActivity(intent);
				
			}
		});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    
}
