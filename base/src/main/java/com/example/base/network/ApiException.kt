package com.example.base.network

import java.io.IOException

/**
 * created by jump on 2021/1/21
 * describe:
 */
class ApiException(val code:Int,val msg:String): IOException()