package net.ajcloud.wansviewplusw.support.http.bean.cloudPaymentHistory;

/**
 * Created by mamengchao on 2018/09/08.
 * Function:
 */
public class OriginalTransactionBean {
    public String payer_email;
    public String payer_name;
    public String status;
    public String time_stamp;
    public String time_zone;
    public String transaction_id;
    public String transaction_type;
    public AmountBean amount;
    public AmountBean fee_amount;
    public AmountBean net_amount;
}
