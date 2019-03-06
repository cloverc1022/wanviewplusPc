package net.ajcloud.wansviewplusw.support.http.Interceptor;

import net.ajcloud.wansviewplusw.support.entity.LocalInfo;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * 添加公共头
 */
public class CommonInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();
        Request signedRequest = requestBuilder
                .addHeader("x-agent-token", LocalInfo.getInstance().deviceId)
                .build();
        return chain.proceed(signedRequest);
    }
}
