package com.wusui.database

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

val Database.groupMembers get() = this.sequenceOf(GroupMembers)

object GroupMembers : Table<GroupMember>("group_members") {
	val index = int("index").primaryKey().bindTo { it.index }
	val groupId = varchar("group_id").bindTo { it.groupId }.references(Groups) { it.group }
	val userId = varchar("user_id").bindTo { it.userId }.references(Users) { it.user }
}

interface GroupMember : Entity<GroupMember> {
	companion object : Entity.Factory<GroupMember>()

	var index: Int
	var groupId: String
	var userId: String
	var group: Group
	var user: User
}
