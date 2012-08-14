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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.gson.Gson;

public class MenuActivity extends SherlockFragmentActivity {
    private String currentMeal;
    private Meals currentHall;
    SharedPreferences sharedPrefSettings;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPrefSettings = getSharedPreferences("Prefs", 0);
        editor = sharedPrefSettings.edit();

        // setBottomFragStatus("unpressed");
        currentMeal = getIntent().getStringExtra("currentMeal");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // fragmentTransaction.add(R.id.Title, new TitleBarFragment());
        fragmentTransaction.add(R.id.Main, new MenusFragment(), "menu");
        fragmentTransaction.replace(R.id.BottomFrag, new RatingBarFragment());

        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_refresh:
            refreshMenuFragment();
            break;
        case android.R.id.home:
            finish();
        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /** Generates the prompt for rating individual menu meals. */
    public void showRatingAlert() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View rateEntryView = factory.inflate(R.layout.ratedialog_layout, null);
        TextView averageTv = (TextView) rateEntryView.findViewById(R.id.aveargeRatingTextView);
        TextView voteCountTv = (TextView) rateEntryView.findViewById(R.id.numVotesTextView);
        float averageRating = currentHall.getMeal(currentMeal).rating;
        int voteCount = currentHall.getMeal(currentMeal).number_ratings;
        averageTv.setText("Current Rating: " + averageRating + " Stars");
        voteCountTv.setText("Total Votes: " + voteCount);

        AlertDialog infoRequest = new AlertDialog.Builder(this).setView(rateEntryView)
                .setTitle("Rate this Meal")
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {

                        final RatingBar rb2 = (RatingBar) rateEntryView
                                .findViewById(R.id.ratingBar2);
                        float rating = rb2.getRating();
                        Log.i("input", "" + rating);
                        String rateId;
                        String memKey = "";
                        int caseNumber = 0;
                        if (currentHall.equals(CurrentMenu.halls.Crossroads)) {
                            memKey = "CROSSROADS_" + currentMeal.toUpperCase();
                            caseNumber = 0;
                        }
                        if (currentHall.equals(CurrentMenu.halls.Cafe3)) {
                            memKey = "CAFE3_" + currentMeal.toUpperCase();
                            caseNumber = 1;
                        }
                        if (currentHall.equals(CurrentMenu.halls.Foothill)) {
                            memKey = "FOOTHILL_" + currentMeal.toUpperCase();
                            caseNumber = 2;
                        }
                        if (currentHall.equals(CurrentMenu.halls.ClarkKerr)) {
                            memKey = "CKC_" + currentMeal.toUpperCase();
                            caseNumber = 3;
                        }
                        rb2.setRating(sharedPrefSettings.getFloat(memKey, -1));
                        String memKeyUse = memKey + "_USER";
                        if (currentMeal.equals("Breakfast")) {
                            rateId = Integer.toString(caseNumber * 3 + 0);
                            System.out.println(rateId);
                            System.out.println(rating);
                        } else if (currentMeal.equals("Lunch")) {
                            rateId = Integer.toString(caseNumber * 3 + 1);
                        } else {
                            rateId = Integer.toString(caseNumber * 3 + 2);
                        }
                        String[] params = { rateId, Float.toString(rating),
                                Float.toString(sharedPrefSettings.getFloat(memKeyUse, -1)) };

                        new Rate().execute(params);

                        sharedPrefSettings.edit().putFloat(memKeyUse, rating).commit();
                        Toast.makeText(getBaseContext(), "Rating Submitted", Toast.LENGTH_SHORT)
                                .show();
                        FragmentManager fragMan = getSupportFragmentManager();
                        RatingBarFragment f = (RatingBarFragment) fragMan
                                .findFragmentById(R.id.BottomFrag);
                        f.setRatingBar(f.v, rating);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {

                        dialog.dismiss();
                    }
                }).create();
        infoRequest.show();
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
        public void sendRating(String dishNumber, String rating, String oldRating) {
            HttpClient client = new DefaultHttpClient();
            HttpParams params = client.getParams();
            HttpClientParams.setRedirecting(params, true);
            HttpPost post = new HttpPost("http://berkeleydining.appspot.com/rate");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("dish_number", dishNumber));
                nameValuePairs.add(new BasicNameValuePair("dish_rating", rating));
                nameValuePairs.add(new BasicNameValuePair("remove", oldRating));
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                client.execute(post);

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Fail?", Toast.LENGTH_LONG).show();
            }
        }

    }

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
            FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction();
            fragmentTransaction.replace(R.id.Main, new RefreshingMenuFragment());
            fragmentTransaction.commit();
        }

        @Override
        protected void onPostExecute(Void unused) {
            CurrentMenu.halls = dining;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction();
            fragmentTransaction.replace(R.id.Main, new MenusFragment(), "menu");
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

    /** Removes the ratingbar when LateNight menu is displayed. */
    public void restoreBottomRatingFragment() {
        if (currentMeal.equals("LateNight")) {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.BottomFrag);
            Fragment f2 = new NoRatingBarFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(f);
            ft.add(R.id.BottomFrag, f2);
            ft.commit();
        }
    }

}