package net.ajcloud.wansviewplusw.support.utils;

import java.io.File;

public class FileUtil {
    //filePath
    public static String FILE_PATH = System.getProperty("user.home");

    public static String getRootImagePath() {
        String path = FILE_PATH + File.separator + "WansviewCloud" + File.separator + "image";
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdirs();
        return path;
    }

    public static String getTmpPath() {
        String path = FILE_PATH + File.separator + "WansviewCloud" + File.separator + "tmp";
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdirs();
        return path;
    }

    public static String getImagePath(String account) {
        String path = FILE_PATH + File.separator + "WansviewCloud" + File.separator + "image" + File.separator + account;
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdirs();
        return path;
    }

    public static String getVideoPath(String account) {
        String path = FILE_PATH + File.separator + "WansviewCloud" + File.separator + "video" + File.separator + account;
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdirs();
        return path;
    }

    public static String getRealtimeImagePath(String deviceId) {
        String path = FILE_PATH + File.separator + "WansviewCloud" + File.separator + "RealtimeImage" + File.separator + deviceId;
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
