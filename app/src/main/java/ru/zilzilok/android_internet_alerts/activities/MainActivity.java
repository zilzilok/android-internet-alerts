package ru.zilzilok.android_internet_alerts.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import ru.zilzilok.android_internet_alerts.R;
import ru.zilzilok.android_internet_alerts.utils.ProgressButton;

public class MainActivity extends AppCompatActivity {
    private ProgressButton addButton;
    private ProgressButton checkButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check button click listener
        View viewCheckButton = findViewById(R.id.buttonCheck);
        checkButton = new ProgressButton(MainActivity.this,
                viewCheckButton, "Check Connection");
        viewCheckButton.setOnClickListener(this::buttonCheckClicked);

        // Add button click listener
        View viewAddButton = findViewById(R.id.buttonAdd);
        addButton = new ProgressButton(MainActivity.this,
                viewAddButton, "Add");
        viewAddButton.setOnClickListener(this::buttonAddClicked);
    }

    public void buttonCheckClicked(View view) {
        checkButton.buttonActivated();
        view.setEnabled(false);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivityManager != null
                    ? connectivityManager.getActiveNetworkInfo()
                    : null;
            if (activeNetwork != null) {
                switch (activeNetwork.getType()) {
                    case ConnectivityManager.TYPE_WIFI:
                        // connected to wifi
                        Toast.makeText(this, "WIFI", Toast.LENGTH_LONG).show();
                        break;
                    case ConnectivityManager.TYPE_MOBILE:
                        // connected to mobile data
                        getNetworkClass(getApplicationContext());
                        break;
                    default:
                        break;
                }
            } else {
                Toast.makeText(this, "No Network", Toast.LENGTH_LONG).show();
            }
            checkButton.buttonRestored();
            view.setEnabled(true);
        }, 800);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getNetworkClass(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = Objects.requireNonNull(mTelephonyManager).getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN: {
                Toast.makeText(getApplicationContext(), "2G (E)",
                        Toast.LENGTH_SHORT).show();
                break;
            }
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP: {
                Toast.makeText(getApplicationContext(), "3G (H/H+)",
                        Toast.LENGTH_SHORT).show();
                break;
            }
            case TelephonyManager.NETWORK_TYPE_LTE: {
                Toast.makeText(getApplicationContext(), "4G (LTE)",
                        Toast.LENGTH_SHORT).show();
                break;
            }
            default:
                Toast.makeText(getApplicationContext(), "Error",
                        Toast.LENGTH_SHORT).show();
        }
    }

    public void buttonAddClicked(View view) {

    }
}
