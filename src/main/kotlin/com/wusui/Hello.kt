package com.wusui

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.t() = get("/t") {
	call.respond("HelloWorld")
}