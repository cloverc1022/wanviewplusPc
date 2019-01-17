package net.ajcloud.wansviewplusw.support.http;

/**
 * Created by mamengchao on 2018/06/06.
 * Function:    okgo请求通用回调
 */
public interface HttpCommonListener<T> {
    void onSuccess(T bean);

    void onFail(int code, String msg);

    //custom
}
