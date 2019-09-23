package com.meow.androiddesertjava;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import timber.log.Timber;

// (6) Let's make this class implement LifecycleObserver, so it can observe
// and manage its own Lifecycle
public final class DessertTimer implements LifecycleObserver {
    // The number of seconds counted since the timer started
    private int secondsCount = 0;
    private Handler handler = new Handler();
    private Runnable runnable;

    // (7) Then, we need to take a lifecycle in the constructor
    public DessertTimer(@NonNull Lifecycle lifecycle) {
        // (8) and add itself to the lifecycle passed in, as an observer
        lifecycle.addObserver(this);
        // we don't need to store the lifecycle
    }

    // (9) Annotate this method with OnLifecycleEvent annotation,
    // forcing this method to run when the app reaches ON_START
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void startTimer() {
        // Create the runnable action, which prints out a log
        // and increments the seconds counter
        runnable = () -> {
            secondsCount++;
            Timber.i("The timer is at " + secondsCount + " seconds");
            // postDelayed re-adds the action to the queue of actions the Handler is cycling
            // through. The delayMillis param tells the handler to run the runnable in
            // 1 second (1000ms)
            handler.postDelayed(runnable, 1000);
        };

        // This is what initially starts the timer
        handler.postDelayed(runnable, 1000);

        // Note that the Thread the handler runs on is determined by a class called Looper.
        // In this case, no looper is defined, and it defaults to the main or UI thread.
    }

    // (10) And also call this when the app reaches ON_STOP
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void stopTimer() {
        handler.removeCallbacks(runnable);
    }

    public int getSecondsCount() {
        return secondsCount;
    }

    public void setSecondsCount(int secondsCount) {
        this.secondsCount = secondsCount;
    }
}
