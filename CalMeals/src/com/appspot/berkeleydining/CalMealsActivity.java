/**
 * The central activity where all the menus, ratings, and 
 * balances can be viewed.
 * 
 * @author Jeff Butterfield and Shouvik Dutta
 * 
 */

package com.appspot.berkeleydining;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.gson.Gson;

public class CalMealsActivity extends SherlockFragmentActivity {
	boolean shouldResume = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		sharedPrefSettings = getSharedPreferences("Prefs", 0);
		editor = sharedPrefSettings.edit();

		setBottomFragStatus("unpressed");
		rememberMeChecked = false;
		usernameTemporary = null;
		passwordTemporary = null;
		debitBalanceTemporary = null;
		pointsBalanceTemporary = null;
		currentMeal = null;
		fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		// fragmentTransaction.add(R.id.Title, new TitleBarFragment());
		fragmentTransaction.add(R.id.Main, new MainFragment());
		fragmentTransaction.replace(R.id.BottomFrag,
				new MealPointButtonFragment());

		fragmentTransaction.commit();
		restoreMiddleFragment();
	}

	/** Generates the log-in prompt for accessing Meal Point and Debit Balances. */
	public void showLoginPrompt() {
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.loginlayout, null);
		AlertDialog infoRequest = new AlertDialog.Builder(this)
				.setView(textEntryView)
				.setTitle("Check Balances")
				.setPositiveButton("Login",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								final EditText et1 = (EditText) textEntryView
										.findViewById(R.id.editText1);
								String userText = et1.getText().toString();
								final EditText et2 = (EditText) textEntryView
										.findViewById(R.id.editText2);
								String passText = et2.getText().toString();
								final CheckBox cb = (CheckBox) textEntryView
										.findViewById(R.id.checkBox1);
								rememberMeChecked = cb.isChecked();

								startFetchBalanceTask(userText, passText);
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {

								dialog.dismiss();
							}
						}).create();
		infoRequest.show();
	}

	/** Generates the prompt for rating individual menu meals. */
	public void showRatingAlert() {
		LayoutInflater factory = LayoutInflater.from(this);
		final View rateEntryView = factory.inflate(R.layout.ratedialog_layout,
				null);
		TextView averageTv = (TextView) rateEntryView
				.findViewById(R.id.aveargeRatingTextView);
		TextView voteCountTv = (TextView) rateEntryView
				.findViewById(R.id.numVotesTextView);
		float averageRating = currentHall.getMeal(currentMeal).rating;
		int voteCount = currentHall.getMeal(currentMeal).number_ratings;
		averageTv.setText("Current Rating: " + averageRating + " Stars");
		voteCountTv.setText("Total Votes: " + voteCount);

		AlertDialog infoRequest = new AlertDialog.Builder(this)
				.setView(rateEntryView)
				.setTitle("Rate this Meal")
				.setPositiveButton("Submit",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {

								final RatingBar rb2 = (RatingBar) rateEntryView
										.findViewById(R.id.ratingBar2);
								float rating = rb2.getRating();
								Log.i("input", "" + rating);
								String rateId;
								String memKey = "";
								int caseNumber = 0;
								if (currentHall
										.equals(CurrentMenu.halls.Crossroads)) {
									memKey = "CROSSROADS_"
											+ currentMeal.toUpperCase();
									caseNumber = 0;
								}
								if (currentHall.equals(CurrentMenu.halls.Cafe3)) {
									memKey = "CAFE3_"
											+ currentMeal.toUpperCase();
									caseNumber = 1;
								}
								if (currentHall
										.equals(CurrentMenu.halls.Foothill)) {
									memKey = "FOOTHILL_"
											+ currentMeal.toUpperCase();
									caseNumber = 2;
								}
								if (currentHall
										.equals(CurrentMenu.halls.ClarkKerr)) {
									memKey = "CKC_" + currentMeal.toUpperCase();
									caseNumber = 3;
								}
								rb2.setRating(sharedPrefSettings.getFloat(
										memKey, -1));
								String memKeyUse = memKey + "_USER";
								if (currentMeal.equals("Breakfast")) {
									rateId = Integer
											.toString(caseNumber * 3 + 0);
									System.out.println(rateId);
									System.out.println(rating);
								} else if (currentMeal.equals("Lunch")) {
									rateId = Integer
											.toString(caseNumber * 3 + 1);
								} else {
									rateId = Integer
											.toString(caseNumber * 3 + 2);
								}
								String[] params = {
										rateId,
										Float.toString(rating),
										Float.toString(sharedPrefSettings
												.getFloat(memKeyUse, -1)) };

								new Rate().execute(params);

								sharedPrefSettings.edit()
										.putFloat(memKeyUse, rating).commit();
								Toast.makeText(getBaseContext(),
										"Rating Submitted", Toast.LENGTH_SHORT)
										.show();
								FragmentManager fragMan = getSupportFragmentManager();
								RatingBarFragment f = (RatingBarFragment) fragMan
										.findFragmentById(R.id.BottomFrag);
								f.setRatingBar(f.v, rating);
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {

								dialog.dismiss();
							}
						}).create();
		infoRequest.show();
	}

	/**
	 * Executes an instance of an AsyncTask that makes HTTP requests to retrieve
	 * balances for account of username User and password Pass.
	 */
	private void startFetchBalanceTask(String user, String pass) {
		fetchBalance = new fetchBalanceTask();
		String[] params = new String[2];
		params[0] = user;
		params[1] = pass;
		setBottomFragStatus("loading");
		fetchBalance.execute(params);
	}

	/**
	 * AsyncTask that downloads the menu in the background while showing a
	 * "Loading..." dialog
	 * 
	 * @author Jeff Butterfield and Shouvik Dutta
	 * 
	 */
	private class fetchBalanceTask extends AsyncTask<String, Void, Void> {
		boolean loginFailed = false;

		@Override
		protected Void doInBackground(String... params) {
			GetCalCard getCard = new GetCalCard();
			String[] moneyinfo = getCard.getInfo(params[0], params[1]);
			if (moneyinfo == null) {
				loginFailed = true;
				return null;
			}
			usernameTemporary = params[0];
			passwordTemporary = params[1];
			debitBalanceTemporary = moneyinfo[0];
			pointsBalanceTemporary = moneyinfo[1];

			return null;
		}

		@Override
		protected void onPreExecute() {
			Fragment f = fragmentManager.findFragmentById(R.id.BottomFrag);
			Fragment f2 = new MealPointLoadingFragment();
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.remove(f);
			ft.add(R.id.BottomFrag, f2);
			ft.commit();
		}

		@Override
		protected void onPostExecute(Void unused) {
			if (loginFailed) {
				setBottomFragStatus("unpressed");
				final AlertDialog alertDialog = new AlertDialog.Builder(
						CalMealsActivity.this)
						.setTitle("Invalid Login")
						.setMessage("Please try again.")
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										dialog.dismiss();

									}
								}).create();

				Fragment mf = fragmentManager.findFragmentById(R.id.Main);
				if (mf.getClass().getName()
						.equals("com.appspot.berkeleydining.MainFragment")) {
					Fragment f = fragmentManager
							.findFragmentById(R.id.BottomFrag);
					Fragment f2 = new MealPointButtonFragment();
					FragmentTransaction ft = getSupportFragmentManager()
							.beginTransaction();
					ft.remove(f);
					ft.add(R.id.BottomFrag, f2);
					ft.commit();
				}
				alertDialog.show();
			} else {
				if (rememberMeChecked) {
					editor.putString("debitBalance", debitBalanceTemporary);
					editor.putString("pointBalance", pointsBalanceTemporary);
					editor.putString("balanceUsername", getUsernameTemporary());
					editor.putString("balancePassword", passwordTemporary);
					editor.commit();
				}
				setBottomFragStatus("loaded");
				Fragment mf = fragmentManager.findFragmentById(R.id.Main);
				Fragment f = fragmentManager.findFragmentById(R.id.BottomFrag);
				if (mf.getClass().getName()
						.equals("com.appspot.berkeleydining.MainFragment")) {
					Fragment f2 = new MealPointLoadedFragment();
					FragmentTransaction ft = getSupportFragmentManager()
							.beginTransaction();
					ft.remove(f);
					ft.add(R.id.BottomFrag, f2);
					ft.commit();
				}
			}
		}
	}

	/**
	 * Displays a dialog box that allows users to refresh their balances or
	 * logout of their accounts.
	 */
	public void showOptions() {
		final CharSequence[] items = { "Refresh", "Logout", "Cancel" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (item == 0) {
					if ((usernameTemporary != null && passwordTemporary != null)) {
						editor.remove("debitBalance");
						editor.remove("pointBalance");
						debitBalanceTemporary = null;
						pointsBalanceTemporary = null;
						startFetchBalanceTask(usernameTemporary,
								passwordTemporary);
						return;
					}
					String usernameStorage = (sharedPrefSettings.getString(
							"balanceUsername", null));
					String passwordStorage = (sharedPrefSettings.getString(
							"balancePassword", null));
					if (usernameStorage != null && passwordStorage != null) {
						editor.remove("debitBalance");
						editor.remove("pointBalance");
						debitBalanceTemporary = null;
						pointsBalanceTemporary = null;
						startFetchBalanceTask(usernameStorage, passwordStorage);
						return;
					}
				}
				if (item == 1) {
					Fragment f = fragmentManager
							.findFragmentById(R.id.BottomFrag);
					Fragment f2 = new MealPointButtonFragment();
					FragmentTransaction ft = getSupportFragmentManager()
							.beginTransaction();
					ft.remove(f);
					ft.add(R.id.BottomFrag, f2);
					ft.commit();
					editor.remove("debitBalance");
					editor.remove("pointBalance");
					editor.remove("balanceUsername");
					editor.remove("balancePassword");
					editor.commit();
					usernameTemporary = null;
					passwordTemporary = null;
					debitBalanceTemporary = null;
					pointsBalanceTemporary = null;
					setBottomFragStatus("unpressed");
					dialog.dismiss();
				}
				if (item == 2) {
					dialog.dismiss();
				}

			}
		});
		AlertDialog alert = builder.create();
		alert.show();

	}

	/**
	 * AsyncTask to send Ratings to server
	 * 
	 * @author Jeff Butterfield and Shouvik Dutta
	 * 
	 */
	private class Rate extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			sendRating(params[0], params[1], params[2]);
			return null;
		}

		/**
		 * Sends new and old rating to server in the background
		 * 
		 * @param dishNumber
		 *            The id for meal
		 * @param rating
		 *            Rating to send
		 * @param oldRating
		 *            Rating to remove
		 */
		public void sendRating(String dishNumber, String rating,
				String oldRating) {
			HttpClient client = new DefaultHttpClient();
			HttpParams params = client.getParams();
			HttpClientParams.setRedirecting(params, true);
			HttpPost post = new HttpPost(
					"http://berkeleydining.appspot.com/rate");
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						1);
				nameValuePairs.add(new BasicNameValuePair("dish_number",
						dishNumber));
				nameValuePairs
						.add(new BasicNameValuePair("dish_rating", rating));
				nameValuePairs.add(new BasicNameValuePair("remove", oldRating));
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				client.execute(post);

			} catch (Exception e) {
				Toast.makeText(getBaseContext(), "Fail?", Toast.LENGTH_LONG)
						.show();
			}
		}

	}

	/**
	 * Returns UsernameTemporary, a field that temporarily holds the user's
	 * username.
	 */
	public String getUsernameTemporary() {
		return usernameTemporary;
	}

	/**
	 * Returns PasswordTemporary, a field that temporarily holds the user's
	 * password.
	 */
	public String getPasswordTemporary() {
		return passwordTemporary;
	}

	/**
	 * Returns debitBalanceTemporary, a field that temporarily holds the
	 * username's debit balance.
	 */
	public String getdebitBalanceTemporary() {
		return debitBalanceTemporary;
	}

	/**
	 * Returns pointsBalanceTemporary, a field that temporarily holds the
	 * username's meal point balance.
	 */
	public String getpointsBalanceTemporary() {
		return pointsBalanceTemporary;
	}

	/** Removes the ratingbar when LateNight menu is displayed. */
	public void restoreBottomRatingFragment() {
		if (currentMeal.equals("LateNight")) {
			Fragment f = fragmentManager.findFragmentById(R.id.BottomFrag);
			Fragment f2 = new NoRatingBarFragment();
			FragmentTransaction ft = fragmentManager.beginTransaction();
			ft.remove(f);
			ft.add(R.id.BottomFrag, f2);
			ft.commit();
		}
	}

	/**
	 * Restores the bottom MealPoint Fragment when the user returns to the main
	 * menu.
	 */
	public void restoreBottomFragment() {
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		if (getBottomFragStatus().equals("loading")) {
			fragmentTransaction.replace(R.id.BottomFrag,
					new MealPointLoadingFragment());
			fragmentTransaction.commit();
		} else if (getBottomFragStatus().equals("loaded")) {

			fragmentTransaction.replace(R.id.BottomFrag,
					new MealPointLoadedFragment());
			fragmentTransaction.commit();
		} else {

			fragmentTransaction.replace(R.id.BottomFrag,
					new MealPointButtonFragment());
			fragmentTransaction.commit();
		}
	}

	/**
	 * Executes a RefreshMenuTask that gets the most recent data (menus and
	 * votes) from the web.
	 */
	public void refreshMenuFragment() {
		RefreshMenuTask refreshTask = new RefreshMenuTask();
		refreshTask.execute((Void[]) null);

		GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession("UA-31032997-1", this);
		tracker.setDebug(true);
		tracker.trackPageView("/menu");
		tracker.dispatch();
		tracker.stopSession();
	}

	/**
	 * AsyncTask to get the most recent data (menus and votes) from the web.
	 * 
	 * @author Jeff Butterfield and Shouvik Dutta
	 * 
	 */
	private class RefreshMenuTask extends AsyncTask<Void, Void, Void> {
		DiningHalls dining;

		@Override
		protected Void doInBackground(Void... params) {
			MenuGetter menu = new MenuGetter();
			dining = menu.menuFetch();
			return null;
		}

		@Override
		protected void onPreExecute() {
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			fragmentTransaction
					.replace(R.id.Main, new RefreshingMenuFragment());
			fragmentTransaction.commit();
		}

		@Override
		protected void onPostExecute(Void unused) {
			CurrentMenu.halls = dining;
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			fragmentTransaction.replace(R.id.Main, new MenusFragment());
			fragmentTransaction.commit();
		}

	}

	/** Sets the currentMeal to parameter Meal. */
	public void setCurrentMeal(String meal) {
		currentMeal = meal;

	}

	/** Returns the currentMeal. */
	public String getCurrentMeal() {
		if (currentMeal == null) {
			resetMenu();
			currentMeal = "Lunch";
		}
		return currentMeal;
	}

	/** Restores the top Fragment when the user returns to the main menu. */
	public void restoreTopFragment() {
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		// fragmentTransaction.replace(R.id.Title, new TitleBarFragment());
		fragmentTransaction.commit();
	}

	public void restoreMiddleFragment() {
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.Main, new MainFragment());
		fragmentTransaction.commit();
	}

	/** Sets the current Dining Hall to Hall. */
	public void setCurrentHall(Meals hall) {
		currentHall = hall;
	}

	/** Returns the currentHall. */
	public Meals getCurrentHall() {
		if (currentHall == null) {
			resetMenu();
			currentHall = CurrentMenu.halls.Crossroads;
		}
		return currentHall;
	}

	public void resetMenu() {
		SharedPreferences prefs = getSharedPreferences("Prefs", 0);
		String json = prefs.getString("json", null);
		Gson gson = new Gson();
		CurrentMenu.halls = gson.fromJson(json, DiningHalls.class);
	}

	/**
	 * Returns the variable bottomFragStatus, which determines if the fragment
	 * should be displaying a loading message or not.
	 */
	public String getBottomFragStatus() {
		return bottomFragStatus;
	}

	/**
	 * Sets the variable bottomFragStatus to BottomFrag, which determines if the
	 * fragment should be displaying a loading message or not.
	 */
	public void setBottomFragStatus(String bottomFrag) {
		bottomFragStatus = bottomFrag;
	}

	boolean rememberMeChecked;
	private String usernameTemporary;
	private String passwordTemporary;
	private String debitBalanceTemporary;
	private String pointsBalanceTemporary;
	private String bottomFragStatus;
	private String currentMeal;
	private Meals currentHall;
	FragmentManager fragmentManager;
	SharedPreferences sharedPrefSettings;
	SharedPreferences.Editor editor;
	fetchBalanceTask fetchBalance;

}
