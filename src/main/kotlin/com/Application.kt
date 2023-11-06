package com

import com.chat.chat
import com.config.JacksonConfig.config
import com.google.gson.GsonBuilder
import com.group.group
import com.data.UserSession
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.message.message
import com.user.user
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.serialization.jackson.*
import io.ktor.serialization.kotlinx.json.*
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
		jackson {
			config("yyyy-MM-dd", "hh:mm:ss", "yyyy-MM-dd hh:mm:ss")
		}
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
			serializer = generateSerializer("yyyy-MM-dd", "hh:mm:ss", "yyyy-MM-dd hh:mm:ss")

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
		user()
		group()
		chat()
		message()
	}
//    install(PartialContent)
//    loadPlugin()
}