package com.group

import com.data.GroupReqData
import com.data.GroupResData
import com.database.DataBaseManager
import com.database.Group
import com.database.GroupAdmin
import com.database.GroupMember
import com.database.groupAdmins
import com.database.groupMembers
import com.database.groups
import com.user.UserController
import com.utils.generateId
import org.ktorm.dsl.eq
import org.ktorm.dsl.inList
import org.ktorm.entity.add
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.forEach
import org.ktorm.entity.isNotEmpty
import org.ktorm.entity.map

object GroupController {
	//验证群聊
	fun validateCreateGroupInfo(groupReqData: GroupReqData): GroupResData {
		if (groupReqData.groupName.isEmpty()) {
			return GroupResData(success = false, msg = "创建失败，群名称不能为空")
		}
		if (groupReqData.creatorId.isEmpty()) {
			return GroupResData(success = false, msg = "创建失败，创建者ID不能为空")
		}
		if (groupReqData.ownerId.isEmpty()) {
			return GroupResData(success = false, msg = "创建失败，群主ID不能为空")
		}
		return createGroup(groupReqData)
	}

	//创建群聊
	private fun createGroup(groupReqData: GroupReqData): GroupResData {
		if (!UserController.userExist(groupReqData.ownerId)) return GroupResData(success = false, msg = "用户不存在")
		val id = generateId()
		DataBaseManager.db.groups.add(com.database.Group {
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
		return GroupResData(success = true, groupId = id, msg = "创建群聊成功")

	}

	//获取用户id所拥有的群聊
	fun getUserOwnerGroup(userId: String): Map<String, Any> {
		if (!UserController.userExist(userId)) return mapOf("success" to false, "msg" to "没有这个用户")
		val groupList = mutableListOf<Group>()
		DataBaseManager.db.groups.filter { it.ownerId eq userId }.forEach {
			groupList.add(it)
		}
		return mapOf("success" to true, "msg" to "获取群组成功", "list" to groupList)

	}

	//获取用户id所加入的群聊
	fun getJoinedGroup(userId: String): Map<String, Any> {
		if (!UserController.userExist(userId)) return mapOf("success" to false, "msg" to "没有这个用户")
		val groupList = mutableListOf<Group>()
		val groupIds = DataBaseManager.db.groupMembers.filter { it.userId eq userId }.map { it.groupId }
		DataBaseManager.db.groups.filter { it.id inList groupIds }.forEach {
			groupList.add(it)
		}
		return mapOf("success" to true, "msg" to "获取群组成功", "list" to groupList)

	}

	//检查id对应的群聊是否存在
	private fun groupExist(groupId: String): Boolean {
		return DataBaseManager.db.groups.filter { it.id eq groupId }.isNotEmpty()
	}

	//通过id查找群聊
	fun findGroupById(groupId: String): Group? {
		if (!groupExist(groupId)) return null
		return DataBaseManager.db.groups.find { it.id eq groupId }
	}

	//查找某个群的所有群成员
	fun findAllUsersByGroupId(groupId: String): List<GroupMember> {
		if (!groupExist(groupId)) return emptyList()
		val users = mutableListOf<GroupMember>()
		DataBaseManager.db.groupMembers.filter { it.groupId eq groupId }.forEach {
			users.add(it)
		}
		return users
	}
}




















