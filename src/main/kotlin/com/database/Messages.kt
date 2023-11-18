package com.database

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.text
import org.ktorm.schema.varchar

val Database.messages get() = this.sequenceOf(Messages)

object Messages : Table<Message>("messages") {
	val index = int("index").bindTo { it.index }
	val messageId = varchar("message_id").primaryKey().bindTo { it.messageId }
	val fromId = varchar("from_id").bindTo { it.fromId }.references(Users) { it.fromUser }
	val toId = varchar("to_id")
		.bindTo { it.toId }
		.references(Users) { it.toUser }
	val toGroupId = varchar("group_id").bindTo { it.toGroupId }.references(Groups) { it.toGroup }
	val messageType = text("message_type").bindTo { it.messageType }
	val content = text("content").bindTo { it.content }
	val sendTime = long("send_time").bindTo { it.sendTime }
}

interface Message : Entity<Message> {
	companion object : Entity.Factory<Message>()

	var index: Int
	var messageId: String
	var fromId: String
	var toId: String?
	var toGroupId: String?
	var messageType: String
	var content: String
	var sendTime: Long
	var fromUser: User
	var toUser: User
	var toGroup: Group
}