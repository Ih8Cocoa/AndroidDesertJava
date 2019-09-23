package com.meow.androiddesertjava;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleObserver;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.meow.androiddesertjava.databinding.ActivityMainBinding;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public final class MainActivity extends AppCompatActivity implements LifecycleObserver {
    // the binding for main activity
    private ActivityMainBinding binding;
    // data
    private int revenue = 0, dessertsSold = 0;

    // (12) constant keys for onSaveInstanceState and onStart
    private final String KEY_REVENUE = "revenue", KEY_DESSERTS_SOLD = "desserts_sold",
    KEY_DESSERT_TIMER = "dessert_timer";

    private final List<Dessert> allDesserts;
    private Dessert currentDessert;

    // (5) Initialize DessertTimer
    private DessertTimer timer = new DessertTimer(
            // (9) we also need to pass in MainActivity's lifecycle here
            getLifecycle()
    );

    public MainActivity() {
        List<Dessert> desserts = Arrays.asList(
                new Dessert(R.drawable.cupcake, 5, 0),
                new Dessert(R.drawable.donut, 10, 5),
                new Dessert(R.drawable.eclair, 15, 20),
                new Dessert(R.drawable.froyo, 30, 50),
                new Dessert(R.drawable.gingerbread, 50, 100),
                new Dessert(R.drawable.honeycomb, 100, 200),
                new Dessert(R.drawable.ics, 500, 500),
                new Dessert(R.drawable.jellybean, 1000, 1000),
                new Dessert(R.drawable.kitkat, 2000, 2000),
                new Dessert(R.drawable.lollipop, 3000, 4000),
                new Dessert(R.drawable.marshmallow, 4000, 8000),
                new Dessert(R.drawable.nougat, 5000, 16000),
                new Dessert(R.drawable.oreo, 6000, 20000)
        );
        allDesserts = Collections.unmodifiableList(desserts);
        currentDessert = allDesserts.get(0);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        // Use Data binding instead
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.desertButton.setOnClickListener(view -> onDesertClicked());

        // (13) Try restoring our saved state if available
        if (savedInstanceState != null) {
            // get the revenue and dessertsSold
            revenue = savedInstanceState.getInt(KEY_REVENUE, 0);
            dessertsSold = savedInstanceState.getInt(KEY_DESSERTS_SOLD, 0);
            int timerSeconds = savedInstanceState.getInt(KEY_DESSERT_TIMER, 0);
            timer.setSecondsCount(timerSeconds);
        }

        // set the data
        binding.setRevenue(revenue);
        binding.setAmountSold(dessertsSold);

        final int imageId = currentDessert.getImageId();
        binding.desertButton.setImageResource(imageId);

        // (3) try calling Timber
        Timber.i("onCreate called");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu first
        getMenuInflater().inflate(R.menu.main_menu, menu);
        Timber.i("onCreateOptionsMenu called");
        return super.onCreateOptionsMenu(menu);
    }

    // execute when an item in options menu is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Timber.i("onOptionsItemSelected called");
        final int itemId = item.getItemId();
        if (itemId == R.id.share_menu_button) {
            // execute the sharing method
            onShare();
        }
        return super.onOptionsItemSelected(item);
    }

    // Update the score when the desert is clicked
    private void onDesertClicked() {
        revenue += currentDessert.getPrice();
        dessertsSold++;
        // update the revenue inside binding
        binding.setRevenue(revenue);
        binding.setAmountSold(dessertsSold);
        showCurrentDesert();
    }

    // show the current desert
    private void showCurrentDesert() {
        Dessert newDessert = allDesserts.get(0);
        for (Dessert dessert : allDesserts) {
            final int startingAmount = dessert.getStartProductionAmount();
            // break out if dessertsSold is smaller the the current dessert's startingAmount
            if (dessertsSold < startingAmount) {
                break;
            }
            // otherwise, update the new dessert variable
            newDessert = dessert;
        }
        if (newDessert != currentDessert) {
            // there is a dessert difference. Begin committing the new dessert
            // to the class field
            currentDessert = newDessert;
            final int id = currentDessert.getImageId();
            binding.desertButton.setImageResource(id);
        }
    }

    // Sharing intent
    private void onShare() {
        Timber.i("onShare called");
        final String message = getString(R.string.share_text, dessertsSold, revenue);
        final Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setText(message)
                .setType("text/plain")
                .getIntent();
        // try starting the activity
        try {
            startActivity(shareIntent);
        } catch (ActivityNotFoundException e) {
            // make a toast saying you can't share
            final String notAvailableMsg = getString(R.string.sharing_not_available);
            Toast.makeText(this, notAvailableMsg, Toast.LENGTH_LONG).show();
        }
    }

    // (4) Logging activity lifecycle
    // A small note: When rotating the phone, the following methods are called:
    // onPause, onStop, onDestroy, onCreate, onStart, onResume
    // because rotating the device counts as a "configuration change" - as if
    // the app is running on a new device. As such, the app needs to be rebuilt entirely
    @Override
    protected void onStart() {
        super.onStart();
        Timber.i("onStart called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.i("onResume called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Timber.i("onPause called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Timber.i("onStop called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.i("onDestroy called");
    }

    // (11) In some cases such as intensive load of other foreground apps,
    // Android may randomly kill the application while it's on the background
    // or in cases such as device rotation, as specified above
    // So the data has to be saved manually in onSaveInstanceState
    // PAY ATTENTION: The correct method is onSaveInstanceState(Bundle bundle),
    // which only takes 1 argument of type Bundle and nothing else
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Timber.i("onSaveInstanceState called");
        // (13) let's store our revenue, dessertsSold and the current dessertTimer
        int seconds = timer.getSecondsCount();
        outState.putInt(KEY_REVENUE, revenue);
        outState.putInt(KEY_DESSERTS_SOLD, dessertsSold);
        outState.putInt(KEY_DESSERT_TIMER, seconds);
        // remember to store less than 100KB of data, or the app will crash
    }
}
