package com.vn.fa.net;


import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by binhbt on 6/7/2016.
 */
public class ApiService {
    private String baseUrl;
    private boolean isLogging;
    private Interceptor interceptor;
    private Converter.Factory converterFactory;
    public ApiService(String baseUrl, boolean isLogging, Interceptor interceptor, Converter.Factory converterFactory){
        this.baseUrl = baseUrl;
        this.isLogging = isLogging;
        this.interceptor = interceptor;
        this.converterFactory = converterFactory;
    }

    public <T> T create(final Class<T> clazz){
        CookieJar cookieJar = new CookieJar() {
            private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url.host());
                if (cookies != null){
                    //Log.e("LEO-COOKIES", "Have cookies "+cookies.size() +" "+url.toString());
                }else{
                    //Log.e("LEO-COOKIES", "NO cookies "+url.toString());
                }
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        };

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        okBuilder.cookieJar(cookieJar);

        Retrofit.Builder builder=  new Retrofit.Builder()
                .baseUrl(this.baseUrl)
                .addConverterFactory(converterFactory == null?GsonConverterFactory.create():converterFactory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okBuilder.build());
        if (this.isLogging || this.interceptor != null){
            HttpLoggingInterceptor okInterceptor = new HttpLoggingInterceptor();
            okInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            //OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
            if (this.isLogging){
                okBuilder.addInterceptor(okInterceptor);
            }
            if (this.interceptor != null){
                okBuilder.addInterceptor(this.interceptor);

            }
            OkHttpClient client = okBuilder.build();
            builder.client(client);
        }
        Retrofit retrofit = builder.build();
        return retrofit.create(clazz);
    }
    public static final class Builder {
        private String baseUrl;
        private boolean isLogging;
        private Interceptor interceptor;
        private Converter.Factory converterFactory;
        public Builder baseUrl(String baseUrl){
            this.baseUrl = baseUrl;
            return this;
        }
        public Builder logging(boolean isLogging){
            this.isLogging = isLogging;
            return this;
        }
        public Builder converterFactory(Converter.Factory converterFactory){
            this.converterFactory = converterFactory;
            return this;
        }
        public ApiService build(){
            return new ApiService(this.baseUrl, this.isLogging, this.interceptor, this.converterFactory);
        }
        public Builder interceptor(Interceptor intercepter){
            this.interceptor  = intercepter;
            return this;
        }
    }
}
