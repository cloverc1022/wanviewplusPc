package net.ajcloud.wansviewplusw.support.http.bean;

/**
 * Created by mamengchao on 2018/05/23.
 * 登录bean
 */
public class SigninBean {
    public String accessToken;
    public String refreshToken;
    public String scope;     //请求范围
    public String tokenType; //token类型
    public long accessExpiresIn; //有效期
    public long refreshExpiresIn; //有效期
    public String signToken; //用于签名
}
