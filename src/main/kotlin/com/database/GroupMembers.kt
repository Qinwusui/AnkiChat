package com.database

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.text
val Database.groupMembers get() = this.sequenceOf(GroupMembers)
object GroupMembers : Table<GroupMember>("group_members") {
	val index=long("index").primaryKey().bindTo { it.index }
	val groupId = text("group_id").bindTo { it.groupId }
	val userId = text("user_id").bindTo { it.userId }
}

interface GroupMember : Entity<GroupMember> {
	companion object : Entity.Factory<GroupMember>()
	var index:Long
	var groupId: String
	var userId: String
}
