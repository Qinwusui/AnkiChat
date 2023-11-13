package com.database

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.text

val Database.messages get() = this.sequenceOf(Messages)

object Messages : Table<Message>("messages") {
	val index = int("index").primaryKey().bindTo { it.index }
	val messageId = text("message_id").bindTo { it.messageId }
	val fromId = text("from_id").references(Users) { it.user }.bindTo { it.fromId }
	val toId = text("to_id")
		.references(Users) { it.user }
		.references(Groups) { it.group }
		.bindTo { it.toId }
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
	var messageType: String
	var content: String
	var sendTime: Long
	var user: User
	var group: Group
}