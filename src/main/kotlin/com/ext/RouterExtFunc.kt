package com.ext

import com.data.GroupReqData
import com.data.Results
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
import io.ktor.util.pipeline.*

inline var PipelineContext<*, ApplicationCall>.userSession: UserSession?
	get() = context.sessions.get()
	set(value) = context.sessions.set(value)

//@POST login扩展函数
fun Route.login() = post(path = "/login") {
	val user = call.receive<UserRegisterReqData>()
	val userRegisterRespData = UserController.validateUserInfo(user, UserController.UserType.Login)
	if (userRegisterRespData.code == 200) {
		userSession = UserSession(
			userId = user.userId,
			token = userRegisterRespData.data as? String? ?: ""
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

	if (userSession == null) {
		return@get
	}
	val userId = call.parameters["id"] ?: return@get
}

//@POST createGroup 创建群聊
fun Route.createGroup() = post("/create") {
	if (userSession == null) {
		call.respond(Results.failure())
		return@post
	}
	val groupReqData = call.receive<GroupReqData>()
	val groupResData = GroupController.validateCreateGroupInfo(groupReqData)
	call.respond(groupResData)
}

//@GET groupList 拿到所有自己是群主的群聊
fun Route.ownerGroup() = get("/owner") {
	if (userSession == null) {
		call.respond(Results.failure())
		return@get
	}
	val groupList = GroupController.getUserOwnerGroup(userSession!!.userId)
	call.respond(groupList)
}

//@GET groupList 拿到所有群聊
fun Route.joinedGroup() = get("/joined") {
	if (userSession == null) {
		call.respond(Results.failure())
		return@get
	}
	val groupList = GroupController.getJoinedGroup(userSession!!.userId)
	call.respond(groupList)
}

//@GET groupList 搜索群聊
//id 群id
fun Route.searchGroup() = get("/search") {
	if (userSession == null) {
		call.respond(Results.failure())
		return@get
	}
	val id = call.request.queryParameters["id"]
	if (id == null) {
		call.respond(Results.failure())
		return@get
	}
	val group = GroupController.findGroupById(id)
	if (group == null) {
		call.respond(Results.failure("没有找到该群"))
		return@get
	}
	call.respond(group)
}


//@GET 获取某个聊天的前N条消息
fun Route.searchChatMessage() = get("/search") {
	val id = call.parameters["id"]
	val type = call.parameters["type"]
	if (userSession == null || id == null || type == null) {
		call.respond(Results.failure())
		return@get
	}
	val messageList = MessageController.findMessages(id, type)
	call.respond(messageList)
}



















