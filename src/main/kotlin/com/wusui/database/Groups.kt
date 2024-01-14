package com.wusui.database

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.text
import org.ktorm.schema.varchar

val Database.groups get() = this.sequenceOf(Groups)

object Groups : Table<Group>("groups") {
	val index = int("index").bindTo { it.index }
	val groupId = varchar("group_id").primaryKey().bindTo { it.groupId }
	val name = text("name").bindTo { it.groupName }
	val createTime = long("create_time").bindTo { it.createTime }
	val creatorId = varchar("creator_id").bindTo { it.creatorId }.references(Users) { it.creatorUser }
	val ownerId = varchar("owner_id").bindTo { it.ownerId }.references(Users) { it.ownerUser }
}

interface Group : Entity<Group> {
	companion object : Entity.Factory<Group>()

	var index: Int
	var groupId: String
	var groupName: String
	var createTime: Long
	var creatorId: String
	var ownerId: String
	var ownerUser: User
	var creatorUser: User
}