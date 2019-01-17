package net.ajcloud.wansviewplusw.support.http.bean;

/**
 * Created by mamengchao on 2018/08/13.
 * Function:
 */
public class RequestBuyBean {

    public String payMode;
    public Url paypal;

    public static class Url {
        public String approvalUrl;
    }
}
