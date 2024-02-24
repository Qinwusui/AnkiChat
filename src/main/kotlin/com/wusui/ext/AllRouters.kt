package com.wusui.ext

import com.wusui.data.ApplyData
import com.wusui.data.GroupReqData
import com.wusui.data.Results
import com.wusui.data.UserRegisterReqData
import com.wusui.data.UserSession
import com.wusui.database.Apply
import com.wusui.friends.FriendsController
import com.wusui.group.GroupController
import com.wusui.message.MessageController
import com.wusui.user.UserController
import com.wusui.utils.generateId
import com.wusui.utils.isEndWithImg
import io.ktor.http.content.*
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
			userRegisterRespData.data as Map<*, *>
		}.onFailure {
			call.respond(Results.failure("获取用户id失败:${it.message}"))
		}.onSuccess {
			val id = it["userId"] as String?
			val token = it["token"] as String?
			userSession = UserSession(
				userId = id ?: "",
				token = token ?: ""
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

//头像相关
fun Route.avatar() = route("/avatar") {
	uploadAvatar()
	getAvatar()
}

//获取用户注册状态
fun Route.accountRegisterStatus() = get("/registerStatus") {
	val userName = call.request.queryParameters["userName"]
	if (userName == null) {
		call.respond(Results.failure("用户名不能为空"))
		return@get
	}
	if (UserController.userExist(userName)) {
		call.respond(Results.success(data = true))
	} else {
		call.respond(Results.success(data = false))
	}
}

//上传头像
fun Route.uploadAvatar() = post("/upload") {
	if (userSession == null) {
		call.respond(Results.failure("用户信息不正确"))
		return@post
	}
	val id = call.request.queryParameters["id"]
	if (id == null) {
		call.respond(Results.failure("id不正确"))
		return@post
	}
	var name = ""
	var content = byteArrayOf()
	val multiPartData = call.receiveMultipart()
	multiPartData.forEachPart { partData: PartData ->
		when (partData) {
			is PartData.FileItem -> {
				name = partData.originalFileName ?: ""
				content = partData.streamProvider().readBytes()
			}

			else -> {}

		}
		partData.dispose()

	}
	if (name.isEmpty()) {
		call.respond(Results.failure("请提供文件名称"))
		return@post
	}
	if (!name.isEndWithImg()) {
		call.respond(Results.failure("文件格式不正确"))
		return@post
	}
	UserController.saveAvatar(id = id, avatar = content)
	call.respond(Results.success("上传成功"))
}

//获取头像
fun Route.getAvatar() = get("/get") {
	if (userSession == null) {
		call.respond(Results.failure("用户未认证"))
		return@get
	}
	//可能是用户id，也可能是群组id
	val id = call.request.queryParameters["id"] ?: userSession?.userId
	if (id == null) {
		call.respond(Results.failure("id不正确"))
		return@get
	}
	val avatar = UserController.getAvatar(id)
	call.respond(avatar)
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

//@GET 获取用户信息
fun Route.userInfo() = get("/info") {
	val userId = call.parameters["id"]

	if (userId == null || userSession == null) {
		call.respond(Results.failure("用户信息不正确"))
		return@get
	}
	val userInfo = UserController.requestUserInfo(userId)
	call.respond(userInfo)
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
	val id = call.parameters["groupId"]
	if (id == null) {
		call.respond(Results.failure())
		return@get
	}
	val results = GroupController.requestGroupInfo(id)
	call.respond(results)
}


//@GET 获取某个聊天的前N条消息
fun Route.searchChatMessage() = get("/search") {
	val id = call.parameters["id"]
	val type = call.parameters["type"]//消息是私聊还是群聊
	val l = call.parameters["limit"]
	if (userSession == null || id == null || type == null) {
		call.respond(Results.failure())
		return@get
	}
	val messageList = MessageController.findMessages(id, type)
	call.respond(messageList)
}

//@GET 获取单条消息
fun Route.messageInfo() = get("/info") {
	val id = call.parameters["id"]
	if (userSession == null || id == null) {
		call.respond(Results.failure())
		return@get
	}
	val message = MessageController.findMessage(id)
	call.respond(message)
}

//@GET 获取用户的最近聊天
fun Route.findUsersRecentMessageRecord() = get("/recent") {
	val id = call.parameters["id"]
	if (userSession == null || id == null) {
		call.respond(Results.failure())
		return@get
	}
	val list = MessageController.findUsersRecentMessageRecord(id)
	call.respond(list)
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
	if (userSession!!.userId != applyData.sendId) {
		call.respond(Results.failure("用户信息不匹配"))
		return@post
	}

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













