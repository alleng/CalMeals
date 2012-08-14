/**
 * A fragment that contains the 4 main meal buttons on the main 
 * page. It handles some basic FragmentTransactions.
 * 
 * @author Jeff Butterfield and Shouvik Dutta
 * 
 */

package com.appspot.berkeleydining;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.customdash, container, false);

        // Set up the Breakfast button
        ImageButton breakfastButton = (ImageButton) v.findViewById(R.id.breakfastbutton);
        breakfastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MenuActivity.class);
                i.putExtra("currentMeal", "Breakfast");
                i.putExtra("mealNumber", 0);
                startActivity(i);
            }

        });

        // Set up the Lunch button
        ImageButton lunchButton = (ImageButton) v.findViewById(R.id.lunchbutton);
        lunchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MenuActivity.class);
                i.putExtra("currentMeal", "Lunch");
                i.putExtra("mealNumber", 1);
                startActivity(i);
            }

        });
        // Set up the Dinner button
        ImageButton dinnerButton = (ImageButton) v.findViewById(R.id.dinnerbutton);
        dinnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MenuActivity.class);
                i.putExtra("currentMeal", "Dinner");
                i.putExtra("mealNumber", 2);
                startActivity(i);
            }

        });
        // Set up the LateNight button
        ImageButton latenightButton = (ImageButton) v.findViewById(R.id.latenightbutton);
        latenightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MenuActivity.class);
                i.putExtra("currentMeal", "LateNight");
                i.putExtra("mealNumber", 3);
                startActivity(i);
            }

        });

        return v;

    }

    @Override
    public void onResume() {
        super.onResume();
        ((CalMealsActivity) getActivity()).restoreBottomFragment();
    }

}