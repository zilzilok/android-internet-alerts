package ru.zilzilok.ict.activities;

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
import ru.zilzilok.ict.utils.connection.ConnectionType;
import ru.zilzilok.ict.utils.database.ConnectionInfoDBHelper;
import ru.zilzilok.ict.utils.layouts.ProgressButton;
import ru.zilzilok.ict.utils.locale.LanguageSettings;
import ru.zilzilok.ict.utils.resources.geolocation.GeoLocation;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private boolean isExitClicked;
    private ProgressButton checkButton;
    private ProgressButton monitorButton;
    private ProgressButton statButton;
//    private GeoLocation geoLocation;

    ConstraintLayout stateProgressLayout;
    TextView appNameTextView;
    ConnectionInfoDBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        LanguageSettings.initializeAppLanguage(this);

        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name_actionbar);

        db = new ConnectionInfoDBHelper(this);
        initializeAppButtons();

//        geoLocation = new GeoLocation(this);
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

    /*
      ____            _     _
     | __ )   _   _  | |_  | |_    ___    _ __    ___
     |  _ \  | | | | | __| | __|  / _ \  | '_ \  / __|
     | |_) | | |_| | | |_  | |_  | (_) | | | | | \__ \
     |____/   \__,_|  \__|  \__|  \___/  |_| |_| |___/
    */
    private void initializeAppButtons() {
        String funcName = "[initializeAppButtons]";

        // Check button click listener
        View viewCheckButton = findViewById(R.id.buttonCheck);
        checkButton = new ProgressButton(MainActivity.this,
                viewCheckButton, getResources().getString(R.string.check_button), R.drawable.ic_check);
        viewCheckButton.setOnClickListener(this::buttonCheckClicked);

        // Monitor button click listener
        View viewMonitorButton = findViewById(R.id.buttonAdd);
        monitorButton = new ProgressButton(MainActivity.this,
                viewMonitorButton, getResources().getString(R.string.monitor_button), R.drawable.ic_monitor);
        viewMonitorButton.setOnClickListener(this::buttonMonitorClicked);

        // Statistics button click listener
        View viewStatButton = findViewById(R.id.buttonStat);
        statButton = new ProgressButton(MainActivity.this,
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

    /*
           ___   _                 _
          / __| | |_    ___   __  | |__
         | (__  | ' \  / -_) / _| | / /
          \___| |_||_| \___| \__| |_\_\
    */
    private void buttonCheckClicked(@NonNull View view) {
        String funcName = "[buttonCheckClicked]";
        Log.i(TAG, String.format("%s Check button was clicked.", funcName));

        checkButton.buttonActivated();
        checkButton.blockButton();
        Log.i(TAG, String.format("%s Check button was blocked.", funcName));
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            String ct = ConnectionType.getNetworkClass(this);
            Toast.makeText(this, ct, Toast.LENGTH_LONG).show();
            Log.i(TAG, String.format("%s Current connection state - %s.", funcName, ct));
            checkButton.buttonRestored();
            checkButton.unblockButton();
            Log.i(TAG, String.format("%s Check button was unblocked.", funcName));
        }, 800);
    }

    /*
          ___   _            _     _        _     _
         / __| | |_   __ _  | |_  (_)  ___ | |_  (_)  __
         \__ \ |  _| / _` | |  _| | | (_-< |  _| | | / _|
         |___/  \__| \__,_|  \__| |_| /__/  \__| |_| \__|
     */
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

    /*
          __  __                _   _
         |  \/  |  ___   _ _   (_) | |_   ___   _ _
         | |\/| | / _ \ | ' \  | | |  _| / _ \ | '_|
         |_|  |_| \___/ |_||_| |_|  \__| \___/ |_|
     */
    private void buttonMonitorClicked(@NonNull View view) {
        String funcName = "[buttonMonitorClicked]";
        Log.i(TAG, String.format("%s Monitor button was clicked.", funcName));

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
                db.updateDatabase(checkedConnectionStates, true);
                startMonitor(checkedConnectionStates);
            }else{
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

    private static class MonitorHandler extends Handler {
        MainActivity mthis;

        MonitorHandler(@NonNull MainActivity activity) {
            this.mthis = activity;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BLOCK_BUTTON:
                    mthis.monitorButton.blockButton();
                    break;
                case UNBLOCK_BUTTON:
                    mthis.monitorButton.unblockButton();
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

    private void startMonitor(@NonNull List<ConnectionState> states) {
        String funcName = "[startMonitor]";

        Log.i(TAG, String.format("%s App started monitoring for connection states.", funcName));

        Handler handler = new MonitorHandler(this);
        new Thread(() -> {
            handler.sendEmptyMessage(BLOCK_BUTTON);
            Log.i(TAG, String.format("%s Monitor button was blocked.", funcName));
            handler.sendEmptyMessage(VISIBLE_STATE);
            Log.i(TAG, String.format("%s ConnectionStateLayout visible.", funcName));
            ConnectionState resState = new ConnectionState(ConnectionType.getNetworkClass(this));
            while (!isExitClicked &&
                    !states.contains(resState = new ConnectionState(ConnectionType.getNetworkClass(this)))) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.i(TAG, String.format("%s Connection state %s appeared.", funcName, resState.getConnectionType()));
            handler.sendEmptyMessage(UNBLOCK_BUTTON);
            Log.i(TAG, String.format("%s Monitor button was unblocked.", funcName));
            handler.sendEmptyMessage(GONE_STATE);
            Log.i(TAG, String.format("%s ConnectionStateLayout invisible.", funcName));
            if (!isExitClicked) {
                resState.notifyAboutState(this);
                db.updateDatabase(Collections.singletonList(resState), false);
            }
        }).start();
    }
}
