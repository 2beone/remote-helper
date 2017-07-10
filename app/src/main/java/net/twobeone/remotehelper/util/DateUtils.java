package net.twobeone.remotehelper.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {

    /**
     * 현재시간을 기본문자열 형식으로 반환합니다.
     */
    public static String getTimeString() {
        return getTimeString("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 현재시간을 포맷문자열 형식으로 반환합니다.
     */
    public static String getTimeString(String format) {
        return new SimpleDateFormat(format, Locale.getDefault()).format(new Date());
    }
}
