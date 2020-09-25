package ru.zilzilok.ict.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.zilzilok.ict.R;
import ru.zilzilok.ict.utils.connection.ConnectionState;
import ru.zilzilok.ict.utils.database.Databases;
import ru.zilzilok.ict.utils.layouts.ProgressButton;
import ru.zilzilok.ict.utils.locale.LanguageSettings;
import ru.zilzilok.ict.utils.resources.Resources;
import ru.zilzilok.ict.utils.resources.geolocation.GeoLocationHandler;

/**
 * Main application activity.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static Context context;

    private boolean isExitClicked;          // determines whether the tracking exit button is pressed
    private ProgressButton checkButton;     // button for checking current connection type
    private ProgressButton trackButton;     // button for starting tracking for connections

    ConstraintLayout stateProgressLayout;   // layout for visualization of tracking
    TextView appNameTextView;               // application text (instead of stateProgressLayout)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        context = this;

        LanguageSettings.initializeAppLanguage(this);

        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name_actionbar);

        initializeAppButtons();

        GeoLocationHandler geoLocationHandler = new GeoLocationHandler(this, Resources.INSTANCE.geoLocation);
    }

    /**
     * @return Application context.
     */
    public static Context getContext() {
        return context;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String funcName = "[onOptionsItemSelected]";

        switch (item.getItemId()) {
            case R.id.settings1:
                Toast.makeText(this, "by @zilzilok", Toast.LENGTH_LONG).show();
                Log.i(TAG, String.format("%s Info Toast appeared.", funcName));
                break;
            case R.id.settings2:
                LanguageSettings.changeAppLanguage(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeAppButtons() {
        String funcName = "[initializeAppButtons]";

        // Check button click listener
        View viewCheckButton = findViewById(R.id.buttonCheck);
        checkButton = new ProgressButton(MainActivity.this,
                viewCheckButton, getResources().getString(R.string.check_button), R.drawable.ic_check);
        viewCheckButton.setOnClickListener(this::buttonCheckClicked);

        // Track button click listener
        View viewTrackButton = findViewById(R.id.buttonAdd);
        trackButton = new ProgressButton(MainActivity.this,
                viewTrackButton, getResources().getString(R.string.track_button), R.drawable.ic_track);
        viewTrackButton.setOnClickListener(this::buttonTrackClicked);

        // Statistics button click listener
        View viewStatButton = findViewById(R.id.buttonStat);
        ProgressButton statButton = new ProgressButton(MainActivity.this,
                viewStatButton, getResources().getString(R.string.stat_button), R.drawable.ic_stat);
        viewStatButton.setOnClickListener(this::statButtonClicked);

        // Current State
        ConstraintLayout stateLayout = findViewById(R.id.constraintLayoutState);
        stateProgressLayout = stateLayout.findViewById(R.id.stateProgress);
        stateProgressLayout.setVisibility(View.GONE);
        appNameTextView = stateLayout.findViewById(R.id.appNameTextView);
        appNameTextView.setVisibility(View.VISIBLE);

        // Initialize exit listener for state
        ImageView stateButton = stateProgressLayout.findViewById(R.id.imageViewPoop);
        stateButton.setOnClickListener(this::buttonCloseClicked);

        Log.i(TAG, String.format("%s Main buttons initialized.", funcName));
    }

    private void buttonCheckClicked(@NonNull View view) {
        String funcName = "[buttonCheckClicked]";
        Log.i(TAG, String.format("%s Check button was clicked.", funcName));

        checkButton.buttonActivated();
        checkButton.blockButton();
        Log.i(TAG, String.format("%s Check button was blocked.", funcName));
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            String ct = ConnectionState.getCurrentConnectionType(this);
            Toast.makeText(this, ct, Toast.LENGTH_LONG).show();
            Log.i(TAG, String.format("%s Current connection state - %s.", funcName, ct));
            checkButton.buttonRestored();
            checkButton.unblockButton();
            Log.i(TAG, String.format("%s Check button was unblocked.", funcName));
        }, 800);
    }

    private void statButtonClicked(@NonNull View view) {
        String funcName = "[statButtonClicked]";
        Log.i(TAG, String.format("%s Stat button was clicked.", funcName));

        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

    /*
          ___   _            _                         _   _
         / __| | |_   __ _  | |_   ___     ___  __ __ (_) | |_
         \__ \ |  _| / _` | |  _| / -_)   / -_) \ \ / | | |  _|
         |___/  \__| \__,_|  \__| \___|   \___| /_\_\ |_|  \__|
     */
    private void buttonCloseClicked(@NonNull View view) {
        String funcName = "[buttonCloseClicked]";
        Log.i(TAG, String.format("%s Close button was clicked.", funcName));

        isExitClicked = true;
    }

    private void buttonTrackClicked(@NonNull View view) {
        String funcName = "[buttonTrackClicked]";
        Log.i(TAG, String.format("%s Track button was clicked.", funcName));

        isExitClicked = false;
        Intent intent = new Intent(this, PopupStatesActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String funcName = "[onActivityResult]";

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            String[] checkedValues = data.getStringArrayExtra("checkedValues");
            if (checkedValues != null) {
                List<ConnectionState> checkedConnectionStates = new ArrayList<>();
                for (String checkedValue : checkedValues) {
                    checkedConnectionStates.add(new ConnectionState(checkedValue));
                }

                initConnectionStateLayout(checkedConnectionStates);
                Databases.getInstance().statisticsDBHelper.updateDatabase(checkedConnectionStates, true);
                startTrack(checkedConnectionStates);
            } else {
                Log.i(TAG, String.format("%s No checked connection states.", funcName));
            }
        } else {
            Log.i(TAG, String.format("%s No checked connection states.", funcName));
        }
    }

    private void initConnectionStateLayout(@NonNull List<ConnectionState> checkedConnectionStates) {
        String funcName = "[initConnectionStateLayout]";

        TextView stateText = stateProgressLayout.findViewById(R.id.progressStateTextView);
        String addition = checkedConnectionStates.size() > 1 ? String.format(" + %s %s",
                checkedConnectionStates.size() - 1,
                getResources().getString(R.string.more)) : "";
        stateText.setText(String.format("%s\n%s%s",
                getResources().getString(R.string.wait),
                checkedConnectionStates.get(0).getConnectionType(),
                addition));

        Log.i(TAG, String.format("%s ConnectionStateLayout was initialized.", funcName));
    }

    private static final int BLOCK_BUTTON = 1;
    private static final int UNBLOCK_BUTTON = 2;
    private static final int VISIBLE_STATE = 3;
    private static final int GONE_STATE = 4;

    private static class TrackHandler extends Handler {
        MainActivity mthis;

        TrackHandler(@NonNull MainActivity activity) {
            this.mthis = activity;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BLOCK_BUTTON:
                    mthis.trackButton.blockButton();
                    break;
                case UNBLOCK_BUTTON:
                    mthis.trackButton.unblockButton();
                    break;
                case VISIBLE_STATE:
                    mthis.stateProgressLayout.setVisibility(View.VISIBLE);
                    mthis.appNameTextView.setVisibility(View.GONE);
                    break;
                case GONE_STATE:
                    mthis.stateProgressLayout.setVisibility(View.GONE);
                    mthis.appNameTextView.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private void startTrack(@NonNull List<ConnectionState> states) {
        String funcName = "[startTrack]";

        Log.i(TAG, String.format("%s App started tracking for connection states.", funcName));

        Handler handler = new TrackHandler(this);
        new Thread(() -> {
            handler.sendEmptyMessage(BLOCK_BUTTON);
            Log.i(TAG, String.format("%s Track button was blocked.", funcName));
            handler.sendEmptyMessage(VISIBLE_STATE);
            Log.i(TAG, String.format("%s ConnectionStateLayout visible.", funcName));
            ConnectionState resState = new ConnectionState(ConnectionState.getCurrentConnectionType(this));
            while (!isExitClicked &&
                    !states.contains(resState = new ConnectionState(ConnectionState.getCurrentConnectionType(this)))) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.i(TAG, String.format("%s Connection state %s appeared.", funcName, resState.getConnectionType()));
            handler.sendEmptyMessage(UNBLOCK_BUTTON);
            Log.i(TAG, String.format("%s Track button was unblocked.", funcName));
            handler.sendEmptyMessage(GONE_STATE);
            Log.i(TAG, String.format("%s ConnectionStateLayout invisible.", funcName));
            if (!isExitClicked) {
                resState.notifyAboutState(this);
                Databases.getInstance().statisticsDBHelper.updateDatabase(Collections.singletonList(resState), false);
            }
        }).start();
    }
}
