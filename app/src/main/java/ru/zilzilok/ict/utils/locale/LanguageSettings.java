package ru.zilzilok.ict.utils.locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.widget.Toast;

import java.util.Locale;

import ru.zilzilok.ict.R;
import ru.zilzilok.ict.activities.MainActivity;

public abstract class LanguageSettings {
    public static void changeAppLanguage(Activity ac) {
        // Get locale due to shared pref
        SharedPreferences sp = ac.getSharedPreferences("lang", Context.MODE_PRIVATE);
        String lang = sp.getString("lang", "ru");
        // Save new app locale
        SharedPreferences.Editor editor = sp.edit();
        lang = lang.equals("ru") ? "en" : "ru";
        editor.putString("lang", lang);
        editor.apply();
        // Set new app locale
        setLocale(ac, lang);

        // Notify about current app language
        Toast.makeText(ac, ac.getResources().getString(R.string.curr_lang), Toast.LENGTH_SHORT).show();
        // Restart Activity
        ac.startActivity(new Intent(ac, MainActivity.class));
        ac.finish();
        ac.overridePendingTransition(0, R.anim.activity_fade_in);
    }

    public static void initializeAppLanguage(Activity ac) {
        SharedPreferences sp = ac.getSharedPreferences("lang", Context.MODE_PRIVATE);
        String lang = sp.getString("lang", "ru");
        setLocale(ac, lang);
    }

    private static void setLocale(Activity ac, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        ac.getResources().updateConfiguration(config, ac.getResources().getDisplayMetrics());
    }
}
