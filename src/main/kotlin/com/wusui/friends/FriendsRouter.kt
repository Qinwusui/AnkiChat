package com.wusui.friends

import com.wusui.ext.agreeFriendApply
import com.wusui.ext.getAllApplies
import com.wusui.ext.refuseFriendApply
import com.wusui.ext.sendFriendApply
import io.ktor.server.routing.*

fun Routing.friends() = route("/friends") {
	getAllApplies()
	route("/apply") {
		sendFriendApply()
		agreeFriendApply()
		refuseFriendApply()
	}

}