package com.chat

import com.data.Message
import com.database.DataBaseManager
import com.utils.generateId
import com.utils.gson
import io.ktor.server.websocket.*
import java.util.concurrent.ConcurrentHashMap

object ChatManager {

	val onlineMembers = ConcurrentHashMap<String, DefaultWebSocketServerSession>()


	//检查发送对象id是否为空
	fun checkToId(toId: String?): Boolean {
		if (toId.isNullOrEmpty()) return false
		if (!toId.lowercase().all { it in '0'..'9' || it in 'a'..'z' }) return false
		var sum = 0
		DataBaseManager.useStatement(isSelect = true) {
			val set = executeQuery(
				"""
					select sum(c) from (select count() as c
					from user
					where user_id = '$toId'
					union all
					select count() as c
					from groups
					where group_id = '$toId')
			""".trimIndent()
			)
			sum = set.getInt("sum(c)")
		}
		return sum == 1
	}

	//存储消息
	fun <T>saveMessage(message: Message<T>): Boolean {
		if (!checkToId(message.toId)) return false
		val json = gson.toJson(message.data)
		val messageId = generateId()
		return DataBaseManager.usePreparedStatement(
			"""
				insert into messages (message_id,send_id,to_id,content,send_time,type)
				values (?,?,?,?,?,?)
			""".trimIndent()
		) {
			setString(1, messageId)
			setString(2, message.sendId)
			setString(3, message.toId)
			setString(4, json)
			setInt(5, System.currentTimeMillis().toInt())
			setString(6, message.type)
		}
	}
}