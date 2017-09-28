package net.twobeone.remotehelper.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {

    public static CharSequence formatSameDayTime(long when) {
        int format = android.text.format.DateUtils.isToday(when) ? DateFormat.SHORT : DateFormat.LONG;
        return android.text.format.DateUtils.formatSameDayTime(when, System.currentTimeMillis(), format, format);
    }

    public static String getTimeString() {
        return getTimeString("yyyy-MM-dd HH:mm:ss");
    }

    public static String getTimeString(String format) {
        return new SimpleDateFormat(format, Locale.getDefault()).format(new Date());
    }
}
