package net.twobeone.remotehelper.util;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import net.twobeone.remotehelper.Constants;

public final class UserUtils {

    public static boolean isRegisted(Context context) {
        return !TextUtils.isEmpty(PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.PREF_USER_NAME, null));
    }
}
