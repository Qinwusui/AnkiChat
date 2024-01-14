package com.wusui.message

import com.wusui.ext.findUsersRecentMessageRecord
import com.wusui.ext.messageInfo
import com.wusui.ext.searchChatMessage
import io.ktor.server.routing.*


fun Routing.message() {
	route("/message") {
		searchChatMessage()
		messageInfo()
		findUsersRecentMessageRecord()
	}
}