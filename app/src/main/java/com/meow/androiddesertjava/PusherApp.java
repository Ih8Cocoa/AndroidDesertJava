package com.meow.androiddesertjava;

import android.app.Application;

import timber.log.Timber;
import timber.log.Timber.DebugTree;

public final class PusherApp extends Application {
    // (1) Adding Tiber logging to our app

    @Override
    public void onCreate() {
        super.onCreate();
        // (2) setup Timber
        final DebugTree debugTree = new DebugTree();
        Timber.plant(debugTree);
    }
}
