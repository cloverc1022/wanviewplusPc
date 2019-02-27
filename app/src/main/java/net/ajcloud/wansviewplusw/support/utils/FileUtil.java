package net.ajcloud.wansviewplusw.support.utils;

import java.io.File;

public class FileUtil {
    //filePath
    public static String FILE_PATH = System.getProperty("user.dir");

    public static String getImagePath(String deviceId) {
        String path = FILE_PATH + File.separator + "locales" + File.separator + "image" + File.separator + deviceId;
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdirs();
        return path;
    }

    public static String getVideoPath(String deviceId) {
        String path = FILE_PATH + File.separator + "locales" + File.separator + "video" + File.separator + deviceId;
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdirs();
        return path;
    }

    public static String getRealtimeImagePath(String deviceId) {
        String path = FILE_PATH + File.separator + "locales" + File.separator + "RealtimeImage" + File.separator + deviceId;
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdirs();
        return path;
    }
}
