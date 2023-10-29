package com.message

import com.chat.ChatManager
import com.data.Message
import com.data.MessageList
import com.database.DataBaseManager

object MessageController {
	suspend fun findMessages(id: String, type: String): MessageList {
		var messageList = MessageList(listOf())
		if (!ChatManager.checkToId(id)) return messageList

		when (type) {
			"group" -> {
				messageList = findGroupMessages(id)
			}

			"private" -> {

			}
		}

		return messageList
	}

	suspend fun findGroupMessages(id: String): MessageList {
		val messageList = suspend {
			val messageList = MessageList(listOf())
			DataBaseManager.useStatement {
				val set = executeQuery(
					"""
				select * from messages where type=group and to_id='$id' 
			""".trimIndent()
				)
				val list = mutableListOf<Message>()
				while (set.next()) {
					val blob = set.getBlob("content")
					val content = blob.binaryStream.readAllBytes().decodeToString()
					println(content)
					list.add(
						Message(
							sendId = set.getString("send_id"),
							toId = set.getString("to_id"),
							type = set.getString("type"),
							data = content
						)
					)
				}
			}
			messageList
		}()
		return messageList
	}
}