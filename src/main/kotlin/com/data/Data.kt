package com.data

import kotlinx.serialization.Serializable
import org.ktorm.logging.LogLevel

data class DataBaseConfig(
	val driverClassName: String = "com.mysql.jdbc.Driver",    // 驱动的类名
	val url: String = "jdbc:mysql://localhost:2342/chat",                // jdbc url
	val username: String = "wusui",           // 用户名
	val password: String = "Qinsansui233...",           // 密码
	val initialSize: Int = 10,      // 默认连接数
	val maxActive: Int = 25,        // 最大连接数
	val maxWait: Long = 3000,       // 最大等待时间
	val logLevel: LogLevel = LogLevel.DEBUG // 输出的日志级别
)

data class Results<T>(val code: Int, val msg: String, val data: T?=null) {
	companion object {
		fun success(): Results<*> = Results(200, "操作成功", null)
		fun success(msg: String) = Results(200, msg, null)
		fun <T> success(data: T) = Results(200, "操作成功", data)
		fun <T> success(msg: String, data: T) = Results(200, msg, data)
		fun failure() = Results(500, "操作失败", null)
		fun failure(msg: String) = Results(500, msg, null)
		fun <T> failure(msg: String, data: T) = Results(500, msg, data)
	}
}

@Serializable
data class UserRegisterReqData(
	val userId: String,
	val pwd: String,
)

@Serializable
data class UserRespData(
	val userId: String = "",
	val token: String = "",
	val msg: String = "",
	val success: Boolean,
	val isRegister: Boolean = false,
)

@Serializable
data class UserSession(
	val userId: String,
	val token: String,
)


@Serializable
data class GroupReqData(
	val groupName: String,
	val creatorId: String,
	val ownerId: String,
)

@Serializable
data class GroupResData(
	val groupId: String = "",
	val success: Boolean = false,
	val msg: String = ""
)

@Serializable
data class Message<T>(
	var sendId: String,//发送者ID
	val toId: String,//接收者ID
	val data: T,//消息内容
	val type: String,//消息类型
)

@Serializable
data class MessageList<T>(
	val success: Boolean = false,
	val msg: String = "",
	var messages: List<Message<T>>
)

@Serializable
data class User(
	val userName: String,
	val userId: String,
	val validate: Int,
	val registerTime: Long,
	val onlineTime: Long
)