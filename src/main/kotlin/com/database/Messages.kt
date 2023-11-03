package com.database

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Column
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.text
val Database.members get() = this.sequenceOf(Messages)
object Messages : Table<Message>("messages") {
	val index = long("index").primaryKey().bindTo { it.index }
	val messageId = text("message_id").bindTo { it.messageId }
	val sendId = text("id").references(Users) { it.user }.bindTo { it.sendId }
	val toUserId = text("to_user_id").references(Users) { it.user }.bindTo { it.toUserId }
	val toGroupId=text("to_group_id").references(Groups){it.group}.bindTo { it.toGroupId }
	val messageType = text("message_type").bindTo { it.messageType }
	val content = text("content").bindTo { it.content }
	val sendTime = long("send_time").bindTo { it.sendTime }
}

interface Message : Entity<Message> {
	companion object : Entity.Factory<Message>()

	var index: Long
	var messageId: String
	var sendId: String
	var toUserId: String?
	var toGroupId:String?
	var messageType: String
	var content: String
	var sendTime: Long
	var user: User
	var group:Group
}