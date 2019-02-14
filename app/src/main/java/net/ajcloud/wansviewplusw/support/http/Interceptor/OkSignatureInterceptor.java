package net.ajcloud.wansviewplusw.support.http.Interceptor;

import net.ajcloud.wansviewplusw.support.device.DeviceCache;
import net.ajcloud.wansviewplusw.support.http.ApiConstant;
import net.ajcloud.wansviewplusw.support.utils.CipherUtil;
import net.ajcloud.wansviewplusw.support.utils.WLog;
import okhttp3.*;
import okio.Buffer;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by mamengchao on 2018/06/21.
 * Function:    添加签名，公共请求头
 */
public class OkSignatureInterceptor implements Interceptor {

    private static final String TAG = "OkGo";
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String originalUrl = request.url().url().toString();
        String encodedPath = request.url().encodedPath();
        String method = request.method();

        if (method.equalsIgnoreCase("POST")) {
            //排除登录的API
            if (!originalUrl.equals(ApiConstant.URL_UAC_REFRESH_TOKEN)
                    && !originalUrl.equals(ApiConstant.URL_START_UP)
                    && !originalUrl.equals(ApiConstant.URL_UAC_CHALLENGE)
                    && !originalUrl.equals(ApiConstant.URL_UAC_SIGNIN)
                    && !originalUrl.equals(ApiConstant.URL_UAC_SIGNUP)) {
                if (method.equalsIgnoreCase("POST")) {
                    try {
                        String timeStamp = System.currentTimeMillis() + "";
                        String reqBody = bodyToString(request);
                        StringBuilder signBody = new StringBuilder();
                        signBody.append("POST");
                        signBody.append("\n");
                        signBody.append(encodedPath);
                        signBody.append("\n");
                        signBody.append(CipherUtil.getSha256(reqBody));
                        signBody.append("\n");

                        String signToken = DeviceCache.getInstance().signinBean.signToken;
                        String stringToSign = "HMAC-SHA256" + "\n" + timeStamp + "\n" + CipherUtil.getSha256(signBody.toString());
                        String signature = CipherUtil.getClondApiSign(signToken, stringToSign);
                        Request newRequest = chain.request().newBuilder()
                                .header("Authorization", "Bearer" + " " + DeviceCache.getInstance().signinBean.accessToken)
                                .header("X-UAC-Signature", "UAC1-HMAC-SHA256" + ";" + timeStamp + ";" + signature)
                                .build();
                        return chain.proceed(newRequest);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return chain.proceed(request);
                    }

                }
            }
        }
        return chain.proceed(request);
    }

    /**
     * 获取json请求参数
     *
     * @param request 请求
     */
    private String bodyToString(Request request) {
        String data = null;
        try {
            Request copy = request.newBuilder().build();
            RequestBody body = copy.body();
            if (body == null) return null;
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            Charset charset = getCharset(body.contentType());
            data = buffer.readString(charset);
        } catch (Exception e) {
            WLog.w(e.getMessage());
        } finally {
            return data;
        }
    }

    private static Charset getCharset(MediaType contentType) {
        Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
        if (charset == null) charset = UTF_8;
        return charset;
    }
}
