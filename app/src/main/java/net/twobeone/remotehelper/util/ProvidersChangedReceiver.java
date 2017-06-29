package net.twobeone.remotehelper.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.twobeone.remotehelper.Constants;

public class ProvidersChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            boolean isLocationEnabled = LocationUtils.isLocationEnabled(context);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            if (prefs.getBoolean(Constants.PREF_LOCATION_ENABLED, false) != isLocationEnabled) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(Constants.PREF_LOCATION_ENABLED, isLocationEnabled);
                editor.commit();
            }
        }
    }
}
