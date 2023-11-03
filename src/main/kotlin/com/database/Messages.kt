package com.database

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.text

object Messages : Table<Message>("messages") {
	val index = long("i").primaryKey().bindTo { it.index }
	val messageId = text("message_id").bindTo { it.messageId }
	val sendId = text("send_id").references(Users) { it }.bindTo { it.sendId }
	val toId = text("to_id").bindTo { it.toId }
	val messageType = text("message_type").bindTo { it.messageType }
	val content = text("content").bindTo { it.content }
	val sendTime = long("send_time").bindTo { it.sendTime }
}

interface Message : Entity<Message> {
	companion object : Entity.Factory<Message>()

	var index: Long
	var messageId: String
	var sendId: String
	var toId: String
	var messageType: String
	var content: String
	var sendTime: Long
}