package com.example.elearning;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import  static com.example.elearning.HttpClient.getClient;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    public sqlinterface getClient(Context context) {
        Gson gson = new GsonBuilder().setLenient().create();
        OkHttpClient httpClient = HttpClient.getClient(context);
        // Retrofit rf=new Retrofit.Builder().baseUrl("https://elearningapp.infinityfreeapp.com/")
        //trust anchor for certifcation path not found for above website

        Retrofit rf = new Retrofit.Builder().baseUrl("https://gunslingerat.000webhostapp.com/Elearning_app/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build();
        sqlinterface sql = rf.create(sqlinterface.class);
       return sql;
    }
}
