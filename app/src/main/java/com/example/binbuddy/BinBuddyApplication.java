package com.example.binbuddy;

import android.app.Application;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class BinBuddyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
