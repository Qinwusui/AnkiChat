package com.database

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.text

object GroupAdmins : Table<Nothing>("group_admin") {
	val groupId = text("group_id")
	val userId = text("id")
}

interface GroupAdmin : Entity<GroupAdmin> {
	companion object : Entity.Factory<GroupAdmin>()

	var groupId: String
	var userId: String
}