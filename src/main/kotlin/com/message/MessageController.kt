package com.message

import com.data.Message
import com.data.Results
import com.database.DataBaseManager
import com.database.messages
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.map
import org.ktorm.entity.sortedBy
import org.ktorm.entity.sortedByDescending
import org.ktorm.entity.toMutableList

object MessageController {
	//查找消息
	//id:群组id或用户id
	suspend fun findMessages(id: String, type: String): Results<*> {

		return when (type) {
			"group" -> {
				findGroupMessages(id)
			}

			"private" -> {
				findPrivateMessages(id)
			}

			else -> Results.failure("消息类型不存在")
		}
	}

	//查找群组所有消息
	private suspend fun findGroupMessages(groupId: String): Results<*> {
		return suspend {
			val messages =
				DataBaseManager.db.messages
					.filter { (it.messageType eq "group") and (it.toGroupId eq groupId) }
					.sortedBy { it.sendTime }
					.map {
						Message(
							fromId = it.fromId,
							toId = it.toId ?: it.toGroupId ?: "",
							data = it.content,
							sendTime = it.sendTime,
							messageId = it.messageId,
							type = it.messageType,
							sendName = it.fromUser.userName,
							toName = it.toUser?.userName ?: it.toGroup?.groupName ?: ""
						)
					}
			Results.success(messages)
		}()
	}

	//查找id对应的私聊消息
	private suspend fun findPrivateMessages(userId: String): Results<*> {
		return suspend {
			Results.success(DataBaseManager.db.messages
				.filter { (it.toId eq userId) and (it.messageType eq "private") }
				.map {
					Message(
						fromId = it.fromId,
						toId = it.toId ?: it.toGroupId ?: userId,
						data = it.content,
						sendTime = it.sendTime,
						messageId = it.messageId,
						type = it.messageType,
						sendName = it.toUser?.userName ?: it.toId ?: "",
						toName = it.toUser?.userName ?: it.toGroup?.groupName ?: ""
					)
				})
		}()
	}

	//通过messageId获取单条消息
	suspend fun findMessage(messageId: String): Results<*> {
		return suspend {
			val res = DataBaseManager.db.messages.find { it.messageId eq messageId }
			if (res != null) {
				val message = Message(
					fromId = res.fromId,
					messageId = res.messageId,
					type = res.messageType,
					sendTime = res.sendTime,
					data = res.content,
					toId = res.toId ?: res.toGroupId ?: "",
					sendName = res.toUser?.userName ?: res.toId ?: "",
					toName = res.toUser?.userName ?: res.toGroup?.groupName ?: ""
				)

				Results.success(message)
			} else {
				Results.failure("消息不存在")
			}
		}()
	}

	//获取某个用户的最近聊天记录
	suspend fun findUsersRecentMessageRecord(userId: String): Results<*> {
		return suspend {
			val res = DataBaseManager.db.messages.filter { it.fromId eq userId }
				.sortedByDescending { it.sendTime }
				.toMutableList()
				.distinctBy {
					it.toId ?: it.toGroupId
				}
				.map {
					Message(
						fromId = it.fromId,
						toId = it.toId ?: it.toGroupId ?: "",
						sendName = it.fromUser.userName,
						toName = it.toUser?.userName ?: it.toGroup?.groupName ?: "",
						data = it.content,
						type = it.messageType,
						sendTime = it.sendTime,
						messageId = it.messageId
					)
				}
			Results.success(res)
		}()
	}
}