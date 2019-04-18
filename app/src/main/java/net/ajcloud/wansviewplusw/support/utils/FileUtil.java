package net.ajcloud.wansviewplusw.support.utils;

import java.io.File;
import java.util.prefs.Preferences;

public class FileUtil {
    //filePath
    private static Preferences preferences = Preferences.userNodeForPackage(FileUtil.class);

    public static String FILE_PATH = System.getProperty("user.home") + File.separator + "WansviewCloud";

    public static String getRootPath() {
        return preferences.get(IPreferences.P_FILE_LOCATION, FILE_PATH);
    }

    public static void setRootPath(String path) {
        if (path != null)
            preferences.put(IPreferences.P_FILE_LOCATION, path);
    }

    public static String getTmpPath() {
        String path = preferences.get(IPreferences.P_FILE_LOCATION, FILE_PATH) + File.separator + "tmp";
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdirs();
        return path;
    }

    public static String getImagePath(String account) {
        String path = preferences.get(IPreferences.P_FILE_LOCATION, FILE_PATH) + File.separator + "image" + File.separator + account;
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdirs();
        return path;
    }

    public static String getVideoPath(String account) {
        String path = preferences.get(IPreferences.P_FILE_LOCATION, FILE_PATH) + File.separator + "video" + File.separator + account;
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdirs();
        return path;
    }

    public static String getRealtimeImagePath(String deviceId) {
        String path = preferences.get(IPreferences.P_FILE_LOCATION, FILE_PATH) + File.separator + "RealtimeImage" + File.separator + deviceId;
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdirs();
        return path;
    }

    public static int getRealImageNum(String oid) {
        File directory = new File(getRealtimeImagePath(oid));
        if (directory.exists()) {
            File[] files = directory.listFiles();
            return files.length;
        } else {
            return 0;
        }
    }

    /**
     * 重置
     */
    public static void resetRealTimeImage(String dir) {
        try {
            File directory = new File(getRealtimeImagePath(dir));
            File file = new File(getRealtimeImagePath(dir) + "/realtime_picture.jpg");
            boolean flag = true;
            // 删除文件夹中的所有文件包括子目录
            File[] files = directory.listFiles();
            for (File tmpFile : files) {
                // 删除子文件
                if (tmpFile.isFile()) {
                    if (file.getName().equals(tmpFile.getName()))
                        continue;
                    flag = tmpFile.delete();
                    if (!flag)
                        break;
                }
            }
            if (!flag) {
                WLog.w("重置目录失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void renameImage(String oid) {
        File directory = new File(getRealtimeImagePath(oid));
        File[] files;
        File file = new File(getRealtimeImagePath(oid) + "/realtime_picture.jpg");
        files = directory.listFiles();
        //数据异常则先重置目录
        if (file.exists() && files.length == 2) {
            boolean flag = file.delete();
            if (flag) {
                files = directory.listFiles();
            }
        }
        if (files.length <= 0) {
            return;
        }
        for (File f : files) {
            if (f.isFile()) { // 判断是否为文件夹
                f.renameTo(new File(getRealtimeImagePath(oid) + "/realtime_picture.jpg"));
                return;
            }
        }
    }
}
