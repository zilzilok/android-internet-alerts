package ru.zilzilok.ict.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import java.util.Locale;

import ru.zilzilok.ict.R;
import ru.zilzilok.ict.utils.connection.ConnectionState;
import ru.zilzilok.ict.utils.connection.ConnectionType;
import ru.zilzilok.ict.utils.database.ConnectionInfoDBHelper;
import ru.zilzilok.ict.utils.layouts.ProgressButton;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    private boolean isExitClicked;
    private ProgressButton checkButton;
    private ProgressButton monitorButton;
    private ProgressButton statButton;

    ConstraintLayout stateProgressLayout;
    TextView appNameTextView;
    ConnectionInfoDBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeAppLanguage();
        setContentView(R.layout.activity_main);
        setTitle("");

        db = new ConnectionInfoDBHelper(this);
        initializeButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings1:
                Toast.makeText(this, "\"Internet Connection Tracker\"\nby @zilzilok", Toast.LENGTH_LONG).show();
                break;
            case R.id.settings2:
                SharedPreferences sp = getSharedPreferences("lang", MODE_PRIVATE);
                String countryCode = sp.getString("lang", "ru");
                SharedPreferences.Editor editor = sp.edit();
                switch (countryCode) {
                    case "ru":
                        countryCode = "en";
                        break;
                    case "en":
                        countryCode = "ru";
                        break;
                }
                editor.putString("lang", countryCode);
                editor.apply();
                changeLang(countryCode);
        }
        return super.onOptionsItemSelected(item);
    }

    /*
      _
     | |       __ _   _ __     __ _   _   _    __ _    __ _    ___
     | |      / _` | | '_ \   / _` | | | | |  / _` |  / _` |  / _ \
     | |___  | (_| | | | | | | (_| | | |_| | | (_| | | (_| | |  __/
     |_____|  \__,_| |_| |_|  \__, |  \__,_|  \__,_|  \__, |  \___|
                              |___/                   |___/
    */
    private void initializeAppLanguage() {
        SharedPreferences sp = getSharedPreferences("lang", 0);
        String lang = sp.getString("lang", "ru");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private void saveLocale(@NonNull String lang) {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("def_loc", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.apply();
    }

    private void changeLang(@NonNull String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        Locale myLocale = new Locale(lang);

        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        saveLocale(lang);

        startActivity(new Intent(this, SplashActivity.class));
        finish();
        Toast.makeText(this, getResources().getString(R.string.curr_lang), Toast.LENGTH_SHORT).show();
        overridePendingTransition(0, 0);
    }

    /*
      ____            _     _
     | __ )   _   _  | |_  | |_    ___    _ __    ___
     |  _ \  | | | | | __| | __|  / _ \  | '_ \  / __|
     | |_) | | |_| | | |_  | |_  | (_) | | | | | \__ \
     |____/   \__,_|  \__|  \__|  \___/  |_| |_| |___/
    */
    private void initializeButtons() {
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
        stateButton.setOnClickListener(this::buttonExitClicked);
    }

    /*
           ___   _                 _
          / __| | |_    ___   __  | |__
         | (__  | ' \  / -_) / _| | / /
          \___| |_||_| \___| \__| |_\_\
    */
    private void buttonCheckClicked(@NonNull View view) {
        checkButton.buttonActivated();
        checkButton.blockButton();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Toast.makeText(this, ConnectionType.getNetworkClass(this), Toast.LENGTH_LONG).show();
            checkButton.buttonRestored();
            checkButton.unblockButton();
        }, 800);
    }

    /*
          ___   _            _     _        _     _
         / __| | |_   __ _  | |_  (_)  ___ | |_  (_)  __
         \__ \ |  _| / _` | |  _| | | (_-< |  _| | | / _|
         |___/  \__| \__,_|  \__| |_| /__/  \__| |_| \__|
     */
    private void statButtonClicked(@NonNull View view) {
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

    /*
          ___   _            _                         _   _
         / __| | |_   __ _  | |_   ___     ___  __ __ (_) | |_
         \__ \ |  _| / _` | |  _| / -_)   / -_) \ \ / | | |  _|
         |___/  \__| \__,_|  \__| \___|   \___| /_\_\ |_|  \__|
     */
    private void buttonExitClicked(@NonNull View view) {
        isExitClicked = true;
    }

    /*
          __  __                _   _
         |  \/  |  ___   _ _   (_) | |_   ___   _ _
         | |\/| | / _ \ | ' \  | | |  _| / _ \ | '_|
         |_|  |_| \___/ |_||_| |_|  \__| \___/ |_|
     */
    private void buttonMonitorClicked(@NonNull View view) {
        isExitClicked = false;
        Intent intent = new Intent(this, PopupStatesActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            String[] checkedValues = data.getStringArrayExtra("checkedValues");
            if (checkedValues != null) {
                List<ConnectionState> checkedConnectionStates = new ArrayList<>();
                for (String checkedValue : checkedValues) {
                    checkedConnectionStates.add(new ConnectionState(checkedValue));
                }

                showConnectionStateLayout(checkedConnectionStates);
                db.updateDatabase(checkedConnectionStates, true);
                startMonitor(checkedConnectionStates);
            }
        }
    }

    private void showConnectionStateLayout(@NonNull List<ConnectionState> checkedConnectionStates) {
        TextView stateText = stateProgressLayout.findViewById(R.id.progressStateTextView);
        String addition = checkedConnectionStates.size() > 1 ? String.format(" + %s %s",
                checkedConnectionStates.size() - 1,
                getResources().getString(R.string.more)) : "";
        stateText.setText(String.format("%s\n%s%s",
                getResources().getString(R.string.wait),
                checkedConnectionStates.get(0).getConnectionType(),
                addition));
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
        Handler handler = new MonitorHandler(this);
        new Thread(() -> {
            handler.sendEmptyMessage(BLOCK_BUTTON);
            handler.sendEmptyMessage(VISIBLE_STATE);
            ConnectionState resState = new ConnectionState(ConnectionType.getNetworkClass(this));
            while (!isExitClicked &&
                    !states.contains(resState = new ConnectionState(ConnectionType.getNetworkClass(this)))) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            handler.sendEmptyMessage(UNBLOCK_BUTTON);
            handler.sendEmptyMessage(GONE_STATE);
            if (!isExitClicked) {
                resState.notifyAboutState(this);
                db.updateDatabase(Collections.singletonList(resState), false);
            }
        }).start();
    }
}
