package com.example.base.network

import com.example.base.network.interceptor.BusinessErrorInterceptor
import com.example.base.network.interceptor.HeaderInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * created by jump on 2021/1/21
 * describe:
 */
abstract class RetrofitFactory<T> {
    private var mBaseUrl = ""
    private var retrofit: Retrofit? = null

    abstract fun baseUrl(): String

    abstract fun getService(): Class<T>

    init {
        mBaseUrl = this.baseUrl()
        if (mBaseUrl.isEmpty()) {
            throw RuntimeException("base url can not be empty!")
        }

    }

    fun getServiceApi(): T = getRetrofit()!!.create(getService())

    fun getServiceApi(baseUrl: String): T =
        getRetrofit()?.newBuilder()?.baseUrl(baseUrl)?.build()?.create(getService())!!

    /**
     * 获取 Retrofit 实例对象
     */
    private fun getRetrofit(): Retrofit? {
        mBaseUrl = this.baseUrl()
        if (retrofit == null) {
            synchronized(RetrofitFactory::class.java) {
                if (retrofit == null) {
                    retrofit = Retrofit.Builder()
                        .baseUrl(mBaseUrl)  // baseUrl
                        .client(attachOkHttpClient())
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(ApiResultCallAdapterFactory())
                        .build()
                }
            }
        }
        return retrofit
    }

    private fun attachOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor())
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(BusinessErrorInterceptor())
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }
}