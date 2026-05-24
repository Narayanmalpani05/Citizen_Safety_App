package com.example.safeu2.api;

import com.example.safeu2.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // 1. For emulator testing
    public static final String BASE_URL_EMULATOR = "http://10.0.2.2:5000/";
    
    // 2. For physical device testing on local WiFi (Change this to your computer's IPv4 address)
    public static final String BASE_URL_PHYSICAL_DEVICE = "http://192.168.0.108:5000/";
    
    // 3. For production release
    public static final String BASE_URL_PROD = "https://malurldet.onrender.com/";
    
    // TOGGLE THIS TRUE if testing on a physical phone connected to the same WiFi as the Flask backend
    public static final boolean TESTING_ON_PHYSICAL_DEVICE = true;

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            String baseUrl;
            if (BuildConfig.DEBUG) {
                baseUrl = TESTING_ON_PHYSICAL_DEVICE ? BASE_URL_PHYSICAL_DEVICE : BASE_URL_EMULATOR;
            } else {
                baseUrl = BASE_URL_PROD;
            }

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(90, TimeUnit.SECONDS)
                    .readTimeout(90, TimeUnit.SECONDS)
                    .writeTimeout(90, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
