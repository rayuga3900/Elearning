package com.example.elearning;

import android.content.Context;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
public class HttpClient {

        private static OkHttpClient okHttpClient;

        private HttpClient() {
            // Private constructor to prevent instantiation
        }

        public static synchronized OkHttpClient getClient(Context context) {
            if (okHttpClient == null) {
                // Create OkHttpClient instance with desired configuration
                long CACHE_SIZE = 10 * 1024 * 1024; // 10 MB

                /*
                when i didnt set the connectTImeout it takes more than 14 seconds
                for  login and fetching requests but after i set
                .connectTimeout(3,TimeUnit.SECONDS) login  and fetching requests
                happens under 3-4  seconds
                 */
                okHttpClient = new OkHttpClient.Builder()
                        .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                        .connectTimeout(1,TimeUnit.SECONDS)
                        .readTimeout(5,TimeUnit.SECONDS)
                        .writeTimeout(5,TimeUnit.SECONDS)
                        .connectionPool(new ConnectionPool(5, 3600, TimeUnit.SECONDS))
                        .cache(new Cache(context.getCacheDir(), CACHE_SIZE))
                        .build();
            }
            return okHttpClient;
            //you hav created the connection pool and used caching successfully
            // after this you only need to use this by calling getClient in the
            //the respective activity
        }
    }


