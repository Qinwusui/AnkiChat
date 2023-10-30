package com.message

import com.ext.searchChatMessage
import io.ktor.server.routing.*


fun Routing.message() {
	route("/message") {
		searchChatMessage()

	}
}