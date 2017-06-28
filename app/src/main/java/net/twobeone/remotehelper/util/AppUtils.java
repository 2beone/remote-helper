package net.twobeone.remotehelper.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;

import net.twobeone.remotehelper.Constants;

import java.io.File;

public final class AppUtils {

    public static PackageInfo getPackageInfo(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException(String.format("package(%s) was not found", context.getPackageName()));
        }
    }

    public static void launchOrMarket(Context context, String uri) {
        if (isInstalled(context, uri)) {
            Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(uri);
            context.startActivity(LaunchIntent);
        } else {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + uri)));
        }
    }

    public static boolean isInstalled(Context context, String uri) {
        try {
            context.getPackageManager().getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static File getDownloadDirectory() {
        return FileUtils.makeDirectoryIfNotExists(String.format("%s/%s/", Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.DONWLOAD_DIRECTORY_NAME));
    }
}
