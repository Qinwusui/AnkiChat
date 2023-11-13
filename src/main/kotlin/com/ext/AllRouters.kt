package com.ext

import com.data.ApplyData
import com.data.GroupReqData
import com.data.Results
import com.data.UserRegisterReqData
import com.data.UserSession
import com.friends.Apply
import com.friends.FriendsController
import com.group.GroupController
import com.message.MessageController
import com.user.UserController
import com.utils.generateId
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.*

inline var PipelineContext<*, ApplicationCall>.userSession: UserSession?
	get() = context.sessions.get()
	set(value) = context.sessions.set(value)

//@POST 登录
fun Route.login() = post(path = "/login") {
	val user = call.receive<UserRegisterReqData>()
	val userRegisterRespData = UserController.validateUserInfo(user, UserController.UserType.Login)
	if (userRegisterRespData.code == 200) {
		runCatching {
			userRegisterRespData.data as Pair<*, *>
		}.onFailure {
			call.respond(Results.failure("获取用户id失败:${it.message}"))
		}.onSuccess {
			val (id, token) = it
			userSession = UserSession(
				userId = id as? String? ?: "",
				token = token as? String? ?: ""
			)
		}

	}
	call.respond(userRegisterRespData)
}

//@POST register注册扩展函数
fun Route.register() = post("/register") {
	val user = call.receive<UserRegisterReqData>()
	val userRegisterRespData = UserController.validateUserInfo(user, UserController.UserType.Register)

	call.respond(userRegisterRespData)
}

//退出登录
fun Route.logout() = post("/logout") {
	val user = call.receive<UserRegisterReqData>()
	val userRespData = UserController.validateUserInfo(user, UserController.UserType.Logout)
	call.respond(userRespData)
}

//@GET getFriends获取好友列表扩展函数
fun Route.getFriends() = get(path = "/{id}/friends") {

	if (userSession == null) {
		call.respond(Results.failure(msg = "用户信息不正确"))

		return@get
	}
	val userId = call.parameters["id"] ?: return@get
	val friends = FriendsController.findFriendsById(userId)
	call.respond(friends)
}

//@POST createGroup 创建群聊
fun Route.createGroup() = post("/create") {

	if (userSession == null) {
		call.respond(Results.failure(msg = "用户信息不正确"))
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
	call.respond(group)
}


//@GET 获取某个聊天的前N条消息
fun Route.searchChatMessage() = get("/search") {
	val id = call.parameters["id"]
	val type = call.parameters["type"]//消息是私聊还是群聊
	if (userSession == null || id == null || type == null) {
		call.respond(Results.failure())
		return@get
	}
	val messageList = MessageController.findMessages(id, type)
	call.respond(messageList)
}

//@GET 获取某用户收到的所有好友申请记录
fun Route.getAllApplies() = get("/applies") {
	if (userSession == null) {
		call.respond(Results.failure("用户信息缺失"))
		return@get
	}
	val userId = userSession!!.userId
	val applies = FriendsController.getAllApplies(userId)
	call.respond(applies)
}

//@Post 发送好友申请
fun Route.sendFriendApply() = post("/send") {
	if (userSession == null) {
		call.respond(Results.failure("用户信息缺失"))
		return@post
	}
	val applyData = call.receive<ApplyData>()
	val apply = Apply {
		sendId = applyData.sendId
		receiveId = applyData.receiveId
		sendTime = System.currentTimeMillis()
		applyId = generateId()
		applyMessage = applyData.applyMessage
	}

	val status = FriendsController.sendAddApply(apply)
	call.respond(status)
}

//@Post 同意好友申请
fun Route.agreeFriendApply() = post("/agree") {
	if (userSession == null) {
		call.respond(Results.failure("用户信息缺失"))
		return@post
	}
	val apply = call.receive<Apply>()
	if (apply.applyId.isEmpty()) {
		call.respond(Results.failure("申请信息为空"))
		return@post
	}
	val results = FriendsController.agreeApply(apply.applyId)

	call.respond(results)
}

//@Post 拒绝好友申请
fun Route.refuseFriendApply() = post("/refuse") {
	if (userSession == null) {
		call.respond(Results.failure("用户信息缺失"))
		return@post
	}
	val apply = call.receive<Apply>()
	if (apply.applyId.isEmpty()) {
		call.respond(Results.failure("申请信息为空"))
		return@post
	}
	val results = FriendsController.refuseApply(applyId = apply.applyId)
	call.respond(results)
}
















