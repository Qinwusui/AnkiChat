package com.ext

import com.data.GroupListResData
import com.data.GroupReqData
import com.data.GroupResData
import com.data.UserRegisterReqData
import com.data.UserSession
import com.group.GroupController
import com.message.MessageController
import com.user.UserController
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

//@POST login扩展函数
fun Route.login() = post(path = "/login") {
	val user = call.receive<UserRegisterReqData>()
	val userRegisterRespData = UserController.validateUserInfo(user, UserController.UserType.Login)
	if (userRegisterRespData.success) {
		call.sessions.set(
			UserSession(
				userId = userRegisterRespData.userId,
				token = userRegisterRespData.token,
				userName = user.userName
			)
		)
	}
	call.respond(userRegisterRespData)
}

//@POST register注册扩展函数
fun Route.register() = post("/register") {
	val user = call.receive<UserRegisterReqData>()
	val userRegisterRespData = UserController.validateUserInfo(user, UserController.UserType.Register)

	call.respond(userRegisterRespData)
}

fun Route.logout() = post("/logout") {
	val user = call.receive<UserRegisterReqData>()
	val userRespData = UserController.validateUserInfo(user, UserController.UserType.Logout)
	call.respond(userRespData)
}

//@GET getFriends获取好友列表扩展函数
fun Route.getFriends() = get(path = "{id}/friends") {
	val session = call.sessions.get<UserSession>()
	if (session == null) {
		return@get
	}
	val userId = call.parameters["id"] ?: return@get
}

//@POST createGroup 创建群聊
fun Route.createGroup() = post("/create") {
	val session = call.sessions.get<UserSession>()
	if (session == null) {
		call.respond(GroupResData(success = false, msg = "不正确的请求"))
		return@post
	}
	val groupReqData = call.receive<GroupReqData>()
	val groupResData = GroupController.validateCreateGroupInfo(groupReqData)
	call.respond(groupResData)
}

//@GET groupList 拿到所有自己是群主的群聊
fun Route.ownerGroup() = get("/owner") {
	val session = call.sessions.get<UserSession>()
	if (session == null) {
		call.respond(GroupListResData(success = false, msg = "不正确的请求"))
		return@get
	}
	val groupList = GroupController.getUserOwnerGroup(session.userId)
	call.respond(groupList)
}

//@GET groupList 拿到所有群聊
fun Route.joinedGroup() = get("/joined") {
	val session = call.sessions.get<UserSession>()
	if (session == null) {
		call.respond(GroupListResData(success = false, msg = "不正确的请求"))
		return@get
	}
	val groupList = GroupController.getJoinedGroup(session.userId)
	call.respond(groupList)
}

//@GET groupList 搜索群聊
//id 群id
fun Route.searchGroup() = get("/search") {
	val session = call.sessions.get<UserSession>()
	if (session == null) {
		call.respond(GroupListResData(success = false, msg = "不正确的请求"))
		return@get
	}
	val id = call.request.queryParameters["id"]
	if (id == null) {
		call.respond(GroupListResData(success = false, msg = "不正确的请求"))
		return@get
	}
	val group = GroupController.findGroupById(id)
	if (group == null) {
		call.respond(GroupListResData(success = false, msg = "没有找到该群"))
		return@get
	}
	call.respond(group)
}


//@GET 获取某个聊天的前N条消息
fun Route.searchChatMessage() = get("/search") {
	val session = call.sessions.get<UserSession>()
	val id = call.parameters["id"]
	val type = call.parameters["type"]
	if (session == null || id == null || type == null) {
		call.respond(GroupResData(success = false, msg = "不正确的请求"))
		return@get
	}
	val messageList = MessageController.findMessages(id, type)
	call.respond(messageList)
}