package net.ajcloud.wansviewplusw.support.http.bean.device;

import java.util.List;

/**
 * Created by mamengchao on 2018/10/25.
 * Function:
 */
public class InvitesBean {
    public String _id;
    public String uid;
    public String deviceId;
    public String __v;
    public String username;
    public List<InviteInfo> invites;

    public static class InviteInfo {
        public String slaveUid;
        public String slaveName;
        public String inviteCode;
        public String status;
        public String createTs;
        public String validTs;
        public String url;
    }
}
