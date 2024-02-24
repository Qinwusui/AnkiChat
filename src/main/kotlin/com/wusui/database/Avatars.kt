package com.wusui.database

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.bytes
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.text
val Database.avatars get() = this.sequenceOf(Avatars)
object Avatars : Table<Avatar>("avatars") {
	val index = int("index").primaryKey().bindTo { it.index }
	val id = text("id").bindTo { it.id }
	val uploadTime = long("upload_time").bindTo { it.uploadTime }
	val avatar = bytes("avatar").bindTo { it.avatar }
}

interface Avatar : Entity<Avatar> {
	companion object : Entity.Factory<Avatar>()

	var index: Int
	var id: String
	var uploadTime: Long
	var avatar: ByteArray
}