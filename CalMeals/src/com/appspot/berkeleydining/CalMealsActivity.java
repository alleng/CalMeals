/**
 * The central activity where all the menus, ratings, and 
 * balances can be viewed.
 * 
 * @author Jeff Butterfield and Shouvik Dutta
 * 
 */

package com.appspot.berkeleydining;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

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
        currentHall = CurrentMenu.halls.Crossroads;
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
