package ru.zilzilok.ict.utils.locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

import ru.zilzilok.ict.R;
import ru.zilzilok.ict.activities.MainActivity;

/**
 * Abstract class for language/locale settings.
 */
public abstract class LanguageSettings {
    private static final String TAG = "LanguageSettings";

    /**
     * Changes the application language (RU to EN and vice versa).
     * @param ac activity where need to change language
     */
    public static void changeAppLanguage(Activity ac) {
        String funcName = "[changeAppLanguage]";

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

        Log.i(TAG, String.format("%s App language changed to %s.", funcName, lang));
    }

    /**
     * Initializes the application language.
     * @param ac activity where need to initialize language
     */
    public static void initializeAppLanguage(Activity ac) {
        String funcName = "[initializeAppLanguage]";

        SharedPreferences sp = ac.getSharedPreferences("lang", Context.MODE_PRIVATE);
        String lang = sp.getString("lang", "ru");
        setLocale(ac, lang);

        Log.i(TAG, String.format("%s App language initialized to %s.", funcName, lang));
    }

    private static void setLocale(Activity ac, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        ac.getResources().updateConfiguration(config, ac.getResources().getDisplayMetrics());
    }
}
