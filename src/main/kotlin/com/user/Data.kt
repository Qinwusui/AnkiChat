package com.user

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
	val ownerId: String
)