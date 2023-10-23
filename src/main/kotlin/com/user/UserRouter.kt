package com.user

import com.routers.getFriends
import com.routers.login
import com.routers.register
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import kotlinx.serialization.json.Json

fun Routing.user() {
	install(ContentNegotiation) {
		json(Json {
			prettyPrint = true
			ignoreUnknownKeys = true
		})
	}
	install(Sessions) {
		val secretSignKey = hex("233123123412acd")
		cookie<UserSession>("session") {
			cookie.path = "/"
			cookie.maxAgeInSeconds = 10 * 60
			transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
		}
	}

	route("/user") {
		//POST 注册
		register()
		//POST 登录
		login()
		//获取好友列表
		getFriends()
	}
}