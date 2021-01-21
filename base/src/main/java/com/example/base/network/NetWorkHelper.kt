package com.example.base.network

import com.example.base.network.interceptor.BusinessErrorInterceptor
import com.example.base.network.interceptor.HeaderInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * created by jump on 2021/1/21
 * describe:
 */

private const val BASE_URL = ""

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor())
    .addInterceptor(HeaderInterceptor())
    .addInterceptor(BusinessErrorInterceptor())
    .build()

val retrofit: Retrofit = Retrofit.Builder()
    .addCallAdapterFactory(ApiResultCallAdapterFactory())
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()