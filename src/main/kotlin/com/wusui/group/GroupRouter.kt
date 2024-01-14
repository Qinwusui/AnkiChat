package com.wusui.group

import com.wusui.ext.createGroup
import com.wusui.ext.joinedGroup
import com.wusui.ext.ownerGroup
import com.wusui.ext.searchGroup
import io.ktor.server.routing.*

fun Routing.group() {
	route("/group") {
		createGroup()
		ownerGroup()
		joinedGroup()
		searchGroup()
	}
}