package com.chat

import com.database.DataBaseManager
import com.data.UserSession
import io.ktor.server.websocket.*
import java.util.concurrent.ConcurrentHashMap

object ChatManager {

	val onlineMembers = ConcurrentHashMap<String, DefaultWebSocketServerSession>()

	//群ID，用户集合
	val groupMembers = ConcurrentHashMap<String, Set<UserSession>>()

	//检查发送对象id是否为空
	fun checkToId(toId: String?): Boolean {
		if (toId == null) return false
		if (toId.isEmpty()) return false
		if (!toId.lowercase().all { it in '0'..'9' || it in 'a'..'z' }) return false
		var sum = 0
		DataBaseManager.useStatement(isSelect = true) {
			val set = executeQuery(
				"""
				select count() from user where user_id ='$toId' and 1=1
			""".trimIndent()
			)
			sum = set.getInt("count()")
		}
		return sum == 1
	}

}