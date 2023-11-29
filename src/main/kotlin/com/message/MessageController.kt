package com.message

import com.data.Results
import com.database.DataBaseManager
import com.database.messages
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.map
import org.ktorm.entity.sortedBy
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
					.filter { (it.messageType eq "group") and (it.toGroupId eq id) }
					.take(limit)
					.sortedBy { it.sendTime }
					.map {
						mapOf(
							"messageId" to it.messageId,
							"content" to it.content,
							"sendName" to it.fromUser.userName,
							"sendId" to it.fromUser.userId,
						)
					}
			Results.success(messages)
		}()
		return messageList
	}

	suspend fun findPrivateMessages(id: String, limit: Int): Results<*> {
		return suspend {
			Results.success(DataBaseManager.db.messages
				.filter { (it.toUserId eq id) and (it.messageType eq "private") }
				.take(limit)
				.map {
					mapOf(
						"messageId" to it.messageId,
					)
				})
		}()
	}

	suspend fun findMessage(id: String): Results<*> {
		return suspend {
			val res = DataBaseManager.db.messages.find { it.messageId eq id }
			if (res != null) {
				val map = mutableMapOf(
					"sendId" to res.fromId,
					"messageId" to res.messageId,
					"messageType" to res.messageType,
					"sendTime" to res.sendTime,
					"content" to res.content
				)
				if (res.toId != null) {
					map["toUserId"] = res.toId!!
				}
				if (res.toGroupId != null) {
					map["toGroupId"] = res.toGroupId!!
				}
				Results.success(map)
			} else {
				Results.failure("消息不存在")
			}
		}()
	}
}