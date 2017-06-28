package net.twobeone.remotehelper.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;

import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.service.GPSInfo;
import net.twobeone.remotehelper.util.AppUtils;

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
            case R.id.nav_help:
                mActivity.startActivity(new Intent(mActivity, HelpActivity.class));
                break;
            case R.id.nav_settings:
                mActivity.startActivity(new Intent(mActivity, SettingsActivity.class));
                break;
            case R.id.nav_osan_homepage:
                mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.osan.go.kr/m/main.do")));
                break;
            case R.id.nav_osan_smart:
                AppUtils.launchOrMarket(mActivity, "kr.go.mosan");
                break;
        }
        return true;
    }
}
