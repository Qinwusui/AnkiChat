package com.routers

import com.group.GroupController
import com.user.GroupListResData
import com.user.GroupReqData
import com.user.UserController
import com.user.UserRegisterReqData
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

//@POST login扩展函数
fun Route.login() = post(path = "/login") {
	val user = call.receive<UserRegisterReqData>()
	val userRegisterRespData = UserController.validate(user, false)
	call.respond(userRegisterRespData)
}

//@POST register注册扩展函数
fun Route.register() = post("/register") {
	val user = call.receive<UserRegisterReqData>()
	val userRegisterRespData = UserController.validate(user, true)
	call.respond(userRegisterRespData)
}

//@GET getFriends获取好友列表扩展函数
fun Route.getFriends() = get(path = "{id}/friends") {
	val userId = call.parameters["id"] ?: return@get
}

//@POST createGroup 创建群聊
fun Route.createGroup() = post("/create") {
	val groupReqData = call.receive<GroupReqData>()
	val groupResData = GroupController.validateGroup(groupReqData)
	call.respond(groupResData)
}

//@GET groupList 拿到群聊
fun Route.groupList() = get("/list") {
	val userId = call.request.queryParameters["userId"]
	if (userId == null) {
		call.respond(GroupListResData(success = false, msg = "用户名为空"))
		return@get
	}
	val groupList = GroupController.getGroupList(userId)
	call.respond(groupList)
}