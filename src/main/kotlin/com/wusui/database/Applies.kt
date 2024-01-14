package com.wusui.database

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.text
import org.ktorm.schema.varchar

val Database.applies get() = this.sequenceOf(Applies)

object Applies : Table<Apply>("friends_applies") {
	val index = int("index").bindTo { it.index }
	val sendId = varchar("send_id").bindTo { it.sendId }.references(Users) { it.sendUser }
	val receiveId = varchar("receive_id").bindTo { it.receiveId }.references(Users) { it.receiveUser }
	val applyMessage = text("apply_message").bindTo { it.applyMessage }
	val applyId = varchar("apply_id").primaryKey().bindTo { it.applyId }
	val sendTime = long("send_time").bindTo { it.sendTime }
}

interface Apply : Entity<Apply> {
	companion object : Entity.Factory<Apply>()

	var index: Int
	var sendId: String
	var receiveId: String
	var applyMessage: String
	var sendTime: Long
	var applyId: String
	var sendUser: User
	var receiveUser: User
}