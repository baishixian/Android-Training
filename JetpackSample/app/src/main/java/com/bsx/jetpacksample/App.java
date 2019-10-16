package com.bsx.jetpacksample;

import android.app.Application;

import com.bsx.jetpacksample.model.RepositoryFactory;

/**
 * App
 *
 * @author baishixian
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RepositoryFactory.getInstance().init(this);
    }
}
