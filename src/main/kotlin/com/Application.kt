package com

import com.group.group
import com.user.user
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

fun main() {
	embeddedServer(Netty, environment = applicationEngineEnvironment {
		log = LoggerFactory.getLogger("ktor-server")
		module {
			module()
			routing {
				user()
				group()
			}
		}
		connector {
			port = 2341
			host = "0.0.0.0"
		}
	}).start(wait = true)
}

fun Application.module() {
	install(WebSockets) {
		maxFrameSize = Long.MAX_VALUE
		masking = false
		contentConverter = KotlinxWebsocketSerializationConverter(
			Json {
				ignoreUnknownKeys = true
				prettyPrint = true
			}
		)
	}
	install(Compression) {
		gzip {
			matchContentType(ContentType.Video.Any)
		}
	}
//    install(PartialContent)
//    loadPlugin()
}