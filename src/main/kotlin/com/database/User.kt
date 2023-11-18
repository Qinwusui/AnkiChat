package com.database

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.text
import org.ktorm.schema.varchar

val Database.users get() = this.sequenceOf(Users)

object Users : Table<User>("users") {
	val index = int("index").bindTo { it.index }
	val id = varchar("user_id").primaryKey().bindTo { it.userId }
	val name = varchar("name").bindTo { it.userName }
	val pwd = varchar("pwd").bindTo { it.pwd }
	val iconUrl = text("icon_url").bindTo { it.iconUrl }
	val lastOnlineTime = long("last_online_time").bindTo { it.lastOnlineTime }
}


interface User : Entity<User> {
	companion object : Entity.Factory<User>()

	var index: Int
	var userId: String
	var userName: String
	var pwd: String
	var iconUrl: String
	var lastOnlineTime: Long
}
