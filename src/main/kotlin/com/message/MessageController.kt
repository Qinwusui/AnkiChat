package com.message

import com.data.Results
import com.database.DataBaseManager
import com.database.messages
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.filter
import org.ktorm.entity.map
import org.ktorm.entity.take

object MessageController {
	suspend fun findMessages(id: String, type: String, limit: Int): Results<*> {

		val result = when (type) {
			"group" -> {
				findGroupMessages(id, limit)
			}

			"private" -> {
				findPrivateMessages(id, limit)
			}

			else -> Results.failure("消息类型不存在")
		}

		return result
	}

	suspend fun findGroupMessages(id: String, limit: Int): Results<*> {
		val messageList = suspend {
			val messages =
				DataBaseManager.db.messages
					.filter { (it.messageType eq "group") and (it.toId eq id) }
					.take(limit)
					.map {
						mapOf(
							"messageId" to it.messageId,
						)
					}
			Results.success(messages)
		}()
		return messageList
	}

	suspend fun findPrivateMessages(id: String, limit: Int): Results<*> {
		return suspend {
			Results.success(DataBaseManager.db.messages
				.filter { (it.toId eq id) and (it.messageType eq "private") }
				.take(limit)
				.map {
					mapOf(
						"messageId" to it.messageId,
					)
				})
		}()
	}
}