package com.wusui.database

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

val Database.groupAdmins get() = this.sequenceOf(GroupAdmins)

object GroupAdmins : Table<GroupAdmin>("group_admins") {
	val index = int("index").primaryKey().bindTo { it.index }
	val groupId = varchar("group_id").bindTo { it.groupId }.references(Groups) { it.group }
	val userId = varchar("user_id").bindTo { it.userId }.references(Users) { it.user }
}

interface GroupAdmin : Entity<GroupAdmin> {
	companion object : Entity.Factory<GroupAdmin>()

	var index: Int
	var groupId: String
	var userId: String
	var group: Group
	var user: User
}