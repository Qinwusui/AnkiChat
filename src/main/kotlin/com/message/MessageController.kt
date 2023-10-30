package com.message

import com.chat.ChatManager
import com.data.Message
import com.data.MessageList
import com.database.DataBaseManager

object MessageController {
	suspend fun findMessages(id: String, type: String): MessageList<Any> {
		var messageList = MessageList<Any>(messages = listOf())
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

	suspend fun findGroupMessages(id: String): MessageList<Any> {
		val messageList = suspend {
			var messageList: MessageList<Any> = MessageList(messages = listOf())
			DataBaseManager.useStatement {
				val set = executeQuery(
					"""
				select * from messages where type='group' and to_id='$id' 
			""".trimIndent()
				)
				val list = mutableListOf<Message<Any>>()
				while (set.next()) {
					val blob = set.getString("content")
					list.add(
						Message(
							sendId = set.getString("send_id"),
							toId = set.getString("to_id"),
							type = set.getString("type"),
							data = blob
						)
					)
				}

				messageList = MessageList(success = true, messages = list)
			}
			messageList
		}()
		return messageList
	}
}