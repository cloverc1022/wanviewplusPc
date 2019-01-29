package net.ajcloud.wansviewplusw.support.utils;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface P2pInterface extends Library {
    P2pInterface instanceDll = (P2pInterface) Native.load("lib/dll/SE_P2PSDK", P2pInterface.class);

    int SE_GetAPIVersion();

    int SE_Initialize();

    int SE_SetServInfo(String dev_id, String p2p_server_ip, String relay_server_ip, int port);

    void relaydisconnect(int cnt_num);
}
