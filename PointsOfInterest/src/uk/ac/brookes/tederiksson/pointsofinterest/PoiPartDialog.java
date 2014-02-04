package uk.ac.brookes.tederiksson.pointsofinterest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class PoiPartDialog extends DialogFragment {
	
	private String message;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    View view = inflater.inflate(R.layout.dialog_part, null);
	    builder.setView(view)
	    	.setMessage(message)
	    	.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                  PoiPartDialog.this.getDialog().cancel();
               }
	    	});   
	    return builder.create();
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

}
