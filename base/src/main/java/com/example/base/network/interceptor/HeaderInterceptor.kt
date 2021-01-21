package com.example.base.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * created by jump on 2021/1/21
 * describe:
 */
class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authorization = request.newBuilder()
            .header("Content-Type", "application/json")
            .build()
        return chain.proceed(authorization)
    }
}