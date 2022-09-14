package com.datas

import kotlinx.serialization.Serializable

@Serializable
data class SignInfo(
    val content: String
)


@Serializable
open class ResInfo(
    val code: Int, val success: Boolean, val msg: String
) {
    object SignSuccess : ResInfo(code = 0, success = true, msg = "注册成功！")
    object SignFailure : ResInfo(code = -1, success = false, msg = "注册失败！")
    object LoginSuccess : ResInfo(code = 0, success = true, msg = "登录成功！")
    object ModSuccess : ResInfo(code = 0, success = true, msg = "修改成功！")
    object LoginFailure : ResInfo(code = -1, success = false, msg = "登录失败！")
    object ModFailure : ResInfo(code = -1, success = false, msg = "修改失败！")
}
