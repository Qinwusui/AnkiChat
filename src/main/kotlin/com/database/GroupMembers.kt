package com.database

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.text

object GroupMembers : Table<Nothing>("group_members") {
	val groupId = text("group_id")
	val userId = text("user_id")
}

interface GroupMember : Entity<GroupMember> {
	companion object : Entity.Factory<GroupMember>()

	var groupId: String
	var userId: String
}
