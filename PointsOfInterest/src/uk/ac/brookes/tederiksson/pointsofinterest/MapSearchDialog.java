package uk.ac.brookes.tederiksson.pointsofinterest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView.FindListener;
import android.widget.NumberPicker;

public class MapSearchDialog extends DialogFragment {
	
	private NumberPicker picker;
	private SharedPreferences prefs;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    
	    prefs = getActivity().getSharedPreferences("poiprefs", Activity.MODE_PRIVATE);
	    
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    View view = inflater.inflate(R.layout.dialog_mapsearch, null);
	    builder.setView(view)
	    // Add action buttons
	           .setPositiveButton(R.string.action_search, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   onPosClick();
	               }
	           })
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                  MapSearchDialog.this.getDialog().cancel();
	               }
	           });   
	    picker = (NumberPicker) view.findViewById(R.id.numberPickerDistance);
		picker.setMaxValue(10000);
		picker.setMinValue(0);
		picker.setWrapSelectorWheel(false);
		picker.setValue(prefs.getInt("radius", 10));
	    return builder.create();
	}

	@Override
	public void onStart() {
		super.onStart();
		
	}
	
	public void onPosClick() {
		prefs.edit().putInt("radius", picker.getValue()).commit();
	}
	
}