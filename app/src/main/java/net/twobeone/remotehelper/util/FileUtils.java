package net.twobeone.remotehelper.util;

import java.io.File;
import java.util.Arrays;

public final class FileUtils {

    private static final String[] VIDEO_EXTENSIONS = {"MP4", "WMV", "ASF", "AVI", "MKV", "MPEG"};
    private static final String[] IMAGE_EXTENSIONS = {"PNG", "JPG", "GIF", "BMP", "JPEG", "TIFF", "TIF"};

    public static String getExtension(File file) {
        String fileName = file.getName();
        if (file.isDirectory() || !fileName.contains(".")) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
    }

    public static File makeDirectoryIfNotExists(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static String getSizeName(File file) {
        long bytes = file.length();
        if (bytes > 1024 * 1024)
            return String.format("%.2f %s", bytes / (1f * 1024 * 1024), "mb");
        else if (bytes > 1024)
            return String.format("%d %s", bytes / 1024, "kb");
        else
            return String.format("%d %s", bytes, "bytes");
    }


    public static boolean isImageFile(File file) {
        return Arrays.asList(IMAGE_EXTENSIONS).contains(getExtension(file));
    }

    public static boolean isVideoFile(File file) {
        return Arrays.asList(VIDEO_EXTENSIONS).contains(getExtension(file));
    }
}
