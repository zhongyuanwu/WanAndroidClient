package com.example.base.network

import okhttp3.Request
import okio.Timeout
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * created by jump on 2021/1/21
 * describe: 自定义callAdapter 统一处理返回结果 返回结果异常处理
 */

/**
 * CallAdapter 工厂类 检查类型ApiResult<T>
 */
class ApiResultCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        //检查类型是否Call<T>
        check(getRawType(returnType) == Call::class.java) { "$returnType 必须是 retrofit2.Call." }
        check(returnType is ParameterizedType) { "$returnType 必须是 parameterized" }

        //获取Call<T> 中的T  检查是否ApiResult<T>
        val apiResultType = getParameterUpperBound(0, returnType)
        check(getRawType(apiResultType) == ApiResult::class.java) { "$apiResultType 必须是 ApiResult" }
        check(apiResultType is ParameterizedType) { "$apiResultType 必须是 parameterized" }

        //取出ApiResult<T>中的T api返回数据对应的数据类型
        val dateType = getParameterUpperBound(0, apiResultType)
        return ApiResultCallAdapter<Any>(dateType)
    }

}

/**
 *
 */
class ApiResultCallAdapter<T>(private val type: Type) : CallAdapter<T, Call<ApiResult<T>>> {
    override fun adapt(call: Call<T>): Call<ApiResult<T>> = ApiResultCall(call)

    override fun responseType(): Type = type
}

/**
 * 统一执行  callback.onResponse 成功回调
 */
class ApiResultCall<T>(private val call: Call<T>) : Call<ApiResult<T>> {
    override fun enqueue(callback: Callback<ApiResult<T>>) {
        call.enqueue(object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                val failure = if (t is ApiException) ApiResult.Failure(t.code, t.msg)
                else ApiResult.Failure(
                    ApiError.unknownException.errorCode,
                    ApiError.unknownException.errorMsg
                )
                callback.onResponse(this@ApiResultCall, Response.success(failure))
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    //这里担心response.body()可能会为null(还没有测到过这种情况)，所以做了一下这种情况的处理，
                    // 处理了这种情况后还有一个好处是我们就能保证我们传给ApiResult.Success的对象就不是null，这样外面用的时候就不用判空了
                    val apiResult = if (response.body() == null) {
                        ApiResult.Failure(
                            ApiError.dataIsNull.errorCode,
                            ApiError.dataIsNull.errorMsg
                        )
                    } else {
                        ApiResult.Success(response.body()!!)
                    }
                    callback.onResponse(this@ApiResultCall, Response.success(apiResult))
                } else {
                    val failure = ApiResult.Failure(
                        ApiError.httpStatusCodeError.errorCode,
                        ApiError.httpStatusCodeError.errorMsg
                    )
                    callback.onResponse(this@ApiResultCall, Response.success(failure))
                }
            }

        })
    }

    override fun isExecuted(): Boolean = call.isExecuted

    override fun timeout(): Timeout = call.timeout()

    override fun clone(): Call<ApiResult<T>> = ApiResultCall(call.clone())

    override fun isCanceled(): Boolean = call.isCanceled

    override fun cancel() = call.cancel()

    override fun execute(): Response<ApiResult<T>> {
        throw UnsupportedOperationException("ApiResultCall 不支持 synchronous execution")
    }

    override fun request(): Request = call.request()
}