/**
 * 
 * The fragment that indicates that the balances is loading. 
 * 
 * @author Jeff Butterfield and Shouvik Dutta
 * 
 */

package com.appspot.berkeleydining;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MealPointLoadingFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mpbuttonloading_layout, container, false);

        return v;

    }

}