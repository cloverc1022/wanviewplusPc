package net.ajcloud.wansviewplusw.support.socket;

import net.ajcloud.wansviewplusw.support.utils.WLog;

import java.net.Socket;

/**
 * Created by mamengchao on 2018/10/22.
 * Function:
 */
public class CheckPortUnit {
    private static final String TAG = "CheckPortUnit";

    public interface checkPortCallback {
        void result(int port);
    }

    public void check(checkPortCallback listener) {
        new Thread(new Runnable() {
            int count = 0;

            @Override
            public void run() {
                Socket socket = null;
                //端口范围：0~65535
                for (int i = 8000; i < 65536; i++) {
                    try {
                        //创建Socket
                        socket = new Socket("127.0.0.1", i); //IP：10.0.2.2，端口i
                        socket.setSoTimeout(100);
                        socket.close();
                    } catch (Exception e) {
                        if (count++ > 20) {
                            break;
                        }
                        WLog.w(TAG, "port:" + i);
                        listener.result(i);
                    }
                }
            }
        }).start();
    }
}
