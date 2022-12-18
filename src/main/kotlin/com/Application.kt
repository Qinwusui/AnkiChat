package com

import com.plugins.loadPlugin
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.websocket.*
import org.slf4j.LoggerFactory

fun main() {
    embeddedServer(Netty, environment = applicationEngineEnvironment {
        log = LoggerFactory.getLogger("ktor-server")
        module {
            module()
        }
        connector {
            port = 54322
            host = "0.0.0.0"
        }
    }).start(wait = true)
}

fun Application.module() {
    install(WebSockets) {
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = GsonWebsocketContentConverter()
    }
    install(Compression) {
        gzip {
            matchContentType(ContentType.Video.Any)
        }
    }
//    install(PartialContent)
    loadPlugin()
}