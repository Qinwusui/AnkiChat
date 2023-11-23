package com.chat

import com.data.UserSession
import com.utils.successOut
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope


fun Routing.chat() {

	webSocket("/chat") {

		val userSession = call.sessions.get<UserSession>()
		if (userSession == null) {
			sendSerialized(mapOf<String, Any>("code" to -1, "msg" to "身份验证失败"))
			close()
			return@webSocket
		}
		userSession.online(this)
		try {
			while (true) {
				runCatching {
					receiveDeserialized<com.data.Message>()
				}.onSuccess {
					processMsg(userSession, it)

				}.onFailure {

				}

			}
		} catch (e: Exception) {
			e.successOut()
		} finally {
			"已结束".successOut()
			userSession.offline(this)
		}
	}

}

fun CoroutineScope.refuseRequest() {

}