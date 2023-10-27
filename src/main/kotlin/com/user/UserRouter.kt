package com.user

import com.ext.getFriends
import com.ext.login
import com.ext.logout
import com.ext.register
import io.ktor.server.routing.*

fun Routing.user() {

	route("/user") {
		//POST 注册
		register()
		//POST 登录
		login()
		//获取好友列表
		getFriends()
		//退出登录
		logout()
	}
}