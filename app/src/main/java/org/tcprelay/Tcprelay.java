package org.tcprelay;

import java.io.File;

public class Tcprelay {
    static {
        System.loadLibrary("app/libs/dll/SE_P2PSDK");
        System.loadLibrary("app/libs/dll/pthreadVC2");
        System.loadLibrary("app/libs/dll/TestJni");
    }

    public native void relayinit();

    public native int relayconnect(String dev_id, String p2p_server_ip, String relay_server_ip, int port);

    public native void relaydisconnect(int cnt_num);

    public native void relaydeinit();
}
