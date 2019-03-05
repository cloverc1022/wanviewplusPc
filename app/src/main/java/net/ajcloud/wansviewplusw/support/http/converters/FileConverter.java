package net.ajcloud.wansviewplusw.support.http.converters;

import net.ajcloud.wansviewplusw.support.utils.WLog;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import java.io.*;

public class FileConverter implements Converter<ResponseBody, File> {

    private String filePath;

    public FileConverter(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public File convert(ResponseBody value) throws IOException {
        return writeResponseBodyToDisk(value, filePath);
    }

    /**
     * 将文件写入本地
     *
     * @param body http响应体
     * @param path 保存路径
     * @return 保存file
     */
    public static File writeResponseBodyToDisk(ResponseBody body, String path) {

        File saveFile = null;
        try {
            saveFile = new File(path);
            createDirs(saveFile);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(saveFile);
                long startTime = System.currentTimeMillis();
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                }
                long endTime = System.currentTimeMillis();
                WLog.i("total time: " + Long.toString(endTime - startTime));
                outputStream.flush();
            } catch (IOException e) {
                return saveFile;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                return saveFile;
            }
        } catch (IOException e) {
            return saveFile;
        }
    }

    private static void createDirs(File file) {
        if (file != null) {
            if (file.exists()) {
                file.delete();
            }
            String dir = file.getParent();
            createDirs(dir);
        }
    }

    private static void createDirs(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

}
