package net.ajcloud.wansviewplusw.support.http.bean.start;

/**
 * Created by mamengchao on 2018/06/04.
 * Function:App启动时需要的公共参数
 */
public class AppConfigBean {
    public String uacUrl;
    public String capUrl;
    public String agreementUrl;
    public String privacyUrl;
    public String cloudStorAgreementUrl;
    public String emcPortalUrl;
    public String storePortalUrl;
    public String wanIp;

    @Override
    public String toString() {
        return "AppConfigBean{" +
                "uacUrl='" + uacUrl + '\'' +
                ", capUrl='" + capUrl + '\'' +
                ", agreementUrl='" + agreementUrl + '\'' +
                ", privacyUrl='" + privacyUrl + '\'' +
                ", cloudStorAgreementUrl='" + cloudStorAgreementUrl + '\'' +
                ", emcPortalUrl='" + emcPortalUrl + '\'' +
                ", storePortalUrl='" + storePortalUrl + '\'' +
                ", wanIp='" + wanIp + '\'' +
                '}';
    }
}
