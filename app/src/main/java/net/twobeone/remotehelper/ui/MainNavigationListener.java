package net.twobeone.remotehelper.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;

import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.service.GPSInfo;

public final class MainNavigationListener implements NavigationView.OnNavigationItemSelectedListener {

    private final Activity mActivity;

    public MainNavigationListener(Activity activity) {
        mActivity = activity;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_safezone:
                GPSInfo gps = new GPSInfo(mActivity);
                if (!gps.isGetLocation()) {
                    gps.showSettingsAlert();
                } else {
                    mActivity.startActivity(new Intent(mActivity, SafetyZoneActivity.class));
                }
                break;
            case R.id.nav_settings:
                mActivity.startActivity(new Intent(mActivity, SettingsActivity.class));
                break;
        }
        return true;
    }
}
