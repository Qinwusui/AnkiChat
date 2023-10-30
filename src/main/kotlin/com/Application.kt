package com

import com.chat.chat
import com.google.gson.GsonBuilder
import com.group.group
import com.data.UserSession
import com.message.message
import com.user.user
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.File
import java.security.KeyStore
import java.time.Duration

fun main() {
//	val keystoreFile=File("./k.jks")
//	val key= buildKeyStore {
//		certificate("wusui"){
//			password="Qinsansui233..."
//			domains= listOf("127.0.0.1","localhost","0.0.0.0")
//
//		}
//	}
//	key.saveToFile(keystoreFile,"Qinsansui233...")

	embeddedServer(Netty, environment = applicationEngineEnvironment {
		log = LoggerFactory.getLogger("ktor")

		watchPaths = listOf("classes")
		module {
			module()
		}
		connector {
			port = 2341
			host = "0.0.0.0"
		}
		sslConnector(
			KeyStore.getInstance(File("./k.jks"), "Qinsansui233...".toCharArray()),
			keyAlias = "wusui",
			keyStorePassword = { "Qinsansui233...".toCharArray() },
			privateKeyPassword = { "Qinsansui233...".toCharArray() }) {
			port = 443
			host = "0.0.0.0"
		}
	}, configure = {

	}).start(wait = true)
}

fun Application.module() {
	install(WebSockets) {
		maxFrameSize = Long.MAX_VALUE
		masking = false

		pingPeriod = Duration.ofSeconds(15)
		timeout = Duration.ofSeconds(15)

		contentConverter = GsonWebsocketContentConverter(
			gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
		)
	}
	install(Compression) {
		gzip {
			matchContentType(ContentType.Video.Any)
		}
	}
	install(ContentNegotiation) {
//		json(Json {
//			prettyPrint = true
//			ignoreUnknownKeys = false
//		})
		gson {
			setPrettyPrinting()
			disableHtmlEscaping()
		}
	}
	install(Sessions) {

		val secretSignKey = hex("6819b57a326945c1968f45236589")
//		cookie<UserSession>("user", directorySessionStorage(File("build/.sessions"))) {
//			cookie.maxAgeInSeconds = 60 * 60 * 24
//			cookie.httpOnly = false
//			transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
//		}
		header<UserSession>("Session", directorySessionStorage(File("build/.headsessions"))) {
			transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
		}
	}
	routing {
		user()
		group()
		chat()
		message()
	}
//    install(PartialContent)
//    loadPlugin()
}