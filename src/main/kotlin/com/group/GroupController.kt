package com.group

import com.database.DataBaseManager
import com.data.Group
import com.data.GroupListResData
import com.data.GroupReqData
import com.data.GroupResData
import com.data.User
import com.user.UserController
import com.utils.generateId

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
		val groupId = generateId()
		val b = DataBaseManager.usePreparedStatement(
			sql = "insert into groups (group_id,group_name,creator_id,owner_id,create_time) values(?,?,?,?,?)"
		) {
			setString(1, groupId)
			setString(2, groupReqData.groupName)
			setString(3, groupReqData.creatorId)
			setString(4, groupReqData.ownerId)
			setLong(5, System.currentTimeMillis())
		}
		//将成员记录到群成员表中
		val c = DataBaseManager.usePreparedStatement(
			sql = "insert into group_members(group_id,user_id) values (?,?)"
		) {
			setString(1, groupId)
			setString(2, groupReqData.ownerId)
		}
		return if (b && c) {
			GroupResData(success = true, groupId = groupId, msg = "创建群聊成功")
		} else {
			GroupResData(success = false, msg = "创建群聊失败")
		}
	}

	//获取用户id所拥有的群聊
	fun getUserOwnerGroup(userId: String): GroupListResData {
		if (!UserController.userExist(userId)) return GroupListResData(success = false, msg = "没有这个用户")
		var groupList: MutableList<Group> = mutableListOf()
		DataBaseManager.useStatement(isSelect = true) {
			val list = mutableListOf<Group>()
			val set = executeQuery("select * from groups where owner_id ='$userId'")
			while (set.next()) {
				list.add(
					Group(
						groupId = set.getString("group_id"),
						groupName = set.getString("group_name"),
						ownerId = set.getString("owner_id"),
						creatorId = set.getString("creator_id")
					)
				)
			}
			groupList = list
		}
		return GroupListResData(success = true, msg = "获取群组成功", list = groupList)

	}

	//获取用户id所加入的群聊
	fun getJoinedGroup(userId: String): GroupListResData {
		if (!UserController.userExist(userId)) return GroupListResData(success = false, msg = "没有这个用户")
		var groupList: MutableList<Group> = mutableListOf()
		DataBaseManager.useStatement(isSelect = true) {
			val list = mutableListOf<Group>()
			val set = executeQuery(
				"""
				select group_id,group_name,creator_id,owner_id
				from groups
				where group_id in (select group_id
				                  from group_members
				                  where user_id = '$userId');
			""".trimIndent()
			)
			while (set.next()) {
				list.add(
					Group(
						groupId = set.getString("group_id"),
						groupName = set.getString("group_name"),
						ownerId = set.getString("owner_id"),
						creatorId = set.getString("creator_id")
					)
				)
			}
			groupList = list
		}
		return GroupListResData(success = true, msg = "获取群组成功", list = groupList)

	}

	//检查id对应的群聊是否存在
	private fun groupExist(groupId: String): Boolean {
		var sum = 0
		DataBaseManager.useStatement(isSelect = true) {
			val set = executeQuery("select count() from groups where group_name='$groupId' or group_id='$groupId'")
			sum = set.getInt("count()")
		}
		return sum == 1
	}

	//通过id查找群聊
	fun findGroupById(groupId: String): Group? {
		if (!groupExist(groupId)) return null
		var group: Group? = null
		DataBaseManager.useStatement(isSelect = true) {
			val set = executeQuery("select * from groups where group_id='$groupId'")
			group = Group(
				groupId = set.getString("group_id"),
				groupName = set.getString("group_name"),
				ownerId = set.getString("owner_id"),
				creatorId = set.getString("creator_id")
			)
		}
		return group
	}

	//查找某个群的所有群成员
	fun findAllUsersByGroupId(groupId: String): List<User>? {
		if (!groupExist(groupId)) return null
		val userList = mutableListOf<User>()
		DataBaseManager.useStatement(isSelect = true) {
			val set = executeQuery(
				"""
				select user_id, user_name, reg_time, last_online_time, validate
				from user
				where user_id in (select user_id from group_members where group_id = '$groupId')
			""".trimIndent()
			)
			while (set.next()) {
				val user = User(
					userName = set.getString("user_name"),
					userId = set.getString("user_id"),
					registerTime = set.getLong("reg_time"),
					onlineTime = set.getLong("last_online_time"),
					validate = set.getInt("validate")
				)
				userList.add(user)
			}
		}
		return userList
	}
}