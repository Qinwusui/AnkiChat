package com

import com.chat.chat
import com.config.JacksonConfig.config
import com.data.UserSession
import com.fasterxml.jackson.databind.ObjectMapper
import com.friends.friends
import com.group.group
import com.message.message
import com.user.user
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.util.*
import org.slf4j.LoggerFactory
import java.io.File
import java.time.Duration

fun main() {
//	TestT()
//	return
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

//		sslConnector(
//			KeyStore.getInstance(File("./k.jks"), "Qinsansui233...".toCharArray()),
//			keyAlias = "wusui",
//			keyStorePassword = { "Qinsansui233...".toCharArray() },
//			privateKeyPassword = { "Qinsansui233...".toCharArray() })
//		{
//			port = 443
//			host = "0.0.0.0"
//		}
	}, configure = {
		configureBootstrap = {

		}
		shutdownTimeout = 3000
		responseWriteTimeoutSeconds = 10
		requestQueueLimit = 20
	}).start(wait = true)
}

inline fun <reified T : Any> generateSerializer(
	localDatePattern: String, localTimePattern: String, localDateTimePattern: String
): SessionSerializer<T> = object : SessionSerializer<T> {
	private val om = ObjectMapper().config(localDatePattern, localTimePattern, localDateTimePattern)
	override fun deserialize(text: String): T = om.readValue(text, T::class.java)
	override fun serialize(session: T): String = om.writeValueAsString(session)
}

fun Application.module() {
	install(WebSockets) {
		maxFrameSize = Long.MAX_VALUE
		masking = false

		pingPeriod = Duration.ofSeconds(15)
		timeout = Duration.ofSeconds(15)

		contentConverter = JacksonWebsocketContentConverter()

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
		jackson {
			config("yyyy-MM-dd", "hh:mm:ss", "yyyy-MM-dd hh:mm:ss")
		}
//		json(Json {
//			prettyPrint = true
//			ignoreUnknownKeys = true
//		})
//		gson {
//			setPrettyPrinting()
//			disableHtmlEscaping()
//		}
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
	install(CORS) {
		allowMethod(HttpMethod.Get)
		allowMethod(HttpMethod.Post)
		allowMethod(HttpMethod.Put)
		allowMethod(HttpMethod.Patch)
		allowMethod(HttpMethod.Delete)
		allowMethod(HttpMethod.Head)
		allowMethod(HttpMethod.Options)
		allowHeader(HttpHeaders.Authorization)
		anyHost()
		allowCredentials = true
		allowNonSimpleContentTypes = true
		maxAgeInSeconds = 1000L * 60 * 60 * 24
	}

	routing {
		t()
		user()
		group()
		chat()
		message()
		friends()
	}
//    install(PartialContent)
//    loadPlugin()
}