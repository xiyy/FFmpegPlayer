package com.xi.liuliu.ffmpegplayer.http;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by liuliu on 2018/2/1.
 */

public class RetrofitManager {
    private static final String TAG = "RetrofitManager";
    private static final int TIME_OUT = 5;
    private static final String BASE_URL = "";

    public static RequestManager getRequestManager(String requestName) {
        return new Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getOkHttpClient(requestName))
                .build()
                .create(RequestManager.class);
    }

    private static OkHttpClient getOkHttpClient(final String requestName) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
        builder.readTimeout(TIME_OUT, TimeUnit.SECONDS);
        //添加统一的header
        Interceptor headerInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request.Builder builder = chain.request().newBuilder();
                builder.header("x-hzz-token", "token value");
                return chain.proceed(builder.build());
            }
        };
        //日志logger拦截器
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String s) {
                if (!TextUtils.isEmpty(s)) {
                    String startChar = s.substring(0, 1);
                    if ("{".equals(startChar)) {
                        //只打印json
                        Log.i(TAG, requestName + "==>" + s);
                    }
                }

            }
        });
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(httpLoggingInterceptor);
        builder.addInterceptor(headerInterceptor);
        return builder.build();
    }
}
