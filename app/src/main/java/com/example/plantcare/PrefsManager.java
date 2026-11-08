package com.example.plantcare;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {
    public static final String DEFAULT_OWNER = "Sahabat Tanaman";

    private final SharedPreferences prefs;

    public PrefsManager(Context context) {
        prefs = context.getSharedPreferences("plantcare_settings", Context.MODE_PRIVATE);
    }

    public void saveOwner(String name) {
        prefs.edit().putString("owner", name).apply();
    }

    public String getOwner() {
        return prefs.getString("owner", DEFAULT_OWNER);
    }
}
