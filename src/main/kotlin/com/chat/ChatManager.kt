package com.chat

import com.database.DataBaseManager
import com.database.Message
import com.database.messages
import com.database.users
import com.utils.generateId
import io.ktor.server.websocket.*
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import java.util.concurrent.ConcurrentHashMap

object ChatManager {

	val onlineMembers = ConcurrentHashMap<String, DefaultWebSocketServerSession>()


	//检查发送对象id是否为空
	fun checkToId(toId: String?): Boolean {
		if (toId.isNullOrEmpty()) return false
		if (!toId.lowercase().all { it in '0'..'9' || it in 'a'..'z' }) return false
		val user = DataBaseManager.db.users.find { it.id eq toId }
		return user != null
	}

	//存储消息
	fun saveMessage(message: com.data.Message): Boolean {
		if (!checkToId(message.toId) || !checkToId(message.toId) || !checkToId(message.fromId)) return false
		val msg = Message {
			fromId = message.fromId
			toId = message.toId
			messageId = generateId()
			messageType = message.type
			toGroupId = message.toGroup
			content = message.data.content
		}
		DataBaseManager.db.messages.add(msg)
		return true
	}
}