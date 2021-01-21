package com.example.base.network

/**
 * created by jump on 2021/1/21
 * describe:
 */

object ApiError {
    //数据是null
    val dataIsNull = Error(-1,"data is null")
    //http status code 不是 成功
    val httpStatusCodeError = Error(-2,"Server error. Please try again later.")
    //未知异常
    val unknownException = Error(-3,"unknown exception")
}

data class Error(val errorCode:Int,val errorMsg:String)