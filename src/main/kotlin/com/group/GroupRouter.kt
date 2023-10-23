package com.group

import com.routers.createGroup
import com.routers.groupList
import io.ktor.server.routing.*

fun Routing.group() {
	route("/group") {
		createGroup()
		groupList()
	}
}