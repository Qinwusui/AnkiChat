package com.group

import com.database.DataBaseManager
import com.user.Group
import com.user.GroupListResData
import com.user.GroupReqData
import com.user.GroupResData
import com.utils.generateId

object GroupController {
	//验证群聊
	fun validateGroup(groupReqData: GroupReqData): GroupResData {
		if (groupReqData.groupName.isEmpty()) {
			return GroupResData(success = false, msg = "创建失败，群名称不能为空")
		}
		if (groupReqData.creatorId.isEmpty()) {
			return GroupResData(success = false, msg = "创建失败，创建者ID不能为空")
		}
		if (groupReqData.ownerId.isEmpty()) {
			return GroupResData(success = false, msg = "创建失败，群主ID不能为空")
		}
		return create(groupReqData)
	}

	//创建群聊
	private fun create(groupReqData: GroupReqData): GroupResData {
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
		return if (b) {
			GroupResData(success = true, groupId = groupId, msg = "创建群聊成功")
		} else {
			GroupResData(success = false, msg = "创建群聊失败")
		}
	}

	fun getGroupList(userId: String): GroupListResData {
		var groupListResData: GroupListResData? = null
		var groupList: MutableList<Group> = mutableListOf()
		DataBaseManager.useStatement {
			val list = mutableListOf<Group>()
			val set = executeQuery("select * from groups where owner_id ='$userId'")
			while (set.next()) {
				list.add(
					Group(
						groupId = set.getString("group_id"),
						groupName = set.getString("group_name"),
						ownerId = set.getString("owner_id")
					)
				)
			}
			groupList = list
		}
		groupListResData = GroupListResData(success = true, msg = "获取群组成功", list = groupList)
		return groupListResData
	}
}