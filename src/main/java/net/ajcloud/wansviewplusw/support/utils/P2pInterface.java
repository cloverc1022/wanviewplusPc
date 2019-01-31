package net.ajcloud.wansviewplusw.support.utils;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface P2pInterface extends Library {
    P2pInterface instanceDll = (P2pInterface) Native.load("lib/dll/librelay", P2pInterface.class);

    //    int SE_GetAPIVersion();
//    int SE_Initialize();
//    int SE_SetServInfo(String dev_id, String p2p_server_ip, String relay_server_ip, int port);
//    int SE_Connect(String dev_id, int port,long timeout);
//    int SE_CheckSessionInfo(int handle, String p2p_server_ip, String relay_server_ip, int port);
//
//    void SE_Close(int handle);
//    void SE_DeInitialize();
    void relayinit();

    int relayconnect(String dev_id, String p2p_server_ip, String relay_server_ip, int port);

    void relaydisconnect(int cnt_num);

    void relaydeinit();
}
