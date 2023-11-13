package com.chat

import com.database.DataBaseManager
import com.database.Message
import com.database.messages
import io.ktor.server.websocket.*
import org.ktorm.entity.add
import java.util.concurrent.ConcurrentHashMap

object ChatManager {

	val onlineMembers = ConcurrentHashMap<String, DefaultWebSocketServerSession>()


	//检查发送对象id是否为空
	fun checkToId(toId: String?): Boolean {
		if (toId.isNullOrEmpty()) return false
		if (!toId.lowercase().all { it in '0'..'9' || it in 'a'..'z' }) return false
		var sum = 0
//		DataBaseManager.useStatement(isSelect = true) {
//			val set = executeQuery(
//				"""
//					select sum(c) from (select count() as c
//					from user
//					where user_id = '$toId'
//					union all
//					select count() as c
//					from groups
//					where group_id = '$toId')
//			""".trimIndent()
//			)
//			sum = set.getInt("sum(c)")
//		}
		return sum == 1
	}

	//存储消息
	fun saveMessage(message: Message): Boolean {
		if (!checkToId(message.toId) || !checkToId(message.toId) || !checkToId(message.fromId)) return false
		DataBaseManager.db.messages.add(message)
		return true
	}
}