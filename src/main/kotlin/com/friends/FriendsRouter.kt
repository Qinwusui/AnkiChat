package com.friends

import com.ext.agreeFriendApply
import com.ext.refuseFriendApply
import com.ext.getAllApplies
import com.ext.sendFriendApply
import io.ktor.server.routing.*

fun Routing.friends() = route("/friends") {
	getAllApplies()
	route("/apply") {
		sendFriendApply()
		agreeFriendApply()
		refuseFriendApply()
	}

}