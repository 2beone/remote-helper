package net.twobeone.remotehelper.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.db.UserDao;
import net.twobeone.remotehelper.db.model.User;
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
            User user = UserDao.getInstance().select();
            if (user != null && !TextUtils.isEmpty(user.name)) {
                findPreference(PREF_USER_NAME).setTitle(user.name);
            }
            findPreference(PREF_APP_VERSION_NAME).setSummary(AppUtils.getPackageInfo(getActivity()).versionName);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            String key = preference.getKey();
            if (key.equals(PREF_USER_NAME)) {
                startActivity(new Intent(getActivity(), UserViewActivity.class));
            } else if (key.equals(PREF_APP_VERSION_NAME)) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getActivity().getPackageName())));
            } else if (key.equals(PREF_APP_TROUBLES)) {
                startActivity(new Intent(getActivity(), TroublesActivity.class));
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }
}
