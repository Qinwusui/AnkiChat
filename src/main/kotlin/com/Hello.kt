package com

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.helloworld()=get {
	call.respond("HelloWorld")
}