package com.database

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.text
val Database.groupAdmins get() = this.sequenceOf(GroupAdmins)
object GroupAdmins : Table<GroupAdmin>("group_admins") {
	val index =long("index").primaryKey().bindTo { it.index }
	val groupId = text("group_id").bindTo { it.groupId }
	val userId = text("user_id").bindTo { it.userId }
}

interface GroupAdmin : Entity<GroupAdmin> {
	companion object : Entity.Factory<GroupAdmin>()
	var index:Long
	var groupId: String
	var userId: String
}