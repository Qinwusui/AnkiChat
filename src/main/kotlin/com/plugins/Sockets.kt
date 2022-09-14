package com.plugins

import com.google.gson.Gson
import com.println
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.ktor.websocket.serialization.*
import kotlinx.coroutines.channels.consumeEach
import java.util.*

data class OutGoingData(
    val nickName: String,
    val msg: String,
    val chatRoomId: String
)

data class IncomingData(
    val nickName: String,
    val msg: String,
    val chatRoomId: String
)

data class UserSession(
    val uuid: String,
    val nickName: String,
    val chatRoomId: String,
)

class Conn(val session: DefaultWebSocketSession, val userSession: UserSession)

/**
 * @author wusui
 *
 * WebSocket 聊天室
 */
fun Application.configureSockets() {
    install(WebSockets) {
//        pingPeriod = Duration.ofSeconds(15)
//        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = GsonWebsocketContentConverter()
//        timeoutMillis = 10

    }
    install(Sessions) {
        cookie<UserSession>("chat") {
            cookie.path = "/"
        }
    }
    routing {

        route("/anki") {
//            static {
//                get {
//                    call.respondRedirect("/chat")
//                }
//            }
            val conns = Collections.synchronizedSet<Conn>(LinkedHashSet())
            webSocket("/chat") {
                val params = call.parameters
                val uuid = params["uuid"] ?: UUID.randomUUID().toString()
                val nickName = params["nickName"] ?: return@webSocket
                val chatRoomId = params["chatRoomId"] ?: return@webSocket
                val conn = Conn(this, UserSession(uuid, nickName, chatRoomId))
                conns.add(conn)
                try {
                    incoming.consumeEach { frame ->
                        val gson = Gson()
                        val b = gson.fromJson(frame.readBytes().decodeToString(), IncomingData::class.java)
                        conns.filter {
                            it.userSession.chatRoomId == conn.userSession.chatRoomId
                        }.forEach {
                            it.session.sendSerializedBase(
                                OutGoingData(
                                    conn.userSession.nickName,
                                    b.msg,
                                    chatRoomId
                                ),
                                GsonWebsocketContentConverter(),
                                Charsets.UTF_8
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.message?.println()
                } finally {
                    conns.remove(conn)
                    close()
                    println(conns.size)
                }

            }
        }
    }
}
