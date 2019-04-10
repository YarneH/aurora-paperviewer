package com.aurora.paperviewerprocessor;

import android.app.Application;
import android.content.Context;

/**
 * This class is used to obtain a context to retrieve the drawable images
 * TODO: retrieve this class when the images are received from aurora
 */
public class App extends Application{

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}