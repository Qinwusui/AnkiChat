package com.chat

import com.group.GroupController
import com.user.Message
import com.user.UserSession
import com.utils.gson
import com.utils.successOut
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

//用户下线
suspend fun UserSession.offline(session: DefaultWebSocketServerSession) {
	session.close()
	session.call.sessions.clear<UserSession>()
	ChatManager.onlineMembers.remove(userId)
	ChatManager.onlineMembers.forEach {
		it.value.send("用户${this.userName}离开了")
	}
}

//用户上线
fun UserSession.online(session: DefaultWebSocketServerSession) {
	ChatManager.onlineMembers[userId] = session
}

//服务端处理消息
//type可能为private，group两种
suspend fun DefaultWebSocketServerSession.processMsg(text: String) {
	val message = gson.fromJson(text, Message::class.java) ?: return
	//接收Id
	val toId = message.toId ?: return
	//消息是群聊消息还是私聊消息
	val type = message.type ?: return
	when (type) {
		"private" -> {

		}

		"group" -> {
			//检查是否存在这个群，如果存在，那么在数据库中找到所有在这个群的用户
			val users = GroupController.findAllUsersByGroupId(toId)
			users?.map {
				UserSession(userId = it.userId, userName = it.userName, token = "")
			}?.forEach {
				ChatManager.onlineMembers[it.userId]?.sendSerialized(message)
			}

		}
	}
}
