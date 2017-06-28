package net.twobeone.remotehelper;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.twobeone.remotehelper.sqlite.SQLiteHelper;
import net.twobeone.remotehelper.util.LocationUtils;

public final class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SQLiteHelper.initialize(this).getReadableDatabase();
        initPreferences();
    }

    private void initPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.PREF_LOCATION_ENABLED, LocationUtils.isLocationEnabled(this));
        editor.commit();
    }

    @Override
    public void onTerminate() {
        SQLiteHelper.getInstance().close();
        super.onTerminate();
    }
}
