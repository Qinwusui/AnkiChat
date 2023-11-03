package com.database

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.text

object Groups : Table<Nothing>("groups") {
	val index = int("i").primaryKey()
	val id = text("group_id")
	val name = text("group_name")
	val createTime = long("create_time")
	val creatorId = text("creator_id")
	val ownerId = text("owner_id")
}

interface Group : Entity<Group> {
	companion object : Entity.Factory<Group>()

	var groupId: String
	var groupName: String
	var createTime: Long
	var creatorId: String
	var ownerId: String
}