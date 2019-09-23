package com.meow.androiddesertjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleObserver;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.meow.androiddesertjava.databinding.ActivityMainBinding;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class MainActivity extends AppCompatActivity implements LifecycleObserver {
    // the binding for main activity
    private ActivityMainBinding binding;
    // data
    private int revenue = 0, dessertsSold = 0;
    private final List<Dessert> allDesserts;
    private Dessert currentDessert;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        // Use Data binding instead
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.desertButton.setOnClickListener(view -> onDesertClicked());

        // set the data
        binding.setRevenue(revenue);
        binding.setAmountSold(dessertsSold);

        final int imageId = currentDessert.getImageId();
        binding.desertButton.setImageResource(imageId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu first
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // execute when an item in options menu is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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
}
