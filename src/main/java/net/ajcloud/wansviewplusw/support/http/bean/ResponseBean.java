package net.ajcloud.wansviewplusw.support.http.bean;

import net.ajcloud.wansviewplusw.support.utils.StringUtil;

import java.io.Serializable;

/**
 * Created by mamengchao on 2018/05/21.
 * 通用的应答bean
 */
public class ResponseBean<T> implements Serializable {
    public String status;
    public String code;
    public String message;
    public T result;

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
        try {
            return !StringUtil.isNullOrEmpty(status) && StringUtil.equals("ok", status.toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ResponseBean<Object> ErrorBean() {
        ResponseBean<Object> errorBean = new ResponseBean<>();
        errorBean.code = "-1";
        errorBean.status = "error";
        errorBean.message = "error";
        return errorBean;
    }

}
