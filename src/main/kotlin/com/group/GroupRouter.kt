package com.group

import com.ext.createGroup
import com.ext.joinedGroup
import com.ext.ownerGroup
import com.ext.searchGroup
import io.ktor.server.routing.*

fun Routing.group() {
	route("/group") {
		createGroup()
		ownerGroup()
		joinedGroup()
		searchGroup()
	}
}