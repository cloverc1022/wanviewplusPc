package net.ajcloud.wansviewplusw.support.http.bean;

import java.io.Serializable;

/**
 * Created by mamengchao on 2018/06/05.
 * Function:    查询绑定状态bean
 */
public class BindStatusBean implements Serializable {
    //bind status
    public int status;  //0 - 无绑定记录 1 - 已绑定,且属于此账户 2 - 已绑定,不属于此账户
    //check bind
    public int retCode;  //0-绑定成功   7-被别的用户绑定   1-绑定失败（比如因为服务端的错误等）
    public String deviceId;
    public AccountInfo oriAccount;

    public static class AccountInfo implements Serializable {
        public String uid;
        public String username;
    }
}
