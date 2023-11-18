package com.chat

import com.data.UserSession
import com.database.DataBaseManager
import com.database.Message
import com.database.users
import com.group.GroupController
import com.user.UserController
import com.utils.gson
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.ktorm.dsl.eq
import org.ktorm.entity.find

//用户下线
suspend fun UserSession.offline(session: DefaultWebSocketServerSession) {
	session.close()
	session.call.sessions.clear<UserSession>()
	ChatManager.onlineMembers.remove(userId)
	DataBaseManager.db.users.find { it.id eq this.userId }?.let { user ->
		ChatManager.onlineMembers.forEach {
			it.value.send("用户${user.userName}离开了")
		}
	}

}

//用户上线
fun UserSession.online(session: DefaultWebSocketServerSession) {
	ChatManager.onlineMembers[userId] = session
}

//服务端处理消息
//type可能为private，group两种
suspend fun processMsg(userSession: UserSession, text: String) {
	val message = gson.fromJson(text, Message::class.java) ?: return
	if (message.fromId != userSession.userId) {
		return
	}
	message.fromId = userSession.userId
	//消息存储进数据库
	ChatManager.saveMessage(message)
	//消息是群聊消息还是私聊消息
	val type = message.messageType
	when (type) {
		"private" -> {
			val user = UserController.findUserById(message.toId) ?: return
			ChatManager.onlineMembers[user.userId]?.sendSerialized(message)

		}

		"group" -> {
			//检查是否存在这个群，如果存在，那么在数据库中找到所有在这个群的用户
			//TODO 每次都查数据库会很慢，需要换到Redis存储
			val users = message.toId?.let { GroupController.findAllUsersByGroupId(it) }
			users?.let { it ->
				it.data?.map {
					UserSession(userId = it.userId, token = it.pwd)
				}?.forEach { session ->
					ChatManager.onlineMembers[session.userId]?.sendSerialized(message)
				}
			}

		}
	}
}
