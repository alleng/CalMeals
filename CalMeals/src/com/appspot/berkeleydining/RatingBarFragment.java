package com.appspot.berkeleydining;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;

public class RatingBarFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefSettings = getActivity().getSharedPreferences("Prefs", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.firstrating_layout, container, false);
        float userRating = -1;
        currentHall = ((MenuActivity) getActivity()).getCurrentHall();
        currentMeal = ((MenuActivity) getActivity()).getCurrentMeal();
        Log.i("ratingBarFragment", currentMeal);
        if (currentHall.equals(CurrentMenu.halls.Crossroads)) {
            if (currentMeal.equals("Breakfast")
                    && sharedPrefSettings.getFloat("CROSSROADS_BREAKFAST_USER", -1) != -1) {
                userRating = sharedPrefSettings.getFloat("CROSSROADS_BREAKFAST_USER", -1);
            } else if (currentMeal.equals("Lunch")
                    && sharedPrefSettings.getFloat("CROSSROADS_LUNCH_USER", -1) != -1) {
                userRating = sharedPrefSettings.getFloat("CROSSROADS_LUNCH_USER", -1);
            } else if (currentMeal.equals("Dinner")
                    && sharedPrefSettings.getFloat("CROSSROADS_DINNER_USER", -1) != -1) {
                userRating = sharedPrefSettings.getFloat("CROSSROADS_DINNER_USER", -1);
            }
        } else if (currentHall.equals(CurrentMenu.halls.Cafe3)) {
            if (currentMeal.equals("Breakfast")
                    && sharedPrefSettings.getFloat("CAFE3_BREAKFAST_USER", -1) != -1) {
                userRating = sharedPrefSettings.getFloat("CAFE3_BREAKFAST_USER", -1);
            } else if (currentMeal.equals("Lunch")
                    && sharedPrefSettings.getFloat("CAFE3_LUNCH_USER", -1) != -1) {
                userRating = sharedPrefSettings.getFloat("CAFE3_LUNCH_USER", -1);
            } else if (currentMeal.equals("Dinner")
                    && sharedPrefSettings.getFloat("CAFE3_DINNER_USER", -1) != -1) {
                userRating = sharedPrefSettings.getFloat("CAFE3_DINNER_USER", -1);
            }
        } else if (currentHall.equals(CurrentMenu.halls.Foothill)) {
            if (currentMeal.equals("Breakfast")
                    && sharedPrefSettings.getFloat("FOOTHILL_BREAKFAST_USER", -1) != -1) {
                userRating = sharedPrefSettings.getFloat("FOOTHILL_BREAKFAST_USER", -1);
            } else if (currentMeal.equals("Lunch")
                    && sharedPrefSettings.getFloat("FOOTHILL_LUNCH_USER", -1) != -1) {
                userRating = sharedPrefSettings.getFloat("FOOTHILL_LUNCH_USER", -1);
            } else if (currentMeal.equals("Dinner")
                    && sharedPrefSettings.getFloat("FOOTHILL_DINNER_USER", -1) != -1) {
                userRating = sharedPrefSettings.getFloat("FOOTHILL_DINNER_USER", -1);
            }
        } else if (currentHall.equals(CurrentMenu.halls.ClarkKerr)) {
            if (currentMeal.equals("Breakfast")
                    && sharedPrefSettings.getFloat("CKC_BREAKFAST_USER", -1) != -1) {
                userRating = sharedPrefSettings.getFloat("CKC_BREAKFAST_USER", -1);
            } else if (currentMeal.equals("Lunch")
                    && sharedPrefSettings.getFloat("CKC_LUNCH_USER", -1) != -1) {
                userRating = sharedPrefSettings.getFloat("CKC_LUNCH_USER", -1);
            } else if (currentMeal.equals("Dinner")
                    && sharedPrefSettings.getFloat("CKC_DINNER_USER", -1) != -1) {
                userRating = sharedPrefSettings.getFloat("CKC_DINNER_USER", -1);
            }
        }

        setRatingBar(v, userRating);

        ImageButton b = (ImageButton) v.findViewById(R.id.button2);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MenuActivity) getActivity()).showRatingAlert();
            }
        });

        return v;
    }

    public void setRatingBar(View v, float userRating) {
        float averageRating = 0;
        averageRating = currentHall.getMeal(currentMeal).rating;
        RatingBar newRb = (RatingBar) v.findViewById(R.id.ratingBar2);
        if (userRating == -1) {
            newRb.setRating(averageRating);
        } else {
            newRb.setRating(userRating);
        }
    }

    SharedPreferences sharedPrefSettings;
    View v;
    Meals currentHall;
    String currentMeal;
}