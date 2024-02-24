package com.wusui.user

import com.wusui.ext.accountRegisterStatus
import com.wusui.ext.avatar
import com.wusui.ext.getFriends
import com.wusui.ext.login
import com.wusui.ext.logout
import com.wusui.ext.register
import com.wusui.ext.userInfo
import io.ktor.server.routing.*

fun Routing.user() = route("/user") {
	//POST 注册
	register()
	//POST 登录
	login()
	//获取好友列表
	getFriends()
	//获取用户信息
	userInfo()
	//退出登录
	logout()
	//头像相关
	avatar()
	//账户注册状态
	accountRegisterStatus()
}