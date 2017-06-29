package net.twobeone.remotehelper.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;

import net.twobeone.remotehelper.Constants;
import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.util.AppUtils;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public static class SettingsFragment extends PreferenceFragment {

        private static final String PREF_USER_NAME = "user_name";
        private static final String PREF_APP_INFO = "app_info";
        private static final String PREF_APP_VERSION_NAME = "app_version_name";
        private static final String PREF_APP_TROUBLES = "app_troubles";

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onResume() {
            super.onResume();
            findPreference(PREF_USER_NAME).setTitle(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Constants.PREF_USER_NAME, getResources().getString(R.string.pref_user_name_title)));
            findPreference(PREF_APP_VERSION_NAME).setSummary(AppUtils.getPackageInfo(getActivity()).versionName);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            String key = preference.getKey();
            if (key.equals(PREF_USER_NAME)) {
                startActivity(new Intent(getActivity(), UserViewActivity.class));
            } else if (key.equals(PREF_APP_INFO)) {
                startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getActivity().getPackageName())));
            } else if (key.equals(PREF_APP_VERSION_NAME)) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getActivity().getPackageName())));
            } else if (key.equals(PREF_APP_TROUBLES)) {
                startActivity(new Intent(getActivity(), TroublesActivity.class));
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }
}
