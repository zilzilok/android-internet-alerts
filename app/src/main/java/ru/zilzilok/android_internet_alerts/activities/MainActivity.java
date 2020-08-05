package ru.zilzilok.android_internet_alerts.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

import java.util.Objects;

import ru.zilzilok.android_internet_alerts.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void buttonCheckClicked(View view) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager != null
                ? connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                : null;
        NetworkInfo mobile = connectivityManager != null
                ? connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                : null;
        if (wifi != null && wifi.isConnectedOrConnecting()) {
            Toast.makeText(this, "WIFI", Toast.LENGTH_LONG).show();
        } else if (mobile != null && mobile.isConnectedOrConnecting()) {
            getNetworkClass(getApplicationContext());
        } else {
            Toast.makeText(this, "No Network", Toast.LENGTH_LONG).show();
        }
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
}
