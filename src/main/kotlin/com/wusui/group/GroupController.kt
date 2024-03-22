package com.wusui.group

import com.wusui.data.GroupReqData
import com.wusui.data.Results
import com.wusui.database.DataBaseManager
import com.wusui.database.Group
import com.wusui.database.GroupAdmin
import com.wusui.database.GroupMember
import com.wusui.database.User
import com.wusui.database.groupAdmins
import com.wusui.database.groupMembers
import com.wusui.database.groups
import com.wusui.user.UserController
import com.wusui.utils.generateId
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.isNotEmpty
import org.ktorm.entity.map

object GroupController {
	//验证群聊
	fun validateCreateGroupInfo(groupReqData: GroupReqData): Results<*> {
		if (groupReqData.groupName.isEmpty()) {
			return Results.failure("创建失败，群名称不能为空")
		}
		if (groupReqData.creatorId.isEmpty()) {
			return Results.failure("创建失败，创建者ID不能为空")
		}
		if (groupReqData.ownerId.isEmpty()) {
			return Results.failure("创建失败，群主ID不能为空")
		}
		return createGroup(groupReqData)
	}

	//创建群聊
	private fun createGroup(groupReqData: GroupReqData): Results<*> {
		if (!UserController.userExist(groupReqData.ownerId)) return Results.failure("用户不存在")
		var id = generateId()
		var notEmpty = DataBaseManager.db.groups.filter { it.groupId eq id }.isNotEmpty()
		while (notEmpty) {
			id = generateId()
			notEmpty = DataBaseManager.db.groups.filter { it.groupId eq id }.isNotEmpty()
		}
		DataBaseManager.db.groups.add(Group {
			groupId = id
			creatorId = groupReqData.creatorId
			ownerId = groupReqData.ownerId
			groupName = groupReqData.groupName
			createTime = System.currentTimeMillis()
		})
		DataBaseManager.db.groupAdmins.add(GroupAdmin {
			groupId = id
			userId = groupReqData.creatorId
		})
		DataBaseManager.db.groupMembers.add(GroupMember {
			groupId = id
			userId = groupReqData.creatorId
		})
		return Results.success(msg = "创建群聊成功", data = id)

	}

	//获取用户id所拥有的群聊
	fun getUserOwnerGroup(userId: String): Results<*> {
		if (!UserController.userExist(userId)) return Results.failure(msg = "没有这个用户")
		val group = DataBaseManager.db.groups.filter { it.ownerId eq userId }.map {
			mapOf(
				"groupId" to it.groupId,
				"groupName" to it.groupName
			)
		}
		return Results.success(msg = "获取群组成功", data = group)

	}

	//获取用户id所加入的群聊
	fun getJoinedGroup(userId: String): Results<*> {
		if (!UserController.userExist(userId)) return Results.failure(msg = "没有这个用户")
		val groupIds =
			DataBaseManager.db.groupMembers
				.filter { it.userId eq userId }
				.map { it.group }
				.map {
					mapOf(
						"groupId" to it.groupId,
						"groupName" to it.groupName,
					)
				}

		return Results.success(msg = "获取群组成功", data = groupIds)


	}

	//检查id对应的群聊是否存在
	private fun groupExist(groupId: String): Boolean {
		return DataBaseManager.db.groups.filter { it.groupId eq groupId }.isNotEmpty()
	}

	//通过id查找群聊信息
	fun requestGroupInfo(groupId: String): Results<*> {
		if (!groupExist(groupId)) return Results.failure("群号不存在")
		val group = DataBaseManager.db.groups.find { it.groupId eq groupId }
		return if (group != null) {
			Results.success(
				mapOf(
					"groupId" to group.groupId,
					"groupName" to group.groupName,
					"creatorId" to group.creatorId,
					"ownerId" to group.creatorId,
					"createTime" to group.createTime
				)
			)
		} else {
			Results.failure("群组不存在")
		}
	}

	//查找某个群的所有群成员
	fun findAllUsersByGroupId(groupId: String): Results<List<User>> {
		if (!groupExist(groupId)) return Results.success(emptyList())
		val users = DataBaseManager.db.groupMembers.filter { it.groupId eq groupId }.map { it.user }
		return Results.success(users)
	}
}



















