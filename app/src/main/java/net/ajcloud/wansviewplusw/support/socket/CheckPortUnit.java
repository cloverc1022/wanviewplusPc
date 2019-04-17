package net.ajcloud.wansviewplusw.support.socket;

import net.ajcloud.wansviewplusw.support.utils.WLog;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mamengchao on 2018/10/22.
 * Function:
 */
public class CheckPortUnit {
    private static final String TAG = "CheckPortUnit";

    public interface checkPortCallback {
        void result(List<Integer> ports);
    }

    public void check(checkPortCallback listener) {
        List list = new ArrayList<>();
        new Thread(new Runnable() {
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
                        if (list.size() > 50) {
                            break;
                        }
                        WLog.w(TAG, "port:" + i);
                        list.add(i);
                        WLog.w(e.toString());
                    }
                }
                listener.result(list);
            }
        }).start();
    }
}
