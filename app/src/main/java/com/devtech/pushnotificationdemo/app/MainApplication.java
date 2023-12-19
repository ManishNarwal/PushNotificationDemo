package com.devtech.pushnotificationdemo.app;

import android.app.Application;

import com.devtech.pushnotificationdemo.BuildConfig;
import com.google.firebase.FirebaseApp;

import dagger.hilt.android.HiltAndroidApp;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

@HiltAndroidApp
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) Timber.plant(new DebugTree());
        FirebaseApp.initializeApp(this);
    }
}
