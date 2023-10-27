package com.data

import kotlinx.serialization.Serializable

@Serializable
data class UserRegisterReqData(
	val userName: String,
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
	val userName: String,
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
data class GroupListResData(
	val success: Boolean,
	val list: List<Group> = listOf(),
	val msg: String
)


@Serializable
data class Group(
	val groupId: String,
	val groupName: String,
	val ownerId: String,
	val creatorId: String
)

@Serializable
data class Message<out T>(
	val toId: String?,
	val type: String?,
	val data: T?
)

@Serializable
data class User(
	val userName: String,
	val userId: String,
	val validate: Int,
	val registerTime: Long,
	val onlineTime: Long
)