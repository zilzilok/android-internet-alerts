package ru.zilzilok.ict.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import ru.zilzilok.ict.R;

/**
 * Activity to select what you want to track.
 */
public class PopupStatesActivity extends AppCompatActivity {
    private static final String TAG = "PopupStatesActivity";

    private ListView listView;  // List of connection types
    String[] values;            // Array with all connection types

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_states);

        showWindow();
        initializeStatesList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
        overridePendingTransition(0, R.anim.activity_fade_in);
    }

    private void initializeStatesList() {
        listView = findViewById(R.id.statesListView);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        values = getResources().getStringArray(R.array.connection_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.state_list_item_multiple_choice, values);
        listView.setAdapter(adapter);

        // State button click listener
        View viewStateButton = findViewById(R.id.stateButton);
        viewStateButton.setOnClickListener(this::stateButtonClicked);

        // Set text for state button
        TextView textViewStateButton = findViewById(R.id.stateButtonTextView);
        textViewStateButton.setText(R.string.start);
    }

    private void stateButtonClicked(@NonNull View view) {
        String funcName = "[stateButtonClicked]";
        Log.i(TAG, String.format("%s Start button in ConnectionStateLayout was clicked.", funcName));

        Intent intent = new Intent();
        if (listView.getCheckedItemCount() > 0) {
            String[] checkedValues = new String[listView.getCheckedItemCount()];
            int j = 0;
            for (int i = 0; i < values.length; i++) {
                if (listView.isItemChecked(i))
                    checkedValues[j++] = values[i];
            }
            intent.putExtra("checkedValues", checkedValues);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
        overridePendingTransition(0, R.anim.activity_fade_in);
    }

    private void showWindow() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.6), (int) (height * 0.5));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);
    }
}
