/**
 * A fragment that contains the 4 main meal buttons on the main 
 * page. It handles some basic FragmentTransactions.
 * 
 * @author Jeff Butterfield and Shouvik Dutta
 * 
 */

package com.appspot.berkeleydining;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class MainFragment extends Fragment {
	Fragment savedFrag = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((CalMealsActivity) getActivity()).resetMenu();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.customdash, container, false);

		// Set up the Breakfast button
		ImageButton breakfastButton = (ImageButton) v
				.findViewById(R.id.breakfastbutton);
		breakfastButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				commitMenuFragments();
				((CalMealsActivity) getActivity()).setCurrentMeal("Breakfast");
				((CalMealsActivity) getActivity())
						.setCurrentHall(CurrentMenu.halls.Crossroads);
			}

		});

		// Set up the Lunch button
		ImageButton lunchButton = (ImageButton) v
				.findViewById(R.id.lunchbutton);
		lunchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				commitMenuFragments();
				((CalMealsActivity) getActivity()).setCurrentMeal("Lunch");
				((CalMealsActivity) getActivity())
						.setCurrentHall(CurrentMenu.halls.Crossroads);
			}

		});
		// Set up the Dinner button
		ImageButton dinnerButton = (ImageButton) v
				.findViewById(R.id.dinnerbutton);
		dinnerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				commitMenuFragments();
				((CalMealsActivity) getActivity()).setCurrentMeal("Dinner");
				((CalMealsActivity) getActivity())
						.setCurrentHall(CurrentMenu.halls.Crossroads);
			}

		});
		// Set up the LateNight button
		ImageButton latenightButton = (ImageButton) v
				.findViewById(R.id.latenightbutton);
		latenightButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				commitMenuFragments();
				((CalMealsActivity) getActivity()).setCurrentMeal("LateNight");
				((CalMealsActivity) getActivity())
						.setCurrentHall(CurrentMenu.halls.Crossroads);
			}

		});

		return v;

	}

	/** Executes an AsyncTask to transition */
	public void commitMenuFragments() {

		ChangeMainFrag ctask = new ChangeMainFrag();
		ctask.execute((Void[]) null);

	}

	/**
	 * An AsyncTask that handles FragmentTransactions involved when
	 * transitioning from the main page to the menu page.
	 * 
	 * @author Jeff Butterfield and Shouvik Dutta
	 * 
	 */
	private class ChangeMainFrag extends AsyncTask<Void, Void, Void> {
		final FragmentManager fragMan = getActivity()
				.getSupportFragmentManager();
		FragmentTransaction ft1;
		FragmentTransaction ft2;
		FragmentTransaction ft3;

		@Override
		protected Void doInBackground(Void... params) {
			// Fragment topf = fragMan.findFragmentById(R.id.Title);
			// Fragment topfNew = new TitleBarRefreshFragment();
			// ft1 = fragMan.beginTransaction();
			// ft1.remove(topf);
			// ft1.add(R.id.Title, topfNew);

			Fragment f = fragMan.findFragmentById(R.id.Main);
			Fragment f2 = new MenusFragment();
			ft2 = fragMan.beginTransaction();
			ft2.setCustomAnimations(R.anim.slide_in_right,
					R.anim.slide_out_left, R.anim.slide_in_left,
					R.anim.slide_out_right);
			ft2.remove(f);
			ft2.add(R.id.Main, f2);
			ft2.addToBackStack(null);

			Fragment bottomF = fragMan.findFragmentById(R.id.BottomFrag);
			Fragment bottomFNew;
			if (((CalMealsActivity) getActivity()).getCurrentMeal().equals(
					"LateNight")) {
				bottomFNew = new NoRatingBarFragment();
			} else {
				bottomFNew = new RatingBarFragment();
			}
			ft3 = fragMan.beginTransaction();
			savedFrag = bottomF;
			ft3.remove(bottomF);
			ft3.add(R.id.BottomFrag, bottomFNew);

			return null;
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(Void unused) {
			// ft1.commit();
			ft2.commit();
			ft3.commit();
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		((CalMealsActivity) getActivity()).restoreBottomFragment();
		((CalMealsActivity) getActivity()).restoreTopFragment();
	}

}