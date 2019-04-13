package com.aurora.paperviewerprocessor;

import android.app.Application;
import android.content.Context;

/**
 * This class is used to obtain a context to retrieve the drawable images
 * TODO: remove this class when the images are received from aurora
 */
public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        // Avoid the static error because i find no way of avoiding this
        // because it has to be referenced from a static context
        // And eventually this class will be removed, it is only used to retrieve a context
        mContext = this; //NOSONAR
    }

    public static Context getContext(){
        return mContext;
    }
}
