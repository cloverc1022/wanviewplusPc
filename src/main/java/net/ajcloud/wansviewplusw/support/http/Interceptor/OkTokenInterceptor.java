//package net.ajcloud.wansviewplusw.support.http.Interceptor;
//
//import android.text.TextUtils;
//
//import net.ajcloud.wansviewplus.main.application.MainApplication;
//import net.ajcloud.wansviewplus.support.tools.manager.SigninAccountManager;
//import net.ajcloud.wansviewplus.support.core.okgo.model.HttpParams;
//import net.ajcloud.wansviewplus.support.core.okgo.request.base.ProgressRequestBody;
//import net.ajcloud.wansviewplus.support.core.okgo.utils.OkLogger;
//import net.ajcloud.wansviewplus.support.tools.WLog;
//import net.ajcloud.wansviewplus.support.utils.preference.PreferenceKey;
//import net.ajcloud.wansviewplus.support.utils.preference.SPUtil;
//
//import net.ajcloud.wansviewplusw.support.http.ApiConstant;
//import net.ajcloud.wansviewplusw.support.utils.StringUtil;
//import net.ajcloud.wansviewplusw.support.utils.WLog;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.nio.charset.Charset;
//import java.text.SimpleDateFormat;
//
//import okhttp3.HttpUrl;
//import okhttp3.Interceptor;
//import okhttp3.MediaType;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//import okio.Buffer;
//
///**
// * Created by mamengchao on 2018/06/12.
// * Function:全局拦截器，刷新token
// */
//public class OkTokenInterceptor implements Interceptor {
//
//    private static final String TAG = "OkGo";
//    private static final Charset UTF_8 = Charset.forName("UTF-8");
//
//    @Override
//    public Response intercept(Chain chain) throws IOException {
//        Request newRequest = null;
//
//        Request originalRequest = chain.request();
//        HttpUrl originalHttpUrl = originalRequest.url();
//        String originalUrl = originalHttpUrl.url().toString();
//        String method = originalRequest.method();
//
//        final String token = SigninAccountManager.getInstance().getCurrentAccountAccessToken();
//        final long expiresIn = SigninAccountManager.getInstance().getCurrentAccountAccessTokenTime();
//
//        if (method.equalsIgnoreCase("POST")) {
//            //排除登录的API
//            if (!originalUrl.equals(ApiConstant.URL_UAC_REFRESH_TOKEN)
//                    && !originalUrl.equals(ApiConstant.URL_START_UP)
//                    && !originalUrl.equals(ApiConstant.URL_UAC_CHALLENGE)
//                    && !originalUrl.equals(ApiConstant.URL_UAC_SIGNIN)
//                    && !originalUrl.equals(ApiConstant.URL_UAC_SIGNUP)) {
//                if (!StringUtil.isNullOrEmpty(token)) {
//                    boolean isValid = expiresIn - System.currentTimeMillis() / 1000 > 3600 * 2 / 4;
//                    if (isValid) {
//                        WLog.i(TAG, "token有效 ");
//                    } else {
//                        try {
//                            //阻塞获取新token
//                            Response response = new RequestApiUnit(MainApplication.getApplication()).refreshToken_sync();
//                            if (response == null) {
//                                return chain.proceed(originalRequest);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            return chain.proceed(originalRequest);
//                        }
//                        JSONObject dataJson = null;
//                        try {
//                            dataJson = new JSONObject(bodyToString(originalRequest));
//                            WLog.d(TAG, "---original:" + dataJson.toString());
//                            JSONObject metaJson = dataJson.getJSONObject("meta");
//                            metaJson.put("accessToken", SigninAccountManager.getInstance().getCurrentAccountAccessToken());
//                            dataJson.put("meta", metaJson);
//                            WLog.d(TAG, "---new:" + dataJson.toString());
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        //重新构建Request
//                        ProgressRequestBody progressRequestBody = (ProgressRequestBody) originalRequest.body();
//                        progressRequestBody.setRequestBody(RequestBody.create(HttpParams.MEDIA_TYPE_JSON, dataJson.toString()));
//                        newRequest = chain.request().newBuilder()
//                                .post(progressRequestBody)
//                                .url(originalUrl)
//                                .build();
//                        //继续请求
//                        return chain.proceed(newRequest);
//                    }
//                } else {
//                    WLog.d(TAG, "token error,please relogin");
//                    MainApplication.getApplication().logout(false, null);
//                }
//            }
//        }
//        return chain.proceed(originalRequest);
//    }
//
//
//    private static Charset getCharset(MediaType contentType) {
//        Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
//        if (charset == null) charset = UTF_8;
//        return charset;
//    }
//
//    /**
//     * 获取json请求参数
//     *
//     * @param request 请求
//     */
//    private String bodyToString(Request request) {
//        String data = null;
//        try {
//            Request copy = request.newBuilder().build();
//            RequestBody body = copy.body();
//            if (body == null) return null;
//            Buffer buffer = new Buffer();
//            body.writeTo(buffer);
//            Charset charset = getCharset(body.contentType());
//            data = buffer.readString(charset);
//            WLog.d(TAG, "拦截前的body：" + data);
//        } catch (Exception e) {
//            OkLogger.printStackTrace(e);
//        } finally {
//            return data;
//        }
//    }
//
//    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//}
