package com.shelfperk.shelfperktextreader.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object APIClient {
    private val BASE_URL = "https://wordsapiv1.p.rapidapi.com"
    private val NEWS_BASE_URL = "https://newsapi.org/"
    private var mRetrofit: Retrofit? = null
    private var newsRetrofit: Retrofit? = null

    val client: Retrofit
        get() {
            val builder = OkHttpClient().newBuilder()
            builder.readTimeout(10, TimeUnit.SECONDS)
            builder.connectTimeout(5, TimeUnit.SECONDS)

            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)
            builder.addInterceptor(interceptor)

            if (mRetrofit == null) {
                mRetrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return this.mRetrofit!!
        }

    val clientNews: Retrofit
        get() {
            val builder = OkHttpClient().newBuilder()
            builder.readTimeout(10, TimeUnit.SECONDS)
            builder.connectTimeout(5, TimeUnit.SECONDS)

            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)
            builder.addInterceptor(interceptor)

            if (newsRetrofit == null) {
                newsRetrofit = Retrofit.Builder()
                    .baseUrl(NEWS_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return this.newsRetrofit!!
        }
}