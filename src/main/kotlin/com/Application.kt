package com

import com.plugins.configureSockets
import com.plugins.login
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import org.slf4j.LoggerFactory

fun String.println() = println(this)

fun main() {
    embeddedServer(Netty, environment = applicationEngineEnvironment {
        log = LoggerFactory.getLogger("ktor.application")
//        watchPaths = listOf("classes")
//        developmentMode=true
        module {
            login()
            configureSockets()
        }
        connector {
            port = 54322
            host = "0.0.0.0"
        }
    }).start(wait = true)
}


