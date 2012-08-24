/**
 * The Activity that provides the application's intro and loads
 * menu data. 
 * 
 * @author Jeff Butterfield and Shouvik Dutta
 * 
 */

package com.appspot.berkeleydining;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.gson.Gson;
import com.mixpanel.android.mpmetrics.MPMetrics;

public class IntroLoadDataActivity extends SherlockActivity {

    int count = 0;
    DiningHalls dining;
    Meals crossroads;
    Meals cafe3;
    Meals foothill;
    Meals meals;
    String currentMeal;
    String cardInfo;
    Meal displayMeal;
    Context myContext = this;
    Boolean hasMenu = false;
    Boolean error = false;
    String meal;
    SharedPreferences sharedPrefSettings;
    Vibrator v;
    ArrayList<String> categoryList;
    AlertDialog alert = null;
    MPMetrics mMPMetrics;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intropage_layout);
        mMPMetrics = ((CalMealsApplication) getApplication()).getMPInstance();
        mMPMetrics.track("Menu Download", null);
        sharedPrefSettings = getSharedPreferences("Prefs", 0);
        getSupportActionBar().hide();
        downloadData();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMPMetrics.flushAll();
    }

    public void downloadData() {
        if (alert != null) {
            alert.dismiss();
        }
        if (isOnline()) {
            dataFetch asyncFetch = new dataFetch();
            asyncFetch.execute((Void[]) null);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Download failed: Please check your connection and try again")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            downloadData();
                        }
                    });
            alert = builder.create();
            alert.show();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    /** Initializes default ratings in SharedPreferences. */
    public void initializeGeneralRatings() {
        Editor prefEd = sharedPrefSettings.edit();
        prefEd.putFloat("CROSSROADS_BREAKFAST", CurrentMenu.halls.Crossroads.Breakfast.rating)
                .commit();
        prefEd.putFloat("CROSSROADS_LUNCH", CurrentMenu.halls.Crossroads.Lunch.rating).commit();
        prefEd.putFloat("CROSSROADS_DINNER", CurrentMenu.halls.Crossroads.Dinner.rating).commit();
        prefEd.putFloat("CAFE3_BREAKFAST", CurrentMenu.halls.Cafe3.Breakfast.rating).commit();
        prefEd.putFloat("CAFE3_LUNCH", CurrentMenu.halls.Cafe3.Lunch.rating).commit();
        prefEd.putFloat("CAFE3_DINNER", CurrentMenu.halls.Cafe3.Dinner.rating).commit();
        prefEd.putFloat("FOOTHILL_BREAKFAST", CurrentMenu.halls.Foothill.Breakfast.rating).commit();
        prefEd.putFloat("FOOTHILL_LUNCH", CurrentMenu.halls.Foothill.Lunch.rating).commit();
        prefEd.putFloat("FOOTHILL_DINNER", CurrentMenu.halls.Foothill.Dinner.rating).commit();
        prefEd.putFloat("CKC_BREAKFAST", CurrentMenu.halls.ClarkKerr.Breakfast.rating).commit();
        prefEd.putFloat("CKC_LUNCH", CurrentMenu.halls.ClarkKerr.Lunch.rating).commit();
        prefEd.putFloat("CKC_DINNER", CurrentMenu.halls.ClarkKerr.Dinner.rating).commit();
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_WEEK);
        prefEd.putInt("DAY", day).commit();
    }

    /**
     * Takes the ratings that were fetched from the web and stores them in
     * SharedPreferences; called from onCreate()
     */
    public void initializeUserRatings() {
        Editor prefEd = sharedPrefSettings.edit();
        prefEd.putBoolean("HAS_RATING", true).commit();
        prefEd.putFloat("CROSSROADS_BREAKFAST_USER", -1).commit();
        prefEd.putFloat("CROSSROADS_LUNCH_USER", -1).commit();
        prefEd.putFloat("CROSSROADS_DINNER_USER", -1).commit();
        prefEd.putFloat("CAFE3_BREAKFAST_USER", -1).commit();
        prefEd.putFloat("CAFE3_LUNCH_USER", -1).commit();
        prefEd.putFloat("CAFE3_DINNER_USER", -1).commit();
        prefEd.putFloat("FOOTHILL_BREAKFAST_USER", -1).commit();
        prefEd.putFloat("FOOTHILL_LUNCH_USER", -1).commit();
        prefEd.putFloat("FOOTHILL_DINNER_USER", -1).commit();
        prefEd.putFloat("CKC_BREAKFAST_USER", -1).commit();
        prefEd.putFloat("CKC_LUNCH_USER", -1).commit();
        prefEd.putFloat("CKC_DINNER_USER", -1).commit();
    }

    /**
     * The AsyncTask that loads menu data into a DiningHalls class.
     * 
     * @author Jeff Butterfield and Shouvik Dutta
     * 
     */
    private class dataFetch extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            MenuGetter menu = new MenuGetter();
            dining = menu.menuFetch();
            crossroads = dining.Crossroads;
            cafe3 = dining.Cafe3;
            foothill = dining.Foothill;
            meals = dining.ClarkKerr;
            return null;
        }

        @Override
        protected void onPreExecute() {
            CurrentMenu.halls = dining;
        }

        @Override
        protected void onPostExecute(Void unused) {
            hasMenu = true;
            CurrentMenu.halls = dining;
            Calendar c = Calendar.getInstance();
            int day = c.get(Calendar.DAY_OF_WEEK);
            if (sharedPrefSettings.getInt("DAY", -1) != day) {
                initializeUserRatings();
            }
            if (!sharedPrefSettings.getBoolean("HAS_RATING", false)) {
                initializeUserRatings();
            }
            initializeGeneralRatings();

            if (!sharedPrefSettings.getBoolean("HAS_RATING", false)) {
                initializeUserRatings();
                initializeGeneralRatings();
            }

            Gson gson = new Gson();
            String json = gson.toJson(dining);
            sharedPrefSettings.edit().putString("json", json).commit();
            Intent myIntent = new Intent(getBaseContext(), CalMealsActivity.class);
            startActivityForResult(myIntent, 0);
        }

    }

}
