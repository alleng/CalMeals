package com.appspot.berkeleydining;

import android.app.Application;

import com.mixpanel.android.mpmetrics.MPMetrics;

public class CalMealsApplication extends Application {
    private MPMetrics mMPMetrics;

    @Override
    public void onCreate() {
        super.onCreate();
        mMPMetrics = getMPInstance();
        mMPMetrics.track("App Launched", null);
    }

    public MPMetrics getMPInstance() {
        return MPMetrics.getInstance(this, "a3bbfb33c58ef9c4348c3fcb1d38f830");
    }
}
