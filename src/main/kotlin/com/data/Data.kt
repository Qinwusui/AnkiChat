package com.data

import kotlinx.serialization.Serializable
import org.ktorm.logging.LogLevel

data class DataBaseConfig(
	val driverClassName: String = "com.mysql.cj.jdbc.Driver",    // 驱动的类名
	val url: String = "jdbc:mysql://127.0.0.1:2342/chat",                // jdbc url
	val username: String = "root",           // 用户名
	val password: String = "Qinsansui233...",           // 密码
	val initialSize: Int = 10,      // 默认连接数
	val maxActive: Int = 25,        // 最大连接数
	val maxWait: Long = 3000,       // 最大等待时间
	val logLevel: LogLevel = LogLevel.DEBUG // 输出的日志级别
)

@Serializable
data class Results<T>(val code: Int, val msg: String, val data: T? = null) {
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
	val userName: String,
	val pwd: String,
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
data class GroupInfo(
	val groupName: String,
	val groupId: String,
	val creatorId: String,
	val ownerId: String
)

@Serializable
data class ApplyData(
	val sendId: String,
	val receiveId: String,
	val applyMessage: String
)

@Serializable
data class Message(
	val fromId: String,
	val toId: String,
	var sendName:String,
	val toName:String,
	val data: String,
	val type: String,
	var sendTime: Long?,
	var messageId: String?,
)

