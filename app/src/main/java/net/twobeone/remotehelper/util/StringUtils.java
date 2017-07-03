package net.twobeone.remotehelper.util;

public final class StringUtils {

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.equals("");
    }

    public static String removeHtmlTag(String html) {
        return html.replaceAll("\\<[^>]*>", "");
    }
}
