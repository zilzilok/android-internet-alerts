package ru.zilzilok.ict.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import com.r0adkll.slidr.Slidr;

import ru.zilzilok.ict.R;
import ru.zilzilok.ict.utils.adapter.ConnectionStatisticAdapter;

public class StatisticsActivity extends AppCompatActivity {
    private static final int NUM_COLUMNS = 3;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        setTitle(R.string.stat_button);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Slidr.attach(this);

        GridView gridView = findViewById(R.id.statGridView);
        gridView.setAdapter(new ConnectionStatisticAdapter(this));
        gridView.setNumColumns(NUM_COLUMNS);
        gridView.setOnTouchListener((v, event) -> event.getAction() == MotionEvent.ACTION_MOVE);

        Spinner spinner = findViewById(R.id.statSpinner);
        ArrayAdapter<?> spinnerAdapter =
                ArrayAdapter.createFromResource(this, R.array.stat_number_connection_types,
                        android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
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
