package net.ajcloud.wansviewplusw.support.http.Interceptor;


import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import net.ajcloud.wansviewplusw.support.device.DeviceCache;
import net.ajcloud.wansviewplusw.support.http.ApiConstant;
import net.ajcloud.wansviewplusw.support.http.RequestApiUnit;
import net.ajcloud.wansviewplusw.support.utils.StringUtil;
import net.ajcloud.wansviewplusw.support.utils.WLog;
import okhttp3.*;
import okio.Buffer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

/**
 * Created by mamengchao on 2018/06/12.
 * Function:全局拦截器，刷新token
 */
public class OkTokenInterceptor implements Interceptor {

    private static final String TAG = "OkGo";
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request newRequest = null;

        Request originalRequest = chain.request();
        HttpUrl originalHttpUrl = originalRequest.url();
        String originalUrl = originalHttpUrl.url().toString();
        String method = originalRequest.method();

        if (method.equalsIgnoreCase("POST")) {
            //排除登录的API
            if (!originalUrl.equals(ApiConstant.URL_UAC_REFRESH_TOKEN)
                    && !originalUrl.equals(ApiConstant.URL_START_UP)
                    && !originalUrl.equals(ApiConstant.URL_UAC_CHALLENGE)
                    && !originalUrl.equals(ApiConstant.URL_UAC_SIGNIN)
                    && !originalUrl.equals(ApiConstant.URL_UAC_SIGNUP)) {
                final String token = DeviceCache.getInstance().getSigninBean().accessToken;
                final long expiresIn = DeviceCache.getInstance().getSigninBean().accessExpiresIn;

                if (!StringUtil.isNullOrEmpty(token)) {
                    boolean isValid = expiresIn - System.currentTimeMillis() / 1000 > 3600 * 2 / 4;
                    if (isValid) {
                        WLog.i(TAG, "token有效 ");
                    } else {
                        try {
                            //阻塞获取新token
                            boolean isSuccess = new RequestApiUnit().refreshToken();
                            if (!isSuccess) {
                                return chain.proceed(originalRequest);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            return chain.proceed(originalRequest);
                        }
                        JSONObject dataJson = null;
                        try {
                            dataJson = JSONObject.parseObject(bodyToString(originalRequest));
                            WLog.w(TAG, "---original:" + dataJson.toString());
                            JSONObject metaJson = dataJson.getJSONObject("meta");
                            metaJson.put("accessToken", DeviceCache.getInstance().getSigninBean().accessToken);
                            dataJson.put("meta", metaJson);
                            WLog.w(TAG, "---new:" + dataJson.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //重新构建Request
                        RequestBody progressRequestBody = RequestBody.create(originalRequest.body().contentType(), dataJson.toString());
                        newRequest = chain.request().newBuilder()
                                .post(progressRequestBody)
                                .url(originalUrl)
                                .build();
                        //继续请求
                        return chain.proceed(newRequest);
                    }
                } else {
                    WLog.w(TAG, "token error,please relogin");
                }
            }
        }
        return chain.proceed(originalRequest);
    }


    private static Charset getCharset(MediaType contentType) {
        Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
        if (charset == null) charset = UTF_8;
        return charset;
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
            WLog.w(TAG, "拦截前的body：" + data);
        } catch (Exception e) {
            WLog.w(e);
        } finally {
            return data;
        }
    }

    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}
