package uk.ac.brookes.tederiksson.pointsofinterest;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PoiHomeFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().setTitle(getResources().getStringArray(R.array.fragment_titles)[0]);
		return inflater.inflate(R.layout.fragment_home, container, false);
	}

}
