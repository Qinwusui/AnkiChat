package com.wusui

import com.fasterxml.jackson.databind.ObjectMapper
import com.wusui.chat.chat
import com.wusui.config.JacksonConfig.config
import com.wusui.data.UserSession
import com.wusui.friends.friends
import com.wusui.group.group
import com.wusui.message.message
import com.wusui.tools.GlobalJson
import com.wusui.user.user
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.util.*
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.File
import java.time.Duration

fun main(args: Array<String>) = EngineMain.main(args)
fun main() {
//	TestT()
//	return
//	embeddedServer(Netty, environment = applicationEngineEnvironment {
//		log = LoggerFactory.getLogger("ktor")
//
//		watchPaths = listOf("classes")
//		module {
//			module()
//		}
//		connector {
//			port = 2341
//			host = "0.0.0.0"
//		}
//
////		sslConnector(
////			KeyStore.getInstance(File("./k.jks"), "Qinsansui233...".toCharArray()),
////			keyAlias = "wusui",
////			keyStorePassword = { "Qinsansui233...".toCharArray() },
////			privateKeyPassword = { "Qinsansui233...".toCharArray() })
////		{
////			port = 443
////			host = "0.0.0.0"
////		}
//	}, configure = {
//		configureBootstrap = {
//
//		}
//		shutdownTimeout = 3000
//		responseWriteTimeoutSeconds = 10
//		requestQueueLimit = 20
//	}).start(wait = true)
}

inline fun <reified T : Any> generateSerializer(
	localDatePattern: String, localTimePattern: String, localDateTimePattern: String
): SessionSerializer<T> = object : SessionSerializer<T> {
	private val om = ObjectMapper().config(localDatePattern, localTimePattern, localDateTimePattern)
	override fun deserialize(text: String): T = om.readValue(text, T::class.java)
	override fun serialize(session: T): String = om.writeValueAsString(session)
}

@OptIn(ExperimentalSerializationApi::class)
fun Application.module() {
	install(WebSockets) {
		maxFrameSize = Long.MAX_VALUE
		masking = false

		pingPeriod = Duration.ofSeconds(15)
		timeout = Duration.ofSeconds(15)

		contentConverter = KotlinxWebsocketSerializationConverter(
			GlobalJson
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
		jackson {
			config("yyyy-MM-dd", "hh:mm:ss", "yyyy-MM-dd hh:mm:ss")
		}
		json(GlobalJson)
//		json(Json {
//			prettyPrint = true
//			ignoreUnknownKeys = true
//		})
//		gson {
//			setPrettyPrinting()
//			disableHtmlEscaping()
//		}
	}
	install(StatusPages) {
		exception<RequestValidationException> { call, cause ->
			call.respond(HttpStatusCode.BadRequest, cause.reasons.joinToString())
		}
//		status(HttpStatusCode.NotFound) { call, status ->
//			call.respondText(text = "啊偶，页面走丢了", status = status)
//		}
		status(HttpStatusCode.BadRequest) { applicationCall, httpStatusCode ->
			applicationCall.respondText(text = "错误的请求", status = httpStatusCode)
		}
		statusFile(HttpStatusCode.NotFound, filePattern = "404.html")
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