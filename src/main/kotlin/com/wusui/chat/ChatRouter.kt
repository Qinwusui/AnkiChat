package com.wusui.chat

import com.wusui.data.Message
import com.wusui.data.UserSession
import com.wusui.utils.successOut
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

inline var ApplicationCall.userSession: UserSession?
	get() = try {
		sessions.get()
	} catch (e: Exception) {
		null
	}
	set(value) = sessions.set(value)

fun Routing.chat() {

	webSocket("/chat") {
		val userSession = call.userSession
		if (userSession == null) {
			call.sessions.successOut()
			sendSerialized(mapOf<String, Any>("code" to -1, "msg" to "身份验证失败"))
			close()
			return@webSocket
		}
		userSession.online(this)
		try {
			incoming.consumeEach {
				val message = converter?.deserialize<Message>(it)
				if (message != null) {
					launch {
						processMsg(userSession, message)
					}

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