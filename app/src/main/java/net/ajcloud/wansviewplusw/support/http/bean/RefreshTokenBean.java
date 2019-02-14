package net.ajcloud.wansviewplusw.support.http.bean;

import net.ajcloud.wansviewplusw.support.utils.StringUtil;

/**
 * Created by mamengchao on 2018/06/14.
 * Function:
 */
public class RefreshTokenBean {
    public String status;
    public String code;
    public String message;
    public SigninBean result;

    public int getResultCode() {
        int resultCode = -1;
        try {
            resultCode = Integer.parseInt(code);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return resultCode;
    }

    public boolean isSuccess() {
        return StringUtil.equals("ok", status.toLowerCase());
    }
}
