package com.friends

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.text

val Database.applies get() = this.sequenceOf(Applies)

object Applies : Table<Apply>("friends_applies") {
	val index = int("index").primaryKey().bindTo { it.index }
	val sendId = text("send_id").bindTo { it.sendId }
	val receiveId = text("receive_id").bindTo { it.receiveId }
	val applyMessage = text("apply_message").bindTo { it.applyMessage }
	val applyId = text("apply_id").bindTo { it.applyId }
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
}