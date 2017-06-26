package net.twobeone.remotehelper.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public final class PermissionUtils {

    private static String[] getRequestedPermissions(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private static String getPermissionGroup(Context context, String permission) {
        try {
            return context.getPackageManager().getPermissionInfo(permission, 0).group;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static String[] getRequiredPermissions(Context context) {
        return getRequiredPermissions(context, getRequestedPermissions(context));
    }

    public static String[] getRequiredPermissions(Context context, String[] permissions) {
        List<String> requiredPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                if (!TextUtils.isEmpty(getPermissionGroup(context, permission))) {
                    requiredPermissions.add(permission);
                }
            }
        }
        return requiredPermissions.toArray(new String[requiredPermissions.size()]);
    }

    public static boolean isNeverAskAgainAll(Activity activity, String[] permissions) {
        int neverAskAgainCount = 0;
        for (String permission : permissions) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                neverAskAgainCount++;
            }
        }
        return neverAskAgainCount > 0 && neverAskAgainCount == permissions.length;
    }

    public static void requestPermission(final Activity activity, String message, final int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setNegativeButton(android.R.string.no, null);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + activity.getPackageName())), requestCode);
            }
        });
        builder.show();
    }
}