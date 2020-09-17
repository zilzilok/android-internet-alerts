package ru.zilzilok.ict.activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.r0adkll.slidr.Slidr;

import java.util.Locale;

import ru.zilzilok.ict.R;
import ru.zilzilok.ict.utils.adapter.ConnectionStatisticAdapter;
import ru.zilzilok.ict.utils.locale.LanguageSettings;
import ru.zilzilok.ict.utils.resources.geolocation.GeoLocationPermission;

public class StatisticsActivity extends AppCompatActivity {
    private static final int NUM_COLUMNS = 3;

    private ConnectionStatisticAdapter gridAdapter;
    private SwitchCompat switchCompat;
    private LinearLayout locationData;
    private SharedPreferences spSpinner;
    private SharedPreferences spSwitchCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageSettings.initializeAppLanguage(this);
        setContentView(R.layout.activity_statistics);

        setTitle(R.string.stat_button);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Slidr.attach(this);

        spSpinner = getSharedPreferences("spinner", MODE_PRIVATE);
        spSwitchCompat = getSharedPreferences("switch_compat", MODE_PRIVATE);

        initializeGridView();
        initializeSpinner();
        initializeSwitchCompat();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeGridView() {
        GridView gridView = findViewById(R.id.statGridView);
        gridAdapter = new ConnectionStatisticAdapter(this);
        gridView.setAdapter(gridAdapter);
        gridView.setNumColumns(NUM_COLUMNS);
        gridView.setOnTouchListener((v, event) -> event.getAction() == MotionEvent.ACTION_MOVE);
    }

    private void initializeSpinner() {
        Spinner spinner = findViewById(R.id.statSpinner);
        ArrayAdapter<?> spinnerAdapter =
                ArrayAdapter.createFromResource(this, R.array.stat_number_connection_types,
                        android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(spSpinner.getInt(SAVE_KEY, 0));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = spSpinner.edit();
                editor.putInt(SAVE_KEY, position);
                editor.apply();
                switch (position) {
                    case 0:
                        gridAdapter.updateView(1);
                        break;
                    case 1:
                        gridAdapter.updateView(0);
                        break;
                    default:
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private static final String SAVE_KEY = "save_text";

    private void initializeSwitchCompat() {
        switchCompat = findViewById(R.id.statSwitchCompat);
        locationData = findViewById(R.id.locationData);
        boolean checkPerm = GeoLocationPermission.checkPermission(StatisticsActivity.this);
        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = spSwitchCompat.edit();
            editor.putBoolean(SAVE_KEY, isChecked);
            editor.apply();
            if (isChecked) {
                if (!checkPerm)
                    GeoLocationPermission.requestPermission(StatisticsActivity.this);
                else
                    locationData.setVisibility(View.VISIBLE);
            } else
                locationData.setVisibility(View.GONE);
        });
        boolean isChecked = spSwitchCompat.getBoolean(SAVE_KEY, false) && checkPerm;
        switchCompat.setChecked(isChecked);
        locationData.setVisibility(isChecked ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == GeoLocationPermission.PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (!locationAccepted)
                switchCompat.setChecked(false);
            else
                locationData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
