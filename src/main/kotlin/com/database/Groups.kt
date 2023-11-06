package com.database

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.text
val Database.groups get() = this.sequenceOf(Groups)
object Groups : Table<Group>("groups") {
	val index = long("i").primaryKey().bindTo { it.index }
	val id = text("group_id").bindTo { it.groupId }
	val name = text("group_name").bindTo { it.groupName }
	val createTime = long("create_time").bindTo { it.createTime }
	val creatorId = text("creator_id").bindTo { it.creatorId }.references(Users){it.creatorUser}
	val ownerId = text("owner_id").bindTo { it.ownerId }.references(Users){it.ownerUser}
}

interface Group : Entity<Group> {
	companion object : Entity.Factory<Group>()
	var index:Long
	var groupId: String
	var groupName: String
	var createTime: Long
	var creatorId: String
	var ownerId: String
	var ownerUser:User
	var creatorUser:User
}