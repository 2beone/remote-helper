package net.twobeone.remotehelper;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.twobeone.remotehelper.util.LocationUtils;

public final class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initPreferences();
    }

    private void initPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.PREF_LOCATION_ENABLED, LocationUtils.isLocationEnabled(this));
        editor.commit();
    }
}
