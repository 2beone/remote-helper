package net.twobeone.remotehelper.util;

import android.content.Context;
import android.location.LocationManager;

public class LocationUtils {

    public static boolean isLocationEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
