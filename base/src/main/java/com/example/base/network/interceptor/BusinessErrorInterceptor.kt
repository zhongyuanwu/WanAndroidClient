package com.example.base.network.interceptor

import com.example.base.network.ApiException
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * created by jump on 2021/1/21
 * describe:
 */
class BusinessErrorInterceptor:Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var response = chain.proceed(chain.request())
        //http status不是成功的情况下，我们不处理
        if (!response.isSuccessful){
            return response
        }

        //因为response.body().string() 只能调用一次，所以这里读取responseBody不使用response.body().string()，原因：https://juejin.im/post/6844903545628524551
        //以下读取resultString的代码节选自
        //https://github.com/square/okhttp/blob/master/okhttp-logging-interceptor/src/main/kotlin/okhttp3/logging/HttpLoggingInterceptor.kt

        val responseBody = response.body!!
        val source = responseBody.source()
        source.request(Long.MAX_VALUE) // Buffer the entire body.
        var buffer = source.buffer
        val contentType = responseBody.contentType()
        val charset: Charset = contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8
        val resultString = buffer.clone().readString(charset)


        val jsonObject = JSONObject(resultString)
        if (!jsonObject.has("errorCode")) {
            return response
        }

        val errorCode = jsonObject.optInt("errorCode")
        //对于业务成功的情况不做处理
        if (errorCode == 0) {
            return response
        }
        //我们的示例里服务器没有返回errorMsg,一般实际应用中服务器都会有errorMsg
        throw ApiException(errorCode, "some error msg")
    }
}