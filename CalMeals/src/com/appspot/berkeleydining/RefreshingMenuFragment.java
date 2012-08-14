/**
 * A fragment that indicates to the user that the 
 * menu data for the application is currently being refreshed.
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

public class RefreshingMenuFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.refreshpageviewer_layout, container, false);

        return v;

    }

}