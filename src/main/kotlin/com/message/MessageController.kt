package com.message

import com.chat.ChatManager
import com.data.MessageList
import com.data.Results
import com.database.DataBaseManager
import com.database.messages
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.filter
import org.ktorm.entity.map

object MessageController {
	suspend fun findMessages(id: String, type: String): Results<*> {
		if (!ChatManager.checkToId(id)) return Results.failure("id不存在")

		val result = when (type) {
			"group" -> {
				Results.success(data = findGroupMessages(id))
			}

			"private" -> {
				Results.success(data = findPrivateMessages(id))
			}

			else -> Results.failure("消息类型不存在")
		}

		return result
	}

	suspend fun findGroupMessages(id: String): MessageList {
		val messageList = suspend {
			val messages =
				DataBaseManager.db.messages.filter { (it.messageType eq "group") and (it.toId eq id) }.map { it }
			MessageList(success = true, messages = messages)
		}()
		return messageList
	}

	suspend fun findPrivateMessages(id: String): MessageList {
		return suspend {
			MessageList(
				success = true,
				messages = DataBaseManager.db.messages
					.filter { (it.toId eq id) and (it.messageType eq "private") }
					.map { it })
		}()
	}
}