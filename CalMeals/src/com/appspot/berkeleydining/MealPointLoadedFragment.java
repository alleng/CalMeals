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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class MealPointLoadedFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mpbalances_layout, container, false);
        TextView pointVal = (TextView) v.findViewById(R.id.pointsVal);
        TextView debitVal = (TextView) v.findViewById(R.id.debitVal);
        String pointText;
        String debitText;

        String temppoints = ((CalMealsActivity) getActivity()).getpointsBalanceTemporary();
        String tempdebit = ((CalMealsActivity) getActivity()).getdebitBalanceTemporary();

        if (temppoints != null && tempdebit != null) {
            pointText = temppoints;

            debitText = tempdebit;
        } else {

            SharedPreferences settings = getActivity().getSharedPreferences("Prefs", 0);
            pointText = settings.getString("pointBalance", "Error");
            debitText = settings.getString("debitBalance", "Error");
        }
        pointVal.setText(pointText);
        debitVal.setText(debitText);
        final ImageButton button = (ImageButton) v.findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if (!((DashboardTesterActivity)
                // getActivity()).getBalanceRequested()) {
                ((CalMealsActivity) getActivity()).showOptions();
                // }
            }
        });
        return v;

    }

}