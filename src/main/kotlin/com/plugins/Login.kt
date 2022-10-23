package com.plugins

import com.datas.ResInfo
import com.datas.SignInfo
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.login() {
    install(ContentNegotiation) {
        gson {}
    }
    routing {
        route("/user") {
            //注册接口
            post("/sign") {
                val signInfo = call.receive<SignInfo>()
                ChatUserManagement.insertUser(signInfo.content).collect {
                    if (it) {
                        call.respond(
                            ResInfo.SignSuccess
                        )
                    } else {
                        call.respond(ResInfo.SignFailure)
                    }
                }
            }
            //登录接口
            post("/login") {
                val signInfo = call.receive<SignInfo>()
                println(signInfo)
                ChatUserManagement.userLogin(signInfo.content).collect {
                    if (it) {
                        call.respond(ResInfo.LoginSuccess)
                    } else {
                        call.respond(ResInfo.LoginFailure)
                    }

                }
            }
            post("/requestQQ") {
                val loginUser = call.receive<ChatUserManagement.LoginUser>()
                ChatUserManagement.getUserQQ(loginUser).collect {
                    call.respond(SignInfo(it))
                }
            }
            //修改密码
            post("/mod") {
                val signInfo = call.receive<SignInfo>()
                ChatUserManagement.updatePwd(signInfo.content).collect {
                    if (it) {
                        call.respond(ResInfo.ModSuccess)
                    } else {
                        call.respond(ResInfo.ModFailure)
                    }
                }
            }
        }
    }
}