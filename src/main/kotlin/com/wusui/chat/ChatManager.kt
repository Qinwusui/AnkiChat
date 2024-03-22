package com.wusui.chat

import com.wusui.database.DataBaseManager
import com.wusui.database.Message
import com.wusui.database.groups
import com.wusui.database.messages
import com.wusui.database.users
import com.wusui.utils.generateId
import io.ktor.server.websocket.*
import io.ktor.util.*
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import java.util.concurrent.ConcurrentHashMap

object ChatManager {

	val onlineMembers = ConcurrentHashMap<String, DefaultWebSocketServerSession>()


	//检查发送对象id是否为空
	private fun checkToId(toId: String?): Boolean {
		if (toId.isNullOrEmpty()) return false
		if (!toId.lowercase().all { it in '0'..'9' || it in 'a'..'z' }) return false
		val user = DataBaseManager.db.users.find { it.id eq toId }
		val group = DataBaseManager.db.groups.find { it.groupId eq toId }
		return user != null || group != null
	}


	//存储消息，返回消息Id
	fun saveMessage(message: com.wusui.data.Message): String? {
		if (!checkToId(message.toId) || !checkToId(message.fromId)) return null
		val msg = Message {
			fromId = message.fromId
			if (message.type == "group") {
				toGroupId = message.toId
			} else {
				toId = message.toId
			}
			messageId = generateId()
			messageType = message.type
			content = message.content
			sendTime = message.sendTime ?: System.currentTimeMillis()
		}
		DataBaseManager.db.messages.add(msg)
		return msg.messageId
	}

}