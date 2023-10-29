package com.message

import com.data.GroupResData
import com.data.UserSession
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

//@GET 获取某个聊天的前N条消息
fun Route.findChatMessage() = get("/messages") {
	val session = call.sessions.get<UserSession>()
	val id = call.parameters["id"]
	val type = call.parameters["type"]
	if (session == null || id == null || type == null) {
		call.respond(GroupResData(success = false, msg = "不正确的请求"))
		return@get
	}
	val messageList = MessageController.findMessages(id,type)
}