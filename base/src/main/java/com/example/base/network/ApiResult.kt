package com.example.base.network

/**
 * created by jump on 2021/1/21
 * describe:密封类，成功和失败
 */
sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Failure(val code: Int, val msg: String) : ApiResult<Nothing>()
}