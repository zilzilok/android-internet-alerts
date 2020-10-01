package ru.zilzilok.ict.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.r0adkll.slidr.Slidr;

import java.util.Locale;

import ru.zilzilok.ict.R;
import ru.zilzilok.ict.utils.adapter.ConnectionStatisticAdapter;
import ru.zilzilok.ict.utils.database.Databases;
import ru.zilzilok.ict.utils.locale.LanguageSettings;
import ru.zilzilok.ict.utils.resources.geolocation.GeoLocationPermission;

/**
 * Activity for application statistics.
 */
public class StatisticsActivity extends AppCompatActivity {
    private static final int NUM_COLUMNS = 3;
    private static final String SAVE_KEY = "save_text";
    private static final String TAG = "StatisticsActivity";

    private ConnectionStatisticAdapter gridAdapter; //  adapter for connection statistics
    private SwitchCompat switchCompat;              //  button to show/remove the location data
    private LinearLayout locationData;              //  location data about geolocation of last selected/appeared
    private SharedPreferences spSpinner;            //  preference data about current spinner choice
    private SharedPreferences spSwitchCompat;       //  preference data about current switchCompat choice
    private AlertDialog restartAlert;               //  dialog for restarting application

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
        initializeRestartAlertDialog();
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
                String funcName = "[initializeSpinner.onItemSelected]";

                Log.i(TAG, String.format("%s %d item was selected.", funcName, position + 1));

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

    private void initializeSwitchCompat() {
        String funcName = "[initializeSwitchCompat]";

        switchCompat = findViewById(R.id.statSwitchCompat);
        locationData = findViewById(R.id.locationData);
        boolean checkPerm = GeoLocationPermission.checkPermission(StatisticsActivity.this);
        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.i(TAG, String.format("%s SwitchCompat checked status - %b.", funcName, checkPerm));

            SharedPreferences.Editor editor = spSwitchCompat.edit();
            editor.putBoolean(SAVE_KEY, isChecked);
            editor.apply();
            if (isChecked) {
                if (!checkPerm)
                    GeoLocationPermission.requestPermission(StatisticsActivity.this);
                else {
                    locationData.setVisibility(View.VISIBLE);

                    Log.i(TAG, String.format("%s Location data visible.", funcName));
                }
            } else {
                locationData.setVisibility(View.GONE);

                Log.i(TAG, String.format("%s Location data invisible.", funcName));
            }
        });
        boolean isChecked = spSwitchCompat.getBoolean(SAVE_KEY, false) && checkPerm;
        switchCompat.setChecked(isChecked);
        locationData.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        Log.i(TAG, String.format("%s Location data %s.", funcName, isChecked ? "visible" : "invisible"));

        TextView lastSelected = locationData.findViewById(R.id.statLastSelectedTextView);
        TextView lastAppeared = locationData.findViewById(R.id.statLastAppearedTextView);

        lastSelected.setText(Databases.getInstance().statisticsDBHelper.getGeolocation(true));
        lastAppeared.setText(Databases.getInstance().statisticsDBHelper.getGeolocation(false));

        LinearLayout lastSelectedLayout = locationData.findViewById(R.id.locationDataSelected);
        lastSelectedLayout.setOnClickListener(v -> {
            Double[] coords = Databases.getInstance().statisticsDBHelper.getCoordinates(true);
            if (coords != null) {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", coords[0], coords[1]);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
        LinearLayout lastAppearedLayout = locationData.findViewById(R.id.locationDataAppeared);
        lastAppearedLayout.setOnClickListener(v -> {
            Double[] coords = Databases.getInstance().statisticsDBHelper.getCoordinates(false);
            if (coords != null) {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", coords[0], coords[1]);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
    }

    private void initializeRestartAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.restart_alert)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                });
        restartAlert = builder.create();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String funcName = "onRequestPermissionsResult";

        if (requestCode == GeoLocationPermission.PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (!locationAccepted)
                switchCompat.setChecked(false);
            else {
                restartAlert.show();
                locationData.setVisibility(View.VISIBLE);
                Log.i(TAG, String.format("%s Location data visible.", funcName));
            }
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
