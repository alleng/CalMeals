/**
 * The fragment that contains the MealPoint button that a user
 * can press if the user wants to view balances. 
 * 
 * @author Jeff Butterfield and Shouvik Dutta
 * 
 */

package com.appspot.berkeleydining;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class MealPointButtonFragment extends Fragment {

	String pointStored = null;
	String debitStored = null;
	String pointsTemp = null;
	String debitTemp = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences settings = getActivity().getSharedPreferences(
				"Prefs", 0);

		pointsTemp = ((CalMealsActivity) getActivity())
				.getpointsBalanceTemporary();
		debitTemp = ((CalMealsActivity) getActivity())
				.getdebitBalanceTemporary();
		pointStored = settings.getString("pointBalance", null);
		debitStored = settings.getString("debitBalance", null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.mpbutton_layout, container, false);
		final ImageButton button = (ImageButton) v.findViewById(R.id.button1);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// Checks for previously fetched balances, temporary or stored,
				// before
				// prompting for username and password.
				if ((pointStored != null && debitStored != null)
						|| (pointsTemp != null && debitTemp != null)) {
					((CalMealsActivity) getActivity())
							.setBottomFragStatus("loaded");
					final FragmentManager fragMan = getActivity()
							.getSupportFragmentManager();
					Fragment f = fragMan.findFragmentById(R.id.BottomFrag);
					Fragment f2 = new MealPointLoadedFragment();
					FragmentTransaction ft = getActivity()
							.getSupportFragmentManager().beginTransaction();
					ft.remove(f);
					ft.add(R.id.BottomFrag, f2);
					ft.commit();
				} else {

					((CalMealsActivity) getActivity()).showLoginPrompt();
				}
			}
		});
		return v;

	}

}